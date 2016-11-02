package com.yuyunchao.asus.mynews.Entry;

/**
 *新闻类型实体类
 * Created by asus on 2016/10/24.
 */
public class NewsTypeEntity {
    private int subid;
    private String subgroup;
    public NewsTypeEntity(){

    }
    public NewsTypeEntity(String subgroup, int subid){
        this.subgroup = subgroup;
        this.subid = subid;

    }
    public int getSubid() {
        return subid;
    }

    public void setSubid(int subid) {
        this.subid = subid;
    }

    public String getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(String subgroup) {
        this.subgroup = subgroup;
    }
}
