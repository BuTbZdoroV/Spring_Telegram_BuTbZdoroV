package org.zxcchatbutb.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zxcchatbutb.model.chat.Chat;
import org.zxcchatbutb.model.chat.ChatMember;
import org.zxcchatbutb.model.user.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDTO {

    private Long id;
    private String chatName;
    private String avatarUrl;
    private Chat.ChatType type;
    private List<PersonDTO> members;
    private List<MessageDTO> messages;
    private PersonDTO owner;


    public static ChatDTO toDTO(Chat chat, ConvertLevel convertLevel) {
        ChatDTO chatDTO = new ChatDTO();

        switch (convertLevel) {
            case LOW -> {
                chatDTO.setId(chat.getId());
                chatDTO.setChatName(chat.getChatName());
                chatDTO.setType(chat.getType());
            }
            case MEDIUM -> {
                chatDTO.setId(chat.getId());
                chatDTO.setChatName(chat.getChatName());
                chatDTO.setAvatarUrl(chat.getAvatarUrl());
                chatDTO.setType(chat.getType());
                chatDTO.setOwner(PersonDTO.toDTO(chat.getOwner(), PersonDTO.ConvertLevel.MEDIUM));
                chatDTO.setMembers(chat.getMembers().stream().map(chatMember -> PersonDTO.toDTO(chatMember.getPerson() ,PersonDTO.ConvertLevel.MEDIUM)).collect(Collectors.toList()));
                chatDTO.setMessages(chat.getMessages().stream().map(chatMessage -> MessageDTO.toDTO(chatMessage, MessageDTO.ConvertLevel.MEDIUM)).collect(Collectors.toList()));
            }
            case HIGH -> {
                chatDTO.setId(chat.getId());
                chatDTO.setChatName(chat.getChatName());
                chatDTO.setAvatarUrl(chat.getAvatarUrl());
                chatDTO.setType(chat.getType());
                chatDTO.setOwner(PersonDTO.toDTO(chat.getOwner(), PersonDTO.ConvertLevel.MEDIUM));
                chatDTO.setMembers(chat.getMembers().stream().map(chatMember -> PersonDTO.toDTO(chatMember.getPerson(), PersonDTO.ConvertLevel.MEDIUM)).collect(Collectors.toList()));
                chatDTO.setMessages(chat.getMessages().stream().map(chatMessage -> MessageDTO.toDTO(chatMessage, MessageDTO.ConvertLevel.HIGH)).collect(Collectors.toList()));
            }
        }

        return chatDTO;
    }

    @Override
    public String toString() {
        return "ChatDTO{" +
                "id=" + id +
                ", chatName='" + chatName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", type=" + type +
                ", members=" + members +
                ", messages=" + messages +
                ", owner=" + owner +
                '}';
    }

    public enum ConvertLevel {
        LOW, MEDIUM, HIGH
    }

}
