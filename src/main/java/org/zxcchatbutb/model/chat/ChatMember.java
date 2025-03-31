package org.zxcchatbutb.model.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zxcchatbutb.model.user.Person;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMember {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id")
    private Person person;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    private ChatRole role;
    private LocalDateTime joinedAt;
    private LocalDateTime lastActiveAt;

    private boolean isMute;

    public ChatMember(Person person, Chat chat, ChatRole role) {
        this.person = person;
        this.chat = chat;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
        this.isMute = false;
    }

    public enum ChatRole {
        USER, ADMIN
    }

}

