package br.com.esign.postdenuncia.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import uk.me.jstott.jcoord.LatLng;
import br.com.esign.postdenuncia.util.CoordenadasUtil;

public class Coordenadas {

    private double latitude;
    private double longitude;

    public Coordenadas(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordenadas(String latitude, String longitude) {
        if (latitude == null || latitude.isEmpty() || longitude == null || longitude.isEmpty()) {
            throw new IllegalArgumentException("A localização é obrigatória.");
        }
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
    }

    public Coordenadas(String utmLatitude, String utmLongitude, String utmZona) {
        if (utmLatitude == null || utmLatitude.isEmpty() || utmLongitude == null || utmLongitude.isEmpty() || utmZona == null || utmZona.isEmpty()) {
            throw new IllegalArgumentException("A localização é obrigatória.");
        }
        LatLng latLng = CoordenadasUtil.utmToDecAsLatLng(utmLatitude, utmLongitude, utmZona);
        this.latitude = latLng.getLat();
        this.longitude = latLng.getLng();
    }

    public Coordenadas(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude.doubleValue();
        this.longitude = longitude.doubleValue();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @JsonIgnore
    public String getLatitudeAsString() {
        return String.valueOf(latitude);
    }

    @JsonIgnore
    public String getLongitudeAsString() {
        return String.valueOf(longitude);
    }

    @JsonIgnore
    public BigDecimal getLatitudeAsBigDecimal() {
        return BigDecimal.valueOf(latitude);
    }

    @JsonIgnore
    public BigDecimal getLongitudeAsBigDecimal() {
        return BigDecimal.valueOf(longitude);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Coordenadas)) {
            return false;
        }
        Coordenadas coordenadas = (Coordenadas) obj;
        return (coordenadas.getLatitude() == getLatitude()
                && coordenadas.getLongitude() == getLongitude());
    }

    @Override
    public int hashCode() {
        long l1 = Double.doubleToLongBits(getLatitude());
        long l2 = Double.doubleToLongBits(getLongitude());
        int i1 = (int) (l1 ^ (l1 >>> 32));
        int i2 = (int) (l2 ^ (l2 >>> 32));
        return 37 * (i1 + i2);
    }

    @Override
    public String toString() {
        return "latitude: " + latitude + "\tlongitude: " + longitude;
    }

}