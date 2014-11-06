package com.company;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by taozhiheng on 14-11-4.
 */
public class MyObserver implements Observer {
    public MyObserver()
    {

    }
    @Override
    public void update(Observable observable, Object data) {
        int task=(Integer)data;
        PersonActivity person=(PersonActivity)observable;
        MyCommand cmd=null;
        MyReceiver receiver=new MyReceiver(person);
        MyInvoker invoker=new MyInvoker();
        switch(task)
        {
            case 0:cmd=new CommandGetUp(receiver);break;
            case 1:cmd=new CommandHaveBreakFast(receiver);break;
            case 2:cmd=new CommandHaveLunch(receiver);break;
            case 3:cmd=new CommandHaveDinner(receiver);break;
            case 4:cmd=new CommandGoToBed(receiver);break;
        }
        invoker.setCommand(cmd);
        invoker.change();
    }
}
