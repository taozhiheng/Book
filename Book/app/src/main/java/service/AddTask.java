package service;

import android.os.AsyncTask;
import android.os.Handler;

import com.hustunique.myapplication.MyApplication;
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

    private Book mBook;
    private List<ChapterInfo> mChapters;
    private android.os.Handler mHandler;

    public AddTask(Book book, List<ChapterInfo> list, Handler handler)
    {
        this.mBook = book;
        this.mChapters = list;
        this.mHandler = handler;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mHandler.sendEmptyMessage(0);
    }

    @Override
    protected Void doInBackground(String... params) {
        DBOperate dbOperate = MyApplication.getDBOperateInstance();
        if(mBook.getUUID() != null)
        {
            dbOperate.setBookStatus(mBook.getUUID(), Constant.STATUS_ADD);
        }
        else
        {
            String uuid = dbOperate.insertBook(mBook, mChapters);
            mBook.setUUID(uuid);
        }
        switch (mBook.getType())
        {
            case Constant.TYPE_AFTER:
                dbOperate.setBookAfter(mBook.getUUID(), params[0]);
                break;
            case Constant.TYPE_NOW:
                dbOperate.setBookNow(mBook.getUUID(), params[0], params[1]);
                break;
            case Constant.TYPE_BEFORE:
                dbOperate.setBookBefore(mBook.getUUID(), params[0]);
                break;
        }
        return null;
    }
}
