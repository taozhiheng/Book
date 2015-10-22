package fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.hustunique.myapplication.AddActivity;
import com.hustunique.myapplication.MyApplication;
import com.hustunique.myapplication.R;
import com.umeng.analytics.MobclickAgent;

import adapter.ViewPagerAdapter;
import util.Constant;

/**
 * Created by taozhiheng on 15-7-4.
 *
 */
public class BookshelfFragment extends Fragment{

    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private TabLayout mIndicator;
    private FloatingActionButton mAdd;
    private String[] titles = new String[]{"想读", "在读", "已读"};

    private final static int[] guideResIds = {R.drawable.click_guide, R.drawable.press_guide, R.drawable.add_book_guide};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bookshelf, container, false);
        mViewPager = (ViewPager) root.findViewById(R.id.bookshelf_view_pager);
        mIndicator = (TabLayout) root.findViewById(R.id.bookshelf_indicator);
        mAdd = (FloatingActionButton) root.findViewById(R.id.bookshelf_add);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), AddActivity.class), Constant.ADD_BOOK);
            }
        });

        mAdapter = new ViewPagerAdapter(
                getActivity().getSupportFragmentManager(),
                new FragmentCreator() {
                    @Override
                    public Fragment newInstance(int position) {
                        switch(position)
                        {
                            case 0:
                                return AfterFragment.newInstance();
                            case 1:
                                return NowFragment.newInstance();
                            case 2:
                                return BeforeFragment.newInstance();
                        }
                        return null;
                    }
                },
                titles);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position)
                {
                    case 0:
                        if(MyApplication.getUpdateFlag(Constant.INDEX_AFTER))
                            AfterFragment.executeLoad();
                        break;
                    case 1:
                        if(MyApplication.getUpdateFlag(Constant.INDEX_NOW))
                            NowFragment.executeLoad();
                        break;
                    case 2:
                        if(MyApplication.getUpdateFlag(Constant.INDEX_BEFORE))
                            BeforeFragment.executeLoad();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mIndicator.setupWithViewPager(mViewPager);
        return root;
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null)
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ADD_BOOK && resultCode == Activity.RESULT_OK && null != data) {
            Toast.makeText(getActivity(), "添加书籍", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("My Bookshelf Fragment");
//        if(UserPref.getFirstGuide(1)) {
//            GuideUtil guideUtil = GuideUtil.getInstance();
//            guideUtil.setClearGuideListener(new GuideUtil.ClearGuideListener() {
//                @Override
//                public void clearGuide() {
//                    UserPref.clearFirstGuide(1);
//                }
//            });
//            guideUtil.setFirst(true);
//            guideUtil.initGuide(getActivity(), guideResIds);
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("My Bookshelf Fragment");
    }
}
