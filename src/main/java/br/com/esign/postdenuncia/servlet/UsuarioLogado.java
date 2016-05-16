package br.com.esign.postdenuncia.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import br.com.esign.postdenuncia.model.Denunciante;
import br.com.esign.postdenuncia.util.MessagesBundle;

/**
 * Servlet implementation class UsuarioLogado
 */
@SuppressWarnings ("serial")
@WebServlet ("/usuariologado")
public class UsuarioLogado extends GenericServlet {
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logParameters(request);
		Response<Denunciante> resp = new Response<Denunciante>();
		try {
			HttpSession session = request.getSession(false);
			if (session == null) {
				throw new RuntimeException(MessagesBundle.ERRO_SESSAO_EXPIRADA);
			}
			Denunciante denunciante = (Denunciante) session.getAttribute("denunciante");
			if (denunciante == null) {
				throw new RuntimeException(MessagesBundle.USUARIO_NAO_ENCONTRADO_NA_SESSAO);
			}
			resp.addEntity(denunciante);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			resp.addException(e);
		}
		jsonResponse(response, resp);
	}
	
}