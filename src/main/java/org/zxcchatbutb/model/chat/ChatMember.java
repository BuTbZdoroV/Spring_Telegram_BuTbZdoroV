package org.zxcchatbutb.model.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zxcchatbutb.model.user.Person;

import java.time.LocalDateTime;
import java.util.Objects;

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
    private AbstractChat chat;

    private ChatRole role;
    private LocalDateTime joinedAt;
    private LocalDateTime lastActiveAt;

    private boolean isMute;

    public ChatMember(Person person, AbstractChat chat, ChatRole role) {
        this.person = person;
        this.chat = chat;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
        this.isMute = false;
    }

    public ChatMember(Person person, AbstractChat chat) {
        this.person = person;
        this.chat = chat;
        this.joinedAt = LocalDateTime.now();
        this.isMute = false;

        if (chat instanceof PrivateChat) {
            this.role = ChatRole.ADMIN;
        }
    }

    public enum ChatRole {
        USER, ADMIN
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChatMember that = (ChatMember) o;
        return Objects.equals(id, that.id) && Objects.equals(person, that.person) && Objects.equals(chat, that.chat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, person, chat);
    }
}

