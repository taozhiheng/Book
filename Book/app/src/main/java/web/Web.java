package web;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import data.Book;
import data.Chapter;
import data.DBOperate;
import data.TimeInfo;
import service.BookUtil;
import service.Counter;
import util.Constant;
import util.TimeUtil;

/**
 * Created by taozhiheng on 15-7-28.
 *
 */
public class Web {

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    public static DBOperate mDBOperate;

    public static void setDbOperate(DBOperate dbOperate)
    {
        mDBOperate = dbOperate;
    }

    public static class MyTask extends AsyncTask<Book, Integer, JSONObject>
    {
        private List<Chapter> mChapters;
        private String mAuth;
        private String mUrl;
        private Book book;
        private Update.InsertCall mCallback;

        public MyTask(String auth, String url, Update.InsertCall callback)
        {
            this.mAuth = auth;
            this.mUrl = url;
            this.mCallback = callback;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject != null) {
                mCallback.addChapters(mChapters);
                insertBook(mAuth, mUrl, jsonObject, mCallback);
            }
        }

        @Override
        protected JSONObject doInBackground(Book... params) {
            //应该异步
            book = params[0];
            if(book.getUUID() != null && book.getUUID().length() == 32) {
                mDBOperate.setBookStatus(book.getId(), Constant.STATUS_MOD);
                return null;
            }
            mChapters = mDBOperate.getChapters(book.getId());
            return BookUtil.getBookJson(book, mChapters);
        }
    }

    //同步查询一本书是否存在
    public static boolean queryBookExist(String url)
    {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            return OkHttpUtil.execute(request).isSuccessful();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    //先查询此书是否存在，不存在则异步插入一本书，插入成功后将本地修改为一致的，并改变标记
    public static void insertBook(String auth, String url,
                                   JSONObject bookInfo, Callback callback)
    {
        Log.d("web", "start insert a book:"+bookInfo.toString());
        RequestBody requestBody =RequestBody.create(MEDIA_TYPE_MARKDOWN, bookInfo.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, callback);
    }

    //异步修改书的基本信息，修改成功后改变标记
    public static void modifyOnlyBook(String auth, String url, final Book book, Callback callback)
    {
        Log.d("web", "start modify a book");
        //异步
        JSONObject bookInfo = BookUtil.getOnlyBookJson(book);
        RequestBody requestBody =RequestBody.create(MEDIA_TYPE_MARKDOWN, bookInfo.toString());
        final Request request = new Request.Builder()
                .url(url)
                .patch(requestBody)
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, callback);
    }



    //异步删除一本书,成功后删除本地书籍
    public static void deleteBook(String auth, String url, Callback callback)
    {
        Log.d("web", "start delete a book:"+url);
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, callback);
    }

    //异步标记一本书为未读,成功后修改标记
    public static void setBookAfter(String auth, String url, Callback callback)
    {
        Log.d("web", "start set a book after:"+url);
        HashMap<String, String> map = new HashMap<>();
        map.put("add_time",
                TimeUtil.getNeedTime(System.currentTimeMillis()));
        JSONObject jsonObject = new JSONObject(map);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, callback);
    }

    //异步标记一本书在读，成功后修改标记
    public static void setBookNow(String auth, String url, String start, String end, Callback callback)
    {
        Log.d("web", "start set a book now:"+url);
        HashMap<String, String> map = new HashMap<>();

        if (start == null || end == null || end.compareTo(start) <= 0) {
            start = TimeUtil.getNeedTime(System.currentTimeMillis());
            end = TimeUtil.getNeedTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        }
        map.put("start_time", start);
        map.put("end_time", end);
        JSONObject jsonObject = new JSONObject(map);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, callback);
    }

    //异步标记一本书已读，成功后修改标记
    public static void setBookBefore(String auth, String url, Callback callback)
    {
        Log.d("web", "start set a book before:" + url);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("end_time",
                TimeUtil.getNeedTime(System.currentTimeMillis()));
        JSONObject jsonObject = new JSONObject(map);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, callback);
    }

    //在已存在服务器切未被删除的书中，异步插入一个章节，成功后修改标记
    public static void insertChapter(String auth, String url, final Chapter chapter, Callback callback)
    {
        Log.d("web", "start insert a chapter");

        if(chapter.getWebBookId()!= null && chapter.getWebBookId().length() == 32 && chapter.getId() > 0)
        {
            mDBOperate.setChapterStatus(chapter.getId(), Constant.STATUS_MOD);
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("name", chapter.getName());
        JSONObject jsonObject = new JSONObject(map);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, jsonObject.toString());
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", auth)
                .post(requestBody)
                .build();
        OkHttpUtil.enqueue(request, callback);
    }

    //在已存在服务器切未被删除的书中，异步修改一个章节，成功后修改标记
    public static void modifyChapter(String auth, String url, String name, Callback callback)
    {
        Log.d("web", "start modify a chapter");
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        JSONObject jsonObject = new JSONObject(map);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", auth)
                .patch(requestBody)
                .build();
        OkHttpUtil.enqueue(request, callback);
    }

    //在已存在服务器切未被删除的书中，异步删除一个章节，成功后删除
    public static void deleteChapter(String auth, String url, Callback callback)
    {
        Log.d("web", "start delete a chapter");

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", auth)
                .delete()
                .build();
        OkHttpUtil.enqueue(request,callback);
    }

    //异步标记一章节未读，成功后修改标记
    public static void setChapterAfter(String auth, String url, Callback callback)
    {
        Log.d("web", "start set a chapter after:"+url);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, "{\"status\":0}");
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, callback);
    }

    //异步标记一章节在读，成功后修改标记
    public static void setChapterNowOrRepeat(String auth, String url, Callback callback)
    {
        Log.d("web", "start set a chapter now:"+url);

        HashMap<String, String> map = new HashMap<>();
        map.put("start_time",
                TimeUtil.getNeedTime(System.currentTimeMillis()));
        JSONObject jsonObject = new JSONObject(map);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, callback);
    }

    //异步标记一章节已读，成功后修改标记
    public static void setChapterBefore(String auth, String url, Callback callback)
    {
        Log.d("web", "start set a chapter before:"+url);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("end_time",
                TimeUtil.getNeedTime(System.currentTimeMillis()));
        JSONObject jsonObject = new JSONObject(map);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, callback);
    }


    //同步查找所有未读或已读书籍,返回uuid集合
    public static List<String> queryWishOrRead(String auth, String url)
    {
        List<String> uuids = new ArrayList<>();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", auth)
                .build();
        try {
            Response response = OkHttpUtil.execute(request);
            if(response.isSuccessful())
            {
                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        uuids.add(jsonObject.getString("uuid"));
                    }
                    return uuids;
                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return uuids;
    }

    //同步查找所有在读书籍，返回uuid集合和type集合
    public static HashMap<String,List<Integer>> queryReading(String auth, String url, List<TimeInfo> timeList)
    {
        HashMap<String,List<Integer>> hashMap = new HashMap<>();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", auth)
                .build();
        try {
            Response response = OkHttpUtil.execute(request);
            if(response.isSuccessful())
            {
                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String uuid = jsonObject.getString("uuid");
                        timeList.add(new TimeInfo(
                                        jsonObject.getString("start_time"),
                                        jsonObject.getString("end_time")));

                        List<Integer> typeList = new ArrayList<>();
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
                            typeList.add(type);
                        }
                        hashMap.put(uuid, typeList);
                    }
                    return hashMap;
                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return hashMap;
    }

    //异步查找书籍
    public static void getBook(String auth, String url, Callback callback)
    {
        Request request = new Request.Builder()
               .url(url)
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, callback);
    }

    //同步清楚服务器数据
    public static boolean clearWeb(String auth, String url)
    {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", auth)
                .delete()
                .build();
        try {
            return OkHttpUtil.execute(request).isSuccessful();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static void queryWords(String url, Callback callback)
    {
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpUtil.enqueue(request, callback);
    }
}
