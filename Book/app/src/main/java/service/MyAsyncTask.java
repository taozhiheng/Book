package service;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

    private final static String URL_BOOKS = "http://pokebook.whitepanda.org:2333/api/v1/user/books";
    private final static String URL_BOOK = "http://pokebook.whitepanda.org:2333/api/v1/books";

    private RequestQueue mRequestQueue;
    private int mCMD;
    private Book mBook;
    private List<ChapterInfo> mChapterInfos;

    private Context mContext;

    public MyAsyncTask(Context context, int command)
    {
        this.mContext = context;
        this.mCMD = command;
        this.mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack());
        mBook = new Book();
        mChapterInfos = new ArrayList<>();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("web","do in background finish");

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

    //使用账户覆盖本地
    private void choseWeb()
    {
        final DBOperate dbOperate = MyApplication.getDBOperateInstance();
        Log.d("web", "sync, start sync from web to local");
        //清空本地数据
        Log.d("net", "sync, clear local tables");
        dbOperate.clearTables();

        Log.d("web", "sync, search all books uuid from web");
        mRequestQueue.add(new MyJsonArrayRequest(
                Request.Method.GET,
                URL_BOOKS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        Log.d("web", "sync, succeed search all books from web, detail:" + response);
                        for (int i = 0; i < response.length(); i++)
                        {
                            final boolean last = (i == response.length()-1);
                            try {
                                //拿到一本书的uuid,type
                                final JSONObject jsonObject = response.getJSONObject(i);
                                String uuid = jsonObject.getString("uuid");
                                String typeString = jsonObject.getString("status");
                                Log.d("web", "sync, succeed get type str:" + typeString);
                                final int type;
                                if(typeString.contains("null"))
                                    type = Constant.TYPE_AFTER;
                                else
                                    type = Integer.parseInt(typeString);

                                //查询书籍详情，将一本书录入本地，正确设置
                                Log.d("web", "sync, search a book detail, url:"+URL_BOOK + "/" + uuid);
                                mRequestQueue.add(new MyJsonObjectRequest(
                                        Request.Method.GET,
                                        URL_BOOK + "/" + uuid,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response)
                                            {
                                                Log.d("web", "sync, succeed search a book detail:" + response);
                                                try {
                                                    //读入书籍信息
                                                    mBook.setUUID(response.getString("uuid"));
                                                    mBook.setIsbn(response.getString("isbn"));
                                                    mBook.setName(response.getString("title"));
                                                    mBook.setAuthor(response.getString("creator"));
                                                    mBook.setPress(response.getString("publisher"));
                                                    mBook.setColor(0);
                                                    mBook.setWordNum(response.getLong("words"));
                                                    mBook.setUrl(response.getString("cover"));
                                                    mBook.setType(type);
                                                    mBook.setStatus(Constant.STATUS_OK);
                                                    //读入章节信息
                                                    mChapterInfos.clear();
                                                    JSONArray chapters = response.getJSONArray("chapters");
                                                    for (int j = 0; j < chapters.length(); j++) {
                                                        JSONObject chapter = chapters.getJSONObject(j);
                                                        mChapterInfos.add(new ChapterInfo(chapter.getInt("id"), chapter.getString("name")));
                                                    }
                                                    //将一本书完整插入本地，但是章节全都是未读状态，待完善
                                                    Log.d("web", "sync, write a book to local");
                                                    dbOperate.writeBook(mBook, mChapterInfos);

                                                    //查询所有在读书籍的全部信息，将其中在读，和已读的章节记入本地数据库
                                                    //执行最后一个请求成功后,从服务器查询所有用户在读书籍信息，修改本地在读书籍章节的类型
                                                    if(last) {

                                                        Log.d("web", "sync, search all reading books info, url:"+URL_BOOKS + "/readings");
                                                        mRequestQueue.add(new MyStringRequest(
                                                                Request.Method.GET,
                                                                URL_BOOKS + "/readings",
                                                                new Response.Listener<String>() {
                                                                    @Override
                                                                    public void onResponse(String res) {
                                                                        Log.d("web", "sync, succeed search all reading books info:" + res);
                                                                        Intent intent = new Intent("com.hustunique.myapplication.MAIN_RECEIVER");

                                                                        try {
                                                                            JSONArray response = new JSONArray(res);
                                                                            for (int i = 0; i < response.length(); i++) {
                                                                                //取得一本书的json串
                                                                                JSONObject bookInfo = response.getJSONObject(i);
                                                                                String uuid = bookInfo.getString("uuid");
                                                                                //取得一本书全部章节jsonArray串
                                                                                JSONArray chapters = bookInfo.getJSONArray("chapters");
                                                                                for (int j = 0; j < chapters.length(); j++) {
                                                                                    //取得一本书一个章节json串，改变章节类型
                                                                                    JSONObject chapter = chapters.getJSONObject(j);
                                                                                    int id = chapter.getInt("id");
                                                                                    String typeString = chapter.getString("status");
                                                                                    Log.d("web", "sync, succeed get type str:" + typeString);
                                                                                    int type;
                                                                                    if(typeString.contains("null"))
                                                                                        type = Constant.TYPE_AFTER;
                                                                                    else
                                                                                        type = Integer.parseInt(typeString);
                                                                                    if(type != Constant.TYPE_AFTER)
                                                                                        dbOperate.setChapterType(uuid, id, type);
                                                                                }
                                                                            }
                                                                            if(dbOperate.getBookNum() == response.length()) {
                                                                                intent.putExtra("syncResult", true);
                                                                                intent.putExtra("info", "同步成功");
                                                                            }
                                                                            else
                                                                            {
                                                                                intent.putExtra("syncResult", false);
                                                                                intent.putExtra("info", "同步不完整");
                                                                            }

                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                            intent.putExtra("syncResult", false);
                                                                            intent.putExtra("info", "章节类型同步失败");
                                                                        }finally {
                                                                            mContext.sendBroadcast(intent);

                                                                        }
                                                                    }
                                                                },
                                                                new Response.ErrorListener() {
                                                                    @Override
                                                                    public void onErrorResponse(VolleyError error) {
                                                                        Log.d("web", "sync, fail search all reading books info" + error+"/"+error.getCause());
                                                                        Intent intent = new Intent("com.hustunique.myapplication.MAIN_RECEIVER");
                                                                        intent.putExtra("syncResult", false);
                                                                        intent.putExtra("info", "章节类型同步失败");
                                                                        mContext.sendBroadcast(intent);
                                                                    }
                                                                }
                                                        ));
                                                        mRequestQueue.start();
                                                    }


                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Log.d("web", e.toString());
                                                }

                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("web", "sync, fail search a book detail " + error);
                                        if(last) {
                                            if(dbOperate.getBookNum() < response.length()) {
                                                Intent intent = new Intent("com.hustunique.myapplication.MAIN_RECEIVER");
                                                intent.putExtra("syncResult", false);
                                                intent.putExtra("info", "同步不完整");
                                                mContext.sendBroadcast(intent);
                                            }
                                        }

                                    }
                                }));
                                mRequestQueue.start();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("web", e.toString());
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("web", "sync, fail search all reading books uuid, why?"+error+" /");
                Intent intent = new Intent("com.hustunique.myapplication.MAIN_RECEIVER");
                intent.putExtra("syncResult", false);
                intent.putExtra("info", "从服务器获取数据失败");
                mContext.sendBroadcast(intent);
            }
        }));
        //?why string can?
