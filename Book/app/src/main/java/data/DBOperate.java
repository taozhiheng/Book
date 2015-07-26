package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import util.Constant;
import util.TimeUtil;

/**
 * Created by taozhiheng on 15-3-27.
 *
 */
public class DBOperate {

    private static DBOperate instance;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDatabase;

    public static DBOperate getInstance(Context context)
    {
        if(instance == null)
            instance = new DBOperate(context);
        return instance;
    }

    private DBOperate(Context context)
    {
        mDBHelper = new DBHelper(context);
        mDatabase = mDBHelper.getWritableDatabase();
    }

    public void open()
    {
        if(!mDatabase.isOpen())
            mDatabase = mDBHelper.getWritableDatabase();
    }

    public void close()
    {
        if(mDatabase.isOpen())
            mDatabase.close();
    }

    public int getBookNum()
    {
        open();
        Cursor cursor = mDatabase.query(Constant.TABLE_BOOK, new String[]{"word_num"},  "status<3", null, null, null, null);
        return cursor.getCount();
    }

    /**
     * 取得用户阅读信息
     * */
    public UserReadInfo getUserReadInfo()
    {
        open();
        UserReadInfo userReadInfo = new UserReadInfo();
        Cursor cursor = mDatabase.query(Constant.TABLE_BOOK, new String[]{"word_num"}, "type=2"+ " and status<3", null, null, null, null);
        userReadInfo.setBookNum(cursor.getCount());
        long words = 0;
        while (cursor.moveToNext())
        {
            words += cursor.getLong(0);
        }
        userReadInfo.setWordNum(words);
        cursor.close();
        cursor = mDatabase.query(Constant.TABLE_CHAPTER, null, "type=2", null, null, null, null);
        userReadInfo.setChapterNum(cursor.getCount());
        cursor.close();
        return userReadInfo;
    }

    /**
     * 增加一本书,将其标记为增加状态,并暂时设置其uuid为_id
     * */
    public String insertBook(Book book, List<ChapterInfo> chapters)
    {
        Log.d("net", "insert book:"+book.getType());
        open();
        long id = mDatabase.insert(Constant.TABLE_BOOK, null, createNewBookValues(book));
        String uuid = ""+id;
        mDatabase.execSQL("update "+Constant.TABLE_BOOK+" set uuid='"+uuid+"' where _id="+id);
        for(int i = 0; i < chapters.size(); i++)
        {
            ChapterInfo chapterInfo = chapters.get(i);
            insertChapter(uuid, i, chapterInfo.getName());
        }
        return uuid;
    }

    /**
     * 彻底删除某本书，同时删除所有章节
     * */
    public int deleteBook(String uuid)
    {
        open();
        mDatabase.delete(Constant.TABLE_CHAPTER, "book_id='"+uuid+"'", null);
        return mDatabase.delete(Constant.TABLE_BOOK, "uuid='"+uuid+"'", null);
    }

    /**
     * 标记某本书为删除状态，同时标记所有章节为删除状态
     * */
    public void setBookDelete(String uuid)
    {
        open();
        mDatabase.execSQL("update "+Constant.TABLE_BOOK+
                " set status=3 where uuid='"+uuid+"'");
        mDatabase.execSQL("update "+Constant.TABLE_CHAPTER+
                " set status=3 where book_id='"+uuid+"'");
    }

    /**
     * 获得一本书章节总数
     * */
    public int getBookChapterNum(String uuid)
    {
        open();
        return mDatabase.query(Constant.TABLE_CHAPTER, null, "book_id='"+uuid+"'"+" and status<3",
                null, null, null, null).getCount();
    }

    /**
     * 获得一本书已完成的章节数
     * */
    public int getBookFinishNum(String uuid)
    {
        open();
        return mDatabase.query(Constant.TABLE_CHAPTER, null, "book_id='"+uuid+"' and type=2"+" and status<3",
                null, null, null, null).getCount();
    }

