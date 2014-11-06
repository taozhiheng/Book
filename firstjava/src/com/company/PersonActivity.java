package com.company;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by taozhiheng on 14-11-4.
 */
public class PersonActivity extends Observable{
    public static final int GET_UP=0;
    public static final int HAVE_BREAKFAST=1;
    public static final int HAVE_LUNCH=2;
    public static final int HAVE_DINNER=3;
    public static final int GO_TO_BED=4;
    String activity="";
    Map<String ,Integer> map=new HashMap<String, Integer>();
    public void setActivity(String time,int task)
    {
       map.put(time, task);
    }
    public int getTaskNum()
    {
        return map.size();
    }
    public boolean isTime(String time)
    {
        try
        {
            int res=map.get(time);
            executeTask(res);
            return true;
        }catch(Exception e)
        {
            return false;
        }

    }
    private void executeTask(int task)
    {
        setChanged();
        notifyObservers(task);
    }
    public void getUp()
    {
        activity="get up";
        System.out.println("I am getting up!");
    }
    public void haveBreakfast()
    {
        activity="have breakfast";
        System.out.println("I am having breakfast!");
    }
    public void haveLunch()
    {
        activity="have lunch";
        System.out.println("I am having lunch!");
    }
    public void haveDinner()
    {
        activity="have dinner";
        System.out.println("I am having dinner!");
    }
    public void goToBed()
    {
        activity="go to bed";
        System.out.println("I am going to bed!");
    }
    public void startTask(String endtime)
    {
        final String deadline=endtime;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i=0;
                int num=PersonActivity.this.getTaskNum();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String time=df.format(new Date());
                while(time.compareTo(deadline)<0)
                {
                    time=df.format(new Date());
                    System.out.println(time);
                    if(PersonActivity.this.isTime(time))
                    {
                        i++;
                        System.out.println("Task process:"+i+"/"+num);
                    }
                    if(i==num)
                    {
                        System.out.println("All tasks have finished!");
                        break;
                    }
                    try{
                        Thread.sleep(1000*60);
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
