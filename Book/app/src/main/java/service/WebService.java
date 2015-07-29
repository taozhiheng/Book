package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hustunique.myapplication.MyApplication;

import adapter.BookRecyclerAdapter;
import util.Constant;

/**
 * Created by taozhiheng on 15-7-23.
 * 向服务器上传本地数据库变更,或者将数据从服务器拷贝到本地,一切操作在第一次同步后以本地为准
 */
public class WebService extends Service {

    private BroadcastReceiver myReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("web", "update,get minutes broadcast");
            if(!MyApplication.getUserOnLine() || MyApplication.getSync())
                return;
            startUpdate(getApplicationContext());
            Log.d("web", "update, start update");
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("web", "create service, register receiver");
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(myReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null)
            return super.onStartCommand(null, flags, startId);
        int cmd = intent.getIntExtra(Constant.KEY_CMD, -1);
        Log.d("net", "start service, command:"+cmd);
        if(cmd == Constant.CMD_SYNC && MyApplication.getUserOnLine())
        {
            startSync(getApplicationContext(), intent.getIntExtra(Constant.KEY_CHOICE, Constant.CHOICE_LOCAL));
        }
        else if(cmd == Constant.CMD_UPDATE && MyApplication.getUserOnLine())
        {
            startUpdate(getApplicationContext());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("web", "destroy service, unregister receiver");
        unregisterReceiver(myReceiver);
    }

    private void startSync(Context context, int choice)
    {

        new MyAsyncTask(context, choice).execute();
    }

    private void startUpdate(Context context)
    {
        new InsertAsyncTask(context).execute();
    }

}
