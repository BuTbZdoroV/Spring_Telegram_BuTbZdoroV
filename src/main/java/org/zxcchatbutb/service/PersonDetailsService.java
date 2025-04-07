package org.zxcchatbutb.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.model.DTO.PersonDTO;
import org.zxcchatbutb.model.chat.ChatMember;
import org.zxcchatbutb.model.user.Person;
import org.zxcchatbutb.repository.ChatMemberRepository;
import org.zxcchatbutb.repository.PersonRepository;
import org.zxcchatbutb.security.PersonDetails;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Service
public class PersonDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;
    private final ChatMemberRepository chatMemberRepository;

    public PersonDetailsService(PersonRepository personRepository, ChatMemberRepository chatMemberRepository) {
        this.personRepository = personRepository;
        this.chatMemberRepository = chatMemberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = personRepository.findByName(username);
        return new PersonDetails(person.orElseThrow(() -> new UsernameNotFoundException(username)));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PersonDTO> getPersonByUsername(String username) {
        Person person = personRepository.findByName(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return ResponseEntity.ok(PersonDTO.toDTO(person, PersonDTO.ConvertLevel.MEDIUM).orElse(null));

    }

    @Transactional(readOnly = true)
    public ResponseEntity<PersonDTO> getPersonPrincipalData(PersonPrincipal principal) {
        Person person = personRepository.findById(principal.getId()).orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Set<ChatMember> members = chatMemberRepository.findChatMemberByPerson(person);
        return ResponseEntity.ok(PersonDTO.toDTO(person, members, PersonDTO.ConvertLevel.HIGH).orElse(null));
    }

    public ResponseEntity<?> logout(HttpServletRequest request,
                       HttpServletResponse response,
                       OAuth2AuthenticationToken authentication) throws IOException {

        // Стандартный выход из Spring Security
        new SecurityContextLogoutHandler().logout(request, response, authentication);

        // Выход из Google OAuth2
        String issuer = (String) authentication.getPrincipal().getAttributes().get("iss");
        String redirectUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .build()
                .toUriString();

        String logoutUrl = issuer + "v2/logout?client_id=" + authentication.getPrincipal().getName() + "&returnTo=" + redirectUri;

        response.sendRedirect(logoutUrl);
        return ResponseEntity.ok().build();
    }

}
