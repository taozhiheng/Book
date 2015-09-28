//package service;
//
//import android.os.AsyncTask;
//import android.os.Handler;
//import android.util.Log;
//
//import com.android.volley.Response;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import data.Book;
//import data.ChapterInfo;
//import util.Constant;
//
///**
// * Created by taozhiheng on 15-7-26.
// *
// */
//public class WriteBookResponse implements Response.Listener<JSONObject> {
//
//    private int type;
//    private List<Integer> mTypeList;
//    private Counter mCounter;
//
//    public WriteBookResponse(Counter counter, int type)
//    {
//        this.mCounter = counter;
//        this.type = type;
//    }
//
//    public WriteBookResponse(Counter counter, int type, List<Integer> typeList)
//    {
//        this.mCounter = counter;
//        this.type = type;
//        this.mTypeList = typeList;
//    }
//
//    @Override
//    public void onResponse(JSONObject response) {
//        mCounter.decrease();
//        Log.d("web", "book detail response:"+response);
//        try {
//            //读入书籍信息
//            Book book = new Book();
//            book.setUUID(response.getString("uuid"));
//            book.setIsbn(response.getString("isbn"));
//            book.setName(response.getString("title"));
//            book.setAuthor(response.getString("creator"));
//            book.setPress(response.getString("publisher"));
//            book.setColor(0);
//            book.setWordNum(response.getLong("words"));
//            book.setUrl(response.getString("cover"));
//            book.setType(type);
//            book.setStatus(Constant.STATUS_OK);
//            //读入章节信息
//            List<ChapterInfo> chapterInfos = new ArrayList<>();
//            JSONArray chapters = response.getJSONArray("chapters");
//            for (int j = 0; j < chapters.length(); j++) {
//                JSONObject chapter = chapters.getJSONObject(j);
//                ChapterInfo chapterInfo = new ChapterInfo(chapter.getInt("id"), chapter.getString("name"));
//                if(mTypeList != null)
//                    chapterInfo.setType(mTypeList.get(j));
//                chapterInfos.add(chapterInfo);
//            }
//            //将一本书完整插入本地，但是章节全都是未读状态，待完善
//            new WriteTask(book, chapterInfos).execute();
//            mCounter.addBookFinishNum(1);
//            mCounter.addChapterNum(chapterInfos.size());
//            Log.d("web", "sync, write a book to local");
//        }catch (JSONException e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//
//}
