package com.company;
import java.util.Date;
import java.text.SimpleDateFormat;
public class Main {

    public static void main(String[] args) {
	// write your code here
        final String deadline="2014-12-06 23:00";
        final PersonActivity person=new PersonActivity();

        person.setActivity("2014-11-06 20:24",PersonActivity.GET_UP);
        person.setActivity("2014-11-06 20:25",PersonActivity.HAVE_BREAKFAST);
        person.setActivity("2014-11-06 20:39",PersonActivity.HAVE_LUNCH);
        person.setActivity("2014-11-06 20:40",PersonActivity.HAVE_DINNER);
        person.setActivity("2014-11-06 20:41",PersonActivity.GO_TO_BED);
        person.setActivity("2014-11-06 20:42",PersonActivity.GET_UP);
        person.setActivity("2014-11-06 20:43",PersonActivity.HAVE_BREAKFAST);
        person.setActivity("2014-11-06 20:44",PersonActivity.HAVE_LUNCH);
        person.setActivity("2014-11-06 20:45",PersonActivity.HAVE_DINNER);
        person.setActivity("2014-11-06 20:46",PersonActivity.GO_TO_BED);

        person.addObserver(new MyObserver());
        person.startTask(deadline);
      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                int i=0;
                int num=person.getTaskNum();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String time=df.format(new Date());
                while(time.compareTo(deadline)<0)
                {

                    time=df.format(new Date());
                    System.out.println(time);
                    if(person.isTime(time))
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
        }).start();*/
    }
}
