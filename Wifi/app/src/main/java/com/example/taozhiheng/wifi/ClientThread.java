package com.example.taozhiheng.wifi;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by taozhiheng on 15-2-7.
 * 用于开启客户端线程
 */
public class ClientThread extends  Thread implements SocketInterface {
    private Socket socket;
    private String host;
    private int port;
    private int timeout = 10000;

    /**传入主机地址host,连接端口timeout,超时timeout
     * *
     * @param host
     * @param port
     * @param timeout
     */
    public ClientThread(String host, int port, int timeout)
    {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        try
        {
            InetSocketAddress socketAddress = new InetSocketAddress(host, port);
            socket = new Socket();
            socket.bind(null);
            socket.connect(socketAddress, timeout);

        }catch (IOException e)
        {
            e.printStackTrace();
            Log.e("Error", "ClientSocket IOException");
        }
    }

    public Socket getSocket()
    {
        return socket;
    }
}
