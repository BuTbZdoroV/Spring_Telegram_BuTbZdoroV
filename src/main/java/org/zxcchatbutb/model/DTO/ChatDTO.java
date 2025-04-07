package org.zxcchatbutb.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.zxcchatbutb.model.chat.AbstractChat;
import org.zxcchatbutb.model.chat.PrivateChat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDTO extends DTO {

    private Long id;
    private String chatName;
    private String avatarUrl;
    private AbstractChat.ChatType type;
    private List<PersonDTO> members;
    private List<MessageDTO> messages;
    private PersonDTO owner;


    public static Optional<ChatDTO> toDTO(AbstractChat abstractChat, ConvertLevel convertLevel) {
        ChatDTO chatDTO = new ChatDTO();

        switch (convertLevel) {
            case LOW -> {
                chatDTO.setId(abstractChat.getId());
                chatDTO.setChatName(abstractChat.getChatName());
                chatDTO.setType(abstractChat.getChatType());
            }
            case MEDIUM -> {
                chatDTO.setId(abstractChat.getId());
                chatDTO.setChatName(abstractChat.getChatName());
                chatDTO.setType(abstractChat.getChatType());
                chatDTO.setMembers(abstractChat.getMembers().stream().map(chatMember -> PersonDTO.toDTO(chatMember.getPerson(), PersonDTO.ConvertLevel.MEDIUM).orElse(null)).collect(Collectors.toList()));
                chatDTO.setMessages(abstractChat.getMessages().stream().map(chatMessage -> MessageDTO.toDTO(chatMessage, MessageDTO.ConvertLevel.MEDIUM).orElse(null)).collect(Collectors.toList()));

                if (abstractChat.getChatType() != AbstractChat.ChatType.PRIVATE) {
                    chatDTO.setOwner(PersonDTO.toDTO(abstractChat.getOwner(), PersonDTO.ConvertLevel.MEDIUM).orElse(null));
                    chatDTO.setAvatarUrl(abstractChat.getAvatarUrl());
                }
            }
            case HIGH -> {
                chatDTO.setId(abstractChat.getId());
                chatDTO.setChatName(abstractChat.getChatName());
                chatDTO.setType(abstractChat.getChatType());
                chatDTO.setMembers(abstractChat.getMembers().stream().map(chatMember -> PersonDTO.toDTO(chatMember.getPerson(), PersonDTO.ConvertLevel.MEDIUM).orElse(null)).collect(Collectors.toList()));
                chatDTO.setMessages(abstractChat.getMessages().stream().map(chatMessage -> MessageDTO.toDTO(chatMessage, MessageDTO.ConvertLevel.HIGH).orElse(null)).collect(Collectors.toList()));

                if (abstractChat.getChatType() != AbstractChat.ChatType.PRIVATE) {
                    chatDTO.setOwner(PersonDTO.toDTO(abstractChat.getOwner(), PersonDTO.ConvertLevel.MEDIUM).orElse(null));
                    chatDTO.setAvatarUrl(abstractChat.getAvatarUrl());
                }
            }
        }

        return Optional.of(chatDTO);
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

}
