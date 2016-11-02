package com.yuyunchao.asus.mynews.JSONManger;

import com.yuyunchao.asus.mynews.Entry.CommentListEntity;
import com.yuyunchao.asus.mynews.Entry.NewsListEntity;
import com.yuyunchao.asus.mynews.Entry.NewsTypeEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 *
 * json解析与新闻数据管理类
 * Created by asus on 2016/10/24.
 */
public class MyJsonManger {

    private ArrayList<NewsTypeEntity> newsSortList = new ArrayList<>();
    private ArrayList<NewsListEntity> newsListList = new ArrayList<>();
    /**
     * 新闻分类的json解析
     * @param s
     * @return
     */
    public ArrayList<NewsTypeEntity> getNewsSortJson(String s){
        try {
            JSONObject object = new JSONObject(s);
            if (object.getString("message").equals("OK")){
                JSONArray array = object.getJSONArray("data");
                for (int i = 0; i<array.length();i++) {
                    JSONObject mObject = array.getJSONObject(i);
                    JSONArray mArray = mObject.getJSONArray("subgrp");
                    for (int j = 0; j < mArray.length(); j++) {
                        JSONObject object1 = mArray.getJSONObject(j);
                        NewsTypeEntity newsTypeEntity =
                                new NewsTypeEntity(object1.getString("subgroup"), object1.getInt("subid"));
                        newsSortList.add(newsTypeEntity);
                    }
                }
                return newsSortList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 新闻列表的json解析
     */
    public ArrayList<NewsListEntity> getNewsListJson(String s,int subid){
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (jsonObject.getString("message").equals("OK")) {
                JSONArray array = jsonObject.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    NewsListEntity entity = new NewsListEntity();
                    entity.setIcon(object.getString("icon"));
                    entity.setLink(object.getString("link"));
                    entity.setSummary(object.getString("summary"));
                    entity.setStamp(object.getString("stamp"));
                    entity.setNid(object.getInt("nid"));
                    entity.setType(object.getInt("type"));
                    entity.setTitle(object.getString("title"));
                    entity.setSubid(subid);
                    entity.setIscollection(1);//1位未收藏，2为收藏
                    newsListList.add(entity);
                }
                return newsListList;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;

    }

    /**
     * 评论的解析
     * @return
     */
    public ArrayList<CommentListEntity> getCommentJson(String s){
        try {
            ArrayList<CommentListEntity> commentListEntities = new ArrayList<>();
            JSONObject object = new JSONObject(s);
            String message = object.getString("message");
            int status = object.getInt("status");
            if (message.equals("OK") && status == 0) {
                JSONArray array = object.getJSONArray("data");
                for (int i=0;i<array.length();i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    CommentListEntity entity = new CommentListEntity();
                    entity.setUid(jsonObject.getString("uid"));
                    entity.setCid(jsonObject.getInt("cid"));
                    entity.setComContent(jsonObject.getString("content"));
                    entity.setPortrait(jsonObject.getString("portrait"));
                    entity.setStamp(jsonObject.getString("stamp"));
                    commentListEntities.add(entity);
                }
                return commentListEntities;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


}