package org.zxcchatbutb.model.chat;

import jakarta.persistence.*;
import lombok.Data;
import org.zxcchatbutb.model.user.Person;

import java.util.*;

@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "chat_type", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractChat {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Person owner;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChatMember> members = new HashSet<>();

    private String chatName;
    private String avatarUrl;

    public abstract ChatType getChatType();

    public boolean addMessage(Message message) {
        message.setChat(this);
        return messages.add(message);
    }

    public boolean removeMessage(Message message) {
        return messages.remove(message);
    }

    public enum ChatType {
        PRIVATE, PUBLIC
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AbstractChat abstractChat = (AbstractChat) o;
        return Objects.equals(id, abstractChat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

