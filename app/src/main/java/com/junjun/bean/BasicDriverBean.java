package com.junjun.bean;

import java.io.Serializable;

public class BasicDriverBean implements Serializable {

    private String phoneCustom;//乘客手机号
    private String phoneDriver;
    private String driverName;//司机姓名
    private String carType;//汽车类型
    private String plateNumber;//车牌号
    private boolean isPassengerGetIn;//乘客是否上车
    private boolean isFinishOrder;//是否结束订单

    public BasicDriverBean(String phoneCustom,String phoneDriver,String driverName,String carType,String plateNumber,
                                boolean isPassengerGetIn,boolean isFinishOrder){
        this.phoneCustom=phoneCustom;
        this.phoneDriver=phoneDriver;
        this.driverName=driverName;
        this.carType=carType;
        this.plateNumber=plateNumber;
        this.isPassengerGetIn=isPassengerGetIn;
        this.isFinishOrder=isFinishOrder;
    }


    public String getPhoneCustom() {
        return phoneCustom;
    }

    public void setPhoneCustom(String phoneCustom) {
        this.phoneCustom = phoneCustom;
    }

    public String getPhoneDriver() {
        return phoneDriver;
    }

    public void setPhoneDriver(String phoneDriver) {
        this.phoneDriver = phoneDriver;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
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


    public boolean isPassengerGetIn() {
        return isPassengerGetIn;
    }

    public void setPassengerGetIn(boolean passengerGetIn) {
        isPassengerGetIn = passengerGetIn;
    }

    public boolean isFinishOrder() {
        return isFinishOrder;
    }

    public void setFinishOrder(boolean finishOrder) {
        isFinishOrder = finishOrder;
    }


}
