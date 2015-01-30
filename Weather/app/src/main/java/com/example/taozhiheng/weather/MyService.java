package com.example.taozhiheng.weather;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import java.util.ArrayList;

/**
 * Created by taozhiheng on 15-1-2.
 * http请求，获取天气数据
 */
public class MyService extends Service{

    private NotificationManager notificationManager;  //通知栏管理
    private Notification notification;                //通知栏
    private MyThread thread;                          //查询线程
    private ArrayList<DailyWeather> list;             //查询结果

    @Override
    public void onCreate() {
        super.onCreate();
        //创建通知栏
        notificationManager = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.icon = R.drawable.wea;
        notification.tickerText = "weather";
        notification.flags = Notification.FLAG_NO_CLEAR;
        Intent intent = new Intent().setClass(this, MainActivity.class);
        notification.contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //开启查询线程
        if(intent != null&&Constant.OK == intent.getIntExtra(Constant.IS_QUERY, Constant.ERROR))
        {
            String city = intent.getStringExtra(Constant.REQUEST_CITY);
            thread = new MyThread(city);
            thread.start();
            Log.i("MyService",city+"开始查询");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private android.os.Handler handler = new android.os.Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(Constant.RESPONSE);
            if(msg.what != 0)
            {
                String city = msg.obj.toString();
                intent.putExtra(Constant.RESPONSE_STATUS, Constant.OK);
                intent.putExtra(Constant.REQUEST_CITY, city);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(Constant.WEATHER_KEY, list);
                intent.putExtras(bundle);
                //显示通知
                showNotification(city);
                Log.i("MyService", "应该显示通知");
            }
            else
            {
                intent.putExtra(Constant.RESPONSE_STATUS, Constant.ERROR);
            }
            //通知activity
            sendBroadcast(intent);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(0);
    }

    private void showNotification(String city)
    {
        notification.contentView = new RemoteViews(this.getPackageName(), R.layout.notify);
        DailyWeather weather = list.get(0);
        String temperature;
        int id;
        if(weather.getDayIconIndex() == Constant.ICON_INDEX_NULL)
        {
            temperature = weather.getLowTemperature()+"°C";
            id = Constant.ids[weather.getNightIconIndex()];
        }
        else
        {
            temperature = weather.getLowTemperature() + "°C~" + weather.getHighTemperature() + "°C";
            id = Constant.ids[weather.getDayIconIndex()];
        }
        notification.contentView.setImageViewResource(R.id.notify_icon, id);
        notification.contentView.setCharSequence(R.id.notify_weather, "setText", city+" "+weather.getWeatherDescribe());
        notification.contentView.setCharSequence(R.id.notify_wind, "setText", temperature+" "+weather.getWindDescribe());
        notification.when = System.currentTimeMillis();
        notificationManager.notify(0, notification);
    }

    class MyThread extends Thread{
        private String city;
        public MyThread(String city)
        {
            this.city = city;
        }

        @Override
        public void run()
        {
            super.run();
            String code = Constant.getCityCode(MyService.this, city);
            if(code.equals(Constant.NO_FOUND))
            {
                handler.sendEmptyMessage(0);
                return;
            }
            String urlString = Constant.HTTP_HEAD+code+".shtml";
            try {
                list = Constant.getWeatherInfo(urlString);
                Message message = new Message();
                message.what = 1;
                message.obj = city;
                handler.sendMessage(message);
            }catch (Exception e)
            {
                Log.i("My", "exception");
                e.printStackTrace();
                handler.sendEmptyMessage(0);
            }
        }
    }
}
