package service;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hustunique.myapplication.MyApplication;
import net.MyJsonArrayRequest;
import net.MyJsonObjectRequest;
import net.MyStringRequest;
import net.OkHttpStack;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import data.Book;
import data.Chapter;
import data.ChapterInfo;
import data.DBOperate;
import util.Constant;
import util.TimeUtil;

/**
 * Created by taozhiheng on 15-7-25.
 *
 */
public class MyAsyncTask extends AsyncTask<Void, Integer, Void>{



    private RequestQueue mRequestQueue;
    private RequestQueue mRequestQueue1;
    private RequestQueue mRequestQueue2;
    private RequestQueue mRequestQueue3;

    private int mCMD;
    private Book mBook;
    private List<ChapterInfo> mChapterInfos;

    private Context mContext;
    private Counter mCounter ;

    public MyAsyncTask(Context context, int command)
    {
        this.mContext = context;
        this.mCMD = command;
        this.mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack());
        this.mRequestQueue1 = Volley.newRequestQueue(context);
        this.mRequestQueue2 = Volley.newRequestQueue(context);
        this.mRequestQueue3 = Volley.newRequestQueue(context);

        mBook = new Book();
        mChapterInfos = new ArrayList<>();
        mCounter = new Counter(0);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("web","do in background finish");
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
                mContext.sendBroadcast(intent);
            }
        }).start();

    }

    @Override
    protected Void doInBackground(Void... params) {
        if(mCMD == Constant.CHOICE_WEB)
        {
            choseWeb();
        }
        //用本地数据覆盖服务器
        else
        {
            choseLocal();
        }
        return null;
    }


    //0-查所有书籍uuid,得到jsonArray
    private android.os.Handler mHandler = new android.os.Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            //开始找未读书籍
            mCounter.increase();
            mRequestQueue1.add(new MyJsonArrayRequest(
                    Request.Method.GET,
                    Constant.URL_BOOKS + "/wishs",
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(final JSONArray response) {
                            mCounter.decrease();
                            mCounter.addBookNum(response.length());
                            Log.d("web", "sync, succeed search all wish books from web, detail:" + response);
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    //拿到一本书的uuid,type
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    String uuid = jsonObject.getString("uuid");
                                    //查询书籍详情，将一本书录入本地，正确设置
                                    Log.d("web", "sync, search a wish book detail, url:" + Constant.URL_BOOK + "/" + uuid);
                                    mCounter.increase();
                                    mRequestQueue1.add(new MyJsonObjectRequest(Request.Method.GET, Constant.URL_BOOK + "/" + uuid, null,
                                            new WriteBookResponse(mCounter, Constant.TYPE_AFTER), new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("web", "fail to find a wish book detail"+error.toString());
                                            mCounter.decrease();
                                        }
                                    }));
                                    mRequestQueue1.start();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("web", "sync, can find wish book detail,:" + error);
                    mCounter.decrease();
                }
            }));
            mRequestQueue1.start();
            //查找所有已读书籍
            mCounter.increase();
            mRequestQueue2.add(new MyJsonArrayRequest(
                    Request.Method.GET,
                    Constant.URL_BOOKS + "/reads",
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(final JSONArray response) {
                            mCounter.decrease();
                            mCounter.addBookNum(response.length());
                            Log.d("web", "sync, succeed search all read books from web, detail:" + response);
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    //拿到一本书的uuid,type
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    String uuid = jsonObject.getString("uuid");
                                    //查询书籍详情，将一本书录入本地，正确设置
                                    Log.d("web", "sync, search a read book detail, url:" + Constant.URL_BOOK + "/" + uuid);
                                    mCounter.increase();
                                    mRequestQueue2.add(new MyJsonObjectRequest(Request.Method.GET, Constant.URL_BOOK + "/" + uuid, null,
                                            new WriteBookResponse(mCounter, Constant.TYPE_BEFORE), new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("web", "fail to find a read book detail" + error.toString());
                                            mCounter.decrease();

                                        }
                                    }));
                                    mRequestQueue2.start();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("web", e.toString());
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mCounter.decrease();
                }
            }));
            mRequestQueue2.start();
            //查找所有在读书籍
            mCounter.increase();
            mRequestQueue3.add(new MyJsonArrayRequest(
                    Constant.URL_BOOKS + "/readings",
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            mCounter.decrease();
                            mCounter.addBookNum(response.length());
                            Log.d("web", "sync, succeed search all reading books info:" + response);
                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    JSONObject jsonObject = response.getJSONObject(i);
                                    String uuid = jsonObject.getString("uuid");

                                    List<Integer> mTypeList = new ArrayList<>();
                                    JSONArray chapters = jsonObject.getJSONArray("chapters");
                                    for (int j = 0; j < chapters.length(); j++) {
                                        //取得一本书一个章节json串，改变章节类型
                                        JSONObject chapter = chapters.getJSONObject(j);
                                        String typeString = chapter.getString("status");
                                        int type;
                                        if (typeString.contains("null"))
                                            type = Constant.TYPE_AFTER;
                                        else
                                            type = Integer.parseInt(typeString);
                                        mTypeList.add(type);
                                    }

                                    Log.d("web", "sync, start search a reading book detail, url:" + Constant.URL_BOOK + "/" + uuid);
                                    mCounter.increase();
                                    mRequestQueue3.add(new MyJsonObjectRequest(Request.Method.GET, Constant.URL_BOOK + "/" + uuid, null,
                                            new WriteBookResponse(mCounter, Constant.TYPE_NOW, mTypeList), new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("web", "fail to find a reading book detail" + error.toString());
                                            mCounter.decrease();
                                        }
                                    }));
                                    mRequestQueue3.start();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }


                        }
                    }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mCounter.decrease();
                }
            }
            ));
            mRequestQueue3.start();
        }
    };


    //使用账户覆盖本地
    private void choseWeb()
    {
        final DBOperate dbOperate = MyApplication.getDBOperateInstance();
        Log.d("web", "sync, start sync from web to local");
        //清空本地数据
        Log.d("net", "sync, clear local tables");
        dbOperate.clearTables();
        mHandler.sendEmptyMessage(0);
    }

    //使用本地覆盖账户
    private void choseLocal()
    {
        final DBOperate dbOperate = MyApplication.getDBOperateInstance();

        Log.d("web", "sync, start sync from local to web");
        Log.d("web", "sync, clear web tables");
        mCounter.increase();
        mRequestQueue.add(new MyStringRequest(
                Request.Method.DELETE,
                Constant.URL_BOOKS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mCounter.decrease();
                        Log.d("web", "sync, succeed clear web tables");

                        //删除本地所有标记为删除的书和章节
                        dbOperate.deleteAll();
                        dbOperate.resetAll();
                        //将书籍逐一插入
                        final List<Book> bookList = dbOperate.getBooks(-1);
                        mCounter.addBookNum(bookList.size());
                        for (final Book book : bookList) {

                            //向服务器发送请求，新建一本书及所有章节,并修改本地书的uuid,章节的book_id,id
                            final List<Chapter> chapters = dbOperate.getChapters(book.getUUID());
                            JSONObject jsonObject = BookUtil.getBookJson(book, chapters);

                            Log.d("web", "insert a book to web:" + jsonObject);
                            mCounter.increase();
                            mRequestQueue.add(new MyJsonObjectRequest(
                                    Request.Method.POST,
                                    Constant.URL_BOOK,
                                    jsonObject,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            mCounter.decrease();
                                            mCounter.addBookFinishNum(1);
                                            //取得书籍的uuid,将本地书籍的uuid及章节的book_id,id改为和服务器一致
                                            Log.d("web", "succeed insert a book to web, detail:" + response);
                                            try {
                                                final String UUID = response.getString("uuid");
                                                dbOperate.resetBookUUID(book.getUUID(), UUID);
                                                dbOperate.setBookStatus(UUID, Constant.STATUS_MOD);
                                                JSONArray jsonArray = response.getJSONArray("chapters");
                                                for (int k = jsonArray.length(); k > 0; k--) {
                                                    JSONObject json = jsonArray.getJSONObject(k - 1);
                                                    Chapter chapter = chapters.get(k - 1);
                                                    int ID = json.getInt("id");
                                                    dbOperate.resetChapterID(
                                                            UUID, chapter.getId(), ID);
                                                    chapter.setBookId(UUID);
                                                    chapter.setId(ID);
                                                }
                                                dbOperate.setChaptersStatus(UUID, Constant.STATUS_MOD);

                                                //然后修改服务器书籍的类型及章节的类型
                                                String url = Constant.URL_BOOKS;
                                                HashMap<String, String> map = new HashMap<>();
                                                switch (book.getType()) {
                                                    case Constant.TYPE_AFTER:
                                                        url += "/wishs/";
                                                        map.put("add_time",
                                                                TimeUtil.getNeedTime(System.currentTimeMillis()));
                                                        break;
                                                    case Constant.TYPE_NOW:
                                                        url += "/readings/";
                                                        String start = book.getStartTime();
                                                        String end = book.getEndTime();
                                                        if (start == null || end == null || end.compareTo(start) <= 0) {
                                                            start = TimeUtil.getNeedTime(System.currentTimeMillis());
                                                            end = TimeUtil.getNeedTime(System.currentTimeMillis() + 12 * 60 * 60 * 1000);
                                                        }
                                                        map.put("start_time", start);
                                                        map.put("end_time", end);
                                                        break;
                                                    case Constant.TYPE_BEFORE:
                                                        url += "/reads/";
                                                        map.put("end_time",
                                                                TimeUtil.getNeedTime(System.currentTimeMillis()));
                                                        break;
                                                }
                                                url += UUID;
                                                JSONObject jsonObject = new JSONObject(map);
                                                //修改书籍类型
                                                Log.d("web", "reset book type" + book.getType() + " to web:" + jsonObject.toString());
                                                mCounter.increase();
                                                mRequestQueue.add(new MyJsonObjectRequest(Request.Method.PUT, url, jsonObject, new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        mCounter.decrease();
                                                        Log.d("web", "succeed reset book type, so book ok:" + response);
                                                        dbOperate.setBookStatus(UUID, Constant.STATUS_OK);
                                                        if(book.getType() != Constant.TYPE_NOW)
                                                            dbOperate.setChaptersStatus(book.getUUID(), Constant.STATUS_OK);
                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        mCounter.decrease();
                                                        Log.d("web", "fail reset book type:" + error);
                                                    }
                                                }));
                                                mRequestQueue.start();

                                                //修改在读书籍的章节类型
                                                if (book.getType() == Constant.TYPE_NOW)
                                                {
                                                    //改变在读书籍的章节类型
                                                    for (final Chapter chapter : chapters) {
                                                        if (chapter.getType() != Constant.TYPE_AFTER) {
                                                            map.clear();
                                                            String chapterUrl = Constant.URL_BOOKS + "/" + UUID + "/chapters/";
                                                            switch (chapter.getType()) {
                                                                case Constant.TYPE_NOW:
                                                                    chapterUrl += "readings/";
                                                                    map.put("start_time", TimeUtil.getNeedTime(System.currentTimeMillis()));
                                                                    break;
                                                                case Constant.TYPE_BEFORE:
                                                                    chapterUrl += "reads/";
                                                                    map.put("end_time", TimeUtil.getNeedTime(System.currentTimeMillis()));
                                                                    break;
                                                            }
                                                            chapterUrl += chapter.getId();
                                                            jsonObject = new JSONObject(map);
                                                            Log.d("web", "reset a chapter type to web:" + jsonObject);
                                                            mCounter.increase();
                                                            mRequestQueue.add(new MyJsonObjectRequest(Request.Method.PUT, chapterUrl,
                                                                    jsonObject,
                                                                    new Response.Listener<JSONObject>() {
                                                                        @Override
                                                                        public void onResponse(JSONObject response) {
                                                                            mCounter.decrease();
                                                                            Log.d("web", "succeed reset a chapter type to web:" + response);
                                                                            dbOperate.setChapterStatus(UUID, chapter.getId(), Constant.STATUS_OK);
//
                                                                        }
                                                                    },
                                                                    new Response.ErrorListener() {
                                                                        @Override
                                                                        public void onErrorResponse(VolleyError error) {
                                                                            mCounter.decrease();
//
                                                                        }
                                                                    }));
                                                            mRequestQueue.start();
                                                        } else
                                                            dbOperate.setChapterStatus(UUID, chapter.getId(), Constant.STATUS_OK);
                                                    }
                                                }


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            mCounter.decrease();
                                            Log.d("web", "fail insert a book to web:"
                                                    + error + "/");
                                        }
                                    }));
                            mRequestQueue.start();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mCounter.decrease();
                        Log.d("web", "cant delete table at web" + error);

                    }
                }));
        mRequestQueue.start();

    }
}
