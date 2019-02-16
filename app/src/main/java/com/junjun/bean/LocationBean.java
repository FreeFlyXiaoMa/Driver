package com.junjun.bean;

import java.io.Serializable;

public class LocationBean implements Serializable {
    private String phoneDriver;
    private double longitude;
    private double latitude;
    private String distance;

    public LocationBean(String phoneDriver, double longitude, double latitude,String distance) {
        this.phoneDriver = phoneDriver;
        this.longitude = longitude;
        this.latitude = latitude;
        this.distance=distance;
    }


    public String getPhoneDriver() {
        return phoneDriver;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getDistance() {
        return distance;
    }
}
