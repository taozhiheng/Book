package fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.hustunique.myapplication.DetailActivity;
import com.hustunique.myapplication.MyApplication;
import com.hustunique.myapplication.R;
import java.util.ArrayList;
import java.util.List;
import adapter.MyOnItemClickListener;
import adapter.MyOnItemLongClickListener;
import adapter.BookRecyclerAdapter;
import data.Book;
import data.DBOperate;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import service.QueryBooksTask;
import ui.DividerItemDecoration;
import util.Constant;
import util.TimeUtil;

/**
 * book operate
 * book type change(db deal with book change)
 * after to now:book type change,update
 * after to finish:book type change,all chapters type change,update
 * 持有数据：书籍id
 * 数据操作：
 * １改变书籍类型，改变数据库书籍类型，　typeS = type
 * ２删除书籍,标记为删除状态
 */
public class AfterFragment extends Fragment implements NumFragment {

    private RecyclerView mRecycler;
    private AlertDialog mDialog;
    private AlertDialog mPlanDialog;
    private AlertDialog mDeleteDialog;
    private DatePicker mPicker;
    private EditText mTime;

    private List<Book> mBookList;
    private BookRecyclerAdapter mAdapter;
    private int mPosition;

    private static AfterFragment mFragmentInstance;

    private final static boolean DEBUG = false;
    public final static String TAG = "life cycle-after";


    Bundle savedState;

    @Override
    public int getItemNum() {
        if( mBookList != null)
            return mBookList.size();
        return 0;
    }

    public static AfterFragment newInstance()
    {
        if(DEBUG)
            Log.d(TAG, "after new instance");
        if(mFragmentInstance == null) {
            mFragmentInstance = new AfterFragment();
            mFragmentInstance.setArguments(new Bundle());
        }
        return mFragmentInstance;
    }

