package br.com.esign.postdenuncia.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

public class JsonUtil {

    private static final ObjectMapper mapper1, mapper2;

    static {
        mapper1 = new ObjectMapper();
        Hibernate4Module module1 = new Hibernate4Module();
        mapper1.registerModule(module1);
        // force lazy loading
        mapper2 = new ObjectMapper();
        Hibernate4Module module2 = new Hibernate4Module();
        module2.enable(Hibernate4Module.Feature.FORCE_LAZY_LOADING);
        mapper2.registerModule(module2);
    }

    public static ObjectMapper getObjectMapper() {
        return getObjectMapper(false);
    }

    public static ObjectMapper getObjectMapper(boolean forceLazyLoading) {
        return (forceLazyLoading) ? mapper2 : mapper1;
    }

    public static String getJson(String httpUrl) throws IOException {
        return getJson(httpUrl, null);
    }

    public static String getJson(String httpUrl, Map<String, String> headers) throws IOException {
        URL url = new URL(httpUrl);
        URLConnection conn = url.openConnection();
        if (headers != null && !headers.isEmpty()) {
            headers.entrySet().stream().forEach((entry) -> {
                conn.addRequestProperty(entry.getKey(), entry.getValue());
            });
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        String json = IOUtils.toString(in);
        return json;
    }

}