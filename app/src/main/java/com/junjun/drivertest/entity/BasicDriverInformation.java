package com.junjun.drivertest.entity;

import org.litepal.crud.DataSupport;

public class BasicDriverInformation extends DataSupport {

    private int id;
    private String driverName;
    private String phoneDriver;
    private String carType;
    private String plateNumber;

    public BasicDriverInformation(){

    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getPhoneDriver() {
        return phoneDriver;
    }

    public void setPhoneDriver(String phoneDriver) {
        this.phoneDriver = phoneDriver;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }





}
