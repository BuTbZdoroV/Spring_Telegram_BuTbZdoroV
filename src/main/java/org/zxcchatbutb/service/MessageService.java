package org.zxcchatbutb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.model.DTO.*;
import org.zxcchatbutb.model.chat.*;
import org.zxcchatbutb.model.user.Person;
import org.zxcchatbutb.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final PersonRepository personRepository;
    private final SimpMessagingTemplate rabbitTemplate;
    private static final List<String> BAD_WORDS = List.of("хуй", "пизда", "ебан", "бля", "сука");
    private final ChatMemberRepository chatMemberRepository;
    private final ChatRepository chatRepository;
    private final AttachmentRepository attachmentRepository;
    private final ContactRepository contactRepository;


    @Transactional
    public ResponseEntity<?> send(MessageDTO message, PersonPrincipal principal) {

        Person sender = personRepository.findById(principal.getId()).orElseThrow();
        AbstractChat abstractChat = chatRepository.findById(message.getChat().getId())
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        if (!chatMemberRepository.existsByPersonIdAndChatId(principal.getId(), abstractChat.getId())) {
            System.out.println("Пользователь не в чате");
            return null;
        }

        if (abstractChat instanceof PrivateChat privateChat) {
            Person receiver = personRepository.findById(privateChat.getPartner(principal).getId()).orElseThrow();

            Contact contact = contactRepository.findContactBetweenPersons(sender, receiver).orElseThrow(() -> new RuntimeException("Contact not found"));

            Contact.ContactPermissions contactPermissions = contact.getPermissionsFor(receiver);

            if (!contactPermissions.isCanSendMessages()) {
                System.out.println("Пользователь " + receiver.getName() + " запретил отправлять пользователю " + sender.getName() + " сообщения");
                rabbitTemplate.convertAndSend(new ErrorMessageDTO(ErrorMessageDTO.ErrorCode.MESSAGE_BLOCK,
                        "Пользователь " + receiver.getName() + " запретил отправлять пользователю " + sender.getName() + " сообщения",
                        LocalDateTime.now()));
            }
        }

        Message newMessage = new Message();
        newMessage.setContent(message.getContent());
        newMessage.setChat(abstractChat);
        newMessage.setSender(sender);
        newMessage.setStatus(Message.Status.SENDING);
        newMessage.setSendAt(LocalDateTime.now());
        List<Attachment> attachments = message.getAttachments().stream().map(attachmentDTO -> AttachmentDTO.toEntity(newMessage, attachmentDTO)).toList();
        newMessage.setAttachments(attachments);

        attachmentRepository.saveAll(attachments);
        messageRepository.save(newMessage);

        return ResponseEntity.ok(MessageDTO.toDTO(newMessage, MessageDTO.ConvertLevel.HIGH).orElse(null));
    }

    private boolean containsBadWords(String text) {
        String lowerText = text.toLowerCase();
        return BAD_WORDS.stream().anyMatch(lowerText::contains);
    }

/*    private void sendWarningToUser(PersonPrincipal user) {
        Message warning = new Message();
        warning.setType(Message.MessageType.SYSTEM);
        warning.setContent("Ваше сообщение содержит запрещённые слова. Пожалуйста, соблюдайте правила чата.");
        warning.setSendAt(LocalDateTime.now());

        messagingTemplate.convertAndSendToUser(
                user.getUsername(),
                "/queue/warnings",
                MessageDTO.toDTO(warning, MessageDTO.ConvertLevel.HIGH)
        );

    }*/

    @Transactional
    public MessageDTO handleUserJoin(Long chatId, PersonPrincipal principal) {
        Message notification = new Message();
        notification.setContent(principal.getUsername() + " присоединился к чату");
        notification.setType(Message.MessageType.SYSTEM);
        notification.setSendAt(LocalDateTime.now());
        notification.setChat(chatRepository.findById(chatId).orElseThrow());
        notification.setSender(personRepository.findById(principal.getId()).orElseThrow());
        notification.setAttachments(new ArrayList<>());
        notification.setStatus(Message.Status.SENDING);

        Message savedMessage = messageRepository.save(notification);


        return MessageDTO.toDTO(savedMessage, DTO.ConvertLevel.HIGH).orElse(null);
    }

    public void handleMessageStatus(StatusUpdateDTO status) {
        rabbitTemplate.convertAndSend(
                "/topic/chat.status." + status.getMessageId(),
                status
        );
    }


    @Transactional
    public List<MessageDTO> getChatHistory(Long chatId) {
        List<MessageDTO> messages = messageRepository.findByChatId(chatId).stream().map(message -> MessageDTO.toDTO(message, MessageDTO.ConvertLevel.HIGH).orElse(null)).toList();
        System.out.println(messages);
        return messages;
    }

}
