package data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import com.hustunique.myapplication.MainActivity;
import com.hustunique.myapplication.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import util.Constant;
import util.TimeUtil;

/**
 * Created by taozhiheng on 14-12-17.
 *  自定义数据库辅助类
 */
public class DBHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "pokeBook.db";//数据库名称
    private static final int DB_VERSION = 1;         //数据库版本
    private Context myContext;                       //数据库context

    //创建book表
    //_id int 自增
    //uuid string 在服务器唯一id,未录入时等于_id
    //name　string 书名
    //author string 作者
    //press string 出版社
    //url string 封面地址
    //color int 颜色
    //word_num long 字数
    //type　int 类型　０－想读　１－在读　２－已读
    //start_time　long 开始阅读时间
    //end_time　　long 结束阅读时间
    //create_time　long 创建时间
    //status　 int 状态　０－同步正常　１－添加 2-修改 3-删除未同步
    //type_status int 类型是否改变 -1-未改变，０－未读，１－在读，２－已读
    public static final String CREATE_BOOK = "CREATE TABLE book(" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "uuid varchar(50), " +
            "isbn varchar(20), " +
            "name varchar(50), " +
            "author varchar(50), " +
            "press varchar(50), " +
            "url varchar(50), " +
            "color INTEGER, " +
            "word_num long, " +
            "type INTEGER, " +
            "start_time varchar(30), " +
            "end_time varchar(30), " +
            "create_time varchar(30), " +
            "status INTEGER," +
            "type_status INTEGER);";
    //删除book表
    public static final String DELETE_BOOK = "DROP TABLE IF EXISTS book;";

    //创建chapter表
    //_id  int 自增章节id
    //id int 章节索引
    //book_id int 隶属书在服务器的唯一id，未录入时等于书的_id
    //name string  章节名
    //type int 类型 0-未读　１－在读 2－已读　３－重复
    //start_time　long 创建时间
    //end_time　long 最后编辑时间
    //status　 int 状态　０－同步正常　１－添加　２－修改　３－删除未同步
    //type_status int 类型是否改变 -1-未改变，０－未读，１－在读，２－已读，３－重复
    //local_book_id long 本地书籍的唯一id
    public static final String CREATE_CHAPTER = "CREATE TABLE chapter("+
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id INTEGER, " +
            "book_id INTEGER, "+
            "name varchar(50), " +
            "type INTEGER, " +
            "start_time long, " +
            "end_time long, " +
            "status INTEGER," +
            "type_status INTEGER," +
            "local_book_id long);";
    public static final String DELETE_CHAPTER = "DROP TABLE IF EXISTS chapter;";
    public DBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //Toast.makeText(myContext, "create database "+DB_NAME, Toast.LENGTH_SHORT).show();
        db.execSQL(CREATE_BOOK);
        db.execSQL(CREATE_CHAPTER);
        init(db);
    }

    private void init(SQLiteDatabase db)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                makeFile();
            }
        }).start();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", "Hello World");
        contentValues.put("color", 0);
        contentValues.put("word_num", 16);
        contentValues.put("type", 1);
        String dirPath = Environment.getExternalStorageDirectory()
                .getPath() + "/Pokebook"; // 要保存的路径
        String fileName = "sample.jpeg"; // 文件名
        contentValues.put("url", dirPath+"/"+fileName );
        long millis = System.currentTimeMillis();
        contentValues.put("start_time", TimeUtil.getNeedTime(millis));
        contentValues.put("end_time", TimeUtil.getNeedTime(millis+4*24*60*60*1000));
        contentValues.put("create_time", TimeUtil.getNeedTime(millis));
        contentValues.put("status", 1);
        contentValues.put("type_status", 1);
        long id = db.insert(Constant.TABLE_BOOK, null, contentValues);

        ContentValues values = new ContentValues();
        values.put("local_book_id",id);
        values.put("type", 0);
        values.put("status", 1);
        values.put("type_status", 0);

        values.put("id", 1);
        values.put("name", "与君初相识");
        db.insert(Constant.TABLE_CHAPTER, null, values);
        values.put("id", 2);
        values.put("name", "犹如明月归");
        db.insert(Constant.TABLE_CHAPTER, null, values);
        values.put("id", 3);
        values.put("name", "天涯明月新");
        db.insert(Constant.TABLE_CHAPTER, null, values);
        values.put("id", 4);
        values.put("name", "朝暮最相思");
        db.insert(Constant.TABLE_CHAPTER, null, values);
    }

    private void makeFile() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String dirPath = Environment.getExternalStorageDirectory()
                    .getPath() + "/Pokebook"; // 要保存的路径
            String fileName = "sample.jpeg"; // 文件名

            try {
                File dir = new File(dirPath);
                if (!dir.exists()) {// 如果目录不存在，创建目录
                    dir.mkdirs();
                }

                File file = new File(dirPath + "/" + fileName);
                if (!file.exists()) {// 如果文件不存在，创建文件
                    AssetManager assetManager = myContext.getAssets();
                    InputStream ins = assetManager.open("sample.jpeg");
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[8192];
                    int count = 0;
                    while ((count = ins.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                    fos.close();
                    ins.close();
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
       // Toast.makeText(myContext, "create database "+DB_NAME, Toast.LENGTH_SHORT).show();
        db.execSQL(DELETE_BOOK);
        db.execSQL(CREATE_BOOK);
        db.execSQL(DELETE_CHAPTER);
        db.execSQL(CREATE_CHAPTER);
    }
}
