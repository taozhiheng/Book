package util;

import android.text.format.DateFormat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by taozhiheng on 15-2-10.
 *
 */
public class TimeUtil {

    public static Calendar getTimeAfterInSecs(int secs)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, secs);
        return calendar;
    }

    public static long getTimeMillis(int year, int month, int day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }

    public static Calendar getCurrentTime()
    {
        return Calendar.getInstance();
    }

    public static Calendar getTodayAt(int hours)
    {
        Calendar today = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int day  = today.get(Calendar.DATE);
        calendar.set(year, month, day, hours,0,0);
        return calendar;
    }

    public static String getUTCString(long millis, String format)
    {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.setTimeInMillis(millis);
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return DateFormat.format(format, cal).toString();
    }


    public static String getNeedTime(long millis)
    {
        String str = getUTCString(millis, Constant.TIME_FORMAT).replace('.', 'T')+".000000";
        Log.d("web", "get need time,millis:"+millis+" timeStr:"+str);
        return str;
    }


    public static String getDateTimeString(Calendar calendar,String format)
    {
        //format:"MM/dd/yyyy hh:mm:ss"
        if(format == null)
            format = "MM/dd/yyyy hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setLenient(false);
        return simpleDateFormat.format(calendar.getTime());
    }

}
