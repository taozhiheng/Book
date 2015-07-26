package com.hustunique.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import fragment.WelcomeFragment;
import util.Constant;
import viewpager.CirclePageIndicator;
import adapter.ViewPagerAdapter;

/**
 * Created by taozhiheng on 15-5-15.
 * Welcome Activity, the will only show when the application is first used
 */
public class WelcomeActivity extends ActionBarActivity {

    private List<Fragment> mFragmentList;
    private String[] titles = new String[]{"page1", "page2", "page3"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        Log.d("welcome", "onCreate");
        SharedPreferences pref = getSharedPreferences(Constant.PREF_NAME, MODE_PRIVATE);
        Boolean isFirst = pref.getBoolean(Constant.PREF_FIRST_USE, true);
        if(!isFirst) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        Log.d("welcome", "first");
        setContentView(R.layout.welcome);
        pref.edit().putBoolean(Constant.PREF_FIRST_USE, false).apply();
        ViewPager mViewPager = (ViewPager) findViewById(R.id.m_viewpager);
        CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.m_viewpager_indicator);
        mFragmentList = new ArrayList<>();
        mFragmentList.add(WelcomeFragment.newInstance(0));
        mFragmentList.add(WelcomeFragment.newInstance(1));
        mFragmentList.add(WelcomeFragment.newInstance(2));
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(
                getSupportFragmentManager(),
                mFragmentList, titles);
        mViewPager.setAdapter(viewPagerAdapter);
        mIndicator.setViewPager(mViewPager, 0);
        mIndicator.setFillColor(getResources().getColor(R.color.accent_material_light));
    }
}
