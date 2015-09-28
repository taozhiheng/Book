package com.hustunique.myapplication;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.zhuge.analysis.stat.ZhugeSDK;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import adapter.PreviewAdapter;
import data.Book;
import data.ChapterInfo;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import ui.DividerItemDecoration;
import ui.StickyLayout;
import util.Constant;

/**
 * Created by taozhiheng on 15-7-23.
 * Preview the book detail of the search book
 */
public class PreviewActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView mIcon;
    private TextView mIconText;
    private TextView mName;
    private TextView mChapterNum;
    private TextView mAuthor;
    private TextView mPress;
    private RecyclerView mRecycler;
    private StickyLayout mStickyLayout;

    private SystemBarTintManager mTintManager;

    private List<ChapterInfo> mChapterInfos;
    private PreviewAdapter mAdapter;
    private Book mBook;

    private int colorSelected = Color.rgb(0xe9, 0x1e, 0x63);

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Book Preview Activity");
        MobclickAgent.onResume(this);

        ZhugeSDK.getInstance().init(getApplicationContext());

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Book Preview Activity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZhugeSDK.getInstance().flush(getApplicationContext());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("detail", "activity onCreate");
        setContentView(R.layout.activity_detail);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            mTintManager = new SystemBarTintManager(this);
//            mTintManager.setStatusBarTintEnabled(true);
//        }
        mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        mIcon = (ImageView) findViewById(R.id.detail_icon);
        mIconText = (TextView) findViewById(R.id.detail_icon_text);
        mName = (TextView) findViewById(R.id.detail_name);
        mChapterNum = (TextView) findViewById(R.id.detail_chapterNum);
        mAuthor = (TextView) findViewById(R.id.detail_author);
        mPress = (TextView) findViewById(R.id.detail_press);
        mRecycler = (RecyclerView) findViewById(R.id.detail_recycler);
        mStickyLayout = (StickyLayout) findViewById(R.id.detail_sticky);
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

        mBook = getIntent().getParcelableExtra(Constant.KEY_BOOK);
        mChapterInfos = new ArrayList<>();
        ArrayList<ChapterInfo> chapterInfos = getIntent().getParcelableArrayListExtra(Constant.KEY_CHAPTERS);
        mChapterInfos.addAll(chapterInfos);
        load();
    }

    private void load()
    {

        mName.setText(mBook.getName());
        mAuthor.setText(mBook.getAuthor());
        mPress.setText(mBook.getPress());
        mChapterNum.setText(mBook.getFinishNum()+"/"+mBook.getChapterNum()+"章  　 "+mBook.getWordNum()+" K字");
        String mUrl = mBook.getUrl();
        if(mUrl != null && !mUrl.equals("null")) {
            mIconText.setVisibility(View.GONE);
            File file = new File(mUrl);
            if(file.exists())
                Picasso.with(this).load(file).into(mIcon);
            else if(mUrl.contains("http"))
                Picasso.with(this).load(Uri.parse(mUrl)).into(mIcon);
        }
        else
        {
            Picasso.with(this).load(R.drawable.book_cover).into(mIcon);
            String name = mBook.getName();
            if(name != null && name.length() > 2)
                name = name.substring(0, 2);
            mIconText.setText(name);
        }
        mAdapter = new PreviewAdapter(mChapterInfos);
        mRecycler.setAdapter(mAdapter);
    }
}
