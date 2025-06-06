package org.zxcchatbutb.model.chat;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.model.user.Person;

@Entity
@DiscriminatorValue("PRIVATE")
@NoArgsConstructor
public class PrivateChat extends AbstractChat {

    public PrivateChat(Person person1, Person person2) {
        ChatMember member1 = new ChatMember(person1, this, ChatMember.ChatRole.ADMIN);
        ChatMember member2 = new ChatMember(person2, this, ChatMember.ChatRole.ADMIN);

        this.getMembers().add(member1);
        this.getMembers().add(member2);

        person1.getChats().add(member1);
        person2.getChats().add(member2);
        this.setChatName(String.format("%s & %s", person1.getName(), person2.getName()));
    }

    public Person getPartner(PersonPrincipal principal) {
        return this.getMembers().stream()
                .map(ChatMember::getPerson)
                .filter(person -> !person.getId().equals(principal.getId()))
                .findFirst()
                .orElse(null);
    }


    @Override
    public ChatType getChatType() {
        return ChatType.PRIVATE;
    }
}
