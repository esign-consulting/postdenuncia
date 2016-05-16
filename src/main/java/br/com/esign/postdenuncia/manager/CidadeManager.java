package br.com.esign.postdenuncia.manager;

import org.hibernate.Session;
import org.hibernate.Transaction;

import br.com.esign.postdenuncia.dao.CidadeDAO;
import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.model.Cidade;
import br.com.esign.postdenuncia.model.Estado;
import br.com.esign.postdenuncia.util.MessagesBundle;
import org.hibernate.resource.transaction.spi.TransactionStatus;

public class CidadeManager {

    public Cidade obterCidade(String nomeEstado, String nomeCidade) throws Exception {
        EstadoManager estadoManager = new EstadoManager();
        Estado estado = estadoManager.obterEstado(nomeEstado);

        if (nomeCidade == null || nomeCidade.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.NOME_CIDADE_OBRIGATORIO);
        }

        CidadeDAO cidadeDAO = new CidadeDAO();

        boolean commitOrRollback = false;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction t = session.getTransaction();
        if (t == null || t.getStatus() != TransactionStatus.ACTIVE) {
            t = session.beginTransaction();
            commitOrRollback = true;
        }

        Cidade cidade = null;
        try {
            cidade = cidadeDAO.obterPeloNome(estado, nomeCidade);
            if (commitOrRollback) {
                t.commit();
            }
        } catch (Exception e) {
            if (commitOrRollback) {
                t.rollback();
            }
            throw e;
        }

        if (cidade == null) {
            cidade = novaCidade(estado, nomeCidade);
        }
        return cidade;
    }

    public Cidade novaCidade(Estado estado, String nomeCidade) throws Exception {
        if (estado == null) {
            throw new IllegalArgumentException(MessagesBundle.ESTADO_OBRIGATORIO);
        }
        if (nomeCidade == null || nomeCidade.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.NOME_CIDADE_OBRIGATORIO);
        }

        CidadeDAO cidadeDAO = new CidadeDAO();

        boolean commitOrRollback = false;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction t = session.getTransaction();
        if (t == null || t.getStatus() != TransactionStatus.ACTIVE) {
            t = session.beginTransaction();
            commitOrRollback = true;
        }

        Cidade cidade = null;
        try {
            cidade = new Cidade();
            cidade.setEstado(estado);
            cidade.setNome(nomeCidade);
            cidadeDAO.save(cidade);

            if (commitOrRollback) {
                t.commit();
            }
        } catch (Exception e) {
            if (commitOrRollback) {
                t.rollback();
            }
            throw e;
        }

        return cidade;
    }

}