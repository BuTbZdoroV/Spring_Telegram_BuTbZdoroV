package org.zxcchatbutb.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.model.DTO.ChatJoinRequest;
import org.zxcchatbutb.model.DTO.MessageDTO;
import org.zxcchatbutb.model.chat.Chat;
import org.zxcchatbutb.model.chat.Message;
import org.zxcchatbutb.model.user.Person;
import org.zxcchatbutb.repository.ChatMemberRepository;
import org.zxcchatbutb.repository.ChatRepository;
import org.zxcchatbutb.repository.MessageRepository;
import org.zxcchatbutb.repository.PersonRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final PersonRepository personRepository;
    private static final List<String> BAD_WORDS = List.of("хуй", "пизда", "ебан", "бля", "сука");
    private final ChatMemberRepository chatMemberRepository;
    private final ChatRepository chatRepository;


    public MessageService(MessageRepository messageRepository, SimpMessagingTemplate messagingTemplate, PersonRepository personRepository, ChatMemberRepository chatMemberRepository, ChatRepository chatRepository) {
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
        this.personRepository = personRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.chatRepository = chatRepository;
    }


    @Transactional
    public String send(Message message, PersonPrincipal principal) {

        Person sender = personRepository.findById(principal.getId()).orElseThrow();
        Chat chat = message.getChat();

        if (chat == null) {
            return "BLOCKED";
        }


        if (!chatMemberRepository.existsByPersonIdAndChatId(principal.getId(), chat.getId())) {
            return "BLOCKED";
        }

        if (containsBadWords(message.getContent())) {
            sendWarningToUser(principal);
            return "BLOCKED";
        }

        Message newMessage = new Message();
        newMessage.setContent(message.getContent());
        newMessage.setChat(chat);
        newMessage.setSender(sender);
        newMessage.setStatus(Message.Status.SENDING);
        newMessage.setSendAt(LocalDateTime.now());
        newMessage.setAttachments(message.getAttachments());

        messageRepository.save(newMessage);

        messagingTemplate.convertAndSend("/topic/chat." + message.getChat().getId(), MessageDTO.toDTO(newMessage, MessageDTO.ConvertLevel.HIGH));
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

    public String handleUserJoin(PersonPrincipal principal, ChatJoinRequest chatJoinRequest) {
        Message notification = new Message();
        notification.setContent(principal.getUsername() + " присоединился к чату");
        notification.setType(Message.MessageType.SYSTEM);
        notification.setSendAt(LocalDateTime.now());
        notification.setChat(chatRepository.findById(chatJoinRequest.getChatId()).orElseThrow());

        messagingTemplate.convertAndSend("/topic/chat."+chatJoinRequest.getChatId(), notification);
        return "DELIVERED";
    }

    @Transactional
    public List<MessageDTO> getChatHistory() {
        return messageRepository.findAll().stream().map(message -> MessageDTO.toDTO(message, MessageDTO.ConvertLevel.HIGH)).collect(Collectors.toList());
    }

}
