package com.company;

/**
 * Created by taozhiheng on 14-11-4.
 */
public class CommandGoToBed implements MyCommand {
    private MyReceiver receiver;
    public CommandGoToBed(MyReceiver receiver)
    {
        this.receiver=receiver;
    }
    public void change()
    {
        receiver.toGoToBed();
    }
}
