package demo;

/**
 * Created by taozhiheng on 14-11-6.
 */
public class Test {
    public static void main(String[] args)
    {
        CarView view=new CarView();
        CarModel car=new CarModel();
        Controller control=new Controller();
        control.setModel(car);
        control.addView(view);
        control.showView(view);
        control.changeCarSpeed(20);
        try{
            Thread.sleep(1000);
        }catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        control.changeCarDirection(CarModel.LEFT);
        try{
            Thread.sleep(1000);
        }catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        control.changeCarLight(3);
        try{
            Thread.sleep(1000);
        }catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }


}
