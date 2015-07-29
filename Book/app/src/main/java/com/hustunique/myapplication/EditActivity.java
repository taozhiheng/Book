package com.hustunique.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import net.FileImageUpload;
import net.MultipartEntity;
import net.MultipartRequest;
import net.MyJsonObjectRequest;
import net.OkHttpStack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
            }});

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

        iconChanged = false;

        String url = MyApplication.getUserUrl();
        if(url != null) {
            OkHttpClient picassoClient = new OkHttpClient();
            picassoClient.setCache(null);
            Picasso picasso=new Picasso.Builder(this).downloader(new OkHttpDownloader(picassoClient)).build();
            picasso.load(url).resize(146, 146).into(mIcon);
        }
        String user = MyApplication.getUser();
        if(user != null)
            mUser.setText(user);
        boolean sex = MyApplication.getUserSex();
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

        if(iconChanged)
        {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartRequest multipartRequest = new MultipartRequest(
                            MyApplication.getUrlHead()+"/api/v1/user/avatar",
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    Log.d("net", "avatar:" + response);
                                    Toast.makeText(getBaseContext(), "头像修改成功", Toast.LENGTH_SHORT).show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("net", "avatar:" +error);
                                    Toast.makeText(getBaseContext(), "头像修改失败"+error, Toast.LENGTH_SHORT).show();
                                }
                            });

                    multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    // 添加header
                    multipartRequest.addHeader("Accept", "application/json");
                    multipartRequest.addHeader("Authorization", MyApplication.getAuthorization());
                    // 通过MultipartEntity来设置参数
                    MultipartEntity multi = multipartRequest.getMultiPartEntity();
                    // 上传文件
                    multi.addBinaryPart("file", FileUtil.getBytesFromFile(new File(mIconPath)));
//                    multi.addFilePart("file", new File(mIconPath));
                    // 将请求添加到队列中
                    Log.d("net", "image:" + mIconPath + " auth:" + MyApplication.getAuthorization());
                    mRequestQueue.add(multipartRequest);
                }
            }).start();
        }

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
                                Log.d("net", "edit:"+response.toString());
                                try {
                                    String username = response.getString("username");
                                    MyApplication.setUser(username);
                                    String avatar = response.getString("avatar");
                                    if(!avatar.contains("http"))
                                        avatar = MyApplication.getUrlHead()
                                                +response.getString("avatar");
                                    MyApplication.setUserUrl(avatar);
                                    MyApplication.setUserMail(response.getString("mail"));
                                    String sexStr = response.getString("sex");
                                    boolean sex = false;
                                    if(sexStr.equals("true"))
                                        sex = true;
                                    MyApplication.setUserSex(sex);
                                    Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.putExtra(Constant.KEY_USER_ICON_CHANGE, iconChanged);
                                    intent.putExtra(Constant.KEY_USER_NAME, username);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }catch (JSONException e)
                                {
                                    e.printStackTrace();
                                    Toast.makeText(getBaseContext(), "修改失败"+e, Toast.LENGTH_SHORT).show();
                                    Log.d("net", e.toString());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getBaseContext(), "修改失败"+error, Toast.LENGTH_SHORT).show();
                                Log.d("net", error.toString());
                            }
                        })
        );
        mRequestQueue.start();
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
                Picasso.with(this).load(new File(mIconPath)).resize(137,137).into(mIcon);
                iconChanged = true;
            }
        }
    }



}
