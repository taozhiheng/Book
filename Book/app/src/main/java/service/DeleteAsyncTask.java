//package service;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.Volley;
//import com.hustunique.myapplication.MyApplication;
//
//import net.MyJsonObjectRequest;
//import net.OkHttpStack;
//
//import org.json.JSONObject;
//
//import java.util.List;
//
//import data.Book;
//import data.DBOperate;
//import util.Constant;
//
///**
// * Created by taozhiheng on 15-7-25.
// * Delete book from local database, and notify web server to do the same change
// */
//public class DeleteAsyncTask extends AsyncTask<Void, Integer, Void> {
//
//
//
//    private RequestQueue mRequestQueue;
//    private Context mContext;
//
//
//    public DeleteAsyncTask(Context context)
//    {
//        this.mContext = context;
//        this.mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack());
//    }
//
//    @Override
//    protected void onPostExecute(Void aVoid) {
//        super.onPostExecute(aVoid);
//        new ModifyAsyncTask(mContext).execute();
//    }
//
//    @Override
//    protected Void doInBackground(Void... params) {
//        deleteBooks();
//        return null;
//    }
//
//    //3删除所有待删除的书籍和章节
//    private void deleteBooks() {
//        final DBOperate dbOperate = MyApplication.getDBOperateInstance();
//
//        //获得所有标记status_del的书籍
//        List<Book> books = dbOperate.getStatusBooks(Constant.STATUS_DEL);
//        //遍历，打印
//        for (Book book : books)
//            Log.d("web", "update will delete book:" + book.getName() + " " + book.getUUID());
//        //逐一处理每一本书
//        for (final Book book : books) {
//
//            //若书籍uuid小于32位，即在服务器上没有，直接删除
//            if (book.getUUID().length() < 32) {
//                dbOperate.deleteBook(book.getUUID());
//                continue;
//            }
//
//            //查询此书是否存在
//            mRequestQueue.add(new MyJsonObjectRequest(
//                    Request.Method.GET,
//                    Constant.URL_BOOK + "/" + book.getUUID(),
//                    null,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            //存在，开始删除服务器数据
//                            Log.d("web", "update, delete a book to web");
//                            mRequestQueue.add(new MyJsonObjectRequest(
//                                    Request.Method.DELETE,
//                                    Constant.URL_BOOK + "/" + book.getUUID(),
//                                    null,
//                                    new Response.Listener<JSONObject>() {
//                                        @Override
//                                        public void onResponse(JSONObject response) {
//                                            Log.d("web", "update, succeed delete a book:" + response);
//                                            //删除成功，删除本地数据
//                                            dbOperate.deleteBook(book.getUUID());
//                                        }
//                                    }, new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    //删除失败，暂时不做处理
//                                    Log.d("web", "update, fail delete a book:" + error);
//                                }
//                            }));
//                            mRequestQueue.start();
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    //不存在，直接删除本地数据
//                    dbOperate.deleteBook(book.getUUID());
//                }
//            }));
//            mRequestQueue.start();
//        }
//    }
//}
