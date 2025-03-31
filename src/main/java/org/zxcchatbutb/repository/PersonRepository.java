package org.zxcchatbutb.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zxcchatbutb.model.user.Person;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByName(String name);
    Optional<Person> findByProviderIdAndAuthProvider(String providerId, Person.AuthProvider authProvider);
    Optional<Person> findByEmail(String email);
    @EntityGraph(attributePaths = {
            "chats",
            "chats.chat",
            "chats.chat.members",
            "chats.chat.members.person",
            "chats.chat.owner",
            "chats.chat.messages"
    })
    @Query("SELECT p FROM Person p WHERE p.id = :id")
    Optional<Person> findByIdWithAllRelations(@Param("id") Long id);

}
