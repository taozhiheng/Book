package com.hustunique.myapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.umeng.analytics.MobclickAgent;
import com.zhuge.analysis.stat.ZhugeSDK;

import net.MyJsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import data.UserPref;
import util.Constant;

/**
 * Created by taozhiheng on 15-5-15.
 *
 */
public class LoginActivity extends AppCompatActivity {

//    private EditText mUser;
//    private EditText mPassword;
//    private Button mLogin;
//    private TextView mRegister;
    private Toolbar mToolbar;
    private View mDBLogin;
    private View mWBLogin;
    private ProgressDialog mProgressDialog;

    private  SystemBarTintManager mTintManager;

    private RequestQueue mRequestQueue;

    private boolean mIsNormalLogin;

    //wb
    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;

    public final static String REQUEST_TAG = "MyLoginRequest";

    private final static boolean DEBUG = true;



    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Login Activity");
        MobclickAgent.onResume(this);

        ZhugeSDK.getInstance().init(getApplicationContext());

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Login Activity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZhugeSDK.getInstance().flush(getApplicationContext());

        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_modify);


//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            mTintManager = new SystemBarTintManager(this);
//            mTintManager.setStatusBarTintEnabled(true);
//            // Holo light action bar color is #DDDDDD
//
//        }
//        mUser = (EditText) findViewById(R.id.user_name);
//        mPassword = (EditText) findViewById(R.id.password);
//        mLogin = (Button) findViewById(R.id.login);
        mDBLogin =  findViewById(R.id.db_login);
        mWBLogin =  findViewById(R.id.wb_login);
//        mRegister = (TextView) findViewById(R.id.register_entrance);
        //mUser.addTextChangedListener(mTextWatcher);
        //mPassword.addTextChangedListener(mTextWatcher);
        //mLogin.setEnabled(false);
//        mLogin.setOnClickListener(mLoginOnClickListener);
        mDBLogin.setOnClickListener(mLoginOnClickListener);
        mWBLogin.setOnClickListener(mLoginOnClickListener);
//        mRegister.setOnClickListener(mRegisterOnClickListener);
        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("登录");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在登录...");
        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mRequestQueue.cancelAll(REQUEST_TAG);
            }
        });

        mRequestQueue = Volley.newRequestQueue(this);

        UserPref.init(this);
//        mUser.setText(UserPref.getUserMail());
//        mPassword.setText(UserPref.getUserPassword());

        mAuthInfo = new AuthInfo(this, Constant.WB_APP_KEY, Constant.WB_REDIRECT_URL, Constant.WB_SCOPE);
        mSsoHandler = new SsoHandler(this, mAuthInfo);
    }

//    private TextWatcher mTextWatcher = new TextWatcher()
//    {
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            if(TextUtils.isEmpty(mUser.getText())|| TextUtils.isEmpty(mPassword.getText()))
//                mLogin.setEnabled(false);
//            else
//                mLogin.setEnabled(true);
//        }
//    };

    private View.OnClickListener mRegisterOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
    };

    private View.OnClickListener mLoginOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login:
