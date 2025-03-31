package org.zxcchatbutb.config.authinfo;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.zxcchatbutb.model.user.Person;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
public class PersonPrincipal implements OAuth2User {

    private Long id;
    private String username;
    private String email;
    private Person.PersonRole role;
    private Map<String, Object> attributes;
    private String imageUrl;

    public static PersonPrincipal create(Person person, Map<String, Object> attributes) {
        PersonPrincipal personPrincipal = new PersonPrincipal();
        personPrincipal.id = person.getId();
        personPrincipal.username = person.getName();
        personPrincipal.email = person.getEmail();
        personPrincipal.role = person.getRole();
        personPrincipal.attributes = attributes;
        personPrincipal.imageUrl = person.getImageUrl();
        return personPrincipal;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    public String getName() {
        return String.valueOf(id); //TODO SET UNIQUE VALUE
    }
}
