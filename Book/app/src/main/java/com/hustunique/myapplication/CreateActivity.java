package com.hustunique.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import adapter.ChapterCreateAdapter;
import data.Book;
import data.Chapter;
import data.ChapterInfo;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import service.AddTask;
import service.EditTask;
import ui.DividerItemDecoration;
import ui.StickyLayout;
import util.Constant;
import util.TimeUtil;


/**
 * wait to finish
 * */

public class CreateActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView mIcon;
    private TextView mIconText;
    private EditText mName;
    private TextView mChapterNum;
    private EditText mWordNum;
    private EditText mAuthor;
    private EditText mPress;
    private RecyclerView mRecycler;
    //private ColorPickerSeekBar mSeekBar;
//    private StickyLayout mStickyLayout;

    private SystemBarTintManager mTintManager;

    private List<Integer> mDeletePosList;
    private List<ChapterInfo> mGroupList;//记录当前状态数据
    private List<Chapter> mChapterList;
    private ChapterCreateAdapter mAdapter;
    private Book mBook;

    private int mAction;
    private AlertDialog mDialog;
    private AlertDialog mPlanDialog;
    private ProgressDialog mProgressDialog;
    private DatePicker mPicker;
    private EditText mTime;
    private StickyLayout mStickyLayout;

    private boolean mChanged;


    private int colorSelected = Color.rgb(0xe9, 0x1e, 0x63);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAction = getIntent().getIntExtra(Constant.KEY_ACTION, Constant.VIEW_BOOK);

        setContentView(R.layout.activity_create);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
        }
        mToolbar = (Toolbar) findViewById(R.id.create_toolbar);
        mIcon = (ImageView) findViewById(R.id.create_icon);
        mIconText = (TextView) findViewById(R.id.create_icon_text);
        mName = (EditText) findViewById(R.id.create_name);
        mChapterNum = (TextView) findViewById(R.id.create_chapterNum);
        mWordNum = (EditText) findViewById(R.id.create_wordNum);
        mAuthor = (EditText) findViewById(R.id.create_author);
        mPress = (EditText) findViewById(R.id.create_press);
        mRecycler = (RecyclerView) findViewById(R.id.create_recycler);
        createDialogs();
        //mSeekBar = (ColorPickerSeekBar) findViewById(R.id.create_seekBar);
