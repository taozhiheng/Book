package web;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hustunique.myapplication.MyApplication;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import data.Book;
import data.ChapterInfo;
import data.DBOperate;
import data.TimeInfo;
import service.Counter;
import util.Constant;

/**
 * Created by taozhiheng on 15-7-29.
 * 完成两个任务之一
 * １将本地数据清空，将服务器所有书籍、章节内容拷贝，将在读书籍章节类型拷贝
 * ２将服务器清空，将本地所有标记删除的书籍删除，
 * 未被标记删除的书籍内容标记为添加，uuid标记为空,book_id标记为空,id标记为-1(空)类型标记为当前类型，章节类型标记为当前类型，并添加到服务器，修改书籍类型、章节类型
 */
public class Sync {

    private final static String U_URL = Constant.ip+"/api/v1/user/books";
    private final static String URL = Constant.ip+"/api/v1/books";

    private static String auth;
    private static Counter mCounter;
    private static Context mContext;

    public static void executeSync(Context context, String authorization, int choice)
    {
        auth = authorization;
        mCounter = new Counter(0);
        mContext = context;
        if(choice == Constant.CHOICE_WEB)
        {
            new Pull().execute();
        }
        else if(choice == Constant.CHOICE_LOCAL)
        {
            Log.d("web", "start sync from local to web");
            new Push().execute();
        }
    }

