package service;

import android.os.AsyncTask;
import android.os.Handler;

import com.hustunique.myapplication.MyApplication;

import java.lang.ref.WeakReference;
import java.util.List;

import data.Book;

/**
 * Created by taozhiheng on 15-8-7.
 */
public class QueryBooksTask extends AsyncTask<Integer, Integer, Void> {

    private WeakReference<List<Book>> mBookListRef;
    private WeakReference<android.os.Handler> mHandlerRef;

    public QueryBooksTask(List<Book> books, android.os.Handler handler)
    {
        this.mBookListRef = new WeakReference<List<Book>>(books);
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
    protected Void doInBackground(Integer... params) {
        List<Book> bookList = mBookListRef.get();
        if(bookList != null)
            bookList.addAll(MyApplication.getDBOperateInstance().getBooks(params[0]));
        return null;
    }
}
