package org.zxcchatbutb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zxcchatbutb.model.chat.Contact;
import org.zxcchatbutb.model.user.Person;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query("SELECT c FROM Contact c WHERE " +
            "(c.personOne = :person1 AND c.personTwo = :person2) OR " +
            "(c.personOne = :person2 AND c.personTwo = :person1)")
    Optional<Contact> findContactBetweenPersons(
            @Param("person1") Person person1,
            @Param("person2") Person person2
    );

    @Query("SELECT c FROM Contact c WHERE c.personOne = :person OR c.personTwo = :person")
    List<Contact> findAllContactsByPerson(@Param("person") Person person);
}

