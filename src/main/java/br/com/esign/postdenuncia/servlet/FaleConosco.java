package br.com.esign.postdenuncia.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.esign.postdenuncia.util.MailUtil;
import br.com.esign.postdenuncia.util.MessagesBundle;
import br.com.esign.postdenuncia.util.ValidationUtil;

/**
 * Servlet implementation class FaleConosco
 */
@SuppressWarnings("serial")
@WebServlet("/faleconosco")
public class FaleConosco extends UsuarioServlet {

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
        try {
            String nome = request.getParameter("nome");
            if (nome == null || nome.isEmpty()) {
                throw new IllegalArgumentException(MessagesBundle.NOME_OBRIGATORIO);
            }

            String email = request.getParameter("email");
            boolean emailInformado = (email != null && !email.isEmpty());

            String celular = request.getParameter("celular");
            boolean celularInformado = (celular != null & !celular.isEmpty());

            if (!emailInformado && !celularInformado) {
                throw new IllegalArgumentException(MessagesBundle.NOME_OU_CELULAR_OBRIGATORIO);
            }
            if (emailInformado) {
                ValidationUtil.validarEmail(email);
            }
            if (celularInformado) {
                ValidationUtil.validarCelular(celular);
            }

            String mensagem = request.getParameter("mensagem");
            if (mensagem == null || mensagem.isEmpty()) {
                throw new IllegalArgumentException(MessagesBundle.MENSAGEM_OBRIGATORIA);
            }

            String mailto = request.getParameter("mailto");

            StringBuilder emailTextMessage = new StringBuilder();
            emailTextMessage.append("Nome: ").append(nome).append("\r\n");
            if (emailInformado) {
                emailTextMessage.append("Email: ").append(email).append("\r\n");
            }
            if (celularInformado) {
                emailTextMessage.append("Celular: ").append(celular).append("\r\n");
            }
            emailTextMessage.append("Mensagem: ").append(mensagem);

            MailUtil mailUtil = new MailUtil();
            mailUtil.sendMessage(mailto, "Post Denúncia - Fale conosco", emailTextMessage.toString());

            text = MessagesBundle.SUCESSO_ENVIO_MENSAGEM;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            text = e.getMessage();
        }
        textResponse(response, text);
    }

}