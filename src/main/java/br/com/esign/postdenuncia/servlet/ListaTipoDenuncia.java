package br.com.esign.postdenuncia.servlet;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Transaction;

import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.dao.TipoDenunciaDAO;
import br.com.esign.postdenuncia.model.TipoDenuncia;

/**
 * Servlet implementation class ListaTipoDenuncia
 */
@SuppressWarnings("serial")
@WebServlet("/listatipodenuncia")
public class ListaTipoDenuncia extends GenericServlet {

    @Inject
    private TipoDenunciaDAO dao;

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
        Response<TipoDenuncia> resp = new Response<>();
        Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        try {
            List<TipoDenuncia> tipoDenunciaList;
            tipoDenunciaList = dao.findAll();
            t.commit();
            resp.setSuccess(tipoDenunciaList);
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            t.rollback();
            resp.addException(e);
        }
        jsonResponse(response, resp);
    }

}