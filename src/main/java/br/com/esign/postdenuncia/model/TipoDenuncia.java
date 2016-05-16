package br.com.esign.postdenuncia.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Immutable
@Table(name = "tipo_denuncia")
@SuppressWarnings("serial")
public class TipoDenuncia implements Serializable {

    @JsonIgnore
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "nome")
    private String nome;

    @Column(name = "ponto")
    private String ponto;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "responsabilidade",
            joinColumns = {
                @JoinColumn(name = "id_tipo_denuncia")},
            inverseJoinColumns = {
                @JoinColumn(name = "id_orgaoResponsavel")})
    private Set<OrgaoResponsavel> orgaosResponsaveis;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPonto() {
        return ponto;
    }

    public void setPonto(String ponto) {
        this.ponto = ponto;
    }

    public Set<OrgaoResponsavel> getOrgaosResponsaveis() {
        return orgaosResponsaveis;
    }

    public void setOrgaosResponsaveis(Set<OrgaoResponsavel> orgaosResponsaveis) {
        this.orgaosResponsaveis = orgaosResponsaveis;
    }

}