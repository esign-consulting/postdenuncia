package br.com.esign.postdenuncia.servlet;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Transaction;

import br.com.esign.postdenuncia.dao.DenunciaDAO;
import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.model.Denuncia;
import br.com.esign.postdenuncia.util.MessagesBundle;
import org.hibernate.resource.transaction.spi.TransactionStatus;

/**
 * Servlet implementation class Logout
 */
@SuppressWarnings ("serial")
@WebServlet ("/mostrafoto")
public class MostraFoto extends GenericServlet {
	
	@Inject DenunciaDAO dao;
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logParameters(request);
		Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
		try {
			String idDenuncia = request.getParameter("idDenuncia");
			if (idDenuncia == null || idDenuncia.isEmpty()) {
				throw new IllegalArgumentException(MessagesBundle.ID_DENUNCIA_OBRIGATORIO);
			}
			Denuncia denuncia = dao.find(Integer.valueOf(idDenuncia));
			if (denuncia == null) {
				throw new IllegalArgumentException(MessageFormat.format(MessagesBundle.ID_DENUNCIA_NAO_ENCONTRADA, idDenuncia));
			}
			String filename = denuncia.getImageFilename();
			t.commit();
			jpgResponse(response, filename);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (t.getStatus() != TransactionStatus.COMMITTED) {
				t.rollback();
			}
		}
	}
	
	@Override
	protected void jpgResponse(HttpServletResponse response, String filename) throws IOException {
		File file = new File(filename);
		BufferedImage img = ImageIO.read(file);
		
		int width = img.getWidth();
		int height = img.getHeight();
		
		byte[] before;
		if (width > 298) {
			int newWidth = 298;
			int newHeight = (newWidth * height) / width;
			
			BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, img.getType());
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(img, 0, 0, newWidth, newHeight, null);
			g.dispose();
			
			ByteArrayOutputStream out0 = new ByteArrayOutputStream();
			ImageIO.write(resizedImage, "jpg", out0);
			out0.flush();
			before = out0.toByteArray();
			out0.close();
			
		} else {
			InputStream in = new FileInputStream(file);
			before = new byte[(int) file.length()];
			in.read(before);
			in.close();
		}
		
		ByteArrayOutputStream out1 = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out1);
		gzip.write(before);
		gzip.close();
		byte[] after = out1.toByteArray();
		out1.close();
		
		response.addHeader("Content-Encoding", "gzip");
		response.setContentLength(after.length);
		
		Calendar inOneYear = Calendar.getInstance();
		inOneYear.add(Calendar.YEAR, 1);
		response.setDateHeader("Expires", inOneYear.getTimeInMillis());
		
		OutputStream out2 = response.getOutputStream();
		out2.write(after);
		out2.flush();
	}
	
}