package org.zxcchatbutb.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.zxcchatbutb.model.chat.Contact;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO extends DTO {
    private Long id;
    private PersonDTO personOne;
    private PersonDTO personTwo;


    public static ContactDTO toDTO(Contact contact, ConvertLevel convertLevel) {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setId(contact.getId());

        switch (convertLevel) {
            case LOW -> {
                contactDTO.setPersonOne(PersonDTO.toDTO(contact.getPersonOne(), ConvertLevel.LOW).orElse(null));
                contactDTO.setPersonTwo(PersonDTO.toDTO(contact.getPersonTwo(), ConvertLevel.LOW).orElse(null));
            }
            case MEDIUM -> {
                contactDTO.setPersonOne(PersonDTO.toDTO(contact.getPersonOne(), ConvertLevel.MEDIUM).orElse(null));
                contactDTO.setPersonTwo(PersonDTO.toDTO(contact.getPersonTwo(), ConvertLevel.MEDIUM).orElse(null));
            } case HIGH -> {
                contactDTO.setPersonOne(PersonDTO.toDTO(contact.getPersonOne(), ConvertLevel.MEDIUM).orElse(null));
                contactDTO.setPersonTwo(PersonDTO.toDTO(contact.getPersonTwo(), ConvertLevel.MEDIUM).orElse(null));
            }
        }

        return contactDTO;
    }
}
