package org.zxcchatbutb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zxcchatbutb.model.user.Person;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByName(String name);
    Optional<Person> findByProviderIdAndAuthProvider(String providerId, Person.AuthProvider authProvider);
    Optional<Person> findByEmail(String email);
}
