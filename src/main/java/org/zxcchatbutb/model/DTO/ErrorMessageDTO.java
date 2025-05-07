package org.zxcchatbutb.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessageDTO {
    private ErrorCode errorCode;
    private String errorMessage;
    private LocalDateTime sendAt;

    public enum ErrorCode {
        MESSAGE_BLOCK,
        SENDER_NOT_FOUND
    }
}
