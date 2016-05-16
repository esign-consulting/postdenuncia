package br.com.esign.postdenuncia.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Immutable
@Table(name = "qualidadeAr")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "qualidadeAr")
@SuppressWarnings("serial")
public class QualidadeAr implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_orgaoResponsavel")
    private OrgaoResponsavel orgaoResponsavel;

    @Column(name = "classificacao")
    private String classificacao;

    @Column(name = "indiceMinimo")
    private Integer indiceMinimo;

    @Column(name = "indiceMaximo")
    private Integer indiceMaximo;

    @Column(name = "cor")
    private String cor;

    @Column(name = "numero")
    private Integer numero;

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

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public Integer getIndiceMinimo() {
        return indiceMinimo;
    }

    public void setIndiceMinimo(Integer indiceMinimo) {
        this.indiceMinimo = indiceMinimo;
    }

    public Integer getIndiceMaximo() {
        return indiceMaximo;
    }

    public void setIndiceMaximo(Integer indiceMaximo) {
        this.indiceMaximo = indiceMaximo;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

}