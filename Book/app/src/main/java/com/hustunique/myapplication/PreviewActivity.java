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
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import adapter.PreviewAdapter;
import data.Book;
import data.ChapterInfo;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import ui.DividerItemDecoration;
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

    private SystemBarTintManager mTintManager;

    private List<ChapterInfo> mChapterInfos;
    private PreviewAdapter mAdapter;
    private Book mBook;

    private int colorSelected = Color.rgb(0xe9, 0x1e, 0x63);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("detail", "activity onCreate");
        setContentView(R.layout.detail);
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
            else
                Picasso.with(this).load(Uri.parse(mUrl)).into(mIcon);
        }
        else
        {
            mIconText.setText(mBook.getName());
            mIconText.setTextSize(30);
            mIconText.setBackgroundColor(colorSelected);
        }
        mAdapter = new PreviewAdapter(mChapterInfos);
        mRecycler.setAdapter(mAdapter);
    }
}
