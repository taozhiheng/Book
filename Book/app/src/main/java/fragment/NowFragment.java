package fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.Calendar;
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
 * now to before:book type change,all chapters type change,update
 * now to after:book type change,all chapters type change,update
 * 持有数据：id
 * 数据操作:
 * 改变书籍类型，改变数据库书籍类型，typeS=type
 * 改变书籍内容，改变数据库书籍内容，status=modify
 * 删除书籍，标记书籍删除状态
 */
public class NowFragment extends Fragment implements NumFragment {

    private RecyclerView mRecycler;
    private AlertDialog mDialog;
    private AlertDialog mPlanDialog;
    private AlertDialog mDeleteDialog;
    private DatePicker mPicker;
    private EditText mTime;

    private int mPosition;
    private List<Book> mBookList;
    private BookRecyclerAdapter mAdapter;

    private static NowFragment mFragmentInstance;

    Bundle savedState;

    public final static String TAG = "life cycle-before";

    @Override
    public int getItemNum() {
        if(mBookList != null)
            return mBookList.size();
        return 0;
    }


    public static NowFragment newInstance() {
        Log.d(TAG, "now new instance");
        if (mFragmentInstance == null) {
            mFragmentInstance = new NowFragment();
            mFragmentInstance.setArguments(new Bundle());
        }
        return mFragmentInstance;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "now on detach");
        super.onDetach();
        mHandler.removeCallbacks(null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "now create");
        View root = inflater.inflate(R.layout.fragment_bookshelf_now, container, false);
        mRecycler = (RecyclerView) root;
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setItemAnimator(new SlideInDownAnimator());
        mRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST, 5f));
        createDialogs();
        return root;
    }


    //创建三个对话框：选项、计划、删除
    private void createDialogs()
    {
        mDialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT)
                .setItems(
                        new String[]{"修改阅读计划","已完成", "放弃治疗", "删除此书"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which){
                                    case 0:
                                        mTime.setText(null);
                                        mPlanDialog.show();
                                        break;
                                    case 1:
                                        //标记为已读，finishNum = chapterNum
                                        //所有章节标记为完成
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
                                        ReadingFragment.executeLoad();
                                        BeforeFragment.executeLoad();
                                        MyApplication.setShouldUpdate(Constant.INDEX_READ);
                                        MyApplication.setShouldUpdate(Constant.INDEX_BEFORE);
                                        break;
                                    case 2:

                                        Log.d(TAG, "book to after, local");
                                        Book book2 = mBookList.get(mPosition);
                                        book2.setType(Constant.TYPE_AFTER);
                                        book2.setFinishNum(0);

                                        DBOperate db = MyApplication.getDBOperateInstance();
                                        db.setBookAfter(
                                                book2.getId(), TimeUtil.getNeedTime(System.currentTimeMillis()));


                                        mBookList.remove(mPosition);
                                        mAdapter.notifyItemRemoved(mPosition);
                                        mAdapter.notifyDataSetChanged();
                                        ReadingFragment.executeLoad();
                                        AfterFragment.executeLoad();
                                        MyApplication.setShouldUpdate(Constant.INDEX_READ);
                                        MyApplication.setShouldUpdate(Constant.INDEX_AFTER);
                                        break;
                                    case 3:
                                        int count = MyApplication.getDBOperateInstance().
                                                getBookReadingNum(mBookList.get(mPosition).getId());
                                        mDeleteDialog.setMessage("本书有"+count+"章在今日阅读计划中,是否仍要删除");
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
                Log.d(TAG, "start --"+which);
                long startTime = TimeUtil.getTimeMillis(mPicker.getYear(), mPicker.getMonth(), mPicker.getDayOfMonth());
                long days = (mTime.getText().toString().length() <=0)? 1 : Long.parseLong(mTime.getText().toString());
                if(days == 0)
                    days = 1;
                long endTime = startTime+days*24*60*60*1000;
                Book book = mBookList.get(mPosition);
                Log.d(TAG, "end --"+which);
                book.setStartTime(TimeUtil.getNeedTime(startTime));
                book.setEndTime(TimeUtil.getNeedTime(endTime));
                    //标记为在读
                DBOperate dbOperate = MyApplication.getDBOperateInstance();
                dbOperate.updateBook(book);
                int status = Constant.STATUS_MOD;
                if(book.getStatus() == Constant.STATUS_ADD)
                    status = Constant.STATUS_ADD;
                dbOperate.setBookStatus(book.getId(), status);

                mAdapter.notifyItemChanged(mPosition);
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

                        MyApplication.getDBOperateInstance().setBookDelete(mBookList.get(mPosition).getId());
                        mBookList.remove(mPosition);

                        mAdapter.notifyItemRemoved(mPosition);
                        mAdapter.notifyDataSetChanged();
                        ReadingFragment.executeLoad();
                        MyApplication.setShouldUpdate(Constant.INDEX_READ);
                        Toast.makeText(getActivity(), "已删除", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("否", null)
                .create();
    }


    //打印log
    @Override
    public void onStart() {
        Log.d(TAG, "now start");
        super.onStart();

    }

    @Override
    public void onResume() {
        Log.d(TAG, "now resume");
        super.onResume();
        if (MyApplication.getUpdateFlag(Constant.INDEX_NOW) || !restoreStateFromArguments()) {
            // First Time, Initialize something here
            load();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "now pause");
    }

    @Override
    public void onStop() {
        Log.d(TAG, "now stop");
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
        Log.d(TAG, "before destroy");

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

    public Bundle getSavedState() {
        return savedState;
    }

    public void setSavedState(Bundle savedState) {
        this.savedState = savedState;
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
        Log.d(TAG, "now restore, book list size:"+mBookList.size());
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
        Log.d(TAG, "now load data");
        if(mBookList == null)
            mBookList = new ArrayList<>();
        else
            mBookList.clear();
        new QueryBooksTask(mBookList, mHandler).execute(Constant.TYPE_NOW);
        Log.d(TAG, "now finish load data");
    }

    //绑定设置adapter
    private void setupAdapter()
    {
        if(mAdapter == null) {
            mAdapter = new BookRecyclerAdapter(getActivity(), mBookList, true);
            mAdapter.setOnItemClickListener(mOnItemClickListener);
            mAdapter.setOnItemLongClickListener(mOnItemLongClickListener);
            mRecycler.setAdapter(mAdapter);
            Log.d(TAG, "now create and set adapter:" + mBookList.size());
        }
        else if(mRecycler.getAdapter() == null)
        {
            mRecycler.setAdapter(mAdapter);
            Log.d(TAG, "now set adapter:"+mBookList.size());
        }
        else
        {
            mAdapter.notifyDataSetChanged();
            Log.d(TAG, "now adapter notify:"+mBookList.size());
        }
    }

    private MyOnItemClickListener mOnItemClickListener = new MyOnItemClickListener() {
        @Override
        public void onItemClick(View view, Book book, int position) {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(Constant.KEY_ACTION, Constant.ADD_CHAPTER);
            intent.putExtra(Constant.KEY_BOOK, book);
            startActivityForResult(intent, Constant.ADD_CHAPTER);
        }
    };

    private MyOnItemLongClickListener mOnItemLongClickListener = new MyOnItemLongClickListener() {
        @Override
        public void onItemLongClick(View view, Book book, int position) {
            mPosition = position;
            mDialog.show();
        }
    };


    public static void executeLoad()
    {
        Log.d(TAG, "now execute load");
        if(mFragmentInstance != null) {
            mFragmentInstance.load();
        }
    }
}
