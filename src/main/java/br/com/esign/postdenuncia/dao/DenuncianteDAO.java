package br.com.esign.postdenuncia.dao;

import br.com.esign.postdenuncia.model.Denunciante;
import br.com.esign.postdenuncia.util.MessagesBundle;

import com.googlecode.genericdao.search.Search;

public class DenuncianteDAO extends GenericDAO<Denunciante, Integer> {

    public Denunciante login(String email, String senha) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.EMAIL_DENUNCIANTE_OBRIGATORIO);
        }
        if (senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.SENHA_DENUNCIANTE_OBRIGATORIA);
        }
        Search denuncianteSearch = new Search(Denunciante.class);
        denuncianteSearch.addFilterEqual("email", email);
        denuncianteSearch.addFilterEqual("senha", senha);
        return searchUnique(denuncianteSearch);
    }

    public Denunciante obterPeloEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.EMAIL_DENUNCIANTE_OBRIGATORIO);
        }
        Search denuncianteSearch = new Search(Denunciante.class);
        denuncianteSearch.addFilterEqual("email", email);
        return searchUnique(denuncianteSearch);
    }

    public Denunciante obterPelaChaveConfirmacaoEmail(String chaveConfirmacaoEmail) {
        if (chaveConfirmacaoEmail == null || chaveConfirmacaoEmail.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.CHAVE_CONFIRMACAO_EMAIL_OBRIGATORIA);
        }
        Search denuncianteSearch = new Search(Denunciante.class);
        denuncianteSearch.addFilterEqual("chaveConfirmacaoEmail", chaveConfirmacaoEmail);
        return searchUnique(denuncianteSearch);
    }

    public Denunciante obterPeloCelular(String celular) {
        if (celular == null || celular.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.CELULAR_DENUNCIANTE_OBRIGATORIO);
        }
        Search denuncianteSearch = new Search(Denunciante.class);
        denuncianteSearch.addFilterEqual("celular", celular);
        return searchUnique(denuncianteSearch);
    }

    public Denunciante obterPelaChaveConfirmacaoCelular(String chaveConfirmacaoCelular) {
        if (chaveConfirmacaoCelular == null || chaveConfirmacaoCelular.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.CHAVE_CONFIRMACAO_CELULAR_OBRIGATORIA);
        }
        Search denuncianteSearch = new Search(Denunciante.class);
        denuncianteSearch.addFilterEqual("chaveConfirmacaoCelular", chaveConfirmacaoCelular);
        return searchUnique(denuncianteSearch);
    }

}