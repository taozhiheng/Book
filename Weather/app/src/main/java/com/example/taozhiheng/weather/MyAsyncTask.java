package com.example.taozhiheng.weather;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
/**
 * Created by taozhiheng on 15-1-1.
 * 从本地加载bitmap
 */
public class MyAsyncTask extends AsyncTask {

    private MyWeatherView view;
    private int index;
    private int which;
    private int width = 80;

    public MyAsyncTask(MyWeatherView view, int index, int which, int width)
    {
        this.view = view;
        this.index = index;
        this.which = which;
        if(width != 0)
            this.width = width;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        view.setSingleIcon((Bitmap)o, index, which);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        int iconIndex = (Integer)params[0];
        if(iconIndex == Constant.ICON_INDEX_NULL)
            return null;
        Bitmap bitmap = BitmapFactory.decodeResource(view.getResources(), Constant.ids[iconIndex]);
        return Bitmap.createScaledBitmap(bitmap, width, width, false);
    }
}
