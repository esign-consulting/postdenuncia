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
 * Servlet implementation class ConfirmaCelular
 */
@SuppressWarnings("serial")
@WebServlet("/confirmacelular")
public class ConfirmaCelular extends UsuarioServlet {

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
            String chaveConfirmacaoCelular = request.getParameter("chave");
            if (chaveConfirmacaoCelular == null || chaveConfirmacaoCelular.isEmpty()) {
                throw new IllegalArgumentException(MessagesBundle.CHAVE_CONFIRMACAO_CELULAR_OBRIGATORIA);
            }

            Denunciante denunciante = dao.obterPelaChaveConfirmacaoCelular(chaveConfirmacaoCelular);
            if (denunciante == null) {
                throw new IllegalArgumentException(MessagesBundle.CHAVE_CONFIRMACAO_CELULAR_NAO_ENCONTRADA);
            }

            text = MessagesBundle.SUCESSO_CONFIRMACAO_CELULAR;

            if (denunciante.isEnviarSenhaPorSMS()) {
                enviarSMSSenha(request, denunciante);
                denunciante.setEnviarSenhaPorSMS(false);
                text = text.concat(" ").concat(MessagesBundle.SUCESSO_ENVIO_SENHA);
            }

            denunciante.setChaveConfirmacaoCelular(null);
            denunciante.setCelularConfirmado(true);
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