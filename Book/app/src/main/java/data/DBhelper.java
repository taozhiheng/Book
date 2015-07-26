package data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * old version DatabaseHelper
 * */

public class DBhelper {
    public static String path;

    private static final String DELETE_BOOK = "DROP TABLE IF EXISTS book;";
    private static final String DELETE_CHAPTER = "DROP TABLE IF EXISTS chaptable;";
    private static final String DELETE_MAIN = "DROP TABLE IF EXISTS maintable;";

    public static SQLiteDatabase createorOpenDatabase(){
        SQLiteDatabase sld=null;
        try{
            sld= SQLiteDatabase.openOrCreateDatabase(path + "/kyapp.db3", null);
        }catch(Exception e){e.printStackTrace();}
        return sld;
    }

    /**
     * table book书的信息
     * id            书的id
     * bookname  　　书名
     * author　　　　作者
     * publisher　　出版社
     * nofchap　　　章节数
     * chapcomp　　　已完成章节数
     * color　　　　　颜色
     *
     * table 章节信息
     * id　　　　　　章节的id
     * bookid　　　　所属书的id
     * chapname　　　章节名
     * tag　　　　　　是否已完成　２－－已完成
     * color　　　　　颜色
     *
     * table maintable 感觉没必要存在?
     * id            ?
     * chapname　　　章节名
     * bookname　　　书名
     * bookid　　　　书的id
     * chapid　　　　章节id
     * color　　　　　颜色
     * */
    public static void createTable(){
        SQLiteDatabase sld=createorOpenDatabase();
        try{
            sld.execSQL("create table if not exists book(id Integer primary key autoincrement not null,"
                    + "bookname varchar(50) not null,"
                    +"author varchar(50),"
                    + "publisher varchar(100),"
                    + "nofchap Integer,"
                    + "chapcomp Integer,"
                    + "color Integer) ");
            sld.execSQL("create table if not exists chaptable(id Integer primary key autoincrement not null," +
                    "bookid Integer," +
                    "chapname varchar(100)," +
                    "tag Integer," +
                    "color Integer)");

            sld.execSQL("create table if not exists maintable(id Integer primary key autoincrement not null," +
                    "chapname varchar(100)," +
                    "bookname varchar(100)," +
                    "bookid Integer,"+
                    "chapid Integer," +
                    "color Integer)");

            sld.close();
        }catch(Exception e){}
    }

    public static void deleteTables(SQLiteDatabase db)
    {
        if(db == null)
            return;
        db.execSQL(DELETE_BOOK);
        db.execSQL(DELETE_CHAPTER);
        db.execSQL(DELETE_MAIN);
    }

    public static boolean insertbook(BookInfo book){
        SQLiteDatabase sld=createorOpenDatabase();
        String bookname=book.getname();
        String author=book.getauthor();
        String publisher=book.getpublisher();
        int color=book.getColor();
        int chapcount=book.getChapcount();

        try{
            String sql="insert into book(bookname,author,publisher,nofchap,chapcomp,color) values(\""+bookname+"\","+"\""+author+"\""+","+"\""+publisher+"\","+chapcount+","
                    +"0,"+color+")";
            sld.execSQL(sql);
            sld.close();
            return true;
        }catch(Exception e){}

        return false;
    }

    public static boolean updateprogress(String bookid){
        SQLiteDatabase sld=createorOpenDatabase();
        String qstr="select * from book where id="+bookid;
        Log.e("sqlstr", qstr);
        Map<String,String> result=querybook(qstr,null).get(0);

        int count= Integer.parseInt(result.get("chapcomp"));
        count++;
        try{
            // sld.execSQL("update chaptable set tag=? where id=?",new Object[]{tag,id});
            ContentValues cv=new ContentValues();
            cv.put("chapcomp",count);
            sld.update("book",cv,"id=?",new String[]{bookid});
        }catch (Exception e){}


        return false;
    }

    public static boolean updatechaptag(int tag,int id){
        SQLiteDatabase sld=createorOpenDatabase();
        try{
           // sld.execSQL("update chaptable set tag=? where id=?",new Object[]{tag,id});
            ContentValues cv=new ContentValues();
            cv.put("tag",tag);
            sld.update("chaptable",cv,"id=?",new String[]{String.valueOf(id)});
        }catch (Exception e){}
        return false;
    }

