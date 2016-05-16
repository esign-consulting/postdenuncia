package br.com.esign.postdenuncia.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import br.com.esign.postdenuncia.model.Denunciante;

/**
 * Servlet implementation class Logout
 */
@SuppressWarnings("serial")
@WebServlet("/logout")
public class Logout extends GenericServlet {

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
        Response<Denunciante> resp = new Response<>();
        try {
            Denunciante denunciante = null;
            HttpSession session = request.getSession(false);
            if (session != null) {
                denunciante = (Denunciante) session.getAttribute("denunciante");
                session.invalidate();
            }
            resp.addEntity(denunciante);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resp.addException(e);
        }
        jsonResponse(response, resp);
    }

}