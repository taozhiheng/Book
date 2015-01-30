package com.example.taozhiheng.weather;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by taozhiheng on 15-1-23.
 * 左侧天气信息adapter
 */
public class MyLeftAdapter extends BaseAdapter {

    private Context context;
    private List<DailyWeather> list;

    public MyLeftAdapter(Context context, List<DailyWeather> list)
    {
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_left, null);
            holder.dateText = (TextView)convertView.findViewById(R.id.dateText);
            holder.infoText = (TextView)convertView.findViewById(R.id.infoText);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        DailyWeather dailyWeather = list.get(position);
        Log.i("MyAdapter", position+dailyWeather.getDateDescribe());
        String textString = dailyWeather.getWeekDescribe()+"\n"+dailyWeather.getDateDescribe();
        holder.dateText.setText(textString);
        textString = dailyWeather.getWeatherDescribe()+"\n"+dailyWeather.getWindDescribe();
        holder.infoText.setText(textString);
        return convertView;
    }

    private static class ViewHolder{
        TextView dateText;
        TextView infoText;
    }
}
