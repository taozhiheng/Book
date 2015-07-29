package web;

import android.app.DownloadManager;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarException;

import data.Book;
import data.Chapter;
import data.ChapterInfo;
import data.DBOperate;
import service.BookUtil;
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

    //异步插入一本书，插入成功后将本地修改为一致的，并改变标记
    public static void insertBook(String auth, String url, final Book book)
    {
        //应该异步
        final List<Chapter> chapters = mDBOperate.getChapters(book.getUUID());
        JSONObject bookInfo = BookUtil.getBookJson(book, chapters);

        RequestBody requestBody =RequestBody.create(MEDIA_TYPE_MARKDOWN, bookInfo.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                try
                {
                    //应该异步
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String UUID = jsonObject.getString("uuid");
                    //重新设置本地数据库这本书的uuid，以及所有章节的book_id, 暂时标记为status_mod
                    mDBOperate.resetBookUUID(book.getUUID(), UUID);
                    mDBOperate.setBookStatus(UUID, Constant.STATUS_OK);
                    //从反馈中取得所有章节的id,重新设置本地数据库这本书所有章节的book_id, id, status=status_mod
                    for (int k = chapters.size(); k > 0; k--) {
                        Chapter chapter = chapters.get(k-1);
                        mDBOperate.resetChapterID(
                                UUID, chapter.getId(), k);
                        chapter.setBookId(UUID);
                    }
                    mDBOperate.setChaptersStatus(UUID, Constant.STATUS_OK);
                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    //异步修改书的基本信息，修改成功后改变标记
    public void modifyOnlyBook(String auth, String url, final Book book)
    {
        //异步
        JSONObject bookInfo = BookUtil.getOnlyBookJson(book);
        RequestBody requestBody =RequestBody.create(MEDIA_TYPE_MARKDOWN, bookInfo.toString());
        final Request request = new Request.Builder()
                .url(url)
                .patch(requestBody)
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //异步
                mDBOperate.setBookStatus(book.getUUID(), Constant.STATUS_OK);
                }
        });
    }

    //异步修改书的所有信息，修改成功后改变标记，并重新设置本地章节id
    public static void modifyBook(String auth, String url, final Book book)
    {
        //异步
        final List<Chapter> chapters = mDBOperate.getChapters(book.getUUID());
        JSONObject bookInfo = BookUtil.getBookJson(book, chapters);
        RequestBody requestBody =RequestBody.create(MEDIA_TYPE_MARKDOWN, bookInfo.toString());
        Request request = new Request.Builder()
                .url(url)
                .patch(requestBody)
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                //异步
                mDBOperate.setBookStatus(book.getUUID(), Constant.STATUS_OK);
                for (int k = chapters.size(); k > 0; k--)
                {
                    Chapter chapter = chapters.get(k-1);
                    mDBOperate.resetChapterID(book.getUUID(), chapter.getId(), k);
                }
                mDBOperate.setChaptersStatus(book.getUUID(), Constant.STATUS_OK);
            }
        });
    }

    //异步删除一本书,成功后删除本地书籍
    public static void deletBook(String auth, String url, final String uuid)
    {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                mDBOperate.deleteBook(uuid);
            }
        });
    }

    //异步标记一本书为未读,成功后修改标记
    public static void setBookAfter(String auth, String url, final String uuid)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("add_time",
                TimeUtil.getNeedTime(System.currentTimeMillis()));
        JSONObject jsonObject = new JSONObject(map);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                mDBOperate.setBookStatus(uuid, Constant.STATUS_OK);
                mDBOperate.setChaptersStatus(uuid, Constant.STATUS_OK);
            }
        });
    }

    public static void setBookNow(String auth, String url, final Book book)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        String start = book.getStartTime();
        String end = book.getEndTime();

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
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                mDBOperate.setBookStatus(book.getUUID(), Constant.STATUS_OK);
            }
        });
    }

    public static void setBookBefore(String auth, String url, final String uuid)
    {
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
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                mDBOperate.setBookStatus(uuid, Constant.STATUS_OK);
                mDBOperate.setChaptersStatus(uuid, Constant.STATUS_OK);
            }
        });
    }

    public static void insertChapter(String auth, String url, final String uuid, final int id, String name)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", name);
        JSONObject jsonObject = new JSONObject(map);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", auth)
                .post(requestBody)
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                mDBOperate.setChapterStatus(uuid, id, Constant.STATUS_OK);
            }
        });
    }

    public static void modifyChapter(String auth, String url, final String uuid, final int id, String name)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", name);
        JSONObject jsonObject = new JSONObject(map);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", auth)
                .patch(requestBody)
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                mDBOperate.setChapterStatus(uuid, id, Constant.STATUS_OK);
            }
        });
    }

    public static void deleteChapter(String auth, String url, final String uuid, final int id)
    {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", auth)
                .delete()
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                mDBOperate.deleteChapter(uuid, id);
            }
        });
    }

    public static void setChapterAfter(String auth, String url, final String uuid, final int id)
    {
        Request request = new Request.Builder()
                .url(url)
                .put(null)
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                mDBOperate.setChapterStatus(uuid, id, Constant.STATUS_OK);
            }
        });
    }

    public static void setChapterNow(String auth, String url, final String uuid, final int id)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("start_time",
                TimeUtil.getNeedTime(System.currentTimeMillis()));
        JSONObject jsonObject = new JSONObject(map);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .addHeader("Authorization", auth)
                .build();
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                mDBOperate.setChapterStatus(uuid, id, Constant.STATUS_OK);
            }
        });
    }

    public static void setChapterBefore(String auth, String url, final String uuid, final int id)
    {
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
        OkHttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                mDBOperate.setChapterStatus(uuid, id, Constant.STATUS_OK);
            }
        });
    }
}
