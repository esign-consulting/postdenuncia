package br.com.esign.postdenuncia.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "denunciante")
@SuppressWarnings("serial")
public class Denunciante implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "retornoPorEmail")
    private boolean retornoPorEmail;

    @JsonIgnore
    @Column(name = "chaveConfirmacaoEmail")
    private String chaveConfirmacaoEmail;

    @Column(name = "emailConfirmado")
    private boolean emailConfirmado;

    @JsonIgnore
    @Column(name = "senha")
    private String senha;

    @JsonIgnore
    @Column(name = "enviarSenhaPorEmail")
    private boolean enviarSenhaPorEmail;

    @JsonIgnore
    @Column(name = "enviarSenhaPorSMS")
    private boolean enviarSenhaPorSMS;

    @Column(name = "celular")
    private String celular;

    @Column(name = "retornoPorSMS")
    private boolean retornoPorSMS;

    @JsonIgnore
    @Column(name = "chaveConfirmacaoCelular")
    private String chaveConfirmacaoCelular;

    @Column(name = "celularConfirmado")
    private boolean celularConfirmado;

    @Column(name = "retornoPorPush")
    private boolean retornoPorPush;

    @OneToOne
    @JoinColumn(name = "id_facebookUser")
    private FacebookUser facebookUser;

    @OneToOne
    @JoinColumn(name = "id_googlePlusPerson")
    private GooglePlusPerson googlePlusPerson;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isRetornoPorEmail() {
        return retornoPorEmail;
    }

    public void setRetornoPorEmail(boolean retornoPorEmail) {
        this.retornoPorEmail = retornoPorEmail;
    }

    public String getChaveConfirmacaoEmail() {
        return chaveConfirmacaoEmail;
    }

    public void setChaveConfirmacaoEmail(String chaveConfirmacaoEmail) {
        this.chaveConfirmacaoEmail = chaveConfirmacaoEmail;
    }

    public boolean isEmailConfirmado() {
        return emailConfirmado;
    }

    public void setEmailConfirmado(boolean emailConfirmado) {
        this.emailConfirmado = emailConfirmado;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isEnviarSenhaPorEmail() {
        return enviarSenhaPorEmail;
    }

    public void setEnviarSenhaPorEmail(boolean enviarSenhaPorEmail) {
        this.enviarSenhaPorEmail = enviarSenhaPorEmail;
    }

    public boolean isEnviarSenhaPorSMS() {
        return enviarSenhaPorSMS;
    }

    public void setEnviarSenhaPorSMS(boolean enviarSenhaPorSMS) {
        this.enviarSenhaPorSMS = enviarSenhaPorSMS;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public boolean isRetornoPorSMS() {
        return retornoPorSMS;
    }

    public void setRetornoPorSMS(boolean retornoPorSMS) {
        this.retornoPorSMS = retornoPorSMS;
    }

    public String getChaveConfirmacaoCelular() {
        return chaveConfirmacaoCelular;
    }

    public void setChaveConfirmacaoCelular(String chaveConfirmacaoCelular) {
        this.chaveConfirmacaoCelular = chaveConfirmacaoCelular;
    }

    public boolean isCelularConfirmado() {
        return celularConfirmado;
    }

    public void setCelularConfirmado(boolean celularConfirmado) {
        this.celularConfirmado = celularConfirmado;
    }

    public boolean isRetornoPorPush() {
        return retornoPorPush;
    }

    public void setRetornoPorPush(boolean retornoPorPush) {
        this.retornoPorPush = retornoPorPush;
    }

    public FacebookUser getFacebookUser() {
        return facebookUser;
    }

    public void setFacebookUser(FacebookUser facebookUser) {
        this.facebookUser = facebookUser;
    }

    public GooglePlusPerson getGooglePlusPerson() {
        return googlePlusPerson;
    }

    public void setGooglePlusPerson(GooglePlusPerson googlePlusPerson) {
        this.googlePlusPerson = googlePlusPerson;
    }

    public String getGender() {
        String gender = null;
        if (facebookUser != null) {
            gender = facebookUser.getGender();
        }
        if ((gender == null || gender.isEmpty()) && googlePlusPerson != null) {
            gender = googlePlusPerson.getGender();
        }
        return gender;
    }

    public String getArticle() {
        String article = null;
        String gender = getGender();
        if (gender == null || gender.isEmpty()) {
            article = "o(a)";
        } else if (gender.equalsIgnoreCase("male")) {
            article = "o";
        } else {
            article = "a";
        }
        return article;
    }

}