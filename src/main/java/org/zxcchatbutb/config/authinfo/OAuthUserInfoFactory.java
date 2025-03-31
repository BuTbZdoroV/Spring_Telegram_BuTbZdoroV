package org.zxcchatbutb.config.authinfo;

import java.util.Map;

public class OAuthUserInfoFactory {

    public static OAuthUserInfo create(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleAuthUserInfo(attributes);
        }
        throw new IllegalArgumentException("Unknown registration id " + registrationId);
    }


}
