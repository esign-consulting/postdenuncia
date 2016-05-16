package br.com.esign.postdenuncia.manager;

import java.text.MessageFormat;

import org.hibernate.Session;
import org.hibernate.Transaction;

import br.com.esign.postdenuncia.dao.EstadoDAO;
import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.model.Estado;
import br.com.esign.postdenuncia.util.MessagesBundle;
import org.hibernate.resource.transaction.spi.TransactionStatus;

public class EstadoManager {

    public Estado obterEstado(String nomeEstado) throws Exception {
        if (nomeEstado == null || nomeEstado.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.NOME_ESTADO_OBRIGATORIO);
        }

        EstadoDAO estadoDAO = new EstadoDAO();

        boolean commitOrRollback = false;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction t = session.getTransaction();
        if (t == null || t.getStatus() != TransactionStatus.ACTIVE) {
            t = session.beginTransaction();
            commitOrRollback = true;
        }

        Estado estado = null;
        try {
            estado = estadoDAO.obterPeloNome(nomeEstado);
            if (commitOrRollback) {
                t.commit();
            }
        } catch (Exception e) {
            if (commitOrRollback) {
                t.rollback();
            }
            throw e;
        }

        if (estado == null) {
            throw new IllegalArgumentException(MessageFormat.format(MessagesBundle.NOME_ESTADO_NAO_ENCONTRADO, nomeEstado));
        }
        return estado;
    }

}