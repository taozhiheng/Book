package com.example.taozhiheng.wifi;

import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by taozhiheng on 15-2-7.
 * 控制socket输入，输出流
 */
public class SocketCommunicate {

    public static final int RECEIVE_OK = 0;
    public static final int RECEIVE_ERROR = 1;
    public static final int SEND_OK = 2;
    public static final int SEND_ERROR = 3;

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean isRun = false;
    private MessageFeedbackListener listener;

    /**传入要处理的socket
     * */
    public SocketCommunicate(Socket socket)
    {
        this.socket = socket;
    }

    public void initStream()
    {
        if(socket == null)
            return;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        }catch (IOException e)
        {
            e.printStackTrace();
            Log.e("Error", "socketCommunicate getStream IOException");
        }
    }

    public void startReceiveMessage()
    {
        isRun = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String string;
                try {
                    while (isRun) {
                        if ((string=bufferedReader.readLine())!=null)
                        {
                            Log.e("Error","receive message:"+string);
                            if(listener != null)
                                listener.messageFeedback(RECEIVE_OK, string);
                        }
                    }
                }catch(IOException e)
                {
                    e.printStackTrace();
                    Log.e("Error", "receiverMessage IOException");
                    if(listener != null)
                        listener.messageFeedback(RECEIVE_ERROR, "receiverMessage IOException");
                }
            }
        }).start();
    }

    public void sendMessage(String message)
    {
        if(outputStream == null)
        {
            if(listener != null)
                listener.messageFeedback(SEND_ERROR, "sendMessage ,outputStream is null");
            return;
        }

        try {
            outputStream.write(message.getBytes());
            outputStream.flush();
            if(listener != null)
                listener.messageFeedback(SEND_OK, message);
        }catch (IOException e)
        {
            e.printStackTrace();
            Log.e("Error", "sendMessage IOException");
            if(listener != null)
                listener.messageFeedback(SEND_ERROR, "sendMessage IOException");
        }
    }

    public void closeSocket()
    {
        if(socket == null)
            return;
        isRun = false;
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        }catch (IOException e)
        {
            e.printStackTrace();
            Log.e("Error", "closeSocket IOException");
        }
    }

    public void setMessageFeedbackListener(MessageFeedbackListener listener)
    {
        this.listener = listener;
    }

    public interface MessageFeedbackListener
    {
        public void messageFeedback(int status, String Message);
    }
}
