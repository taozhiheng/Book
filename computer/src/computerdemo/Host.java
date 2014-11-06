package computerdemo;

/**
 * Created by taozhiheng on 14-11-6.
 */
public class Host {
    private static int number=0;
    private String name;
    private char level;
    public Host(String name,char level)
    {
        number++;
        this.name=name+number;
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
