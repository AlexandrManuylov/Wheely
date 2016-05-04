package org.chaynik.wheely.model.geo.dto;

public class GeoData {
    transient private int id;
    private double lat;
    private double lon;

    public GeoData() {

    }

    public GeoData(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }


    public double getLon() {
        return lon;
    }


}
