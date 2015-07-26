package com.hustunique.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.readystatesoftware.systembartint.SystemBarTintManager;
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

    private EditText mUser;
    private EditText mPassword;
    private Button mLogin;
    private TextView mRegister;
    private Toolbar mToolbar;
    private Button mDBLogin;
    private Button mWBLogin;
    private ProgressDialog mProgressDialog;

    private  SystemBarTintManager mTintManager;

    private RequestQueue mRequestQueue;

    private boolean mIsNormalLogin;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
            // Holo light action bar color is #DDDDDD

        }
        mUser = (EditText) findViewById(R.id.user_name);
        mPassword = (EditText) findViewById(R.id.password);
        mLogin = (Button) findViewById(R.id.login);
        mDBLogin = (Button) findViewById(R.id.db_login);
        mWBLogin = (Button) findViewById(R.id.wb_login);
        mRegister = (TextView) findViewById(R.id.register_entrance);
        mUser.addTextChangedListener(mTextWatcher);
        mPassword.addTextChangedListener(mTextWatcher);
        mLogin.setEnabled(false);
        mLogin.setOnClickListener(mLoginOnClickListener);
        mDBLogin.setOnClickListener(mLoginOnClickListener);
        mWBLogin.setOnClickListener(mLoginOnClickListener);
        mRegister.setOnClickListener(mRegisterOnClickListener);
        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("登录");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }});

        mRequestQueue = Volley.newRequestQueue(this);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在登录...");
//        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                mRequestQueue.cancelAll(null);
//            }
//        });

        UserPref.init(this);
        mUser.setText(UserPref.getUserMail());
        mPassword.setText(UserPref.getUserPassword());
    }

    private TextWatcher mTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(TextUtils.isEmpty(mUser.getText())|| TextUtils.isEmpty(mPassword.getText()))
                mLogin.setEnabled(false);
            else
                mLogin.setEnabled(true);
        }
    };

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
                    mIsNormalLogin = true;
                    String user = mUser.getText().toString().trim();
                    String password = mPassword.getText().toString().trim();
                    if (user.length() > 1 && user.length() < 17 && password.length() > 7 && password.length() < 17) {
                        Log.d("net", "start login");
                        doLogin(user, password);
                    } else
                        Toast.makeText(getBaseContext(), Constant.USER_OR_PASSWORD_ILLEGAL, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.db_login:
                    mIsNormalLogin = false;
                    doDBLogin();
                    break;
                case R.id.wb_login:
                    mIsNormalLogin = false;
                    doWBLogin();
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


    //使用豆瓣登录
    private void doDBLogin()
    {
        startActivityForResult(new Intent(this, DBAuthActivity.class), Constant.DB_LOGIN);
    }

    //使用微博登录
    private void doWBLogin()
    {
        startActivityForResult(new Intent(this, WBAuthActivity.class), Constant.WB_LOGIN);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null)
        {
            HashMap<String, String> map = new HashMap<>();
            switch (requestCode)
            {
                case Constant.WB_LOGIN:
                    map.put("site", "weibo");
                    Toast.makeText(this, "wb:"+data.getStringExtra(Constant.AUTH_CODE), Toast.LENGTH_SHORT).show();
                    break;
                case Constant.DB_LOGIN:
                    map.put("site", "douban");
                    Toast.makeText(this, "db:"+data.getStringExtra(Constant.AUTH_CODE), Toast.LENGTH_SHORT).show();
                    break;
            }
            String code = data.getStringExtra(Constant.AUTH_CODE);
            code = code.substring(code.indexOf('=')+1);
            map.put("code", code);
            JSONObject jsonObject = new JSONObject(map);
            executeLogin(MyApplication.getUrlHead()+Constant.URL_THIRD_PART_LOGIN, jsonObject);
        }
    }


    //执行登录，获取授权，继而获取用户信息，计入MyApplication
    private void executeLogin(String url, JSONObject jsonObject)
    {
        mProgressDialog.show();
        mRequestQueue.add(new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String authorization = response.getString("auth");
                            //记录授权，设置用户在线
                            MyApplication.setAuthorization(authorization);
                            MyApplication.setUserOnLine(true);
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
                                                            avatar = "http://" + Constant.HOST_NAME + ":" + Constant.PORT + avatar;
                                                        MyApplication.setUserUrl(avatar);
                                                        MyApplication.setUserMail(mail);
                                                        //通知fragment刷新
                                                        MyApplication.setShouldUpdate(Constant.INDEX_READ);
                                                        MyApplication.setShouldUpdate(Constant.INDEX_AFTER);
                                                        MyApplication.setShouldUpdate(Constant.INDEX_NOW);
                                                        MyApplication.setShouldUpdate(Constant.INDEX_BEFORE);

                                                        Intent intent = new Intent();
                                                        intent.putExtra(Constant.KEY_OLD_MAIL, UserPref.getUserMail());
                                                        intent.putExtra(Constant.KEY_MAIL, mail);

                                                        if(mIsNormalLogin)
                                                        {
                                                            UserPref.setUserMail(mail);
                                                            UserPref.setUserPassword(mPassword.getText().toString().trim());
                                                        }

                                                        mProgressDialog.dismiss();

                                                        setResult(RESULT_OK, intent);
                                                        finish();
                                                    } catch (JSONException e)
                                                    {
                                                        e.printStackTrace();
                                                        mProgressDialog.dismiss();
                                                        Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_SHORT).show();
                                                        Log.d("net", "login:" + e.toString());
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_SHORT).show();
                                                    Log.d("net", "login:" + error.toString());
                                                }
                                            })
                            );
                            mRequestQueue.start();
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                            mProgressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_SHORT).show();
                            Log.d("net", "login:"+e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_SHORT).show();
                        Log.d("net", "login:"+error.toString());
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
        });
        mRequestQueue.start();
    }
}
