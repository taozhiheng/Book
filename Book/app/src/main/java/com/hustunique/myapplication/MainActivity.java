package com.hustunique.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import net.MyJsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import adapter.MyFragmentAdapter;
import data.DBOperate;
import data.UserPref;
import fragment.BeforeFragment;
import fragment.AddListener;
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
    private List<Fragment> mFragmentList;
    private String[] titles = new String[]{"今日阅读", "我的书架", "阅读日历"};

    private MyFragmentAdapter mFragmentPagerAdapter;
    private LinearLayout mHeader;
    private ImageView mIcon;
    private TextView mUser;
    private TextView mEmail;
    private FrameLayout mContainer;

    private SystemBarTintManager mTintManager;

    private AlertDialog mDialog;

    private RequestQueue mRequestQueue;

    private AlertDialog mChoseDialog;

    private ProgressDialog mProgressDialog;


    private void autoLogin()
    {
        mRequestQueue.add(new MyJsonObjectRequest(
                        Request.Method.GET,
                        MyApplication.getUrlHead() + Constant.URL_USER_INFO,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("net", "login:"+response.toString());
                                try
                                {
                                    //读取用户基本信息
                                    MyApplication.setUserOnLine(true);
                                    String mail = response.getString("mail");
                                    String username = response.getString("username");
                                    String sexStr = response.getString("sex");
                                    boolean sex = false;
                                    if(sexStr.equals("true"))
                                        sex = true;
                                    String avatar = response.getString("avatar");
                                    //记录基本信息
                                    MyApplication.setUser(username);
                                    MyApplication.setUserSex(sex);
                                    if(!avatar.contains("http"))
                                        avatar = MyApplication.getUrlHead() + avatar;
                                    MyApplication.setUserUrl(avatar);
                                    MyApplication.setUserMail(mail);
                                    //通知fragment刷新
                                    MyApplication.setShouldUpdate(Constant.INDEX_READ);
                                    MyApplication.setShouldUpdate(Constant.INDEX_AFTER);
                                    MyApplication.setShouldUpdate(Constant.INDEX_NOW);
                                    MyApplication.setShouldUpdate(Constant.INDEX_BEFORE);

                                    setUserInfo();

                                    //检查数据状态
                                    mRequestQueue.add(new MyJsonObjectRequest(
                                                    Request.Method.GET,
                                                    Constant.URL_BOOKS_COUNT,
                                                    null,
                                                    new Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject response) {
                                                            try
                                                            {
                                                                int netCount = response.getInt("count");
                                                                int localCount =
                                                                        MyApplication.getDBOperateInstance().getBookNum();
                                                                Toast.makeText(getBaseContext(), "localCount:"+localCount+" netCount:"+netCount, Toast.LENGTH_SHORT).show();
                                                                if(localCount <= 0 && netCount != 0)
                                                                    sync(Constant.CHOICE_WEB);
                                                                else if(netCount <= 0 && localCount != 0)
                                                                    sync(Constant.CHOICE_LOCAL);
                                                            }catch (JSONException e)
                                                            {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    },null
                                    ));
                                    mRequestQueue.start();

                                } catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                               setUserInfo();
                            }
                        })
        );
        mRequestQueue.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mHeader = (LinearLayout) mNavigationView.findViewById(R.id.header);
        mIcon = (ImageView) mHeader.findViewById(R.id.drawer_user_icon);
        mUser = (TextView) mHeader.findViewById(R.id.drawer_user_name);
        mEmail = (TextView) mHeader.findViewById(R.id.drawer_user_email);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mContainer = (FrameLayout) findViewById(R.id.main_container);
        mDialog = new AlertDialog.Builder(this)
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
                    }
                })
                .setNegativeButton("再看会儿", null)
                .create();
        mDrawerLayout.setStatusBarBackground(R.color.accent_material_dark);
        init();

        mRequestQueue = Volley.newRequestQueue(this);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                MyApplication.copyOldDB();
