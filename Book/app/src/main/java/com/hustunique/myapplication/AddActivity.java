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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import util.Constant;
import util.TimeUtil;

/**
 * Created by taozhiheng on 15-7-7.
 * wait to finish
 */
public class AddActivity extends AppCompatActivity{

    private Toolbar mToolbar;
    private SearchView mSearch;
    private RecyclerView mRecycler;

    private AlertDialog mDialog;
    private ImageView mImageView;
    private TextView mEmptyText;
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        mToolbar = (Toolbar) findViewById(R.id.add_toolbar);
        mSearch = (SearchView) findViewById(R.id.add_search);
        mRecycler = (RecyclerView) findViewById(R.id.add_recycler);
        mEmptyText = (TextView) findViewById(R.id.add_empty);
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

        mEmptyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmptyText.setVisibility(View.GONE);
            }
        });
        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String key = query.trim();
                if(!key.equals(mLastQuery)) {
                    query(query);
                    mLastQuery = query.trim();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        createDialogs();
        initDataSet();
    }

    private void createDialogs()
    {
        mDialog = new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                .setTitle("添加到")
                .setItems(
                        new String[]{"想读","在读", "已读"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mProgressDialog.setMessage("正在添加书籍...");
                                switch (which) {
                                    case 0:
                                        mBook.setType(Constant.TYPE_AFTER);
                                        MyApplication.setShouldUpdate(Constant.INDEX_AFTER);
                                        break;
                                    case 1:
                                        mPlanDialog.show();
                                        return;
                                    case 2:
                                        mBook.setType(Constant.TYPE_BEFORE);
                                        MyApplication.setShouldUpdate(Constant.INDEX_BEFORE);
                                        break;
                                }


//                                mProgressDialog.show();
//                                DBOperate dbOperate = MyApplication.getDBOperateInstance();
//                                if(mBook.getUUID() != null)
//                                    dbOperate.setBookStatus(mBook.getUUID(), Constant.STATUS_ADD);
//                                else
//                                {
//                                    String uuid = dbOperate.insertBook(mBook, mGroupList);
//                                    mBook.setUUID(uuid);
//                                }
//                                if(mBook.getType() == Constant.TYPE_AFTER)
//                                    dbOperate.setBookAfter(mBook.getUUID(), time);
//                                else if(mBook.getType() == Constant.TYPE_BEFORE)
//                                    dbOperate.setBookBefore(mBook.getUUID(), time);
//                                mProgressDialog.dismiss();

                                String time = TimeUtil.getNeedTime(System.currentTimeMillis());
                                mProgressDialog.show();
                                new AddTask(mBook, mGroupList, mHandler).execute(time);
                            }
                        })
                .create();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.plan_dialog, null);
        mPicker = (DatePicker)view.findViewById(R.id.dialog_datePicker);
        mTime = (EditText)view.findViewById(R.id.dialog_time);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProgressDialog.show();
                long startTime = TimeUtil.getTimeMillis(mPicker.getYear(), mPicker.getMonth(), mPicker.getDayOfMonth());
                int days = (mTime.getText().toString().length() <=0)? 1 : Integer.parseInt(mTime.getText().toString());
                if(days == 0)
                    days = 1;
                long endTime = startTime+days * 24 * 60 * 60 * 1000;
                mBook.setType(Constant.TYPE_NOW);
                mBook.setStartTime(TimeUtil.getNeedTime(startTime));
                mBook.setEndTime(TimeUtil.getNeedTime(endTime));


