package org.zxcchatbutb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.model.DTO.AttachmentDTO;
import org.zxcchatbutb.service.ChatService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/createPublicChat/{chatName}")
    public ResponseEntity<?> createPublicChat(@AuthenticationPrincipal PersonPrincipal person, @PathVariable String chatName) {
        return chatService.createPublicChat(person, chatName);
    }

    @PostMapping("/createPrivateChat/{person2Id}")
    public ResponseEntity<?> createPrivateChat(@AuthenticationPrincipal PersonPrincipal person, @PathVariable Long person2Id) {
        return chatService.createPrivateChat(person, person2Id);
    }

    @GetMapping("/getChats")
    public ResponseEntity<?> getChats(@AuthenticationPrincipal PersonPrincipal principal) {
        return chatService.getChats(principal);
    }

    @PatchMapping("/{chatId}/addMemberToChat/{person2Id}/")
    public ResponseEntity<?> addMemberToChat(@AuthenticationPrincipal PersonPrincipal person, @PathVariable Long person2Id, @PathVariable Long chatId) {
        return chatService.addMemberToChat(person, person2Id, chatId);
    }

    @PostMapping("/upload")
    public ResponseEntity<List<AttachmentDTO>> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            Principal principal) {

        List<AttachmentDTO> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            // Сохранение файла и получение URL
            // String fileUrl = fileStorageService.storeFile(file);

            AttachmentDTO dto = new AttachmentDTO();
            dto.setUrl("");
            dto.setFileType(file.getContentType());

            attachments.add(dto);
        }

        return ResponseEntity.ok(attachments);
    }

}
