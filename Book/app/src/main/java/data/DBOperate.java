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
import web.Web;

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
     * 创建一条新的书籍记录
     * isbn,name,author,press,url,color,word_num,type,start_time,end_time,create_time,status,typeS
     * */
    private static ContentValues createNewBookValues(Book book)
    {
        ContentValues contentValues = new ContentValues();

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
        contentValues.put("type_status", 0);
        return contentValues;
    }

    /**
     * 增加一本书,uuid为空,status=1,typeS=0
     * return id
     * */
    public long insertBook(Book book, List<ChapterInfo> chapters)
    {
        Log.d("net", "insert book:" + book.getType());
        open();
        long id = mDatabase.insert(Constant.TABLE_BOOK, null, createNewBookValues(book));
        int i = 1;
        for(ChapterInfo chapterInfo :chapters)
        {
            if(chapterInfo.getName().trim().compareTo("")==0)
                continue;
            insertChapter(id, i, null, chapterInfo.getName());
            i++;
        }
        return id;
    }

    /**
     * id,彻底删除某本书，同时删除所有章节
     * */
    public int deleteBook(long id)
    {
        open();
        mDatabase.delete(Constant.TABLE_CHAPTER, "local_book_id=" + id, null);
        return mDatabase.delete(Constant.TABLE_BOOK, "_id="+id, null);
    }

    /**
     * uuid,彻底删除某本书，同时删除所有章节
     * */
    public int deleteBook(String uuid)
    {
        open();
        mDatabase.delete(Constant.TABLE_CHAPTER, "book_id='" + uuid + "'", null);
        return mDatabase.delete(Constant.TABLE_BOOK, "uuid='"+uuid+"'", null);
    }

    /**
     * id,标记某本书为删除状态，同时标记所有章节为删除状态
     * */
    public void setBookDelete(long id)
    {
        open();
        mDatabase.execSQL("update " + Constant.TABLE_BOOK +
                " set status=3 where _id=" + id);
        mDatabase.execSQL("update " + Constant.TABLE_CHAPTER +
                " set status=3 where local_book_id=" + id);
    }

    /**
     * id,获得一本书章节总数,status<3
     * */
    public int getBookChapterNum(long id)
    {
        open();
        return mDatabase.query(Constant.TABLE_CHAPTER, null, "local_book_id="+id+" and status<3",
                null, null, null, null).getCount();
    }

    /**
     * id,获得一本书已完成的章节数,status<3
     * */
    public int getBookFinishNum(long id)
    {
        open();
        return mDatabase.query(Constant.TABLE_CHAPTER, null, "local_book_id="+id+" and type=2 and status<3",
                null, null, null, null).getCount();
    }

    /**
     * id,获得一本书正在读的章节数,status<3
     * */
    public int getBookReadingNum(long id)
    {
        open();
        return mDatabase.query(Constant.TABLE_CHAPTER, null, "local_book_id="+id+" and type=1 or type=3 and status<3",
                null, null, null, null).getCount();
    }

    /**
     * id获得一本书的信息
     * uuid,isbn,naem,author,press,url,color,word_num,type,start_time,end_time
     * id, typeS
     * */
    public Book getBookInfo(long id)
    {
        String[] query = new String[]{"uuid", "isbn", "name", "author",
                "press", "url", "color", "word_num",
                "type", "start_time", "end_time", "status", "create_time","_id", "type_status"};
        Cursor cursor = mDatabase.query(Constant.TABLE_BOOK,
                query, "_id="+id+" and status<3", null, null, null, null, "0,1");
        if(!cursor.moveToNext())
            return null;
        Book book = new Book(cursor.getLong(13), cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getInt(6), -1, -1,
                cursor.getLong(7), cursor.getInt(8), cursor.getString(12), cursor.getInt(11), cursor.getInt(14),
                cursor.getString(9), cursor.getString(10));
        cursor.close();
        return book;
    }

    /**
     * 获得某种类型的所有书的信息
     * uuid,isbn,name,author,
     * press,url,color,word_num,
     * type,start_time,end_time,status
     * id,typeS
     * */
    public List<Book> getBooks(int type)
    {
        Log.d("web", "get type book:" + type);
        open();
        List<Book> bookList = new ArrayList<>();
        String[] query = new String[]{"uuid", "isbn", "name", "author",
                "press", "url", "color", "word_num",
                "type", "start_time", "end_time", "status", "_id", "type_status"};
        String where = "type="+type + " and status<3";
        if(type == -1)
            where = "status<3";
        Cursor cursor = mDatabase.query(Constant.TABLE_BOOK,
                query, where, null, null, null, null);
        while(cursor.moveToNext())
        {
            long id = cursor.getLong(12);
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

            int finishNum = getBookFinishNum(id);
            int chapterNum = getBookChapterNum(id);
            Log.d("web", "book:"+name+" type:"+bookType+" status:"+status+" types="+cursor.getInt(13)+" uuid:"+uuid);
            bookList.add(new Book(id, uuid, isbn, name, author, press,
                    url, color, finishNum, chapterNum,
                    wordNum, bookType, null, status, cursor.getInt(13),
                    startTime, endTime));
        }
        cursor.close();
        return bookList;
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
     * id更新一本书的信息status<3
     * name,author,press,url,color,word_num,type,start_time,end_time
     * */
    public void updateBook(Book book)
    {
        open();
        mDatabase.update(Constant.TABLE_BOOK, createBookValues(book), "_id=" + book.getId() + " and status<3", null);
    }

    /**
     * id修改书的类型,0-未读，１－在读，２－已读，同时标记typeS=type,章节的typeS=ok
     * */
    public void setBookAfter(long id, String startTime)
    {
        open();
        mDatabase.execSQL("update " + Constant.TABLE_BOOK +
                " set type=" + Constant.TYPE_AFTER + " , type_status=" + Constant.T_STATUS_AFTER + " , start_time='" + startTime +
                "' where _id=" + id);
        mDatabase.execSQL("update " + Constant.TABLE_CHAPTER + " set type=0 , type_status=" + Constant.T_STATUS_OK + " where local_book_id=" + id);
    }

    public void setBookNow(long id, String startTime, String endTime)
    {
        open();
        mDatabase.execSQL("update " + Constant.TABLE_BOOK +
                " set type=" + Constant.TYPE_NOW + " , type_status=" + Constant.T_STATUS_NOW + " , start_time='" + startTime + "' , end_time='" + endTime +
                "' where _id=" + id);
        mDatabase.execSQL("update "+Constant.TABLE_CHAPTER+" set type=0 , type_status="+Constant.T_STATUS_OK+" where local_book_id=" + id);
        Log.d("net", "set book now");
    }

    public void setBookBefore(long id, String endTime)
    {
        open();
        mDatabase.execSQL("update " + Constant.TABLE_BOOK +
                " set type=" + Constant.TYPE_BEFORE + " , type_status=" + Constant.T_STATUS_BEFORE + " , end_time='" + endTime +
                "' where _id=" + id);
        mDatabase.execSQL("update " + Constant.TABLE_CHAPTER + " set type=2 , type_status=" + Constant.T_STATUS_OK + " where local_book_id=" + id);
    }

    /**
     * id修改一本书的状态
     * status 0-ok,1-add,2-mod,3-del
     * 在上传到服务器，对书进行修改时使用
     * */
    public void setBookStatus(long id, int status)
    {
        open();
        mDatabase.execSQL("update "+Constant.TABLE_BOOK+
                " set status="+status+
                " where _id="+id);
    }

    public void setBookTypeOk(long id)
    {
        open();
        mDatabase.execSQL("update "+Constant.TABLE_BOOK+
                " set type_status="+Constant.T_STATUS_OK+
                " where _id="+id);
    }


    /**
     * id检查书是否已读完，若读完，则改变书的类型
     * */
    public void checkBookFinish(long id)
    {
        int finishNum = getBookFinishNum(id);
        int chapterNum = getBookChapterNum(id);
        if(finishNum == chapterNum)
            setBookBefore(id, TimeUtil.getNeedTime(System.currentTimeMillis()));
    }

    /**
     * id获得书的最大章节索引
     * */
    public int getBookMaxChapterIndex(long id)
    {
        open();
        Cursor cursor = mDatabase.query(Constant.TABLE_CHAPTER, new String[]{"id"}, "local_book_id=" + id + " and status<3", null, null, null, "id desc", "0,1");
        if(!cursor.moveToNext())
            return -1;
        int index = cursor.getInt(0);
        cursor.close();
        return index;
    }


    /**
     * 创建一条新的章节记录
     * id,local_book_id,name,type,status
     * */
    private static ContentValues createNewChapterValues(long bookId, int id, String uuid,  String name)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("book_id", uuid);
        contentValues.put("local_book_id", bookId);
        contentValues.put("name", name);
        contentValues.put("type", 0);
        contentValues.put("status", 1);
        contentValues.put("type_status", 0);
        return contentValues;
    }
    /**
     * 在新增加的书中增加一个章节，
     * book_id为空，id为最大序号
     * status=1, typeS = 0
     * */
    public void insertChapter(long bookId, int id, String uuid, String name)
    {
        open();
        mDatabase.insert(Constant.TABLE_CHAPTER, null, createNewChapterValues(bookId, id, uuid, name));
    }

    /**
     * chapterId彻底删除一个章节
     * */
    public void deleteChapter(long chapterId)
    {
        open();
        mDatabase.delete(Constant.TABLE_CHAPTER, "_id=" + chapterId, null);

    }


    /**
     * chapterId将一个章节标记为删除状态
     * */
    public void setChapterDelete(long chapterId)
    {
        open();
        mDatabase.execSQL("update " + Constant.TABLE_CHAPTER +
                " set status=3 where _id=" + chapterId);
    }

    /**
     * 获得所有在读章节
     * id,book_id,name,type,status,book_name,url
     * _id, local_book_id,typeS
     * */
    public List<Chapter> getNowChapters()
    {
        open();
        List<Chapter> chapterList = new ArrayList<>();
        Cursor cursor = mDatabase.query(Constant.TABLE_CHAPTER,
                new String[]{"id", "book_id", "name", "type","status", "_id", "local_book_id", "type_status"},
                "type=1 or type=3"+" and status<3" , null, null, null, null);
        while (cursor.moveToNext())
        {
            long bookId = cursor.getLong(6);
            int id = cursor.getInt(0);
            String webBookId = cursor.getString(1);
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
            chapterList.add(new Chapter(cursor.getLong(5), bookId,id, webBookId, name, type, null,status,cursor.getInt(7),
                    bookName, url));
        }
        cursor.close();
        return chapterList;
    }

    /**
     * id获得uuid
     * */
    public String getBookUUID(int id)
    {
        open();
        Cursor cursor = mDatabase.query(Constant.TABLE_BOOK, new String[]{"uuid"}, "_id="+id, null, null, null, null, "0,1");
        if(cursor.moveToNext())
            return cursor.getString(0);
        return null;
    }

    /**
     * id获取某本书未修改章节数目
     * */
    public int getOKChapterNum(long id)
    {
        open();
        Cursor cursor = mDatabase.query(Constant.TABLE_CHAPTER, new String[]{"type"}, "local_book_id="+id+" and status=0", null, null, null, null);
        if(cursor.moveToNext())
            return cursor.getCount();
        return 0;
    }

    /**
     * id获取某本书的所有章节status<3
     * id,book_id,name,type,status
     * 新增排序
     * */
    public List<Chapter> getChapters(long id)
    {
        Log.d("web", "get book chapters " + id);
        open();
        List<Chapter> chapterList = new ArrayList<>();
        Cursor cursor = mDatabase.query(Constant.TABLE_CHAPTER,
                new String[]{"id", "book_id", "name", "type","status", "_id", "local_book_id", "type_status"},
                "local_book_id="+id+" and status<3" , null, null, null, null);
        List<Chapter> finishList = new ArrayList<>();
        while (cursor.moveToNext())
        {
            int webId = cursor.getInt(0);
            String webBookId = cursor.getString(1);
            String name = cursor.getString(2);
            int type = cursor.getInt(3);
            int status = cursor.getInt(4);

            Log.d("web", "book chapter:" + name + " type:" + type + " status:" + status + " " + webBookId + "/" + id);
            Chapter chapter = new Chapter(cursor.getLong(5), cursor.getLong(6), webId, webBookId, name, type, null,status, cursor.getInt(7));
            if(type == Constant.TYPE_BEFORE)
                finishList.add(chapter);
            else
                chapterList.add(chapter);
        }
        Log.d("web", "book chapter name:" + chapterList.size() + ", " + finishList.size());
        chapterList.addAll(finishList);
        cursor.close();
        return chapterList;
    }

    /**
     * chapterId修改ok,modify书的章节的类型,0-未读，１－在读，２－已读，3－重复，同时标记其为增加或修改状态
     * 检查书是否已完成
     * */
    public void setChapterType(long chapterId, int type)
    {
        Log.d("web", "set chapter type, id:"+chapterId+" type:"+type);
        open();
        mDatabase.execSQL("update " + Constant.TABLE_CHAPTER +
                " set type=" + type + ", type_status=" + type +
                " where _id=" + chapterId + " and status<3");
    }

    /**
     * chapterId更新一个章节的信息status<3
     * name
     * */
    public void updateChapter(long chapterId, String name)
    {
        open();
        mDatabase.execSQL("update "+Constant.TABLE_CHAPTER+
                " set name='"+name+
                "' where _id="+chapterId+" and status<3");
    }

    /**
     * chapterId修改一个章节的状态
     * status 0-ok,1-add,2-mod,3-del
     * 在上传到服务器，对书进行修改时使用
     * */
    public void setChapterStatus(long chapterId, int status)
    {
        Log.d("web", "set chapter status:"+chapterId+"/"+status);
        open();
        mDatabase.execSQL("update "+Constant.TABLE_CHAPTER+
                " set status="+status+
                " where _id="+chapterId);
    }

    /**
     * id修改一本书章节的状态
     * status 0-ok,1-add,2-mod,3-del
     * 在上传到服务器，对书进行修改时使用
     * */
    public void setChaptersStatus(long id, int status)
    {
        open();
        mDatabase.execSQL("update " + Constant.TABLE_CHAPTER +
                " set status=" + status +
                " where local_book_id="+id);
    }


    public void setChapterTypeOK(long id)
    {
        open();
        mDatabase.execSQL("update " + Constant.TABLE_CHAPTER +
                " set type_status=" + Constant.T_STATUS_OK +
                " where _id=" + id);
    }



    /**
     * 写入一本书及所有章节
     * */
    public void writeBook(Book book, List<ChapterInfo> chapterInfos) {
        Log.d("net", "write a book from web to local");
        open();
        long bookId = mDatabase.insert(Constant.TABLE_BOOK, null, getBookValues(book));
        String uuid = book.getUUID();
        for(ChapterInfo chapterInfo : chapterInfos)
        {
            int type = book.getType();
            if(type == Constant.TYPE_NOW && chapterInfo.getType() != -1)
                type = chapterInfo.getType();
            mDatabase.insert(Constant.TABLE_CHAPTER, null,
                    getChapterValues(uuid, chapterInfo.getPosition(), bookId, chapterInfo.getName(), type));
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
    public ContentValues getChapterValues(String webBookId, int webId, long bookId, String name, int type)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", webId);
        contentValues.put("book_id", webBookId);
        contentValues.put("local_book_id", bookId);
        contentValues.put("name", name);
        contentValues.put("type", type);
        contentValues.put("status", Constant.STATUS_OK);
        return contentValues;
    }

    /**
     * 修改一本书的uuid及所有章节的book_id
     * */
    public void setBookUUID(long id, String UUID)
    {
        Log.d("web", "set book insert ok:_id=" + id);
        open();
        mDatabase.execSQL("update " + Constant.TABLE_BOOK +
                " set uuid='" + UUID + "' ,status=" + Constant.STATUS_OK +
                " where _id=" + id);
        mDatabase.execSQL("update " + Constant.TABLE_CHAPTER +
                " set book_id='" + UUID +
                "' where local_book_id=" + id);
    }

    /**
     * chapterId修改一个章节的id,标记为status_ok
     * */
    public void setChapterID(long id, int ID)
    {
        Log.d("web", " reset chapter id, from " + id + " to " + ID);
        open();
        mDatabase.execSQL("update "+Constant.TABLE_CHAPTER+
                " set id="+ID+" , status="+Constant.STATUS_OK+
                " where _id="+id);
    }
    /**
     * chapterId修改一个章节的id,标记为status_ok,types = ok
     * */
    public void setChapterOK(long id, int ID)
    {
        Log.d("web", " reset chapter id, from " + id + " to " + ID);
        open();
        mDatabase.execSQL("update "+Constant.TABLE_CHAPTER+
                " set id="+ID+" , status="+Constant.STATUS_OK+" , type_status="+Constant.T_STATUS_OK+
                " where _id="+id);
    }

    /**
     * 获得某个标记状态的所有书籍
     * */
    public List<Book> getStatusBooks(int status)
    {
        Log.d("web", " ChapterInfo chapterInfo = chapters.get(i);get status book:"+status);
        open();
        List<Book> bookList = new ArrayList<>();
        String[] query = new String[]{"uuid", "isbn", "name", "author",
                "press", "url", "color", "word_num",
                "type", "start_time", "end_time", "status", "_id", "type_status"};
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

            Log.d("web", "status book:_id="+cursor.getLong(12)+" "+name+" status:"+bookStatus+" type:"+type);
            bookList.add(new Book(cursor.getLong(12), uuid, isbn, name, author, press,
                    url, color, 0, 0,
                    wordNum, type, null, bookStatus, cursor.getInt(13),
                    startTime, endTime));
        }
        cursor.close();
        return bookList;
    }

    public List<Chapter> getStatusChapters(int status)
    {
        open();
        Log.d("web", "get status chapters:" + status);
        List<Chapter> chapterList = new ArrayList<>();
        Cursor cursor = mDatabase.query(Constant.TABLE_CHAPTER,
                new String[]{"id", "book_id", "name", "type","status", "_id", "local_book_id", "type_status"},
                "status="+status, null, null, null, null);
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String bookId = cursor.getString(1);
            String name = cursor.getString(2);
            int type = cursor.getInt(3);
            int chapterStatus = cursor.getInt(4);
            Log.d("web", "status chapter:"+name+" type:"+type+" status:"+chapterStatus+"book uuid:"+bookId);

            chapterList.add(new Chapter(cursor.getLong(5), cursor.getLong(6), id, bookId, name, type, null, chapterStatus, cursor.getInt(7)));
        }
        cursor.close();
        return chapterList;
    }

    public List<WebBook> getWebBooks()
    {
        Log.d("web","get web books");
        open();
        List<WebBook> books = new ArrayList<>();
        Cursor cursor = mDatabase.query(Constant.TABLE_BOOK,
                new String[]{ "_id", "uuid", "type", "start_time", "end_time"},
                "type_status>-1", null, null, null, null);
        while (cursor.moveToNext())
        {
            books.add(new WebBook(cursor.getLong(0), cursor.getString(1), cursor.getInt(2),
                    cursor.getString(3), cursor.getString(4)));
        }
        return books;
    }

    public List<WebChapter> getWebChapters()
    {
        open();
        List<WebChapter> chapters = new ArrayList<>();
        Cursor cursor = mDatabase.query(Constant.TABLE_CHAPTER,
                new String[]{ "_id", "book_id", "id", "type"},
                "type_status>-1", null, null, null, null);
        while (cursor.moveToNext())
        {
            chapters.add(new WebChapter(cursor.getLong(0), cursor.getString(1), cursor.getInt(2),
                    cursor.getInt(3)));
        }
        return chapters;
    }


    public void initAll()
    {
        open();
        mDatabase.execSQL(DBHelper.DELETE_BOOK);
        mDatabase.execSQL(DBHelper.DELETE_CHAPTER);
        mDatabase.execSQL(DBHelper.CREATE_BOOK);
        mDatabase.execSQL(DBHelper.CREATE_CHAPTER);
    }

    /**
     * 删除所有待删除的书和章节
     * */
    public void deleteAll()
    {
        open();
        mDatabase.delete(Constant.TABLE_CHAPTER, "status=" + Constant.STATUS_DEL, null);
        mDatabase.delete(Constant.TABLE_BOOK, "status=" + Constant.STATUS_DEL, null);
    }

    /**
     * 将所有书和章节标记为新增状态
     * */
    public void resetAll()
    {
        open();
        mDatabase.execSQL("update "+Constant.TABLE_CHAPTER+
                " set status=" + Constant.STATUS_ADD+" , type_status=0 , book_id='', id=-1");
        mDatabase.execSQL("update "+Constant.TABLE_BOOK+
                " set status="+Constant.STATUS_ADD+" , type_status=0 , uuid=''");
    }
}
