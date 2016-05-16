package br.com.esign.postdenuncia.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.esign.postdenuncia.util.DistanceCalculationUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "estacaoMonitoramento")
@SuppressWarnings("serial")
public class EstacaoMonitoramento implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_orgaoResponsavel")
    private OrgaoResponsavel orgaoResponsavel;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_cidade")
    private Cidade cidade;

    @Column(name = "nome")
    private String nome;

    @JsonIgnore
    @Column(name = "bairro")
    private String bairro;

    @JsonIgnore
    @Column(name = "endereco")
    private String endereco;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Transient
    private Double distancia;

    @Transient
    private Medicao ultimaMedicao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OrgaoResponsavel getOrgaoResponsavel() {
        return orgaoResponsavel;
    }

    public void setOrgaoResponsavel(OrgaoResponsavel orgaoResponsavel) {
        this.orgaoResponsavel = orgaoResponsavel;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }

    public void setDistancia(Coordenadas coordenadas) {
        setDistancia(DistanceCalculationUtil.distance(
                coordenadas.getLatitude(), coordenadas.getLongitude(),
                getLatitude().doubleValue(), getLongitude().doubleValue()));
    }

    public Medicao getUltimaMedicao() {
        return ultimaMedicao;
    }

    public void setUltimaMedicao(Medicao ultimaMedicao) {
        this.ultimaMedicao = ultimaMedicao;
    }

    @JsonIgnore
    public Coordenadas getCoordenadas() {
        return new Coordenadas(latitude, longitude);
    }

    @JsonIgnore
    public void setCoordenadas(Coordenadas coordenadas) {
        this.latitude = coordenadas.getLatitudeAsBigDecimal();
        this.longitude = coordenadas.getLongitudeAsBigDecimal();
    }

}