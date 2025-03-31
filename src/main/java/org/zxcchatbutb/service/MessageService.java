package org.zxcchatbutb.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.model.chat.Message;
import org.zxcchatbutb.repository.MessageRepository;
import org.zxcchatbutb.repository.PersonRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final PersonRepository personRepository;
    private static final List<String> BAD_WORDS = List.of("хуй", "пизда", "ебан", "бля", "сука");


    public MessageService(MessageRepository messageRepository, SimpMessagingTemplate messagingTemplate, PersonRepository personRepository) {
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
        this.personRepository = personRepository;
    }


    @Transactional
    public String send(Message message, PersonPrincipal principal) {
        message.setSender(personRepository.findById(Long.valueOf(principal.getName())).orElseThrow(() -> new RuntimeException("Пользователь не найден")));
        message.setSendAt(LocalDateTime.now());

        if (containsBadWords(message.getContent())) {
            sendWarningToUser(principal);
            return "BLOCKED";
        }

        messagingTemplate.convertAndSend("/topic/chat", message);
        messageRepository.save(message);
        return "DELIVERED";
    }

    private boolean containsBadWords(String text) {
        String lowerText = text.toLowerCase();
        return BAD_WORDS.stream().anyMatch(lowerText::contains);
    }

    private void sendWarningToUser(PersonPrincipal user) {
        Message warning = new Message();
        warning.setType(Message.MessageType.SYSTEM);
        warning.setContent("Ваше сообщение содержит запрещённые слова. Пожалуйста, соблюдайте правила чата.");
        warning.setSendAt(LocalDateTime.now());

        messagingTemplate.convertAndSendToUser(
                user.getUsername(),
                "/queue/warnings",
                warning
        );

    }

    public String handleUserJoin(PersonPrincipal principal) {
        Message notification = new Message();
        notification.setContent(principal.getUsername() + " присоединился к чату");
        notification.setType(Message.MessageType.SYSTEM);
        messagingTemplate.convertAndSend("/topic/chat", notification);
        messagingTemplate.convertAndSendToUser(principal.getUsername(), "/queue/join", notification);
        return "DELIVERED";
    }

    @Transactional
    public List<Message> getChatHistory() {
        return messageRepository.findAll();
    }

}
