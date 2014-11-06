package computerdemo;

/**
 * Created by taozhiheng on 14-11-6.
 */
public class Factory1 extends Factory {
    private static int numberH=0;
    private static int numberM=0;
    private static int numberL=0;
    @Override
     public Display createDisplay(char level)
    {
        int num=0;
        switch(level)
        {
            case 'h':
            case 'H':numberH++;num=numberH;break;
            case 'm':
            case 'M':numberM++;num=numberM;break;
            case 'l':
            case 'L':numberL++;num=numberL;break;
        }
        return new Display("Factory1_No."+num,level);
    }

}
