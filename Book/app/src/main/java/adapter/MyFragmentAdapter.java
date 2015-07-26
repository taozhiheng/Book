package adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ViewGroup;

import fragment.BookshelfFragment;
import fragment.CalendarFragment;
import fragment.ReadingFragment;

/**
 * Created by taozhiheng on 15-7-6.
 * MainActivity 3 main fragments adapter
 */
public class MyFragmentAdapter extends FragmentPagerAdapter {

    private FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction;
    private Fragment mCurrentPrimaryItem;

    public MyFragmentAdapter(FragmentManager fm)
    {
        super(fm);
        this.mFragmentManager = fm;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ReadingFragment.newInstance();
            case 1:
                return new BookshelfFragment();
            case 2:
                return new CalendarFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    private String makeFragmentName(int container, long id)
    {
        return container+"@"+id;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        final long itemId = getItemId(position);

        // Do we already have this fragment?
        String name = makeFragmentName(container.getId(), itemId);
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            mCurTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            mCurTransaction.add(container.getId(), fragment,
                    makeFragmentName(container.getId(), itemId));
        }
        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        mCurTransaction.detach((Fragment)object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }


}
