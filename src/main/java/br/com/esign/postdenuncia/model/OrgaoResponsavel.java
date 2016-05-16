package br.com.esign.postdenuncia.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "orgaoResponsavel")
@SuppressWarnings("serial")
public class OrgaoResponsavel implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "sigla")
    private String sigla;

    @Column(name = "nome")
    private String nome;

    @Column(name = "site")
    private String site;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_cidade")
    private Cidade cidade;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "responsabilidade",
            joinColumns = {
                @JoinColumn(name = "id_orgaoResponsavel")},
            inverseJoinColumns = {
                @JoinColumn(name = "id_tipo_denuncia")})
    private Set<TipoDenuncia> tiposDenuncia;

    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "id_orgaoResponsavel")
    private Set<EstacaoMonitoramento> estacoesMonitoramento;

    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "id_orgaoResponsavel")
    private Set<QualidadeAr> qualidadesAr;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Set<TipoDenuncia> getTiposDenuncia() {
        return tiposDenuncia;
    }

    public void setTiposDenuncia(Set<TipoDenuncia> tiposDenuncia) {
        this.tiposDenuncia = tiposDenuncia;
    }

    public Set<EstacaoMonitoramento> getEstacoesMonitoramento() {
        return estacoesMonitoramento;
    }

    public void setEstacoesMonitoramento(
            Set<EstacaoMonitoramento> estacoesMonitoramento) {
        this.estacoesMonitoramento = estacoesMonitoramento;
    }

    public Set<QualidadeAr> getQualidadesAr() {
        return qualidadesAr;
    }

    public void setQualidadesAr(Set<QualidadeAr> qualidadesAr) {
        this.qualidadesAr = qualidadesAr;
    }

}