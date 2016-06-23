package fragment;

import android.support.v4.app.Fragment;

/**
 * Created by taozhiheng on 15-7-5.
 *
 */
public interface FragmentCreator {
    Fragment newInstance(int position);
}
