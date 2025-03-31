package org.zxcchatbutb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.model.chat.Chat;
import org.zxcchatbutb.model.chat.ChatMember;
import org.zxcchatbutb.model.user.Person;
import org.zxcchatbutb.repository.ChatRepository;
import org.zxcchatbutb.repository.MessageRepository;
import org.zxcchatbutb.repository.PersonRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final PersonRepository personRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;


    @Transactional
    public ResponseEntity<?> createPublicChat(PersonPrincipal person, String chatName) {
        if (person == null || chatName == null || chatName.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Chat newPublicChat = new Chat();
        newPublicChat.setChatName(chatName);
        newPublicChat.setOwner(personRepository.findById(person.getId()).orElseThrow(() -> new RuntimeException("Авторизованный пользователь не найден")));
        newPublicChat.setType(Chat.ChatType.PUBLIC);

        chatRepository.save(newPublicChat);
        return ResponseEntity.ok(newPublicChat);
    }

    public ResponseEntity<?> createPrivateChat(PersonPrincipal person1Principal, Long person2ID) {

        Person person1 = personRepository.findById(person1Principal.getId()).orElse(null);
        Person person2 = personRepository.findById(person2ID).orElse(null);

        if (person1 == null || person2 == null) {
            return ResponseEntity.badRequest().build();
        }

        Chat newPrivateChat = createPrivateChat(person1, person2);

        chatRepository.save(newPrivateChat);
        return ResponseEntity.ok(newPrivateChat);
    }

    private Chat createPrivateChat(Person user1, Person user2) {
        Chat chat = new Chat();
        chat.setType(Chat.ChatType.PRIVATE);
        chat.setChatName(user2.getName());
        chat.setAvatarUrl(user2.getImageUrl());
        chat.getMembers().addAll(List.of(
                new ChatMember(user1, chat, ChatMember.ChatRole.ADMIN),
                new ChatMember(user2, chat, ChatMember.ChatRole.ADMIN)
        ));
        return chat;
    }




}
