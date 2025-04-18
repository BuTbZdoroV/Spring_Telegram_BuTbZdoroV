package org.zxcchatbutb.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zxcchatbutb.model.chat.ChatMember;

import java.util.Objects;
import java.util.Set;


@Entity
@Data
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    @Size(min = 2, max = 25)
    private String name;

    @Column(unique = true, nullable = false)
    @Size(min = 5, max = 100)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PersonRole role;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @OneToMany(mappedBy = "person")
    private Set<ChatMember> chats;

    private String providerId;
    private String imageUrl;

    public Person() {
        role = PersonRole.USER;
    }

    public enum AuthProvider {
        local,
        google,
        github,
        facebook
    }

    public enum PersonRole {
        USER, PREMIUM, ADMIN;

        @Override
        public String toString() {
            return "ROLE_" + this.name();
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
