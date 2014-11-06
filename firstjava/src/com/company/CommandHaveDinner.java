package com.company;

/**
 * Created by taozhiheng on 14-11-4.
 */
public class CommandHaveDinner implements MyCommand{
    private MyReceiver receiver;
    public CommandHaveDinner(MyReceiver receiver)
    {
        this.receiver=receiver;
    }
    public void change()
    {
        receiver.toHaveDinner();
    }
}
