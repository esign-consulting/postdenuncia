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
 * Servlet implementation class Login
 */
@SuppressWarnings("serial")
@WebServlet("/login")
public class Login extends GenericServlet {

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
        Response<Denunciante> resp = new Response<>();
        Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        try {
            String usuario = request.getParameter("usuario");
            if (usuario == null || usuario.isEmpty()) {
                throw new IllegalArgumentException(MessagesBundle.USUARIO_OBRIGATORIO);
            }

            String senha = request.getParameter("senha");
            if (senha == null || senha.isEmpty()) {
                throw new IllegalArgumentException(MessagesBundle.SENHA_OBRIGATORIA);
            }

            Denunciante denunciante = dao.login(usuario, senha);
            if (denunciante == null) {
                throw new IllegalArgumentException(MessagesBundle.ERRO_USUARIO_OU_SENHA_INCORRETOS);
            }

            request.getSession(true).setAttribute("denunciante", denunciante);

            t.commit();
            resp.addEntity(denunciante);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            t.rollback();
            resp.addException(e);
        }
        jsonResponse(response, resp);
    }

}