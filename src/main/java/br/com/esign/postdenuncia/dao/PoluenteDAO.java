package br.com.esign.postdenuncia.dao;

import br.com.esign.postdenuncia.model.Poluente;
import br.com.esign.postdenuncia.util.MessagesBundle;

import com.googlecode.genericdao.search.Search;

public class PoluenteDAO extends GenericDAO<Poluente, Integer> {

    public Poluente obterPelaRepresentacao(String representacao) {
        if (representacao == null || representacao.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.REPRESENTACAO_POLUENTE_OBRIGATORIA);
        }
        Search poluenteSearch = new Search(Poluente.class);
        poluenteSearch.addFilterEqual("representacao", representacao);
        return searchUnique(poluenteSearch);
    }

    public Poluente obterPeloNome(String nome) {
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.NOME_POLUENTE_OBRIGATORIO);
        }
        Search poluenteSearch = new Search(Poluente.class);
        poluenteSearch.addFilterEqual("nome", nome);
        return searchUnique(poluenteSearch);
    }

}