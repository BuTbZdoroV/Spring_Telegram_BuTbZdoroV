package org.zxcchatbutb.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.model.DTO.PersonDTO;
import org.zxcchatbutb.service.PersonDetailsService;

import java.io.IOException;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonDetailsService personDetailsService;
    private final ContactService contactService;

    public PersonController(PersonDetailsService personDetailsService, ContactService contactService) {
        this.personDetailsService = personDetailsService;
        this.contactService = contactService;
    }

    @GetMapping("/login-success")
    public String loginSuccess() {
        return "Login successful!";
    }
    
    @GetMapping("/login-failure")
    public String loginFailure() {
        return "Login failed!";
    }

    @PostMapping("/createContact/{personTwoId}")
    public ResponseEntity<?> createContact(@AuthenticationPrincipal PersonPrincipal personOne, @PathVariable Long personTwoId) {
        return contactService.createContact(personOne, personTwoId);
    }

    @GetMapping("/findPersonByUsername/{username}")
    public ResponseEntity<PersonDTO> findPersonByUsername(@PathVariable String username) {
        return personDetailsService.getPersonByUsername(username);
    }

    @GetMapping("/getPersonPrincipalData")
    public ResponseEntity<PersonDTO> getPersonPrincipalData(@AuthenticationPrincipal PersonPrincipal personPrincipal) {
        return personDetailsService.getPersonPrincipalData(personPrincipal);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response,
                                    OAuth2AuthenticationToken authentication) throws IOException {
        return personDetailsService.logout(request, response, authentication);
    }


}