package br.com.esign.postdenuncia.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "medicao")
@SuppressWarnings("serial")
public class Medicao implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_estacaoMonitoramento")
    private EstacaoMonitoramento estacaoMonitoramento;

    @ManyToOne
    @JoinColumn(name = "id_qualidadeAr")
    private QualidadeAr qualidadeAr;

    @Column(name = "datahora")
    private Date datahora;

    @Column(name = "indice")
    private Double indice;

    @ManyToOne
    @JoinColumn(name = "id_poluente")
    private Poluente poluente;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EstacaoMonitoramento getEstacaoMonitoramento() {
        return estacaoMonitoramento;
    }

    public void setEstacaoMonitoramento(EstacaoMonitoramento estacaoMonitoramento) {
        this.estacaoMonitoramento = estacaoMonitoramento;
    }

    public QualidadeAr getQualidadeAr() {
        return qualidadeAr;
    }

    public void setQualidadeAr(QualidadeAr qualidadeAr) {
        this.qualidadeAr = qualidadeAr;
    }

    public Date getDatahora() {
        return datahora;
    }

    public void setDatahora(Date datahora) {
        this.datahora = datahora;
    }

    public Double getIndice() {
        return indice;
    }

    public void setIndice(Double indice) {
        this.indice = indice;
    }

    public Poluente getPoluente() {
        return poluente;
    }

    public void setPoluente(Poluente poluente) {
        this.poluente = poluente;
    }

}