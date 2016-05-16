package br.com.esign.postdenuncia.util;

import java.io.IOException;

import br.com.esign.postdenuncia.google.plus.Person;

public class GoogleUtil {

    public static Person personProfile(String json) throws IOException {
        return JsonUtil.getObjectMapper().readValue(json, Person.class);
    }

}