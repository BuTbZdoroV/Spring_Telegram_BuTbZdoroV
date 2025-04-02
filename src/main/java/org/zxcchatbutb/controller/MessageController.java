package org.zxcchatbutb.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.model.DTO.ChatJoinRequest;
import org.zxcchatbutb.model.DTO.MessageDTO;
import org.zxcchatbutb.model.chat.Message;
import org.zxcchatbutb.service.MessageService;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/chat.send")
    public String send(@Valid @Payload Message message, Principal principal) {
        PersonPrincipal userPrincipal = (PersonPrincipal) ((Authentication) principal).getPrincipal();
        return messageService.send(message, userPrincipal);
    }

    @MessageMapping("/chat.join")
    public String handleUserJoin(Principal principal, @Payload ChatJoinRequest chatJoinRequest) {
        PersonPrincipal userPrincipal = (PersonPrincipal) ((Authentication) principal).getPrincipal();
        return messageService.handleUserJoin(userPrincipal, chatJoinRequest);
    }

    @GetMapping("/chat/history")
    @ResponseBody
    public List<MessageDTO> getChatHistory() {
        return messageService.getChatHistory();
    }

}
