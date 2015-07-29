package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import com.hustunique.myapplication.R;

/**
 * Created by taozhiheng on 15-7-4.
 * Do nothing now,just show a CalendarView
 */
public class CalendarFragment extends Fragment {

    private CalendarView mCalendar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        mCalendar = (CalendarView) root.findViewById(R.id.calendar);
        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                Toast.makeText(getActivity(), "date:"+year+"/"+month+"/"+dayOfMonth, Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null)
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
    }

}
