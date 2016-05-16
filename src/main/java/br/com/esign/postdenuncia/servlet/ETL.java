package br.com.esign.postdenuncia.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.esign.postdenuncia.etl.FonteTimer;
import br.com.esign.postdenuncia.util.Global;
import br.com.esign.postdenuncia.util.MessagesBundle;

/**
 * Servlet implementation class ETL
 */
@SuppressWarnings("serial")
@WebServlet("/etl")
public class ETL extends GenericServlet {

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
            String action = request.getParameter("action");
            if (action == null || action.isEmpty()) {
                throw new IllegalArgumentException(MessagesBundle.ACAO_OBRIGATORIA);
            }
            if (!action.equalsIgnoreCase("start") && !action.equalsIgnoreCase("stop")) {
                throw new IllegalArgumentException(MessagesBundle.ERRO_ACAO_DESCONHECIDA);
            }

            String name = request.getParameter("name");
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException(MessagesBundle.NOME_ETL_OBRIGATORIO);
            }
            FonteTimer timer = Global.getInstance().getFonteTimerInstance(name);
            if (timer == null) {
                throw new IllegalArgumentException(MessagesBundle.ETL_NAO_ENCONTRADO);
            }

            if (action.equalsIgnoreCase("start")) {
                timer.start();
            } else {
                timer.stop();
            }
            text = MessagesBundle.SUCESSO_EXECUCAO_ACAO;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            text = e.getMessage();
        }
        textResponse(response, text);
    }

}