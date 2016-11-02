package com.yuyunchao.asus.mynews.constant;

/**
 * Created by asus on 2016/10/24.
 */
public class DBConstant {
    public static final String DB_NAME = "news.db";
    public static final String DB_TABLE_NAME_NEWS_TYPE = "news_type";
    public static final String DB_TABLE_NAME_NEWS_LIST = "news_list";
    public static final String DB_TABLE_NEWS_TYPE_COU_SUBID= "subid";
    public static final String DB_TABLE_NEWS_TYPE_COU_SUBGROUP= "subgroup";
    public static final String DB_TABLE_NEWS_LIST_COU_SUMMARY= "summary";
    public static final String DB_TABLE_NEWS_LIST_COU_ICON= "icon";
    public static final String DB_TABLE_NEWS_LIST_COU_STAMP= "stamp";
    public static final String DB_TABLE_NEWS_LIST_COU_LINK= "link";
    public static final String DB_TABLE_NEWS_LIST_COU_TITLE= "title";
    public static final String DB_TABLE_NEWS_LIST_COU_NID= "nid";
    public static final String DB_TABLE_NEWS_LIST_COU_TYPE= "type";
    public static final String DB_TABLE_NEWS_LIST_COU_COLLECTION= "iscollection";

    public static final String CREATE_TABLE_NEWS_TYPE = "CREATE TABLE  "+DB_TABLE_NAME_NEWS_TYPE+" (" +
            "_id integer primary key autoincrement, " +
            "subgroup text," +
            "subid integer)";
    public static final String CREATE_TABLE_NEWS_LIST = "CREATE TABLE  "+DB_TABLE_NAME_NEWS_LIST+" (" +
            "_id integer primary key autoincrement, " +
            "summary text," +
            "icon text," +
            "stamp text," +
            "link text," +
            "title text," +
            "type integer," +
            "subid integer," +
            "iscollection integer," +
            "nid integer)";
}
