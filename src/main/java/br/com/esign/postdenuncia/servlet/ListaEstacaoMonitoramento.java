package br.com.esign.postdenuncia.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Transaction;

import br.com.esign.postdenuncia.dao.EstacaoMonitoramentoDAO;
import br.com.esign.postdenuncia.dao.EstadoDAO;
import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.dao.MedicaoDAO;
import br.com.esign.postdenuncia.dao.UltimaMedicaoCache;
import br.com.esign.postdenuncia.google.geocode.BrazilGeocodeResponse;
import br.com.esign.postdenuncia.google.geocode.BrazilGoogleGeocode;
import br.com.esign.postdenuncia.model.Coordenadas;
import br.com.esign.postdenuncia.model.EstacaoMonitoramento;
import br.com.esign.postdenuncia.model.Estado;
import br.com.esign.postdenuncia.model.Medicao;
import org.apache.commons.lang.StringUtils;

/**
 * Servlet implementation class ListaEstacaoMonitoramento
 */
@SuppressWarnings("serial")
@WebServlet("/listaestacaomonitoramento")
public class ListaEstacaoMonitoramento extends GenericServlet {

    @Inject
    private EstadoDAO estadoDAO;
    @Inject
    private EstacaoMonitoramentoDAO estacaoMonitoramentoDAO;
    @Inject
    private MedicaoDAO medicaoDAO;

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
        Response<ListaEstacaoMonitoramentoResponse> resp = new Response<>();
        Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        try {
            resp.addEntity(obterListaEstacaoMonitoramentoResponse(request));
            t.commit();
        } catch (IOException | ParseException e) {
            logger.error(e.getMessage(), e);
            t.rollback();
            resp.addException(e);
        }
        jsonResponse(response, resp);
    }

    protected ListaEstacaoMonitoramentoResponse obterListaEstacaoMonitoramentoResponse(HttpServletRequest request) throws IOException, ParseException {
        List<EstacaoMonitoramento> list = null;

        String latitude = request.getParameter("latitude");
        String longitude = request.getParameter("longitude");
        String endereco = request.getParameter("endereco");

        BrazilGoogleGeocode googleGeocode = null;
        if (!StringUtils.isBlank(latitude) && !StringUtils.isBlank(longitude)) {
            googleGeocode = new BrazilGoogleGeocode(latitude, longitude);
        } else if (!StringUtils.isBlank(endereco)) {
            googleGeocode = new BrazilGoogleGeocode(endereco);
        }

        Coordenadas coordenadas = null;
        if (googleGeocode != null) {
            BrazilGeocodeResponse geocodeResponse = (BrazilGeocodeResponse) googleGeocode.getResponseObject();
            if (geocodeResponse.isBrazil()) {
                String nomeEstado = geocodeResponse.getNomeEstado();
                Estado estado = estadoDAO.obterPeloNome(nomeEstado);
                if (estado != null) {
                    coordenadas = (!StringUtils.isBlank(endereco))
                            ? geocodeResponse.getCoordenadas()
                            : new Coordenadas(latitude, longitude);
                    list = estacaoMonitoramentoDAO.listarPorProximidade(estado, coordenadas);
                }
            }

        } else {
            String siglaEstado = request.getParameter("estado");
            String nomeCidade = request.getParameter("cidade");

            if (!StringUtils.isBlank(siglaEstado)) {
                list = (!StringUtils.isBlank(nomeCidade))
                        ? estacaoMonitoramentoDAO.listarPorSiglaEstadoENomeCidade(siglaEstado, nomeCidade)
                        : estacaoMonitoramentoDAO.listarPorSiglaEstado(siglaEstado);
            } else {
                list = (!StringUtils.isBlank(nomeCidade))
                        ? estacaoMonitoramentoDAO.listarPorNomeCidade(nomeCidade)
                        : estacaoMonitoramentoDAO.findAll();
            }
        }

        String paramUltimaMedicao = request.getParameter("ultimaMedicao");
        String paramDatahora = request.getParameter("datahora");
        boolean ultimaMedicaoIsenta = ("isenta".equalsIgnoreCase(paramUltimaMedicao));
        List<EstacaoMonitoramento> estacaoMonitoramentoList;
        if (list != null && !list.isEmpty() && !ultimaMedicaoIsenta) {
            Date datahora = null;
            if (!StringUtils.isBlank(paramDatahora)) {
                SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                datahora = parser.parse(URLDecoder.decode(paramDatahora, "utf-8"));
            }
            boolean ultimaMedicaoOpcional = ("opcional".equalsIgnoreCase(paramUltimaMedicao));
            estacaoMonitoramentoList = new ArrayList<>(list.size());
            for (EstacaoMonitoramento estacaoMonitoramento : list) {
                Medicao ultimaMedicao;
                if (datahora == null) {
                    ultimaMedicao = UltimaMedicaoCache.getCache().getUltimaMedicao(estacaoMonitoramento);
                } else {
                    ultimaMedicao = medicaoDAO.obterUltimaMedicao(estacaoMonitoramento, datahora);
                }
                if ((ultimaMedicaoOpcional) || (!ultimaMedicaoOpcional && ultimaMedicao != null)) {
                    estacaoMonitoramento.setUltimaMedicao(ultimaMedicao);
                    estacaoMonitoramentoList.add(estacaoMonitoramento);
                }
            }
        } else {
            estacaoMonitoramentoList = list;
        }

        return new ListaEstacaoMonitoramentoResponse(coordenadas, estacaoMonitoramentoList);
    }

    public class ListaEstacaoMonitoramentoResponse {

        private final Coordenadas coordenadas;
        private final List<EstacaoMonitoramento> estacaoMonitoramentoList;

        public ListaEstacaoMonitoramentoResponse(Coordenadas coordenadas, List<EstacaoMonitoramento> estacaoMonitoramentoList) {
            this.coordenadas = coordenadas;
            this.estacaoMonitoramentoList = estacaoMonitoramentoList;
        }

        public Coordenadas getCoordenadas() {
            return coordenadas;
        }

        public List<EstacaoMonitoramento> getEstacaoMonitoramentoList() {
            return estacaoMonitoramentoList;
        }

    }

}