//        mRequestQueue.add(new MyStringRequest(
//                Request.Method.GET,
//                URL_BOOKS, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("web", "sync, succeed search all books uuid from web, detail:" + response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("web", "sync, fail search all books uuid from web, why:"
//                        + error+" /"+error.networkResponse.statusCode);
//            }
//        }));
        mRequestQueue.start();
    }

    //使用本地覆盖账户
    private void choseLocal()
    {
        final DBOperate dbOperate = MyApplication.getDBOperateInstance();

        Log.d("web", "sync, start sync from local to web");
        Log.d("web", "sync, clear web tables");
        mRequestQueue.add(new MyStringRequest(
                Request.Method.DELETE,
                URL_BOOKS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("web", "sync, succeed clear web tables");

                        List<Book> bookList = dbOperate.getBooks(-1);
                        //删除本地所有标记为删除的书和章节
                        dbOperate.deleteAll();
                        for (final Book book : bookList) {

                            final boolean last = (bookList.indexOf(book) == response.length()-1);


                            dbOperate.setBookStatus(book.getUUID(), Constant.STATUS_ADD);
                            dbOperate.setChaptersStatus(book.getUUID(), Constant.STATUS_ADD);
                            //向服务器发送请求，新建一本书及所有章节,并修改本地书的uuid,章节的book_id,id
                            final List<Chapter> chapters = dbOperate.getChapters(book.getUUID());

                            JSONObject jsonObject = BookUtil.getBookJson(book, chapters);
                            Log.d("web", "insert a book to web:" + jsonObject);
                            mRequestQueue.add(new MyJsonObjectRequest(
                                    Request.Method.POST,
                                    URL_BOOK,
                                    jsonObject,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            //取得书籍的uuid,将本地书籍的uuid及章节的book_id,id改为和服务器一致
                                            Log.d("web", "succeed insert a book to web, detail:" + response);
                                            try {
                                                final String UUID = response.getString("uuid");

                                                dbOperate.resetBookUUID(book.getUUID(), UUID);
                                                JSONArray jsonArray = response.getJSONArray("chapters");
                                                for (int k = 0; k < jsonArray.length(); k++) {
                                                    JSONObject json = jsonArray.getJSONObject(k);
                                                    Chapter chapter = chapters.get(k);
                                                    int ID = json.getInt("id");
                                                    dbOperate.resetChapterID(
                                                            UUID, chapter.getId(), ID);
                                                    chapter.setId(ID);

                                                }

                                                //然后修改服务器书籍的类型及章节的类型
                                                String url = URL_BOOKS;
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
                                                mRequestQueue.add(new MyJsonObjectRequest(Request.Method.PUT, url, jsonObject, new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        Log.d("web", "succeed reset book type, so book ok:" + response);
                                                        dbOperate.setBookStatus(UUID, Constant.STATUS_OK);
                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Log.d("web", "fail reset book type:" + error);
                                                    }
                                                }));
                                                mRequestQueue.start();

                                                //修改在读书籍的章节类型
                                                if (book.getType() == Constant.TYPE_NOW) {
                                                    //改变在读书籍的章节类型
                                                    for (final Chapter chapter : chapters) {
                                                        if (chapter.getType() != Constant.TYPE_AFTER) {
                                                            map.clear();
                                                            String chapterUrl = URL_BOOKS + "/" + UUID + "/chapters/";
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
                                                            mRequestQueue.add(new MyJsonObjectRequest(Request.Method.PUT, chapterUrl,
                                                                    jsonObject,
                                                                    new Response.Listener<JSONObject>() {
                                                                        @Override
                                                                        public void onResponse(JSONObject response) {
                                                                            Log.d("web", "succeed reset a chapter type to web:" + response);
                                                                            dbOperate.setChapterStatus(UUID, chapter.getId(), Constant.STATUS_OK);
                                                                            if(last && chapters.indexOf(chapter) == chapters.size()-1)
                                                                            {
                                                                                Intent intent = new Intent("com.hustunique.myapplication.MAIN_RECEIVER");
                                                                                intent.putExtra("syncResult", true);
                                                                                intent.putExtra("info", "同步成功");
                                                                                mContext.sendBroadcast(intent);
                                                                            }
                                                                        }
                                                                    },
                                                                    new Response.ErrorListener() {
                                                                        @Override
                                                                        public void onErrorResponse(VolleyError error) {
                                                                            Log.d("web", "fail reset a chapter type to web:" + error);
                                                                            Intent intent = new Intent("com.hustunique.myapplication.MAIN_RECEIVER");
                                                                            intent.putExtra("syncResult", false);
                                                                            intent.putExtra("info", "章节同步未完成");
                                                                            mContext.sendBroadcast(intent);
                                                                        }
                                                                    }));
                                                            mRequestQueue.start();
                                                        }
                                                    }
                                                }
                                                else
                                                {
                                                    Intent intent = new Intent("com.hustunique.myapplication.MAIN_RECEIVER");
                                                    intent.putExtra("syncResult", true);
                                                    intent.putExtra("info", "同步成功");
                                                    mContext.sendBroadcast(intent);
                                                }


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("web", "fail insert a book to web:" + error);
                                            if(last) {
                                                Intent intent = new Intent("com.hustunique.myapplication.MAIN_RECEIVER");
                                                intent.putExtra("syncResult", false);
                                                intent.putExtra("info", "同步不完整,"
                                                        + dbOperate.getStatusBooks(Constant.STATUS_OK).size() + "/" + dbOperate.getBookNum() + "本书");
                                                mContext.sendBroadcast(intent);
                                            }

                                        }
                                    }));
                            mRequestQueue.start();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Intent intent = new Intent("com.hustunique.myapplication.MAIN_RECEIVER");
                        intent.putExtra("syncResult", false);
                        intent.putExtra("info", "同步失败");
                        mContext.sendBroadcast(intent);
                    }
                }));
        mRequestQueue.start();

    }
}
