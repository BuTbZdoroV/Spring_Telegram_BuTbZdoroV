package org.zxcchatbutb.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.model.user.Person;
import org.zxcchatbutb.service.PersonDetailsService;

@RestController
public class PersonController {

    @GetMapping("/login-success")
    public String loginSuccess() {
        return "Login successful!";
    }
    
    @GetMapping("/login-failure")
    public String loginFailure() {
        return "Login failed!";
    }


}