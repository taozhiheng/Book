package demo;

/**
 * Created by taozhiheng on 14-11-5.
 */
public class Decorator_lower extends Decorator {
    public Decorator_lower(Component component)
    {
        super(component);
    }
    @Override
    public String change()
    {
        return super.change().toUpperCase();
    }
}
