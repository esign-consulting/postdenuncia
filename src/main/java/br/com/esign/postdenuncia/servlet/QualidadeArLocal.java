package br.com.esign.postdenuncia.servlet;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.esign.postdenuncia.manager.EstacaoMonitoramentoManager;
import br.com.esign.postdenuncia.model.Coordenadas;
import br.com.esign.postdenuncia.model.EstacaoMonitoramento;

/**
 * Servlet implementation class QualidadeArLocal
 */
@SuppressWarnings ("serial")
@WebServlet ("/qualidadearlocal")
public class QualidadeArLocal extends GenericServlet {
	
	@Inject private EstacaoMonitoramentoManager estacaoMonitoramentoMgr;
	
    /**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logParameters(request);
		Response<EstacaoMonitoramento> resp = new Response<EstacaoMonitoramento>();
		try {
			String latitude = request.getParameter("latitude");
			String longitude = request.getParameter("longitude");
			String nEstacoes = request.getParameter("nEstacoes");
			
			Coordenadas coordenadas = new Coordenadas(latitude, longitude);
			List<EstacaoMonitoramento> estacaoMonitoramentoList = estacaoMonitoramentoMgr.listarPorProximidade(
				coordenadas, (nEstacoes == null || nEstacoes.isEmpty()) ? 1 : Integer.parseInt(nEstacoes));
			
			resp.setSuccess(estacaoMonitoramentoList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			resp.addException(e);
		}
		jsonResponse(response, resp);
	}
	
}