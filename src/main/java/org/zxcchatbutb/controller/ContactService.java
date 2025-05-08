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
    public ResponseEntity<ContactDTO> getOrCreateContactDTO(PersonPrincipal principal, Long personTwoId) {
        Person personOne = personRepository.findById(principal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + principal.getId()));
        Person personTwo = personRepository.findById(personTwoId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + personTwoId));

        Optional<Contact> existingContact = contactRepository.findContactBetweenPersons(personOne, personTwo);

        return existingContact.map(contact -> ResponseEntity.ok(ContactDTO.toDTO(contact, DTO.ConvertLevel.HIGH))).orElseGet(() -> createContactDTO(principal, personTwoId));

    }

    @Transactional
    public Contact getOrCreateContact(PersonPrincipal principal, Long personTwoId) {
        Person personOne = personRepository.findById(principal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + principal.getId()));
        Person personTwo = personRepository.findById(personTwoId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + personTwoId));

        Optional<Contact> existingContact = contactRepository.findContactBetweenPersons(personOne, personTwo);

        return existingContact.orElseGet(() -> createContact(principal, personTwoId));
    }


    @Transactional
    public ResponseEntity<ContactDTO> createContactDTO(PersonPrincipal principal, Long personTwoId) {
        Person personOne = personRepository.findById(principal.getId()).orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Person personTwo = personRepository.findById(personTwoId).orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Contact contact = new Contact(personOne, personTwo);

        PrivateChat newPrivateChat = new PrivateChat(personOne, personTwo);

        contactRepository.save(contact);
        chatRepository.save(newPrivateChat);

        return ResponseEntity.ok(ContactDTO.toDTO(contact, DTO.ConvertLevel.HIGH));
    }

    @Transactional
    public Contact createContact(PersonPrincipal principal, Long personTwoId) {
        Person personOne = personRepository.findById(principal.getId()).orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Person personTwo = personRepository.findById(personTwoId).orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Contact contact = new Contact(personOne, personTwo);

        PrivateChat newPrivateChat = new PrivateChat(personOne, personTwo);

        contactRepository.save(contact);
        chatRepository.save(newPrivateChat);

        return contact;
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
