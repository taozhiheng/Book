package com.example.taozhiheng.musicplayer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by taozhiheng on 14-12-10.
 * 歌曲播放activity
 */
public class ShowActivity extends Activity implements View.OnClickListener{

    public static LrcView lyric;             //歌词视图
    private ImageView icon;                  //歌曲图标
    private TextView song;                   //显示当前歌曲名
    private TextView singer;                 //显示当前歌手名
    private TextView currentTime;            //显示当前时间
    private TextView maxTime;                //显示歌曲时长
    private ImageView favor;                 //喜爱标记
    private Button mode;                     //播放模式
    private Button last;                     //上一曲
    private Button playOrPause;              //播放或暂停
    private Button next;                     //下一曲
    private SeekBar progress;                //歌曲播放进度条
    private Receiver receiver;               //接受器
    private int MODE ;                       //播放模式
    private int FAVOR;                       //喜爱标记
    private float startX;                    //手指按下x坐标
    private float endX;                      //手指放开x坐标
    private String[] modeString = {"列表循环","单曲循环","顺序播放","随机播放"};                         //播放模式字符串
    private int[] modeId = {R.drawable.loop, R.drawable.repeat, R.drawable.queue, R.drawable.random};//播放模式图片id
    private String[] favorString = {"已取消收藏", "已收藏到我的最爱"};                                  //喜爱状态字符串
    private int[] drawableId = {R.drawable.unfavor, R.drawable.favor};                               //喜爱状态图片id
    public static final String ACTION = "android.receiver.action.receive";                           //广播动作常量

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                finish();
                overridePendingTransition(R.anim.in_ltr, R.anim.out_ltr);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        Log.i("show activity--","onDestroy");
        super.onDestroy();
        unregisterReceiver(receiver);
        overridePendingTransition(R.anim.in_ltr, R.anim.out_ltr);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.in_ltr, R.anim.out_ltr);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startX = event.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                endX = event.getX();
                if (endX - startX > 10) {// 表示向右滑动
                    finish();
                    overridePendingTransition(R.anim.in_ltr, R.anim.out_ltr);
                }
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent showLyric = new Intent(this, ControlReceiver.class);
        showLyric.putExtra("buttonId", 7);
        sendBroadcast(showLyric);
        Intent intent = getIntent();
        if(intent == null)
            return;
        MODE = intent.getIntExtra("mode",0);
        mode.setBackgroundResource(modeId[MODE%4]);
        song.setText(intent.getStringExtra("song"));
        singer.setText(intent.getStringExtra("singer"));
        MyAsyncTask task = new MyAsyncTask(getBaseContext(), icon);
        task.execute(intent.getLongExtra("songId", -1), intent.getLongExtra("albumId", -1));
        int length = intent.getIntExtra("time",180000);
        progress.setMax(length);
        maxTime.setText(getTime(length/1000));
        if(intent.getIntExtra("state",0) == 1)
        {
            playOrPause.setBackgroundResource(R.drawable.play);
        }
        else
        {
            playOrPause.setBackgroundResource(R.drawable.pause);
        }
        FAVOR = intent.getIntExtra("favor", 0);
        favor.setBackgroundResource(drawableId[FAVOR]);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent("android.receiver.action.RECEIVER");
        int buttonId = -2;
        switch (v.getId()) {
            case R.id.show_last:
                buttonId = -1;
                break;
            case R.id.show_next:
                buttonId = 1;
                break;
            case R.id.show_playOrPause:
                buttonId = 0;
                break;
            case R.id.favor:
                FAVOR = FAVOR^1;
                favor.setBackgroundResource(drawableId[FAVOR]);
                Intent intent = new Intent(AllRoot.REFLECT);
                intent.putExtra("command", 4);
                intent.putExtra("favor", FAVOR);
                sendBroadcast(intent);
                Toast.makeText(getBaseContext(), favorString[FAVOR],Toast.LENGTH_SHORT).show();
                break;
            case R.id.show_mode:
                MODE++;
                AllRoot.setMode(MODE%4);
                buttonId = 3;
                mode.setBackgroundResource(modeId[MODE%4]);
                Toast.makeText(getBaseContext(), "进入"+modeString[MODE%4]+"模式",Toast.LENGTH_SHORT).show();
                break;
        }
        if(buttonId != -2)
        {
            i.putExtra("buttonId", buttonId);
            sendBroadcast(i);
        }

    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int command = intent.getIntExtra("command", 0);
            switch (command)
            {
                case 1:
                    song.setText(intent.getStringExtra("songName"));
                    singer.setText(intent.getStringExtra("singerName"));
                    MyAsyncTask task = new MyAsyncTask(getBaseContext(), icon);
                    task.execute(intent.getLongExtra("songId", -1), intent.getLongExtra("albumId", -1));
                    int length = (int)intent.getLongExtra("songLength",100);
                    progress.setMax(length);
                    progress.setProgress(0);
                    maxTime.setText(getTime(length / 1000));
                    currentTime.setText("00:00");
                    lyric.setText(null);
                    break;
                case 2:
                    if(intent.getIntExtra("state", 0) == 1)
                    {
                        playOrPause.setBackgroundResource(R.drawable.play);
                    }
                    else
                    {
                        playOrPause.setBackgroundResource(R.drawable.pause);
                    }
                    break;
                case 3:
                    int value = intent.getIntExtra("progress", 0);
                    progress.setProgress(value);
                    currentTime.setText(getTime(value/1000));
                    break;
                case 4:
                    lyric.setIndex(intent.getIntExtra("index", 0));
                    lyric.invalidate();
                    lyric.setAnimation(AnimationUtils.loadAnimation(ShowActivity.this, R.anim.alpha_z));
            }
        }
    }

    //初始化
    private void init()
    {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        lyric = (LrcView)findViewById(R.id.lyric);
        icon = (ImageView)findViewById(R.id.show_icon);
        favor = (ImageView)findViewById(R.id.favor);
        song = (TextView)findViewById(R.id.show_song);
        singer = (TextView)findViewById(R.id.show_singer);
        currentTime = (TextView)findViewById(R.id.currentTime);
        maxTime = (TextView)findViewById(R.id.maxTime);
        mode = (Button)findViewById(R.id.show_mode);
        last = (Button)findViewById(R.id.show_last);
        playOrPause = (Button)findViewById(R.id.show_playOrPause);
        next = (Button)findViewById(R.id.show_next);
        progress = (SeekBar)findViewById(R.id.show_progress);
        mode.setOnClickListener(this);
        last.setOnClickListener(this);
        playOrPause.setOnClickListener(this);
        next.setOnClickListener(this);
        favor.setOnClickListener(this);

        receiver = new Receiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        registerReceiver(receiver, filter);
        final Intent i = new Intent(ShowActivity.this, ControlReceiver.class);

        progress.setEnabled(true);
        progress.setProgress(0);
        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                {
                    i.putExtra("buttonId", 2);
                    i.putExtra("progress", progress);
                    sendBroadcast(i);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    //将int值转为时间字符串
    private String getTime(int value)
    {
        String time = value/60+":"+value%60;
        if(time.charAt(1) == ':')
            time = "0"+time;
        if (time.length() == 4)
            time = time.substring(0,3)+"0"+time.charAt(3);
        return time;
    }
}
