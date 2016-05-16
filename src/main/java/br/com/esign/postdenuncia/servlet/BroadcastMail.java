package br.com.esign.postdenuncia.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import br.com.esign.postdenuncia.util.MailUtil;
import br.com.esign.postdenuncia.util.MessagesBundle;

/**
 * Servlet implementation class BroadcastMail
 */
@SuppressWarnings("serial")
@WebServlet("/broadcastmail")
public class BroadcastMail extends GenericServlet {

    @Inject
    private DenuncianteDAO denuncianteDAO;

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
            String emailSubject = request.getParameter("subject");
            if (emailSubject == null || emailSubject.isEmpty()) {
                throw new IllegalArgumentException(MessagesBundle.ASSUNTO_EMAIL_OBRIGATORIO);
            }
            String emailText = request.getParameter("text");
            if (emailText == null || emailText.isEmpty()) {
                throw new IllegalArgumentException(MessagesBundle.TEXTO_EMAIL_OBRIGATORIO);
            }
            List<Denunciante> denunciantesList = denuncianteDAO.findAll();
            if (denunciantesList == null || denunciantesList.isEmpty()) {
                throw new RuntimeException(MessagesBundle.DENUNCIANTE_NAO_ENCONTRADO);
            }
            MailUtil mailUtil = new MailUtil();
            List<MailUtil.MailData> mailDataList = new ArrayList<>(denunciantesList.size());
            denunciantesList.stream().forEach((denunciante) -> {
                StringBuilder entireEmailText = new StringBuilder();
                entireEmailText.append("Prezad").append(denunciante.getArticle()).append(" ").append(denunciante.getNome()).append(",\r\n\r\n");
                entireEmailText.append(emailText).append("\r\n\r\n");
                entireEmailText.append("Atenciosamente,\r\n\r\n");
                entireEmailText.append("Equipe do Post Denúncia.");
                if (!denunciante.isEmailConfirmado()) {
                    String url = request.getRequestURL().toString();
                    String link = url.substring(0, url.lastIndexOf("/")) + "/confirmaemail?chave=" + denunciante.getChaveConfirmacaoEmail();
                    entireEmailText.append("\r\n\r\nPS: Para confirmar o email, por favor acesse o link: ").append(link);
                }
                mailDataList.add(mailUtil.getMailData(denunciante.getEmail(), "Post Denúncia - " + emailSubject, entireEmailText.toString()));
            });
            mailUtil.sendMessage(mailDataList);
            t.commit();
            text = MessagesBundle.SUCESSO_BROADCAST_MAIL;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            t.rollback();
            text = e.getMessage();
        }
        textResponse(response, text);
    }

}