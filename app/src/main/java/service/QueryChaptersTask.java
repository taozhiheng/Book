package service;

import android.os.AsyncTask;

import com.hustunique.myapplication.MyApplication;

import java.util.List;

import data.Chapter;

/**
 * Created by taozhiheng on 15-8-7.
 */
public class QueryChaptersTask extends AsyncTask<Long, Integer, Void> {

    private List<Chapter> mChapterList;
    private android.os.Handler mHandler;

    public QueryChaptersTask(List<Chapter> chapters, android.os.Handler handler)
    {
        this.mChapterList = chapters;
        this.mHandler = handler;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mHandler.sendEmptyMessage(0);
    }

    @Override
    protected Void doInBackground(Long... params) {
        if(params[0] < 0)
            mChapterList.addAll(MyApplication.getDBOperateInstance().getNowChapters());
        else
            mChapterList.addAll(MyApplication.getDBOperateInstance().getChapters(params[0]));
        return null;
    }
}
