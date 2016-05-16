package br.com.esign.postdenuncia.dao;

import br.com.esign.postdenuncia.model.OrgaoResponsavel;
import br.com.esign.postdenuncia.util.MessagesBundle;

import com.googlecode.genericdao.search.Search;

public class OrgaoResponsavelDAO extends GenericDAO<OrgaoResponsavel, Integer> {

    public OrgaoResponsavel obterPelaSigla(String sigla) {
        if (sigla == null || sigla.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.SIGLA_ORGAO_RESPONSAVEL_OBRIGATORIA);
        }
        Search orgaoResponsavelSearch = new Search(OrgaoResponsavel.class);
        orgaoResponsavelSearch.addFilterEqual("sigla", sigla);
        return searchUnique(orgaoResponsavelSearch);
    }

    public OrgaoResponsavel obterPeloNome(String nome) {
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.NOME_ORGAO_RESPONSAVEL_OBRIGATORIO);
        }
        Search orgaoResponsavelSearch = new Search(OrgaoResponsavel.class);
        orgaoResponsavelSearch.addFilterEqual("nome", nome);
        return searchUnique(orgaoResponsavelSearch);
    }

    public OrgaoResponsavel obterPelaResponsabilidade(String siglaOrgaoResponsavel, String siglaEstado, String codigoTipoDenuncia) {
        Search orgaoResponsavelSearch = new Search(OrgaoResponsavel.class);
        orgaoResponsavelSearch.addFilterEqual("sigla", siglaOrgaoResponsavel);
        orgaoResponsavelSearch.addFilterEqual("estado.sigla", siglaEstado);
        orgaoResponsavelSearch.addFilterEqual("tiposDenuncia.codigo", codigoTipoDenuncia);
        return searchUnique(orgaoResponsavelSearch);
    }

}