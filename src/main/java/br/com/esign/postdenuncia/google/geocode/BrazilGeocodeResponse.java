package br.com.esign.postdenuncia.google.geocode;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.esign.google.geocode.model.GeocodeResponse;
import br.com.esign.google.geocode.model.Geometry;
import br.com.esign.google.geocode.model.Location;
import br.com.esign.postdenuncia.model.Coordenadas;

public class BrazilGeocodeResponse extends GeocodeResponse {

    @JsonIgnore
    public String getEnderecoCompleto() {
        return getEnderecoCompleto(getIndex());
    }

    public String getEnderecoCompleto(int i) {
        return getFormattedAddress();
    }

    @JsonIgnore
    public String getNomeBairro() {
        return getNomeBairro(getIndex());
    }

    public String getNomeBairro(int i) {
        String nomeBairro = getSublocalityLongName(i);
        if (nomeBairro == null || nomeBairro.isEmpty()) {
            nomeBairro = getNeighborhoodLongName(i);
        }
        return nomeBairro;
    }

    @JsonIgnore
    public String getNomeCidade() {
        return getNomeCidade(getIndex());
    }

    public String getNomeCidade(int i) {
        String nomeCidade = getLocalityLongName(i);
        if (nomeCidade == null || nomeCidade.isEmpty()) {
            nomeCidade = getAdministrativeAreaLevel2LongName(i);
        }
        return nomeCidade;
    }

    @JsonIgnore
    public String getNomeEstado() {
        return getNomeEstado(getIndex());
    }

    public String getNomeEstado(int i) {
        return getAdministrativeAreaLevel1LongName(i);
    }

    @JsonIgnore
    public String getSiglaPais() {
        return getSiglaPais(getIndex());
    }

    public String getSiglaPais(int i) {
        return getCountryShortName(i);
    }

    @JsonIgnore
    public boolean isBrazil() {
        return isBrazil(getIndex());
    }

    public boolean isBrazil(int i) {
        return "BR".equals(getSiglaPais(i));
    }

    @JsonIgnore
    public Coordenadas getCoordenadas() {
        Coordenadas coordenadas = null;
        Geometry geometry = getGeometry();
        if (geometry != null) {
            Location location = geometry.getLocation();
            if (location != null) {
                coordenadas = new Coordenadas(location.getLat(), location.getLng());
            }
        }
        return coordenadas;
    }

}