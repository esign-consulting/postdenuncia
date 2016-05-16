package br.com.esign.postdenuncia.model;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.esign.postdenuncia.util.Global;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "denuncia")
@SuppressWarnings("serial")
public class Denuncia implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_tipo_denuncia")
    private TipoDenuncia tipo;

    @ManyToOne
    @JoinColumn(name = "id_denunciante")
    private Denunciante denunciante;

    @ManyToOne
    @JoinColumn(name = "id_cidade")
    private Cidade cidade;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "endereco")
    private String endereco;

    @Column(name = "datahora")
    private Date datahora;

    @Column(name = "inclusao")
    private Date inclusao;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "latitudeGoogle")
    private BigDecimal latitudeGoogle;

    @Column(name = "longitudeGoogle")
    private BigDecimal longitudeGoogle;

    @Column(name = "foto")
    private String foto;

    @Column(name = "infoAdicional")
    private String infoAdicional;

    @Column(name = "retorno")
    private boolean retorno;

    @Column(name = "tipoDeviceOrigem")
    private String tipoDeviceOrigem;

    @ManyToOne
    @JoinColumn(name = "id_orgaoResponsavel")
    private OrgaoResponsavel orgaoResponsavel;

    @Column(name = "protocolo")
    private String protocolo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TipoDenuncia getTipo() {
        return tipo;
    }

    public void setTipo(TipoDenuncia tipo) {
        this.tipo = tipo;
    }

    public Denunciante getDenunciante() {
        return denunciante;
    }

    public void setDenunciante(Denunciante denunciante) {
        this.denunciante = denunciante;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
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

    public Date getDatahora() {
        return datahora;
    }

    public void setDatahora(Date datahora) {
        this.datahora = datahora;
    }

    public Date getInclusao() {
        return inclusao;
    }

    public void setInclusao(Date inclusao) {
        this.inclusao = inclusao;
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

    public BigDecimal getLatitudeGoogle() {
        return latitudeGoogle;
    }

    public void setLatitudeGoogle(BigDecimal latitudeGoogle) {
        this.latitudeGoogle = latitudeGoogle;
    }

    public BigDecimal getLongitudeGoogle() {
        return longitudeGoogle;
    }

    public void setLongitudeGoogle(BigDecimal longitudeGoogle) {
        this.longitudeGoogle = longitudeGoogle;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(String infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    public boolean isRetorno() {
        return retorno;
    }

    public void setRetorno(boolean retorno) {
        this.retorno = retorno;
    }

    public String getTipoDeviceOrigem() {
        return tipoDeviceOrigem;
    }

    public void setTipoDeviceOrigem(String deviceOrigem) {
        this.tipoDeviceOrigem = deviceOrigem;
    }

    public OrgaoResponsavel getOrgaoResponsavel() {
        return orgaoResponsavel;
    }

    public void setOrgaoResponsavel(OrgaoResponsavel orgaoResponsavel) {
        this.orgaoResponsavel = orgaoResponsavel;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    @JsonIgnore
    public String getImagesFolder() {
        String imagesFolder = Global.getInstance().getImagesFolder();
        if (!imagesFolder.endsWith(File.separator)) {
            imagesFolder = imagesFolder.concat(File.separator);
        }
        imagesFolder = imagesFolder.concat(tipo.getCodigo());
        File file = new File(imagesFolder);
        if (!file.exists()) {
            file.mkdir();
        }
        return imagesFolder.concat(File.separator);
    }

    @JsonIgnore
    public String getImageFilename() {
        return getImagesFolder() + getId() + ".jpg";
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

    @JsonIgnore
    public Coordenadas getCoordenadasGoogle() {
        return new Coordenadas(latitudeGoogle, longitudeGoogle);
    }

    @JsonIgnore
    public void setCoordenadasGoogle(Coordenadas coordenadasGoogle) {
        this.latitudeGoogle = coordenadasGoogle.getLatitudeAsBigDecimal();
        this.longitudeGoogle = coordenadasGoogle.getLongitudeAsBigDecimal();
    }

}