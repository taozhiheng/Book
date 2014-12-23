package com.example.taozhiheng.musicplayer;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by taozhiheng on 14-12-14.
 * 程序主activity
 */
public class AllRoot extends Activity implements View.OnClickListener{

    private static List<HashMap<String, Object>> dataList;                              //歌曲数据列表

    private SQLiteDatabase db;               //数据库实例
    private SharedPreferences pref;          //键值对实例

    private int page = 0;                    //当前歌曲列表页面　０－本地音乐　１－我的最爱　２－我的下载　３－最近播放
    private int currentPosition = 0;         //当前选中的组位置
    private static int MODE = -1;            //当前播放模式
    private int STATE;                       //当前播放状态 STOP--停止状态　PLAY--正在播放状态　　PAUSE--暂停状态
    private final static int STOP = 0;       //停止状态
    private final static int PLAY = 1;       //播放状态
    private final static int PAUSE = 2;      //暂停状态

    private ReflectReceiver receiver;        //接受service的receiver
    private List<View> itemList;             //viewFlipper两个view
    private MyListAdapter adapter;           //自定义适配器
    private ListView lv;                     //列表
    private TextView localMusic;             //本地音乐
    private TextView myFavor;                //我的最爱
    private TextView myDownload;             //我的下载
    private TextView myList;                 //我的歌单
    private TextView recentPlay;             //最近播放

    private ImageView icon;                  //显示当前歌曲图标
    private TextView song;                   //显示当前歌曲名
    private TextView singer;                 //显示当前歌手名
    private Button last;                     //上一曲按钮
    private Button playOrPause;              //播放或暂停按钮
    private Button next;                     //下一曲按钮

