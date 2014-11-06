package demo;

/**
 * Created by taozhiheng on 14-11-5.
 */
public class ForTest {
    public static void main(String[] args)
    {
        Component text0=new ConcreteComponent("I am A good Man!");
        Component text1=new Decorator(text0);
        Component text2=new Decorator_upper(text1);
        Component text3=new Decorator_lower(text2);
        System.out.println(text0.change());
        System.out.println(text1.change());
        System.out.println(text2.change());
        System.out.println(text3.change());
    }

}
