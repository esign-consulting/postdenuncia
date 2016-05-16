package br.com.esign.postdenuncia.manager;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.dao.MedicaoDAO;
import br.com.esign.postdenuncia.dao.UltimaMedicaoCache;
import br.com.esign.postdenuncia.model.EstacaoMonitoramento;
import br.com.esign.postdenuncia.model.Medicao;
import br.com.esign.postdenuncia.util.MessagesBundle;
import org.hibernate.resource.transaction.spi.TransactionStatus;

public class MedicaoManager {

    public Medicao obterUltimaMedicao(EstacaoMonitoramento estacaoMonitoramento) throws Exception {
        if (estacaoMonitoramento == null) {
            throw new IllegalArgumentException(MessagesBundle.ESTACAO_MONITORAMENTO_OBRIGATORIA);
        }

        MedicaoDAO medicaoDAO = new MedicaoDAO();

        boolean commitOrRollback = false;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction t = session.getTransaction();
        if (t == null || t.getStatus() != TransactionStatus.ACTIVE) {
            t = session.beginTransaction();
            commitOrRollback = true;
        }

        Medicao ultimaMedicao = null;
        try {
            ultimaMedicao = medicaoDAO.obterUltimaMedicao(estacaoMonitoramento);
            if (commitOrRollback) {
                t.commit();
            }
        } catch (Exception e) {
            if (commitOrRollback) {
                t.rollback();
            }
            throw e;
        }
        return ultimaMedicao;
    }

    public Medicao obterUltimaMedicao(EstacaoMonitoramento estacaoMonitoramento, boolean cache) throws Exception {
        Medicao ultimaMedicao = null;
        if (cache) {
            ultimaMedicao = UltimaMedicaoCache.getCache().getUltimaMedicao(estacaoMonitoramento);
        }
        if (ultimaMedicao == null) {
            ultimaMedicao = obterUltimaMedicao(estacaoMonitoramento);
        }
        return ultimaMedicao;
    }

    public List<Medicao> obterUltimasMedicoes(EstacaoMonitoramento estacaoMonitoramento, Date datahora, int nMedicoes) throws Exception {
        if (estacaoMonitoramento == null) {
            throw new IllegalArgumentException(MessagesBundle.ESTACAO_MONITORAMENTO_OBRIGATORIA);
        }

        MedicaoDAO medicaoDAO = new MedicaoDAO();

        boolean commitOrRollback = false;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction t = session.getTransaction();
        if (t == null || t.getStatus() != TransactionStatus.ACTIVE) {
            t = session.beginTransaction();
            commitOrRollback = true;
        }

        List<Medicao> ultimasMedicoes = null;
        try {
            ultimasMedicoes = medicaoDAO.obterUltimasMedicoes(estacaoMonitoramento, datahora, nMedicoes);
            if (commitOrRollback) {
                t.commit();
            }
        } catch (Exception e) {
            if (commitOrRollback) {
                t.rollback();
            }
            throw e;
        }
        return ultimasMedicoes;
    }

}