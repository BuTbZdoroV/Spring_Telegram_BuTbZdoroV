package org.zxcchatbutb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.model.DTO.ChatDTO;
import org.zxcchatbutb.model.DTO.ContactDTO;
import org.zxcchatbutb.model.DTO.DTO;
import org.zxcchatbutb.model.chat.Contact;
import org.zxcchatbutb.model.chat.PrivateChat;
import org.zxcchatbutb.model.user.Person;
import org.zxcchatbutb.repository.ChatRepository;
import org.zxcchatbutb.repository.ContactRepository;
import org.zxcchatbutb.repository.PersonRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final PersonRepository personRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public ResponseEntity<ContactDTO> getOrCreateContact(PersonPrincipal principal, Long personTwoId) {
        Person personOne = personRepository.findById(principal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + principal.getId()));
        Person personTwo = personRepository.findById(personTwoId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + personTwoId));

        Optional<Contact> existingContact = contactRepository.findContactBetweenPersons(personOne, personTwo);

        if (existingContact.isPresent()) {
            return ResponseEntity.ok(ContactDTO.toDTO(existingContact.get(), DTO.ConvertLevel.HIGH));
        } else {
            Contact newContact = new Contact(personOne, personTwo);
            contactRepository.save(newContact);

            return ResponseEntity.ok(ContactDTO.toDTO(newContact, DTO.ConvertLevel.HIGH));
        }

    }


    @Transactional
    public ResponseEntity<ChatDTO> createContact(PersonPrincipal principal, Long personTwoId) {
        Person personOne = personRepository.findById(principal.getId()).orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Person personTwo = personRepository.findById(personTwoId).orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Contact contact = new Contact(personOne, personTwo);

        personOne.getContacts().add(contact);
        personTwo.getContacts().add(contact);

        PrivateChat newPrivateChat = new PrivateChat(personOne, personTwo);

        personRepository.save(personOne);
        personRepository.save(personTwo);

        contactRepository.save(contact);

        chatRepository.save(newPrivateChat);

        return ResponseEntity.ok(ChatDTO.toDTO(newPrivateChat, DTO.ConvertLevel.HIGH).orElse(null));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ContactDTO> getContactInfo(PersonPrincipal principal, Long personTwoId, Contact.ContactPermissions contactPermissions) {
        Person personOne = personRepository.findById(principal.getId()).orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Person personTwo = personRepository.findById(personTwoId).orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Contact contact = contactRepository.findContactBetweenPersons(personOne, personTwo).orElseThrow(() -> new RuntimeException("Contact not found"));

        return ResponseEntity.ok(ContactDTO.toDTO(contact, DTO.ConvertLevel.HIGH));
    }

    @Transactional
    public ResponseEntity<ContactDTO> updateContactSecurityPolicy(PersonPrincipal principal, Long personTwoId, Contact.ContactPermissions contactPermissions) {
        Person personOne = personRepository.findById(principal.getId()).orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Person personTwo = personRepository.findById(personTwoId).orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Contact contact = contactRepository.findContactBetweenPersons(personOne, personTwo).orElseThrow(() -> new RuntimeException("Contact not found"));

        contact.changePermissionFor(personOne, contactPermissions);

        contactRepository.save(contact);

        return ResponseEntity.ok(ContactDTO.toDTO(contact, DTO.ConvertLevel.HIGH));
    }

}
