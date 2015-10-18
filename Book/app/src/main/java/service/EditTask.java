package service;

import android.os.AsyncTask;
import android.os.Handler;

import com.hustunique.myapplication.MyApplication;

import java.lang.ref.WeakReference;
import java.util.List;

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
    private WeakReference<List<Integer>> mDeletePosListRef;
    private WeakReference<List<ChapterInfo>> mGroupListRef;//记录当前状态数据
    private WeakReference<List<Chapter>> mChapterListRef;
    private WeakReference<Book> mBookRef;
    private WeakReference<Handler> mHandlerRef;

    public EditTask(Book mBook, List<ChapterInfo> mGroupList, List<Chapter> chapters, List<Integer> mDeletePosList,
                    Handler handler) {
        this.mBookRef = new WeakReference<Book>(mBook);
        this.mGroupListRef = new WeakReference<List<ChapterInfo>>(mGroupList);
        this.mChapterListRef = new WeakReference<List<Chapter>>(chapters);
        this.mDeletePosListRef = new WeakReference<List<Integer>>(mDeletePosList);
        this.mHandlerRef = new WeakReference<Handler>(handler);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Handler handler  = mHandlerRef.get();
        if(handler != null)
            handler.sendEmptyMessage(0);
    }

    @Override
    protected Void doInBackground(Void... params) {
        Book book = mBookRef.get();
        List<Integer> deletePosList = mDeletePosListRef.get();
        List<ChapterInfo> groupList = mGroupListRef.get();
        List<Chapter> chapterList = mChapterListRef.get();
        if(book == null || groupList == null || chapterList == null)
            return null;
        DBOperate dbOperate = MyApplication.getDBOperateInstance();
        dbOperate.updateBook(book);
        //数据库中存在，但不确定是否已添加到网络,接下来的操作：
        //若至操作完成仍未加到网络，正常
        //若至操作完成已添加到网络，
        int status = Constant.STATUS_MOD;
        if(book.getStatus() == Constant.STATUS_ADD)
            status = Constant.STATUS_ADD;
        dbOperate.setBookStatus(book.getId(), status);
        //更新或增加
        int index = dbOperate.getBookMaxChapterIndex(book.getId());
        boolean hasNew = false;
        for (ChapterInfo chapterInfo : groupList) {
            int position = chapterInfo.getPosition();
            Chapter chapter;
            String chapterName = chapterInfo.getName().trim();
            //更新
            if (position != -1 && chapterName.compareTo("")!=0 ) {
                chapter = chapterList.get(position);
                if(chapter.getName().equals(chapterName))
                    continue;
                dbOperate.updateChapter(chapter.getId(), chapterName);
                int chapterStatus = Constant.STATUS_MOD;
                if(chapter.getStatus() == Constant.STATUS_ADD)
                    chapterStatus = Constant.STATUS_ADD;
                dbOperate.setChapterStatus(chapter.getId(), chapterStatus);
            }
            else if(chapterInfo.getName().trim().compareTo("") != 0)//增加
            {
                index++;
                dbOperate.insertChapter(book.getId(), index, book.getUUID(), chapterInfo.getName());
                if(!hasNew)
                    hasNew = true;
            }
        }
        //删除
        if (deletePosList != null)
        {
            for (int position : deletePosList)
                dbOperate.setChapterDelete(chapterList.get(position).getId());
            if(book.getType() == Constant.TYPE_NOW)
                dbOperate.checkBookFinish(book.getId());
        }
        if(hasNew && book.getType() == Constant.TYPE_BEFORE)
        {
            dbOperate.setBookBefore(book.getId(), book.getEndTime());
        }
        return null;
    }
}
