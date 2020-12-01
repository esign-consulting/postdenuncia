package br.com.esign.postdenuncia.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Transaction;

import br.com.esign.postdenuncia.dao.CidadeDAO;
import br.com.esign.postdenuncia.dao.DenunciaDAO;
import br.com.esign.postdenuncia.dao.DenuncianteDAO;
import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.dao.TipoDenunciaDAO;
import br.com.esign.postdenuncia.google.geocode.BrazilGeocodeResponse;
import br.com.esign.postdenuncia.google.geocode.BrazilGoogleGeocode;
import br.com.esign.postdenuncia.model.Cidade;
import br.com.esign.postdenuncia.model.Coordenadas;
import br.com.esign.postdenuncia.model.Denuncia;
import br.com.esign.postdenuncia.model.Denunciante;
import br.com.esign.postdenuncia.model.TipoDenuncia;
import br.com.esign.postdenuncia.model.TipoDevice;
import br.com.esign.postdenuncia.util.MessagesBundle;

import com.googlecode.genericdao.search.Search;

/**
 * Servlet implementation class NovaDenuncia
 */
@SuppressWarnings ("serial")
@WebServlet ("/novadenuncia")
@MultipartConfig
public class NovaDenuncia extends GenericServlet {
	
	@Inject private TipoDenunciaDAO tipoDenunciaDAO;
	@Inject private DenuncianteDAO denuncianteDAO;
	@Inject private CidadeDAO cidadeDAO;
	@Inject private DenunciaDAO denunciaDAO;
	
	@Inject @Any private Event<Denuncia> eventoNovaDenuncia;
	
    /**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logParameters(request);
		Response<Denuncia> resp = new Response<Denuncia>();
		Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
		try {
			TipoDenuncia tipoDenuncia = obterTipoDenuncia(request);
			Denunciante denunciante = obterDenunciante(request);
			Date datahora = obterDatahora(request);
			
			Denuncia denuncia = denunciaDAO.obter(tipoDenuncia, denunciante, datahora);
			if (denuncia != null) {
				throw new RuntimeException(MessagesBundle.ERRO_DENUNCIA_SUBMETIDA);
			}
			
			denuncia = new Denuncia();
			denuncia.setTipo(tipoDenuncia);
			denuncia.setDenunciante(denunciante);
			denuncia.setDatahora(datahora);
			denuncia.setInclusao(new Date());
			
			String latitude = getValueAsString(request.getPart("latitude"));
			String longitude = getValueAsString(request.getPart("longitude"));
			BrazilGoogleGeocode googleGeocode = new BrazilGoogleGeocode(latitude, longitude);
			BrazilGeocodeResponse geocodeResponse = (BrazilGeocodeResponse) googleGeocode.getResponseObject();
			if (!geocodeResponse.isBrazil()) {
				throw new IllegalArgumentException(MessagesBundle.DENUNCIA_RESTRITA_BRASIL);
			}
			Coordenadas coordenadas = new Coordenadas(latitude, longitude);
			denuncia.setCoordenadas(coordenadas);
			
			Coordenadas coordenadasGoogle = geocodeResponse.getCoordenadas();
			denuncia.setCoordenadasGoogle(coordenadasGoogle);
			
			denuncia.setEndereco(geocodeResponse.getEnderecoCompleto());
			denuncia.setBairro(geocodeResponse.getNomeBairro());
			
			String nomeEstado = geocodeResponse.getNomeEstado();
			String nomeCidade = geocodeResponse.getNomeCidade();
			Cidade cidade = cidadeDAO.obter(nomeEstado, nomeCidade);
			denuncia.setCidade(cidade);
			
			String infoAdicional = getValueAsString(request.getPart("infoAdicional"));
			if (infoAdicional != null && !infoAdicional.isEmpty()) {
				denuncia.setInfoAdicional(infoAdicional);
			} else {
				if (tipoDenuncia.getCodigo().equals("fumacapreta")) {
					throw new IllegalArgumentException(MessagesBundle.PLACA_OBRIGATORIA);
				}
			}
			
			String retorno = getValueAsString(request.getPart("retorno"));
			denuncia.setRetorno("on".equals(retorno));
			
			byte[] foto = getValueAsBytes(request.getPart("foto"));
			if (foto == null || foto.length == 0) {
				throw new ServletException(MessagesBundle.FOTO_OBRIGATORIA);
			}
			
			String tipoDevice = request.getHeader("User-Agent");
			if (tipoDevice != null && !tipoDevice.isEmpty() && tipoDevice.indexOf(TipoDevice.ANDROID) != -1) {
				denuncia.setTipoDeviceOrigem(TipoDevice.ANDROID);
			} else {
				denuncia.setTipoDeviceOrigem(TipoDevice.APPLE);
			}
			
			denunciaDAO.save(denuncia);
			try {
				FileOutputStream file = new FileOutputStream(new File(denuncia.getImageFilename()));
				file.write(foto);
				file.close();
			} catch (Exception e) {
				throw new ServletException(MessagesBundle.ERRO_PERSISTENCIA_FOTO, e);
			}
			
			denuncia.setFoto("./mostrafoto?idDenuncia=" + denuncia.getId());
			denunciaDAO.save(denuncia);
			t.commit();
			eventoNovaDenuncia.fire(denuncia);
			resp.addEntity(denuncia);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			t.rollback();
			resp.addException(e);
		}
		jsonResponse(response, resp);
	}
	
	private TipoDenuncia obterTipoDenuncia(HttpServletRequest request) throws ServletException, IOException {
		String codigoTipoDenuncia = getValueAsString(request.getPart("codigoTipoDenuncia"));
		if (codigoTipoDenuncia == null || codigoTipoDenuncia.isEmpty()) {
			throw new IllegalArgumentException(MessagesBundle.TIPO_DENUNCIA_OBRIGATORIO);
		}
		Search tipoDenunciaSearch = new Search(TipoDenuncia.class);
		tipoDenunciaSearch.addFilterEqual("codigo", codigoTipoDenuncia);
		TipoDenuncia tipoDenuncia = tipoDenunciaDAO.searchUnique(tipoDenunciaSearch);
		if (tipoDenuncia == null) {
			throw new IllegalArgumentException(MessageFormat.format(MessagesBundle.CODIGO_TIPO_DENUNCIA_NAO_ENCONTRADO, codigoTipoDenuncia));
		}
		return tipoDenuncia;
	}
	
	private Denunciante obterDenunciante(HttpServletRequest request) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Denunciante denunciante = (Denunciante) session.getAttribute("denunciante");
			if (denunciante != null) {
				return denunciante;
			}
		}
		String emailDenunciante = getValueAsString(request.getPart("emailDenunciante"));
		String senhaDenunciante = getValueAsString(request.getPart("senhaDenunciante"));
		Denunciante denunciante = denuncianteDAO.login(emailDenunciante, senhaDenunciante);
		if (denunciante == null) {
			throw new IllegalArgumentException(MessagesBundle.EMAIL_E_SENHA_DENUNCIANTE_NAO_ENCONTRADO);
		}
		return denunciante;
	}
	
	private Date obterDatahora(HttpServletRequest request) throws ServletException, IOException, ParseException {
		String datahora = getValueAsString(request.getPart("datahora"));
		if (datahora == null || datahora.isEmpty()) {
			throw new IllegalArgumentException(MessagesBundle.DATAHORA_OBRIGATORIA);
		}
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return formatter.parse(datahora);
	}
	
}