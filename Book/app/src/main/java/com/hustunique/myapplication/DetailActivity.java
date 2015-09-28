package com.hustunique.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.zhuge.analysis.stat.ZhugeSDK;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import adapter.ChapterAdapter;
import data.Book;
import data.Chapter;
import data.DBOperate;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import service.QueryChaptersTask;
import ui.DividerItemDecoration;
import ui.MyAnimation;
import ui.StickyLayout;
import uk.co.senab.photoview.PhotoViewAttacher;
import util.Constant;
import util.FileUtil;


/**
 * operate
 * chapter type change
 * after to now：chapter type change,ReadingFragment update
 * finish to now：chapter type change,Book finishNum change,ReadingFragment update
 * now to after：chapter type change,ReadingFragment update
 * 持有数据:chapterId,type
 * 数据操作：改变章节类型，改变持有章节类型，改变数据库章节类型，typeS=type
 * */

public class DetailActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView mIcon;
    private TextView mIconText;
    private TextView mName;
    private TextView mChapterNum;
    private TextView mAuthor;
    private TextView mPress;
    private RecyclerView mRecycler;
    private StickyLayout mStickyLayout;

//    private ImageView mPhotoView;
//    private ImageView shadow;

    private SystemBarTintManager mTintManager;

    private List<Chapter> mChapterList;
    private ChapterAdapter mAdapter;
    private Book mBook;

    private int mAction;

    private int mColorIndex = 0;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Book Detail Activity");
        MobclickAgent.onResume(this);

        ZhugeSDK.getInstance().init(getApplicationContext());

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Book Detail Activity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZhugeSDK.getInstance().flush(getApplicationContext());
        mHandler.removeCallbacks(null);

    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK && mPhotoView.getVisibility() == View.VISIBLE)
//        {
//            mPhotoView.setVisibility(View.GONE);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("detail", "activity onCreate");
        mAction = getIntent().getIntExtra(Constant.KEY_ACTION, Constant.VIEW_BOOK);
        setContentView(R.layout.activity_detail);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
        }
        mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        mIcon = (ImageView) findViewById(R.id.detail_icon);
        mIconText = (TextView) findViewById(R.id.detail_icon_text);
        mName = (TextView) findViewById(R.id.detail_name);
        mChapterNum = (TextView) findViewById(R.id.detail_chapterNum);
        mAuthor = (TextView) findViewById(R.id.detail_author);
        mPress = (TextView) findViewById(R.id.detail_press);
        mRecycler = (RecyclerView) findViewById(R.id.detail_recycler);
//        mPhotoView = (ImageView) findViewById(R.id.photo_view);
//        shadow = (ImageView) findViewById(R.id.detail_shadow);

        mToolbar.setTitle("书籍详情");
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
                DividerItemDecoration.VERTICAL_LIST, 1));
        mStickyLayout =(StickyLayout)findViewById(R.id.detail_sticky);
        mStickyLayout.setOnGiveUpTouchEventListener(new StickyLayout.OnGiveUpTouchEventListener() {
            @Override
            public boolean giveUpTouchEvent(MotionEvent event) {
                View view = mRecycler.getChildAt(0);
                if (view != null && view.getTop() >= 0)
                    return true;
                else if (mRecycler.getChildCount() == 0 && mRecycler.getTop() >= 0)
                    return true;
                return false;
            }
        });

        mBook = getIntent().getParcelableExtra(Constant.KEY_BOOK);
        load();
    }

    private Bitmap getBitmap(View view)
    {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    private void load()
    {
        Log.d("detail", "activity load");
        mColorIndex = mBook.getColor();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mTintManager.setStatusBarTintColor(Constant.colors[mColorIndex]);
        }
        mToolbar.setBackgroundColor(Constant.colors[mColorIndex]);
        mName.setText(mBook.getName());
        mAuthor.setText(mBook.getAuthor());
        mPress.setText(mBook.getPress());
        mChapterNum.setText(mBook.getFinishNum()+"/"+mBook.getChapterNum()+"章  　 "+mBook.getWordNum()+" 千字");
        String mUrl = mBook.getUrl();
        if(mUrl != null && !mUrl.equals("null")) {
            File file = new File(mUrl);
            if(file.exists()) {
                mIconText.setVisibility(View.GONE);
                Picasso.with(this).load(file).into(mIcon);
            }
            else if(mUrl.startsWith("http")) {
                mIconText.setVisibility(View.GONE);
                Picasso.with(this).load(Uri.parse(mUrl)).placeholder(R.drawable.book_cover).into(mIcon);
            }
            else
            {
                mIconText.setVisibility(View.VISIBLE);
                Picasso.with(this).load(R.drawable.book_cover).into(mIcon);
                String name = mBook.getName();
                if(name != null && name.length() > 2)
                    name = name.substring(0, 2);
                mIconText.setText(name);
            }
        }
        else
        {
            mIconText.setVisibility(View.VISIBLE);
            Picasso.with(this).load(R.drawable.book_cover).into(mIcon);
            String name = mBook.getName();
            if(name != null && name.length() > 2)
                name = name.substring(0, 2);
            mIconText.setText(name);
        }

//        DBOperate dbOperate = MyApplication.getDBOperateInstance();
//
//        mChapterList = dbOperate.getChapters(mBook.getId());
        mChapterList = new ArrayList<>();
        new QueryChaptersTask(mChapterList, mHandler).execute(mBook.getId());

        Log.d("detail", "activity finish load");
    }

    private void setupAdapter()
    {
        boolean visible = false;
        if (mAction == Constant.ADD_CHAPTER)
            visible = true;
        mAdapter = new ChapterAdapter(mChapterList, visible, mColorIndex);
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ChapterAdapter.ChapterOnItemClickListener() {
            @Override
            public void onItemClick(int position, int type) {
                Chapter chapter = mChapterList.get(position);
                DBOperate dbOperate = MyApplication.getDBOperateInstance();
                //原来状态是完成，标记为在读或未读，book finishNum减少１
                //原来状态是未读或在读，book不变
                if (chapter.getType() == Constant.TYPE_BEFORE)
                {
                    mBook.setFinishNum(mBook.getFinishNum() - 1);
                    mChapterNum.setText(mBook.getFinishNum() + "/" + mBook.getChapterNum() + "章  　 "
                            + mBook.getWordNum() + " K字");
                }
                chapter.setType(type);

                dbOperate.setChapterType(chapter.getId(), type);

                if (type == Constant.TYPE_NOW) {
                    Toast.makeText(getBaseContext(), "已添加", Toast.LENGTH_SHORT).show();
                }
                MyApplication.setShouldUpdate(Constant.INDEX_READ);
            }
        });
    }

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0)
            {
                setupAdapter();
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_detail_edit)
        {
            Intent intent = new Intent(this, CreateActivity.class);
            intent.putExtra(Constant.KEY_ACTION, Constant.ACTION_EDIT_BOOK);
            intent.putExtra(Constant.KEY_BOOK, mBook);
            startActivityForResult(intent, Constant.ACTION_EDIT_BOOK);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.ACTION_EDIT_BOOK && resultCode == Activity.RESULT_OK && null != data) {
            mBook = data.getParcelableExtra(Constant.KEY_BOOK);
            load();
        }
    }
}
