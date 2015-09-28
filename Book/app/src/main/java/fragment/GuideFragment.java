package fragment;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hustunique.myapplication.MainActivity;
import com.hustunique.myapplication.MyApplication;
import com.hustunique.myapplication.R;
import com.squareup.picasso.Picasso;

import data.UserPref;

public class GuideFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;

    private static GuideFragment[] mFragment = new GuideFragment[3];

    private final static String TAG = "life cycle-guide";


    public static GuideFragment newInstance(int position) {
        if(position > 2)
            return null;
        if(mFragment[position] == null) {
            mFragment[position] = new GuideFragment();
            Bundle b = new Bundle();
            b.putInt(ARG_POSITION, position);
            mFragment[position].setArguments(b);
        }
        return mFragment[position];
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "guide on attach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "guide on create");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        position = getArguments().getInt(ARG_POSITION);
        View rootView = null;
        Log.d(TAG, "guide on create view");
        switch(position)
        {
            case 0:
                rootView = inflater.inflate(R.layout.guide_0, container, false);
                ((SimpleDraweeView)rootView).setImageURI(Uri.parse("res://drawable/" + R.drawable.guide0));
                break;
            case 1:
                rootView = inflater.inflate(R.layout.guide_1, container, false);
                ((SimpleDraweeView)rootView).setImageURI(Uri.parse("res://drawable/"+R.drawable.guide1));
                //MyApplication.getPicasso().load(R.drawable.guide1).into((ImageView)rootView);
                break;
            case 2:
                rootView = inflater.inflate(R.layout.guide_2, container, false);
                SimpleDraweeView image = (SimpleDraweeView) rootView.findViewById(R.id.welcome2_icon);
                image.setImageURI(Uri.parse("res://drawable/" + R.drawable.guide2));
                Button experience = (Button) rootView.findViewById(R.id.experience);
                experience.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        UserPref.init(getActivity());
                        UserPref.clearFirstUse();
                        getActivity().finish();

                    }
                });
        }
        return rootView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "guide on resume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "guide on pause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "guide on stop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "guide on destroy view");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "guide on destroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "guide on detach");
        super.onDetach();
    }
}