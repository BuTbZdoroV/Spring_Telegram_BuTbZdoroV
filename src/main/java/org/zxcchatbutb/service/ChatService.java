package org.zxcchatbutb.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.model.DTO.ChatDTO;
import org.zxcchatbutb.model.DTO.DTO;
import org.zxcchatbutb.model.DTO.PersonDTO;
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
    public ResponseEntity<?> createPrivateChat(PersonPrincipal person1Principal, Long person2ID) {

        Person person1 = personRepository.findById(person1Principal.getId()).orElse(null);
        Person person2 = personRepository.findById(person2ID).orElse(null);

        if (person1 == null || person2 == null) {
            return ResponseEntity.badRequest().build();
        }

        PrivateChat newPrivateChat = createPrivateChat(person1, person2);

        chatRepository.save(newPrivateChat);
        return ResponseEntity.ok(ChatDTO.toDTO(newPrivateChat, DTO.ConvertLevel.HIGH));
    }

    private PrivateChat createPrivateChat(Person user1, Person user2) {
        return new PrivateChat(user1, user2);
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

        chat.getMembers().add(new ChatMember(person2, chat, ChatMember.ChatRole.USER));

        chatRepository.save(chat);
        return ResponseEntity.ok("Пользователь " + person2.getName() + " добавлен в чат " + chat.getChatName());

    }


    @Transactional(readOnly = true)
    public ResponseEntity<?> getChatMembers(Long chatId) {
        try {
            PublicChat chat = (PublicChat) chatRepository.findById(chatId)
                    .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

            List<ChatMember> members = chatMemberRepository.findChatMemberByChat(chat);
            List<PersonDTO> memberDTOs = members.stream()
                    .map(m -> PersonDTO.toDTO(m.getPerson(), DTO.ConvertLevel.MEDIUM).orElse(null))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(memberDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
