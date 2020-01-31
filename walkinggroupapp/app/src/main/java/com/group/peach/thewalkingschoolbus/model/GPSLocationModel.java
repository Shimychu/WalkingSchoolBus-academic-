package com.group.peach.thewalkingschoolbus.model;

import java.util.Date;

public class GPSLocationModel {
    private Double lat;
    private Double lng;
    private Date timestamp;

    public GPSLocationModel(){

    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
