package com.hustunique.myapplication;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by taozhiheng on 15-7-7.
 * wait to finish
 */
public class AboutActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private SystemBarTintManager mTintManager;
    private TextView mTitle1;
    private TextView mDetail1;
    private TextView mTitle2;
    private TextView mDetail2;

    private boolean mIsShowing1;
    private boolean mIsShowing2;

    private Animation mShowAnimation;
    private Animation mHideAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
            // Holo light action bar color is #DDDDDD

        }
        mToolbar = (Toolbar) findViewById(R.id.about_toolbar);
        mTitle1 = (TextView) findViewById(R.id.about_title1);
        mDetail1 = (TextView) findViewById(R.id.about_detail1);
        mTitle2 = (TextView) findViewById(R.id.about_title2);
        mDetail2 = (TextView) findViewById(R.id.about_detail2);

        mToolbar.setTitle("关于我们");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mShowAnimation = AnimationUtils.loadAnimation(this, R.anim.text_show);
        mHideAnimation = AnimationUtils.loadAnimation(this, R.anim.text_hide);


        mHideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!mIsShowing1)
                    mDetail1.setVisibility(View.GONE);
                if(!mIsShowing2)
                    mDetail2.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mTitle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsShowing1 = !mIsShowing1;
                mTitle1.setSelected(mIsShowing1);
                if(mIsShowing1)
                {
                    mDetail1.setVisibility(View.VISIBLE);
                    mDetail1.startAnimation(mShowAnimation);
                }
                else
                {
                    mDetail1.startAnimation(mHideAnimation);
                }
            }
        });
        mTitle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsShowing2 = !mIsShowing2;
                mTitle2.setSelected(mIsShowing2);
                if(mIsShowing2)
                {
                    mDetail2.setVisibility(View.VISIBLE);
                    mDetail2.startAnimation(mShowAnimation);
                }
                else
                {
                    mDetail2.startAnimation(mHideAnimation);
                }
            }
        });

        mIsShowing1 = false;
        mIsShowing2 = false;

    }

}
