package br.com.esign.postdenuncia.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.esign.postdenuncia.manager.OrgaoResponsavelManager;
import br.com.esign.postdenuncia.model.LineChartData;

/**
 * Servlet implementation class QualidadeArHistorica
 */
@SuppressWarnings ("serial")
@WebServlet ("/qualidadearhistorica")
public class QualidadeArHistorica extends GenericServlet {
	
	@Inject private OrgaoResponsavelManager orgaoResponsavelMgr;
	
    /**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logParameters(request);
		Response<LineChartData> resp = new Response<LineChartData>();
		try {
			String siglaOrgaoResponsavel = request.getParameter("siglaOrgaoResponsavel");
			String nomeEstacaoMonitoramento = request.getParameter("nomeEstacaoMonitoramento");
			
			List<LineChartData> lineChartDataList = new ArrayList<LineChartData>(1);
			LineChartData lineChartData = orgaoResponsavelMgr.obterLineChartData(siglaOrgaoResponsavel, nomeEstacaoMonitoramento);
			lineChartDataList.add(lineChartData);
			
			resp.setSuccess(lineChartDataList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			resp.addException(e);
		}
		jsonResponse(response, resp);
	}
	
}