package com.example.taozhiheng.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by taozhiheng on 15-1-25.
 * 右侧指数信息adapter
 */
public class MyRightAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private int lastPosition = -1;
    private final int[] iconIds = {R.drawable.glasss, R.drawable.clothes, R.drawable.trip, R.drawable.sport,
    R.drawable.car, R.drawable.makeup, R.drawable.cold, R.drawable.light, R.drawable.soft};
    private final String[] valueNames = {"太阳镜指数", "穿衣\n指数", "旅游\n指数", "运动\n指数", "洗车\n指数", "化妆\n指数", "感冒\n指数", "紫外线指数", "舒适度指数"};
    public MyRightAdapter(Context context, List<String> list)
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_right, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.summary = (TextView) convertView.findViewById(R.id.summary);
            holder.detail = (TextView) convertView.findViewById(R.id.detail);
            holder.detailCover = (TextView) convertView.findViewById(R.id.detail_cover);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.icon.setBackgroundResource(iconIds[position]);
        String str = list.get(position);
        if(!str.equals(Constant.NO_FOUND))
        {
            String[] array = str.split(",");
            if(array.length == 2) {
                holder.summary.setText(array[0]);
                holder.detail.setText(array[1]);
            }
            else
            {
                holder.summary.setText("");
                holder.detail.setText("暂无详情");
            }
        }
        else
        {
                holder.summary.setText("");
                holder.detail.setText("暂无详情");
        }
        holder.detailCover.setText(valueNames[position]);
        if(position == lastPosition && holder.detail.getVisibility() == View.GONE) {
            holder.detail.setVisibility(View.VISIBLE);
            holder.detailCover.setVisibility(View.GONE);
        }
        else
        {
            holder.detail.setVisibility(View.GONE);
            holder.detailCover.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private static class ViewHolder{
        ImageView icon;
        TextView summary;
        TextView detail;
        TextView detailCover;
    }

    public void showDetail(int position)
    {
        this.lastPosition = position;
        notifyDataSetChanged();
    }
}
