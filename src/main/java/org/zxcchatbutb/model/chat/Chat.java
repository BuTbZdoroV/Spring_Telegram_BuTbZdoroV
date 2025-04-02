package org.zxcchatbutb.model.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zxcchatbutb.model.user.Person;

import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Person owner;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChatMember> members = new HashSet<>();

    private String chatName;
    private String avatarUrl;
    private ChatType type;



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
        Chat chat = (Chat) o;
        return Objects.equals(id, chat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

