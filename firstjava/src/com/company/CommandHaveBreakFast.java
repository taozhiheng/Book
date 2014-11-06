package com.company;

/**
 * Created by taozhiheng on 14-11-4.
 */
public class CommandHaveBreakFast implements MyCommand{
    private MyReceiver receiver;
    public CommandHaveBreakFast(MyReceiver receiver)
    {
        this.receiver=receiver;
    }
    public void change()
    {
        receiver.toHaveBreakfast();
    }
}
