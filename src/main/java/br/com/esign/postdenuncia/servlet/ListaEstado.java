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

import br.com.esign.postdenuncia.dao.EstadoDAO;
import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.model.Estado;

import com.googlecode.genericdao.search.Search;

/**
 * Servlet implementation class ListaEstado
 */
@SuppressWarnings("serial")
@WebServlet("/listaestado")
public class ListaEstado extends GenericServlet {

    @Inject
    private EstadoDAO dao;

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
        Response<Estado> resp = new Response<>();
        Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        try {
            List<Estado> estadoList;
            Search estadoSearch = new Search(Estado.class);
            estadoSearch.addSortAsc("nome");
            estadoList = dao.search(estadoSearch);
            t.commit();
            resp.setSuccess(estadoList);
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            t.rollback();
            resp.addException(e);
        }
        jsonResponse(response, resp);
    }

}