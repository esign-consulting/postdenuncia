package br.com.esign.postdenuncia.etl.inema;

import java.io.IOException;

import br.com.esign.postdenuncia.dao.OrgaoResponsavelDAO;
import br.com.esign.postdenuncia.etl.FonteMedicao;
import br.com.esign.postdenuncia.etl.FonteTask;
import br.com.esign.postdenuncia.model.OrgaoResponsavel;

public class INEMATask extends FonteTask {

    @Override
    protected FonteMedicao obterFonteMedicao() throws IOException {
        return new ConsultaQualidadeArTempoReal();
    }

    @Override
    protected OrgaoResponsavel obterOrgaoResponsavel() {
        OrgaoResponsavelDAO orgaoResponsavelDAO = new OrgaoResponsavelDAO();
        return orgaoResponsavelDAO.find(8);
    }

}