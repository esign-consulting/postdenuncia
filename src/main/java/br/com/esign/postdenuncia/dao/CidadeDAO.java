package br.com.esign.postdenuncia.dao;

import javax.inject.Inject;

import br.com.esign.postdenuncia.model.Cidade;
import br.com.esign.postdenuncia.model.Estado;
import br.com.esign.postdenuncia.util.MessagesBundle;

import com.googlecode.genericdao.search.Search;

public class CidadeDAO extends GenericDAO<Cidade, Integer> {

    @Inject
    private EstadoDAO estadoDAO;

    public Cidade obter(String nomeEstado, String nomeCidade) {
        Estado estado = estadoDAO.obterPeloNome(nomeEstado);
        if (nomeCidade == null || nomeCidade.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.NOME_CIDADE_OBRIGATORIO);
        }
        Search cidadeSearch = new Search(Cidade.class);
        cidadeSearch.addFilterEqual("estado", estado);
        cidadeSearch.addFilterEqual("nome", nomeCidade);
        Cidade cidade = searchUnique(cidadeSearch);
        if (cidade == null) {
            cidade = new Cidade();
            cidade.setEstado(estado);
            cidade.setNome(nomeCidade);
            save(cidade);
        }
        return cidade;
    }

    public Cidade obterPeloNome(Estado estado, String nomeCidade) {
        Search cidadeSearch = new Search(Cidade.class);
        cidadeSearch.addFilterEqual("estado", estado);
        cidadeSearch.addFilterEqual("nome", nomeCidade);
        return searchUnique(cidadeSearch);
    }

}