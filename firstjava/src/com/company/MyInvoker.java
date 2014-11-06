package com.company;

/**
 * Created by taozhiheng on 14-11-4.
 */
public class MyInvoker {
    public MyCommand command;

    public void setCommand(MyCommand cmd){
        this.command = cmd;
    }

    public void change(){
        this.command.change();
    }
}
