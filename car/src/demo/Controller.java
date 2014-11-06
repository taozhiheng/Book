package demo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taozhiheng on 14-11-6.
 */
public class Controller {
    private CarModel car;
    private CarView instrument;
    private List<CarView> list=new ArrayList<CarView>();
    public void setModel(CarModel car)
    {
        this.car=car;
    }
    public void addView(CarView view)
    {
        if(car!=null)
        {
            car.addObserver(view);
            list.add(view);
        }

    }
    public void removeView(CarView view)
    {
        list.remove(view);
    }
    public void changeCarSpeed(int speed)
    {
        car.setSpeed(speed);
    }
    public void carSpeedUp()
    {
        car.speedUp();
    }
    public void carSpeedDown()
    {
        car.speedDown();
    }
    public void changeCarDirection(int direction)
    {
        car.setDirection(direction);
    }
    public void changeCarLight(int light)
    {
        car.setLight(light);
    }
    public void showView(CarView view)
    {
        final CarView target=view;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i=0;
                while(i<10)
                {
                    target.showSpeed();
                    target.showDirection();
                    target.showLight();
                    System.out.println();
                    i++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();
    }
}
