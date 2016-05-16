package br.com.esign.postdenuncia.dao;

import java.util.Date;
import java.util.List;

import br.com.esign.postdenuncia.model.EstacaoMonitoramento;
import br.com.esign.postdenuncia.model.Medicao;
import br.com.esign.postdenuncia.model.OrgaoResponsavel;

import com.googlecode.genericdao.search.Field;
import com.googlecode.genericdao.search.Search;

public class MedicaoDAO extends GenericDAO<Medicao, Integer> {

    public Date obterDatahoraUltimaMedicao(OrgaoResponsavel orgaoResponsavel) {
        Search medicaoSearch = new Search(Medicao.class);
        medicaoSearch.setResultMode(Search.RESULT_SINGLE);
        medicaoSearch.addField("datahora", Field.OP_MAX);
        medicaoSearch.addFilterEqual("estacaoMonitoramento.orgaoResponsavel", orgaoResponsavel);
        return searchUnique(medicaoSearch);
    }

    public Medicao obterUltimaMedicao(EstacaoMonitoramento estacaoMonitoramento) {
        Search medicaoSearch = new Search(Medicao.class);
        medicaoSearch.addFilterEqual("estacaoMonitoramento", estacaoMonitoramento);
        medicaoSearch.addSort("datahora", true);
        medicaoSearch.setMaxResults(1);
        return searchUnique(medicaoSearch);
    }

    public Medicao obterUltimaMedicao(EstacaoMonitoramento estacaoMonitoramento, Date datahora) {
        Search medicaoSearch = new Search(Medicao.class);
        medicaoSearch.addFilterEqual("estacaoMonitoramento", estacaoMonitoramento);
        medicaoSearch.addFilterLessOrEqual("datahora", datahora);
        medicaoSearch.addSort("datahora", true);
        medicaoSearch.setMaxResults(1);
        return searchUnique(medicaoSearch);
    }

    public List<Medicao> obterUltimasMedicoes(EstacaoMonitoramento estacaoMonitoramento, Date datahora, int nMedicoes) {
        Search medicaoSearch = new Search(Medicao.class);
        medicaoSearch.addFilterEqual("estacaoMonitoramento", estacaoMonitoramento);
        medicaoSearch.addFilterLessOrEqual("datahora", datahora);
        medicaoSearch.addFilterNotNull("indice");
        medicaoSearch.addSort("datahora", true);
        medicaoSearch.setMaxResults(nMedicoes);
        return search(medicaoSearch);
    }

}