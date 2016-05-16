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
import br.com.esign.postdenuncia.dao.FacebookUserDAO;
import br.com.esign.postdenuncia.dao.GooglePlusPersonDAO;
import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.model.Denunciante;
import br.com.esign.postdenuncia.model.FacebookUser;
import br.com.esign.postdenuncia.model.GooglePlusPerson;
import br.com.esign.postdenuncia.util.MessagesBundle;
import br.com.esign.postdenuncia.util.ValidationUtil;

/**
 * Servlet implementation class NovoUsuario
 */
@SuppressWarnings ("serial")
@WebServlet ("/novousuario")
public class NovoUsuario extends UsuarioServlet {
	
	@Inject private DenuncianteDAO denuncianteDAO;
	@Inject private FacebookUserDAO fbUserDAO;
	@Inject private GooglePlusPersonDAO gpPersonDAO;
	
    /**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logParameters(request);
		Response<Denunciante> resp = new Response<Denunciante>();
		Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
		try {
			Denunciante denunciante = null;
			
			String nome = request.getParameter("nome");
			if (nome == null || nome.isEmpty()) {
				throw new IllegalArgumentException(MessagesBundle.NOME_OBRIGATORIO);
			}
			String email = request.getParameter("email");
			if (email == null || email.isEmpty()) {
				throw new IllegalArgumentException(MessagesBundle.EMAIL_OBRIGATORIO);
			}
			ValidationUtil.validarEmail(email);
			if (denuncianteDAO.obterPeloEmail(email) != null) {
				throw new IllegalArgumentException(MessagesBundle.ERRO_EMAIL_REGISTRADO);
			}
			String senha = request.getParameter("senha");
			if (senha == null || senha.isEmpty()) {
				throw new IllegalArgumentException(MessagesBundle.SENHA_OBRIGATORIA);
			}
			if (senha.length() < 6) {
				throw new IllegalArgumentException(MessagesBundle.ERRO_SENHA_PEQUENA);
			}
			
			denunciante = new Denunciante();
			denunciante.setNome(nome);
			denunciante.setEmail(email);
			denunciante.setSenha(senha);
			
			String fbId = request.getParameter("fbId");
			if (fbId != null && !fbId.isEmpty()) {
				FacebookUser fbUser = fbUserDAO.find(fbId);
				if (fbUser != null) {
					denunciante.setFacebookUser(fbUser);
				}
			}
			
			String gpId = request.getParameter("gpId");
			if (gpId != null && !gpId.isEmpty()) {
				GooglePlusPerson gpPerson = gpPersonDAO.find(gpId);
				if (gpPerson != null) {
					denunciante.setGooglePlusPerson(gpPerson);
				}
			}
			
			String celular = request.getParameter("celular");
			if (celular != null && !celular.isEmpty()) {
				ValidationUtil.validarCelular(celular);
				if (denuncianteDAO.obterPeloCelular(celular) != null) {
					throw new IllegalArgumentException(MessagesBundle.ERRO_CELULAR_REGISTRADO);
				}
				denunciante.setCelular(celular);
				
				enviarSMSConfirmacao(request, denunciante);
			}
			
			enviarEmailConfirmacao(request, denunciante);
			
			String retornoPorEmail = request.getParameter("retornoPorEmail");
			denunciante.setRetornoPorEmail("on".equals(retornoPorEmail));
			String retornoPorSMS = request.getParameter("retornoPorSMS");
			denunciante.setRetornoPorSMS("on".equals(retornoPorSMS));
			String retornoPorPush = request.getParameter("retornoPorPush");
			denunciante.setRetornoPorPush("on".equals(retornoPorPush));
			
			denuncianteDAO.save(denunciante);
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