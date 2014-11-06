package demo;

/**
 * Created by taozhiheng on 14-11-5.
 */
public class Decorator_upper extends Decorator{
    public Decorator_upper(Component compent)
    {
        super(compent);
    }
    @Override
    public String change()
    {
        return super.change().toUpperCase();

    }
}
