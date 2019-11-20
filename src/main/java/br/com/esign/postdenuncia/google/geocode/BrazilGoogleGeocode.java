package br.com.esign.postdenuncia.google.geocode;

import java.io.IOException;

import br.com.esign.google.geocode.GoogleGeocode;
import br.com.esign.google.geocode.GoogleGeocodeLanguage;
import br.com.esign.google.geocode.model.GeocodeResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BrazilGoogleGeocode extends GoogleGeocode {

    private final static String apiKey = System.getProperty("google.geocode.api.key");

    public BrazilGoogleGeocode(String address) {
        super(apiKey, address);
        setLanguage(GoogleGeocodeLanguage.PORTUGUESE_BRAZIL);
    }

    public BrazilGoogleGeocode(String lat, String lng) {
        super(apiKey, lat, lng);
        setLanguage(GoogleGeocodeLanguage.PORTUGUESE_BRAZIL);
    }

    @Override
    public GeocodeResponse getResponseObject() throws IOException {
        String json = getJsonString();
        ObjectMapper mapper = new ObjectMapper();
        GeocodeResponse response = mapper.readValue(json, BrazilGeocodeResponse.class);
        return response;
    }

}