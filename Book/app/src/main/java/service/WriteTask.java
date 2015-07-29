package service;

import android.os.AsyncTask;

import com.hustunique.myapplication.MyApplication;

import java.util.List;

import data.Book;
import data.ChapterInfo;

/**
 * Created by taozhiheng on 15-7-26.
 */
public class WriteTask extends AsyncTask<Void, Integer, Void>
{

    private Book mBook;
    private List<ChapterInfo> mChapters;

    public WriteTask(Book book, List<ChapterInfo> list)
    {
        this.mBook = book;
        this.mChapters = list;
    }

    @Override
    protected Void doInBackground(Void... params) {
        MyApplication.getDBOperateInstance().writeBook(mBook, mChapters);
        return null;
    }
}
