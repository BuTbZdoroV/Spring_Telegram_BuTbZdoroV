package org.zxcchatbutb.config.authinfo;

import java.util.Map;

public class GoogleAuthUserInfo implements OAuthUserInfo {

    private final Map<String, Object> attributes;

    public GoogleAuthUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getId() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getImageUrl() {
        return attributes.get("picture").toString();
    }
}
