package com.junjun.drivertest.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/7/6 0006.
 */

public class JourneyEntity extends DataSupport {

    private int id;
    private String journeyTime;
    private String startAddress;
    private String endAddress;
    private boolean isChecked;

    public JourneyEntity(){

    }

    //构造器
    private JourneyEntity(int id, String journeyTime, String startAddress, String endAddress, boolean isChecked){
        super();
        this.id=id;
        this.journeyTime=journeyTime;
        this.startAddress=startAddress;
        this.endAddress=endAddress;
        this.isChecked=isChecked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJourneyTime() {
        return journeyTime;
    }

    public void setJourneyTime(String journeyTime) {
        this.journeyTime = journeyTime;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}
