package br.com.esign.postdenuncia.etl.feam;

import java.io.IOException;

import br.com.esign.postdenuncia.dao.OrgaoResponsavelDAO;
import br.com.esign.postdenuncia.etl.FonteMedicao;
import br.com.esign.postdenuncia.etl.FonteTask;
import br.com.esign.postdenuncia.model.OrgaoResponsavel;

public class FEAMTask extends FonteTask {

    @Override
    protected FonteMedicao obterFonteMedicao() throws IOException {
        return new BoletimDiarioQualidadeAr();
    }

    @Override
    protected OrgaoResponsavel obterOrgaoResponsavel() {
        OrgaoResponsavelDAO orgaoResponsavelDAO = new OrgaoResponsavelDAO();
        return orgaoResponsavelDAO.find(5);
    }

}