package br.com.esign.postdenuncia.dao;

import br.com.esign.postdenuncia.model.Estado;
import br.com.esign.postdenuncia.util.MessagesBundle;

import com.googlecode.genericdao.search.Search;

public class EstadoDAO extends GenericDAO<Estado, Integer> {

    public Estado obterPelaSigla(String sigla) {
        if (sigla == null || sigla.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.SIGLA_ESTADO_OBRIGATORIA);
        }
        Search estadoSearch = new Search(Estado.class);
        estadoSearch.addFilterEqual("sigla", sigla);
        return searchUnique(estadoSearch);
    }

    public Estado obterPeloNome(String nome) {
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.NOME_ESTADO_OBRIGATORIO);
        }
        Search estadoSearch = new Search(Estado.class);
        estadoSearch.addFilterEqual("nome", nome);
        return searchUnique(estadoSearch);
    }

}