    /**
     * 获得一本书的信息
     * uuid,isbn,naem,author,press,url,color,word_num,type,start_time,end_time
     * */
    public Book getBookInfo(String uuid)
    {
        String[] query = new String[]{"uuid", "isbn", "name", "author",
                "press", "url", "color", "word_num",
                "type", "start_time", "end_time", "status", "create_time"};
        Cursor cursor = mDatabase.query(Constant.TABLE_BOOK,
                query, "uuid='"+uuid+"'"+" and status<3", null, null, null, null, "0,1");
        if(!cursor.moveToNext())
            return null;
        Book book = new Book(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getInt(6), -1, -1,
                cursor.getLong(7), cursor.getInt(8), cursor.getString(12), cursor.getInt(11),
                cursor.getString(9), cursor.getString(10));
        cursor.close();
        return book;
    }

    /**
     * 获得某种类型的所有书的信息
     * uuid,isbn,name,author,
     * press,url,color,word_num,
     * type,start_time,end_time,status
     * */
    public List<Book> getBooks(int type)
    {
        Log.d("web", "get type book:"+type);
        open();
        List<Book> bookList = new ArrayList<>();
        String[] query = new String[]{"uuid", "isbn", "name", "author",
                "press", "url", "color", "word_num",
                "type", "start_time", "end_time", "status"};
        String where = "type="+type + " and status<3";
        if(type == -1)
            where = "status<3";
        Cursor cursor = mDatabase.query(Constant.TABLE_BOOK,
                query, where, null, null, null, null);
        while(cursor.moveToNext())
        {
            String uuid = cursor.getString(0);
            String isbn = cursor.getString(1);
            String name = cursor.getString(2);
            String author = cursor.getString(3);
            String press = cursor.getString(4);
            String url = cursor.getString(5);
            int color = cursor.getInt(6);

            long wordNum = cursor.getLong(7);
            int bookType = cursor.getInt(8);
            String startTime = cursor.getString(9);
            String endTime = cursor.getString(10);
            int status = cursor.getInt(11);

            int finishNum = getBookFinishNum(uuid);
            int chapterNum = getBookChapterNum(uuid);
            Log.d("web", "book:"+name+" type:"+bookType+" status:"+status+" uuid:"+uuid);
            bookList.add(new Book(uuid, isbn, name, author, press,
                    url, color, finishNum, chapterNum,
                    wordNum, bookType, null, status,
                    startTime, endTime));
        }
        cursor.close();
        return bookList;
    }

    /**
     * 更新一本书的信息
     * name,author,press,url,color,word_num,type,start_time,end_time
     * */
    public void updateBook(Book book)
    {
        open();
        mDatabase.update(Constant.TABLE_BOOK, createBookValues(book), "uuid='"+book.getUUID()+"'"+" and status<3", null);
    }

    /**
     * 修改书的类型,0-未读，１－在读，２－已读，同时标记其为增加或修改状态
     * */
    public void setBookAfter(String uuid, String startTime)
    {
        open();
        mDatabase.execSQL("update "+Constant.TABLE_BOOK+
                " set type="+Constant.TYPE_AFTER+" , start_time='"+startTime+
                "' where uuid='"+uuid+"'");
        mDatabase.execSQL("update "+Constant.TABLE_CHAPTER+" set type=0 where book_id='"+uuid+"'");
    }

    public void setBookNow(String uuid, String startTime, String endTime)
    {
        open();
        mDatabase.execSQL("update "+Constant.TABLE_BOOK+
                " set type="+Constant.TYPE_NOW+" , start_time='"+startTime+"' , end_time='"+endTime+
                "' where uuid='"+uuid+"'");
        mDatabase.execSQL("update "+Constant.TABLE_CHAPTER+" set type=0 where book_id='"+uuid+"'");
        Log.d("net", "set book now");
    }

    public void setBookBefore(String uuid, String endTime)
    {
        open();
        mDatabase.execSQL("update "+Constant.TABLE_BOOK+
                " set type="+Constant.TYPE_BEFORE+" , end_time='"+endTime+
                "' where uuid='"+uuid+"'");
        mDatabase.execSQL("update "+Constant.TABLE_CHAPTER+" set type=2 where book_id='"+uuid+"'");
    }

    /**
     * 修改一本书的状态
     * status 0-ok,1-add,2-mod,3-del
     * 在上传到服务器，对书进行修改时使用
     * */
    public void setBookStatus(String uuid, int status)
    {
        open();
        mDatabase.execSQL("update "+Constant.TABLE_BOOK+
                " set status="+status+
                " where uuid='"+uuid+"'");
    }