//        mCreateText = (EditText) findViewById(R.id.create_text);
//        mStickyLayout = (StickyLayout) findViewById(R.id.create_sticky);

        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setItemAnimator(new SlideInDownAnimator());
        mRecycler.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST, 1));
        mStickyLayout =(StickyLayout)findViewById(R.id.create_sticky);
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

        if(mAction != Constant.ACTION_EDIT_BOOK)
            mToolbar.setTitle("创建书籍");
        else
            mToolbar.setTitle("编辑书籍");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mChanged = false;

        setListeners();

        loadData();

        setupAdapter();


    }

    private void loadData()
    {
        //读入数据
        mDeletePosList = new ArrayList<>();
        mGroupList =new ArrayList<>();
        if(mAction == Constant.ACTION_EDIT_BOOK)
        {
            mBook = getIntent().getParcelableExtra(Constant.KEY_BOOK);

            Log.d("net", "read book info from local");
            mChapterList = MyApplication.getDBOperateInstance().getChapters(mBook.getUUID());
            Chapter chapter;
            if (mChapterList != null) {
                for (int i = 0; i < mChapterList.size(); i++) {
                    chapter = mChapterList.get(i);
                    mGroupList.add(new ChapterInfo(i, chapter.getName()));
                }
            }

            String url = mBook.getUrl();
            if(url != null && !url.equals("null")) {
                mIconText.setVisibility(View.GONE);
                File file = new File(url);
                if(file.exists())
                    Picasso.with(this).load(file).into(mIcon);
                else
                    Picasso.with(this).load(Uri.parse(url)).into(mIcon);
            }
            else
            {
                String titleName = mBook.getName();
                if(titleName.length()>2)
                    titleName = titleName.substring(0, 2);
                mIconText.setText(titleName);
                mIconText.setTextSize(30);
                mIconText.setBackgroundColor(colorSelected);
            }
            mName.setText(mBook.getName());
            mAuthor.setText(mBook.getAuthor());
            mPress.setText(mBook.getPress());
            mChapterNum.setText(mBook.getFinishNum()+"/"+mBook.getChapterNum()+"章");
            mWordNum.setText(mBook.getWordNum()+"");


        }
        else
        {
            if(mAction == Constant.ACTION_SCAN_BOOK)
            {
                mBook = getIntent().getParcelableExtra(Constant.KEY_BOOK);
                List<ChapterInfo> list = getIntent().getParcelableArrayListExtra(Constant.KEY_CHAPTERS);
                if(list != null)
                    mGroupList.addAll(list);
            }
            if(mBook == null)
            {
                mBook = new Book();
            }
            else
            {
                mName.setText(mBook.getName());
                mAuthor.setText(mBook.getAuthor());
                mPress.setText(mBook.getPress());
                mChapterNum.setText("0/"+mBook.getChapterNum()+"章");
                mWordNum.setText(mBook.getWordNum()+"");
                String url = mBook.getUrl();
                if(url != null && !url.equals("null")) {
                    mIconText.setVisibility(View.GONE);
                    File file = new File(url);
                    if(file.exists())
                        Picasso.with(this).load(file).into(mIcon);
                    else
                        Picasso.with(this).load(Uri.parse(url)).into(mIcon);
                }
                else
                {
                    String titleName = mBook.getName();
                    if(titleName.length()>2)
                        titleName = titleName.substring(0, 2);
                    mIconText.setText(titleName);
                    mIconText.setTextSize(30);
                    mIconText.setBackgroundColor(colorSelected);
                }
            }
        }
    }

    private void setupAdapter()
    {
        mAdapter = new ChapterCreateAdapter(mGroupList);
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemChangedListener(new ChapterCreateAdapter.MyOnItemChangedListener() {

            //增加一项时，chapterNum增加１；若书已完成，则finishNum同步增加１
            @Override
            public void onItemInsert(int position, String str) {
                mGroupList.add(new ChapterInfo(-1, str));
                if(mBook.getType() == Constant.TYPE_BEFORE )
                    mBook.setFinishNum(mBook.getFinishNum()+1);
                mBook.setChapterNum(mBook.getChapterNum()+1);
                mChapterNum.setText(mBook.getFinishNum()+"/"+mBook.getChapterNum()+"章");
                mAdapter.notifyItemChanged(position);
                mAdapter.notifyItemInserted(position+1);
                if(!mChanged)
                    mChanged = true;
            }

            //删除一项时，chapterNum减少１
            //若删除原有的,完成状态的章节，　finishNum减少１
            //若删除不是原有的，但是书已完成，finishNum同步减少１
            @Override
            public void onItemDelete(int position) {
                Toast.makeText(getBaseContext(), "delete:"+position, Toast.LENGTH_SHORT).show();
                ChapterInfo chapterInfo = mGroupList.get(position);
                int index = chapterInfo.getPosition();
                if (index != -1) {
                    mDeletePosList.add(index);
                    if(mChapterList.get(index).getType() == Constant.TYPE_BEFORE)
                        mBook.setFinishNum(mBook.getFinishNum()-1);
                }
                else if(mBook.getType() == Constant.TYPE_BEFORE)
                    mBook.setFinishNum(mBook.getFinishNum()-1);
                mBook.setChapterNum(mBook.getChapterNum()-1);
                mChapterNum.setText(mBook.getFinishNum()+"/"+mBook.getChapterNum()+"章");
                mGroupList.remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemChanged(mGroupList.size());
                if(!mChanged)
                    mChanged = true;
            }

            @Override
            public void onItemModify(int position, String str) {
                ChapterInfo chapterInfo = mGroupList.get(position);
                chapterInfo.setName(str);
                if(!mChanged)
                    mChanged = true;
            }
        });
    }

    private void createDialogs()
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在添加...");
        mProgressDialog.setCancelable(false);

        mDialog = new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                .setTitle("添加到")
                .setItems(
                        new String[]{"想读", "在读", "已读"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                                        mBook.setFinishNum(mBook.getChapterNum());
                                        MyApplication.setShouldUpdate(Constant.INDEX_BEFORE);
                                        break;
                                }
//                                DBOperate dbOperate = MyApplication.getDBOperateInstance();
//                                String uuid = dbOperate.insertBook(mBook, mGroupList);
//                                if(mBook.getType() == Constant.TYPE_AFTER)
//                                    dbOperate.setBookAfter(uuid, time);
//                                else if(mBook.getType() == Constant.TYPE_BEFORE)
//                                    dbOperate.setBookBefore(uuid, time);
                                mProgressDialog.show();
                                String time = TimeUtil.getNeedTime(System.currentTimeMillis());
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
                MyApplication.setShouldUpdate(Constant.INDEX_NOW);
                mProgressDialog.show();
                new AddTask(mBook, mGroupList, mHandler).execute(mBook.getStartTime(), mBook.getEndTime());

            }
        });
        builder.setNegativeButton("取消", null);
        mPlanDialog = builder.create();
    }

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mProgressDialog.dismiss();
            Toast.makeText(getBaseContext(), "已添加", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private void setListeners(){

        mName.setTag(0);
        mWordNum.setTag(1);
        mAuthor.setTag(2);
        mPress.setTag(3);
        mName.setOnKeyListener(mOnKeyListener);
        mWordNum.setOnKeyListener(mOnKeyListener);
        mAuthor.setOnKeyListener(mOnKeyListener);
        mPress.setOnKeyListener(mOnKeyListener);

        mName.setOnFocusChangeListener(mOnFocusChangeListener);
        mWordNum.setOnFocusChangeListener(mOnFocusChangeListener);
        mAuthor.setOnFocusChangeListener(mOnFocusChangeListener);
        mPress.setOnFocusChangeListener(mOnFocusChangeListener);

        mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                        Constant.IMAGE);
            }
        });
        mIconText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                        Constant.IMAGE);
            }
        });
    }

    private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!mChanged)
                mChanged = true;
        }
    };

    private View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            int tag=(Integer)(v.getTag());
            if(keyCode == KeyEvent.KEYCODE_ENTER&&event.getAction()==KeyEvent.ACTION_UP) {
                switch (tag) {
                    case 0:
                        mWordNum.requestFocus();
                        break;
                    case 1:
                        mAuthor.requestFocus();
                        break;
                    case 2:
                        mPress.requestFocus();
                        break;
                }
            }
            return false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    private boolean checkIllegal()
    {
        if(mBook.getName().compareTo("") != 0 && mGroupList.size() >0)
            return true;
        if(mBook.getName().compareTo("") == 0 && mGroupList.size() <= 0)
            Toast.makeText(getBaseContext(), "信息不全请补充", Toast.LENGTH_SHORT).show();
        else if(mBook.getName().compareTo("") == 0)
            Toast.makeText(getBaseContext(), "请填写书名信息", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getBaseContext(), "请填写章节信息", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_create_finish)
        {
            //移动焦点,使最后改变生效
            mName.requestFocus();
            //记录添加或修改信息
            mBook.setName(mName.getText().toString().trim());
            mBook.setAuthor(mAuthor.getText().toString().trim());
            mBook.setPress(mPress.getText().toString().trim());
            mBook.setChapterNum(mGroupList.size());

            String wordStr = mWordNum.getText().toString().trim();
            if(wordStr.compareTo("") != 0)
                mBook.setWordNum(Long.parseLong(wordStr));
            if(!checkIllegal())
            {
                return super.onOptionsItemSelected(item);
            }

            if(mAction == Constant.ACTION_EDIT_BOOK)
            {
                if(!mChanged)
                {
                    finish();
                    return true;
                }
                Log.d("net", "edit book");
                Log.d("net", "write book to local");
                //保存书籍，章节信息
                mProgressDialog.show();
                new EditTask(mBook, mGroupList, mChapterList, mDeletePosList, mEditHandler).execute();

                //update all

            }
            else
            {
                Log.d("net", "create book to local");
                mDialog.show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Handler mEditHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MyApplication.setShouldUpdate(Constant.INDEX_AFTER);
            MyApplication.setShouldUpdate(Constant.INDEX_NOW);
            MyApplication.setShouldUpdate(Constant.INDEX_BEFORE);
            MyApplication.setShouldUpdate(Constant.INDEX_READ);

            Intent intent = new Intent();
            intent.putExtra(Constant.KEY_BOOK, mBook);
            setResult(RESULT_OK, intent);
            Toast.makeText(getBaseContext(), "已保存", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && null != data) {
            switch (requestCode) {
                case Constant.IMAGE:
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    if (picturePath != null) {
                        if(mIconText.getVisibility() == View.VISIBLE)
                            mIconText.setVisibility(View.GONE);
                        Picasso.with(this).load(new File(picturePath)).into(mIcon);
                        mBook.setUrl(picturePath);
                    }
                    break;
            }
        }
    }
}
