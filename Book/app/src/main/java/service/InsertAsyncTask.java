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
 * Insert a book to web server,and reset the id of local database
 */
public class InsertAsyncTask extends AsyncTask<Void, Integer, Void>{

    private final static String URL_BOOKS = "http://pokebook.whitepanda.org:2333/api/v1/user/books";
    private final static String URL_BOOK = "http://pokebook.whitepanda.org:2333/api/v1/books";

    private RequestQueue mRequestQueue;
    private Context mContext;


    public InsertAsyncTask(Context context)
    {
        this.mContext = context;
        this.mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack());
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        new DeleteAsyncTask(mContext).execute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        insertBooks();
        return null;
    }

    //1插入所有待插入的书籍及其附属的章节，然后从服务器的反馈中提取uuid, id修改本地数据库，同时标记状态status_od
    private void insertBooks() {
        final DBOperate dbOperate = MyApplication.getDBOperateInstance();
        //取得所有标记为status_add的书籍
        List<Book> books = dbOperate.getStatusBooks(Constant.STATUS_ADD);
        //遍历打印出来
        for (Book book : books)
            Log.d("web", "update will insert book:" + book.getName() + book.getType() + " " + book.getUUID());
        //遍历处理每一本可能添加的书
        for (final Book book : books) {
            //查询此书是否存在
            mRequestQueue.add(new MyJsonObjectRequest(
                    Request.Method.GET,
                    URL_BOOK + "/" + book.getUUID(),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //书籍已存在,将其标记为status_mod状态，等待修改操作
                            dbOperate.setBookStatus(book.getUUID(), Constant.STATUS_MOD);
                        }
                    },
                    //插入一本书
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //书籍不存在，添加

                            //获取所有此书未删除的章节
                            final List<Chapter> chapters = dbOperate.getChapters(book.getUUID());
                            //将书和章节封装成一个json对象
                            JSONObject jsonObject = BookUtil.getBookJson(book, chapters);
                            Log.d("web", "insert a book to web:" + jsonObject);
                            //发送post请求，向服务器插入一本书
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
                                                //插入书籍成功

                                                //从反馈中取得uuid
                                                final String UUID = response.getString("uuid");
                                                //重新设置本地数据库这本书的uuid，暂时标记为status_mod
                                                dbOperate.resetBookUUID(book.getUUID(), UUID);
                                                dbOperate.setBookStatus(UUID, Constant.STATUS_MOD);
                                                //从反馈中取得所有章节的id,重新设置本地数据库这本书所有章节的book_id, id, status=status_mod
                                                JSONArray jsonArray = response.getJSONArray("chapters");
                                                for (int k = 0; k < jsonArray.length(); k++) {
                                                    JSONObject json = jsonArray.getJSONObject(k);
                                                    Chapter chapter = chapters.get(k);
                                                    int ID = json.getInt("id");
                                                    dbOperate.resetChapterID(
                                                            UUID, chapter.getId(), ID);
                                                    chapter.setBookId(UUID);
                                                    chapter.setId(ID);
                                                }
                                                dbOperate.setChaptersStatus(UUID, Constant.STATUS_MOD);


                                                //然后修改服务器书籍的类型及章节的类型

                                                //根据书的类型设置url及参数
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


                                                //发送put请求，修改书籍类型
                                                Log.d("web", "insert reset book type book:"+book.getName()+" type:" + book.getType() + " to web:" + jsonObject.toString());
                                                mRequestQueue.add(new MyJsonObjectRequest(Request.Method.PUT, url, jsonObject, new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        Log.d("web", "succeed reset book type, so set book ok:" + response);
                                                        //修改成功，设置书籍状态status_ok
                                                        dbOperate.setBookStatus(UUID, Constant.STATUS_OK);
                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        //修改失败，书籍状态仍未status_mod
                                                        Log.d("web", "fail reset book type:" + error);
                                                    }
                                                }));
                                                mRequestQueue.start();

                                                //如果书籍是在读书籍，遍历修改在读书籍的章节类型
                                                if (book.getType() == Constant.TYPE_NOW) {
                                                    //改变在读书籍的章节类型
                                                    for (final Chapter chapter : chapters) {
                                                        if (chapter.getType() != Constant.TYPE_AFTER) {
                                                            map.clear();
                                                            String chapterUrl = URL_BOOKS + "/" + UUID + "/chapters/";
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
                                                                            dbOperate.setChapterStatus(UUID, chapter.getId(), Constant.STATUS_OK);
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
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("web", "fail insert a book to web:"
                                                    + error);
                                        }
                                    }));
                            mRequestQueue.start();
                        }
                    }
            ));
            mRequestQueue.start();
        }
    }
}
