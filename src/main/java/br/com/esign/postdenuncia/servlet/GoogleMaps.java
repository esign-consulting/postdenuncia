package br.com.esign.postdenuncia.servlet;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.esign.postdenuncia.google.geocode.BrazilGoogleGeocode;

/**
 * Servlet implementation class GoogleMaps
 */
@SuppressWarnings("serial")
@WebServlet("/googlemaps")
public class GoogleMaps extends GenericServlet {

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
        String result = null;
        try {
            String address = URLDecoder.decode(request.getParameter("address"), "utf-8");
            BrazilGoogleGeocode googleGeocode = new BrazilGoogleGeocode(address);
            result = googleGeocode.getJsonString();
        } catch (RuntimeException e) {
            throw new ServletException(e);
        }
        jsonResponse(response, result);
    }

}