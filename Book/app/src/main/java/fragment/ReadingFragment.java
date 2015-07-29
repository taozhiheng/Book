package fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hustunique.myapplication.MyApplication;
import com.hustunique.myapplication.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import adapter.MyOnItemFunctionListener;
import adapter.ReadingAdapter;
import data.Chapter;
import data.DBOperate;
import data.UserPref;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import ui.DividerItemDecoration;
import ui.StickyLayout;
import util.Constant;
import util.TimeUtil;

/**
 * operate
 * chapter type change
 * now to finish:chapter type change,book finishNum change,book type change,update(db deal with book change)
 * now to after:chapter type change,update
 */
public class ReadingFragment extends Fragment {

    private TextView mDate;
    private TextView mWords;
    private TextView mPeople;
    private RecyclerView mRecycler;
    private StickyLayout mStickyLayout;
    private FloatingActionButton mAdd;

    private SwipeRefreshLayout mRefresh;

    private ReadingAdapter mAdapter;
    private List<Chapter> mChapterList;
    private static int mPosition;

    private AddListener mListener;

    private static ReadingFragment mFragmentInstance;

    private RequestQueue mRequestQueue;

    Bundle savedState;

    public static ReadingFragment newInstance()
    {
        if(mFragmentInstance == null) {
            mFragmentInstance = new ReadingFragment();
            mFragmentInstance.setArguments(new Bundle());
        }
        return mFragmentInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reading, container, false);
        mRefresh = (SwipeRefreshLayout) root.findViewById(R.id.reading_refresh);
        mDate = (TextView) root.findViewById(R.id.reading_date);
        mWords = (TextView) root.findViewById(R.id.reading_words);
        mPeople = (TextView) root.findViewById(R.id.reading_people);
        mRecycler = (RecyclerView) root.findViewById(R.id.reading_recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setItemAnimator(new SlideInDownAnimator());
        mRecycler.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST, 5f));



        mAdd = (FloatingActionButton) root.findViewById(R.id.reading_add);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.addChapter();
            }
        });
        mStickyLayout =(StickyLayout)root.findViewById(R.id.sticky_layout);
        mStickyLayout.setOnGiveUpTouchEventListener(new StickyLayout.OnGiveUpTouchEventListener() {
            @Override
            public boolean giveUpTouchEvent(MotionEvent event) {
                    View view = mRecycler.getChildAt(0);
                    if (view != null && view.getTop() >= 0)
                        return true;
                    else if(mRecycler.getChildCount() == 0 && mRecycler.getTop()>=0)
                        return true;
                return false;
            }
        });
        return root;
    }

    @Override
    public void onStart() {
        Log.d("net", "reading start");
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        checkTime();
        if (MyApplication.getUpdateFlag(Constant.INDEX_READ) || !restoreStateFromArguments()) {
            // First Time, Initialize something here
            mWords.performClick();
            load();
            setupAdapter();
        }
        setupAdapter();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("net", "reading pause");
    }

    @Override
    public void onStop() {
        Log.d("net", "reading stop");
        super.onStop();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (AddListener)activity;
        IntentFilter filter = new IntentFilter(Intent.ACTION_DATE_CHANGED);
        getActivity().registerReceiver(mDateReceiver, filter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(mDateReceiver);
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
        mChapterList = savedInstanceState.getParcelableArrayList(Constant.KEY_BOOKS);
        if(mChapterList == null)
            mChapterList = new ArrayList<>();
        mAdapter = new ReadingAdapter(getActivity(), mChapterList);
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
        outState.putParcelableArrayList(Constant.KEY_BOOKS, (ArrayList<Chapter>)mChapterList);
    }


    private BroadcastReceiver mDateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkTime();
        }
    };

    private void checkTime()
    {
        String time = TimeUtil.getDateTimeString(Calendar.getInstance(), "yyyy.MM.dd");
        if(!time.equals(mDate.getText().toString()))
        {
            mDate.setText(time);
            getWords();
        }
    }

    private void getWords()
    {
        long millis = System.currentTimeMillis();
        String time = TimeUtil.getNeedTime(millis);
        String time1 = TimeUtil.getNeedTime(millis + 24 * 60 * 60 * 1000);
        String time2 = TimeUtil.getNeedTime(millis + 24*60*60*1000*2);
        String urlHead = MyApplication.getUrlHead() + Constant.URL_SAYING+"?date=";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(new JsonObjectRequest(Request.Method.GET, urlHead + time,
                null, new WordsListener(0),
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        UserPref.init(getActivity());
                        String last = UserPref.getTime();
                        String current =
                                TimeUtil.getDateTimeString(Calendar.getInstance(), "yyyy.MM.dd");
                        if(!current.equals(last))
                        {
                            UserPref.setTime(current);
                            UserPref.setWords(0, UserPref.getWords(1));
                            UserPref.setWords(1, UserPref.getWords(2));
                            UserPref.setWords(2, null);
                        }
                        String content = UserPref.getWords(0);
                        if(content != null) {
                            String saying = content.substring(0, content.indexOf('&'));
                            String author = content.substring(content.indexOf('&')+1);
                            mPeople.setText(author);
                            mWords.setText("　　" + saying);
                        }
                    }
                }));
        requestQueue.add(new JsonObjectRequest(Request.Method.GET, urlHead+time1,
                null, new WordsListener(1), null));
        requestQueue.add(new JsonObjectRequest(Request.Method.GET, urlHead+time2,
                null, new WordsListener(2), null));
        requestQueue.start();
    }

    class WordsListener implements Response.Listener<JSONObject>{

        public int mTimeFlag;

        public WordsListener(int flag)
        {
            this.mTimeFlag = flag;
        }

        @Override
        public void onResponse(JSONObject response) {
            Log.d("net", "word:"+response);
            try {
                String author = response.getString("name");
                String saying = response.getString("saying");
                UserPref.init(getActivity());
                if(mTimeFlag == 0)
                {
                    mPeople.setText(author);
                    mWords.setText("　　" + saying);
                    String current =
                            TimeUtil.getDateTimeString(Calendar.getInstance(), "yyyy.MM.dd");
                    UserPref.setTime(current);
                }
                UserPref.setWords(mTimeFlag, saying+"&"+author);
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    //从网络或本地数据库加载数据至list,并创建adapter
    private void load()
    {
        //从本地加载数据
        Log.d("net", "reading chapters from local");
        if ((mChapterList = MyApplication.getDBOperateInstance().getNowChapters()) == null)
            mChapterList = new ArrayList<>();
        mAdapter = new ReadingAdapter(getActivity(), mChapterList);
        Log.d("net", "finish reading chapters from local");
    }

    //绑定设置adapter
    private void setupAdapter()
    {
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemFunctionListener(mOnItemFunctionListener);
    }

    private MyOnItemFunctionListener mOnItemFunctionListener = new MyOnItemFunctionListener() {
        @Override
        public void onItemFunction(View view, Chapter chapter, int position, int function) {
            mPosition = position;
            Log.d("net","function position:"+position);
            switch (function)
            {
                case 0://finish
                    //章节标记为已完成
                    //重设书的完成数，检查书是否完成
                    Log.d("net", "write chapter finish to local");
                    chapter.setType(Constant.TYPE_BEFORE);
                    resetChapter(chapter);

                    mChapterList.remove(mPosition);
                    mAdapter.notifyItemRemoved(mPosition);
                    mAdapter.notifyItemRangeChanged(mPosition, mAdapter.getItemCount()-mPosition);
                    Toast.makeText(getActivity(), "已读完", Toast.LENGTH_SHORT).show();
                    MyApplication.setShouldUpdate(Constant.INDEX_NOW);
                    MyApplication.setShouldUpdate(Constant.INDEX_BEFORE);
                    break;
                case 1://top
                    Log.d("net", "top, local");
                    mChapterList.remove(mPosition);
                    mChapterList.add(0, chapter);
                    mAdapter.notifyItemRangeChanged(0, mPosition+1);
                    Toast.makeText(getActivity(), "已置顶", Toast.LENGTH_SHORT).show();
                    break;
                case 2://delete
                    //标记章节为未读
                    Log.d("net", "delete, local");
                    chapter.setType(Constant.TYPE_AFTER);
                    mChapterList.remove(mPosition);
                    resetChapter(chapter);

                    mAdapter.notifyItemRemoved(mPosition);
                    mAdapter.notifyItemRangeChanged(mPosition, mAdapter.getItemCount()-mPosition);
                    Toast.makeText(getActivity(), "已取消", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void resetChapter(Chapter chapter)
    {
        DBOperate dbOperate = MyApplication.getDBOperateInstance();
        dbOperate.setChapterType(chapter.getBookId(),
                chapter.getId(), chapter.getType());
        int status = Constant.STATUS_MOD;
        if(chapter.getStatus() == Constant.STATUS_ADD)
            status = Constant.STATUS_ADD;
        dbOperate.setChapterStatus(chapter.getBookId(), chapter.getId(), status);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null)
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
    }


    public static void executeLoad()
    {
        if(mFragmentInstance != null) {
            mFragmentInstance.load();
            mFragmentInstance.setupAdapter();
        }
    }

}
