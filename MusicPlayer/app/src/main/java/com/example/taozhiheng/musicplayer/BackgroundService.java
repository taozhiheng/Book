package com.example.taozhiheng.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.zip.Inflater;

/**
 * Created by taozhiheng on 14-12-8.
 * 提供音乐播放的service
 */
public class BackgroundService extends Service {
    private MediaPlayer mediaPlayer;          //播放实例
    private Notification notification;        //通知栏
    private NotificationManager notifyManager;//通知栏管理

    private SharedPreferences pref;           //键值对实例
    private List<HashMap<String, Object>> dataList = null;//播放数据列表

    private static android.os.Handler handler = new android.os.Handler();//处理歌词handler实例
    private LrcProcess mLrcProcess;	//歌词处理
    private List<LrcContent> lrcList = new ArrayList<LrcContent>(); //存放歌词列表
    private int index = 0;			//歌词检索值
    private int currentTime;        //当前播放时间
    private int duration = 100;     //当前歌曲时长
    private int position = 0;       //当前播放歌曲相对位置

    //播放模式变量及常量
    private int MODE = -1;                  //播放模式
    public final static int LOOP = 0;       //列表循环
    public final static int REPEAT = 1;     //单曲循环
    public final static int QUEUE = 2;      //顺序播放
    public final static int RANDOM = 3;     //随机播放

    //播放状态变量及常量
    private int STATE;                       //记录当前播放状态
    private final static int STOP = 0;       //停止状态
    private final static int PLAY = 1;       //播放状态
    private final static int PAUSE = 2;      //暂停状态

