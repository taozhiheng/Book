package com.example.taozhiheng.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private boolean isRun;
    private TextView textView;
    private Button button;
    private ListView list;
    private EditText input;
    private Button send;
    private InputStream inputStream;
    private OutputStream outputStream;
    private ArrayAdapter adapter;

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;

    private List<WifiP2pDevice> deviceList = new ArrayList<>();
    private List<String> deviceNameList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView) findViewById(R.id.text);
        button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(discoverRunnable).start();
            }
        });
        list = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceNameList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WifiP2pDevice device = deviceList.get(position);
                Log.e("MainActivity", "start to connect:"+device.deviceName);
                new Thread(new  MyRuunable(device)).start();
            }
        });

        input = (EditText) findViewById(R.id.input);
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(input.getText().toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerWifiRecevier();
        registerPeerReceiver();
        regitsterConnectionRecevier();
        initWifiDirect();
    }

    private void initWifiDirect()
    {
        wifiP2pManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this, getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                initWifiDirect();
            }
        });
    }

    private WifiP2pManager.ActionListener actionListener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
            Log.e("MainActivity", "succeed");
        }

        @Override
        public void onFailure(int reason) {
            String errorMessage = "WiFi Direct Failed:";
            switch(reason)
            {
                case WifiP2pManager.BUSY:
                    errorMessage += "Framework busy";
                    break;
                case WifiP2pManager.ERROR :
                    errorMessage += "Internal error";
                    break;
                case WifiP2pManager.P2P_UNSUPPORTED:
                    errorMessage += "Unknown error";
                    break;
                default:
                    errorMessage += "Unknown error";
            }
            Log.e("MainActivity", errorMessage);
            //wifiP2pManager.discoverPeers(channel, null);
        }
    };

    private void startWifiDirect()
    {
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivityForResult(intent, 1);
    }



    private Runnable discoverRunnable = new Runnable() {
        @Override
        public void run() {
            discoverPeers();
        }
    };
    private void discoverPeers()
    {
        wifiP2pManager.discoverPeers(channel, actionListener);
        Log.e("MainActivity", "try to discover");
    }

    private void connectTo(WifiP2pDevice device)
    {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        wifiP2pManager.connect(channel, config, actionListener);
        Log.e("MainActivity", "address:"+config.deviceAddress);
    }
    private class MyRuunable implements Runnable{
       private WifiP2pDevice device;
       public MyRuunable(WifiP2pDevice device)
       {
           this.device = device;
       }
       @Override
       public void run() {
           connectTo(device);
       }
   }




    class SocketRunable implements Runnable
    {
        private String host;
        private int which;//0-client, 1-server

        public SocketRunable(String host,int which)
        {
            this.host = host;
            this.which = which;
        }

        @Override
        public void run() {
            Socket socket;
            try {
                if (which == 0) {
                    InetSocketAddress socketAddress = new InetSocketAddress(host, 6666);
                    socket = new Socket();
                    socket.bind(null);
                    socket.connect(socketAddress, 5000);

                } else {

                    ServerSocket serverSocket = new ServerSocket(6666);
                    socket = serverSocket.accept();

                }
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
                isRun = true;
                Log.e("MainActivity", "socket connect");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String string;
                        try {
                            while (isRun) {
                                if ((string=bufferedReader.readLine())!=null)
                                {
                                    Message message = Message.obtain();
                                    message.what = 1;
                                    message.obj = string;
                                    handler.sendMessage(message);
                                }
                            }
                        }catch(IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void regitsterConnectionRecevier()
    {
        IntentFilter filter = new IntentFilter(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        registerReceiver(connectionChangedRecevier, filter);
    }

    private BroadcastReceiver connectionChangedRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected())
            {
                Log.e("MainActivity", "connect succeed!");
                wifiP2pManager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        if (info.groupFormed) {
                            if (info.isGroupOwner) {
                                Log.e("MainActivity", "owner" + info.groupOwnerAddress);
                                new Thread(new SocketRunable(info.groupOwnerAddress.getHostAddress(),1)).start();
                            } else {
                                new Thread(new SocketRunable(info.groupOwnerAddress.getHostAddress(),0)).start();
                                Log.e("MainActivity", "user:" + info.groupOwnerAddress);
                            }
                        }
                    }
                });
            }
            else
            {
                Log.e("MainActivity", "disconnect");
            }
        }
    };

    private void registerPeerReceiver()
    {
        IntentFilter filter = new IntentFilter(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        registerReceiver(peerDiscoveryReceiver, filter);
    }

    private BroadcastReceiver peerDiscoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            wifiP2pManager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList peers) {
                    deviceList.clear();
                    deviceList.addAll(peers.getDeviceList());
                    deviceNameList.clear();
                    for(WifiP2pDevice device : deviceList)
                    {
                        deviceNameList.add(device.deviceName);
                        Log.e("MainActivity", device.deviceName);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };

    private void registerWifiRecevier()
    {
        IntentFilter filter = new IntentFilter(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, filter);
    }

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, WifiP2pManager.WIFI_P2P_STATE_DISABLED);
            switch (state)
            {
                case WifiP2pManager.WIFI_P2P_STATE_ENABLED:
                    textView.setText("wifi enabled!");
                    break;
                default:
                    textView.setText("wifi disabled!");
                    startWifiDirect();
            }
        }
    };
    private void sendMessage(String message)
    {
        if(outputStream == null)
            return;
        try {
            outputStream.write(message.getBytes());
            outputStream.flush();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        input.setText(null);
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(MainActivity.this, "收到了："+msg.obj.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        isRun = false;
        super.onDestroy();
    }
}
