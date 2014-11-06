package computerdemo;

/**
 * Created by taozhiheng on 14-11-6.
 */
public class Test {
    public static void main(String[] args)
    {
        char level = 'H';
        Factory factory = new Factory1();
        AutoInstaller install=new AutoInstaller(factory,level);
        Computer computer1= install.InstallComputer();
        install.chooseFactory(new Factory2());
        Computer computer2=install.InstallComputer();
        install.chooseLevel('m');
        Computer computer3=install.InstallComputer();
    }
}
