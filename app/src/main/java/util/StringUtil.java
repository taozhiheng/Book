package util;

/**
 * Created by taozhiheng on 15-7-17.
 *
 */
public class StringUtil {

    public static int getSubStringCount(String str, String sub)
    {
        int count = 0;
        int index;
        while((index = str.indexOf(sub))!= -1)
        {
            count++;
            str = str.substring(index+1);
        }
        return count;
    }
}
