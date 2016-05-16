package br.com.esign.postdenuncia.etl.iap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Estacao {

    @JsonProperty("NOME")
    private String nome;

    @JsonProperty("SIGLA")
    private String sigla;

    @JsonProperty("IQA")
    private String iqa;

    @JsonProperty("INDICE")
    private String indice;

    @JsonProperty("POLUENTE")
    private String poluente;

    @JsonProperty("HORA")
    private String hora;

    @JsonProperty("OPERACAO")
    private String operacao;

    @JsonProperty("REGIONAL")
    private String regional;

    private long x;

    private long y;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getIqa() {
        return iqa;
    }

    public void setIqa(String iqa) {
        this.iqa = iqa;
    }

    public String getIndice() {
        return indice;
    }

    public void setIndice(String indice) {
        this.indice = indice;
    }

    public String getPoluente() {
        return poluente;
    }

    public void setPoluente(String poluente) {
        this.poluente = poluente;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getRegional() {
        return regional;
    }

    public void setRegional(String regional) {
        this.regional = regional;
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

}