    /**
     * 检查书是否已读完，若读完，则改变书的类型
     * */
    public void checkBookFinish(String uuid)
    {
        int finishNum = getBookFinishNum(uuid);
        int chapterNum = getBookChapterNum(uuid);
        if(finishNum == chapterNum)
            setBookBefore(uuid, TimeUtil.getNeedTime(System.currentTimeMillis()));
    }

    /**
     * 获得书的最大章节索引
     * */
    public int getBookMaxChapterIndex(String uuid)
    {
        open();
        Cursor cursor = mDatabase.query(Constant.TABLE_CHAPTER, new String[]{"id"}, "book_id='"+uuid+"'"+" and status<3", null, null, null, "id desc", "0,1");
        if(!cursor.moveToNext())
            return -1;
        int index = cursor.getInt(0);
        cursor.close();
        return index;
    }

    /**
     * 增加一个章节，并将其标记为增加状态
     * */
    public void insertChapter(String bookId, int id, String name)
    {
        open();
        mDatabase.insert(Constant.TABLE_CHAPTER, null,  createNewChapterValues(bookId, id, name));
    }

    /**
     * 彻底删除一个章节
     * */
    public void deleteChapter(String bookId, int id)
    {
        open();
        mDatabase.delete(Constant.TABLE_CHAPTER, "book_id='"+bookId+"' and id="+id, null);

    }

    /**
     * 删除一本书的全部章节
     * */
    public void deleteChapters(String bookId)
    {
        open();
        mDatabase.delete(Constant.TABLE_CHAPTER, "book_id='"+bookId+"'", null);

    }

    /**
     * 将一个章节标记为删除状态
     * */
    public void setChapterDelete(String bookId, int id)
    {
        open();
        mDatabase.execSQL("update "+Constant.TABLE_CHAPTER+
                " set status=3 where book_id='"+bookId+"' and id="+id);
    }

