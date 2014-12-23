package com.example.taozhiheng.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by taozhiheng on 14-12-8.
 * activity与service通信的broadcastReceiver
 */
public class ControlReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent("android.service.action.SERVICE");
        int buttonId = intent.getIntExtra("buttonId",0);
        i.putExtra("buttonId",buttonId);
        if(buttonId == 2)
            i.putExtra("progress", intent.getIntExtra("progress", 0));
        if(buttonId == 5)
            i.putExtra("position", intent.getIntExtra("position", 0));
        if(buttonId == 6)
            i.putExtra("change", intent.getBooleanExtra("change", true));
        context.startService(i);
    }
}
