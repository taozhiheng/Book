package fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hustunique.myapplication.R;

/**
 * Created by taozhiheng on 15-9-8.
 */
public class PhotoFragment extends Fragment {

    ImageView mPhoto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photo, container, false);
        mPhoto = (ImageView) root.findViewById(R.id.photo);
        Bitmap bitmap = getArguments().getParcelable("bitmap");
        mPhoto.setImageBitmap(bitmap);
        return root;
    }
}
