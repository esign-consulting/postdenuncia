package br.com.esign.postdenuncia.etl.iap;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"WIND24", "IQA24"})
public class EstacaoList {

    @JsonProperty("ESTACAO")
    private List<Estacao> estacao;

    public List<Estacao> getEstacao() {
        return estacao;
    }

    public void setEstacao(List<Estacao> estacao) {
        this.estacao = estacao;
    }

}