package com.yuyunchao.asus.mynews.Entry;

import java.io.Serializable;

/**
 * Created by asus on 2016/10/25.
 */
public class NewsListEntity implements Serializable{
    private String summary;
    private String icon;
    private String stamp;
    private String link;
    private String title;
    private int nid;
    private int type;
    private int subid;
    private int iscollection;

    public NewsListEntity(){}

    public NewsListEntity(String icon, String link, int nid, String stamp, String summary, String title, int type,int subid,int iscollection) {
        this.icon = icon;
        this.link = link;
        this.nid = nid;
        this.stamp = stamp;
        this.summary = summary;
        this.title = title;
        this.type = type;
        this.subid = subid;
        this.iscollection = iscollection;
    }
    public int getIscollection() {
        return iscollection;
    }
    public void setIscollection(int iscollection) {
        this.iscollection = iscollection;
    }

    public int getSubid() {
        return subid;
    }

    public void setSubid(int subid) {
        this.subid = subid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
