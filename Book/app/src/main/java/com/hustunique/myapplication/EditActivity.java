package com.hustunique.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.zhuge.analysis.stat.ZhugeSDK;

import net.MultipartEntity;
import net.MultipartRequest;
import net.MyJsonObjectRequest;
import net.OkHttpStack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import ui.CircleImageView;
import util.Constant;
import util.FileUtil;

/**
 * Created by taozhiheng on 15-7-12.
 */
public class EditActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private CircleImageView mIcon;
    private EditText mUser;
    private TextView mBoy;
    private TextView mGirl;

    private ImageView mBoyIcon;
    private ImageView mGirlIcon;


    private String mIconPath;
    private boolean iconChanged;

    private RequestQueue mRequestQueue;
    private ProgressDialog mProgress;

    private final static boolean DEBUG = false;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Edit UserInfo Activity");
        MobclickAgent.onResume(this);

        ZhugeSDK.getInstance().init(getApplicationContext());

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Edit UserInfo Activity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZhugeSDK.getInstance().flush(getApplicationContext());

        if(mProgress != null) {
            mProgress.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mToolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        mIcon = (CircleImageView) findViewById(R.id.edit_icon);
        mUser = (EditText) findViewById(R.id.edit_user);
        mBoy = (TextView) findViewById(R.id.edit_boy);
        mGirl = (TextView) findViewById(R.id.edit_girl);
        mBoyIcon = (ImageView) findViewById(R.id.edit_boy_icon);
        mGirlIcon = (ImageView) findViewById(R.id.edit_girl_icon);
        mToolbar.setTitle("编辑资料");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("编辑资料");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                        Constant.IMAGE);
            }
        });
        mBoy.setOnClickListener(mOnClickListener);
        mGirl.setOnClickListener(mOnClickListener);


        mProgress = new ProgressDialog(this);
        mProgress.setMessage("正在处理...");

        iconChanged = false;

        Picasso.with(this).load(R.drawable.ic_user_icon).resize(146, 146).into(mIcon);
        String url = MyApplication.getUserUrl();
        if(url != null && !url.contains("null")){
            MyApplication.getPicasso().load(Uri.parse(url))
                    .resize(146, 146).into(mIcon);

        }
        String user = MyApplication.getUser();
        if(user != null)
            mUser.setText(user);
        boolean sex = MyApplication.getUserSex();
        if(DEBUG)
            Log.d("net", "read sex:"+sex);
        if(!sex)
            mBoy.performClick();
        else
            mGirl.performClick();

        mRequestQueue = Volley.newRequestQueue(this, new OkHttpStack());

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.edit_boy:
                    mBoy.setSelected(true);
                    mGirl.setSelected(false);
                    mBoyIcon.setVisibility(View.VISIBLE);
                    mGirlIcon.setVisibility(View.GONE);
                    break;
                case R.id.edit_girl:
                    mBoy.setSelected(false);
                    mGirl.setSelected(true);
                    mBoyIcon.setVisibility(View.GONE);
                    mGirlIcon.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_edit_finish)
        {
            String user = mUser.getText().toString().trim();
            if(user.compareTo("") == 0)
                return super.onOptionsItemSelected(item);
            boolean sex = false;//男
            if(mGirlIcon.getVisibility() == View.VISIBLE)
                sex = true;

            executeEdit(user, sex);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void executeEdit(final String user, boolean sex)
    {
        mProgress.show();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", user);
        hashMap.put("sex", sex);
        JSONObject jsonObject = new JSONObject(hashMap);
        mRequestQueue.add(new MyJsonObjectRequest(
                        Request.Method.PATCH,
                        MyApplication.getUrlHead() + Constant.URL_USER_INFO,
                        jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(DEBUG)
                                    Log.d("net", "edit:"+response.toString());
                                try {
                                    String username = response.getString("username");
                                    MyApplication.setUser(username);
                                    String avatar = response.getString("avatar");
                                    if(!avatar.contains("http"))
                                        avatar = MyApplication.getUrlHead()+avatar;
                                    MyApplication.setUserUrl(avatar);
                                    MyApplication.setUserMail(response.getString("mail"));
                                    String sexStr = response.getString("sex");
                                    boolean sex = false;
                                    if(sexStr.equals("true"))
                                        sex = true;
                                    MyApplication.setUserSex(sex);
                                    Intent intent = new Intent();
                                    intent.putExtra(Constant.KEY_USER_ICON_CHANGE, iconChanged);
                                    intent.putExtra(Constant.KEY_USER_NAME, username);
                                    setResult(RESULT_OK, intent);

                                    if(iconChanged)
                                    {

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MultipartRequest multipartRequest = new MultipartRequest(
                                                        MyApplication.getUrlHead().replace("2333", "8000")+"/api/v1/user/avatar",
                                                        new Response.Listener<String>() {

                                                            @Override
                                                            public void onResponse(String response) {
                                                                if(DEBUG)
                                                                    Log.d("net", "avatar:" + response);
                                                                mProgress.dismiss();
                                                                Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT).show();
                                                                if(MyApplication.getUserUrl() != null && !MyApplication.getUserUrl().contains("null"))
                                                                {
                                                                    finish();
                                                                    return;
                                                                }
                                                                try
                                                                {
                                                                    JSONObject json = new JSONObject(response);
                                                                    String avatar = json.getString("avatar");
                                                                    if(!avatar.contains("http"))
                                                                        avatar = MyApplication.getUrlHead() +avatar;
                                                                    MyApplication.setUserUrl(avatar);
                                                                }catch (JSONException e)
                                                                {
                                                                    e.printStackTrace();
                                                                }
                                                                finally {
                                                                    finish();
                                                                }

                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                if(DEBUG)
                                                                    Log.d("net", "avatar:" + error);
                                                                mProgress.dismiss();
                                                                Toast.makeText(getBaseContext(), "抱歉，头像修改失败", Toast.LENGTH_SHORT).show();
                                                                finish();
                                                            }
                                                        });

//                                                multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                                // 添加header
                                                multipartRequest.addHeader("Accept", "application/json");
                                                multipartRequest.addHeader("Authorization", MyApplication.getAuthorization());
                                                // 通过MultipartEntity来设置参数
                                                MultipartEntity multi = multipartRequest.getMultiPartEntity();
                                                // 上传文件
                                                multi.addBinaryPart("file", FileUtil.getBytesFromFile(new File(mIconPath)));
//                    multi.addFilePart("file", new File(mIconPath));
                                                // 将请求添加到队列中
                                                if(DEBUG)
                                                    Log.d("net", "image:" + mIconPath + " auth:" + MyApplication.getAuthorization());
                                                mRequestQueue.add(multipartRequest);
                                            }
                                        }).start();
                                    }
                                    else
                                    {
                                        mProgress.dismiss();
                                        Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }catch (JSONException e)
                                {
                                    e.printStackTrace();
                                    mProgress.dismiss();
                                    Toast.makeText(getBaseContext(), "修改失败", Toast.LENGTH_SHORT).show();
                                    if(DEBUG)
                                        Log.d("net", e.toString());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                mProgress.dismiss();
                                Toast.makeText(getBaseContext(), "修改失败", Toast.LENGTH_SHORT).show();
                                if(DEBUG)
                                    Log.d("net", error.toString());
                            }
                        })
        );
//        mRequestQueue.start();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            mIconPath = picturePath;
            if(mIconPath != null) {
                MyApplication.getPicasso().load(new File(mIconPath)).resize(137,137).into(mIcon);
                iconChanged = true;
            }
        }
    }



}
