package org.zxcchatbutb.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.zxcchatbutb.model.chat.ChatMember;

import java.time.LocalDateTime;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberDTO extends DTO {
    private Long id;
    private PersonDTO person;
    private ChatDTO chat;
    private ChatMember.ChatRole role;
    private LocalDateTime joinedAt;
    private LocalDateTime lastActiveAt;
    private boolean isMute;

    public static Optional<ChatMemberDTO> toDTO(ChatMember chatMember, ConvertLevel convertLevel) {
        ChatMemberDTO dto = new ChatMemberDTO();
        switch (convertLevel) {
            case LOW -> {
                dto.setId(chatMember.getId());
                dto.setChat(ChatDTO.toDTO(chatMember.getChat(), ConvertLevel.LOW).orElse(null));
                dto.setPerson(PersonDTO.toDTO(chatMember.getPerson(), ConvertLevel.LOW).orElse(null));
                dto.isMute = chatMember.isMute();
            } case MEDIUM -> {
                dto.setId(chatMember.getId());
                dto.setChat(ChatDTO.toDTO(chatMember.getChat(), ConvertLevel.LOW).orElse(null));
                dto.setPerson(PersonDTO.toDTO(chatMember.getPerson(), ConvertLevel.LOW).orElse(null));
                dto.lastActiveAt = chatMember.getLastActiveAt();
                dto.isMute = chatMember.isMute();
            } case HIGH -> {
                dto.setId(chatMember.getId());
                dto.setChat(ChatDTO.toDTO(chatMember.getChat(), ConvertLevel.LOW).orElse(null));
                dto.setPerson(PersonDTO.toDTO(chatMember.getPerson(), ConvertLevel.LOW).orElse(null));
                dto.isMute = chatMember.isMute();
                dto.lastActiveAt = chatMember.getLastActiveAt();
                dto.joinedAt = chatMember.getJoinedAt();
                dto.role = chatMember.getRole();
            }
        }
        return Optional.of(dto);
    }

}
