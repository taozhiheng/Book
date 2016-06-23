package service;

import android.os.AsyncTask;
import android.os.Handler;

import com.hustunique.myapplication.MyApplication;

import java.lang.ref.WeakReference;
import java.util.List;

import data.Book;
import data.ChapterInfo;
import data.DBOperate;
import util.Constant;

/**
 * Created by taozhiheng on 15-7-25.
 * Add a book and chapters to local database
 */
public class AddTask extends AsyncTask<String, Integer, Void> {

    private WeakReference<Book> mBookRef;
    private WeakReference<List<ChapterInfo>> mChaptersRef;
    private WeakReference<android.os.Handler> mHandlerRef;

    public AddTask(Book book, List<ChapterInfo> list, Handler handler)
    {
        this.mBookRef = new WeakReference<Book>(book);
        this.mChaptersRef = new WeakReference<List<ChapterInfo>>(list);
        this.mHandlerRef = new WeakReference<Handler>(handler);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Handler handler = mHandlerRef.get();
        if(handler != null)
            handler.sendEmptyMessage(0);
    }

    @Override
    protected Void doInBackground(String... params) {
        DBOperate dbOperate = MyApplication.getDBOperateInstance();
        //已存在，但是被取消过，标记为删除状态
        Book book = mBookRef.get();
        if(book == null)
            return null;
        if(book.getId() != -1)
        {
            dbOperate.setBookStatus(book.getId(), Constant.STATUS_ADD);
        }
        else
        {
            List<ChapterInfo> list = mChaptersRef.get();
            if(list != null) {
                long id = dbOperate.insertBook(book, list);
                book.setId(id);
            }
        }
        switch (book.getType())
        {
            case Constant.TYPE_AFTER:
                dbOperate.setBookAfter(book.getId(), params[0]);
                break;
            case Constant.TYPE_NOW:
                dbOperate.setBookNow(book.getId(), params[0], params[1]);
                break;
            case Constant.TYPE_BEFORE:
                dbOperate.setBookBefore(book.getId(), params[0]);
                break;
        }
        return null;
    }
}
