package org.zxcchatbutb.model.DTO;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.zxcchatbutb.model.chat.ChatMember;
import org.zxcchatbutb.model.user.Person;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO extends DTO {
    private Long id;
    private String name;
    private String email;
    private Person.PersonRole role;
    @Enumerated(EnumType.STRING)
    private Person.AuthProvider authProvider;
    private String providerId;
    private String imageUrl;
    private List<ChatDTO> chats;
    private List<ChatMemberDTO> chatMembers;

    public static Optional<PersonDTO> toDTO(Person person, ConvertLevel convertLevel) {
        return toDTO(person, null, convertLevel);
    }

    public static Optional<PersonDTO> toDTO(Person person, Set<ChatMember> chatMember, ConvertLevel convertLevel) {
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
                    personDTO.setChatMembers(chatMember.stream().map(chatMember1 -> ChatMemberDTO.toDTO(chatMember1, ConvertLevel.HIGH).orElse(null)).collect(Collectors.toList()));
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
                personDTO.setChats(person.getChats().stream().map(chatMember1 -> ChatDTO.toDTO(chatMember1.getChat(), ChatDTO.ConvertLevel.HIGH).orElse(null)).collect(Collectors.toList()));
                if (chatMember != null) {
                    personDTO.setChatMembers(chatMember.stream().map(chatMember1 -> ChatMemberDTO.toDTO(chatMember1, ConvertLevel.HIGH).orElse(null)).collect(Collectors.toList()));
                }
            }
        }

        return Optional.of(personDTO);
    }

}
