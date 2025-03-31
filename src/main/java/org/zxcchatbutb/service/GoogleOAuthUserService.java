package org.zxcchatbutb.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.zxcchatbutb.config.authinfo.OAuthUserInfo;
import org.zxcchatbutb.config.authinfo.OAuthUserInfoFactory;
import org.zxcchatbutb.config.authinfo.PersonPrincipal;
import org.zxcchatbutb.model.user.Person;
import org.zxcchatbutb.repository.PersonRepository;

import java.util.Optional;

@Service
public class GoogleOAuthUserService extends DefaultOAuth2UserService {

    private final PersonRepository personRepository;

    public GoogleOAuthUserService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        return this.processUser(userRequest, oAuth2User);

    }


    private OAuth2User processUser(OAuth2UserRequest userRequest, OAuth2User auth2User) throws OAuth2AuthenticationException {
        OAuthUserInfo userInfo = OAuthUserInfoFactory.create(userRequest.getClientRegistration().getRegistrationId(), auth2User.getAttributes());

        if (!StringUtils.hasText(userInfo.getEmail())) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST));
        }

        Optional<Person> user = personRepository.findByEmail(userInfo.getEmail());
        Person person;
        if (user.isPresent()) {
            person = user.get();
            if (!person.getAuthProvider().equals(Person.AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST));
            }

            person = updateExistingUser(user.get(), userInfo);
        } else {
            person = registerNewUser(userRequest, userInfo);
        }

        return PersonPrincipal.create(person, auth2User.getAttributes());
    }

    private Person registerNewUser(OAuth2UserRequest userRequest, OAuthUserInfo info) {
        Person person = new Person();
        person.setAuthProvider(Person.AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()));
        person.setProviderId(info.getId());
        person.setEmail(info.getEmail());
        person.setName(info.getName());
        person.setRole(Person.PersonRole.USER);
        person.setImageUrl(info.getImageUrl());

        return personRepository.save(person);
    }

    private Person updateExistingUser(Person existingUser, OAuthUserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setEmail(oAuth2UserInfo.getEmail());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        return personRepository.save(existingUser);
    }
}
