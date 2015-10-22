package fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hustunique.myapplication.MyApplication;
import com.hustunique.myapplication.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.MyOnItemFunctionListener;
import adapter.ReadingAdapter;
import data.Chapter;
import data.DBOperate;
import data.UserPref;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import service.QueryChaptersTask;
import ui.DividerItemDecoration;
import ui.StickyLayout;
import util.Constant;
import util.TimeUtil;

/**
 * operate
 * chapter type change
 * now to finish:chapter type change,book finishNum change,book type change,update(db deal with book change)
 * now to after:chapter type change,update
 * 持有数据：bookId, chapterId
 * 数据操作，改变章节类型：在数据库改变章节类型，并且设置typeS = type,需要时检查书籍是否已经完成
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

    private final static int[] guideResIds = {R.drawable.left_guide, R.drawable.right_guide, R.drawable.add_chapter_guide};

    private RequestQueue mRequestQueue;

    Bundle savedState;

    public final static String TAG = "life cycle-reading";

    public static ReadingFragment newInstance()
    {
        if(mFragmentInstance == null) {
            mFragmentInstance = new ReadingFragment();
            mFragmentInstance.setArguments(new Bundle());
        }
        return mFragmentInstance;
    }

    public ReadingFragment()
    {
        if(mFragmentInstance != null)
            mFragmentInstance = null;
        mFragmentInstance = this;
        mFragmentInstance.setArguments(new Bundle());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "reading create view");
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
                Log.d(TAG, "reading add button on click");
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
                else if (mRecycler.getChildCount() == 0 && mRecycler.getTop() >= 0)
                    return true;
                return false;
            }
        });
        return root;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "reading start");
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
//        if(UserPref.getFirstGuide(0)) {
//            GuideUtil guideUtil = GuideUtil.getInstance();
//            guideUtil.setClearGuideListener(new GuideUtil.ClearGuideListener() {
//                @Override
//                public void clearGuide() {
//                    UserPref.clearFirstGuide(0);
//                }
//            });
//            guideUtil.setFirst(true);
//            guideUtil.initGuide(getActivity(), guideResIds);
//        }
        MobclickAgent.onPageStart("Today Reading Fragment");
        checkTime();
        if (MyApplication.getUpdateFlag(Constant.INDEX_READ) || !restoreStateFromArguments()) {
            // First Time, Initialize something here
            load();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Today Reading Fragment");
        Log.d(TAG, "reading pause");
    }

    @Override
    public void onStop() {
        Log.d(TAG, "reading stop");
        super.onStop();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "reading attach");
        UserPref.init(getActivity());
        mListener = (AddListener)activity;
        IntentFilter filter = new IntentFilter(Intent.ACTION_DATE_CHANGED);
        getActivity().registerReceiver(mDateReceiver, filter);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "reading detach");
        getActivity().unregisterReceiver(mDateReceiver);
        mHandler.removeCallbacks(null);
    }


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
        Log.d(TAG, "reading destroy view");

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
            if(b != null)
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
        Log.d(TAG, "restore state");
        mChapterList =  savedInstanceState.getParcelableArrayList(Constant.KEY_CHAPTERS);
        if(mChapterList == null)
            mChapterList = new ArrayList<>();
        if(mChapterList.size() > 0)
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
        outState.putParcelableArrayList(Constant.KEY_CHAPTERS, (ArrayList<Chapter>)mChapterList);
    }


    private BroadcastReceiver mDateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkTime();
        }
    };

    /**
     * 检查显示的名言师傅是今日的，若不是则尝试从本地获取数据
     * 在每次onResume,或者收到日期改变的广播时调用
     * */
    private void checkTime()
    {
        if(mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(getActivity());
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
        mRequestQueue.add(new JsonObjectRequest(Request.Method.GET, urlHead + time,
                null, new WordsListener(0),
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //网络查询失败，则重新设置本地数据，并使用本地数据更新名言
                        long last = UserPref.getTime();
                        long current = TimeUtil.getTodayAt(0).getTimeInMillis();
                        int days = (int)(current-last)/(24*60*60*1000);
                        if(days > 0 && days < 3)
                        {
                            UserPref.setTime(current);
                            UserPref.setWords(0, UserPref.getWords(days));
                            UserPref.setWords(1, UserPref.getWords(days+1));
                            UserPref.setWords(2, null);
                        }
                        String content = UserPref.getWords(0);
                        if(content != null) {
                            String saying = content.substring(0, content.indexOf('&'));
                            String author = content.substring(content.indexOf('&')+1);
                            mPeople.setText(author);
                            mWords.setText("　　" + saying);
                        }
                        else
                        {
                            queryToday();
                        }
                    }
                }));
        mRequestQueue.add(new JsonObjectRequest(Request.Method.GET, urlHead + time1,
                null, new WordsListener(1), null));
        mRequestQueue.add(new JsonObjectRequest(Request.Method.GET, urlHead+time2,
                null, new WordsListener(2), null));
        mRequestQueue.start();
    }

    class WordsListener implements Response.Listener<JSONObject>{

        public int mTimeFlag;

        public WordsListener(int flag)
        {
            this.mTimeFlag = flag;
        }

        @Override
        public void onResponse(JSONObject response) {
            Log.d(TAG, "word:"+response);
            //网络查询成功，则将结果写入本地，并使用结果来更新名言
            try {
                String author = response.getString("name");
                String saying = response.getString("saying");
                UserPref.init(getActivity());
                if(mTimeFlag == 0)
                {
                    mPeople.setText(author);
                    mWords.setText("　　" + saying);
                    long current = TimeUtil.getTodayAt(0).getTimeInMillis();
                    UserPref.setTime(current);
                }
                UserPref.setWords(mTimeFlag, saying+"&"+author);
            }catch (JSONException e)
            {
                e.printStackTrace();
                if(mTimeFlag == 0)
                {
                    queryToday();
                }
            }
        }
    }

    private void queryToday()
    {
        long millis = System.currentTimeMillis();
        String time = TimeUtil.getNeedTime(millis);
        String urlHead = MyApplication.getUrlHead() + Constant.URL_SAYING+"?date=";
        mRequestQueue.add(new JsonObjectRequest(Request.Method.GET, urlHead + time,
                null, new WordsListener(0),
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //网络查询失败，则重新设置本地数据，并使用本地数据更新名言
                        long last = UserPref.getTime();
                        long current = TimeUtil.getTodayAt(0).getTimeInMillis();
                        int days = (int)(current-last)/(24*60*60*1000);
                        if(days > 0 && days < 3)
                        {
                            UserPref.setTime(current);
                            UserPref.setWords(0, UserPref.getWords(days));
                            UserPref.setWords(1, UserPref.getWords(days+1));
                            UserPref.setWords(2, null);
                        }
                        String content = UserPref.getWords(0);
                        if(content != null) {
                            String saying = content.substring(0, content.indexOf('&'));
                            String author = content.substring(content.indexOf('&')+1);
                            mPeople.setText(author);
                            mWords.setText("　　" + saying);
                        }
                        else
                        {
                            queryToday();
                        }
                    }
                }));
        mRequestQueue.start();
    }

    //从网络或本地数据库加载数据至list,并创建adapter
    private void load()
    {
        //从本地加载数据
        Log.d(TAG, "reading chapters from local");
        if(mChapterList == null)
            mChapterList = new ArrayList<>();
        else
            mChapterList.clear();
        new QueryChaptersTask(mChapterList, mHandler).execute((long)-1);
        Log.d(TAG, "finish reading chapters from local");
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

    //绑定设置adapter
    private void setupAdapter()
    {
        if(mAdapter == null) {
            mAdapter = new ReadingAdapter(getActivity(), mChapterList);
            mAdapter.setOnItemFunctionListener(mOnItemFunctionListener);
            mRecycler.setAdapter(mAdapter);
            Log.d(TAG, "reading create and set adapter:"+mChapterList.size());
        }
        else if(mRecycler.getAdapter() == null)
        {
            mRecycler.setAdapter(mAdapter);
            Log.d(TAG, "reading set adapter:"+mChapterList.size());
        } else {
            mAdapter.notifyDataSetChanged();
            Log.d(TAG, "reading adapter notify:"+mChapterList.size());
        }
    }

    private MyOnItemFunctionListener mOnItemFunctionListener = new MyOnItemFunctionListener() {
        @Override
        public void onItemFunction(View view, Chapter chapter, int position, int function) {
            mPosition = position;
            Log.d(TAG,"function position:"+position);
            switch (function)
            {
                case 0://finish
                    //章节标记为已完成
                    //重设书的完成数，检查书是否完成
                    Log.d(TAG, "write chapter finish to local");
                    DBOperate dbOperate = MyApplication.getDBOperateInstance();
                    dbOperate.setChapterType(chapter.getId(), Constant.TYPE_BEFORE);
                    dbOperate.checkBookFinish(chapter.getBookId());

                    mChapterList.remove(mPosition);
                    mAdapter.notifyItemRemoved(mPosition);
                    mAdapter.notifyItemRangeChanged(mPosition, mAdapter.getItemCount() - mPosition);
                    Toast.makeText(getActivity(), "已读完", Toast.LENGTH_SHORT).show();
                    NowFragment.executeLoad();
                    BeforeFragment.executeLoad();
                    MyApplication.setShouldUpdate(Constant.INDEX_NOW);
                    MyApplication.setShouldUpdate(Constant.INDEX_BEFORE);
                    break;
                case 1://top
                    Log.d(TAG, "top, local");
                    mChapterList.remove(mPosition);
                    mChapterList.add(0, chapter);
//                    mAdapter.notifyItemRangeChanged(0, mPosition+1);
                    mAdapter.notifyItemChanged(0);
                    mAdapter.notifyItemChanged(mPosition);
                    Toast.makeText(getActivity(), "已置顶", Toast.LENGTH_SHORT).show();
                    break;
                case 2://delete
                    //标记章节为未读
                    Log.d(TAG, "delete, local");
                    int type = Constant.TYPE_AFTER;
                    if(chapter.getType() == Constant.TYPE_REPEAT)
                        type = Constant.TYPE_BEFORE;
                    MyApplication.getDBOperateInstance().setChapterType(chapter.getId(), type);

                    mChapterList.remove(mPosition);
                    mAdapter.notifyItemRemoved(mPosition);
                    mAdapter.notifyItemRangeChanged(mPosition, mAdapter.getItemCount()-mPosition);
                    Toast.makeText(getActivity(), "已取消", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null)
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
    }


    public static void executeLoad()
    {
        Log.d(TAG, "reading execute load");
        if(mFragmentInstance != null) {
            mFragmentInstance.load();
        }
    }

}
