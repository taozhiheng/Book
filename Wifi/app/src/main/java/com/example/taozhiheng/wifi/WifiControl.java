package com.example.taozhiheng.wifi;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by taozhiheng on 15-2-7.
 * 控制WifiDirect
 */
public class WifiControl {
    private Context context;
    private WifiP2pManager.ActionListener actionListener;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;

    /**传入一个context对象来构造实例
     * */
    public WifiControl(Context context)
    {
        this.context = context;
    }

    /**初始化WifiDirect,取得wifiP2pManager,channel
     * 可以通过监听WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION动作取得设备状态
     * intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, WifiP2pManager.WIFI_P2P_STATE_DISABLED);
     * */
    public void initWifiDirect()
    {
        wifiP2pManager = (WifiP2pManager)context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(context, context.getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                initWifiDirect();
            }
        });
    }

    /**调用设置未打开WifiDirect
     *在WifiDirect为打开时通用系统设置
     * */
    public void startWifiDirect()
    {
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        context.startActivity(intent);
    }

    /**搜索WifiDirect设备
     * 可以通过监听WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION动作取得当前搜索到的所有设备信息
     * wifiP2pManager.requestPeers(channel, new WifiP2pManager.PeerListListener(){...} )
     * */
    public void discoverPeers()
    {
        wifiP2pManager.discoverPeers(channel, actionListener);
        Log.e("MainActivity", "try to discover");
    }

    /**与某个已知的设备建立连接
     * 可以通过监听WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION动作取得设备的连接状态
     * 连接成功时可以取得hosAdress,建立socket连接
     * */
    public void connectTo(WifiP2pDevice device)
    {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        wifiP2pManager.connect(channel, config, actionListener);
        Log.e("MainActivity", "address:"+config.deviceAddress);
    }
    /**停止搜索WifiDirect设备,API16
     * */
    public void stopDiscover()
    {
        wifiP2pManager.cancelConnect(channel, actionListener);
    }

    /**断开连接
     * */
    public void stopConnet()
    {
        wifiP2pManager.cancelConnect(channel,actionListener);
    }
    /**绑定WifiDirect事件监听器
     * 在wifiP2pManager执行动作时被回调
     * @param actionListener
     */
    public void setActionListener(WifiP2pManager.ActionListener actionListener)
    {
        this.actionListener = actionListener;
    }
}
