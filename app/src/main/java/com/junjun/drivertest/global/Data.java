package com.junjun.drivertest.global;

import org.apache.mina.core.session.IoSession;
import org.litepal.LitePalApplication;

public class Data extends LitePalApplication{
    private String phone;
    private IoSession session;
    private String city;
    private String driverName;//司机姓名
    private String carType;//汽车类型
    private String plateNumber;//车牌号

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
}
