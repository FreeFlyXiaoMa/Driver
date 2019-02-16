package com.junjun.bean;

import java.io.Serializable;

public class OrderBean implements Serializable {
    private String phoneCustom;
    private int price;
    private boolean isEvaluated;//订单是否评价
    private boolean isPay;//是否付款
    private int starNumbers;//几星评价

    public OrderBean(String phoneCustom, int price, boolean isEvaluated, boolean isPay, int starNumbers){
        this.phoneCustom=phoneCustom;
        this.price=price;
        this.isEvaluated=isEvaluated;
        this.isPay=isPay;
        this.starNumbers=starNumbers;
    }


    public String getPhoneCustom() {
        return phoneCustom;
    }

    public void setPhoneCustom(String phoneCustom) {
        this.phoneCustom = phoneCustom;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isEvaluated() {
        return isEvaluated;
    }

    public void setEvaluated(boolean evaluated) {
        isEvaluated = evaluated;
    }

    public boolean isPay() {
        return isPay;
    }

    public void setPay(boolean pay) {
        isPay = pay;
    }

    public int getStarNumbers() {
        return starNumbers;
    }

    public void setStarNumbers(int starNumbers) {
        this.starNumbers = starNumbers;
    }







}
