package computerdemo;

/**
 * Created by taozhiheng on 14-11-6.
 */
public class AutoInstaller {
    private Factory factory;
    private char level;
    public AutoInstaller(Factory factory, char level)
    {
        this.factory=factory;
        if(level>='a'&&level<='z')
            level=(char)('A'+level-'a');
        this.level=level;
    }
    public void chooseFactory(Factory factory)
    {
        this.factory=factory;
    }
    public void chooseLevel(char level)
    {
        if(level>='a'&&level<='z')
            level=(char)('A'+level-'a');
        this.level=level;
    }
    public Computer InstallComputer()
    {
        Host host=new Host("standard",level);
        Display display=factory.createDisplay(level);
        Computer computer=new Computer();
        computer.installHost(host);
        computer.installDisplay(display);
        System.out.println("Congratulations!Your new computer has been prepared well!");
        return computer;
    }
}
