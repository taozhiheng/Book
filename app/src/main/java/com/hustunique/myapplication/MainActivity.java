package com.hustunique.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.zhuge.analysis.stat.ZhugeSDK;

import net.MyJsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

import adapter.ViewPagerAdapter;
import data.DBOperate;
import data.UserPref;
import fragment.AddListener;
import fragment.AfterFragment;
import fragment.BeforeFragment;
import fragment.BookshelfFragment;
import fragment.CalendarFragment;
import fragment.FragmentCreator;
import fragment.NowFragment;
import fragment.ReadingFragment;
import service.Counter;
import service.WebService;
import util.Constant;

public class MainActivity extends AppCompatActivity implements AddListener{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mToggle;
    private String[] titles = new String[]{"今日阅读", "我的书架"};

//    private MyFragmentAdapter mFragmentPagerAdapter;
    private LinearLayout mHeader;
    private ImageView mIcon;
    private TextView mUser;
//    private TextView mEmail;
//    private FrameLayout mContainer;


    private AlertDialog mDialog;

    private RequestQueue mRequestQueue;

    private AlertDialog mChoseDialog;

    private ProgressDialog mProgressDialog;

    private ViewPager mViewPager;

    private final static String TAG = "life cycle-main";

    private int mCurrentItem = 0;

    private final static boolean DEBUG = true;

    private void autoLogin()
    {
        if(DEBUG)
            Log.d(TAG, "main activity auto login");
        mRequestQueue.add(new MyJsonObjectRequest(
                        Request.Method.GET,
                        MyApplication.getUrlHead() + Constant.URL_USER_INFO,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(DEBUG)
                                    Log.d(TAG, "auto login:" + response.toString());
                                try {
                                    //读取用户基本信息
                                    String mail = response.getString("mail");
                                    String username = response.getString("username");
                                    String sexStr = response.getString("sex");
                                    boolean sex = false;
                                    if (sexStr.equals("true"))
                                        sex = true;
                                    String avatar = response.getString("avatar");
                                    //记录基本信息
                                    MyApplication.setUser(username);
                                    MyApplication.setUserSex(sex);
                                    if (!avatar.contains("http"))
                                        avatar = MyApplication.getUrlHead() + avatar;
                                    MyApplication.setUserUrl(avatar);
                                    MyApplication.setUserMail(mail);
                                    MyApplication.setUserOnLine(true);

                                    JSONObject personObject = new JSONObject();
                                    //预置字段部分示例
                                    personObject.put("avatar", avatar);
                                    personObject.put("name", username);
                                    if (sex)
                                        personObject.put("gender", "女");
                                    else
                                        personObject.put("gender", "男");
                                    personObject.put("email", mail);

                                    //进行标识，第二个参数为您在您的APP中标识用户的ID
                                    ZhugeSDK.getInstance().identify(getApplicationContext(), mail,
                                            personObject);


                                    //通知fragment刷新
                                    MyApplication.setShouldUpdate(Constant.INDEX_READ);
                                    MyApplication.setShouldUpdate(Constant.INDEX_AFTER);
                                    MyApplication.setShouldUpdate(Constant.INDEX_NOW);
                                    MyApplication.setShouldUpdate(Constant.INDEX_BEFORE);

                                    setUserInfo();
                                    checkData(mail, mail);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        })
        );
//        mRequestQueue.start();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(DEBUG)
            Log.d(TAG, "main activity on create");
//        UserPref.init(this);
//        if(UserPref.getFirstUse())
//        {
//            Intent intent=new Intent(this,GuideActivity.class);
//            startActivity(intent);
//            finish();
//        }
//        if(!getIntent().getBooleanExtra("start",false))
//        {
//            Intent intent=new Intent(this,StartActivity.class);
//            startActivity(intent);
//            finish();
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mHeader = (LinearLayout) mNavigationView.findViewById(R.id.header);
        mIcon = (ImageView) mHeader.findViewById(R.id.drawer_user_icon);
        mUser = (TextView) mHeader.findViewById(R.id.drawer_user_name);
//        mEmail = (TextView) mHeader.findViewById(R.id.drawer_user_email);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
//        mContainer = (FrameLayout) findViewById(R.id.main_container);
        mViewPager = (ViewPager) findViewById(R.id.main_container);

//        mDrawerLayout.setStatusBarBackground(R.color.accent_material_dark);
        init();

        mRequestQueue = Volley.newRequestQueue(this);
        MobclickAgent.openActivityDurationTrack(false);

        ((MyApplication)getApplication()).init();
        if(DEBUG)
            Log.d(TAG, "start service");
        startService(new Intent(this, WebService.class));
        IntentFilter filter = new IntentFilter("com.hustunique.myapplication.MAIN_RECEIVER");
        registerReceiver(mReceiver, filter);

        MyApplication.setAuthorization(UserPref.getUserAuth());
        autoLogin();

    }

