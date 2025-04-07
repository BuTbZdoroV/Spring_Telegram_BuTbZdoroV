package org.zxcchatbutb.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zxcchatbutb.model.chat.Attachment;
import org.zxcchatbutb.model.chat.Message;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {
    private Long id;
    private String url;
    private String fileType;
    private Long messageId;

    public static AttachmentDTO toDTO(Attachment attachment) {
        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(attachment.getId());
        dto.setUrl(attachment.getUrl());
        dto.setFileType(attachment.getFileType());
        dto.setMessageId(attachment.getMessage().getId());
        return dto;
    }

    public static Attachment toEntity(Message message, AttachmentDTO dto) {
        Attachment attachment = new Attachment();
        attachment.setId(dto.getId());
        attachment.setUrl(dto.getUrl());
        attachment.setFileType(dto.getFileType());
        attachment.setMessage(message);
        return attachment;
    }

}
