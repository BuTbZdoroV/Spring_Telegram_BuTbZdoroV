package org.zxcchatbutb.model.DTO;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.model.chat.ChatMember;
import org.zxcchatbutb.model.user.Person;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {
    private Long id;
    private String name;
    private String email;
    private Person.PersonRole role;
    @Enumerated(EnumType.STRING)
    private Person.AuthProvider authProvider;
    private String providerId;
    private String imageUrl;
    private List<ChatDTO> chats;
    private ChatMember.ChatRole chatRole; // Роль в конкретном чате
    private LocalDateTime joinedAt; // Когда присоединился к чату
    private LocalDateTime lastActiveAt; // Когда последний раз был активен в чату

    public static PersonDTO toDTO(Person person, ConvertLevel convertLevel) {
        return toDTO(person, null, convertLevel);
    }

    public static PersonDTO toDTO(Person person, ChatMember chatMember, ConvertLevel convertLevel) {
        PersonDTO personDTO = new PersonDTO();

        switch (convertLevel) {
            case LOW -> {
                personDTO.setId(person.getId());
                personDTO.setName(person.getName());
                break;
            }
            case MEDIUM -> {
                personDTO.setId(person.getId());
                personDTO.setName(person.getName());
                personDTO.setRole(person.getRole());
                personDTO.setImageUrl(person.getImageUrl());
                if (chatMember != null) {
                    personDTO.setChatRole(chatMember.getRole());
                    personDTO.setJoinedAt(chatMember.getJoinedAt());
                    personDTO.setLastActiveAt(chatMember.getLastActiveAt());
                }
                break;
            }
            case HIGH -> {
                personDTO.setId(person.getId());
                personDTO.setName(person.getName());
                personDTO.setEmail(person.getEmail());
                personDTO.setRole(person.getRole());
                personDTO.setProviderId(person.getProviderId());
                personDTO.setImageUrl(person.getImageUrl());
                personDTO.setAuthProvider(person.getAuthProvider());
                personDTO.setChats(person.getChats().stream().map(chatMember1 -> ChatDTO.toDTO(chatMember1.getChat(), ChatDTO.ConvertLevel.HIGH)).collect(Collectors.toList()));
                if (chatMember != null) {
                    personDTO.setChatRole(chatMember.getRole());
                    personDTO.setJoinedAt(chatMember.getJoinedAt());
                    personDTO.setLastActiveAt(chatMember.getLastActiveAt());
                }
            }
        }

        return personDTO;
    }

    public enum ConvertLevel {
        LOW, MEDIUM, HIGH
    }
}
