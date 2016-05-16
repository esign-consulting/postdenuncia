package br.com.esign.postdenuncia.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.esign.postdenuncia.manager.EstacaoMonitoramentoManager;
import br.com.esign.postdenuncia.model.EstacaoMonitoramento;

/**
 * Servlet implementation class NovaEstacaoMonitoramento
 */
@SuppressWarnings ("serial")
@WebServlet ("/salvaestacaomonitoramento")
public class SalvaEstacaoMonitoramento extends GenericServlet {
	
	@Inject private EstacaoMonitoramentoManager estacaoMonitoramentoMgr;
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logParameters(request);
		Response<EstacaoMonitoramento> resp = new Response<EstacaoMonitoramento>();
		try {
			String siglaOrgaoResponsavel = request.getParameter("siglaOrgaoResponsavel");
			String nome = request.getParameter("nome");
			String decLatitude = request.getParameter("decLatitude");
			String decLongitude = request.getParameter("decLongitude");
			String utmLatitude = request.getParameter("utmLatitude");
			String utmLongitude = request.getParameter("utmLongitude");
			String utmZona = request.getParameter("utmZona");
			String endereco = request.getParameter("endereco");
			String enderecoIndex = request.getParameter("enderecoIndex");
			
			EstacaoMonitoramento estacaoMonitoramento = estacaoMonitoramentoMgr.salvar(siglaOrgaoResponsavel, nome,
					decLatitude, decLongitude, utmLatitude, utmLongitude, utmZona, endereco, enderecoIndex);
			
			resp.addEntity(estacaoMonitoramento);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			resp.addException(e);
		}
		jsonResponse(response, resp);
	}
	
}