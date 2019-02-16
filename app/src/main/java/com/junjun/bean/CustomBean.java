package com.junjun.bean;

import java.io.Serializable;

public class CustomBean implements Serializable {

    private String phoneCustom;
    private double currentLat;//乘客当前的经纬度
    private double currentLong;
    private String startName;
    private double startLat;
    private double startLong;
    private String endName;
    private double endLat;
    private double endLong;
    private int price;


    public CustomBean(String phoneCustom,double currentLat,double currentLong, String startName, double startLat, double startLong,
                      String endName, double endLat, double endLong, int price
                      ){
        this.phoneCustom = phoneCustom;
        this.currentLat=currentLat;
        this.currentLong=currentLong;
        this.startName = startName;
        this.startLat = startLat;
        this.startLong = startLong;
        this.endName = endName;
        this.endLat = endLat;
        this.endLong = endLong;
        this.price = price;

    }

    public String getPhoneCustom() {
        return phoneCustom;
    }
    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLong() {
        return currentLong;
    }

    public void setCurrentLong(double currentLong) {
        this.currentLong = currentLong;
    }


    public String getStartName() {
        return startName;
    }

    public double getStartLong() {
        return startLong;
    }

    public String getEndName() {
        return endName;
    }

    public double getStartLat() {
        return startLat;
    }

    public double getEndLat() {
        return endLat;
    }

    public double getEndLong() {
        return endLong;
    }

    public int getPrice() {
        return price;
    }


}
