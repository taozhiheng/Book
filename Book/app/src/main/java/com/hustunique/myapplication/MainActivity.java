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
import com.android.volley.toolbox.Volley;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Picasso;
import net.MyJsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import adapter.MyFragmentAdapter;
import data.DBOperate;
import fragment.BeforeFragment;
import fragment.AddListener;
import fragment.NowFragment;
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

    private int mChoice = -1;

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
        setUserInfo();
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
                    startActivityForResult(new Intent(MainActivity.this, PersonActivity.class), Constant.LOGIN);
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
        if(position == 1)
        {
            NowFragment.executeLoad();
            BeforeFragment.executeLoad();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUserInfo()
    {
        String str;
        if((str=MyApplication.getUserUrl()) != null) {
            Picasso.with(this).invalidate(Uri.parse(str));
            Picasso.with(this).load(Uri.parse(str)).into(mIcon);
        }
        if((str=MyApplication.getUser()) != null)
            mUser.setText(str);
        else
            mUser.setText("请登录");
        mEmail.setText(MyApplication.getUserMail());
    }

    private final static String URL_BOOKS_COUNT = "http://pokebook.whitepanda.org:2333/api/v1/user/books/count";

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
                        URL_BOOKS_COUNT,
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
        }
    }

    private void sync(int choice)
    {
        mProgressDialog.show();
        mChoice = choice;
        Intent intent = new Intent(this, WebService.class);
        intent.putExtra(Constant.KEY_CMD, Constant.CMD_SYNC);
        if(choice == Constant.CHOICE_WEB)
            intent.putExtra(Constant.KEY_CHOICE, Constant.CHOICE_WEB);
        else
            intent.putExtra(Constant.KEY_CHOICE, Constant.CHOICE_LOCAL);
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
        stopService(new Intent("com.hustunique.myapplication.UPDATE"));
        DBOperate dbOperate = MyApplication.getDBOperateInstance();
        if(dbOperate != null )
            dbOperate.close();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_main_sync && mChoice != -1)
        {
            sync(mChoice);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mProgressDialog.dismiss();
            Toast.makeText(getBaseContext(), intent.getStringExtra("info"), Toast.LENGTH_SHORT).show();
            if(intent.getBooleanExtra("syncResult", false))
                findViewById(R.id.action_main_sync).setVisibility(View.GONE);
            MyApplication.setShouldUpdate(Constant.INDEX_READ);
            MyApplication.setShouldUpdate(Constant.INDEX_AFTER);
            MyApplication.setShouldUpdate(Constant.INDEX_NOW);
            MyApplication.setShouldUpdate(Constant.INDEX_BEFORE);
        }
    };
}