    //下载到本地,写入数据库
    public static class Pull extends AsyncTask<Void, Integer, Void>
    {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mCounter.hasCount()) {
                        try {
                            Thread.sleep(500);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent("com.hustunique.myapplication.MAIN_RECEIVER");
                    intent.putExtra("counter", mCounter);
                    intent.putExtra("choice", Constant.CHOICE_WEB);
                    mContext.sendBroadcast(intent);
                }
            }).start();
        }

        @Override
        protected Void doInBackground(Void... params) {
            MyApplication.getDBOperateInstance().initAll();
            List<String> wishList = Web.queryWishOrRead(auth, U_URL+"/wishs");
            mCounter.addBookNum(wishList.size());
            for(String wish : wishList)
            {
                Message message = Message.obtain();
                message.what = 0;
                message.obj = URL+"/"+wish;
                Log.d("web", "increase a request, url:"+message.obj.toString());

                mPullHandler.sendMessage(message);
                mCounter.increase();
            }
            List<String> readList = Web.queryWishOrRead(auth, U_URL + "/reads");
            mCounter.addBookNum(readList.size());
            for(String read : readList)
            {
                Message message = Message.obtain();
                message.what = 2;
                message.obj = URL+"/"+read;
                Log.d("web", "increase a request, url:"+message.obj.toString());

                mPullHandler.sendMessage(message);
                mCounter.increase();

            }

            List<TimeInfo> timeList = new ArrayList<>();
            Map<String, List<Integer>> map = Web.queryReading(auth, U_URL + "/readings", timeList);
            mCounter.addBookNum(map.size());
            int i = 0;
            for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
                String uuid = entry.getKey();
                ArrayList<Integer> types = (ArrayList<Integer>)entry.getValue();
                Message message = Message.obtain();
                message.what = 1;
                message.obj = URL+"/"+uuid;
                Bundle bundle = new Bundle();
                bundle.putIntegerArrayList("types", types);
                if(i < timeList.size()) {
                    bundle.putParcelable("timeInfo", timeList.get(i));
                    i++;
                }
                message.setData(bundle);
                Log.d("web", "increase a request, url:" + message.obj.toString());
                mPullHandler.sendMessage(message);
                mCounter.increase();
            }
            return null;
        }
    }

    private static Handler mPullHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1)
            {
                Bundle bundle = msg.getData();
                List<Integer> types = bundle.getIntegerArrayList("types");
                TimeInfo timeInfo = bundle.getParcelable("timeInfo");
                Web.getBook(auth, msg.obj.toString(), new NormalCall(mCounter, msg.what, types, timeInfo));
            }
            else
            {
                Web.getBook(auth, msg.obj.toString(), new NormalCall(mCounter, msg.what));
            }
        }
    };

    private static class NormalCall implements Callback
    {
        private int type;
        private List<Integer> mTypeList;
        private TimeInfo mTimeInfo;
        private Counter mCounter;

        public NormalCall(Counter counter, int type)
        {
            this.mCounter = counter;
            this.type = type;
        }

        public NormalCall(Counter counter, int type, List<Integer> typeList, TimeInfo timeInfo)
        {
            this.mCounter = counter;
            this.type = type;
            this.mTypeList = typeList;
            this.mTimeInfo = timeInfo;
        }

        @Override
        public void onResponse(Response response) throws IOException {
            if(!response.isSuccessful())
                return;
            try {
                //读入书籍信息
                String string = response.body().string();
                Log.d("web", "book detail:"+string);
                JSONObject json = new JSONObject(string);
                Book book = new Book();
                book.setUUID(json.getString("uuid"));
                book.setIsbn(json.getString("isbn"));
                book.setName(json.getString("title"));
                book.setAuthor(json.getString("creator"));
                book.setPress(json.getString("publisher"));
                String color = json.getString("color");
                if(color == null || color.contains("null"))
                    book.setColor(0);
                else
                    book.setColor(Integer.valueOf(color));
                book.setWordNum(json.getLong("words"));
                book.setUrl(json.getString("cover"));
                book.setType(type);
                book.setStatus(Constant.STATUS_OK);
                if(mTimeInfo != null)
                {
                    book.setStartTime(mTimeInfo.getStartTime());
                    book.setEndTime(mTimeInfo.getEndTime());
                }
                //读入章节信息
                List<ChapterInfo> chapterInfos = new ArrayList<>();
                JSONArray chapters = json.getJSONArray("chapters");
                for (int j = 0; j < chapters.length(); j++) {
                    JSONObject chapter = chapters.getJSONObject(j);
                    ChapterInfo chapterInfo = new ChapterInfo(chapter.getInt("id"), chapter.getString("name"));
                    if(mTypeList != null) {
                        chapterInfo.setType(mTypeList.get(j));
                        Log.d("web", "write a now book chapter type:"+mTypeList.get(j));
                    }
                    chapterInfos.add(chapterInfo);
                }
                //将一本书完整插入本地，但是章节全都是未读状态，待完善
//                new WriteTask(book, chapterInfos).execute();
                MyApplication.getDBOperateInstance().writeBook(book, chapterInfos);
                mCounter.addBookFinishNum(1);
                mCounter.addChapterNum(chapterInfos.size());
                mCounter.decrease();
                Log.d("web", "sync, write a book to local");
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Request request, IOException e) {
            mCounter.decrease();
        }
    }



    public static class Push extends AsyncTask<Void, Integer, Void>
    {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (Update.updateIsRunning()) {
                        try {
                            Thread.sleep(500);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent("com.hustunique.myapplication.MAIN_RECEIVER");
                    mCounter.addBookNum(Update.getBookNum());
                    mCounter.addBookFinishNum(Update.getBookFinishNum());
                    mCounter.addChapterNum(Update.getChapterNum());
                    intent.putExtra("counter", mCounter);
                    intent.putExtra("choice", Constant.CHOICE_LOCAL);
                    mContext.sendBroadcast(intent);
                }
            }).start();

        }

        //删除服务器所有数据，删除本地所有待删除书籍和章节，设置书籍状态为ok,类型为未设定
        @Override
        protected Void doInBackground(Void... params) {
            boolean result = Web.clearWeb(auth, U_URL);
            Log.d("web", "clear web tables:" + result);
            DBOperate dbOperate = MyApplication.getDBOperateInstance();
            dbOperate.deleteAll();
            dbOperate.resetAll();
            mPushHandler.sendEmptyMessage(0);
            Log.d("web", "reset all to new");
            return null;
        }
    }

    private static Handler mPushHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0) {
                Update.executeUpdate(MyApplication.getAuthorization());
            }
        }
    };
}
