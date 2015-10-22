package com.hustunique.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;


import fragment.FragmentCreator;
import fragment.GuideFragment;
import adapter.ViewPagerAdapter;

/**
 * Created by taozhiheng on 15-5-15.
 * Welcome Activity, the will only show when the application is first used
 */
public class GuideActivity extends AppCompatActivity {

    private RadioGroup mRadio;
    private ViewPager mViewPager;


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        mViewPager = (ViewPager) findViewById(R.id.m_viewpager);
        mRadio = (RadioGroup) findViewById(R.id.radio_group);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(
                getSupportFragmentManager(),
                new FragmentCreator() {
                    @Override
                    public Fragment newInstance(int position) {
                        switch(position)
                        {
                            case 0:
                                return GuideFragment.newInstance(0);
                            case 1:
                                return GuideFragment.newInstance(1);
                            case 2:
                                return GuideFragment.newInstance(2);
                        }
                        return null;
                    }
                },
                new String[]{"welcome", "welcome", "welcome"}
        );

        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mRadio.check(R.id.radioButton);
                        break;
                    case 1:
                        mRadio.check(R.id.radioButton2);
                        break;
                    case 2:
                        mRadio.check(R.id.radioButton3);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

}