    public static boolean insertchap(String chapname,int bookid){
        try{
            String sql="insert into chaptable(bookid,chapname,tag) values("+ String.valueOf(bookid)+",\""+chapname+"\","+"0"+")";
            SQLiteDatabase sld=createorOpenDatabase();
            sld.execSQL(sql);
        }catch (Exception e){};
        return false;
    }

    public static boolean insertchap(Map<String,String> item,String bookid){
        try{
            String sql="insert into chaptable(id,bookid,chapname,tag) values("+item.get("id")+","+bookid+",\""+item.get("chapname")+"\","+item.get("tag")+")";
            SQLiteDatabase sld=createorOpenDatabase();
            sld.execSQL(sql);
        }catch (Exception e){};
        return false;
    }

    public static boolean delete(String sql){
        SQLiteDatabase sld=createorOpenDatabase();
        try{
            sld.execSQL(sql);
            sld.close();
            return true;
        }catch(Exception e){}
        return false;
    }

//    public static boolean insertmainitem(Main_item mainitem){
//        try{
//            String sql="insert into maintable(chapname,bookname,chapid,bookid,color) values(\""+mainitem.item.get("chapname")+"\",\""+mainitem.item.get("bookname")+"\","+mainitem.item.get("chapid")+","+mainitem.item.get("bookid")+","+mainitem.item.get("color")+")";
//            Log.i("sql", sql);
//            SQLiteDatabase sld=createorOpenDatabase();
//            sld.execSQL(sql);
//        }catch (Exception e){};
//        return false;
//    }

    public static boolean cleartable(){
        SQLiteDatabase sld=createorOpenDatabase();
        sld.execSQL("delete from maintable");
        sld.close();
        return false;
    }

    public static ArrayList<Map<String,String>> querybook(String sql,String temp){
        Vector<Vector<String>> result=query(sql);
        ArrayList<Map<String,String>> list=new ArrayList<Map<String,String>>();
        for(int i=0;i<result.size();i++){
            Map<String,String> map=new HashMap<String, String>();
            map.put("id",result.get(i).get(0));
            map.put("bookname", result.get(i).get(1));
            map.put("author", result.get(i).get(2));
            map.put("publisher",result.get(i).get(3));
            map.put("nofchap", result.get(i).get(4));
            map.put("chapcomp", result.get(i).get(5));
            map.put("color",result.get(i).get(6));
            list.add(map);
        }
        return list;
    }

    public static ArrayList<Map<String,String>> querymaintable(String sql,String temp){
        Vector<Vector<String>> result=query(sql);
        ArrayList<Map<String,String>> list=new ArrayList<Map<String,String>>();
        for(int i=0;i<result.size();i++){
            Map<String,String> map=new HashMap<String, String>();
            map.put("id",result.get(i).get(0));
            map.put("bookname", result.get(i).get(2));
            map.put("chapname", result.get(i).get(1));
            map.put("chapid",result.get(i).get(4));
            map.put("bookid",result.get(i).get(3));
            map.put("color",result.get(i).get(5));
            list.add(map);
        }
        return list;
    }

    public static String querybookid(String sql,String temp){
        Vector<Vector<String>> result=query(sql);
        String id=result.get(result.size()-1).get(0);
        return id;
    }

    public static ArrayList<Map<String,String>> querychapter(String sql,String temp){
        Vector<Vector<String>> result=query(sql);
        Log.i("sql", sql);
        ArrayList<Map<String,String>> list=new ArrayList<Map<String,String>>();
        for(int i=0;i<result.size();i++){
            Map<String,String> map=new HashMap<String, String>();
            map.put("id",result.get(i).get(0));
            map.put("chapname", result.get(i).get(2));
            map.put("tag", result.get(i).get(3));
            list.add(map);
        }
        Log.i("list", String.valueOf(list.size()));
        return list;
    }

    public static Vector<Vector<String>> query(String sql){
        Vector<Vector<String>> vector=new Vector<Vector<String>>();
        SQLiteDatabase sld=createorOpenDatabase();
        try{
            Cursor cur=sld.rawQuery(sql, new String[] {});
            while(cur.moveToNext()){
                Vector<String> v=new Vector<String>();
                int col=cur.getColumnCount();
                for(int i=0;i<col;i++){
                    v.add(cur.getString(i));
                }
                vector.add(v);
            }
            cur.close();
            sld.close();
        }catch(Exception e){}
        return vector;
    }
}