package service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hustunique.myapplication.MyApplication;

import net.MyJsonArrayRequest;
import net.MyJsonObjectRequest;
import net.OkHttpStack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import data.Book;
import data.ChapterInfo;
import data.DBOperate;
import util.Constant;

/**
 * Created by taozhiheng on 15-7-26.
 */
public class Pull {

    private final static String URL_BOOKS = "http://pokebook.whitepanda.org:2333/api/v1/user/books";
    private final static String URL_BOOK = "http://pokebook.whitepanda.org:2333/api/v1/books";

    private RequestQueue mRequestQueue;
    private int mCMD;
    private Book mBook;
    private List<ChapterInfo> mChapterInfos;

    private Context mContext;

    public Pull(Context context, int command)
    {
        this.mContext = context;
        this.mCMD = command;
        this.mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack());
        mBook = new Book();
        mChapterInfos = new ArrayList<>();
    }

    //使用账户覆盖本地
    public void choseWeb()
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
                                        new Response.Listener<JSONObject>()
                                        {
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
                                                        mRequestQueue.add(new MyJsonArrayRequest(
                                                                URL_BOOKS + "/readings",
                                                                new Response.Listener<JSONArray>() {
                                                                    @Override
                                                                    public void onResponse(JSONArray response) {
                                                                        Log.d("web", "sync, succeed search all reading books info:" + response);
                                                                        Intent intent = new Intent("com.hustunique.myapplication.MAIN_RECEIVER");

                                                                        try {
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
                                                                                    if (typeString.contains("null"))
                                                                                        continue;
                                                                                    else
                                                                                        type = Integer.parseInt(typeString);
                                                                                    if (type != Constant.TYPE_AFTER)
                                                                                        dbOperate.setChapterType(uuid, id, type);
                                                                                }
                                                                            }
                                                                            if (dbOperate.getBookNum() == response.length()) {
                                                                                intent.putExtra("syncResult", true);
                                                                                intent.putExtra("info", "同步成功");
                                                                            } else {
                                                                                intent.putExtra("syncResult", false);
                                                                                intent.putExtra("info", "同步不完整");
                                                                            }

                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                            intent.putExtra("syncResult", false);
                                                                            intent.putExtra("info", "章节类型同步失败");
                                                                        } finally {
                                                                            mContext.sendBroadcast(intent);

                                                                        }
                                                                    }
                                                                },
                                                                new Response.ErrorListener() {
                                                                    @Override
                                                                    public void onErrorResponse(VolleyError error) {
                                                                        Log.d("web", "sync, fail search all reading books info" + error + "/" + error.getCause());
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
                                        },
                                        new Response.ErrorListener()
                                        {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.d("web", "sync, fail search a book detail "+error.getCause()+"/" + error.getNetworkTimeMs());
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
}
