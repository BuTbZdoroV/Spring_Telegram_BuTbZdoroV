package org.zxcchatbutb.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.service.PersonDetailsService;

@Controller
@RequestMapping("/oauth2")
public class OAuthController {

    private final PersonDetailsService personDetailsService;

    public OAuthController(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    @GetMapping("/profile")
    public String profilePage(Model model, @AuthenticationPrincipal PersonPrincipal principal) {
        return personDetailsService.profilePage(model, principal);
    }

}
