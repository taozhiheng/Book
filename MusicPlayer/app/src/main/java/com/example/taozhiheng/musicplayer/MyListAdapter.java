package com.example.taozhiheng.musicplayer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by taozhiheng on 14-12-14.
 * 自定义listView适配器
 */
public class MyListAdapter extends BaseAdapter {
    private List<HashMap<String, Object>> dataList;
    private LayoutInflater layoutInflater;
    public MyListAdapter(Context context, List<HashMap<String, Object>> list)
    {
        this.layoutInflater = LayoutInflater.from(context);
        this.dataList = list;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.childview, null);
            holder.songIcon = (ImageView)convertView.findViewById(R.id.songIcon);
            holder.songName = (TextView)convertView.findViewById(R.id.songName);
            holder.singerName = (TextView)convertView.findViewById(R.id.singerName);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        HashMap<String, Object> dataItem = (HashMap<String, Object>)this.getItem(position);
        holder.songName.setText(dataItem.get("songName").toString());
        holder.singerName.setText(dataItem.get("singerName").toString());
        //songIcon设定图片。。。
        long songId = (Long) dataItem.get("songId");
        long albumId = (Long) dataItem.get("albumId");
        MyAsyncTask task = new MyAsyncTask(layoutInflater.getContext(), holder.songIcon);
        task.execute(songId, albumId);
        return convertView;
    }
    //viewHolder
    static class ViewHolder
    {
        ImageView songIcon;
        TextView songName;
        TextView singerName;
    }
}
