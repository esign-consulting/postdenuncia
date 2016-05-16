package br.com.esign.postdenuncia.servlet;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Transaction;

import br.com.esign.postdenuncia.dao.DenunciaDAO;
import br.com.esign.postdenuncia.dao.DenuncianteDAO;
import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.model.Denuncia;
import br.com.esign.postdenuncia.model.Denunciante;
import br.com.esign.postdenuncia.util.MessagesBundle;

import com.googlecode.genericdao.search.Search;
import java.text.ParseException;

/**
 * Servlet implementation class ListaDenuncia
 */
@SuppressWarnings("serial")
@WebServlet("/listadenuncia")
public class ListaDenuncia extends GenericServlet {

    @Inject
    private DenuncianteDAO denuncianteDAO;
    @Inject
    private DenunciaDAO denunciaDAO;

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
        Response<Denuncia> resp = new Response<>();
        Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        try {
            List<Denuncia> denunciaList;
            SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Search denunciaSearch = new Search(Denuncia.class);

            Denunciante denunciante = obterDenunciante(request);
            denunciaSearch.addFilterEqual("denunciante", denunciante);

            String dataInicial = request.getParameter("dataInicial");
            if (dataInicial != null) {
                Date datahoraInicial = parser.parse(dataInicial.concat(" 00:00:00"));
                denunciaSearch.addFilterGreaterOrEqual("datahora", datahoraInicial);
            }

            String datahoraInicial = request.getParameter("datahoraInicial");
            if (datahoraInicial != null) {
                denunciaSearch.addFilterGreaterThan("datahora", parser.parse(datahoraInicial));
            }

            String dataFinal = request.getParameter("dataFinal");
            if (dataFinal != null) {
                Date datahoraFinal = parser.parse(dataFinal.concat(" 23:59:59"));
                denunciaSearch.addFilterLessOrEqual("datahora", datahoraFinal);
            }

            Object[] tiposDenuncia = request.getParameterValues("tiposDenuncia");
            if (tiposDenuncia != null) {
                denunciaSearch.addFilterIn("tipo.codigo", tiposDenuncia);
            }

            String estado = request.getParameter("estado");
            if (estado != null && !estado.isEmpty()) {
                denunciaSearch.addFilterEqual("cidade.estado.sigla", estado);
                String cidade = request.getParameter("cidade");
                if (cidade != null && !cidade.isEmpty()) {
                    denunciaSearch.addFilterEqual("cidade.nome", cidade);
                }
            }

            denunciaSearch.addSortAsc("datahora");
            denunciaList = denunciaDAO.search(denunciaSearch);
            t.commit();
            resp.setSuccess(denunciaList);
        } catch (ServletException | IOException | ParseException e) {
            logger.error(e.getMessage(), e);
            t.rollback();
            resp.addException(e);
        }
        jsonResponse(response, resp);
    }

    private Denunciante obterDenunciante(HttpServletRequest request) throws ServletException, IOException {
        String emailDenunciante = request.getParameter("emailDenunciante");
        if (emailDenunciante != null && !emailDenunciante.isEmpty()) {
            Denunciante denunciante = denuncianteDAO.obterPeloEmail(emailDenunciante);
            if (denunciante == null) {
                throw new IllegalArgumentException(MessageFormat.format(MessagesBundle.EMAIL_DENUNCIANTE_NAO_ENCONTRADO, emailDenunciante));
            }
            return denunciante;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new RuntimeException(MessagesBundle.ERRO_SESSAO_EXPIRADA);
        }
        Denunciante denunciante = (Denunciante) session.getAttribute("denunciante");
        if (denunciante == null) {
            throw new RuntimeException(MessagesBundle.USUARIO_NAO_ENCONTRADO_NA_SESSAO);
        }
        return denunciante;
    }

}