    private void init()
    {
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_navigation_menu);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Picasso.with(this).load(R.drawable.ic_user_icon).resize(100, 100).into(mIcon);
        mDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("退出")
                .setMessage("你确定要退出应用吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.setUserOnLine(false);
                        MyApplication.setAuthorization(null);
                        MyApplication.setUser(null);
                        MyApplication.setUserSex(false);
                        MyApplication.setUserMail(null);
                        MyApplication.setUserUrl(null);
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .setNegativeButton("再看会儿", null)
                .create();
        mChoseDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                .setMessage("检测到本地数据与您当前帐号不一致,将进行同步,您希望怎样同步?")
                .setPositiveButton("以本地为准", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sync(Constant.CHOICE_LOCAL);
                    }
                })
                .setNegativeButton("以帐号为准", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sync(Constant.CHOICE_WEB);
                    }
                })
                .create();
        mChoseDialog.setCancelable(false);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在同步...");
        mProgressDialog.setCancelable(false);

        if (mToolbar != null) {
            mToolbar.setTitle(titles[0]);
            setSupportActionBar(mToolbar);
        }

        mHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MyApplication.getUserOnLine())
                    startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), Constant.LOGIN);
                else
                    startActivityForResult(new Intent(MainActivity.this, PersonActivity.class), Constant.PERSON);
            }
        });

        mToggle = new ActionBarDrawerToggle(
                this,                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                mToolbar,             /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );
        mDrawerLayout.setDrawerListener(mToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mToggle.syncState();
            }
        });
//        mFragmentPagerAdapter = new MyFragmentAdapter(getSupportFragmentManager());

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.drawer_reading:
                        setCurrentItem(0);
                        break;
                    case R.id.drawer_bookshelf:
                        setCurrentItem(1);
                        break;
//                    case R.id.drawer_calendar:
//                        setCurrentItem(2);
//                        break;
//                    case R.id.drawer_sync:
//                        if(MyApplication.getUserOnLine() && MyApplication.getAuthorization() != null) {
//                            mChoseDialog.setCancelable(true);
//                            mChoseDialog.show();
//                        }
//                        break
                    case R.id.drawer_feedback:
                        startActivity(new Intent(MainActivity.this, FeedbackActivity.class));
                        break;
                    case R.id.drawer_about:
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        break;
                    case R.id.drawer_exit:
                        mDialog.show();
                        break;
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        mViewPager.setAdapter(new ViewPagerAdapter(
                getSupportFragmentManager(),
                new FragmentCreator() {
                    @Override
                    public Fragment newInstance(int position) {
                        switch (position)
                        {
                            case 0:
                                return new ReadingFragment();
                            case 1:
                                return new BookshelfFragment();
//                            case 2:
//                                return new CalendarFragment();
                        }
                        return null;
                    }
                },
                titles
        ));
//        setCurrentItem(0);
    }



    @Override
    public void addChapter() {
        setCurrentItem(1);
    }


    private void setCurrentItem(int position)
    {
        if(mViewPager != null && position < 2)
        {
            if(position == 0)
            {
                mNavigationView.getMenu().findItem(R.id.drawer_reading).setChecked(true);
                mNavigationView.getMenu().findItem(R.id.drawer_bookshelf).setChecked(false);

            }
            else
            {
                mNavigationView.getMenu().findItem(R.id.drawer_reading).setChecked(false);
                mNavigationView.getMenu().findItem(R.id.drawer_bookshelf).setChecked(true);
            }
            mToolbar.setTitle(titles[position]);
            mViewPager.setCurrentItem(position);
            mCurrentItem = position;
        }
    }


    @Override
    protected void onResume() {
        if(DEBUG)
            Log.d(TAG, "main activity on resume:"+this);

        super.onResume();
        MobclickAgent.onResume(this);
        ZhugeSDK.getInstance().init(getApplicationContext());

    }


    @Override
    protected void onPause() {
        if(DEBUG)
            Log.d(TAG, "main activity on pause");
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        if(DEBUG)
            Log.d(TAG, "main activity on stop");
        super.onStop();
    }



    @Override
    protected void onStart() {
        if(DEBUG)
            Log.d(TAG, "main activity on start");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        if(DEBUG)
            Log.d(TAG, "main activity on restart");

        super.onRestart();
    }

    private void setUserInfo()
    {
        String str;
        Picasso.with(this).load(R.drawable.ic_user_icon).resize(100, 100).into(mIcon);
        if((str=MyApplication.getUserUrl())!= null && !str.contains("null"))
            MyApplication.getPicasso().load(Uri.parse(str))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .resize(100, 100).into(mIcon);
        if(DEBUG)
            Log.d(TAG, "url:" + str);
        if((str=MyApplication.getUser()) != null)
            mUser.setText(str);
        else
            mUser.setText("请登录");
//        mEmail.setText(MyApplication.getUserMail());
    }


    private void checkData(final String mail, final String oldMail)
    {
        Log.d(TAG, "check data");
        //检查数据
        mRequestQueue.add(new MyJsonObjectRequest(
                Request.Method.GET,
                Constant.URL_BOOKS_COUNT,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            UserPref.setUserMail(mail);
                            int netCount = response.getInt("count");
                            int localCount =
                                    MyApplication.getDBOperateInstance().getBookNum();
                            Log.d(TAG, "netCount:"+netCount+" localCount:"+localCount);
                            Log.d("Auth", MyApplication.getAuthorization());
                            //首次登录
                            if(oldMail == null)
                            {
                                Log.d(TAG, "first login");
                                //帐号内不空，覆盖本地
                                if(netCount != 0)
                                    sync(Constant.CHOICE_WEB);
                                    //帐号内为空，本地不空，覆盖帐号
                                else if (localCount != 0)
                                    sync(Constant.CHOICE_LOCAL);
                            }
                            else if(!mail.equals(oldMail))
                            {
                                Log.d(TAG, "oldMail:"+oldMail+" mail:"+mail);
                                //帐号内不空，本地为空，覆盖本地
                                if(localCount <= 0 && netCount != 0)
                                    sync(Constant.CHOICE_WEB);
                                //帐号内为空，本地不空，覆盖帐号
                                else if (netCount <= 0 && localCount != 0)
                                    sync(Constant.CHOICE_LOCAL);
                                //都不为空，选择
                                else if(netCount * localCount > 0)
                                    mChoseDialog.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            checkData(mail, oldMail);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "read data Error:"+error);
                if(oldMail == null)
                    UserPref.setUserMail(oldMail);
                Snackbar.make(mNavigationView, "读取数据失败,你可以重新登录或重试", Snackbar.LENGTH_SHORT)
                        .setAction("重试", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkData(mail, oldMail);
                            }
                        }).show();
            }
        }
        ));
