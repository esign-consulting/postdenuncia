package br.com.esign.postdenuncia.manager;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;

import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.dao.OrgaoResponsavelDAO;
import br.com.esign.postdenuncia.model.EstacaoMonitoramento;
import br.com.esign.postdenuncia.model.LineChartData;
import br.com.esign.postdenuncia.model.Medicao;
import br.com.esign.postdenuncia.model.OrgaoResponsavel;
import br.com.esign.postdenuncia.model.QualidadeAr;
import br.com.esign.postdenuncia.util.MessagesBundle;
import org.hibernate.resource.transaction.spi.TransactionStatus;

public class OrgaoResponsavelManager {

    public OrgaoResponsavel obterOrgaoResponsavel(String siglaOrgaoResponsavel) throws Exception {
        if (siglaOrgaoResponsavel == null || siglaOrgaoResponsavel.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.SIGLA_ORGAO_RESPONSAVEL_OBRIGATORIA);
        }

        OrgaoResponsavelDAO orgaoResponsavelDAO = new OrgaoResponsavelDAO();

        boolean commitOrRollback = false;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction t = session.getTransaction();
        if (t == null || t.getStatus() != TransactionStatus.ACTIVE) {
            t = session.beginTransaction();
            commitOrRollback = true;
        }

        OrgaoResponsavel orgaoResponsavel = null;
        try {
            orgaoResponsavel = orgaoResponsavelDAO.obterPelaSigla(siglaOrgaoResponsavel);
            if (commitOrRollback) {
                t.commit();
            }
        } catch (Exception e) {
            if (commitOrRollback) {
                t.rollback();
            }
            throw e;
        }

        if (orgaoResponsavel == null) {
            throw new IllegalArgumentException(MessageFormat.format(MessagesBundle.SIGLA_ORGAO_RESPONSAVEL_NAO_ENCONTRADO, siglaOrgaoResponsavel));
        }
        return orgaoResponsavel;
    }

    public LineChartData obterLineChartData(String siglaOrgaoResponsavel, String nomeEstacaoMonitoramento) throws Exception {
        boolean commitOrRollback = false;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction t = session.getTransaction();
        if (t == null || t.getStatus() != TransactionStatus.ACTIVE) {
            t = session.beginTransaction();
            commitOrRollback = true;
        }

        EstacaoMonitoramentoManager estacaoMonitoramentoMgr = new EstacaoMonitoramentoManager();
        MedicaoManager medicaoMgr = new MedicaoManager();

        LineChartData lineChartData = null;
        try {
            OrgaoResponsavel orgaoResponsavel = obterOrgaoResponsavel(siglaOrgaoResponsavel);
            EstacaoMonitoramento estacaoMonitoramento = estacaoMonitoramentoMgr.obterPeloNome(orgaoResponsavel, nomeEstacaoMonitoramento);

            Set<QualidadeAr> qualidadesAr = orgaoResponsavel.getQualidadesAr();
            List<Medicao> ultimasMedicoes = medicaoMgr.obterUltimasMedicoes(estacaoMonitoramento, new Date(), obterNMedicoes(orgaoResponsavel));
            ultimasMedicoes.sort((m1, m2) -> m1.getDatahora().compareTo(m2.getDatahora()));

            lineChartData = new LineChartData();
            lineChartData.setQualidadesAr((qualidadesAr == null || qualidadesAr.isEmpty()) ? null : qualidadesAr);
            lineChartData.setUltimasMedicoes(ultimasMedicoes);

            if (commitOrRollback) {
                t.commit();
            }
        } catch (Exception e) {
            if (commitOrRollback) {
                t.rollback();
            }
            throw e;
        }

        return lineChartData;
    }

    private int obterNMedicoes(OrgaoResponsavel orgaoResponsavel) {
        final int[] nMedicoes = {24, 30, 30, 24, 30, 30, 30, 24};
        return nMedicoes[orgaoResponsavel.getId() - 1];
    }

}