    private ViewFlipper viewFlipper;         //切换显示页面的viewFlipper实例
    private long exitTime = 0;               //按下back键的时间
    private float startX;                    //手指按下的x坐标
    private float endX;                      //手指放开的x坐标
    private ActionBar actionBar;             //actionBar实例
    private String[] itemString = new String[]{"本地音乐","我的最爱","我的下载","最近播放"};//actionBar的字符串
    public final static String REFLECT = "android.receiver.action.reflect";             //广播动作常量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common);
        DBHelper helper = new DBHelper(this);
        db = helper.getWritableDatabase();
        pref = getSharedPreferences("song",MODE_PRIVATE);
        if(getIntent() != null)
        {
            currentPosition = getIntent().getIntExtra("position", 0);
            if(STATE != getIntent().getIntExtra("state", STOP))
            {
                STATE = getIntent().getIntExtra("state",STOP);
                if(STATE == PLAY)
                    playOrPause.setBackgroundResource(R.drawable.play);
                else
                    playOrPause.setBackgroundResource(R.drawable.pause);
            }
        }
        init();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("page", page);
        editor.commit();
        db.close();
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        Log.i("state:","AllRoot onResume");
        super.onResume();
        //读取播放信息
        if(currentPosition == 0)
            currentPosition = pref.getInt("position",0);
        if(dataList != null) {
            try
            {
                song.setText(getCurrentSong());
                singer.setText(getCurrentSinger());
                MyAsyncTask task = new MyAsyncTask(getBaseContext(), icon);
                task.execute(getCurrentSongId(), getCurrentAlbumId());
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            song.setText(pref.getString("songName", "当前歌曲"));
            singer.setText(pref.getString("singerName", "歌手"));
        }
        if(MODE == -1)
        {
            MODE = pref.getInt("mode",0);
        }
        setItem();
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
                if (endX - startX > 10 && itemList.get(1).getVisibility() == View.VISIBLE ) {// 表示向右滑动
                    previous();
                } else if (endX - startX < 10 && itemList.get(0).getVisibility() == View.VISIBLE) {// 表示向左滑动
                    next();
                }
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                if(itemList.get(1).getVisibility() == View.VISIBLE)
                {
                    previous();
                }
                else {
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        exitTime = System.currentTimeMillis();
                        Toast.makeText(getBaseContext(), "再次猛击返回桌面", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent i = new Intent(Intent.ACTION_MAIN);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addCategory(Intent.CATEGORY_HOME);
                        startActivity(i);
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id)
        {
            case android.R.id.home:
                previous();
                break;
            case R.id.action_settings:
                return true;
            case R.id.action_exit:
                Intent i = new Intent(this,BackgroundService.class);
                stopService(i);

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        int buttonId = -2;
        int chose = -1;
        switch(v.getId())
        {
            case R.id.localMusic:
                chose = 0;
                break;
            case R.id.myFavor:
                chose = 1;
                break;
            case R.id.myDownload:
                chose = 2;
                break;
            case R.id.recentPlay:
                chose = 3;
                break;
            case R.id.myList:
                //
                break;
            //跳转到播放界面
            case R.id.bottom_icon:
            case R.id.bottom_song:
            case R.id.bottom_singer:
                if(currentPosition >= dataList.size())
                    break;
                Intent i = new Intent(this,ShowActivity.class);
                i.putExtra("song", getCurrentSong());
                i.putExtra("singer", getCurrentSinger());
                i.putExtra("state",STATE);
                i.putExtra("time",(int)getCurrentTime());
                i.putExtra("mode",MODE);
                i.putExtra("favor",getCurrentFavor());
                i.putExtra("songId", getCurrentSongId());
                i.putExtra("albumId", getCurrentAlbumId());
                i.putExtra("songPath", getCurrentPath());
                startActivity(i);
                overridePendingTransition(R.anim.in_rtl,R.anim.out_rtl);
                break;
            case R.id.bottom_last:
                buttonId = -1;
                break;
            case R.id.bottom_playOrPause:
                buttonId = 0;
                break;
            case R.id.bottom_next:
                buttonId = 1;
                break;
        }
        if(buttonId != -2)
        {
            Intent intent = new Intent(this, ControlReceiver.class);
            intent.putExtra("buttonId", buttonId);
            sendBroadcast(intent);
        }
        if(chose != -1)
        {
            itemList.get(1).setVisibility(View.VISIBLE);
            itemList.get(0).setVisibility(View.GONE);
            page = chose;
            getData(page);
            adapter = new MyListAdapter(AllRoot.this, dataList);
            lv.setAdapter(adapter);
            next();
            Intent intent = new Intent(AllRoot.this, ControlReceiver.class);
            intent.putExtra("buttonId", 6);
            sendBroadcast(intent);
        }
  }
    //初始化
    private void init()
    {
        //寻找组件，绑定监视器

        itemList = new ArrayList<View>();
        itemList.add(findViewById(R.id.main));
        itemList.add(findViewById(R.id.activity_main));

        viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        localMusic = (TextView)itemList.get(0).findViewById(R.id.localMusic);
        myFavor = (TextView)itemList.get(0).findViewById(R.id.myFavor);
        myDownload = (TextView)itemList.get(0).findViewById(R.id.myDownload);
        myList = (TextView)itemList.get(0).findViewById(R.id.myList);
        recentPlay = (TextView)itemList.get(0).findViewById(R.id.recentPlay);

        icon = (ImageView)findViewById(R.id.bottom_icon);
        song = (TextView)findViewById(R.id.bottom_song);
        singer = (TextView)findViewById(R.id.bottom_singer);
        last = (Button)findViewById(R.id.bottom_last);
        playOrPause = (Button)findViewById(R.id.bottom_playOrPause);
        next = (Button)findViewById(R.id.bottom_next);
        lv = (ListView)itemList.get(1).findViewById(R.id.lv);

        localMusic.setOnClickListener(this);
        myFavor.setOnClickListener(this);
        myDownload.setOnClickListener(this);
        myList.setOnClickListener(this);
        recentPlay.setOnClickListener(this);

        icon.setOnClickListener(this);
        song.setOnClickListener(this);
        singer.setOnClickListener(this);
        last.setOnClickListener(this);
        playOrPause.setOnClickListener(this);
        next.setOnClickListener(this);

        //启动service
        Intent i = new Intent(this,BackgroundService.class);
        startService(i);
        STATE = STOP;
        //注册广播
        receiver = new ReflectReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(REFLECT);
        registerReceiver(receiver, filter);

        page = pref.getInt("page", 0);
        //获取适配器数据源
        new Thread(new Runnable() {
            @Override
            public void run() {
                getData(page);
                adapter = new MyListAdapter(AllRoot.this, dataList);
                lv.setAdapter(adapter);
                Intent intent = new Intent(AllRoot.this, ControlReceiver.class);
                intent.putExtra("buttonId", 6);
                intent.putExtra("change",false);
                sendBroadcast(intent);

            }
        }).start();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(AllRoot.this,ControlReceiver.class);
                intent.putExtra("buttonId", 5);
                intent.putExtra("position", position);
                sendBroadcast(intent);
            }
        });

    }
    //改变播放模式
    public static void setMode(int mode)
    {
        MODE = mode;
    }

    public class ReflectReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int command = intent.getIntExtra("command", 0);
            //1--设置位置，图片，歌曲，歌手，２－－设置播放按钮，３－－退出
            Log.i("lyric","all root command:"+command);
            switch(command)
            {
                case 1:
                    currentPosition = intent.getIntExtra("position", 0);
                    setItem();
                    song.setText(getCurrentSong());
                    singer.setText(getCurrentSinger());
                    MyAsyncTask task = new MyAsyncTask(getBaseContext(), icon);
                    task.execute(getCurrentSongId(), getCurrentAlbumId());
                    db.execSQL("update local_music set recent="+System.currentTimeMillis()+" where _id="+getCurrentId());
                    Log.i("favor change ","setText");
                    break;
                case 2:
                    if(STATE != intent.getIntExtra("state",STOP))
                    {
                        STATE = intent.getIntExtra("state",STOP);
                        if(STATE == PLAY)
                            playOrPause.setBackgroundResource(R.drawable.play);
                        else
                            playOrPause.setBackgroundResource(R.drawable.pause);
                    }
                    break;
                case 3:
                    Intent i = new Intent(AllRoot.this,BackgroundService.class);
                    stopService(i);
                    finish();
                    break;
                case 4://更改喜爱状态
                    db.execSQL("update local_music set favor="+intent.getIntExtra("favor", 0)+" where _id="+getCurrentId());
                    Log.i("favor change ",""+intent.getIntExtra("favor", 0));
                    break;
            }
        }
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
    //获得当前歌曲喜爱状态
    public int getCurrentFavor()
    {
        return (Integer)((HashMap<String, Object>) getCurrentInfo()).get("favor");
    }
    //获得当前歌曲id
    public int getCurrentId()
    {
        return (Integer)((HashMap<String, Object>) getCurrentInfo()).get("_id");
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
    //获得歌曲id
    private long getCurrentSongId()
    {
        return  (Long)((HashMap<String, Object>) getCurrentInfo()).get("songId");
    }
    //获得专辑id
    private long getCurrentAlbumId()
    {
        return  (Long)((HashMap<String, Object>) getCurrentInfo()).get("albumId");
    }
    //获得当前歌曲信息
    private Object getCurrentInfo()
    {
        return adapter.getItem(currentPosition);
    }
    //从数据库读取信息
    private void getData(int page)
    {
        Cursor cursor = db.query("local_music", null,null,null,null,null,null);
        if(cursor.getCount()<1)
        {
            cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            int songNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int singerNameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int timeLengthColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int songPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            int songIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
            ContentValues values;
            while(cursor.moveToNext())
            {
                db.beginTransaction();
                try
                {
                    values = new ContentValues();
                    values.put("song", cursor.getString(songNameColumn));
                    values.put("singer", cursor.getString(singerNameColumn));
                    values.put("length", cursor.getLong(timeLengthColumn));
                    values.put("path", cursor.getString(songPathColumn));
                    values.put("song_id", cursor.getLong(songIdColumn));
                    values.put("album_id", cursor.getLong(albumIdColumn));
                    values.put("favor", 0);
                    values.put("download", 0);
                    values.put("recent", 0);
                    db.insert("local_music", null, values);
                    db.setTransactionSuccessful();
                }
                finally
                {
                    db.endTransaction();
                }
            }
            cursor.close();
        }
        cursor.close();

        String where = null;
        switch(page)
        {
            case 0:
                break;
            case 1:
                where = "favor=1";
                break;
            case 2:
                where = "download=1";
                break;
            case 3:
                where = "recent>="+(System.currentTimeMillis()-24*60*60*1000);
                break;
        }
        cursor = db.query("local_music", new String[]{"song", "singer", "length", "path", "favor", "download", "recent", "_id", "song_id","album_id"}, where, null, null, null, null);
        Log.i("count", "--"+cursor.getCount());
        HashMap<String , Object> dataItem;
        dataList = new ArrayList<HashMap<String, Object>>();
        while(cursor.moveToNext())
        {
            dataItem = new HashMap<String, Object>();
            dataItem.put("songName", cursor.getString(0));
            dataItem.put("singerName", cursor.getString(1));
            dataItem.put("timeLength", cursor.getLong(2));
            dataItem.put("songPath", cursor.getString(3));
            dataItem.put("favor", cursor.getInt(4));
            dataItem.put("download", cursor.getInt(5));
            dataItem.put("recent", cursor.getInt(6));
            dataItem.put("_id", cursor.getInt(7));
            dataItem.put("songId", cursor.getLong(8));
            dataItem.put("albumId", cursor.getLong(9));
            dataList.add(dataItem);
        }
        cursor.close();
        Log.i("count", "dataList--"+dataList.size());
    }
    //取出歌曲列表
    public static List<HashMap<String, Object>> getList()
    {
        return dataList;
    }
    //标记选中项
    private void setItem()
    {
        try
        {
            lv.setItemChecked(currentPosition, true);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //切换到上一个view
    private void previous()
    {
        viewFlipper.setInAnimation(this, R.anim.in_ltr);
        viewFlipper.setOutAnimation(this, R.anim.out_ltr);
        viewFlipper.showPrevious();

        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("Music");
    }
    //切换到上一个view
    private void next()
    {
        viewFlipper.setInAnimation(this, R.anim.in_rtl);
        viewFlipper.setOutAnimation(this, R.anim.out_rtl);
        viewFlipper.showNext();
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(itemString[page]);
    }
}
