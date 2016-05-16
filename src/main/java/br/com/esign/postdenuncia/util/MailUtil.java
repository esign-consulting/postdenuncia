package br.com.esign.postdenuncia.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MailUtil {

    private final Logger logger = LogManager.getLogger();

    public void sendMessage(String to, String subject, String text) {
        sendMessage(getMailData(to, subject, text));
    }

    public void sendMessage(MailData mailData) {
        List<MailData> mailDataList = new ArrayList<>(1);
        mailDataList.add(mailData);
        sendMessage(mailDataList);
    }

    public void sendMessage(final List<MailData> mailDataList) {
        Thread thread = new Thread() {
            final String from = "admin@postdenuncia.com.br";

            @Override
            public void run() {
                Properties properties = new Properties();
                properties.put("mail.smtp.host", "mail.postdenuncia.com.br");
                properties.put("mail.smtp.port", "587");
                properties.put("mail.smtp.auth", "true");
                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, "stella01");
                    }
                });

                mailDataList.stream().forEach((mailData) -> {
                    send(session, mailData);
                });
            }

            private void send(Session session, MailData mailData) {
                final String charset = "utf-8";

                MimeMessage message = new MimeMessage(session);
                try {
                    message.setFrom(new InternetAddress(from));
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailData.getTo()));
                    message.setSubject(mailData.getSubject(), charset);
                    message.setText(mailData.getText(), charset);
                    Transport.send(message);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

        };
        thread.start();
    }

    public MailData getMailData(String to, String subject, String text) {
        return new MailData(to, subject, text);
    }

    public class MailData {

        private final String to;
        private final String subject;
        private final String text;

        public MailData(String to, String subject, String text) {
            this.to = to;
            this.subject = subject;
            this.text = text;
        }

        public String getTo() {
            return to;
        }

        public String getSubject() {
            return subject;
        }

        public String getText() {
            return text;
        }

    }

}