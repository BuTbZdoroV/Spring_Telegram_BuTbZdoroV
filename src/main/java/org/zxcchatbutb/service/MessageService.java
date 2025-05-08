package org.zxcchatbutb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.controller.ContactService;
import org.zxcchatbutb.model.DTO.*;
import org.zxcchatbutb.model.chat.*;
import org.zxcchatbutb.model.user.Person;
import org.zxcchatbutb.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final PersonRepository personRepository;
    private final SimpMessagingTemplate messageTemplate;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatRepository chatRepository;
    private final AttachmentRepository attachmentRepository;
    private final ContactRepository contactRepository;
    private final ContactService contactService;


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

            Contact contact = contactService.getOrCreateContact(principal, receiver.getId());

            Contact.ContactPermissions contactPermissions = contact.getPermissionsFor(receiver);

            if (!contactPermissions.isCanSendMessages()) {
                System.out.println("Пользователь " + receiver.getName() + " запретил отправлять пользователю " + sender.getName() + " сообщения");
                messageTemplate.convertAndSend(new ErrorMessageDTO(ErrorMessageDTO.ErrorCode.MESSAGE_BLOCK,
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

        MessageDTO messageDTO = MessageDTO.toDTO(newMessage, MessageDTO.ConvertLevel.HIGH).orElse(null);

        if (messageDTO != null) {
            messageTemplate.convertAndSend("/topic/chat." + abstractChat.getId(), messageDTO);
        }

        return ResponseEntity.ok(messageDTO);
    }

    @Transactional
    public ResponseEntity<?> handleUserJoin(Long chatId, PersonPrincipal principal) {
   /*     Message notification = new Message();
        notification.setContent(principal.getUsername() + " присоединился к чату");
        notification.setType(Message.MessageType.SYSTEM);
        notification.setSendAt(LocalDateTime.now());
        notification.setChat(chatRepository.findById(chatId).orElseThrow());
        notification.setSender(personRepository.findById(principal.getId()).orElseThrow());
        notification.setAttachments(new ArrayList<>());
        notification.setStatus(Message.Status.SENDING);

        Message savedMessage = messageRepository.save(notification);*/

        log.atInfo().log("Пользователь " + principal.getUsername() + "Присоединился к чату + " + chatId);

      //  return ResponseEntity.ok(MessageDTO.toDTO(savedMessage, DTO.ConvertLevel.HIGH).orElse(null));
        return null;
    }

    public void handleMessageStatus(StatusUpdateDTO status) {
        messageTemplate.convertAndSend(
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
