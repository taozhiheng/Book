package adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.List;

import fragment.FragmentCreator;
import fragment.NumFragment;

/**
 * BookshelfFragment viewpager adapter
 * */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    //final int PAGE_COUNT =3;
    private FragmentCreator mFragmentCreator;
    private List<Fragment> mFragmentList;
    private String titles[] ;

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> list, String[] titles) {
        super(fm);
        this.mFragmentList = list;
        this.titles = titles;
    }

    public ViewPagerAdapter(FragmentManager fm, FragmentCreator fragmentCreator, String[] titles) {
        super(fm);
        this.mFragmentCreator = fragmentCreator;
        this.titles = titles;
    }

    public void setTitles(String[] titles)
    {
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("life cycle", "viewpager get position:"+position);
        if(mFragmentCreator != null)
            return mFragmentCreator.newInstance(position);
        return mFragmentList.get(position);
    }

    public CharSequence getPageTitle(int position) {
        if(mFragmentList != null) {
            NumFragment fragment = (NumFragment) mFragmentList.get(position);
            if(fragment != null)
                return titles[position]+" "+fragment.getItemNum();
        }
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

}