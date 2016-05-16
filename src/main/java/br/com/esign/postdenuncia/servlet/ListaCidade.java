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

import br.com.esign.postdenuncia.dao.CidadeDAO;
import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.model.Cidade;

import com.googlecode.genericdao.search.Search;

/**
 * Servlet implementation class ListaCidade
 */
@SuppressWarnings("serial")
@WebServlet("/listacidade")
public class ListaCidade extends GenericServlet {

    @Inject
    private CidadeDAO dao;

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
        Response<Cidade> resp = new Response<>();
        Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        try {
            List<Cidade> cidadeList;
            Search cidadeSearch = new Search(Cidade.class);

            String emailDenunciante = request.getParameter("emailDenunciante");
            if (emailDenunciante != null && !emailDenunciante.isEmpty()) {
                cidadeSearch.addFilterEqual("denuncias.denunciante.email", emailDenunciante);
                cidadeSearch.setDistinct(true);
            }

            Object[] tiposDenuncia = request.getParameterValues("tiposDenuncia");
            if (tiposDenuncia != null) {
                cidadeSearch.addFilterIn("denuncias.tipo.codigo", tiposDenuncia);
                cidadeSearch.setDistinct(true);
            }

            cidadeSearch.addSortAsc("nome");
            cidadeSearch.addSortAsc("estado.sigla");
            cidadeList = dao.search(cidadeSearch);
            t.commit();
            resp.setSuccess(cidadeList);
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            t.rollback();
            resp.addException(e);
        }
        jsonResponse(response, resp);
    }

}