//                DBOperate dbOperate = MyApplication.getDBOperateInstance();
//                String uuid = dbOperate.insertBook(mBook, mGroupList);
//                dbOperate.setBookNow(uuid, mBook.getStartTime(), mBook.getEndTime());
//                mProgressDialog.dismiss();
                MyApplication.setShouldUpdate(Constant.INDEX_NOW);

                mProgressDialog.show();
                new AddTask(mBook, mGroupList, mHandler).execute(mBook.getStartTime(), mBook.getEndTime());

            }
        });
        builder.setNegativeButton("取消", null);
        mPlanDialog = builder.create();

        mDeleteDialog = new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                .setTitle("确定取消添加？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //删除此书及所有章节
                        mProgressDialog.setMessage("正在取消添加...");
                        mProgressDialog.show();
                        MyApplication.getDBOperateInstance().setBookDelete(mBook.getUUID());
                        mProgressDialog.dismiss();
                        mImageView.setSelected(false);
                    }
                })
                .setNegativeButton("否", null)
                .create();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("玩命搜索中...");
        mProgressDialog.setCancelable(false);
    }

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mProgressDialog.dismiss();
            mImageView.setSelected(true);
        }
    };

    private void initDataSet()
    {
        mHashMap = new HashMap<>();
        mRequestQueue = Volley.newRequestQueue(this);
        mBookList = new ArrayList<>();
        mAdapter = new AddAdapter(this, mBookList);
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AddAdapter.AddOnItemClickListener() {
            @Override
            public void onItemFlagClick(ImageView imageView, int position) {
                mImageView = imageView;
                mPosition = position;
                mBook = mBookList.get(mPosition);
                mGroupList = mHashMap.get(mPosition);
                if (!imageView.isSelected()) {
                    if(mGroupList.size() > 0)
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
                startActivityForResult(new Intent(this, MipcaActivityCapture.class), Constant.ACTION_SCAN_BOOK);
                break;
            case R.id.action_add_input:
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
            queryISBN(isbnCode);
        }
    }


    // 传入isbn,去豆瓣搜索,显示dialog,结束时消除dialog
    private void queryISBN(String isbnCode)
    {
        mSearch.clearFocus();
        mProgressDialog.setMessage("玩命搜索中...");
        mProgressDialog.show();
        mRequestQueue.add(new JsonObjectRequest(
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
                                                        intent.putExtra(Constant.KEY_BOOK, new Book(null, isbn13, title, author, publisher,
                                                                url, 0, 0, chapterInfos.size(),
                                                                wordNum[0], 0, null, 1));
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
                                                    intent.putExtra(Constant.KEY_BOOK, new Book(null, isbn13, title, author, publisher,
                                                            url, 0, 0, chapterInfos.size(),
                                                            wordNum[0], 0, null, 1));
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
                }));
        mRequestQueue.start();
    }


    //传入关键字，去豆瓣搜索，显示所有搜索到的书籍，一般不会超过20条
    private void query(final String key)
    {
        mSearch.clearFocus();
        mProgressDialog.setMessage("玩命搜索中...");
        mProgressDialog.show();
        String keywords = key;
        try
        {
            keywords = URLEncoder.encode(key, "UTF-8"); //先对中文进行UTF-8编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mRequestQueue.add(new JsonObjectRequest(
                Request.Method.GET,
                Constant.DB_QUERY_URL+"?q="+keywords,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("net", "query response:" + response.toString());
                        try {
                            JSONArray jsonArray = response.getJSONArray("books");
                            JSONObject jsonObject;
                            mBookList.clear();
                            mHashMap.clear();
                            mAdapter.notifyDataSetChanged();
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                jsonObject = jsonArray.getJSONObject(i);
                                final String isbn13;
                                if(jsonObject.has("isbn13"))
                                    isbn13 = jsonObject.getString("isbn13");
                                else
                                    isbn13 = jsonObject.getString("isbn10");
                                final long[] wordNum = {0};
                                final String title = jsonObject.getString("title");
                                final String author = ParseAuthor(jsonObject.getJSONArray("author"));
                                final String publisher = jsonObject.getString("publisher");
                                final String url = jsonObject.getString("image");
                                final ArrayList<ChapterInfo> chapterInfos = parseChapters(jsonObject.getString("catalog"));

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
                                                        }finally {
                                                            mBookList.add(new Book(null, isbn13, title, author, publisher,
                                                                    url, 0, 0, chapterInfos.size(),
                                                                    wordNum[0], 0, null, 1));
                                                            mAdapter.notifyItemInserted(mBookList.size()-1);
                                                            mHashMap.put(mBookList.size()-1, chapterInfos);
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        wordNum[0] = 0;
                                                        mBookList.add(new Book(null, isbn13, title, author, publisher,
                                                                url, 0, 0, chapterInfos.size(),
                                                                wordNum[0], 0, null, 1));
                                                        mAdapter.notifyItemInserted(mBookList.size()-1);
                                                        mHashMap.put(mBookList.size()-1, chapterInfos);
                                                    }
                                                }
                                        ));
                                mRequestQueue.start();
                            }
                            mProgressDialog.dismiss();
                            Toast.makeText(AddActivity.this, "共为您找到"+jsonArray.length()+"本书", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            mProgressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(AddActivity.this, "抱歉,没找到^-^" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Charset", "UTF-8");
                return headers;
            }
        });
        mRequestQueue.start();
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
        ArrayList<ChapterInfo> chapterInfos = new ArrayList<>();
        String[] chapters = response.split("\n");
        for(int i = 0; i < chapters.length-1; i++)
        {
            chapterInfos.add(new ChapterInfo(i, chapters[i].trim()));
        }
        return chapterInfos;

    }
}
