package com.yuyunchao.asus.mynews.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.yuyunchao.asus.mynews.Entry.NewsListEntity;
import com.yuyunchao.asus.mynews.Entry.NewsTypeEntity;
import com.yuyunchao.asus.mynews.constant.DBConstant;

import java.util.ArrayList;

/**
 *
 * Created by asus on 2016/10/24.
 */
public class MyDBHelper extends SQLiteOpenHelper {
    SQLiteDatabase db;
    public MyDBHelper(Context context) {
        super(context, DBConstant.DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBConstant.CREATE_TABLE_NEWS_TYPE);
        db.execSQL(DBConstant.CREATE_TABLE_NEWS_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 向news_type中插入数据
     */
    public void insert(NewsTypeEntity entity){
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+DBConstant.DB_TABLE_NAME_NEWS_TYPE
                +" where subgroup = ?",new String[]{entity.getSubgroup()});
        if (cursor.getCount()>=1){
            return;
        }
        db.execSQL(
                "insert into " + DBConstant.DB_TABLE_NAME_NEWS_TYPE + " values(null,?,?)",
                new Object[]{entity.getSubgroup(), entity.getSubid()}
        );
    }
    /**
     * 从news_type中查询
     */
    public ArrayList<NewsTypeEntity> select(){
        ArrayList<NewsTypeEntity> lists = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from "+ DBConstant.DB_TABLE_NAME_NEWS_TYPE+" order by subid ASC",
                null
        );
        int subid_index = cursor.getColumnIndex(DBConstant.DB_TABLE_NEWS_TYPE_COU_SUBID);
        int subgroup_index = cursor.getColumnIndex(DBConstant.DB_TABLE_NEWS_TYPE_COU_SUBGROUP);
        while (cursor.moveToNext()){
            NewsTypeEntity info = new NewsTypeEntity();
            info.setSubid(cursor.getInt(subid_index));
            info.setSubgroup(cursor.getString(subgroup_index));
            lists.add(info);
        }
        return lists;
    }
    /**
     * 向news_list中插入数据
     */
    public void insertIntoNewsList(NewsListEntity entity){

        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+DBConstant.DB_TABLE_NAME_NEWS_LIST
                +" where nid = ?",new String[]{entity.getNid()+""});
        if (cursor.getCount()>=1){
            return;
        }
        /**
         * "_id integer primary key autoincrement, " +
         "summary text," +
         "icon text," +
         "stamp text," +
         "link text," +
         "title text," +
         "type integer," +
         "subid integer," +
         "iscollection integer," +
         "nid integer)
         */
        db.execSQL(
                "insert into " + DBConstant.DB_TABLE_NAME_NEWS_LIST + " values(null,?,?,?,?,?,?,?,?,?)",
                new Object[]{entity.getSummary(), entity.getIcon(), entity.getStamp(),
                        entity.getLink(), entity.getTitle(), entity.getType(),
                        entity.getSubid(), entity.getIscollection(),entity.getNid()}
        );
    }
    /**
     * 从news_list中查询
     */
    public ArrayList<NewsListEntity> selectFromNewsList(int subid){
        ArrayList<NewsListEntity> lists = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from " + DBConstant.DB_TABLE_NAME_NEWS_LIST +" where subid = ?",
                new String[]{subid+""}
        );
        if (cursor == null){
            return null;
        }
        int icon_index = cursor.getColumnIndex(DBConstant.DB_TABLE_NEWS_LIST_COU_ICON);
        int title_index = cursor.getColumnIndex(DBConstant.DB_TABLE_NEWS_LIST_COU_TITLE);
        int link_index = cursor.getColumnIndex(DBConstant.DB_TABLE_NEWS_LIST_COU_LINK);
        int nid_index = cursor.getColumnIndex(DBConstant.DB_TABLE_NEWS_LIST_COU_NID);
        int stamp_index = cursor.getColumnIndex(DBConstant.DB_TABLE_NEWS_LIST_COU_STAMP);
        int summary_index = cursor.getColumnIndex(DBConstant.DB_TABLE_NEWS_LIST_COU_SUMMARY);
        int type_index = cursor.getColumnIndex(DBConstant.DB_TABLE_NEWS_LIST_COU_TYPE);
        int iscollection_index = cursor.getColumnIndexOrThrow(DBConstant.DB_TABLE_NEWS_LIST_COU_COLLECTION);
        while (cursor.moveToNext()){
            String icon = cursor.getString(icon_index);
            String title = cursor.getString(title_index);
            String link = cursor.getString(link_index);
            String stamp = cursor.getString(stamp_index);
            String summary = cursor.getString(summary_index);
            int nid = cursor.getInt(nid_index);
            int type = cursor.getInt(type_index);
            int iscollection = cursor.getInt(iscollection_index);
            NewsListEntity info = new NewsListEntity(icon, link, nid, stamp, summary, title, type, subid,iscollection);
            lists.add(info);
        }
        return lists;
    }

    /**
     * 从news_list中查询已经收藏的数据
     */
    public ArrayList<NewsListEntity> selectFromNewsListByCollection(){
        ArrayList<NewsListEntity> lists = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from " + DBConstant.DB_TABLE_NAME_NEWS_LIST +" where iscollection = ?",
                new String[]{2+""}
        );
        if (cursor == null){
            return null;
        }
        int icon_index = cursor.getColumnIndex(DBConstant.DB_TABLE_NEWS_LIST_COU_ICON);
        int title_index = cursor.getColumnIndex(DBConstant.DB_TABLE_NEWS_LIST_COU_TITLE);
        int link_index = cursor.getColumnIndex(DBConstant.DB_TABLE_NEWS_LIST_COU_LINK);
        int nid_index = cursor.getColumnIndex(DBConstant.DB_TABLE_NEWS_LIST_COU_NID);
        int stamp_index = cursor.getColumnIndex(DBConstant.DB_TABLE_NEWS_LIST_COU_STAMP);
        int summary_index = cursor.getColumnIndex(DBConstant.DB_TABLE_NEWS_LIST_COU_SUMMARY);
        int type_index = cursor.getColumnIndex(DBConstant.DB_TABLE_NEWS_LIST_COU_TYPE);
        int iscollection_index = cursor.getColumnIndexOrThrow(DBConstant.DB_TABLE_NEWS_LIST_COU_COLLECTION);
        int subid_index = cursor.getColumnIndexOrThrow(DBConstant.DB_TABLE_NEWS_TYPE_COU_SUBID);
        while (cursor.moveToNext()){
            String icon = cursor.getString(icon_index);
            String title = cursor.getString(title_index);
            String link = cursor.getString(link_index);
            String stamp = cursor.getString(stamp_index);
            String summary = cursor.getString(summary_index);
            int nid = cursor.getInt(nid_index);
            int type = cursor.getInt(type_index);
            int subid = cursor.getInt(subid_index);
            int iscollection = cursor.getInt(iscollection_index);
            NewsListEntity info = new NewsListEntity(icon, link, nid, stamp, summary, title, type, subid,iscollection);
            lists.add(info);
        }
        return lists;
    }

    /**
     * 更改收藏状态
     */
    public void updataNewsCollection(int nid){
        ArrayList<NewsListEntity> lists = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from " + DBConstant.DB_TABLE_NAME_NEWS_LIST +" where nid = ?",
                new String[]{nid+""}
        );
        cursor.moveToFirst();
        int iscollection_index = cursor.getColumnIndexOrThrow(DBConstant.DB_TABLE_NEWS_LIST_COU_COLLECTION);
        int iscollection = cursor.getInt(iscollection_index);
        if (iscollection == 1){
            iscollection = 2;
            Log.i("yyc", "2");
        }else if (iscollection == 2){
            iscollection = 1;
            Log.i("yyc", "1");
        }
        ContentValues cv = new ContentValues();
        cv.put(DBConstant.DB_TABLE_NEWS_LIST_COU_COLLECTION,iscollection);
        String[] args = {String.valueOf(nid)};
        db.update(DBConstant.DB_TABLE_NAME_NEWS_LIST,cv,"nid=?",args);
    }



}
