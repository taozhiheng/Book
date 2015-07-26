package service;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;

import com.hustunique.myapplication.MyApplication;

import java.util.List;

import adapter.ChapterCreateAdapter;
import data.Book;
import data.Chapter;
import data.ChapterInfo;
import data.DBOperate;
import util.Constant;

/**
 * Created by taozhiheng on 15-7-25.
 * Edit a book and chapters in local database
 */
public class EditTask extends AsyncTask<Void, Integer, Void> {
    private List<Integer> mDeletePosList;
    private List<ChapterInfo> mGroupList;//记录当前状态数据
    private List<Chapter> mChapterList;
    private Book mBook;
    private Handler mHandler;

    public EditTask(Book mBook, List<ChapterInfo> mGroupList, List<Chapter> chapters, List<Integer> mDeletePosList,
                    Handler handler) {
        this.mBook = mBook;
        this.mGroupList = mGroupList;
        this.mChapterList = chapters;
        this.mDeletePosList = mDeletePosList;
        this.mHandler = handler;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mHandler.sendEmptyMessage(0);
    }

    @Override
    protected Void doInBackground(Void... params) {
        String bookId = mBook.getUUID();
        DBOperate dbOperate = MyApplication.getDBOperateInstance();
        dbOperate.updateBook(mBook);
        int status = Constant.STATUS_MOD;
        if(mBook.getStatus() == Constant.STATUS_ADD)
            status = Constant.STATUS_ADD;
        dbOperate.setBookStatus(mBook.getUUID(), status);
        //更新或增加
        int index = dbOperate.getBookMaxChapterIndex(bookId);
        for (ChapterInfo chapterInfo : mGroupList) {
            int position = chapterInfo.getPosition();
            Chapter chapter;
            //更新
            if (position != -1) {
                chapter = mChapterList.get(position);
                dbOperate.updateChapter(chapter.getBookId(), chapter.getId(), chapterInfo.getName());
                int status2 = Constant.STATUS_MOD;
                if(mBook.getStatus() == Constant.STATUS_ADD)
                    status2 = Constant.STATUS_ADD;
                dbOperate.setChapterStatus(chapter.getBookId(), chapter.getId(), status2);
            } else//增加
            {
                index++;
                dbOperate.insertChapter(bookId, index, chapterInfo.getName());
            }
        }
        //删除
        if (mDeletePosList != null)
        {
            for (int position : mDeletePosList)
                dbOperate.setChapterDelete(bookId, mChapterList.get(position).getId());
            if(mBook.getType() == Constant.TYPE_NOW)
                dbOperate.checkBookFinish(bookId);
        }
        return null;
    }
}
