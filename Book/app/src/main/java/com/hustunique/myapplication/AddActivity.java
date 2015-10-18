package com.hustunique.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.Callback;
import com.umeng.analytics.MobclickAgent;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import adapter.AddAdapter;
import data.Book;
import data.ChapterInfo;
import data.DBOperate;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import service.AddTask;
import ui.DividerItemDecoration;
import ui.OnRcvScrollListener;
import util.Constant;
import util.TimeUtil;
import web.OkHttpUtil;

/**
 * Created by taozhiheng on 15-7-7.
 * 使用友盟和诸葛io统计了三个事件
 * 1.search　每次查找的关键字key,显示的总条目total,添加了几本书
 * 2.scan 点击扫码按钮的次数
 * 3.create　点击新建按钮的次数
 */
public class AddActivity extends AppCompatActivity{

    private Toolbar mToolbar;
    private SearchView mSearch;
    private RecyclerView mRecycler;

    private AlertDialog mDialog;
    private ImageView mImageView;
//    private TextView mEmptyText;
    private ProgressDialog mProgressDialog;
    private AlertDialog mDeleteDialog;
    private AlertDialog mPlanDialog;
    private DatePicker mPicker;
    private EditText mTime;

    private AddAdapter mAdapter;
    private int mPosition;

    private Book mBook;
    private List<ChapterInfo> mGroupList;//记录当前状态数据

    private List<Book> mBookList;
    private HashMap<Integer, ArrayList<ChapterInfo>> mHashMap;

    private RequestQueue mRequestQueue;

    private String mLastQuery;

    private int total;

    public final static String REQUEST_TAG = "MyQueryRequest";

    private final static int COLOR_SIZE = 6;

    private int addCount;

    private boolean isLoadingData;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Add Book Activity");
        MobclickAgent.onResume(this);

