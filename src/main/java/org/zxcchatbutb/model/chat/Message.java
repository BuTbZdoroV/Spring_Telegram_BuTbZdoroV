package org.zxcchatbutb.model.chat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zxcchatbutb.model.user.Person;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private MessageType type;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private Person sender;
    @NotBlank
    @Size(min = 1, max = 255)
    private String content;
    private LocalDateTime sendAt;
    private Status status;
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<Attachment> attachments;

    @ManyToOne()
    @JoinColumn(name = "chat_id")
    private Chat chat;

    public enum Status {
        SENDING,
        DELIVERED,
        READ
    }

    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        VOICE,
        SYSTEM
    }

}