    @Override
    public void onDetach() {
        if(DEBUG)
            Log.d(TAG, "after on detach");
        super.onDetach();
        mHandler.removeCallbacks(null);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(DEBUG)
            Log.d(TAG, "after create");
        View root = inflater.inflate(R.layout.fragment_bookshelf_after, container, false);
        mRecycler = (RecyclerView) root;
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setItemAnimator(new SlideInDownAnimator());
        mRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST, 5));
        createDialogs();

        return root;
    }


    //创建三个对话框：选项、计划、删除
    private void createDialogs()
    {
        mDialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT)
                .setItems(
                        new String[]{"开始阅读","已读完", "删除此书"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which)
                                {
                                    case 0:
                                        mTime.setText(null);
                                        mPlanDialog.show();
                                        break;
                                    case 1:
                                        //标记书已读完，finishNum = chapterNum
                                        //设置所有章节为已读状态

                                        if(DEBUG)
                                            Log.d(TAG, "book to before, local");
                                        Book book = mBookList.get(mPosition);
                                        book.setType(Constant.TYPE_BEFORE);
                                        book.setFinishNum(book.getChapterNum());

                                        DBOperate dbOperate = MyApplication.getDBOperateInstance();
                                        dbOperate.setBookBefore(
                                                book.getId(), TimeUtil.getNeedTime(System.currentTimeMillis()));


                                        mBookList.remove(mPosition);
                                        mAdapter.notifyItemRemoved(mPosition);
                                        mAdapter.notifyDataSetChanged();
                                        MyApplication.setShouldUpdate(Constant.INDEX_BEFORE);
                                        break;
                                    case 2:
                                        mDeleteDialog.show();
                                        break;
                                }
                            }
                        })
                .create();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.plan_dialog, null);
        mPicker = (DatePicker)view.findViewById(R.id.dialog_datePicker);
        mTime = (EditText)view.findViewById(R.id.dialog_time);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long startTime = TimeUtil.getTimeMillis(mPicker.getYear(), mPicker.getMonth(), mPicker.getDayOfMonth());
                long days = (mTime.getText().toString().length() <=0)? 1 : Long.parseLong(mTime.getText().toString());

                if(days == 0)
                    days = 1;
                long endTime = startTime+days*24*60*60*1000;
                if(DEBUG)
                    Log.d(TAG, "book to now, days:"+days);
                Book book = mBookList.get(mPosition);
                book.setType(Constant.TYPE_NOW);
                book.setStartTime(TimeUtil.getNeedTime(startTime));
                book.setEndTime(TimeUtil.getNeedTime(endTime));
                //标记为在读
                DBOperate dbOperate = MyApplication.getDBOperateInstance();
                dbOperate.setBookNow(book.getId(), book.getStartTime(), book.getEndTime());


                mBookList.remove(mPosition);
                mAdapter.notifyItemRemoved(mPosition);
                mAdapter.notifyDataSetChanged();

                NowFragment.executeLoad();
                MyApplication.setShouldUpdate(Constant.INDEX_NOW);
            }
        });
        builder.setNegativeButton("取消", null);
        mPlanDialog = builder.create();
        mDeleteDialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("确定删除此书")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //删除此书及所有章节
                        if(DEBUG)
                            Log.d(TAG, "delete book, local");
                        Book book = mBookList.get(mPosition);
                        DBOperate dbOperate = MyApplication.getDBOperateInstance();
                        dbOperate.setBookDelete(book.getId());

                        mBookList.remove(mPosition);
                        mAdapter.notifyItemRemoved(mPosition);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "已删除", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("否", null)
                .create();
    }

    @Override
    public void onStart() {
        if(DEBUG)
            Log.d(TAG, "after start");
        super.onStart();

    }

    @Override
    public void onResume() {
        if(DEBUG)
            Log.d(TAG, "after resume");
        super.onResume();
        if (MyApplication.getUpdateFlag(Constant.INDEX_AFTER) || !restoreStateFromArguments()) {
            // First Time, Initialize something here
            load();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(DEBUG)
            Log.d(TAG, "after pause");
    }

    @Override
    public void onStop() {
        if(DEBUG)
            Log.d(TAG, "after stop");
        super.onStop();
    }

    //保存恢复数据或刷新数据


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Restore State Here

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save State Here
        saveStateToArguments();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(DEBUG)
            Log.d(TAG, "after destroy view");

        // Save State Here
        saveStateToArguments();
    }

    ////////////////////
    // Don't Touch !!
    ////////////////////

    private void saveStateToArguments() {
        if (getView() != null)
            savedState = saveState();
        if (savedState != null) {
            Bundle b = getArguments();
            b.putBundle("internalSavedViewState", savedState);
        }
    }

    ////////////////////
    // Don't Touch !!
    ////////////////////

    private boolean restoreStateFromArguments() {
        Bundle b = getArguments();
        savedState = b.getBundle("internalSavedViewState");
        if (savedState != null) {
            restoreState();
            return true;
        }
        return false;
    }

    /////////////////////////////////
    // Restore Instance State Here
    /////////////////////////////////

    private void restoreState() {
        if (savedState != null) {
            // For Example
            //tv1.setText(savedState.getString("text"));
            onRestoreState(savedState);
        }
    }

    protected void onRestoreState(Bundle savedInstanceState) {
        mBookList =  savedInstanceState.getParcelableArrayList(Constant.KEY_BOOKS);
        if(mBookList == null)
            mBookList = new ArrayList<>();
        if(DEBUG)
            Log.d(TAG, "after restore, book list size:"+mBookList.size());
        if(mBookList.size() > 0)
            setupAdapter();
        else
            load();
    }

    //////////////////////////////
    // Save Instance State Here
    //////////////////////////////

    private Bundle saveState() {
        Bundle state = new Bundle();
        // For Example
        //state.putString("text", tv1.getText().toString());
        onSaveState(state);
        return state;
    }

    protected void onSaveState(Bundle outState) {
        outState.putParcelableArrayList(Constant.KEY_BOOKS, (ArrayList<Book>)mBookList);
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

    //从网络或本地数据库读取数据至list,并创建adapter
    private void load()
    {
        if(DEBUG)
            Log.d(TAG, "after load data");
        if(mBookList == null)
            mBookList = new ArrayList<>();
        else
            mBookList.clear();
        new QueryBooksTask(mBookList, mHandler).execute(Constant.TYPE_AFTER);
//        mBookList = MyApplication.getDBOperateInstance().getBooks(Constant.TYPE_AFTER);
        if(DEBUG)
            Log.d(TAG, "after finish start load data task");
    }

    //绑定设置adapter
    private void setupAdapter()
    {
        if(mAdapter == null) {
            mAdapter = new BookRecyclerAdapter(getActivity(), mBookList, false);
            mAdapter.setOnItemClickListener(mOnItemClickListener);
            mAdapter.setOnItemLongClickListener(mOnItemLongClickListener);
            mRecycler.setAdapter(mAdapter);
            if(DEBUG)
                Log.d(TAG, "after create and set adapter:" + mBookList.size());
        }
        else if(mRecycler.getAdapter() == null)
        {
            mRecycler.setAdapter(mAdapter);
            if(DEBUG)
                Log.d(TAG, "after set adapter:"+mBookList.size());
        }
        else
        {
            mAdapter.notifyDataSetChanged();
            if(DEBUG)
                Log.d(TAG, "after adapter notify:"+mBookList.size());

        }

    }

    private MyOnItemClickListener mOnItemClickListener = new MyOnItemClickListener() {
        @Override
        public void onItemClick(View view, Book book, int position) {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(Constant.KEY_ACTION, Constant.VIEW_BOOK);
            intent.putExtra(Constant.KEY_BOOK, book);
            startActivity(intent);
        }
    };

    private MyOnItemLongClickListener mOnItemLongClickListener = new MyOnItemLongClickListener() {
        @Override
        public void onItemLongClick(View view, Book book, int position) {
            mDialog.show();
            mPosition = position;
        }
    };


    public static void executeLoad()
    {
        if(DEBUG)
            Log.d(TAG, "after execute load");

        if(mFragmentInstance != null)
        {
            mFragmentInstance.load();
        }
    }
}
