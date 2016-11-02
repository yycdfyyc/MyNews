package com.yuyunchao.asus.mynews.Entry;

import java.util.ArrayList;

/**
 * Created by asus on 2016/11/2.
 */
public class UserEntity {
    private String iconUrl;
    private String uid;
    private int comNum;
    private int integration;
    private ArrayList<UserLoginLogEntity> entities;

    public int getComNum() {
        return comNum;
    }

    public void setComNum(int comNum) {
        this.comNum = comNum;
    }

    public ArrayList<UserLoginLogEntity> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<UserLoginLogEntity> entities) {
        this.entities = entities;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getIntegration() {
        return integration;
    }

    public void setIntegration(int integration) {
        this.integration = integration;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
