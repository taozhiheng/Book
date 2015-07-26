package tools;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 本类是封装了对时间操作的静态工具类。
 * 部分源码来源自网络。
 * 
 * @author 寂静的写者
 * @since 2015年2月25日19:00:29
 * @blog http://blog.csdn.net/LonelyWriter
 */
public final class TimeUtil {
	
	/* 静态变量 */
	
	/**
	 * 一天为24*60*60*1000 = 86400000毫秒
	 */
	public static final int ONE_DAY_MILLIS = 86400000;
	/**
	 * 一小时为60*60*1000=3600000毫秒
	 */
	public static final int ONE_HOUR_MILLIS = 3600000;
	
	/**
	 * 后天之后
	 */
	public static final int DAYS_LATER = 4;
	/**
	 * 后天
	 */
	public static final int DAY_AFTER_TOMMORROW = 3;
	/**
	 * 明天
	 */
	public static final int DAY_TOMMORROW = 2;
	/**
	 * 今天
	 */
	public static final int DAY_TODAY = 1;
	/**
	 * 未知时间
	 */
	public static final int DAY_UNKNOW = 0;
	/**
	 * 昨天
	 */
	public static final int DAY_YESTERDAY = -1;
	/**
	 * 前天
	 */
	public static final int DAY_BEFORE_YESTERDAY = -2;
	/**
	 * 前天之前
	 */
	public static final int DAYS_FARTHER = -3;
	
	/* 判断时间 */
	
	/**
	 * 给出一个时间的描述。比如“昨天 12:30”。
	 * 
	 * @param time
	 * @param defaultPattern
	 *            当time不在前天与后天之，则调用此默认格式初始化时间描述。 <br>
	 *            如果为null，将默认使用“yyyy-MM-dd”作为时间描述
	 * @return
	 *         时间的描述
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getDescription(long time, String defaultPattern) {
		long currentTime = System.currentTimeMillis();
		
		// 获取结尾的 小时和分钟
		String endTime = getFormatedTimeStr(time, "HH:mm");
		int whatDay = whatDay(new Date(time), new Date(currentTime));
		
		switch (whatDay) {
			case DAY_TODAY:
				return "今天 " + endTime;
				
			case DAY_YESTERDAY:
				return "昨天 " + endTime;
				
			case DAY_TOMMORROW:
				return "明天 " + endTime;
				
			case DAY_BEFORE_YESTERDAY:
				return "前天 " + endTime;
				
			case DAY_AFTER_TOMMORROW:
				return "后天 " + endTime;
				
			default:
				return getFormatedTimeStr(time, defaultPattern != null ? defaultPattern : "yyyy-MM-dd");
		}
	}
	
	/**
	 * 按照时间格式给出一个时间的描述。比如“2014年8月17日 09:50”
	 * 
	 * @param time
	 * @param pattern
	 *            时间的格式。如果为null，则默认使用
	 *            “yyyy-MM-dd HH:mm”
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getFormatedTimeStr(long time, String pattern) {
		Date date = new Date(time);
		DateFormat format = new SimpleDateFormat(pattern != null ? pattern : "yyyy-MM-dd HH:mm");
		return format.format(date);
	}
	
	/**
	 * 判断judgeTime相对于relativeTime的时间，比如说今天，昨天，或者前天
	 * 
	 * @param judgeTime
	 *            需要判断的时间，比如昨天。
	 * @param relativeTime
	 *            相对的时间，比如今天。如果为null的话，将默认设置为今天
	 * @return
	 *         时间的标号。请参见： <li> {@link #DAY_AFTER_TOMMORROW}
	 *         <li> {@link #DAY_BEFORE_YESTERDAY} <li>
	 *         {@link #DAY_TODAY} <li>
	 *         {@link #DAY_TOMMORROW} <li>
	 *         {@link #DAY_YESTERDAY} <li>
	 *         {@link #DAYS_FARTHER} <li>
	 *         {@link #DAYS_LATER} <li>{@link #DAY_UNKNOW}
	 */
	@SuppressLint("SimpleDateFormat")
	public static int whatDay(Date judgeTime, Date relativeTime) {
		if (relativeTime == null) {
			relativeTime = new Date(System.currentTimeMillis());
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String relativeTimeStr = format.format(relativeTime);
		// 获取今天0点的时间
		Date relativeDay;
		try {
			relativeDay = format.parse(relativeTimeStr);
			
			// 用judgeTime减去相对时间的那天的0点。
			long millis = judgeTime.getTime() - relativeDay.getTime();
			
			if (millis >= 0) {
				// 今天0点之后的时间
				if (millis < ONE_DAY_MILLIS) {
					// 1天之内
					return DAY_TODAY;
				} else if (millis >= ONE_DAY_MILLIS && millis < 2 * ONE_DAY_MILLIS) {
					// 1~2天之内
					return DAY_TOMMORROW;
				} else if (millis >= 2 * ONE_DAY_MILLIS && millis < 3 * ONE_DAY_MILLIS) {
					// 2~3之内
					return DAY_AFTER_TOMMORROW;
				} else {
					return DAYS_LATER;
				}
			} else {
				// 今天0点之前的时间
				millis = -millis;
				if (millis < ONE_DAY_MILLIS) {
					// 之前1天之内
					return DAY_YESTERDAY;
				} else if (millis >= ONE_DAY_MILLIS && millis < 2 * ONE_DAY_MILLIS) {
					// 之前1~2天之内
					return DAY_BEFORE_YESTERDAY;
				} else {
					return DAYS_FARTHER;
				}
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
			// 报错则默认是unknow
			return DAY_UNKNOW;
		}
	}
}
