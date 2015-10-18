package web;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hustunique.myapplication.MyApplication;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import data.Book;
import data.Chapter;
import data.DBOperate;
import data.WebBook;
import data.WebChapter;
import service.Counter;
import util.Constant;

/**
 * Created by taozhiheng on 15-7-29.
 * 完成12种任务
 * １插入一本不存在的书及所有章节到服务器，插入成功后，设置uuid,book_id，清除标记，清除所有章节标记
 * 2修改一本已经存在服务器且没有被删除的书，修改成功后，清除标记
 * 3删除一本已经存在的书，删除成功后，删除本地
 * 4在已经存在服务器且没哟被删除的书中插入一个章节，插入成功后，设置id，清除标记
 * 5在已经存在服务器且没哟被删除的书中修改一个章节，修改成功后，清除标记
 * ６在已经存在服务器且没哟被删除的书中删除一个章节，删除成功后，删除本地
 *
 * 7，8，9标记一本已存在服务其的书为未读、在读、已读，标记成功后，清除标记
 * 10,11,12标记一本已存在服务器的章节为未读、在读、已读，标记成功后，清除标记
 *
 * 修改和更换类型应该在插入之后进行
 */
public class Update {
    /**
     * Created by taozhiheng on 15-7-25.
     * Delete book from local database, and notify web server to do the same change
     */

    private static String auth;

    //插入，修改，删除书或章节的url
    public final static String B_URL = Constant.ip+"/api/v1/books";
    //修改书或章节类型的url
    public final static String UB_URL = Constant.ip+"/api/v1/user/books";
    private static DBOperate mDBOperate;

    public static final int INSERT = 0 ;
    public static final int INSERT_CHAPTER = -1;
    public static final int MODIFY = -2;
    public static final int MODIFY_CHAPTER = -3;
    public static final int DELETE = -4;
    public static final int DELETE_CHAPTER = -5;
    public static final int TYPE = -6;
    public static final int TYPE_CHAPTER = -7;
    public static final int FINISH = -8;

    //刷新标志，是否正在进行刷新
    private static boolean isRunning;
    private static Counter counter;


    public static void executeUpdate(String authorization)
    {
        auth = authorization;
        mDBOperate = MyApplication.getDBOperateInstance();
        Log.d("web", "start update task, start insert task");
        isRunning = true;
        if(counter == null)
            counter = new Counter(0);
        mHandler.sendEmptyMessage(INSERT);
    }

    public static boolean updateIsRunning()
    {
        return isRunning;
    }

    public static int getBookNum()
    {
        return counter.getBookNum();
    }

    public static int getBookFinishNum()
    {
        return counter.getBookFinishNum();
    }

    public static int getChapterNum()
    {
        return counter.getChapterNum();
    }


