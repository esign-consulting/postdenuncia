package br.com.esign.postdenuncia.dao;

import java.util.List;

import br.com.esign.postdenuncia.model.Coordenadas;
import br.com.esign.postdenuncia.model.EstacaoMonitoramento;
import br.com.esign.postdenuncia.model.Estado;
import br.com.esign.postdenuncia.model.OrgaoResponsavel;
import br.com.esign.postdenuncia.util.MessagesBundle;

import com.googlecode.genericdao.search.Search;

public class EstacaoMonitoramentoDAO extends GenericDAO<EstacaoMonitoramento, Integer> {

    public EstacaoMonitoramento obterPeloNome(OrgaoResponsavel orgaoResponsavel, String nome) {
        if (orgaoResponsavel == null) {
            throw new IllegalArgumentException(MessagesBundle.ORGAO_RESPONSAVEL_ESTACAO_MONITORAMENTO_OBRIGATORIO);
        }
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.NOME_ESTACAO_MONITORAMENTO_OBRIGATORIO);
        }
        Search estacaoMonitoramentoSearch = new Search(EstacaoMonitoramento.class);
        estacaoMonitoramentoSearch.addFilterEqual("orgaoResponsavel", orgaoResponsavel);
        estacaoMonitoramentoSearch.addFilterEqual("nome", nome);
        return searchUnique(estacaoMonitoramentoSearch);
    }

    public List<EstacaoMonitoramento> listarPorEstado(Estado estado) {
        Search estacaoMonitoramentoSearch = new Search(EstacaoMonitoramento.class);
        estacaoMonitoramentoSearch.addFetch("cidade");
        estacaoMonitoramentoSearch.addFilterEqual("cidade.estado", estado);
        estacaoMonitoramentoSearch.addSortAsc("nome");
        return search(estacaoMonitoramentoSearch);
    }

    public List<EstacaoMonitoramento> listarPorSiglaEstado(String siglaEstado) {
        Search estacaoMonitoramentoSearch = new Search(EstacaoMonitoramento.class);
        estacaoMonitoramentoSearch.addFetch("cidade");
        estacaoMonitoramentoSearch.addFilterEqual("cidade.estado.sigla", siglaEstado);
        estacaoMonitoramentoSearch.addSortAsc("nome");
        return search(estacaoMonitoramentoSearch);
    }

    public List<EstacaoMonitoramento> listarPorNomeCidade(String nomeCidade) {
        Search estacaoMonitoramentoSearch = new Search(EstacaoMonitoramento.class);
        estacaoMonitoramentoSearch.addFetch("cidade");
        estacaoMonitoramentoSearch.addFilterEqual("cidade.nome", nomeCidade);
        estacaoMonitoramentoSearch.addSortAsc("nome");
        return search(estacaoMonitoramentoSearch);
    }

    public List<EstacaoMonitoramento> listarPorSiglaEstadoENomeCidade(String siglaEstado, String nomeCidade) {
        Search estacaoMonitoramentoSearch = new Search(EstacaoMonitoramento.class);
        estacaoMonitoramentoSearch.addFetch("cidade");
        estacaoMonitoramentoSearch.addFilterEqual("cidade.nome", nomeCidade);
        estacaoMonitoramentoSearch.addFilterEqual("cidade.estado.sigla", siglaEstado);
        estacaoMonitoramentoSearch.addSortAsc("nome");
        return search(estacaoMonitoramentoSearch);
    }

    public List<EstacaoMonitoramento> listarPorProximidade(Estado estado, final Coordenadas coordenadas) {
        List<EstacaoMonitoramento> estacoesMonitoramento = listarPorEstado(estado);
        estacoesMonitoramento.stream().forEach((e) -> e.setDistancia(coordenadas));
        estacoesMonitoramento.sort((e1, e2) -> e1.getDistancia().compareTo(e2.getDistancia()));
        return estacoesMonitoramento;
    }

}