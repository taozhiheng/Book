package com.hustunique.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import adapter.ChapterAdapter;
import data.Book;
import data.Chapter;
import data.DBOperate;
import fragment.ReadingFragment;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import ui.DividerItemDecoration;
import ui.StickyLayout;
import util.Constant;


/**
 * operate
 * chapter type change
 * after to now：chapter type change,ReadingFragment update
 * finish to now：chapter type change,Book finishNum change,ReadingFragment update
 * now to after：chapter type change,ReadingFragment update
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

    private SystemBarTintManager mTintManager;

    private List<Chapter> mChapterList;
    private ChapterAdapter mAdapter;
    private Book mBook;

    private int mAction;

    private int colorSelected = Color.rgb(0xe9, 0x1e, 0x63);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    private void load()
    {
        Log.d("detail", "activity load");
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
        boolean visible = false;
        if (mAction == Constant.ADD_CHAPTER)
            visible = true;
        DBOperate dbOperate = MyApplication.getDBOperateInstance();
        if ((mChapterList = dbOperate.getChapters(mBook.getUUID())) == null) {
            if(mBook.getUUID().matches("[0-9]+")) {
                mBook.setUUID(dbOperate.getBookUUID(Integer.parseInt(mBook.getUUID())));
                mChapterList = dbOperate.getChapters(mBook.getUUID());
            }
            if (mChapterList == null)
                mChapterList = new ArrayList<>();
            else
            {
                MyApplication.setShouldUpdate(Constant.INDEX_READ);
                MyApplication.setShouldUpdate(Constant.INDEX_AFTER);
                MyApplication.setShouldUpdate(Constant.INDEX_NOW);
                MyApplication.setShouldUpdate(Constant.INDEX_BEFORE);
            }
        }
        mAdapter = new ChapterAdapter(mChapterList, visible);
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

                dbOperate.setChapterType(mBook.getUUID(), chapter.getId(), type);
                int status = Constant.STATUS_MOD;
                if(chapter.getStatus() == Constant.STATUS_ADD)
                    status = Constant.STATUS_ADD;
                dbOperate.setChapterStatus(mBook.getUUID(), chapter.getId(), status);

                if (type == Constant.TYPE_NOW) {
                    Toast.makeText(getBaseContext(), "已添加", Toast.LENGTH_SHORT).show();
                }
                MyApplication.setShouldUpdate(Constant.INDEX_READ);
            }
        });
        Log.d("detail", "activity finish load");
    }


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
