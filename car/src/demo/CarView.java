package demo;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by taozhiheng on 14-11-6.
 */
public class CarView implements Observer{

    private int speed=0;
    private int direction=0;
    private int light=0;
    public void update(Observable observable,Object obj)
    {
        CarModel car=(CarModel)observable;
        String which=(String)obj;
        if(which.equals("speed"))
        {
            this.speed=car.getSpeed();
        }
        else if(which.equals("direction"))
        {
            this.direction=car.getDirection();
        }
        else if(which.equals("light"))
        {
            this.light=car.getLight();
        }
    }
    public void showSpeed()
    {
        System.out.println("Now,the car's speed is "+speed);
    }
    public void showDirection()
    {
        System.out.println("Now,the car's direction is "+direction);
    }
    public void showLight()
    {
        System.out.println("Now,the car's direction is "+light);
    }

}
