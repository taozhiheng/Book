package com.hustunique.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Picasso;
import data.UserReadInfo;
import util.Constant;


public class PersonActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView mIcon;
    private TextView mBooks;
    private TextView mChapters;
    private TextView mDays;
    private TextView mWords;
    private TextView mUser;


    private SystemBarTintManager mTintManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
            // Holo light action bar color is #DDDDDD
        }

        mToolbar = (Toolbar) findViewById(R.id.person_toolbar);
        mIcon = (ImageView) findViewById(R.id.person_icon);
        mUser = (TextView) findViewById(R.id.person_user);
        mBooks = (TextView) findViewById(R.id.person_books);
        mChapters = (TextView) findViewById(R.id.person_chapters);
        mDays = (TextView) findViewById(R.id.person_days);
        mWords = (TextView) findViewById(R.id.person_words);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("个人中心");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        UserReadInfo userReadInfo = MyApplication.getDBOperateInstance().getUserReadInfo();
        mBooks.setText(""+userReadInfo.getBookNum());
        mChapters.setText(""+userReadInfo.getChapterNum());
        mDays.setText(""+userReadInfo.getDayNum());
        mWords.setText(""+userReadInfo.getWordNum());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupUserInfo();
    }

    private void setupUserInfo()
    {
        String picturePath = MyApplication.getUserUrl();
        if (picturePath != null) {
            Picasso.with(this).invalidate(Uri.parse(picturePath));
            Picasso.with(this).load(Uri.parse(picturePath)).into(mIcon);
        }
        mUser.setText(MyApplication.getUser());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.in_ltr, R.anim.out_ltr);
                break;
            case R.id.action_person_edit:
                startActivityForResult(new Intent(this, EditActivity.class), Constant.ACTION_EDIT);
        }

        return super.onOptionsItemSelected(item);
    }

}
