package com.example.taozhiheng.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by taozhiheng on 14-12-21.
 * 加载图片AsyncTask
 */
public class MyAsyncTask extends AsyncTask<Long, Integer, Bitmap>
{
    private ImageView imageView;
    private Context context;
    public MyAsyncTask(Context context, ImageView imageView)
    {
        this.context = context;
        this.imageView = imageView;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    protected Bitmap doInBackground(Long... params) {
        return AlbumGet.getArtwork(context, params[0] ,params[1], true);
    }
}
