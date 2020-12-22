package workflow.common.utils;

import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
	public static final SimpleDateFormat df=new SimpleDateFormat("yyyy/MM/dd HH:mm");
	
	public static final long ONE_DAY = 86400000;
	public static final long ONE_HOUR= 3600000;
	public static final long ONE_MINUTE= 60000;

	public static boolean isSameDay(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		if (c1.get(1) != c2.get(1))
			return false;

		return (c1.get(6) == c2.get(6));
	}

	public static long roundToSecond(long time) {
		if (time % 1000L != 0L)
			return (time = (time / 1000L + 1L) * 1000L);

		return time;
	}

	public static long floorToSecond(long time) {
		return (time = time / 1000L * 1000L);
	}

	public static int getCurrentHour() {
		Calendar calendar = Calendar.getInstance();

		return calendar.get(11);
	}

	public static String timeFormatter(long second) {
		long hour = second / 3600L;
		second %= 3600L;
		long minute = second / 60L;
		second %= 60L;

		return hour + "小时" + minute + "分钟" + second + "秒";
	}

	public static boolean isInTimeFrame(String start, String end) {
		Calendar now = Calendar.getInstance();

		String[] parts = StringUtils.split(start, ":");
		int hour = new Integer(parts[0]).intValue();
		int minute = new Integer(parts[1]).intValue();
		Calendar startTime = Calendar.getInstance();
		startTime.set(11, hour);
		startTime.set(12, minute);
		startTime.set(13, 0);

		parts = StringUtils.split(end, ":");
		hour = new Integer(parts[0]).intValue();
		minute = new Integer(parts[1]).intValue();
		Calendar endTime = Calendar.getInstance();
		endTime.set(11, hour);
		endTime.set(12, minute);
		endTime.set(13, 0);

		return ((now.before(endTime)) && (now.after(startTime)));
	}

	public static long getCountdown(String time) {
		Calendar nextTime = Calendar.getInstance();

		String[] parts = StringUtils.split(time, ":");
		int hour = new Integer(parts[0]).intValue();
		int minute = new Integer(parts[1]).intValue();
		nextTime.set(11, hour);
		nextTime.set(12, minute);
		nextTime.set(13, 0);

		if (nextTime.before(Calendar.getInstance()))
			return (nextTime.getTime().getTime() + 86400000L - System
					.currentTimeMillis());

		return (nextTime.getTime().getTime() - System.currentTimeMillis());
	}
	
	public static String getYYYYMMDDHHMMDDTimeStr(Timestamp time){
		return df.format(new Date(time.getTime()));
	}
	
	public static String getYYYYMMDDHHMMDDTimeStr(Long time){
		if(time<=0){
			return "";
		}
		return df.format(new Date(time));
	}
	
	@SuppressWarnings("static-access")
	public static int getHourOfDay(){
		Calendar can=Calendar.getInstance();
		return can.get(can.HOUR_OF_DAY);
	}
	/**
	 * 获取服务器下次刷新时间
	 * @return
	 */
	public static long getServerRefreshTime(){
		//系统刷新时间为凌晨3点-所以要当前时间-3个小时
		long time=System.currentTimeMillis()-(ONE_HOUR*3)+ONE_DAY;
		
		Calendar can=Calendar.getInstance();
		can.setTimeInMillis(time);
		can.set(Calendar.HOUR_OF_DAY, 3);
		can.set(Calendar.MINUTE, 0);
		can.set(Calendar.SECOND, 0);
		can.set(Calendar.MILLISECOND, 0);
		return can.getTimeInMillis();
	}
	
	/**
	 * 获取本月的第一个星期一的日期
	 * @return
	 */
	public static int getMonthFirstMonday(){
		Calendar can=Calendar.getInstance();
		//can.add(Calendar.MONTH, 1);
		can.set(Calendar.DAY_OF_MONTH, 1);
		int i=1;
		while(can.get(Calendar.DAY_OF_WEEK)!=Calendar.MONDAY){
			can.set(Calendar.DAY_OF_MONTH, i++);
		}
		return can.get(Calendar.DAY_OF_YEAR);
	}
	
	public static long timeStringToLong(String timeStr){
		long result=0l;
		try {
			result=df.parse(timeStr).getTime();
		} catch (ParseException e) {
			
		}
		return result;
	}

}