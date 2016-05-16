package br.com.esign.postdenuncia.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Transaction;

import br.com.esign.postdenuncia.dao.DenuncianteDAO;
import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.model.Denunciante;
import br.com.esign.postdenuncia.util.MessagesBundle;
import br.com.esign.postdenuncia.util.ValidationUtil;

/**
 * Servlet implementation class SalvaUsuario
 */
@SuppressWarnings ("serial")
@WebServlet ("/salvausuario")
public class SalvaUsuario extends UsuarioServlet {
	
	@Inject private DenuncianteDAO dao;
	
    /**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logParameters(request);
		Response<Denunciante> resp = new Response<Denunciante>();
		Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
		try {
			HttpSession session = request.getSession(false);
			if (session == null) {
				throw new RuntimeException(MessagesBundle.ERRO_SESSAO_EXPIRADA);
			}
			Denunciante denunciante = (Denunciante) session.getAttribute("denunciante");
			if (denunciante == null) {
				throw new RuntimeException(MessagesBundle.USUARIO_NAO_ENCONTRADO_NA_SESSAO);
			}
			
			String nome = request.getParameter("nome");
			if (nome == null || nome.isEmpty()) {
				throw new IllegalArgumentException(MessagesBundle.NOME_OBRIGATORIO);
			}
			boolean alterouNome = !nome.equals(denunciante.getNome());
			if (alterouNome) {
				denunciante.setNome(nome);
			}
			
			String email = request.getParameter("email");
			if (email == null || email.isEmpty()) {
				throw new IllegalArgumentException(MessagesBundle.EMAIL_OBRIGATORIO);
			}
			boolean alterouEmail = !email.equals(denunciante.getEmail());
			if (alterouEmail) {
				ValidationUtil.validarEmail(email);
				if (dao.obterPeloEmail(email) != null) {
					throw new IllegalArgumentException(MessagesBundle.ERRO_EMAIL_REGISTRADO);
				}
				denunciante.setEmail(email);
			}
			
			String senha = request.getParameter("senha");
			if (senha != null && !senha.isEmpty()) {
				boolean alterouSenha = !senha.equals(denunciante.getSenha());
				if (alterouSenha) {
					if (senha.length() < 6) {
						throw new IllegalArgumentException(MessagesBundle.ERRO_SENHA_PEQUENA);
					}
					denunciante.setSenha(senha);
				}
			}
			
			String celular = request.getParameter("celular");
			if (celular != null && !celular.isEmpty()) {
				boolean alterouCelular = !celular.equals(denunciante.getCelular());
				if (alterouCelular) {
					ValidationUtil.validarCelular(celular);
					if (dao.obterPeloCelular(celular) != null) {
						throw new IllegalArgumentException(MessagesBundle.ERRO_CELULAR_REGISTRADO);
					}
					denunciante.setCelular(celular);
					enviarSMSConfirmacao(request, denunciante);
				}
			}
			
			if (alterouEmail) {
				enviarEmailConfirmacao(request, denunciante);
			}
			
			String retornoPorEmail = request.getParameter("retornoPorEmail");
			boolean alterouRetornoPorEmail = ("on".equals(retornoPorEmail) != denunciante.isRetornoPorEmail());
			if (alterouRetornoPorEmail) {
				denunciante.setRetornoPorEmail(!denunciante.isRetornoPorEmail());
			}
			
			String retornoPorSMS = request.getParameter("retornoPorSMS");
			boolean alterouRetornoPorSMS = ("on".equals(retornoPorSMS) != denunciante.isRetornoPorSMS());
			if (alterouRetornoPorSMS) {
				denunciante.setRetornoPorSMS(!denunciante.isRetornoPorSMS());
			}
			
			String retornoPorPush = request.getParameter("retornoPorPush");
			boolean alterouRetornoPorPush = ("on".equals(retornoPorPush) != denunciante.isRetornoPorPush());
			if (alterouRetornoPorPush) {
				denunciante.setRetornoPorPush(!denunciante.isRetornoPorPush());
			}
			
			dao.save(denunciante);
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