//            }
//        }).start();

        Log.d("web", "start service");
        startService(new Intent(this, WebService.class));
        IntentFilter filter = new IntentFilter("com.hustunique.myapplication.MAIN_RECEIVER");
        registerReceiver(mReceiver, filter);

        UserPref.init(this);
        MyApplication.setAuthorization(UserPref.getUserAuth());
        autoLogin();
    }

    private void init()
    {
        mChoseDialog = new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
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
        mFragmentList = new ArrayList<>();
        mFragmentPagerAdapter = new MyFragmentAdapter(getSupportFragmentManager());

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.drawer_reading:
                        setCurrentItem(0);
                        break;
                    case R.id.drawer_bookshelf:
                        setCurrentItem(1);
                        break;
                    case R.id.drawer_calendar:
                        setCurrentItem(2);
                        break;
                    case R.id.drawer_sync:
                        if(MyApplication.getUserOnLine() && MyApplication.getAuthorization() != null) {
                            mChoseDialog.setCancelable(true);
                            mChoseDialog.show();
                        }
                        break;
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

                return true;
            }
        });
        setCurrentItem(0);
    }


    @Override
    public void addChapter() {
        setCurrentItem(1);
    }


    private void setCurrentItem(int position)
    {
        mNavigationView.getMenu().getItem(position).setChecked(true);
        Fragment fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(mContainer, position);
        mFragmentPagerAdapter.setPrimaryItem(mContainer, 0, fragment);
        mFragmentPagerAdapter.finishUpdate(mContainer);
        mToolbar.setTitle(titles[position]);
        mDrawerLayout.closeDrawers();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUserInfo()
    {
        String str;
        if((str=MyApplication.getUserUrl()) != null) {
            OkHttpClient picassoClient = new OkHttpClient();
            picassoClient.setCache(null);
            Picasso picasso=new Picasso.Builder(this).downloader(new OkHttpDownloader(picassoClient)).build();
            picasso.load(str).resize(100, 100).into(mIcon);
        }
        if((str=MyApplication.getUser()) != null)
            mUser.setText(str);
        else
            mUser.setText("请登录");
        mEmail.setText(MyApplication.getUserMail());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(resultCode == RESULT_OK)
        {
            setUserInfo();
            if(requestCode == Constant.LOGIN)
            {
                //检查数据
                mRequestQueue.add(new MyJsonObjectRequest(
                        Request.Method.GET,
                        Constant.URL_BOOKS_COUNT,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try
                                {
                                    int netCount = response.getInt("count");
                                    int localCount =
                                            MyApplication.getDBOperateInstance().getBookNum();
                                    Toast.makeText(getBaseContext(), "localCount:"+localCount+" netCount:"+netCount, Toast.LENGTH_SHORT).show();
                                    if(localCount <= 0 && netCount != 0)
                                        sync(Constant.CHOICE_WEB);
                                    else if(netCount <= 0 && localCount != 0)
                                        sync(Constant.CHOICE_LOCAL);
                                    else if(localCount * netCount > 0)
                                    {
                                        String oldMail = data.getStringExtra(Constant.KEY_OLD_MAIL);
                                        String mail = data.getStringExtra(Constant.KEY_MAIL);
                                        if(!mail.equals(oldMail))
                                            mChoseDialog.show();
                                    }

                                }catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        },null
                ));
                mRequestQueue.start();

            }
        }else if(resultCode == RESULT_CANCELED && requestCode == Constant.PERSON)
        {
            Picasso.with(this).load(R.mipmap.ic_user_icon).into(mIcon);
            mUser.setText("请登录");
            mEmail.setText(null);
        }
    }


    private void sync(int choice)
    {
        if(!MyApplication.getUserOnLine())
            return;
        mProgressDialog.show();
        Intent intent = new Intent(this, WebService.class);
        intent.putExtra(Constant.KEY_CMD, Constant.CMD_SYNC);
        if(choice == Constant.CHOICE_WEB)
            intent.putExtra(Constant.KEY_CHOICE, Constant.CHOICE_WEB);
        else
            intent.putExtra(Constant.KEY_CHOICE, Constant.CHOICE_LOCAL);
        MyApplication.setSync(true);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDialog != null) {
            mDialog.dismiss();
        }
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
        if(mChoseDialog != null)
            mChoseDialog.dismiss();

        Log.d("web", "stop service");
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
            String head = "同步成功:";
            if(counter.getBookFinishNum() < counter.getBookNum())
                head = "同步不完整:";
            Toast.makeText(getBaseContext(), head+counter.getBookFinishNum()+"/"+counter.getBookNum()+"本"+" "+counter.getChapterNum()+"章", Toast.LENGTH_SHORT).show();
            ReadingFragment.executeLoad();
            MyApplication.setShouldUpdate(Constant.INDEX_READ);
            MyApplication.setShouldUpdate(Constant.INDEX_AFTER);
            MyApplication.setShouldUpdate(Constant.INDEX_NOW);
            MyApplication.setShouldUpdate(Constant.INDEX_BEFORE);
        }
    };
}