//                    mIsNormalLogin = true;
//                    String user = mUser.getText().toString().trim();
//                    String password = mPassword.getText().toString().trim();
//                    if (user.length() > 1 && user.length() < 17 && password.length() > 7 && password.length() < 17) {
//                        Log.d("net", "start login");
//                        doLogin(user, password);
//                    } else
//                        Toast.makeText(getBaseContext(), Constant.USER_OR_PASSWORD_ILLEGAL, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.db_login:
                    mIsNormalLogin = false;
                    doDBLogin();
                    break;
                case R.id.wb_login:
                    mIsNormalLogin = false;
                    doWBLogin2();
                    break;
            }
        }
    };

    //普通登录
    private void doLogin(String user, String password)
    {
        HashMap<String, String> map = new HashMap<>();
        map.put("mail", user);
        map.put("password", password);
        JSONObject jsonObject = new JSONObject(map);
        executeLogin(MyApplication.getUrlHead()+Constant.URL_LOGIN, jsonObject);
    }


    //使用豆瓣登录,仅web授权
    private void doDBLogin()
    {
        startActivityForResult(new Intent(this, DBAuthActivity.class), Constant.DB_LOGIN);
    }

    //使用微博登录， 仅web授权
    private void doWBLogin()
    {
        startActivityForResult(new Intent(this, WBAuthActivity.class), Constant.WB_LOGIN);
    }

    //使用微博登录，sso或web授权
    private void doWBLogin2()
    {
        mSsoHandler.authorize(new AuthDialogListener());
    }

    private final static String TAG="weibo";
    class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onCancel() {
            if(DEBUG)
                Log.d(TAG, "===================AuathDialogListener=Auth cancel==========");
//            Util.showToast(mContext, "取消授权操作。");
        }

        @Override
        public void onComplete(Bundle values) {
//            LOG.cstdr(TAG, "===================AuthDialogListener=onComplete==========");
            for (String key : values.keySet()) {
                if(DEBUG)
                    Log.d(TAG, "values:key = " + key + " value = " + values.getString(key));
            }
            String uid = values.getString("uid");
            String token = values.getString("access_token");
            HashMap<String, Object> map = new HashMap<>();
            map.put("uid", uid);
            map.put("access_token", token);
            if(uid == null)
            {
                map.clear();
                String code = values.getString(Constant.AUTH_CODE);
                code = code.substring(code.indexOf('=') + 1);
                map.put("code", code);
            }
            JSONObject body = new JSONObject(map);
            map.clear();
            map.put("site", "weibo");
            map.put("body", body);
            JSONObject jsonObject = new JSONObject(map);
            executeLogin(MyApplication.getUrlHead() + Constant.URL_THIRD_PART_LOGIN, jsonObject);
        }



        @Override
        public void onWeiboException(WeiboException e) {
            Log.d(TAG, "===================AuthDialogListener=onWeiboException=WeiboException = " + e.getMessage());
//            Util.showToast(mContext, "授权失败，请检查网络连接。出错信息：" + e.getMessage());
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null)
        {
            if(DEBUG)
                Log.d(TAG, "all:"+data.toString());
            if(requestCode == Constant.DB_LOGIN) {
                HashMap<String, Object> map = new HashMap<>();
//            switch (requestCode)
//            {
//                case Constant.WB_LOGIN:
//                    map.put("site", "weibo");
//                    Toast.makeText(this, "wb:"+data.getStringExtra(Constant.AUTH_CODE), Toast.LENGTH_SHORT).show();
//                    break;
//                case Constant.DB_LOGIN:
//                    map.put("site", "douban");
////                    Toast.makeText(this, "db:"+data.getStringExtra(Constant.AUTH_CODE), Toast.LENGTH_SHORT).show();
//                    break;

//            }
                String code = data.getStringExtra(Constant.AUTH_CODE);
                code = code.substring(code.indexOf('=') + 1);
                map.put("code", code);
                JSONObject body = new JSONObject(map);
                map.clear();
                map.put("site", "douban");
                map.put("body", body);
                if(DEBUG)
                    Log.d(TAG, code);
                JSONObject jsonObject = new JSONObject(map);
                executeLogin(MyApplication.getUrlHead() + Constant.URL_THIRD_PART_LOGIN, jsonObject);
            }
            else
            {
                if(mSsoHandler != null)
                    mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }


    //执行登录，获取授权，继而获取用户信息，计入MyApplication
    private void executeLogin(String url, JSONObject jsonObject)
    {
        mProgressDialog.show();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(DEBUG)
                                Log.d(TAG, "login:"+response);
                            final String authorization = response.getString("auth");
                            //记录授权，设置用户在线
                            MyApplication.setAuthorization(authorization);
                            UserPref.setUserAuth(authorization);
                            mRequestQueue.add(new MyJsonObjectRequest(
                                            Request.Method.GET,
                                            MyApplication.getUrlHead() + Constant.URL_USER_INFO,
                                            null,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    if(DEBUG)
                                                        Log.d(TAG, "login,read user info:"+response.toString());
                                                    try
                                                    {
                                                        //读取用户基本信息
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
                                                        MyApplication.setUserOnLine(true);

                                                        JSONObject personObject = new JSONObject();
                                                        //预置字段部分示例
                                                        personObject.put("avatar", avatar);
                                                        personObject.put("name", username);
                                                        if(sex)
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

                                                        Intent intent = new Intent();
                                                        intent.putExtra(Constant.KEY_OLD_MAIL, UserPref.getUserMail());
                                                        intent.putExtra(Constant.KEY_MAIL, mail);

//                                                        if(mIsNormalLogin)
//                                                        {
//                                                            UserPref.setUserMail(mail);
//                                                            UserPref.setUserPassword(mPassword.getText().toString().trim());
//                                                        }

                                                        mProgressDialog.dismiss();

                                                        setResult(RESULT_OK, intent);
                                                        finish();
                                                    } catch (JSONException e)
                                                    {
                                                        e.printStackTrace();
                                                        mProgressDialog.dismiss();
                                                        Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_SHORT).show();
                                                        if(DEBUG)
                                                            Log.d(TAG, "login:" + e.toString());
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_SHORT).show();
                                                    if(DEBUG)
                                                        Log.d(TAG, "login:" + error.toString());
                                                }
                                            })
                            );
//                            mRequestQueue.start();
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                            mProgressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_SHORT).show();
                            if(DEBUG)
                                Log.d(TAG, "login:"+e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_SHORT).show();
                        if(DEBUG)
                            Log.d(TAG, "login:"+error.toString());
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        request.setTag(REQUEST_TAG);
        mRequestQueue.add(request);
//        mRequestQueue.start();
    }
}
