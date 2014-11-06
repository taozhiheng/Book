package computerdemo;

/**
 * Created by taozhiheng on 14-11-6.
 */
public class Display {
    private String name;
    private char level;
    public Display(String name,char level)
    {
        this.name=name;
        this.level=level;
    }
    public String getName()
    {
        return name;
    }
    public char getLevel()
    {
        return level;
    }
}
