package com.example.taozhiheng.musicplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by taozhiheng on 14-12-17.
 *  自定义数据库辅助类
 */
public class DBHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "music.db";//数据库名称
    private static final int DB_VERSION = 1;         //数据库版本
    private Context myContext;                       //数据库context

    //创建local_music表
    private static final String CREATE_LOCALMUSIC = "CREATE TABLE local_music(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "song varchar(100), singer varchar(50), length long, path varchar(80), favor INTEGER, " +
            "download INTEGER, recent long, song_id long, album_id long);";
    //删除local_music表
    private static final String DELETE_LOCALMUSIC = "DROP TABLE IF EXISTS local_music;";
    public DBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Toast.makeText(myContext, "create database "+DB_NAME, Toast.LENGTH_SHORT).show();
        db.execSQL(CREATE_LOCALMUSIC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Toast.makeText(myContext, "create database "+DB_NAME, Toast.LENGTH_SHORT).show();
        db.execSQL(DELETE_LOCALMUSIC);
        db.execSQL(CREATE_LOCALMUSIC);
    }
}
