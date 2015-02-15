package tw.supra.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import tw.supra.epe.R;
import android.content.Context;

public class TimeUtil {
	private static final String LOG_TAG = TimeUtil.class.getSimpleName();
	public static final long SECOND = 1000;
	public static final long MINUTE = 60 * SECOND;
	public static final long HOUR = 60 * MINUTE;
	public static final long DAY = 24 * HOUR;
	public static final long WEEK = 7 * DAY;
	public static final long MONTH = 30 * DAY;
	public static final long YEAR = 365 * DAY;

	public static String formatTime(Context context, String lstr) {
		long l;
		try {
			l = Long.parseLong(lstr);
		} catch (Exception e) {
			e.printStackTrace();
			l = new Date().getTime();
		}
		return formatTime(context, l);
	}

	public static String formatTimeWithCountDown(Context context, String lstr) {
		long l;
		try {
			l = Long.parseLong(lstr);
		} catch (Exception e) {
			e.printStackTrace();
			l = new Date().getTime();
		}
		return formatTimeWithCountDown(context, l);
	}

	public static String formatDate(Context context, long timeStamp) {
		// return new SimpleDateFormat("yyyy-MM-dd").format(new
		// Date(timeStamp));
		return formatDate(context, new Date(timeStamp));
	}

	public static String formatDate(Context context, Date date) {
		// return new SimpleDateFormat("yyyy-MM-dd").format(new
		// Date(timeStamp));
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	public static String formatTime(Context context, long timeStamp) {
		timeStamp *= 1000;
		long currentTime = System.currentTimeMillis();
		long diff = currentTime - timeStamp;
		if (diff < YEAR) {
			return new SimpleDateFormat("MM-dd HH:mm").format(new Date(
					timeStamp));
		} else {
			return new SimpleDateFormat("yyyy-MM-dd")
					.format(new Date(timeStamp));
		}
	}

	public static String formatTimeWithCountDown(Context context, long timeStamp) {
		timeStamp *= 1000;
		long currentTime = System.currentTimeMillis();
		long diff = currentTime - timeStamp;
		if (diff < MINUTE) {
			return context.getString(R.string.time_some_seconds_ago,
					(diff / SECOND));
		} else if (diff < HOUR) {
			return context.getString(R.string.time_some_minutes_ago,
					(diff / MINUTE));
		} else if (diff < DAY) {
			return context.getString(R.string.time_some_hours_ago,
					(diff / HOUR));
		} else if (diff < 2 * DAY) {
			return context.getString(R.string.time_yester_day);
		} else if (diff < 3 * DAY) {
			return context.getString(R.string.time_the_day_before_yester_day);
		} else if (diff < WEEK) {
			return context.getString(R.string.time_some_day_ago, (diff / DAY));
		} else if (diff < 2 * WEEK) {
			return context.getString(R.string.time_last_week);
		} else if (diff < MONTH) {
			return context
					.getString(R.string.time_some_week_ago, (diff / WEEK));
		} else if (diff < YEAR) {
			return new SimpleDateFormat("MM-dd HH:mm").format(new Date(
					timeStamp));
		} else {
			return new SimpleDateFormat("yyyy-MM-dd")
					.format(new Date(timeStamp));
		}
	}

	public static int compareDiffAsDay(long t1, long t2) {
		long diff = Math.abs(t1 - t2);
		return new Long(diff / DAY).intValue();
	}

}
