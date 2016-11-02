package com.yuyunchao.asus.mynews.Entry;

/**
 * Created by asus on 2016/11/1.
 */
public class UserLoginLogEntity {
    private String time;
    private String address;
    private String device;

    public UserLoginLogEntity(String address, String device, String time) {
        this.address = address;
        this.device = device;
        this.time = time;
    }

    public UserLoginLogEntity() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
