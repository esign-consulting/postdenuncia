package br.com.esign.postdenuncia.util;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;
import br.com.esign.postdenuncia.model.Coordenadas;

public class CoordenadasUtil {

    public static LatLng utmToDecAsLatLng(String utmLatitude, String utmLongitude, String utmZona) {
        double easting = Double.parseDouble(utmLongitude);
        double northing = Double.parseDouble(utmLatitude);
        char latZone = utmZona.charAt(utmZona.length() - 1);
        int lngZone = Integer.parseInt(utmZona.substring(0, utmZona.length() - 1));

        UTMRef utm = new UTMRef(easting, northing, latZone, lngZone);
        LatLng latLng = utm.toLatLng();
        return latLng;
    }

    public static Coordenadas utmToDecAsCoordenadas(String utmLatitude, String utmLongitude, String utmZona) {
        LatLng latLng = utmToDecAsLatLng(utmLatitude, utmLongitude, utmZona);
        double latitude = latLng.getLat();
        double longitude = latLng.getLng();

        return new Coordenadas(latitude, longitude);
    }

}