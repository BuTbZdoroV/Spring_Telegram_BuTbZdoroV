package org.zxcchatbutb.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.model.user.Person;
import org.zxcchatbutb.repository.PersonRepository;
import org.zxcchatbutb.security.PersonDetails;

import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = personRepository.findByName(username);
        return new PersonDetails(person.orElseThrow(() -> new UsernameNotFoundException(username)));
    }

    public String profilePage(Model model, PersonPrincipal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", principal);
        return "profile";
    }


    @Transactional
    public ResponseEntity<Person> getPerson(PersonPrincipal principal) {
        return ResponseEntity.ok(personRepository.findById(principal.getId()).orElseThrow(() -> new RuntimeException("User not found")));
    }

}
