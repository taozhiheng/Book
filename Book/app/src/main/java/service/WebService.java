package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hustunique.myapplication.MyApplication;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import data.UserPref;
import util.Constant;
import util.TimeUtil;
import web.Sync;
import web.Update;
import web.Web;

/**
 * Created by taozhiheng on 15-7-23.
 * 向服务器上传本地数据库变更,或者将数据从服务器拷贝到本地,一切操作在第一次同步后以本地为准
 */
public class WebService extends Service {

    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("web", "update,get minutes broadcast");
            if(!MyApplication.getUserOnLine() || MyApplication.getSync())
                return;
            startUpdate();
            Log.d("web", "update, start update");
        }
    };

    private BroadcastReceiver mNetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                                 .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    MyApplication.setUserOnLine(mNetworkInfo.isAvailable());
                }
            }
            else
                MyApplication.setUserOnLine(false);
        }
    };

    private BroadcastReceiver mDateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UserPref.init(context);
            long millis = System.currentTimeMillis();
            String time = TimeUtil.getNeedTime(millis);
            String time1 = TimeUtil.getNeedTime(millis + 24 * 60 * 60 * 1000);
            String time2 = TimeUtil.getNeedTime(millis + 24*60*60*1000*2);
            String urlHead = MyApplication.getUrlHead() + Constant.URL_SAYING+"?date=";
            Web.queryWords(urlHead+time, new WordsCall(0));
            Web.queryWords(urlHead+time1, new WordsCall(1));
            Web.queryWords(urlHead+time2, new WordsCall(2));

        }
    };

    class WordsCall implements Callback
    {
        private int index;

        public WordsCall(int index)
        {
            this.index = index;
        }

        @Override
        public void onFailure(Request request, IOException e) {

        }

        @Override
        public void onResponse(Response response) throws IOException {
            try {
                JSONObject json = new JSONObject(response.body().string());
                String author = json.getString("name");
                String saying = json.getString("saying");
                UserPref.setWords(index, saying+"&"+author);
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

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
        registerReceiver(mUpdateReceiver, filter);
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetReceiver, filter);
        filter = new IntentFilter(Intent.ACTION_DATE_CHANGED);
        registerReceiver(mDateReceiver, filter);
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
            startUpdate();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("web", "destroy service, unregister receiver");
        unregisterReceiver(mUpdateReceiver);
        unregisterReceiver(mNetReceiver);
        unregisterReceiver(mDateReceiver);
    }

    private void startSync(Context context, int choice)
    {

//        new MyAsyncTask(context, choice).execute();
        Sync.executeSync(context, MyApplication.getAuthorization(), choice);
    }

    private void startUpdate()
    {
//        new InsertAsyncTask(context).execute();
        Update.executeUpdate(MyApplication.getAuthorization());
    }

}
