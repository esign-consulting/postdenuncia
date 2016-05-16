package br.com.esign.postdenuncia.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Transaction;

import br.com.esign.postdenuncia.dao.DenuncianteDAO;
import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.model.Denunciante;
import br.com.esign.postdenuncia.util.MessagesBundle;

/**
 * Servlet implementation class ConfirmaEmail
 */
@SuppressWarnings("serial")
@WebServlet("/confirmaemail")
public class ConfirmaEmail extends UsuarioServlet {

    @Inject
    private DenuncianteDAO dao;

    /**
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logParameters(request);
        String text = null;
        Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        try {
            String chaveConfirmacaoEmail = request.getParameter("chave");
            if (chaveConfirmacaoEmail == null || chaveConfirmacaoEmail.isEmpty()) {
                throw new IllegalArgumentException(MessagesBundle.CHAVE_CONFIRMACAO_EMAIL_OBRIGATORIA);
            }

            Denunciante denunciante = dao.obterPelaChaveConfirmacaoEmail(chaveConfirmacaoEmail);
            if (denunciante == null) {
                throw new IllegalArgumentException(MessagesBundle.CHAVE_CONFIRMACAO_EMAIL_NAO_ENCONTRADA);
            }

            text = MessagesBundle.SUCESSO_CONFIRMACAO_EMAIL;

            if (denunciante.isEnviarSenhaPorEmail()) {
                enviarEmailSenha(request, denunciante);
                denunciante.setEnviarSenhaPorEmail(false);
                text = text.concat(" ").concat(MessagesBundle.SUCESSO_ENVIO_SENHA);
            }

            denunciante.setChaveConfirmacaoEmail(null);
            denunciante.setEmailConfirmado(true);
            dao.save(denunciante);

            t.commit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            t.rollback();
            text = e.getMessage();
        }
        textResponse(response, text);
    }

}