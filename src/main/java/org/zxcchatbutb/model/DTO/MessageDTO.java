package org.zxcchatbutb.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zxcchatbutb.model.chat.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private String content;
    private LocalDateTime sendAt;
    private Message.Status status;
    private Message.MessageType type;
    private List<AttachmentDTO> attachments;
    private PersonDTO sender;

    public static MessageDTO toDTO(Message message, ConvertLevel convertLevel) {
        MessageDTO messageDTO = new MessageDTO();

        switch (convertLevel) {
            case LOW -> {
                messageDTO.setId(message.getId());
                break;
            }
            case MEDIUM -> {
                messageDTO.setId(message.getId());
                messageDTO.setContent(message.getContent());
                messageDTO.setSendAt(LocalDateTime.now());
                messageDTO.setStatus(message.getStatus());
                messageDTO.setType(message.getType());
                messageDTO.setSender(PersonDTO.toDTO(message.getSender(), PersonDTO.ConvertLevel.MEDIUM));
            }
            case HIGH -> {
                messageDTO.setId(message.getId());
                messageDTO.setContent(message.getContent());
                messageDTO.setSendAt(LocalDateTime.now());
                messageDTO.setStatus(message.getStatus());
                messageDTO.setType(message.getType());
                messageDTO.setAttachments(message.getAttachments().stream().map(AttachmentDTO::toDTO).collect(Collectors.toList()));
                messageDTO.setSender(PersonDTO.toDTO(message.getSender(), PersonDTO.ConvertLevel.MEDIUM));
            }
        }

        return messageDTO;
    }

    public enum ConvertLevel {
        LOW, MEDIUM, HIGH
    }
}