    private static Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Log.d("web", "receive message:"+msg.what);
            switch (msg.what)
            {
                case -8:
                    /**修改章节类型后，刷新完成*/
                    isRunning = false;
                    break;
                case -7:
                    /**修改书籍类型完成后，修改章节类型*/
                    new TypeChapterTask(UB_URL).execute();
                    break;
                case -6:
                    /**删除章节完成后，修改书籍类型*/
                    new TypeTask(UB_URL).execute();
                    break;
                case -5:
                    /**删除书籍完成后，删除章节*/
                    new DeleteChapterTask(B_URL).execute();
                    break;
                case -4:
                    /**修改章节完成后，删除书籍*/
                    new DeleteTask(B_URL).execute();
                    break;
                case -3:
                    /**修改书籍完成后，修改章节*/
                    new ModifyChapterTask(B_URL).execute();
                    break;
                case -2:
                    /**插入章节完成后，修改书籍*/
                    new ModifyTask(B_URL).execute();
                    break;
                case -1:
                    /**插入书籍完成后，插入章节*/
                    new InsertChapterTask(B_URL).execute();
                    break;
                case 0:
                    /**开始插入书籍*/
                    new InsertTask(B_URL).execute();
                    break;

                case 1://insert book
                    Book book1 = bundle.getParcelable("book");
                    new Web.MyTask(auth, bundle.getString("url"),
                            new InsertCall(book1, (Counter)msg.obj)).execute(book1);
                    break;
                case 2://modify book
                    Book book2 = bundle.getParcelable("book");
                    Web.modifyOnlyBook(auth, bundle.getString("url"), book2,
                            new ModifyCall(book2.getId(), (Counter)msg.obj));
                    break;
                case 3://delete book
                    Web.deleteBook(auth, bundle.getString("url"),
                            new DeleteCall(bundle.getLong("id"), (Counter)msg.obj));
                    break;
                case 4://insert chapter
                    Chapter chapter4 = bundle.getParcelable("chapter");
                    Web.insertChapter(auth, bundle.getString("url"), chapter4,
                            new InsertChapterCall(chapter4.getId(), (Counter)msg.obj));
                    break;
                case 5:
                    Web.modifyChapter(auth, bundle.getString("url"), bundle.getString("name"),
                            new ModifyChapterCall(bundle.getLong("id"), (Counter)msg.obj));
                    break;
                case 6:
                    Log.d("web", "delete chapter ,in handler url:" + bundle.getString("url"));
                    Web.deleteChapter(auth, bundle.getString("url"),
                            new DeleteChapterCall(bundle.getLong("id"), (Counter)msg.obj));
                    break;
                case 7:
                    Web.setBookAfter(auth, bundle.getString("url"),
                            new TypeCall(bundle.getLong("id"), (Counter)msg.obj));
                    break;
                case 8:
                    Web.setBookNow(auth, bundle.getString("url"),
                            bundle.getString("start"), bundle.getString("end"),
                            new TypeCall(bundle.getLong("id"), (Counter)msg.obj));
                    break;
                case 9:
                    Web.setBookBefore(auth, bundle.getString("url"),
                            new TypeCall(bundle.getLong("id"), (Counter)msg.obj));
                    break;
                case 10:
                    Web.setChapterAfter(auth, bundle.getString("url"),
                            new TypeChapterCall(bundle.getLong("id"), (Counter)msg.obj));
                    break;
                case 11:
                    Web.setChapterNowOrRepeat(auth, bundle.getString("url"),
                            new TypeChapterCall(bundle.getLong("id"), (Counter) msg.obj));
                    break;
                case 12:
                    Web.setChapterBefore(auth, bundle.getString("url"),
                            new TypeChapterCall(bundle.getLong("id"), (Counter)msg.obj));
                    break;
                case 13:
                    Web.setChapterNowOrRepeat(auth, bundle.getString("url"),
                            new TypeChapterCall(bundle.getLong("id"), (Counter) msg.obj));
                    break;

            }
        }
    };


    //所有task中均有计数器,用于判断任务是否已经完成
    public static class InsertTask extends AsyncTask<Void, Integer, Void> {


        private String urlHead;
        private Counter mCounter;

        public InsertTask(String urlHead) {
            this.urlHead = urlHead;
            this.mCounter = new Counter(0);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("web", "start delete task");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isEnd()) {
                        try {
                            Thread.sleep(500);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    //开始插入章节
                    counter.addBookNum(mCounter.getBookNum());
                    counter.addChapterNum(mCounter.getChapterNum());
                    mHandler.sendEmptyMessage(INSERT_CHAPTER);
                }
            }).start();
        }

        public boolean isEnd()
        {
            return mCounter.hasCount();
        }

        public Counter getCounter()
        {
            return mCounter;
        }

        @Override
        protected Void doInBackground(Void... params) {
            insertBooks();
            return null;
        }

        private void insertBooks()
        {
            DBOperate dbOperate = MyApplication.getDBOperateInstance();
            //
            List<Book> books = dbOperate.getStatusBooks(Constant.STATUS_ADD);
            mCounter.addBookNum(books.size());
            for (Book book : books)
            {
                Message message = Message.obtain();
                message.what = 1;
                message.obj = mCounter;
                Bundle bundle = new Bundle();
                bundle.putString("url", urlHead);
                bundle.putParcelable("book", book);
                message.setData(bundle);
                mHandler.sendMessage(message);
                mCounter.increase();
            }

        }
    }

    public static class ModifyTask extends AsyncTask<Void, Integer, Void> {


        private String urlHead;
        private Counter mCounter;


        public ModifyTask(String urlHead) {
            this.urlHead = urlHead;
            mCounter = new Counter(0);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("web", "start delete task");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isEnd()) {
                        try {
                            Thread.sleep(500);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    //开始修改章节内容
                    mHandler.sendEmptyMessage(MODIFY_CHAPTER);
                }
            }).start();
        }

        public boolean isEnd()
        {
            return mCounter.hasCount();
        }

        public Counter getCounter()
        {
            return mCounter;
        }

        @Override
        protected Void doInBackground(Void... params) {
            modifyBooks();
            return null;
        }

        private void modifyBooks()
        {
            DBOperate dbOperate = MyApplication.getDBOperateInstance();
            //获得所有标记status_del的书籍
            List<Book> books = dbOperate.getStatusBooks(Constant.STATUS_MOD);
            for (Book book : books)
            {
                if(book.getUUID() == null || book.getUUID().length() < 32)
                    continue;
                Message message = Message.obtain();
                message.what = 2;
                message.obj = mCounter;
                Bundle bundle = new Bundle();
                bundle.putString("url", urlHead+"/"+book.getUUID());
                bundle.putParcelable("book", book);
                message.setData(bundle);
                mHandler.sendMessage(message);
                mCounter.increase();
            }

        }
    }

    public static class DeleteTask extends AsyncTask<Void, Integer, Void> {


        private String urlHead;
        private Counter mCounter;


        public DeleteTask(String urlHead)
        {
            this.urlHead = urlHead;
            mCounter = new Counter(0);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("web", "start delete task");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isEnd()) {
                        try {
                            Thread.sleep(500);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    //开始删除章节
                    mHandler.sendEmptyMessage(DELETE_CHAPTER);
                }
            }).start();
        }

        public boolean isEnd()
        {
            return mCounter.hasCount();
        }

        public Counter getCounter()
        {
            return mCounter;
        }

        @Override
        protected Void doInBackground(Void... params) {
            deleteBooks();
            return null;
        }

        private void deleteBooks() {
            DBOperate dbOperate = MyApplication.getDBOperateInstance();
            //获得所有标记status_del的书籍
            List<Book> books = dbOperate.getStatusBooks(Constant.STATUS_DEL);
            //逐一处理每一本书
            for (Book book : books) {
                //若书籍uuid小于32位，即在服务器上没有，直接删除
                if (book.getUUID() == null || book.getUUID().length() < 32) {
                    dbOperate.deleteBook(book.getUUID());
                    continue;
                }
                Message message = Message.obtain();
                message.what = 3;
                message.obj = mCounter;
                Bundle bundle = new Bundle();
                bundle.putString("url", urlHead+"/"+book.getUUID());
                bundle.putLong("id", book.getId());
                message.setData(bundle);
                mHandler.sendMessage(message);
                mCounter.increase();
            }
        }
    }

    public static class InsertChapterTask extends AsyncTask<Void, Integer, Void> {


        private String urlHead;
        private Counter mCounter;


        public InsertChapterTask(String urlHead)
        {
            this.urlHead = urlHead;
            this.mCounter = new Counter(0);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isEnd()) {
                        try {
                            Thread.sleep(500);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    //开始修改书籍内容
                    mHandler.sendEmptyMessage(MODIFY);
                }
            }).start();
        }

        public boolean isEnd()
        {
            return mCounter.hasCount();
        }

        public Counter getCounter()
        {
            return mCounter;
        }

        @Override
        protected Void doInBackground(Void... params) {
            insertChapters();
            return null;
        }
        //
        private void insertChapters() {
            DBOperate dbOperate = MyApplication.getDBOperateInstance();

            List<Chapter> chapters = dbOperate.getStatusChapters(Constant.STATUS_ADD);
            //逐一处理每一章
            for (Chapter chapter : chapters) {
                //若书籍uuid小于32位，即在服务器上没有
                if(chapter.getWebBookId() == null || chapter.getWebBookId().length() < 32)
                    continue;
                Message message = Message.obtain();
                message.what = 4;
                message.obj = mCounter;
                Bundle bundle = new Bundle();
                bundle.putString("url", urlHead+"/"+chapter.getWebBookId()+"/chapters");
                bundle.putParcelable("chapter", chapter);
                message.setData(bundle);
                mHandler.sendMessage(message);
                mCounter.increase();
            }
        }
    }

    public static class ModifyChapterTask extends AsyncTask<Void, Integer, Void> {


        private String urlHead;
        private Counter mCounter;


        public ModifyChapterTask(String urlHead)
        {
            this.urlHead = urlHead;
            mCounter = new Counter(0);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("web", "start delete task");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isEnd()) {
                        try {
                            Thread.sleep(500);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    //开始删除书籍
                    mHandler.sendEmptyMessage(DELETE);
                }
            }).start();
        }

        public boolean isEnd()
        {
            return mCounter.hasCount();
        }

        public Counter getCounter()
        {
            return mCounter;
        }

        @Override
        protected Void doInBackground(Void... params) {
            modifyChapters();
            return null;
        }

        private void modifyChapters() {
            DBOperate dbOperate = MyApplication.getDBOperateInstance();
            //获得所有标记status_del的书籍
            List<Chapter> chapters = dbOperate.getStatusChapters(Constant.STATUS_MOD);
            //逐一处理每一本书
            for (Chapter chapter : chapters) {
                //若书籍uuid小于32位，即在服务器上没有
                if(chapter.getWebBookId() == null || chapter.getWebBookId().length() < 32)
                    continue;
                Message message = Message.obtain();
                message.what = 5;
                message.obj = mCounter;
                Bundle bundle = new Bundle();
                bundle.putString("url", urlHead+"/"+chapter.getWebBookId()+"/chapters/"+chapter.getWebId());
                bundle.putLong("id", chapter.getId());
                bundle.putString("name", chapter.getName());
                message.setData(bundle);
                mHandler.sendMessage(message);
                mCounter.increase();
            }
        }
    }

    public static class DeleteChapterTask extends AsyncTask<Void, Integer, Void> {


        private String urlHead;
        private Counter mCounter;

        public DeleteChapterTask(String urlHead)
        {
            this.urlHead = urlHead;
            mCounter = new Counter(0);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isEnd()) {
                        try {
                            Thread.sleep(500);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    //开始修改书籍类型
                    mHandler.sendEmptyMessage(TYPE);
                }
            }).start();
        }

        public boolean isEnd()
        {
            return mCounter.hasCount();
        }

        public Counter getCounter()
        {
            return mCounter;
        }

        @Override
        protected Void doInBackground(Void... params) {
            deleteChapters();
            return null;
        }
        //3删除所有待删除的书籍和章节
        private void deleteChapters() {
            DBOperate dbOperate = MyApplication.getDBOperateInstance();
            //获得所有标记status_del的书籍
            List<Chapter> chapters = dbOperate.getStatusChapters(Constant.STATUS_DEL);
            //逐一处理每一本书
            for (Chapter chapter : chapters) {
                //若书籍uuid小于32位，即在服务器上没有
                if(chapter.getWebBookId() == null || chapter.getWebBookId().length() < 32)
                    continue;
                Message message = Message.obtain();
                message.what = 6;
                message.obj = mCounter;
                Bundle bundle = new Bundle();
                bundle.putString("url", urlHead +"/"+ chapter.getWebBookId() + "/chapters/" + chapter.getWebId());
                Log.d("web", "delete chapter url:" + urlHead + "/" + chapter.getWebBookId() + "/chapters/" + chapter.getWebId());
                bundle.putLong("id", chapter.getId());
                message.setData(bundle);
                mHandler.sendMessage(message);
                mCounter.increase();
            }
        }
    }

    public static class TypeTask extends AsyncTask<Void, Integer, Void> {


        private String urlHead;
        private Counter mCounter;

        public TypeTask(String urlHead)
        {
            this.urlHead = urlHead;
            this.mCounter = new Counter(0);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isEnd()) {
                        try {
                            Thread.sleep(500);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    //开始修改章节类型
                    counter.addBookFinishNum(mCounter.getBookFinishNum());
                    mHandler.sendEmptyMessage(TYPE_CHAPTER);
                }
            }).start();
        }

        public boolean isEnd()
        {
            return mCounter.hasCount();
        }

        public Counter getCounter()
        {
            return mCounter;
        }

        @Override
        protected Void doInBackground(Void... params) {
            typeBooks();
            return null;
        }

        private void typeBooks() {
            DBOperate dbOperate = MyApplication.getDBOperateInstance();
            //获得所有标记status_del的书籍
            List<WebBook> books = dbOperate.getWebBooks();
            //逐一处理每一本书
            for (WebBook book : books) {

                if (book.getUuid() == null || book.getUuid().length() < 32) {
                    continue;
                }
                Log.d("web", "set book type:"+book.getUuid()+"/"+book.getType());
                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                switch (book.getType())
                {
                    case 0:
                        message.what = 7;
                        bundle.putString("url", urlHead+"/wishs/"+book.getUuid());
                        break;
                    case 1:
                        message.what = 8;
                        bundle.putString("start", book.getStart());
                        bundle.putString("end", book.getEnd());
                        bundle.putString("url", urlHead+"/readings/"+book.getUuid());
                        break;
                    case 2:
                        message.what = 9;
                        bundle.putString("url", urlHead+"/reads/"+book.getUuid());
                        break;
                }
                bundle.putLong("id", book.getId());
                message.setData(bundle);
                message.obj = mCounter;
                mHandler.sendMessage(message);
                mCounter.increase();
            }
        }
    }

    public static class TypeChapterTask extends AsyncTask<Void, Integer, Void> {


        private String urlHead;
        private Counter mCounter;

        public TypeChapterTask(String urlHead)
        {
            this.urlHead = urlHead;
            mCounter = new Counter(0);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isEnd()) {
                        try {
                            Thread.sleep(500);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    //完成刷新
                    mHandler.sendEmptyMessage(FINISH);
                }
            }).start();
        }

        public boolean isEnd()
        {
            return mCounter.hasCount();
        }

        public Counter getCounter()
        {
            return mCounter;
        }

        @Override
        protected Void doInBackground(Void... params) {
            typeChapters();
            return null;
        }
        //
        private void typeChapters() {
            DBOperate dbOperate = MyApplication.getDBOperateInstance();
            //获得所有标记status_del的书籍
            List<WebChapter> chapters = dbOperate.getWebChapters();
            //逐一处理每一本书
            for (WebChapter chapter : chapters) {
                //若书籍uuid小于32位，即在服务器上没有
                if(chapter.getWebBookId() == null || chapter.getWebBookId().length() < 32)
                    continue;
                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                switch (chapter.getType())
                {
                    case 0:
                        message.what = 10;
                        bundle.putString("url", urlHead+"/"+chapter.getWebBookId()+"/chapters/wishs/"+chapter.getWebId());
                        break;
                    case 1:
                        message.what = 11;
                        bundle.putString("url", urlHead+"/"+chapter.getWebBookId()+"/chapters/readings/"+chapter.getWebId());
                        break;
                    case 2:
                        message.what = 12;
                        bundle.putString("url", urlHead+"/"+chapter.getWebBookId()+"/chapters/reads/"+chapter.getWebId());
                        break;
                    case 3:
                        message.what = 13;
                        bundle.putString("url", urlHead+"/"+chapter.getWebBookId()+"/chapters/readings/"+chapter.getWebId());
                        break;
                }
                bundle.putLong("id", chapter.getId());
                message.setData(bundle);
                message.obj = mCounter;
                mHandler.sendMessage(message);
                mCounter.increase();
            }
        }
    }



    public static class InsertCall implements Callback {

        private Book book;
        private List<Chapter> chapters;
        private Counter mCounter;

        public InsertCall(Book book, Counter counter)
        {
            this.book = book;
            this.chapters = new ArrayList<>();
            this.mCounter = counter;
        }

        public void addChapters(List<Chapter> chapters)
        {
            this.chapters.addAll(chapters);
        }

        @Override
        public void onFailure(Request request, IOException e) {
            mCounter.decrease();
            Log.d("web", "fail insert a book");
        }

        @Override
        public void onResponse(Response response) throws IOException {
            if(!response.isSuccessful()) {
                mCounter.decrease();
                Log.d("web", "fail insert a book onResponse:"+response.code()+"--"+response.body().string());
                return;
            }
            Log.d("web", "succeed insert a book");
            try
            {
                //应该异步
                JSONObject jsonObject = new JSONObject(response.body().string());
                Log.d("web", "insert a book detail:"+jsonObject.toString());
                String UUID = jsonObject.getString("uuid");
                //重新设置本地数据库这本书的uuid，这本书所有章节的book_id,以及所有章节的book_id, 标记为status_OK
                mDBOperate.setBookUUID(book.getId(), UUID);
                //从反馈中取得所有章节的id,重新设置本地数据库, id, status=status_ok
                int type = book.getType();
                if(type != Constant.TYPE_NOW) {
                    for (int k = chapters.size(); k > 0; k--) {
                        Chapter chapter = chapters.get(k - 1);
                        mDBOperate.setChapterOK(chapter.getId(), k);
                    }
                }
                else
                {
                    for (int k = chapters.size(); k > 0; k--) {
                        Chapter chapter = chapters.get(k - 1);
                        mDBOperate.setChapterID(chapter.getId(), k);
                    }
                }
                mCounter.addBookFinishNum(1);
                mCounter.addChapterNum(chapters.size());
            }catch (JSONException e)
            {
                e.printStackTrace();
            }finally {
                mCounter.decrease();
            }
        }
    }

    public static class InsertChapterCall implements Callback
    {
        private long chapterId;
        private Counter mCounter;

        public InsertChapterCall(long id, Counter counter)
        {
            this.chapterId = id;
            this.mCounter = counter;
        }


        @Override
        public void onFailure(Request request, IOException e) {
            mCounter.decrease();
            Log.d("web", "fail insert a chapter");
        }

        @Override
        public void onResponse(Response response) throws IOException {
            if(!response.isSuccessful()) {
                mCounter.decrease();
                return;
            }
            Log.d("web", "succeed insert a chapter");
            try
            {
                //应该异步
                JSONObject json = new JSONObject(response.body().string());
                mDBOperate.setChapterID(chapterId, json.getInt("id"));
            }catch (JSONException e)
            {
                e.printStackTrace();
            }finally {
                mCounter.decrease();
            }
        }
    }

    public static class ModifyCall implements Callback
    {
        private long id;
        private Counter mCounter;

        public ModifyCall(long id, Counter counter)
        {
            this.id = id;
            this.mCounter = counter;
        }


        @Override
        public void onFailure(Request request, IOException e) {
            mCounter.decrease();
            Log.d("web", "fail modify a book");

        }

        @Override
        public void onResponse(Response response) throws IOException {
            if(!response.isSuccessful()) {
                mCounter.decrease();
                return;
            }
            Log.d("web", "succeed modify a book");
            mDBOperate.setBookStatus(id, Constant.STATUS_OK);
            mCounter.decrease();
        }
    }

    public static class ModifyChapterCall implements Callback
    {
        private long chapterId;
        private Counter mCounter;

        public ModifyChapterCall(long id, Counter counter)
        {
            this.chapterId = id;
            this.mCounter = counter;
        }


        @Override
        public void onFailure(Request request, IOException e) {
            mCounter.decrease();
            Log.d("web", "fail modify a chapter");

        }

        @Override
        public void onResponse(Response response) throws IOException {
            if(!response.isSuccessful()) {
                mCounter.decrease();
                return;
            }
            Log.d("web", "succeed modify a chapter");
            mDBOperate.setChapterStatus(chapterId, Constant.STATUS_OK);
            mCounter.decrease();
        }
    }

    public static class DeleteCall implements Callback
    {
        private long id;
        private Counter mCounter;

        public DeleteCall(long id, Counter counter)
        {
            this.id = id;
            this.mCounter = counter;
        }


        @Override
        public void onFailure(Request request, IOException e) {
            mCounter.decrease();
            Log.d("web", "fail delete a book");

        }

        @Override
        public void onResponse(Response response) throws IOException {
            if(!response.isSuccessful()) {
                mCounter.decrease();
                return;
            }
            Log.d("web", "succeed delete a book");
            mDBOperate.deleteBook(id);
            mCounter.decrease();
        }
    }

    public static class DeleteChapterCall implements Callback
    {
        private long chapterId;
        private Counter mCounter;

        public DeleteChapterCall(long id, Counter counter)
        {
            this.chapterId = id;
            this.mCounter = counter;
        }


        @Override
        public void onFailure(Request request, IOException e) {
            mCounter.decrease();
            Log.d("web", "fail delete a chapter");

        }

        @Override
        public void onResponse(Response response) throws IOException {
            if(!response.isSuccessful()) {
                mCounter.decrease();
                return;
            }
            Log.d("web", "succeed delete a chapter");
            mDBOperate.deleteChapter(chapterId);
            mCounter.decrease();
        }
    }

    public static class TypeCall implements Callback
    {
        private long id;
        private Counter mCounter;

        public TypeCall(long id, Counter counter)
        {
            this.id = id;
            this.mCounter = counter;
        }


        @Override
        public void onFailure(Request request, IOException e) {
            mCounter.decrease();
            Log.d("web", "fail set a book type");

        }

        @Override
        public void onResponse(Response response) throws IOException {
            if(!response.isSuccessful()) {
                mCounter.decrease();
                return;
            }
            Log.d("web", "succeed set a book type");
            mDBOperate.setBookTypeOk(id);
            mCounter.addBookFinishNum(1);
            mCounter.decrease();
        }
    }

    public static class TypeChapterCall implements Callback
    {
        private long chapterId;
        private Counter mCounter;

        public TypeChapterCall(long id, Counter counter)
        {
            this.chapterId = id;
            this.mCounter = counter;
        }


        @Override
        public void onFailure(Request request, IOException e) {
            mCounter.decrease();
            Log.d("web", "fail set a chapter type");

        }

        @Override
        public void onResponse(Response response) throws IOException {
            if(!response.isSuccessful()) {
                mCounter.decrease();
                return;
            }
            Log.d("web", "succeed set a chapter type");
            mDBOperate.setChapterTypeOK(chapterId);
            mCounter.decrease();
        }
    }

}
