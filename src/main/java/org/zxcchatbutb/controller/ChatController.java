package org.zxcchatbutb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.service.ChatService;

@RestController("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/createPublicChat/{chatName}")
    public ResponseEntity<?> createPublicChat(@AuthenticationPrincipal PersonPrincipal principal, @PathVariable String chatName) {
        return chatService.createPublicChat(principal, chatName);
    }

    @PostMapping("/createPrivateChat/{person2Id}")
    public ResponseEntity<?> createPrivateChat(@AuthenticationPrincipal PersonPrincipal person, @PathVariable Long person2Id) {
        return chatService.createPrivateChat(person, person2Id);
    }

}
