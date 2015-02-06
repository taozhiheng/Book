package com.example.taozhiheng.wifi;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by taozhiheng on 15-2-7.
 * 用于开启服务器线程
 */
public class ServerThread extends  Thread implements SocketInterface {

    private Socket socket;
    private int port;

    /**传入连接端口port,必须与服务器端口一致
     * */
    public ServerThread(int port)
    {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
        }catch (IOException e)
        {
            e.printStackTrace();
            Log.e("Error", "serverSocket IOException");
        }
    }

    public Socket getSocket()
    {
        return socket;
    }
}
