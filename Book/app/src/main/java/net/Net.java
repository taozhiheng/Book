//package net;
//
//
//import android.os.Handler;
//import android.util.Log;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.hustunique.myapplication.MyApplication;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import adapter.BookRecyclerAdapter;
//import adapter.ReadingAdapter;
//import data.ChapterInfo;
//import util.Constant;
//import util.StringUtil;
//import util.TimeUtil;
//
///**
// * Created by taozhiheng on 15-7-16.
// *
// */
//public class Net {
//
//
//    //??
//    public static void resetNetBookType(RequestQueue requestQueue, NetBook book, int type)
//    {
//        String url = MyApplication.getUrlHead()+ "/api/v1/user/books/";
//        HashMap<String, String> map = new HashMap<>();
//        switch (type)
//        {
//            case Constant.BTYPE_AFTER:
//                url += "wishs/";
//                map.put("add_time",
//                        TimeUtil.getTimeString(System.currentTimeMillis(), Constant.TIME_FORMAT).replace('.', 'T')+".000000");
//                break;
//            case Constant.BTYPE_NOW:
//                url += "readings/";
//                map.put("start_time", book.getStartTimeStr());
//                map.put("end_time", book.getEndTimeStr());
//                break;
//            case Constant.BTYPE_BEFORE:
//                url += "reads/";
//                map.put("end_time",
//                        TimeUtil.getTimeString(System.currentTimeMillis(), Constant.TIME_FORMAT).replace('.', 'T')+".000000");
//                break;
//        }
//        url += book.getUuid();
//        JSONObject jsonObject = new JSONObject(map);
//        Log.d("net", "reset :"+jsonObject.toString());
//        requestQueue.add(new MyJsonObjectRequest(Request.Method.PUT, url, jsonObject, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d("net", "reset book type:" + response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("error", error.toString());
//            }
//        }));
//        requestQueue.start();
//    }
//
//    //颜色，封面暂不可改
//    //OK
//    public static void insertNetBook(final RequestQueue requestQueue, final NetBook book, List<ChapterInfo> chapters)
//    {
//        HashMap<String,Object> map = new HashMap<>();
//        map.put("isbn", "null");
//        map.put("title", book.getName());
//        map.put("creator", book.getAuthor());
//        if(book.getUrl() != null)
//            map.put("cover", book.getUrl());
//        else
//            map.put("cover", "null");
//        map.put("color", 0);
//        map.put("publisher", book.getPress());
//        map.put("words", book.getWordNum());
//
//        JSONArray jsonArray = new JSONArray();
//        HashMap<String, String> hashMap = new HashMap<>();
//        ChapterInfo chapter;
//        for(int i = 0; i < chapters.size(); i++)
//        {
//            chapter = chapters.get(i);
//            hashMap.put("id", ""+i);
//            hashMap.put("name", chapter.getName());
//            jsonArray.put(new JSONObject(hashMap));
//        }
//
//        map.put("chapters", jsonArray);
//        JSONObject json = new JSONObject(map);
//        Log.d("net", "insert book json:"+json.toString());
//        requestQueue.add(new MyJsonObjectRequest(
//                Request.Method.POST,
//                MyApplication.getUrlHead() + "/api/v1/books",
//                json,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("net", "insert book:" + response);
//                        try {
//                            String uuid = response.getString("uuid");
//                            book.setUuid(uuid);
//                            resetNetBookType(requestQueue, book, book.getType());
//                            Log.d("net", "insert book uuid" + book.getUuid()+" type:"+book.getType());
//                        }catch (JSONException e)
//                        {
//                            Log.d("net", e.toString());
//                        }
//                    }
//                },
//                null));
//        requestQueue.start();
//    }
//
//    //?
//    public static void deleteNetBook(RequestQueue requestQueue, NetBook book)
//    {
//        requestQueue.add(new MyJsonObjectRequest(
//                Request.Method.DELETE,
//                MyApplication.getUrlHead()+"/api/v1/books"+book.getUuid(),
//                null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("net", "delete book:"+response);
//                    }
//                },
//                null));
//        requestQueue.start();
//    }
//
//    public static void getBooks(final List<NetBook> bookList,
//                                final BookRecyclerAdapter adapter,
//                                final RequestQueue requestQueue,
//                                final int type)
//    {
//        String url = MyApplication.getUrlHead()+"/api/v1/user/books/";
//        switch (type)
//        {
//            case Constant.BTYPE_AFTER:
//                url += "wishs";
//                break;
//            case Constant.BTYPE_NOW:
//                url += "readings";
//                break;
//            case Constant.BTYPE_BEFORE:
//                url += "reads";
//                break;
//        }
//        final String urls = url;
//        final List<SI> idList = new ArrayList<>();
//        requestQueue.add(new MyJsonArrayRequest(
//                Request.Method.GET,
//                MyApplication.getUrlHead() + "/api/v1/user/chapters/readings",
//                null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        Log.d("net", "get reading chapters:"+response.toString());
//                        JSONObject json;
//                        try {
//                            for (int i = 0; i < response.length(); i++) {
//                                json = response.getJSONObject(i);
//                                String uuid = json.getString("book_uuid");
//                                int id = json.getInt("chapter_id");
//                                idList.add(new SI(uuid, id));
//                            }
//                            requestQueue.add(new MyJsonArrayRequest(
//                                    Request.Method.GET,
//                                    urls,
//                                    null,
//                                    new Response.Listener<JSONArray>() {
//                                        @Override
//                                        public void onResponse(JSONArray response) {
//                                            Log.d("net", "get books:"+response.toString());
//                                            JSONObject json;
//                                            for(int i = 0; i < response.length(); i++)
//                                            {
//                                                try {
//                                                    json = response.getJSONObject(i);
//                                                    final String uuid = json.getString("uuid");
//                                                    final String startTime;
//                                                    final String endTime;
//
//                                                    if(type == Constant.BTYPE_AFTER)
//                                                    {
//                                                        startTime = json.getString("add_time");
//                                                        endTime = null;
//                                                    }
//                                                    else
//                                                    {
//                                                        startTime =json.getString("start_time");
//                                                        endTime =json.getString("end_time");
//                                                    }
//                                                    final int unreadNum;
//                                                    if(type == Constant.BTYPE_NOW) {
//                                                        String unread = json.getString("unread_chapters");
//                                                        if(unread.length()<3)
//                                                            unreadNum = 0;
//                                                        else
//                                                            unreadNum = StringUtil.getSubStringCount(unread, ",")+1;
//                                                    }
//                                                    else if(type == Constant.BTYPE_BEFORE)
//                                                        unreadNum = 0;
//                                                    else
//                                                        unreadNum = -1;
//
//                                                    requestQueue.add(new MyJsonObjectRequest(
//                                                            Request.Method.GET,
//                                                            MyApplication.getUrlHead() + "/api/v1/books/" + uuid,
//                                                            null,
//                                                            new Response.Listener<JSONObject>() {
//                                                                @Override
//                                                                public void onResponse(JSONObject response) {
//                                                                    Log.d("net", "get book info:"+response.toString());
//                                                                    try {
//                                                                        String bookName = response.getString("title");
//                                                                        String author = response.getString("creator");
//                                                                        String press = response.getString("publisher");
//                                                                        String url = response.getString("cover");
//                                                                        int wordNum = response.getInt("words");
//                                                                        JSONArray jsonArray = response.getJSONArray("chapters");
//                                                                        JSONObject json;
//                                                                        int chapterNum = jsonArray.length();
//                                                                        int readingNum = 0;
//                                                                        for(int i = 0; i<jsonArray.length(); i++)
//                                                                        {
//                                                                            json = jsonArray.getJSONObject(i);
//                                                                            int id = json.getInt("id");
//                                                                            for(SI si : idList)
//                                                                            {
//                                                                                if(si.uuid.equals(uuid) && si.id == id)
//                                                                                {
//                                                                                    readingNum++;
//                                                                                    break;
//                                                                                }
//                                                                            }
//                                                                        }
//                                                                        int finishNum = (unreadNum<0)? 0:chapterNum-readingNum-unreadNum;
//                                                                        bookList.add(new NetBook(uuid, bookName, author,
//                                                                                press, url, finishNum,
//                                                                                chapterNum, wordNum, type, startTime, endTime));
//                                                                        adapter.notifyItemInserted(bookList.size()-1);
//                                                                    } catch (JSONException e) {
//                                                                        e.printStackTrace();
//                                                                    }
//                                                                }
//                                                            },
//                                                            null
//                                                    ));
//                                                    requestQueue.start();
//                                                }catch (JSONException e)
//                                                {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        }
//                                    },
//                                    null));
//                            requestQueue.start();
//                        }catch (JSONException e)
//                        {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                null));
//        requestQueue.start();
//
//    }
//
//    //httpStack
//    //颜色，封面暂不可改
//    public static void updateNetBook(RequestQueue requestQueue, NetBook book)
//    {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("title", book.getName());
//        map.put("creator", book.getAuthor());
//        map.put("publisher", book.getPress());
//        if(book.getUrl() != null)
//            map.put("cover", book.getUrl());
//        else
//            map.put("cover", "null");
//        map.put("words", String.valueOf(book.getWordNum()));
//        JSONObject json = new JSONObject(map);
//        requestQueue.add(new MyJsonObjectRequest(
//                Request.Method.PATCH,
//                MyApplication.getUrlHead()+"/api/v1/books/"+book.getUuid(),
//                json,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("net", "update book:"+response);
//                    }
//                },
//                null));
//        requestQueue.start();
//    }
//
//    public static void insertChapter(RequestQueue requestQueue,NetChapter chapter)
//    {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("name", chapter.getName());
//        JSONObject json = new JSONObject(map);
//        requestQueue.add(new MyJsonObjectRequest(
//                Request.Method.POST,
//                MyApplication.getUrlHead()+"/api/v1/books/"+chapter.getUUID()+"/chapters",
//                json,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("net", "insert chapter:"+response);
//                    }
//                },
//                null));
//        requestQueue.start();
//    }
//
//    public static void deleteNetChapter(RequestQueue requestQueue,NetChapter chapter)
//    {
//        requestQueue.add(new MyJsonObjectRequest(
//                Request.Method.DELETE,
//                MyApplication.getUrlHead()+"/api/v1/books/"+chapter.getUUID()+"/chapters"+chapter.getId(),
//                null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("net", "delete chapter:"+response);
//                    }
//                },
//                null));
//        requestQueue.start();
//    }
//
//    public static void getReadingNetChapters(
//            final List<NetChapter> chapterList,
//            final ReadingAdapter adapter,
//            final RequestQueue requestQueue)
//    {
//        requestQueue.add(new MyJsonArrayRequest(
//                Request.Method.GET,
//                MyApplication.getUrlHead()+"/api/v1/user/chapters/readings",
//                null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        Log.d("net", "get reading chapters:"+response.toString());
//                        JSONObject json;
//                        for(int i = 0; i < response.length(); i++)
//                        {
//                            try {
//                                json = response.getJSONObject(i);
//                                final String uuid = json.getString("book_uuid");
//                                final int id = json.getInt("chapter_id");
//                                final String[] bookName = new String[1];
//                                final String[] url = new String[1];
//                                requestQueue.add(new MyJsonObjectRequest(
//                                        Request.Method.GET,
//                                        MyApplication.getUrlHead() + "/api/v1/books/" + uuid,
//                                        null,
//                                        new Response.Listener<JSONObject>() {
//                                            @Override
//                                            public void onResponse(JSONObject response) {
//                                                try {
//                                                    bookName[0] = response.getString("title");
//                                                    url[0] = response.getString("cover");
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        },
//                                        null
//                                        ));
//                                requestQueue.add(new MyJsonObjectRequest(
//                                        Request.Method.GET,
//                                        MyApplication.getUrlHead() + "/api/v1/books/"+ uuid + "/chapters/" + id,
//                                        null,
//                                        new Response.Listener<JSONObject>() {
//                                            @Override
//                                            public void onResponse(JSONObject response) {
//                                                Log.d("net", "chapter info:"+response);
//                                                try {
//                                                    String name = response.getString("name");
//                                                    chapterList.add(
//                                                            new NetChapter(uuid, id,
//                                                                    bookName[0], name,
//                                                                    Constant.CTYPE_NOW,url[0]));
//                                                    adapter.notifyDataSetChanged();
//                                                }catch (JSONException e)
//                                                {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        },
//                                        null));
//                                requestQueue.start();
//                            }catch (JSONException e)
//                            {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                }));
//        requestQueue.start();
//    }
//
//    static class SI
//    {
//        String uuid;
//        int id;
//        public SI(String uuid, int id)
//        {
//            this.uuid = uuid;
//            this.id = id;
//        }
//    }
//
//    public static void getNetChapters(
//            final List<NetChapter> chapterList,
//            final Handler handler,
//            final RequestQueue requestQueue,
//            final String uuid)
//    {
//        final List<SI> idList = new ArrayList<>();
//        requestQueue.add(new MyJsonArrayRequest(
//                        Request.Method.GET,
//                        MyApplication.getUrlHead() + "/api/v1/user/chapters/readings",
//                        null,
//                        new Response.Listener<JSONArray>() {
//                            @Override
//                            public void onResponse(JSONArray response) {
//                                Log.d("net", "get chapter reading:"+response);
//                                JSONObject json;
//                                try {
//                                    for (int i = 0; i < response.length(); i++) {
//                                        json = response.getJSONObject(i);
//                                        String uuid = json.getString("book_uuid");
//                                        int id = json.getInt("chapter_id");
//                                        idList.add(new SI(uuid, id));
//                                    }
//                                }catch (JSONException e)
//                                {
//                                    e.printStackTrace();
//                                }
//                            }
//                        },
//                        null));
//        requestQueue.add(new JsonObjectRequest(
//                Request.Method.GET,
//                MyApplication.getUrlHead() + "/api/v1/books/" + uuid,
//                null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("net", "get book info:"+response);
//                        try {
//                            JSONArray jsonArray = response.getJSONArray("chapters");
//                            JSONObject json;
//                            for(int i = 0; i < jsonArray.length(); i++)
//                            {
//                                json = jsonArray.getJSONObject(i);
//                                int id = json.getInt("id");
//                                String name = json.getString("name");
//                                int type = Constant.CTYPE_AFTER;
//                                for(SI si : idList)
//                                {
//                                    if(si.uuid.equals(uuid) && si.id == id)
//                                    {
//                                        type = Constant.CTYPE_NOW;
//                                        break;
//                                    }
//                                }
//                                chapterList.add(new NetChapter(uuid, id, null, name, type, null));
////                                if(adapter != null)
////                                    adapter.notifyDataSetChanged();
//                                if(handler != null)
//                                    handler.sendEmptyMessage(0);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Log.d("net", "error:"+e.toString());
//                        }
//                    }
//                },
//                null
//        ));
//        requestQueue.start();
//    }
//
//    public static void resetNetChapterType(RequestQueue requestQueue, NetChapter chapter, int type)
//    {
//        String url = MyApplication.getUrlHead()+ "/api/v1/user/books/"+chapter.getUUID()+"/chapters/";
//        switch (type)
//        {
//            case Constant.CTYPE_AFTER:
//                url += "wishs/";
//                break;
//            case Constant.CTYPE_NOW:
//                url += "readings/";
//                break;
//            case Constant.CTYPE_FINISH:
//                url += "reads/";
//                break;
//        }
//        url += chapter.getId();
//        requestQueue.add(new MyStringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("net", "reset chapter type:"+response);
//            }
//        }, null));
//        requestQueue.start();
//    }
//
//    //需用httpStack
//    public static void updateNetChapter(RequestQueue requestQueue, NetChapter chapter)
//    {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("name", chapter.getName());
//        JSONObject json = new JSONObject(map);
//        requestQueue.add(new MyJsonObjectRequest(
//                Request.Method.PATCH,
//                MyApplication.getUrlHead()+"/api/v1/books/"+chapter.getUUID()+"/chapters"+chapter.getId(),
//                json,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("net", "update chapter:"+response);
//                    }
//                },
//                null));
//        requestQueue.start();
//    }
//}
