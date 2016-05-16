package br.com.esign.postdenuncia.etl;

import java.util.Date;

import br.com.esign.postdenuncia.model.Coordenadas;

public class DadosMedicao {

    private Date datahora;
    private String estacao;
    private String qualidadeAr;
    private String indice;
    private String poluente;
    private String latitude;
    private String longitude;

    public Date getDatahora() {
        return datahora;
    }

    public void setDatahora(Date datahora) {
        this.datahora = datahora;
    }

    public String getEstacao() {
        return estacao;
    }

    public void setEstacao(String estacao) {
        this.estacao = estacao;
    }

    public String getQualidadeAr() {
        return qualidadeAr;
    }

    public void setQualidadeAr(String qualidadeAr) {
        this.qualidadeAr = qualidadeAr;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Coordenadas getCoordenadas() {
        Coordenadas coordenadas;
        try {
            coordenadas = new Coordenadas(latitude, longitude);
        } catch (IllegalArgumentException e) {
            coordenadas = null;
        }
        return coordenadas;
    }

    @Override
    public String toString() {
        final String TAB = "\t";
        StringBuilder sb = new StringBuilder();
        sb.append("estacao: ").append(estacao).append(TAB);
        sb.append("qualidadeAr: ").append(qualidadeAr).append(TAB);
        sb.append("indice: ").append(indice).append(TAB);
        sb.append("poluente: ").append(poluente).append(TAB);
        sb.append("datahora: ").append(datahora).append(TAB);
        sb.append("latitude: ").append(latitude).append(TAB);
        sb.append("longitude: ").append(longitude);
        return sb.toString();
    }

}