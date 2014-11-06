package demo;

/**
 * Created by taozhiheng on 14-11-5.
 */
public class ConcreteComponent implements Component{
    public String text=null;
    public ConcreteComponent()
    {

    }
    public ConcreteComponent(String text)
    {
        this.text=text;
    }
    public void setText(String text)
    {
        this.text=text;
    }
    public String change()
    {
        return text;
    }
}
