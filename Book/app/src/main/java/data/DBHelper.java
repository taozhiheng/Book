package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

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
    //status　 int 状态　０－同步正常　１－添加 2-修改 3-添加且修改　４－删除未同步
    private static final String CREATE_BOOK = "CREATE TABLE book(" +
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
            "status INTEGER);";
    //删除book表
    private static final String DELETE_BOOK = "DROP TABLE IF EXISTS book;";

    //创建chapter表
    //_id  int 自增章节id
    //id int 章节索引
    //book_id int 隶属书在服务器的唯一id，未录入时等于书的_id
    //name string  章节名
    //type int 类型 0-未读　１－在读 2- 已读
    //start_time　long 创建时间
    //end_time　long 最后编辑时间
    //status　 int 状态　０－同步正常　１－添加　２－修改　３－删除未同步

    private static final String CREATE_CHAPTER = "CREATE TABLE chapter("+
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id INTEGER, " +
            "book_id INTEGER, "+
            "name varchar(50), " +
            "type INTEGER, " +
            "start_time long, " +
            "end_time long, " +
            "status INTEGER);";
    private static final String DELETE_CHAPTER = "DROP TABLE IF EXISTS chapter;";
    public DBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Toast.makeText(myContext, "create database "+DB_NAME, Toast.LENGTH_SHORT).show();
        db.execSQL(CREATE_BOOK);
        db.execSQL(CREATE_CHAPTER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Toast.makeText(myContext, "create database "+DB_NAME, Toast.LENGTH_SHORT).show();
        db.execSQL(DELETE_BOOK);
        db.execSQL(CREATE_BOOK);
        db.execSQL(DELETE_CHAPTER);
        db.execSQL(CREATE_CHAPTER);
    }
}
