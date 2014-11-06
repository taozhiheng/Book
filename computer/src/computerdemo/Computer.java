package computerdemo;

/**
 * Created by taozhiheng on 14-11-6.
 */
public class Computer {
    private Host host;
    private Display display;
    public Computer()
    {

    }
    public void installHost(Host host)
    {
        this.host=host;
        System.out.println("The host:"+host.getName()+" -version_"+host.getLevel()+" has been installed into your computer!");
    }
    public void installDisplay(Display display)
    {
        this.display=display;
        System.out.println("The display:"+display.getName()+" -version_"+display.getLevel()+" has been installed into your computer!");
    }
    public Host getHost()
    {
        return host;
    }
    public Display getDisplay()
    {
        return display;
    }
}
