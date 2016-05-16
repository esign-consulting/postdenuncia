package br.com.esign.postdenuncia.servlet;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import br.com.esign.postdenuncia.model.Denunciante;
import br.com.esign.postdenuncia.util.MailUtil;
import br.com.esign.postdenuncia.util.SMSUtil;

/**
 * Usuario servlet
 */
@SuppressWarnings ("serial")
public class UsuarioServlet extends GenericServlet {
    
	protected void enviarEmailConfirmacao(HttpServletRequest request, Denunciante denunciante) {
		String chaveConfirmacaoEmail = UUID.randomUUID().toString();
		String url = request.getRequestURL().toString();
		String link = url.substring(0, url.lastIndexOf("/")) + "/confirmaemail?chave=" + chaveConfirmacaoEmail;
		
		StringBuilder text = new StringBuilder();
		text.append("Prezad").append(denunciante.getArticle()).append(" ").append(denunciante.getNome()).append(",\r\n\r\n");
		text.append("Por favor, acesse o link abaixo para confirmar seu email:\r\n\r\n");
		text.append(link).append("\r\n\r\n");
		text.append("Obrigado,\r\n\r\n");
		text.append("Equipe do Post Denúncia.");
		
		MailUtil mailUtil = new MailUtil();
		try {
			mailUtil.sendMessage(denunciante.getEmail(), "Post Denúncia - Confirmação de email", text.toString());
		} finally {
			mailUtil = null;
		}
		
		denunciante.setChaveConfirmacaoEmail(chaveConfirmacaoEmail);
		denunciante.setEmailConfirmado(false);
	}
	
	protected void enviarSMSConfirmacao(HttpServletRequest request, Denunciante denunciante) {
		String chaveConfirmacaoCelular = UUID.randomUUID().toString();
		String url = request.getRequestURL().toString();
		String link = url.substring(0, url.lastIndexOf("/")) + "/confirmacelular?chave=" + chaveConfirmacaoCelular;
		
//		StringBuilder text = new StringBuilder();
//		text.append("Prezado ").append(denunciante.getNome()).append(",\r\n\r\n");
//		text.append("Por favor, acesse o link abaixo para confirmar seu celular:\r\n\r\n");
//		text.append(link).append("\r\n\r\n");
//		text.append("Obrigado,\r\n\r\n");
//		text.append("Equipe do Post Denúncia.");
		
		StringBuilder text = new StringBuilder();
		text.append("Para confirmar seu celular acesse: ");
		text.append(link);
		
		SMSUtil.simpleSend(denunciante.getCelular(), text.toString());
		
		denunciante.setChaveConfirmacaoCelular(chaveConfirmacaoCelular);
		denunciante.setCelularConfirmado(false);
	}
	
	protected void enviarEmailSenha(HttpServletRequest request, Denunciante denunciante) {
		StringBuilder text = new StringBuilder();
		text.append("Prezad").append(denunciante.getArticle()).append(" ").append(denunciante.getNome()).append(",\r\n\r\n");
		text.append("Sua senha é:\r\n\r\n");
		text.append(denunciante.getSenha()).append("\r\n\r\n");
		text.append("Atenciosamente,\r\n\r\n");
		text.append("Equipe do Post Denúncia.");
		
		MailUtil mailUtil = new MailUtil();
		try {
			mailUtil.sendMessage(denunciante.getEmail(), "Post Denúncia - Senha esquecida", text.toString());
		} finally {
			mailUtil = null;
		}
	}
	
	protected void enviarSMSSenha(HttpServletRequest request, Denunciante denunciante) {
//		StringBuilder text = new StringBuilder();
//		text.append("Prezado ").append(denunciante.getNome()).append(",\r\n\r\n");
//		text.append("Sua senha é:\r\n\r\n");
//		text.append(denunciante.getSenha()).append("\r\n\r\n");
//		text.append("Atenciosamente,\r\n\r\n");
//		text.append("Equipe do Post Denúncia.");
		
		StringBuilder text = new StringBuilder();
		text.append("Sua senha é: ");
		text.append(denunciante.getSenha());
		
		SMSUtil.simpleSend(denunciante.getCelular(), text.toString());
	}
	
}