package service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hustunique.myapplication.MyApplication;
import net.MyJsonObjectRequest;
import net.OkHttpStack;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import data.Book;
import data.Chapter;
import data.DBOperate;
import util.Constant;
import util.TimeUtil;

/**
 * Created by taozhiheng on 15-7-25.
 *
 */
public class ModifyAsyncTask extends AsyncTask<Void, Integer, Void> {

    private final static String URL_BOOKS = "http://pokebook.whitepanda.org:2333/api/v1/user/books";
    private final static String URL_BOOK = "http://pokebook.whitepanda.org:2333/api/v1/books";

    private RequestQueue mRequestQueue;
    private Context mContext;


    public ModifyAsyncTask(Context context)
    {
        this.mContext = context;
        this.mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack());
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... params) {
        modifyBooks();
        return null;
    }

    //2修改所有已插入待修改的书籍，同时重新设置章节信息（覆盖方式），修改所有章节id
    private void modifyBooks() {
        final DBOperate dbOperate = MyApplication.getDBOperateInstance();

        //获得所有标记为status_mod的书籍
        List<Book> books = dbOperate.getStatusBooks(Constant.STATUS_MOD);

        //若书籍未修改，只是改变了章节类型
        if(books.size() == 0)
        {
            List<Chapter> chapters = dbOperate.getStatusChapters(Constant.STATUS_MOD);
            for (final Chapter chapter : chapters) {
                HashMap<String, String> map = new HashMap<>();
                if (chapter.getType() != Constant.TYPE_AFTER) {
                    String chapterUrl = URL_BOOKS + "/" + chapter.getBookId() + "/chapters/";
                    //章节类型为在读和已读时进行修改，设置不同的url和参数
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
                    JSONObject jsonObject = new JSONObject(map);
                    Log.d("web", "reset a chapter type to web:" + jsonObject);
                    //发送put请求，修改章节的类型
                    mRequestQueue.add(new MyJsonObjectRequest(Request.Method.PUT, chapterUrl,
                            jsonObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("web", "succeed reset a chapter type to web:" + response);
                                    //修改章节类型成功，设置其状态为status_ok
                                    dbOperate.setChapterStatus(chapter.getBookId(), chapter.getId(), Constant.STATUS_OK);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //修改章节类型失败，状态仍然为status_mod
                                    Log.d("web", "fail reset a chapter type to web:" + error);
                                }
                            }));
                    mRequestQueue.start();
                }
            }
            return;
        }
        //遍历，打印所有书籍信息
        for (Book book : books)
            Log.d("web", "update will modify book:" + book.getName() + " " + book.getUUID());
        //遍历，逐一处理每本书的修改操作
        for (final Book book : books) {

            //如果书的uuid没有32位，则属于未添加的书籍
            if (book.getUUID().length() < 32) {
                dbOperate.setBookStatus(book.getUUID(), Constant.STATUS_ADD);
                continue;
            }

            //查询此书是否存在
            mRequestQueue.add(new MyJsonObjectRequest(
                    Request.Method.GET,
                    URL_BOOK + "/" + book.getUUID(),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //书籍已存在,开始修改操作
                            Log.d("web", "update, start modify a book");

                            //录入书籍基本信息，得到一个json对象
                            //得到所有未删除章节
                            final List<Chapter> chapters = dbOperate.getChapters(book.getUUID());
                            //将书和章节封装成一个json对象
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("isbn", book.getIsbn());
                            map.put("title", book.getName());
                            map.put("creator", book.getAuthor());
                            map.put("publisher", book.getPress());
                            map.put("cover", book.getUrl());
                            map.put("color", "0");
                            map.put("words", String.valueOf(book.getWordNum()));

                            JSONArray chaptersArray = new JSONArray();
                            HashMap<String, String> chapterMap = new HashMap<>();
                            for(Chapter chapter : chapters)
                            {
                                chapterMap.put("name", chapter.getName());
                                chaptersArray.put(new JSONObject(chapterMap));
                            }
                            map.put("chapters", chapters);
                            JSONObject json = new JSONObject(map);

                            Log.d("web", "update, update a book basic info to web");
                            //发送patch请求，修改书籍基本信息
                            mRequestQueue.add(new MyJsonObjectRequest(
                                    Request.Method.PATCH,
                                    URL_BOOK + "/" + book.getUUID(),
                                    json,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Log.d("web", "update,succeed update book basic info:" + response);
                                            dbOperate.deleteChapters(book.getUUID());
                                            dbOperate.setBookStatus(book.getUUID(), Constant.STATUS_MOD);
                                            //从反馈中取得所有章节的id,重新设置本地数据库这本书所有章节的book_id, id, status=status_mod
                                            try {
                                                JSONArray jsonArray = response.getJSONArray("chapters");
                                                for (int k = 0; k < jsonArray.length(); k++) {
                                                    JSONObject json = jsonArray.getJSONObject(k);
                                                    Chapter chapter = chapters.get(k);
                                                    int ID = json.getInt("id");
                                                    dbOperate.resetChapterID(
                                                            book.getUUID(), chapter.getId(), ID);
                                                    chapter.setBookId(book.getUUID());
                                                    chapter.setId(ID);
                                                }
                                                dbOperate.setChaptersStatus(book.getUUID(), Constant.STATUS_MOD);
                                            }catch (JSONException e)
                                            {
                                                e.printStackTrace();
                                            }

                                            //修改基本信息成功，开始修改其类型
                                            //根据类型设置url和参数json
                                            HashMap<String, String> map = new HashMap<String, String>();
                                            String url = URL_BOOKS;
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
                                                        end = TimeUtil.getNeedTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
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
                                            url += book.getUUID();
                                            JSONObject jsonObject = new JSONObject(map);

                                            Log.d("web", "modify reset book type book:"+book.getName()+" type:" + book.getType() + " to web:" + jsonObject.toString());

                                            //发送put请求，修改书籍的类型
                                            mRequestQueue.add(new MyJsonObjectRequest(Request.Method.PUT, url, jsonObject, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    Log.d("web", "succeed reset book type, so set book ok:" + response);
                                                    //修改基本信息和类型都成功，将其状态设置为status_ok
                                                    dbOperate.setBookStatus(book.getUUID(), Constant.STATUS_OK);
                                                }
                                            },
                                                    new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            //修改类型失败，暂时不做处理
                                                            Log.d("web", "update,fail update book type:" + error);
                                                        }
                                                    }));
                                            mRequestQueue.start();

                                            //如果书籍是在读书籍，遍历修改在读书籍的章节类型
                                            if (book.getType() == Constant.TYPE_NOW) {
                                                //改变在读书籍的章节类型
                                                for (final Chapter chapter : chapters) {
                                                    if (chapter.getType() != Constant.TYPE_AFTER) {
                                                        map.clear();
                                                        String chapterUrl = URL_BOOKS + "/" + book.getUUID() + "/chapters/";
                                                        //章节类型为在读和已读时进行修改，设置不同的url和参数
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
                                                        //发送put请求，修改章节的类型
                                                        mRequestQueue.add(new MyJsonObjectRequest(Request.Method.PUT, chapterUrl,
                                                                jsonObject,
                                                                new Response.Listener<JSONObject>() {
                                                                    @Override
                                                                    public void onResponse(JSONObject response) {
                                                                        Log.d("web", "succeed reset a chapter type to web:" + response);
                                                                        //修改章节类型成功，设置其状态为status_ok
                                                                        dbOperate.setChapterStatus(book.getUUID(), chapter.getId(), Constant.STATUS_OK);
                                                                    }
                                                                },
                                                                new Response.ErrorListener() {
                                                                    @Override
                                                                    public void onErrorResponse(VolleyError error) {
                                                                        //修改章节类型失败，状态仍然为status_mod
                                                                        Log.d("web", "fail reset a chapter type to web:" + error);
                                                                    }
                                                                }));
                                                        mRequestQueue.start();
                                                    }
                                                }
                                            }


                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //修改基本信息失败，暂时不做处理
                                    Log.d("web", "update,fail update book basic info:" + error);
                                }
                            }));
                            mRequestQueue.start();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //书籍不存在，暂时不做处理
                            Log.d("web", "update, modify book not exist");
                        }
                    }
            ));
            mRequestQueue.start();

        }
    }
}