        ZhugeSDK.getInstance().init(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Add Book Activity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZhugeSDK.getInstance().flush(getApplicationContext());
        sendAddCount();
        mHandler.removeCallbacks(null);
        if(mDialog != null) {
            mDialog.dismiss();
        }
        if(mDeleteDialog != null) {
            mDeleteDialog.dismiss();
        }
        if(mPlanDialog != null) {
            mPlanDialog.dismiss();
        }
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void sendAddCount()
    {
        if(mLastQuery != null)
        {
            Map<String, String> map = new HashMap<>();
            map.put("key", mLastQuery);
            map.put("total", ""+mBookList.size());
            map.put("append", ""+addCount);
            MobclickAgent.onEventValue(AddActivity.this, "search", map, addCount);
            JSONObject event = new JSONObject(map);
            ZhugeSDK.getInstance().track(getApplicationContext(), "search", event);
            Log.d("send", "search:" + mLastQuery + "-" + addCount);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        mToolbar = (Toolbar) findViewById(R.id.add_toolbar);
        mSearch = (SearchView) findViewById(R.id.add_search);
        mRecycler = (RecyclerView) findViewById(R.id.add_recycler);
//        mEmptyText = (TextView) findViewById(R.id.add_empty);
        //mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.add_progressBar);
        mToolbar.setTitle("添加书籍");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setItemAnimator(new SlideInDownAnimator());
        mRecycler.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));

//        mEmptyText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEmptyText.setVisibility(View.GONE);
//            }
//        });
        addCount = 0;
        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String key = query.trim();
                if(!key.equals(mLastQuery) || mBookList.size() == 0) {
                    sendAddCount();
                    addCount = 0;
                    total = 0;
                    mBookList.clear();
                    mHashMap.clear();
                    mAdapter.notifyDataSetChanged();
                    mAdapter.setTotal(20);
                    query(query, 0);
                    mLastQuery = query.trim();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearch.setSubmitButtonEnabled(true);
        createDialogs();
        initDataSet();
    }

    private void createDialogs()
    {
        mDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("添加到")
                .setItems(
                        new String[]{"想读","在读", "已读"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int color = (int)(Math.random()*COLOR_SIZE);
                                mBook.setColor(color);
                                mProgressDialog.setMessage("正在添加书籍...");
                                switch (which) {
                                    case 0:
                                        mBook.setType(Constant.TYPE_AFTER);
                                        MyApplication.setShouldUpdate(Constant.INDEX_AFTER);
                                        break;
                                    case 1:
                                        mTime.setText(null);
                                        mPlanDialog.show();
                                        return;
                                    case 2:
                                        mBook.setType(Constant.TYPE_BEFORE);
                                        MyApplication.setShouldUpdate(Constant.INDEX_BEFORE);
                                        break;
                                }
                                String time = TimeUtil.getNeedTime(System.currentTimeMillis());
                                mProgressDialog.setCancelable(false);
                                mProgressDialog.show();
                                new AddTask(mBook, mGroupList, mHandler).execute(time);
                            }
                        })
                .create();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        View view = LayoutInflater.from(this).inflate(R.layout.plan_dialog, null);
        mPicker = (DatePicker)view.findViewById(R.id.dialog_datePicker);
        mTime = (EditText)view.findViewById(R.id.dialog_time);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProgressDialog.show();
                long startTime = TimeUtil.getTimeMillis(mPicker.getYear(), mPicker.getMonth(), mPicker.getDayOfMonth());
                long days = (mTime.getText().toString().length() <=0)? 1 : Long.parseLong(mTime.getText().toString());
                if (days == 0)
                    days = 1;
                long endTime = startTime + days * 24 * 60 * 60 * 1000;
                mBook.setType(Constant.TYPE_NOW);
                mBook.setStartTime(TimeUtil.getNeedTime(startTime));
                mBook.setEndTime(TimeUtil.getNeedTime(endTime));

                MyApplication.setShouldUpdate(Constant.INDEX_NOW);

                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                new AddTask(mBook, mGroupList, mHandler).execute(mBook.getStartTime(), mBook.getEndTime());

            }
        });
        builder.setNegativeButton("取消", null);
        mPlanDialog = builder.create();

        mDeleteDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("确定取消添加？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //删除此书及所有章节,标记为删除，但不立即删除
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.setMessage("正在取消添加...");
                        mProgressDialog.show();
                        MyApplication.getDBOperateInstance().setBookDelete(mBook.getId());
                        mProgressDialog.dismiss();
                        mProgressDialog.setCancelable(true);
                        mImageView.setSelected(false);
                        Toast.makeText(getBaseContext(), "已取消", Toast.LENGTH_SHORT).show();
                        addCount--;
                    }
                })
                .setNegativeButton("否", null)
                .create();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("玩命搜索中...");
        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mRequestQueue.cancelAll(REQUEST_TAG);
                if (isLoadingData) {
                    isLoadingData = false;
                    mRecycler.scrollBy(0, -mAdapter.getHideHeight());
                }
            }
        });
    }

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mProgressDialog.dismiss();
            mProgressDialog.setCancelable(true);
            mImageView.setSelected(true);
            Toast.makeText(getBaseContext(), "已添加", Toast.LENGTH_SHORT).show();
            addCount++;
        }
    };


    private void initDataSet()
    {
        mHashMap = new HashMap<>();
        mRequestQueue = Volley.newRequestQueue(this);
        mBookList = new ArrayList<>();
        mAdapter = new AddAdapter(this, mBookList);
        mRecycler.setAdapter(mAdapter);
        mRecycler.addOnScrollListener(new OnRcvScrollListener(){
            @Override
            public void onBottom() {
                super.onBottom();
                // 到底部自动加载
                int size = mAdapter.getItemCount()-1;
                if (!isLoadingData && mAdapter.getItemViewType(size) == 1){
                    if(size < total) {
                        query(mLastQuery, size);
                        isLoadingData = true;
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), "抱歉,已到天涯海角^-^", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        mAdapter.setOnItemClickListener(new AddAdapter.AddOnItemClickListener() {
            @Override
            public void onItemFlagClick(ImageView imageView, int position) {
                mImageView = imageView;
                mPosition = position;
                mBook = mBookList.get(mPosition);
                mGroupList = mHashMap.get(mPosition);
                if (!imageView.isSelected()) {
                    if (mGroupList.size() > 0)
                        mDialog.show();
                    else
                        Toast.makeText(getBaseContext(), "此书没有章节,无法添加^_^", Toast.LENGTH_SHORT).show();
                    for (ChapterInfo chapterInfo : mGroupList)
                        Log.d("net", "chapter:" + chapterInfo.getName());
                } else {
                    mDeleteDialog.show();
                }
            }

            @Override
            public void onItemClick(int position) {
                Book book = mBookList.get(position);
                ArrayList<ChapterInfo> chapterInfos = mHashMap.get(position);
                Intent intent = new Intent(AddActivity.this, PreviewActivity.class);
                intent.putExtra(Constant.KEY_BOOK, book);
                intent.putParcelableArrayListExtra(Constant.KEY_CHAPTERS, chapterInfos);
                startActivity(intent);
            }

            @Override
            public void onClick(int size) {
//                if(size < total)
//                    query(mLastQuery, size);
//                else
//                    Toast.makeText(getBaseContext(), "抱歉,已到天涯海角^-^", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_add_scan:
                MobclickAgent.onEvent(this, "scan");
                ZhugeSDK.getInstance().track(this, "scan");
                Log.d("send", "scan");
                startActivityForResult(new Intent(this, MipcaActivityCapture.class), Constant.ACTION_SCAN_BOOK);
                break;
            case R.id.action_add_input:
                MobclickAgent.onEvent(this, "create");
                ZhugeSDK.getInstance().track(this, "create");
                Log.d("send", "create");
                Intent intent = new Intent(this, CreateActivity.class);
                intent.putExtra(Constant.KEY_ACTION, Constant.ACTION_CREATE_BOOK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && data != null && requestCode == Constant.ACTION_SCAN_BOOK) {
            String isbnCode = data.getStringExtra("result");
            Toast.makeText(getBaseContext(), isbnCode, Toast.LENGTH_SHORT).show();
            queryISBN(isbnCode);
        }
    }



    // 传入isbn,去豆瓣搜索,显示dialog,结束时消除dialog
    private void queryISBN(String isbnCode)
    {
        mSearch.clearFocus();
        mProgressDialog.setMessage("玩命搜索中...");
        mProgressDialog.show();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Constant.DB_URL + isbnCode,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            final String isbn13 = response.getString("isbn13");
                            final long[] wordNum = {0};
                            final String title = response.getString("title");
                            final String author = ParseAuthor(response.getJSONArray("author"));
                            final String publisher = response.getString("publisher");
                            final String url = response.getString("image");
                            final ArrayList<ChapterInfo> chapterInfos = parseChapters(response.getString("catalog"));
                            mRequestQueue.add(
                                    new JsonObjectRequest(
                                            Request.Method.GET,
                                            MyApplication.getUrlHead() + "/api/v1/books/" + isbn13 + "/words",
                                            null,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    Log.d("net","words:"+response);
                                                    try {
                                                        wordNum[0] = response.getLong("words");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        wordNum[0] = 0;
                                                    }finally
                                                    {
                                                        Intent intent = new Intent(AddActivity.this, CreateActivity.class);
                                                        intent.putParcelableArrayListExtra(Constant.KEY_CHAPTERS, chapterInfos);
                                                        intent.putExtra(Constant.KEY_ACTION, Constant.ACTION_SCAN_BOOK);
                                                        intent.putExtra(Constant.KEY_BOOK, new Book(-1, null, isbn13, title, author, publisher,
                                                                url, 0, 0, chapterInfos.size(),
                                                                wordNum[0], 0, null, 1, Constant.T_STATUS_AFTER));
                                                        mProgressDialog.dismiss();
                                                        startActivity(intent);
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    wordNum[0] = 0;
                                                    Intent intent = new Intent(AddActivity.this, CreateActivity.class);
                                                    intent.putParcelableArrayListExtra(Constant.KEY_CHAPTERS, chapterInfos);
                                                    intent.putExtra(Constant.KEY_ACTION, Constant.ACTION_SCAN_BOOK);
                                                    intent.putExtra(Constant.KEY_BOOK, new Book(-1, null, isbn13, title, author, publisher,
                                                            url, 0, 0, chapterInfos.size(),
                                                            wordNum[0], 0, null, 1, Constant.T_STATUS_AFTER));
                                                    mProgressDialog.dismiss();
                                                    startActivity(intent);
                                                }
                                            }
                                    ));
                            mRequestQueue.start();



                        }catch (JSONException e)
                        {
                            mProgressDialog.dismiss();
                            Toast.makeText(AddActivity.this, "抱歉,没找到^-^", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(AddActivity.this, "抱歉,没找到^-^", Toast.LENGTH_SHORT).show();
                    }
                });
        request.setTag(REQUEST_TAG);
        mRequestQueue.add(request);
        mRequestQueue.start();
    }


    //传入关键字，去豆瓣搜索，显示所有搜索到的书籍，一般不会超过20条
    private void query(final String key, final int start)
    {
        mSearch.clearFocus();
        if(start == 0) {
            mProgressDialog.setMessage("玩命搜索中...");
            mProgressDialog.show();
        }
        String keywords = key;
        try
        {
            keywords = URLEncoder.encode(key, "UTF-8"); //先对中文进行UTF-8编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            isLoadingData = false;
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Constant.DB_QUERY_URL + "?q=" + keywords + "&start=" + start,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("net", "query response:" + response.toString());
                        try {
                            JSONArray jsonArray = response.getJSONArray("books");
                            JSONObject jsonObject;
                            if(start == 0) {
//                                mBookList.clear();
//                                mHashMap.clear();
//                                mAdapter.notifyDataSetChanged();
                                total = response.getInt("total");
                                mAdapter.setTotal(total);
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                final String isbn13;
                                if (jsonObject.has("isbn13"))
                                    isbn13 = jsonObject.getString("isbn13");
                                else
                                    isbn13 = jsonObject.getString("isbn10");
//                                final long[] wordNum = {0};
                                final String title = jsonObject.getString("title");
                                final String author = ParseAuthor(jsonObject.getJSONArray("author"));
                                final String publisher = jsonObject.getString("publisher");
                                final String url = jsonObject.getString("image");
                                final ArrayList<ChapterInfo> chapterInfos = parseChapters(jsonObject.getString("catalog"));
                                mBookList.add(new Book(-1, null, isbn13, title, author, publisher,
                                        url, 0, 0, chapterInfos.size(),
                                        0, 0, null, 1, Constant.T_STATUS_AFTER));
                                mAdapter.notifyItemInserted(mBookList.size() - 1);
                                mHashMap.put(mBookList.size() - 1, chapterInfos);
                                if(chapterInfos.size() > 0) {
                                    JsonObjectRequest wordRequest = new JsonObjectRequest(
                                            Request.Method.GET,
                                            MyApplication.getUrlHead() + "/api/v1/books/" + isbn13 + "/words",
                                            null, new WordListener(i), null);
                                    wordRequest.setTag(REQUEST_TAG);
                                    mRequestQueue.add(wordRequest);
                                    mRequestQueue.start();
                                }
                            }
                            mProgressDialog.dismiss();
                            Toast.makeText(AddActivity.this, "共为您找到" + jsonArray.length() + "本书", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            mProgressDialog.dismiss();
                        }
                        finally {
                            isLoadingData = false;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(start == 0)
                            mProgressDialog.dismiss();
                        Toast.makeText(AddActivity.this, "抱歉,没找到^-^", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Charset", "UTF-8");
                return headers;
            }
        };
        request.setTag(REQUEST_TAG);
        mRequestQueue.add(request);
        mRequestQueue.start();
    }

    class WordListener implements Response.Listener<JSONObject> {

        private int mPosition;

        public WordListener(int position)
        {
            this.mPosition = position;
        }

        @Override
        public void onResponse(JSONObject response) {
            Log.d("web","book words:"+response);
            try {
                long wordNum = response.getLong("words");
                if(wordNum != 0)
                {
                    mBookList.get(mPosition).setWordNum(wordNum);
                    mAdapter.notifyItemChanged(mPosition);
                }
            } catch (JSONException e) {
                e.printStackTrace();
//              wordNum[0] = 0;
            }
//            finally {
//                mBookList.add(new Book(null, isbn13, title, author, publisher,
//                        url, 0, 0, chapterInfos.size(),
//                        wordNum[0], 0, null, 1));
//                mAdapter.notifyItemInserted(mBookList.size()-1);
//                mHashMap.put(mBookList.size()-1, chapterInfos);
//            }
        }
    }


    //从jsonArray中提取出所有作者
    private String ParseAuthor(JSONArray array){
        StringBuffer str =new StringBuffer();
        for(int i=0;i<array.length();i++)
        {
            try{
                str=str.append(array.getString(i)).append(" ");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return str.toString();
    }

    //对章节信息拆分，分成一个个的章节
    public static ArrayList<ChapterInfo> parseChapters(String response)
    {
        Log.d("web", "catalog:"+response);
        ArrayList<ChapterInfo> chapterInfos = new ArrayList<>();
        if(response == null || response.compareTo("" )==0)
            return chapterInfos;
        String[] chapters = response.split("\n");
        int j = 1;
        for(int i = 0; i < chapters.length; i++)
        {
            String name = chapters[i].trim();
            Log.d("web", "catalog item:"+response);

            if(name.compareTo("")==0)
                continue;
            chapterInfos.add(new ChapterInfo(j, chapters[i].trim()));
            j++;
        }
        return chapterInfos;
    }
}