    /**
     * 获得所有在读章节
     * id,book_id,name,type,status,book_name,url
     * */
    public List<Chapter> getNowChapters()
    {
        open();
        List<Chapter> chapterList = new ArrayList<>();
        Cursor cursor = mDatabase.query(Constant.TABLE_CHAPTER,
                new String[]{"id", "book_id", "name", "type","status"},
                "type=1"+" and status<3" , null, null, null, null);
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String bookId = cursor.getString(1);
            String name = cursor.getString(2);
            int type = cursor.getInt(3);
            int status = cursor.getInt(4);
            Book book = getBookInfo(bookId);
            String bookName = null;
            String url = null;
            if(book != null)
            {
                bookName = book.getName();
                url = book.getUrl();
            }
            Log.d("web", "now chapter:"+name+" status:"+status);
            chapterList.add(new Chapter(id, bookId, name, type, null,status,
                    bookName, url));
        }
        cursor.close();
        return chapterList;
    }

    /**
     * 获取某本书的所有章节
     * id,book_id,name,type,status
     * */
    public List<Chapter> getChapters(String bookId)
    {
        open();
        List<Chapter> chapterList = new ArrayList<>();
        Cursor cursor = mDatabase.query(Constant.TABLE_CHAPTER,
                new String[]{"id", "book_id", "name", "type","status"},
                "book_id='"+bookId+"' and status<3" , null, null, null, null);
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String bookID = cursor.getString(1);
            String name = cursor.getString(2);
            int type = cursor.getInt(3);
            int status = cursor.getInt(4);

            Log.d("web", "book chapter:"+name+" type:"+type+" status:"+status);

            chapterList.add(new Chapter(id, bookID, name, type, null,status));
        }
        cursor.close();
        return chapterList;
    }

    /**
     * 修改书的类型,0-未读，１－在读，２－已读，同时标记其为增加或修改状态
     * */
    public void setChapterType(String bookId, int id, int type)
    {
        Log.d("web", "set chapter type, id:"+id+" type:"+type);
        open();
        mDatabase.execSQL("update " + Constant.TABLE_CHAPTER +
                " set type=" + type +
                " where book_id='" + bookId + "' and id=" + id+" and status<3");
        if(type == Constant.TYPE_BEFORE)
            checkBookFinish(bookId);
    }

    /**
     * 更新一个章节的信息
     * name
     * */
    public void updateChapter(String bookId, int id, String name)
    {
        open();
        mDatabase.execSQL("update "+Constant.TABLE_CHAPTER+
                " set name='"+name+
                "' where book_id='"+bookId+"' and id="+id+" and status<3");
    }

    /**
     * 修改一个章节的状态
     * status 0-ok,1-add,2-mod,3-del
     * 在上传到服务器，对书进行修改时使用
     * */
    public void setChapterStatus(String bookId, int id, int status)
    {
        open();
        mDatabase.execSQL("update "+Constant.TABLE_CHAPTER+
                " set status="+status+
                " where book_id='"+bookId+"' and id="+id);
    }

    /**
     * 修改一本书章节的状态
     * status 0-ok,1-add,2-mod,3-del
     * 在上传到服务器，对书进行修改时使用
     * */
    public void setChaptersStatus(String bookId, int status)
    {
        open();
        mDatabase.execSQL("update "+Constant.TABLE_CHAPTER+
                " set status="+status+
                " where book_id='"+bookId+"'");
    }

    /**
     * 创建一条新的书籍记录
     * uuid,isbn,name,author,press,url,color,word_num,type,start_time,end_time,create_time,status
     * */
    private static ContentValues createNewBookValues(Book book)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("uuid", book.getUUID());

        contentValues.put("isbn", book.getIsbn());
        contentValues.put("name", book.getName());
        contentValues.put("author", book.getAuthor());
        contentValues.put("press", book.getPress());
        contentValues.put("url", book.getUrl());
        contentValues.put("color", book.getColor());
        contentValues.put("word_num", book.getWordNum());
        contentValues.put("type", 0);

        contentValues.put("start_time", book.getStartTime());
        contentValues.put("end_time", book.getEndTime());
        contentValues.put("create_time", book.getCreateTime());

        contentValues.put("status", 1);
        return contentValues;
    }

    /**
     * 创建一条待修改的书籍记录
     * name,author,press,url,color,word_num,start_time,end_time
     * */
    private static ContentValues createBookValues(Book book)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", book.getName());
        contentValues.put("author", book.getAuthor());
        contentValues.put("press", book.getPress());
        contentValues.put("url", book.getUrl());
        contentValues.put("color", book.getColor());
        contentValues.put("word_num", book.getWordNum());

        contentValues.put("start_time", book.getStartTime());
        contentValues.put("end_time", book.getEndTime());

        return contentValues;
    }

    /**
     * 创建一条新的章节记录
     * id,book_id,name,type,status
     * */
    private static ContentValues createNewChapterValues(String bookId, int id, String name)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("book_id", bookId);
        contentValues.put("name", name);
        contentValues.put("type", 0);
        contentValues.put("status", 1);
        return contentValues;
    }

    /**
     * 清空两张表
     * */
    public void clearTables()
    {
        open();
        mDatabase.delete(Constant.TABLE_BOOK, null, null);
        mDatabase.delete(Constant.TABLE_CHAPTER, null, null);
    }

    /**
     * 写入一本书及所有章节
     * */
    public void writeBook(Book book, List<ChapterInfo> chapterInfos)
    {
        Log.d("net", "write a book from web to local");
        open();
        mDatabase.insert(Constant.TABLE_BOOK, null, getBookValues(book));
        String uuid = book.getUUID();
        int type = Constant.TYPE_AFTER;
        if(book.getType() == Constant.TYPE_BEFORE)
            type = Constant.TYPE_BEFORE;
        for(ChapterInfo chapterInfo : chapterInfos)
        {
            mDatabase.insert(Constant.TABLE_CHAPTER, null,
                    getChapterValues(uuid, chapterInfo.getPosition(), chapterInfo.getName(), type));
        }
    }

    /**
     * 复制书的信息，返回一个ContentValues
     * */
    private static ContentValues getBookValues(Book book)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("uuid", book.getUUID());

        contentValues.put("isbn", book.getIsbn());
        contentValues.put("name", book.getName());
        contentValues.put("author", book.getAuthor());
        contentValues.put("press", book.getPress());
        contentValues.put("url", book.getUrl());
        contentValues.put("color", book.getColor());
        contentValues.put("word_num", book.getWordNum());
        contentValues.put("type", book.getType());

        contentValues.put("start_time", book.getStartTime());
        contentValues.put("end_time", book.getEndTime());
        contentValues.put("create_time", book.getCreateTime());

        contentValues.put("status", book.getStatus());
        return contentValues;
    }

    /**
     * 复制章节的信息，返回一个ContentValues
     * */
    public ContentValues getChapterValues(String bookId, int id, String name, int type)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("book_id", bookId);
        contentValues.put("name", name);
        contentValues.put("type", type);
        contentValues.put("status", Constant.STATUS_OK);
        return contentValues;
    }

    /**
     * 修改一本书的uuid及所有章节的book_id
     * */
    public void resetBookUUID(String uuid, String UUID)
    {
        open();
        mDatabase.execSQL("update "+Constant.TABLE_BOOK+
                " set uuid='"+UUID+
                "' where uuid='"+uuid+"'");
        mDatabase.execSQL("update "+Constant.TABLE_CHAPTER+
                " set book_id='"+UUID+
                "' where book_id='"+uuid+"'");
    }

    /**
     * 修改一个章节的id
     * */
    public void resetChapterID(String bookId, int id, int ID)
    {
        open();
        mDatabase.execSQL("update "+Constant.TABLE_CHAPTER+
                " set id='"+ID+
                "' where book_id='"+bookId+"' and id="+id);
    }

    /**
     * 获得某个标记状态的所有书籍
     * */
    public List<Book> getStatusBooks(int status)
    {
        Log.d("web", "get status book:"+status);
        open();
        List<Book> bookList = new ArrayList<>();
        String[] query = new String[]{"uuid", "isbn", "name", "author",
                "press", "url", "color", "word_num",
                "type", "start_time", "end_time", "status"};
        Cursor cursor = mDatabase.query(Constant.TABLE_BOOK,
                query, "status="+status, null, null, null, null);
        while(cursor.moveToNext())
        {
            String uuid = cursor.getString(0);
            String isbn = cursor.getString(1);
            String name = cursor.getString(2);
            String author = cursor.getString(3);
            String press = cursor.getString(4);
            String url = cursor.getString(5);
            int color = cursor.getInt(6);

            long wordNum = cursor.getLong(7);
            int type = cursor.getInt(8);
            String startTime = cursor.getString(9);
            String endTime = cursor.getString(10);
            int bookStatus = cursor.getInt(11);

            int finishNum = getBookFinishNum(uuid);
            int chapterNum = getBookChapterNum(uuid);
            Log.d("web", "status book:"+name+" status:"+bookStatus+" type:"+type);
            bookList.add(new Book(uuid, isbn, name, author, press,
                    url, color, finishNum, chapterNum,
                    wordNum, type, null, bookStatus,
                    startTime, endTime));
        }
        cursor.close();
        return bookList;
    }

    public List<Chapter> getStatusChapters(int status)
    {
        open();
        Log.d("web", "get status book:"+status);
        List<Chapter> chapterList = new ArrayList<>();
        Cursor cursor = mDatabase.query(Constant.TABLE_CHAPTER,
                new String[]{"id", "book_id", "name", "type","status"},
                "status="+status, null, null, null, null);
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String bookId = cursor.getString(1);
            String name = cursor.getString(2);
            int type = cursor.getInt(3);
            int chapterStatus = cursor.getInt(4);
            Log.d("web", "chapter:"+name+" status:"+chapterStatus);

            chapterList.add(new Chapter(id, bookId, name, type, null, chapterStatus));
        }
        cursor.close();
        return chapterList;
    }

    /**
     * 删除所有待删除的书和章节
     * */
    public void deleteAll()
    {
        open();
        mDatabase.delete(Constant.TABLE_BOOK, "status="+Constant.STATUS_DEL, null);
        mDatabase.delete(Constant.TABLE_CHAPTER, "status="+Constant.STATUS_DEL, null);
    }
}
