package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.hustunique.myapplication.LoginActivity;
import com.hustunique.myapplication.R;

public class WelcomeFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;

    public static WelcomeFragment newInstance(int position) {
        WelcomeFragment f = new WelcomeFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        position = getArguments().getInt(ARG_POSITION);
        switch(position)
        {
            case 0:
                return inflater.inflate(R.layout.welcome_0, container, false);
            case 1:
                return inflater.inflate(R.layout.welcome_1, container, false);
            case 2:
                View rootView = inflater.inflate(R.layout.welcome_2, container, false);
                Button experience = (Button) rootView.findViewById(R.id.experience);
                experience.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                });
                return rootView;
        }

        return null;
    }
}