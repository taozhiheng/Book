package com.hustunique.myapplication;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import data.Book;
import data.ChapterInfo;
import data.DBOperate;
import data.DBhelper;
import util.Constant;

/**
 * Created by taozhiheng on 15-7-9.
 * MyApplication class, there are many important data about the application,such as userInfo
 */
public class MyApplication extends Application {

    private static DBOperate mDBOperate;
    private static boolean mUserOnLine;
    private static String mUser;
    private static String mUserMail;
    private static boolean mUserSex;
    private static String mUserUrl;
    private static String mPath;
    private static String mAuthorization;

    private static boolean[] mShouldUpdate = new boolean[4];

    private static boolean mIsSync;

    private static String mUrlHead;

    @Override
    public void onCreate() {
        super.onCreate();
        setUrlHead();
        mDBOperate = DBOperate.getInstance(this);
        mPath = getFilesDir().toString();
        mUserOnLine = false;
        mShouldUpdate[0] = true;
        mShouldUpdate[1] = true;
        mShouldUpdate[2] = true;
        mShouldUpdate[3] = true;

    }

    public static void setSync(boolean sync)
    {
        mIsSync = sync;
    }

    public static boolean getSync()
    {
        return mIsSync;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static void setUrlHead()
    {
        mUrlHead = "http://182.92.158.119:2333";
//        mUrlHead = "http://"+Constant.HOST_NAME+":"+Constant.PORT;
        //new MyAsyncTask().execute(Constant.HOST_NAME);
    }

    public static String getUrlHead()
    {
        return mUrlHead;
    }

    public static void setUserMail(String mail)
    {
        mUserMail = mail;
    }

    public static String getUserMail()
    {
        return mUserMail;
    }


    public static void setUserUrl(String url)
    {
        mUserUrl = url;
    }

    public static String getUserUrl()
    {
        return mUserUrl;
    }

    public static void setAuthorization(String authorization)
    {
        mAuthorization = authorization;
    }

    public static String getAuthorization()
    {
        return mAuthorization;
    }

    public static void setShouldUpdate(int index)
    {
        mShouldUpdate[index] = true;
    }

    public static boolean getUpdateFlag(int index)
    {
        boolean should = mShouldUpdate[index];
        mShouldUpdate[index] = false;
        return should;
    }

    public static DBOperate getDBOperateInstance()
    {
        return mDBOperate;
    }

    public static void copyOldDB()
    {
        SQLiteDatabase db;
        try
        {
            db = SQLiteDatabase.openDatabase(mPath + "/kyapp.db3", null, SQLiteDatabase.OPEN_READWRITE);
        }catch (SQLiteCantOpenDatabaseException e)
        {
            e.printStackTrace();
            return;
        }
        if(IsTableExist(db, "book"))
        {
            String[] query = new String[]{"id", "bookname",
                    "author", "publisher", "color"};
            Cursor cursor = db.query("book",
                    query, null, null, null, null, null);
            Book book = new Book();
            while(cursor.moveToNext())
            {
                book.setName(cursor.getString(1));
                book.setAuthor(cursor.getString(2));
                book.setPress(cursor.getString(3));
                book.setColor(cursor.getInt(4));
                book.setCreateTime(util.TimeUtil.getNeedTime(System.currentTimeMillis()));
                getDBOperateInstance().insertBook(book, getChapterInfos(db, cursor.getInt(0)));
            }
            cursor.close();
        }
        DBhelper.deleteTables(db);
        db.close();
    }

    private static List<ChapterInfo> getChapterInfos(SQLiteDatabase db, int id)
    {
        List<ChapterInfo> chapterInfos = new ArrayList<>();
        if(IsTableExist(db, "chaptable"))
        {
            Cursor cursor = db.query("chaptable",
                    new String[]{"chapname"},
                    "bookid="+id , null, null, null, null);
            int i = 0;
            while (cursor.moveToNext())
            {
                String name = cursor.getString(0);
                chapterInfos.add(new ChapterInfo(i, name));
                i++;
            }
            cursor.close();
        }
        return chapterInfos;
    }

    //判断表是否存在
    private static boolean IsTableExist(SQLiteDatabase db, String tableName) {
        boolean isTableExist=false;
        if(db == null||tableName == null){
            return false;
        }
        Cursor cursor = db
                .rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='"+tableName.trim()+"'", null);
        if(cursor.moveToNext()){
            int count = cursor.getInt(0);
            if(count>0){
                isTableExist = true;
            }
        }
        cursor.close();
        return isTableExist;
    }

    public static void setUser(String user)
    {
        mUser = user;
    }

    public static String getUser()
    {
        return mUser;
    }

    public static void setUserSex(boolean sex)
    {
        mUserSex = sex;
    }

    public static boolean getUserSex()
    {
        return mUserSex;
    }

    public static void setUserOnLine(boolean onLine)
    {
        mUserOnLine = onLine;
    }

    public static boolean getUserOnLine()
    {
        return mUserOnLine;
    }

}
