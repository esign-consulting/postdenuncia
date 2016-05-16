package br.com.esign.postdenuncia.etl.iap;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegiaoList {

    @JsonProperty("REGIAO")
    private List<Regiao> regiao;

    public List<Regiao> getRegiao() {
        return regiao;
    }

    public void setRegiao(List<Regiao> regiao) {
        this.regiao = regiao;
    }

}