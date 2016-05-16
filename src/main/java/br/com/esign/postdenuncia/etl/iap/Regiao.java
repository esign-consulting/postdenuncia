package br.com.esign.postdenuncia.etl.iap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Regiao {

    @JsonProperty("SIGLA")
    private String sigla;

    @JsonProperty("ESCRITORIO")
    private String escritorio;

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getEscritorio() {
        return escritorio;
    }

    public void setEscritorio(String escritorio) {
        this.escritorio = escritorio;
    }

}