    //按钮button_id常量
    private final static int BUTTON_LAST = -1;           //上一曲键
    private final static int BUTTON_PLAY_OR_PAUSE = 0;   //播放或暂停键
    private final static int BUTTON_NEXT = 1;            //下一曲键
    private final static int BUTTON_PROGRESS = 2;        //进度条
    private final static int BUTTON_MODE = 3;            //播放模式键
    private final static int BUTTON_EXIT = 4;            //退出键
    private final static int BUTTON_LIST = 5;             //列表项
    private boolean tag = true;                           //服务是否继续标志

    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while(tag) {
                if (mediaPlayer.isPlaying())
                {
                    Intent toShow = new Intent(ShowActivity.ACTION);
                    toShow.putExtra("command", 3);
                    toShow.putExtra("progress", mediaPlayer.getCurrentPosition());
                    sendBroadcast(toShow);
                }
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    });

    /**
     * 初始化歌词配置
     */
    public void initLrc(){
        mLrcProcess = new LrcProcess();
        //读取歌词文件
        mLrcProcess.readLRC(getCurrentPath());
        //传回处理后的歌词文件
        lrcList = mLrcProcess.getLrcList();
        ShowActivity.lyric.setmLrcList(lrcList);
        //切换带动画显示歌词
        //ShowActivity.lyric.setAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        handler.post(mRunnable);
    }
    //显示歌词runnable
    Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if(!tag)
                return;
            Intent intent = new Intent(ShowActivity.ACTION);
            intent.putExtra("command", 4);
            intent.putExtra("index", lrcIndex());
            sendBroadcast(intent);
            handler.postDelayed(mRunnable, 100);
        }
    };
    /**
     * 根据时间获取歌词显示的索引值
     * @return
     */
    public int lrcIndex() {
        if(mediaPlayer.isPlaying()) {
            currentTime = mediaPlayer.getCurrentPosition();
            duration = mediaPlayer.getDuration();
        }
        if(currentTime < duration)
        {
            for (int i = 0; i < lrcList.size(); i++) {
                if (i < lrcList.size() - 1)
                {
                    if (currentTime <= lrcList.get(i).getLrcTime() && i == 0)
                    {
                        index = i;
                        break;
                    }
                    if (currentTime >= lrcList.get(i).getLrcTime()&& currentTime < lrcList.get(i + 1).getLrcTime())
                    {
                        index = i;
                        break;
                    }
                }
                if (i == lrcList.size() - 1&& currentTime >= lrcList.get(i).getLrcTime())
                {
                    index = i;
                }
            }
        }
        return index;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            int buttonId = intent.getIntExtra("buttonId",-2);
            switch(buttonId)
            {
                case BUTTON_LAST:
                case BUTTON_NEXT:
                    setPosition(buttonId);
                    if(position<0||position>=dataList.size())
                        this.stop();
                    else
                        this.play();
                    break;
                case BUTTON_PLAY_OR_PAUSE:
                    if(STATE == STOP)
                        this.play();
                    else if(STATE == PAUSE)
                        this.continuePlay();
                    else
                        this.pause();
                    break;
                case BUTTON_PROGRESS:
                    mediaPlayer.seekTo(intent.getIntExtra("progress", 0));
                    break;
                case BUTTON_MODE:
                    MODE = (MODE+1)%4;
                    break;
                case BUTTON_EXIT:
                    Intent i = new Intent(AllRoot.REFLECT);
                    i.putExtra("command",3);
                    sendBroadcast(i);
                    break;
                case BUTTON_LIST:
                    position = intent.getIntExtra("position", 0);
                    this.play();
                    break;
                case 6:
                    dataList = new ArrayList<HashMap<String, Object>>();
                    for(HashMap<String, Object> item : AllRoot.getList() )
                    {
                        dataList.add(item);
                    }
                    if(position != 0&&intent.getBooleanExtra("change",true))
                        position = 0;
                    break;
                case 7:
                    initLrc();
                    Intent toShow = new Intent(ShowActivity.ACTION);
                    toShow.putExtra("command", 1);
                    toShow.putExtra("songName", getCurrentSong());
                    toShow.putExtra("singerName", getCurrentSinger());
                    toShow.putExtra("songLength", getCurrentTime());
                    toShow.putExtra("songPath", getCurrentPath());
                    toShow.putExtra("songId", getCurrentSongId());
                    toShow.putExtra("albumId", getCurrentAlbumId());
                    sendBroadcast(toShow);
                    break;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.i("state","create service");
        super.onCreate();
        initService();
    }

    @Override
    public void onDestroy() {
        Log.i("state","destroy service");
        tag = false;
        this.stop();
        mediaPlayer.release();
        notifyManager.cancel(0);
        try
        {
            SharedPreferences.Editor editor = pref.edit();
            Log.i("end position",""+position);
            editor.putInt("position", position);
            editor.putInt("mode", MODE);
            editor.putString("songName", getCurrentSong());
            editor.putString("singerName", getCurrentSinger());
            editor.commit();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
    }
    //控制歌曲播放
    private void play()
    {
        if(STATE != PLAY)
        {
            STATE = PLAY;
            changeButtonState();
        }
        notification.contentView.setViewVisibility(R.id.notify_play, View.VISIBLE);
        notification.contentView.setViewVisibility(R.id.notify_pause, View.GONE);
        MyServiceAsyncTask task = new MyServiceAsyncTask(getBaseContext(), notification.contentView);
        task.execute(getCurrentSongId(), getCurrentAlbumId());
        notification.contentView.setCharSequence(R.id.notify_song, "setText", getCurrentSong());
        notification.contentView.setCharSequence(R.id.notify_singer, "setText", getCurrentSinger());
        notifyManager.notify(0, notification); Log.i("path--position", getCurrentPath() + "--" + position);
        try
        {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getCurrentPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            initLrc();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            Intent toAll = new Intent(AllRoot.REFLECT);
            toAll.putExtra("command", 1);
            toAll.putExtra("position", position);
            sendBroadcast(toAll);
            Intent toShow = new Intent(ShowActivity.ACTION);
            toShow.putExtra("command", 1);
            toShow.putExtra("songName", getCurrentSong());
            toShow.putExtra("singerName", getCurrentSinger());
            toShow.putExtra("songLength", getCurrentTime());
            toShow.putExtra("songPath", getCurrentPath());
            toShow.putExtra("songId", getCurrentSongId());
            toShow.putExtra("albumId", getCurrentAlbumId());
            sendBroadcast(toShow);
        }
    }
    //控制播放继续
    private void continuePlay()
    {
        if(STATE != PLAY)
        {
            STATE = PLAY;
            changeButtonState();
        }
        notification.contentView.setViewVisibility(R.id.notify_play, View.VISIBLE);
        notification.contentView.setViewVisibility(R.id.notify_pause, View.GONE);
        notifyManager.notify(0, notification);
        mediaPlayer.start();
    }
    //控制播放暂停
    private void pause()
    {
        if(STATE != PAUSE)
        {
            STATE = PAUSE;
            changeButtonState();
        }
        notification.contentView.setViewVisibility(R.id.notify_pause, View.VISIBLE);
        notification.contentView.setViewVisibility(R.id.notify_play, View.GONE);
        notifyManager.notify(0, notification);
        mediaPlayer.pause();
    }
    //控制播放停止
    private void stop()
    {
        if(STATE != STOP)
        {
            STATE = STOP;
            changeButtonState();
        }
        notification.contentView.setViewVisibility(R.id.notify_pause, View.VISIBLE);
        notification.contentView.setViewVisibility(R.id.notify_play, View.GONE);
        notifyManager.notify(0, notification);
        mediaPlayer.stop();
    }

    //获得当前歌曲名
    public String getCurrentSong()
    {
        return ((HashMap<String, Object>) getCurrentInfo()).get("songName").toString();
    }
    //获得当前歌手名
    public String getCurrentSinger()
    {
        return ((HashMap<String, Object>) getCurrentInfo()).get("singerName").toString();
    }
    //获得当前歌曲路径
    public  String getCurrentPath()
    {
        return ((HashMap<String, Object>) getCurrentInfo()).get("songPath").toString();
    }
    //获得当前歌曲时长
    public long getCurrentTime()
    {
        return  (Long)((HashMap<String, Object>) getCurrentInfo()).get("timeLength");

    }
    //获得当前歌曲id
    public long getCurrentSongId()
    {
        return  (Long)((HashMap<String, Object>) getCurrentInfo()).get("songId");
    }
    //获得当前专辑id
    public long getCurrentAlbumId()
    {
        return  (Long)((HashMap<String, Object>) getCurrentInfo()).get("albumId");
    }
    //获得当前歌曲信息
    private Object getCurrentInfo()
    {
        return dataList.get(position);
    }

    //初始化service
    private void initService()
    {
        //创建播放器
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //一曲终了自动下一曲
                mediaPlayer.stop();//或许是多余的
                Intent intent = new Intent(BackgroundService.this, ControlReceiver.class);
                intent.putExtra("buttonId",BUTTON_NEXT);
                sendBroadcast(intent);
            }
        });
        //弹出通知栏
        notification = new Notification();
        notification.flags = Notification.FLAG_NO_CLEAR;
        notification.icon = R.drawable.app;
        notification.tickerText = "Music Player";
        notification.when = System.currentTimeMillis();
        RemoteViews remoteView = new RemoteViews(this.getPackageName(),R.layout.notify);
        notification.contentView = remoteView;
        //设置position,STATE,MODE
        pref = getSharedPreferences("song",MODE_PRIVATE);
        position = pref.getInt("position", 0);
        Log.i("start position", ""+position);
        STATE = STOP;
        MODE = pref.getInt("mode", LOOP);
        //设置按钮监听
        PendingIntent pendingIntent;
        Intent intent;
        intent = new Intent(this, ControlReceiver.class);
        intent.putExtra("buttonId",BUTTON_LAST);
        pendingIntent = PendingIntent.getBroadcast(this,BUTTON_LAST,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.notify_last, pendingIntent);
        intent = new Intent(this, ControlReceiver.class);
        intent.putExtra("buttonId",BUTTON_PLAY_OR_PAUSE);
        pendingIntent = PendingIntent.getBroadcast(this,BUTTON_PLAY_OR_PAUSE,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.notify_play, pendingIntent);
        remoteView.setOnClickPendingIntent(R.id.notify_pause, pendingIntent);
        intent = new Intent(this, ControlReceiver.class);
        intent.putExtra("buttonId",BUTTON_NEXT);
        pendingIntent = PendingIntent.getBroadcast(this,BUTTON_NEXT,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.notify_next, pendingIntent);
        intent = new Intent(this, ControlReceiver.class);
        intent.putExtra("buttonId",BUTTON_EXIT);
        pendingIntent = PendingIntent.getBroadcast(this,BUTTON_EXIT,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.notify_exit, pendingIntent);

        notification.contentView.setCharSequence(R.id.notify_song, "setText", pref.getString("songName","Music"));
        notification.contentView.setCharSequence(R.id.notify_singer, "setText", pref.getString("singerName","Singer"));
        Intent startAll = new Intent(this, AllRoot.class);
        startAll.putExtra("position", position);
        PendingIntent contentIntent = PendingIntent.getActivity
                (this, 0,startAll, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentIntent = contentIntent;
        notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifyManager.notify(0, notification);
        //启动刷新进度条线程
        thread.start();
    }
    //设定歌曲当前位置 position
    private void setPosition(int buttonId)
    {
        if(dataList.size()<1)
            return;
        switch (buttonId) {
            case -1://last button
                if (MODE == LOOP)
                    position = (dataList.size() + position - 1) % dataList.size();
                if (MODE == QUEUE)
                    position = position == -1 ? -1 : position - 1;
                if (MODE == RANDOM)
                    position = (int) (Math.random() * dataList.size());
                break;
            case 1://next button
                if (MODE == LOOP)
                    position = (position + 1) % dataList.size();
                if (MODE == QUEUE)
                    position = position == dataList.size() ? dataList.size() : position + 1;
                if (MODE == RANDOM)
                    position = (int) (Math.random() * dataList.size());
                break;
        }
    }
    //发广播通知button背景改变
    private void changeButtonState()
    {
        Intent toAll = new Intent(AllRoot.REFLECT);
        toAll.putExtra("command", 2);
        toAll.putExtra("state", STATE);
        sendBroadcast(toAll);
        Log.i("lyric", "service send state to all");
        Intent toShow = new Intent(ShowActivity.ACTION);
        toShow.putExtra("command", 2);
        toShow.putExtra("state", STATE);
        sendBroadcast(toShow);
    }
    //更改通知栏歌曲图标的AsyncTask
    class MyServiceAsyncTask extends AsyncTask<Long, Integer, Bitmap>
    {
        private Context context;
        private RemoteViews view;
        public MyServiceAsyncTask(Context context, RemoteViews view)
        {
            this.context = context;
            this.view = view;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            view.setBitmap(R.id.notify_icon, "setImageBitmap", bitmap);
        }
        @Override
        protected Bitmap doInBackground(Long... params) {
            return AlbumGet.getArtwork(context, params[0] ,params[1], true);
        }
    }




}

