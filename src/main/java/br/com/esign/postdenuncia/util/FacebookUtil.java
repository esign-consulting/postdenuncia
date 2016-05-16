package br.com.esign.postdenuncia.util;

import java.io.IOException;

import br.com.esign.postdenuncia.facebook.User;

public class FacebookUtil {

    public static User userProfile(String json) throws IOException {
        return JsonUtil.getObjectMapper().readValue(json, User.class);
    }

}