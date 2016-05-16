package br.com.esign.postdenuncia.dao;

import java.util.Date;

import br.com.esign.postdenuncia.model.Denuncia;
import br.com.esign.postdenuncia.model.Denunciante;
import br.com.esign.postdenuncia.model.TipoDenuncia;

import com.googlecode.genericdao.search.Search;

public class DenunciaDAO extends GenericDAO<Denuncia, Integer> {

    public Denuncia obter(TipoDenuncia tipo, Denunciante denunciante, Date datahora) {
        Search denunciaSearch = new Search(Denuncia.class);
        denunciaSearch.addFilterEqual("tipo", tipo);
        denunciaSearch.addFilterEqual("denunciante", denunciante);
        denunciaSearch.addFilterEqual("datahora", datahora);
        return searchUnique(denunciaSearch);
    }

}