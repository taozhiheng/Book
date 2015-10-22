package com.hustunique.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.umeng.analytics.MobclickAgent;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import util.Constant;

/**
 * Created by taozhiheng on 15-7-11.
 * RegisterActivity
 */
public class RegisterActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private EditText[] mInfos = new EditText[4];
    private Button mRegister;
    private TextView mLogin;

    private RequestQueue mRequestQueue;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Register Activity");
        MobclickAgent.onResume(this);

        ZhugeSDK.getInstance().init(getApplicationContext());

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Register Activity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZhugeSDK.getInstance().flush(getApplicationContext());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        mInfos[0] = (EditText) findViewById(R.id.register_email);
        mInfos[1] = (EditText) findViewById(R.id.register_user);
        mInfos[2] = (EditText) findViewById(R.id.register_psw);
        mInfos[3] = (EditText) findViewById(R.id.register_psw_confirm);
        mRegister = (Button) findViewById(R.id.register);
        mLogin = (TextView) findViewById(R.id.register_login);
        mRegister.setOnClickListener(mRegisterOnClickListener);
        mLogin.setOnClickListener(mCancelListener);
        mToolbar.setTitle("注册");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(mCancelListener);

    }

    private View.OnClickListener mCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private View.OnClickListener mRegisterOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.register:
                    if(canRegister())
                    {
                        doRegister(mInfos[0].getText().toString().trim(), mInfos[1].getText().toString().trim(),
                                mInfos[2].getText().toString().trim(), mInfos[3].getText().toString().trim());
                    }
                    else
                        Toast.makeText(getBaseContext(), Constant.REGISTER_FAIL, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //判断是否可以注册
    private boolean canRegister()
    {
        boolean isEnabled = true;
        int length;
        for(EditText item : mInfos)
        {
            length = item.getText().toString().trim().length();
            if(length < 2 || length > 21)
            {
                isEnabled = false;
                break;
            }
        }
        if(! mInfos[2].getText().toString().equals(mInfos[3].getText().toString())
                || mInfos[2].getText().toString().trim().length()<8)
            isEnabled = false;
        return isEnabled;
    }

    //执行注册post请求
    private void doRegister(String mail, String username, String password, String password2)
    {
        if(mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(this);
        HashMap<String, String> map = new HashMap<>();
        map.put("mail", mail);
        map.put("username", username);
        map.put("password", password);
        map.put("password2", password2);
        JSONObject jsonObject = new JSONObject(map);
        mRequestQueue.add(new JsonObjectRequest(
                Request.Method.POST,
                MyApplication.getUrlHead() + Constant.URL_REGISTER,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String authorization = response.getString("auth");
                            MyApplication.setAuthorization(authorization);
                            Toast.makeText(getBaseContext(), "注册成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), "注册失败", Toast.LENGTH_SHORT).show();

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
//        mRequestQueue.start();
    }

}
