package org.zxcchatbutb.model.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
    @Id
    @GeneratedValue
    private Long id;
    private String url;
    private String fileType;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;
}