//        mRequestQueue.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(resultCode == RESULT_OK)
        {
            if(requestCode == Constant.LOGIN)
            {
                setUserInfo();
                String oldMail = data.getStringExtra(Constant.KEY_OLD_MAIL);
                String mail = data.getStringExtra(Constant.KEY_MAIL);
                checkData(mail, oldMail);
            }
            else if(requestCode == Constant.PERSON && data.getBooleanExtra(Constant.KEY_USER_ICON_CHANGE, false))
            {
                String str;
                Picasso.with(this).load(R.drawable.ic_user_icon).resize(100, 100).into(mIcon);
                if((str=MyApplication.getUserUrl())!= null && !str.contains("null"))
                    MyApplication.getPicasso().load(Uri.parse(str))
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .resize(100, 100).into(mIcon);
                mUser.setText(MyApplication.getUser());
//                mEmail.setText(MyApplication.getUserMail());
            }
        }else if(resultCode == RESULT_CANCELED && requestCode == Constant.PERSON)
        {
            Picasso.with(this).load(R.drawable.ic_user_icon).resize(100, 100).into(mIcon);
            mUser.setText("请登录");
//            mEmail.setText(null);
        }
    }


    private void sync(int choice)
    {
        if(!MyApplication.getUserOnLine())
            return;
        Intent intent = new Intent(this, WebService.class);
        intent.putExtra(Constant.KEY_CMD, Constant.CMD_SYNC);
        mProgressDialog.show();
        MyApplication.setSync(true);
        intent.putExtra(Constant.KEY_CHOICE, choice);
        startService(intent);
        //通知fragment刷新
        MyApplication.setShouldUpdate(Constant.INDEX_READ);
        MyApplication.setShouldUpdate(Constant.INDEX_AFTER);
        MyApplication.setShouldUpdate(Constant.INDEX_NOW);
        MyApplication.setShouldUpdate(Constant.INDEX_BEFORE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK)
//        {
//
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if(DEBUG)
            Log.d(TAG, "main activity on back pressed");
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        if(DEBUG)
            Log.d(TAG, "main activity on destroy");
        super.onDestroy();
        ZhugeSDK.getInstance().flush(getApplicationContext());

        if(mDialog != null) {
            mDialog.dismiss();
        }
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
        if(mChoseDialog != null)
            mChoseDialog.dismiss();

        if(DEBUG)
            Log.d(TAG, "stop service");
        stopService(new Intent(this, WebService.class));
        DBOperate dbOperate = MyApplication.getDBOperateInstance();
        if(dbOperate != null )
            dbOperate.close();
        unregisterReceiver(mReceiver);
    }



    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mProgressDialog.dismiss();
            MyApplication.setSync(false);

            Counter counter = intent.getParcelableExtra("counter");
            int choice = intent.getIntExtra("choice", Constant.CHOICE_LOCAL);
            String str;
            str = "同步成功:";
            if (counter.getBookFinishNum() < counter.getBookNum())
                str = "同步不完整:";

            str += counter.getBookFinishNum()+"/"+counter.getBookNum()+"本 "+counter.getChapterNum()+"章";
            Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
            if(choice == Constant.CHOICE_WEB) {
                ReadingFragment.executeLoad();
                AfterFragment.executeLoad();
                NowFragment.executeLoad();
                BeforeFragment.executeLoad();
            }
        }
    };
}
