package fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import ui.DividerItemDecoration;
import util.Constant;
import util.TimeUtil;

/**
 * book operate
 * book type change(db deal with book change)
 * before to now:book type change,update
 * before to after:book type change,all chapters type change,update
 */
public class BeforeFragment extends Fragment implements NumFragment {
    private RecyclerView mRecycler;
    private AlertDialog mDialog;
    private AlertDialog mPlanDialog;
    private AlertDialog mDeleteDialog;
    private DatePicker mPicker;
    private EditText mTime;

    private SwipeRefreshLayout mRefresh;

    private int mPosition;
    private List<Book> mBookList;
    private BookRecyclerAdapter mAdapter;

    private static BeforeFragment mFragmentInstance;


    Bundle savedState;


    @Override
    public int getItemNum() {
        if(mBookList != null)
            return mBookList.size();
        return 0;
    }

    public static BeforeFragment newInstance()
    {
        Log.d("net", "before new instance");
        if(mFragmentInstance == null) {
            mFragmentInstance = new BeforeFragment();
            mFragmentInstance.setArguments(new Bundle());
        }
        return mFragmentInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("net", "before create");
        View root = inflater.inflate(R.layout.fragment_bookshelf_before, container, false);
        mRefresh = (SwipeRefreshLayout) root;
        mRecycler = (RecyclerView) root.findViewById(R.id.recycler_before);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setItemAnimator(new SlideInDownAnimator());
        mRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST, 5));
        mRefresh.setColorSchemeResources(
                android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_blue_bright);
        mRefresh.setOnRefreshListener(mRefreshListener);
        createDialogs();
        return root;
    }

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            load();
            setupAdapter();
            mRefresh.setRefreshing(false);
        }
    };

    //创建三个对话框：选项、计划、删除
    private void createDialogs()
    {
        mDialog = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog)
                .setItems(
                        new String[]{"温故知新", "删除此书"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which)
                                {
                                    case 0:
                                        mPlanDialog.show();
                                        break;
                                    case 1:
                                        mDeleteDialog.show();
                                        break;
                                }
                            }
                        })
                .create();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.plan_dialog, null);
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
                long endTime = startTime+days*24*60*60*1000;

                Log.d("net", "book to now, local");
                Book book = mBookList.get(mPosition);
                book.setType(Constant.TYPE_NOW);
                book.setFinishNum(0);
                book.setStartTime(TimeUtil.getNeedTime(startTime));
                book.setEndTime(TimeUtil.getNeedTime(endTime));
                //标记为在读，finishNum=0
                //所有章节标记为未读
                DBOperate dbOperate = MyApplication.getDBOperateInstance();
                dbOperate.setBookNow(book.getUUID(), book.getStartTime(), book.getEndTime());
                int status = Constant.STATUS_MOD;
                if(book.getStatus() == Constant.STATUS_ADD)
                    status = Constant.STATUS_ADD;
                dbOperate.setBookStatus(book.getUUID(), status);

                mBookList.remove(mPosition);
                mAdapter.notifyItemRemoved(mPosition);
                mAdapter.notifyDataSetChanged();

                NowFragment.executeLoad();
                MyApplication.setShouldUpdate(Constant.INDEX_NOW);
            }
        });
        builder.setNegativeButton("取消", null);
        mPlanDialog = builder.create();
        mDeleteDialog = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog)
                .setTitle("确定删除此书")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //删除此书及所有章节
                        Log.d("net", "delete book, local");
                        Book book = mBookList.get(mPosition);
                        DBOperate dbOperate = MyApplication.getDBOperateInstance();
                        dbOperate.setBookDelete(book.getUUID());
                        dbOperate.setBookStatus(book.getUUID(), Constant.STATUS_DEL);

                        mBookList.remove(mPosition);
                        mAdapter.notifyItemRemoved(mPosition);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("否", null)
                .create();
    }

    @Override
    public void onStart() {
        Log.d("net", "before start");
        super.onStart();

    }

    @Override
    public void onResume() {
        Log.d("net", "before resume");
        super.onResume();
        if (MyApplication.getUpdateFlag(Constant.INDEX_BEFORE) || !restoreStateFromArguments()) {
            // First Time, Initialize something here
            load();
        }
        setupAdapter();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("net", "before pause");
    }

    @Override
    public void onStop() {
        Log.d("net", "before stop");
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
        Log.d("net", "before destroy");

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
        mBookList = savedInstanceState.getParcelableArrayList(Constant.KEY_BOOKS);
        mAdapter = new BookRecyclerAdapter(getActivity(), mBookList);
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

    //从网络或本地数据库读取数据至list,并创建adapter
    private void load()
    {
        Log.d("net", "before load");
        Log.d("net", "before load from local");
        if ((mBookList = MyApplication.getDBOperateInstance().getBooks(Constant.TYPE_BEFORE)) == null)
            mBookList = new ArrayList<>();
        mAdapter = new BookRecyclerAdapter(getActivity(), mBookList);
        Log.d("net", "before finish load from local");
    }

    //绑定设置adapter
    private void setupAdapter()
    {
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mAdapter.setOnItemLongClickListener(mOnItemLongClickListener);
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
            mPosition = position;
            mDialog.show();
        }
    };

    public static void executeLoad()
    {
        if(mFragmentInstance != null) {
            mFragmentInstance.load();
            mFragmentInstance.setupAdapter();
        }
    }
}
