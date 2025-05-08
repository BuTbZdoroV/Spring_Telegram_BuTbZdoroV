package org.zxcchatbutb.model.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zxcchatbutb.model.user.Person;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "person_one_id")
    private Person personOne;
    @ManyToOne
    @JoinColumn(name = "person_two_id")
    private Person personTwo;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "canSendMessages", column = @Column(name = "person_one_can_send")),
            @AttributeOverride(name = "canCall", column = @Column(name = "person_one_can_call")),
            @AttributeOverride(name = "canAddToChats", column = @Column(name = "person_one_can_add")),
            @AttributeOverride(name = "canSeeAvatar", column = @Column(name = "person_one_can_see_avatar")),
            @AttributeOverride(name = "canSeePhoneNumber", column = @Column(name = "person_one_can_see_phone")),
            @AttributeOverride(name = "canSeeLastSeen", column = @Column(name = "person_one_can_see_last_seen"))
    })
    private ContactPermissions contactPermissionsPersonOne;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "canSendMessages", column = @Column(name = "person_two_can_send")),
            @AttributeOverride(name = "canCall", column = @Column(name = "person_two_can_call")),
            @AttributeOverride(name = "canAddToChats", column = @Column(name = "person_two_can_add")),
            @AttributeOverride(name = "canSeeAvatar", column = @Column(name = "person_two_can_see_avatar")),
            @AttributeOverride(name = "canSeePhoneNumber", column = @Column(name = "person_two_can_see_phone")),
            @AttributeOverride(name = "canSeeLastSeen", column = @Column(name = "person_two_can_see_last_seen"))
    })
    private ContactPermissions contactPermissionsPersonTwo;

    public Contact(Person personOne, Person personTwo) {

        if (personOne.equals(personTwo)) {
            throw new IllegalArgumentException("personOne and personTwo are both equal");
        }

        this.personOne = personOne;
        this.personTwo = personTwo;

        this.contactPermissionsPersonOne = new ContactPermissions(personOne.getDefaultContactPolicy());
        this.contactPermissionsPersonTwo = new ContactPermissions(personTwo.getDefaultContactPolicy());
    }

    public ContactPermissions getPermissionsFor(Person person) {
        if (person.getId().equals(personOne.getId())) {
            return contactPermissionsPersonOne;
        } else if (person.getId().equals(personTwo.getId())) {
            return contactPermissionsPersonTwo;
        }
        throw new IllegalArgumentException("Person not part of this contact");
    }

    private ContactPermissions setContactPermissionsPersonOne(ContactPermissions contactPermissionsPersonOne) {
        this.contactPermissionsPersonOne = contactPermissionsPersonOne;
        return contactPermissionsPersonOne;
    }

    private ContactPermissions setContactPermissionsPersonTwo(ContactPermissions contactPermissionsPersonTwo) {
        this.contactPermissionsPersonTwo = contactPermissionsPersonTwo;
        return contactPermissionsPersonTwo;
    }

    public ContactPermissions changePermissionFor(Person person, ContactPermissions contactPermissions) {
        if (person.equals(personOne)) {
            return setContactPermissionsPersonOne(contactPermissions);
        } else if (person.equals(personTwo)) {
            return setContactPermissionsPersonTwo(contactPermissions);
        }
        throw new IllegalArgumentException("Person not part of this contact");
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactPermissions {
        private boolean canSendMessages;
        private boolean canCall;
        private boolean canAddToChats;

        private boolean canSeeAvatar;
        private boolean canSeePhoneNumber;
        private boolean canSeeLastSeen;

        public ContactPermissions(Person.DefaultContactPolicy defaultPolicy) {
            this.canSendMessages = defaultPolicy.isAllowMessages();
            this.canCall = defaultPolicy.isAllowCalls();
            this.canAddToChats = defaultPolicy.isAllowAddingToChats();
            this.canSeeAvatar = defaultPolicy.isShowAvatar();
            this.canSeePhoneNumber = defaultPolicy.isShowPhoneNumber();
            this.canSeeLastSeen = defaultPolicy.isShowLastSeen();
        }
    }
}


