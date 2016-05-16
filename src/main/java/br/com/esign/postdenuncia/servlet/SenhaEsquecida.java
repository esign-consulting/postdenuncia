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
import br.com.esign.postdenuncia.util.ValidationUtil;
import org.hibernate.resource.transaction.spi.TransactionStatus;

/**
 * Servlet implementation class SenhaEsquecida
 */
@SuppressWarnings ("serial")
@WebServlet ("/senhaesquecida")
public class SenhaEsquecida extends UsuarioServlet {
	
	@Inject private DenuncianteDAO dao;
	
    /**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logParameters(request);
		Response<Denunciante> resp = new Response<Denunciante>();
		Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
		try {
			Denunciante denunciante = null;
			
			String email = request.getParameter("email");
			boolean emailInformado = (email != null && !email.isEmpty());
			
			String celular = request.getParameter("celular");
			boolean celularInformado = (celular != null && !celular.isEmpty());
			
			if (!emailInformado && !celularInformado) {
				throw new IllegalArgumentException(MessagesBundle.NOME_OU_CELULAR_REGISTRADO_OBRIGATORIO);
			}
			
			if (emailInformado) {
				ValidationUtil.validarEmail(email);
				denunciante = dao.obterPeloEmail(email);
				if (denunciante == null && !celularInformado) {
					throw new IllegalArgumentException(MessagesBundle.EMAIL_NAO_ENCONTRADO);
				}
				if (denunciante != null && !celularInformado && !denunciante.isEmailConfirmado()) {
					enviarEmailConfirmacao(request, denunciante);
					denunciante.setEnviarSenhaPorEmail(true);
					dao.save(denunciante);
					t.commit();
					throw new IllegalArgumentException(MessagesBundle.ERRO_EMAIL_NAO_CONFIRMADO);
				}
			}
			
			if (celularInformado) {
				ValidationUtil.validarCelular(celular);
				if (denunciante == null) {
					denunciante = dao.obterPeloCelular(celular);
					if (denunciante == null) {
						throw new IllegalArgumentException(MessagesBundle.CELULAR_NAO_ENCONTRADO);
					}
					if (!denunciante.isCelularConfirmado()) {
						enviarSMSConfirmacao(request, denunciante);
						denunciante.setEnviarSenhaPorSMS(true);
						dao.save(denunciante);
						t.commit();
						throw new IllegalArgumentException(MessagesBundle.ERRO_CELULAR_NAO_CONFIRMADO);
					}
				} else {
					if (!celular.equals(denunciante.getCelular())) {
						throw new IllegalArgumentException(MessagesBundle.USUARIO_NAO_ENCONTRADO);
					}
				}
			}
			
			if (denunciante == null) {
				throw new IllegalArgumentException(MessagesBundle.USUARIO_NAO_ENCONTRADO);
			} else {
				if (emailInformado) {
					enviarEmailSenha(request, denunciante);
				}
				if (celularInformado) {
					enviarSMSSenha(request, denunciante);
				}
			}
			
			t.commit();
			resp.addEntity(denunciante);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (t.getStatus() != TransactionStatus.COMMITTED) {
				t.rollback();
			}
			resp.addException(e);
		}
		jsonResponse(response, resp);
	}
	
}