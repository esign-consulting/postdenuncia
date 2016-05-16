package br.com.esign.postdenuncia.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SMSUtil {

    private static final Logger logger = LogManager.getLogger();

    public static void simpleSend(final String destinatario, final String msg) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://www.facilitamovel.com.br/api/simpleSend.ft?user=esign&password=123135&destinatario=" + destinatario + "&msg=" + URLEncoder.encode(msg, "utf-8"));
                    URLConnection conn = url.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    String result = IOUtils.toString(in);
                    if ("1".equals(result)) {
                        throw new Exception("Login inválido.");
                    } else if ("2".equals(result)) {
                        throw new Exception("Usuário sem créditos.");
                    } else if ("3".equals(result)) {
                        throw new Exception("Celular inválido.");
                    } else if ("4".equals(result)) {
                        throw new Exception("Mensagem inválida.");
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        };
        thread.start();
    }

}