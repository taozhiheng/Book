package com.example.taozhiheng.weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;

/**
 * Created by taozhiheng on 15-1-1.
 * 自定义绘制天气描述的类
 */
public class MyWeatherView extends View{
    //private Context context;
    private Bitmap[] dayIcons = new Bitmap[7];
    private Bitmap[] nightIcons = new Bitmap[7];
    private ArrayList<DailyWeather> list;
    private DailyWeather weather;
    private Paint paint;
    private boolean flag = false;
    public MyWeatherView(Context context)
    {
        super(context);
        //this.context = context;
        paint = new Paint();
    }

    public MyWeatherView(Context context, AttributeSet atts)
    {
        super(context, atts);
        //this.context = context;
        paint = new Paint();
    }

    public MyWeatherView(Context context, AttributeSet atts, int defaultStyle)
    {
        super(context,atts,defaultStyle);
        //this.context = context;
        paint = new Paint();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        Log.i("MyWeatherView", "onDraw");
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        drawTemperatureLines(canvas, paint);
        drawTexts(canvas, paint);
        if(flag)
            drawIcons(canvas, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {

        super.onSizeChanged(w, h, oldW, oldH);
        MyAsyncTask task1;
        MyAsyncTask task2;
        for(int index = 0; index < 7; index++)
        {
            task1 = new MyAsyncTask(this, index, 0, this.getWidth()/8);
            task1.execute(list.get(index).getDayIconIndex());
            task2 = new MyAsyncTask(this, index, 1, this.getWidth()/8);
            task2.execute(list.get(index).getNightIconIndex());
        }
    }

    public void setSingleIcon(Bitmap bitmap, int index, int which)
    {
        //Log.i("MyView", "setIcon:"+bitmap.equals(null));
        if(which == 0)
            dayIcons[index] = bitmap;
        else
            nightIcons[index] = bitmap;
        if(index == 6 && which == 1)
        {
            flag = true;
            this.invalidate();
        }
    }

    public void setViewData(ArrayList<DailyWeather> list)
    {
        this.list = list;
        this.invalidate();
    }

    public void drawTemperatureLines(Canvas canvas, Paint paint)
    {
        //画折线图4/14
        int line_0 = getHeight()/14*8;
        int max = 0;
        int min = 0;
        for(int i = 0 ; i < 7; i++)
        {
            weather = list.get(i);
            if(weather.getHighTemperature() != Constant.HIGH_TEMPERATURE_NULL && weather.getHighTemperature()>max)
                max = weather.getHighTemperature();
            if(weather.getHighTemperature()<min)
                min = weather.getHighTemperature();
            if(weather.getLowTemperature()>max)
                max = weather.getLowTemperature();
            if(weather.getLowTemperature()<min)
                min = weather.getLowTemperature();
        }

        int distance = getHeight()/14*4/(max - min + 6);
        line_0 = line_0 + (max+min)/2*distance;
        Path path = new Path();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        if(list.get(0).getHighTemperature() == Constant.HIGH_TEMPERATURE_NULL)
        {
            path.moveTo(getWidth()/14*3, line_0-list.get(1).getHighTemperature()*distance);
        }
        else
        {
            path.moveTo(getWidth() / 14 , line_0 - list.get(0).getHighTemperature() * distance);
        }
        for(int i = 0; i<7; i++)
        {
            weather = list.get(i);
            canvas.drawText(weather.getHighTemperature()+"°C", getWidth()/14*(2*i+1)-15, line_0-20-weather.getHighTemperature()*distance, paint);
            canvas.drawCircle(getWidth() / 14 * (2 * i + 1), line_0 - weather.getHighTemperature() * distance, 5, paint);
            if(i>0)
            {
                path.lineTo(getWidth() / 14 * (2 * i + 1), line_0-weather.getHighTemperature()*distance);
            }
        }
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        path.reset();
        path.moveTo(getWidth()/14, line_0-list.get(0).getLowTemperature()*distance);
        for(int i = 0; i<7; i++)
        {
            weather = list.get(i);
            canvas.drawText(weather.getLowTemperature()+"°C", getWidth()/14*(2*i+1)-15, line_0+35-weather.getLowTemperature()*distance, paint);
            canvas.drawCircle(getWidth() / 14 * (2 * i + 1), line_0 - weather.getLowTemperature() * distance, 5, paint);
            if(i>0)
            {
                path.lineTo(getWidth() / 14 * (2 * i + 1), line_0-weather.getLowTemperature()*distance);
            }
        }
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);
        paint.setColor(0x80ffffff);
        PathEffect effect = new DashPathEffect(new float[]{8,15}, 1);
        paint.setPathEffect(effect);
        for(int i =0 ; i<8; i++) {
            path.reset();
            path.moveTo(getWidth()/14*2*i, 0);
            path.lineTo(getWidth()/14*2*i, getHeight());
            canvas.drawPath(path, paint);
        }
        path.reset();
        path.moveTo(0,0);
        path.lineTo(getWidth(),0);
        canvas.drawPath(path, paint);
        path.reset();
        path.moveTo(0,getHeight());
        path.lineTo(getWidth(),getHeight());
        canvas.drawPath(path, paint);
        paint.setPathEffect(null);
    }

    public void drawTexts(Canvas canvas, Paint paint)
    {
        //画文字描述4/14
        paint.setColor(Color.WHITE);
        paint.setTextSize(25);
        paint.setStrokeWidth(0);
        for(int i = 0; i<7; i++)
        {
            weather = list.get(i);
            canvas.drawText(weather.getWeekDescribe(), getWidth()/14*(2*i+1)-25, getHeight()/14, paint);
            canvas.drawText(weather.getDateDescribe(), getWidth()/14*(2*i+1)-25, getHeight()/14*2, paint);
        }
        paint.setTextSize(20);
        paint.setStrokeWidth(0);
        paint.setColor(Color.WHITE);
        for(int i = 0; i<7; i++)
        {
            weather = list.get(i);
            int row = (weather.getWeatherDescribe().length()-1)/2;
            if(weather.getWeatherDescribe().length() == 1)
            {
                canvas.drawText(weather.getWeatherDescribe(), getWidth()/14*(2*i+1)-weather.getWeatherDescribe().length()*10, getHeight()/14*11, paint);
            }
            else
            {
                for(int j = 0; j < row; j++)
                {
                    canvas.drawText(weather.getWeatherDescribe().substring(2*j, 2*j+2),
                            getWidth()/14*(2*i+1)-20, getHeight()/14*11+20*j, paint);
                }
                canvas.drawText(weather.getWeatherDescribe().substring(2*row, weather.getWeatherDescribe().length()),
                        getWidth()/14*(2*i+1)-20, getHeight()/14*11+20*row, paint);
            }
            row = (weather.getWindDescribe().length()-1)/2;
            if(weather.getWindDescribe().length() == 1)
            {
                canvas.drawText(weather.getWindDescribe(), getWidth()/14*(2*i+1)-weather.getWindDescribe().length()*10, getHeight()/14*12+20, paint);
            }
            else
            {
                for(int j = 0; j < row; j++)
                {
                    canvas.drawText(weather.getWindDescribe().substring(2*j, 2*j+2),
                            getWidth()/14*(2*i+1)-20, getHeight()/14*12+20*j+20, paint);
                }
                canvas.drawText(weather.getWindDescribe().substring(2*row, weather.getWindDescribe().length()),
                        getWidth()/14*(2*i+1)-20, getHeight()/14*12+20*row+20, paint);
            }
        }
    }

    private void drawIcons(Canvas canvas, Paint paint)
    {
        //画天气图标4/14
        for(int i = 0; i<7; i++) {
            if(dayIcons[i] != null)
                canvas.drawBitmap(dayIcons[i], getWidth()/14*(2*i+1)-dayIcons[i].getWidth()/2 , getHeight()/14*3, paint);
            else
                Log.i("MyView", "dayIcons["+i+"] is null");
            if(nightIcons[i] != null)
                canvas.drawBitmap(nightIcons[i], getWidth()/14*(2*i+1)-nightIcons[i].getWidth()/2 , getHeight()/14*4, paint);
            else
                Log.i("MyView", "nightIcons["+i+"] is null");
        }
    }

}