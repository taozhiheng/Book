package demo;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by taozhiheng on 14-11-6.
 */
public class CarModel extends Observable{
    public static int UP=1;
    public static int BACK=2;
    public static int LEFT=3;
    public static int RIGHT=4;

    private int speed=0;
    private int direction=1;
    private int light=0;

    public void setSpeed(int speed)
    {
        this.speed=speed;
        setChanged();
        notifyObservers("speed");
    }
    public void speedUp()
    {
        speed+=1;
        setChanged();
        notifyObservers("speed");
    }
    public void speedDown()
    {
        if(speed>0)
            speed-=1;
        setChanged();
        notifyObservers("speed");
    }

    public void setDirection(int direction)
    {
        this.direction=direction;
        setChanged();
        notifyObservers("direction");
    }
    public void setLight(int light)
    {
        this.light=light;
        setChanged();
        notifyObservers("light");
    }
    public int getSpeed()
    {
        return speed;
    }
    public int getDirection()
    {
        return direction;
    }
    public int getLight()
    {
        return light;
    }

}
