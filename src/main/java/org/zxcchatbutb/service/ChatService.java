package org.zxcchatbutb.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.model.DTO.ChatDTO;
import org.zxcchatbutb.model.DTO.ChatMemberDTO;
import org.zxcchatbutb.model.DTO.DTO;
import org.zxcchatbutb.model.chat.AbstractChat;
import org.zxcchatbutb.model.chat.ChatMember;
import org.zxcchatbutb.model.chat.PrivateChat;
import org.zxcchatbutb.model.chat.PublicChat;
import org.zxcchatbutb.model.user.Person;
import org.zxcchatbutb.repository.ChatMemberRepository;
import org.zxcchatbutb.repository.ChatRepository;
import org.zxcchatbutb.repository.MessageRepository;
import org.zxcchatbutb.repository.PersonRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final PersonRepository personRepository;
    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final MessageRepository messageRepository;


    @Transactional
    public ResponseEntity<?> createPublicChat(PersonPrincipal person, String chatName) {
        if (person == null || chatName == null || chatName.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Person owner = personRepository.findById(person.getId()).orElseThrow(() -> new RuntimeException("Авторизованный пользователь не найден"));

        PublicChat newPublicChat = new PublicChat();
        newPublicChat.setChatName(chatName);
        newPublicChat.setOwner(owner);

        chatRepository.save(newPublicChat);

        ChatMember chatMember = new ChatMember(owner, newPublicChat, ChatMember.ChatRole.ADMIN);
        chatMemberRepository.save(chatMember);

        return ResponseEntity.ok(ChatDTO.toDTO(newPublicChat, ChatDTO.ConvertLevel.HIGH));
    }

    @Transactional
    public ResponseEntity<?> getOrCreatePrivateChat(PersonPrincipal person1Principal, Long person2Id) {
        try {
            Person person1 = personRepository.findById(person1Principal.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User 1 not found"));
            Person person2 = personRepository.findById(person2Id)
                    .orElseThrow(() -> new EntityNotFoundException("User 2 not found"));

            PrivateChat privateChat = getOrCreatePrivateChat(person1, person2);
            return ResponseEntity.ok(ChatDTO.toDTO(privateChat, DTO.ConvertLevel.HIGH));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private PrivateChat getOrCreatePrivateChat(Person user1, Person user2) {
        return chatRepository.findChatBetweenUsers(user1.getId(), user2.getId())
                .orElseGet(() -> {
                    PrivateChat newChat = new PrivateChat();

                    ChatMember member1 = new ChatMember(user1, newChat, ChatMember.ChatRole.ADMIN);
                    ChatMember member2 = new ChatMember(user2, newChat, ChatMember.ChatRole.ADMIN);

                    newChat.setMembers(Set.of(member1, member2));

                    return chatRepository.save(newChat);
                });
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getChats(PersonPrincipal person) {
        List<AbstractChat> abstractChatList = chatMemberRepository.findChatsByPersonId(person.getId());
        List<ChatDTO> chatDTOS = abstractChatList.stream().map(chatMember -> ChatDTO.toDTO(chatMember, ChatDTO.ConvertLevel.HIGH).orElse(null)).collect(Collectors.toList());
        System.out.println(chatDTOS.toString());
        return ResponseEntity.ok(chatDTOS);
    }

    @Transactional
    public ResponseEntity<?> addMemberToChat(PersonPrincipal person, Long person2Id, Long chatId) {
        Person person1 = personRepository.findById(person.getId()).orElseThrow();
        Person person2 = personRepository.findById(person2Id).orElseThrow();
        PublicChat chat = (PublicChat) chatRepository.findById(chatId).orElseThrow();

        if (person1.equals(person2)) {
            return ResponseEntity.badRequest().build();
        }

        if (person1.getChats().stream().noneMatch(chat1 -> chat.getId().equals(chat1.getId()))) {
            return ResponseEntity.badRequest().build();
        }

        ChatMember chatMember = new ChatMember(person2, chat, ChatMember.ChatRole.USER);

        chat.getMembers().add(chatMember);
        chatRepository.save(chat);
        person2.getChats().add(chatMember);
        personRepository.save(person2);

        System.out.println(person2.getChats().stream().map(chatMember1 -> chatMember1.getChat().getChatName()).collect(Collectors.toList()));
        System.out.println(chat.getMembers().stream().map(chatMember1 -> chatMember1.getPerson().getName()).collect(Collectors.toList()));
        return ResponseEntity.ok("Пользователь " + person2.getName() + " добавлен в чат " + chat.getChatName());

    }


    @Transactional(readOnly = true)
    public ResponseEntity<List<ChatMemberDTO>> getChatMembers(PersonPrincipal principal, Long chatId) {

        if (principal == null || chatId == null) {
            return ResponseEntity.badRequest().build();
        }

        AbstractChat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));
        Person currentPerson = personRepository.findById(principal.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!chat.personInMemberChat(currentPerson)) return ResponseEntity.badRequest().build();

        List<ChatMemberDTO> members = chat.getMembers().stream()
                .filter(chatMember -> !chatMember.getPerson().equals(currentPerson))
                .map(chatMember -> ChatMemberDTO.toDTO(chatMember, DTO.ConvertLevel.HIGH).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        return ResponseEntity.ok(members);
    }

}
