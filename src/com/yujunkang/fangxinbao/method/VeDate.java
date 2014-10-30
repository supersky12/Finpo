package com.yujunkang.fangxinbao.method;

/**
 * 
 * @date 2014-5-24
 * @author xieb
 * 
 */
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;
import android.util.Log;

public class VeDate {

	/*
	 * 将122秒数转换成02:02的格式
	 */
	public static String convertSecondesToMinSecFormat(int seconds) {
		double min = seconds / 60;
		double sec = seconds % 60;
		DecimalFormat nf = new DecimalFormat("#00");
		return nf.format(min) + ":" + nf.format(sec);
	}

	/**
	 * 获取现在时间
	 * 
	 * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
	 */
	public static Date getNowDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		ParsePosition pos = new ParsePosition(8);
		Date currentTime_2 = formatter.parse(dateString, pos);
		return currentTime_2;
	}

	public static long getDataStamp(Date queryDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			String tempdate = formatter.format(queryDate);
			return formatter.parse(tempdate).getTime() / 1000;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			return new Date().getTime() / 1000;
		}
	}

	public static long getDataStamp(String queryDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			
			return formatter.parse(queryDate).getTime() / 1000;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			return new Date().getTime() / 1000;
		}
	}
	
	
	/**
	 * 根据指定的格式获取时间字符串
	 * 
	 * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
	 */
	public static String DateToStr(long date, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(date);
			return sdf.format(cal.getTime());
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 将Long时间转换成 "MM/dd 周几"
	 * 
	 * @param dayStr
	 * @return
	 */
	public static String getDateShow(long time) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(time);

			return sdf.format(cal.getTime());
		} catch (Exception ex) {
			return null;
		}
	}

	public static long getString2Long(String time) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return 0;
		}
		return date.getTime();

	}

	/**
	 * 获取时间的天
	 * 
	 * @param time
	 * @return
	 */
	public static int getDayOfDate(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(time);
			return getDayOfDate(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return 0;
		}

	}

	/**
	 * 获取时间的天
	 * 
	 * @param time
	 * @return
	 */
	public static int getDayOfDate(Date time) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(time);
			return c.get(Calendar.DATE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return 0;
		}

	}

	/**
	 * 获取时间的月
	 * 
	 * @param time
	 * @return
	 */
	public static int getMonthOfDate(String time) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(time);
			return getMonthOfDate(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return 0;
		}

	}

	/**
	 * 获取时间的月
	 * 
	 * @param time
	 * @return
	 */
	public static int getMonthOfDate(Date time) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(time);
			return c.get(Calendar.MONTH) + 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return 0;
		}

	}

	/**
	 * 获取时间的年
	 * 
	 * @param time
	 * @return
	 */
	public static int getYearOfDate(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(time);
			return getYearOfDate(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return 0;
		}

	}

	/**
	 * 获取时间的年
	 * 
	 * @param time
	 * @return
	 */
	public static int getYearOfDate(Date time) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(time);
			return c.get(Calendar.YEAR);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return 0;
		}

	}

	/**
	 * 获取一年天的间隔数
	 * 
	 * @param time
	 * @return
	 */
	public static int getYearOfTwoDates(Date time1, Date time2) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(time1);
			int day1 = c.get(Calendar.DAY_OF_YEAR);

			c.setTime(time2);
			int day2 = c.get(Calendar.DAY_OF_YEAR);
			return Math.abs(day1 - day2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return 0;
		}

	}

	
	/**
	 * 
	 * @param oldTime 较小的时间
	 * @param newTime 较大的时间 (如果为空   默认当前时间 ,表示和当前时间相比)
	 * @return -1 ：同一天.    0：昨天 .   1 ：至少是前天.
	 * @throws ParseException 转换异常
	 */
	private String isYeaterday(String oldTime) {
		try {
			if (!TextUtils.isEmpty(oldTime)) {
				// 换成yyyy-MM-dd 00：00：00
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date srcTime = format.parse(oldTime);
				String todayStr = format.format(new Date());
				Date today = format.parse(todayStr);
				// 昨天 86400000=24*60*60*1000 一天
				if ((today.getTime() - srcTime.getTime()) > 0
						&& (today.getTime() - srcTime.getTime()) <= 86400000) {
					return "昨天";
				} else if ((today.getTime() - srcTime.getTime()) <= 0) { // 至少是今天
					return "今天";
				} else { // 至少是前天
					format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					Date resultTime = format.parse(oldTime);
					format = new SimpleDateFormat("MM-dd HH:mm");
					return format.format(resultTime);
				}
			}
		} catch (Exception ex) {

		}
		return oldTime;
		
	}

	
	
	public static long getString2LongDate(String time) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return 0;
		}
		return date.getTime();

	}

	public static String getFormatSimpleDate(String time, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
		Pattern pattern = Pattern.compile("(\\d{4}).*?(\\d{2}).*?(\\d{2})");
		Matcher matcher = pattern.matcher(time);
		try {
			if (matcher.find()) {
				StringBuilder buff = new StringBuilder();
				buff.append(matcher.group(1));
				buff.append("-");
				buff.append(matcher.group(2));
				buff.append("-");
				buff.append(matcher.group(3));
				SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd",
						Locale.ENGLISH);
				_sdf.setLenient(false);
				Date date = _sdf.parse(buff.toString());
				return sdf.format(date);
			}
		} catch (ParseException e) {

		}
		return sdf.format(new Date(System.currentTimeMillis()));

	}

	/**
	 * 将Long时间转换成 "yyyy-MM-dd"
	 * 
	 * @param dayStr
	 * @return
	 */
	public static String getDateShowToShort(long time) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(time);
			return sdf.format(cal.getTime());
		} catch (Exception ex) {
			return null;
		}

	}

	/**
	 * 获取现在时间
	 * 
	 * @return返回短时间格式 yyyy-MM-dd
	 */
	public static Date getNowDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		ParsePosition pos = new ParsePosition(8);
		Date currentTime_2 = formatter.parse(dateString, pos);
		return currentTime_2;
	}

	/**
	 * 获取现在时间
	 * 
	 * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
	 */
	public static String getStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取现在时间
	 * 
	 * @return返回字符串格式 yyyy-MM-dd HH:mm:ss:SSS
	 */
	public static String getMeStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss:SSS");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取现在时间 MM-dd HH:mm
	 * 
	 * @return
	 */
	public static String getStringMeDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 得到指定格式当前时间字符
	 * 
	 * @return
	 */
	public static String getFormatStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取现在时间
	 * 
	 * @return 返回短时间字符串格式yyyy-MM-dd
	 */
	public static String getStringDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 得到两日期间的天数
	 * 
	 * @param time1
	 *            结束日期 yyy-mm-dd
	 * @param time2
	 *            开始日期 yyy-mm-dd
	 * @return
	 */
	public static long getDaysIntervalNum(String time1, String time2) {
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
			quot = quot / 1000 / 60 / 60 / 24;
		} catch (ParseException e) {
			//
		}
		return quot;
	}

	/**
	 * 按不同的统计口径来计算指定时间与当前时间的相差值
	 * 
	 * @param time1
	 *            yyyy-mm-dd hh:mm
	 * @param flag
	 *            0:统计精确到天,1:统计精确到小时,2:统计精确到分钟
	 * @return
	 */
	public static long getDaysIntervalNum(String time1, int flag) {
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date date1 = ft.parse(time1);
			Date date2 = new Date();
			quot = date1.getTime() - date2.getTime();
			if (flag == 0) {
				quot = quot / 1000 / 60 / 60 / 24;
			} else if (flag == 1) {
				quot = quot / 1000 / 60 / 60;
			} else if (flag == 2) {
				quot = quot / 1000 / 60;
			}
		} catch (ParseException e) {
			//
		}
		return quot;
	}

	/**
	 * 根据相差的天数得到相应订阅时间统计字串
	 * 
	 * @param time1
	 * @param daysNum
	 * @return
	 */
	public static String getAttentionTimeStr(String time1, long daysNum) {
		String result = "";
		if (daysNum > 2) {
			result = "起飞前2天以上";
		} else if (daysNum <= 2 && daysNum >= 1) {
			result = "起飞前1-2天";
		} else {
			// 当天的统计口径按小时
			long hourNum = getDaysIntervalNum(time1, 1);
			if (hourNum > 2) {
				result = "起飞前2小时以上";
			} else if (hourNum <= 2 && hourNum >= 0) {
				result = "起飞前2小时以内";
			} else {
				result = "起飞后";
			}
		}
		return result;
	}

	/**
	 * 把天数间隔转为提示文字
	 * 
	 * @param daysNum
	 * @return
	 */
	public static String getDaysIntervalStr(long daysNum) {
		String result = "";
		if (daysNum < 0) {
			result = "过期";
		} else if (daysNum == 0) {
			// 当天
			result = "今天";
		} else if (daysNum == 1) {
			// 明天
			result = "明天";
		} else if (daysNum == 2) {
			// 后天
			result = "后天";
		} else if (daysNum > 2 && daysNum <= 7) {
			// 一周内
			result = "一周内";
		} else if (daysNum > 7 && daysNum <= 30) {
			// 一月内
			result = "一月内";
		} else {
			// 一月以上
			result = "一月以上";
		}
		return result;

	}

	/**
	 * 获取明天时间
	 * 
	 * @return 返回短时间字符串格式yyyy-MM-dd
	 */
	public static String getTomorrowDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c1 = Calendar.getInstance();
		c1.setTime(currentTime);
		c1.add(Calendar.HOUR_OF_DAY, 24);
		String dateString = formatter.format(c1.getTime());
		return dateString;
	}

	/**
	 * 获取时间 小时:分;秒 HH:mm:ss
	 * 
	 * @return
	 */
	public static String getTimeShort() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取时间 小时:分;秒 HH:mm
	 * 
	 * @return
	 */
	public static String getTimeSuperShort() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date strToDateNormal(String strDate) {
		Date strtodate = new Date();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			ParsePosition pos = new ParsePosition(0);
			strtodate = formatter.parse(strDate, pos);
			return strtodate;
		} catch (Exception ex) {
			return strtodate;
		}
	}

	/**
	 * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date strToDateLong(String strDate) {
		Date strtodate = new Date();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			ParsePosition pos = new ParsePosition(0);
			strtodate = formatter.parse(strDate, pos);
		} catch (Exception ex) {

		}
		return strtodate;
	}

	/**
	 * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param dateDate
	 * @return
	 */
	public static String dateToStrLong(java.util.Date dateDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(dateDate);
		return dateString;
	}

	/**
	 * 将短时间格式时间转换为字符串 yyyy-MM-dd
	 * 
	 * @param dateDate
	 * @param k
	 * @return
	 */
	public static String dateToStr(java.util.Date dateDate) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String dateString = formatter.format(dateDate);
			return dateString;
		} catch (Exception ex) {
			return "";
		}
	}

	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date strToDate(String strDate) {
		Date strtodate = new Date();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			ParsePosition pos = new ParsePosition(0);
			strtodate = formatter.parse(strDate, pos);
		} catch (Exception ex) {
			return strtodate;
		}
		return strtodate;
	}

	/**
	 * 得到现在时间
	 * 
	 * @return
	 */
	public static Date getNow() {
		Date currentTime = new Date();
		return currentTime;
	}

	/**
	 * 提取一个月中的最后一天
	 * 
	 * @param day
	 * @return
	 */
	public static Date getLastDate(long day) {
		Date date = new Date();
		long date_3_hm = date.getTime() - 3600000 * 34 * day;
		Date date_3_hm_date = new Date(date_3_hm);
		return date_3_hm_date;
	}

	/**
	 * 得到现在时间
	 * 
	 * @return 字符串 yyyyMMdd HHmmss
	 */
	public static String getStringToday() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 得到现在小时
	 */
	public static String getHour() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		String hour;
		hour = dateString.substring(11, 13);
		return hour;
	}

	/**
	 * 得到现在分钟
	 * 
	 * @return
	 */
	public static String getTime() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		String min;
		min = dateString.substring(14, 16);
		return min;
	}

	/**
	 * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMdd，注意字母y不能大写。
	 * 
	 * @param sformat
	 *            yyyyMMddhhmmss
	 * @return
	 */
	public static String getUserDate(String sformat) {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(sformat);
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 二个小时时间间的差值,必须保证二个时间都是"HH:MM"的格式，返回字符型的分钟
	 */
	public static String getTwoHour(String st1, String st2) {
		String[] kk = null;
		String[] jj = null;
		kk = st1.split(":");
		jj = st2.split(":");
		if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0]))
			return "0";
		else {
			double y = Double.parseDouble(kk[0]) + Double.parseDouble(kk[1])
					/ 60;
			double u = Double.parseDouble(jj[0]) + Double.parseDouble(jj[1])
					/ 60;
			if ((y - u) > 0)
				return y - u + "";
			else
				return "0";
		}
	}

	/**
	 * 得到日期与今天的间隔天数
	 */
	public static int getIntervalDays(String startDate) {
		try {
			long startDateLong = strToDate(startDate).getTime();
			long nowDateLong = new Date().getTime();
			return (int) ((startDateLong - nowDateLong) / (24 * 60 * 60 * 1000));
		} catch (Exception ex) {
			return 0;
		}

	}

	/**
	 * 得到二个日期间的间隔秒数
	 */
	public static String getIntervalSeconds(Date startDate, Date endDate) {
		int time = 0;
		try {
			long interval = endDate.getTime() - startDate.getTime();
			time = (int) (interval / 1000);
		} catch (Exception e) {
			return "0";
		}
		return String.valueOf(time);
	}

	/**
	 * 得到二个日期间的间隔天数
	 */
	public static String getTwoDay(Date myDate, String sj2) {
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		int myYear = myDate.getYear() + 1900;
		int myMonth = myDate.getMonth() + 1;
		int myDay = myDate.getDate();
		int myHour = myDate.getHours();
		int myMinute = myDate.getMinutes();
		int mySecond = myDate.getSeconds();
		long day = 0;
		String myDateStr = String.valueOf(myYear) + "-"
				+ String.valueOf(myMonth) + "-" + String.valueOf(myDay) + " "
				+ String.valueOf(myHour) + ":" + String.valueOf(myMinute) + ":"
				+ String.valueOf(mySecond);
		try {
			java.util.Date date = myFormatter.parse(myDateStr);
			java.util.Date mydate = myFormatter.parse(sj2);
			day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
			return "0";
		}
		return day + "";
	}

	/**
	 * 时间前推或后推分钟,其中JJ表示分钟.
	 */
	public static String getPreTime(String sj1, String jj) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mydate1 = "";
		try {
			Date date1 = format.parse(sj1);
			long Time = (date1.getTime() / 1000) + Integer.parseInt(jj) * 60;
			date1.setTime(Time * 1000);
			mydate1 = format.format(date1);
		} catch (Exception e) {
		}
		return mydate1;
	}

	/**
	 * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
	 */
	public static String getNextDay(String nowdate, String delay) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String mdate = "";
			Date d = strToDate(nowdate);
			long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24
					* 60 * 60;
			d.setTime(myTime * 1000);
			mdate = format.format(d);
			return mdate;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 判断是否润年
	 * 
	 * @param ddate
	 * @return
	 */
	public static boolean isLeapYear(String ddate) {

		/**
		 * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
		 * 3.能被4整除同时能被100整除则不是闰年
		 */
		Date d = strToDate(ddate);
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(d);
		int year = gc.get(Calendar.YEAR);
		if ((year % 400) == 0)
			return true;
		else if ((year % 4) == 0) {
			if ((year % 100) == 0)
				return false;
			else
				return true;
		} else
			return false;
	}

	/**
	 * 返回美国时间格式 26 Apr 2006
	 * 
	 * @param str
	 * @return
	 */
	public static String getEDate(String str) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(str, pos);
		String j = strtodate.toString();
		String[] k = j.split(" ");
		return k[2] + k[1].toUpperCase() + k[5].substring(2, 4);
	}

	/**
	 * 获取一个月的最后一天
	 * 
	 * @param dat
	 * @return
	 */
	public static String getEndDateOfMonth(String dat) {// yyyy-MM-dd
		String str = dat.substring(0, 8);
		String month = dat.substring(5, 7);
		int mon = Integer.parseInt(month);
		if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8
				|| mon == 10 || mon == 12) {
			str += "31";
		} else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
			str += "30";
		} else {
			if (isLeapYear(dat)) {
				str += "29";
			} else {
				str += "28";
			}
		}
		return str;
	}

	/**
	 * 判断二个时间是否在同一个周
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameWeekDates(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		if (0 == subYear) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
					.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
			// 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
					.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
					.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		return false;
	}

	/**
	 * 产生周序列,即得到当前时间所在的年度是第几周
	 * 
	 * @return
	 */
	public static String getSeqWeek() {
		try {
			Calendar c = Calendar.getInstance(Locale.CHINA);
			String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
			if (week.length() == 1)
				week = "0" + week;
			String year = Integer.toString(c.get(Calendar.YEAR));
			return year + week;
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 将 年、月、日 转化成带星期的日期字符串，如：05月09日
	 */
	public static String getDateString(String date) {
		SimpleDateFormat MM_dd = new SimpleDateFormat("MM月dd日");
		try {
			return MM_dd.format(strToDate(date));
		} catch (Exception ex) {
			return "";
		}

	}

	/**
	 * 获得一个日期所在的周的星期几的日期，如要找出2002年2月3日所在周的星期一是几号
	 * 
	 * @param sdate
	 * @param num
	 * @return
	 */
	public static String getWeek(String sdate, String num) {
		// 再转换为时间
		Date dd = VeDate.strToDate(sdate);
		Calendar c = Calendar.getInstance();
		c.setTime(dd);
		if (num.equals("1")) // 返回星期一所在的日期
			c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		else if (num.equals("2")) // 返回星期二所在的日期
			c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		else if (num.equals("3")) // 返回星期三所在的日期
			c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		else if (num.equals("4")) // 返回星期四所在的日期
			c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		else if (num.equals("5")) // 返回星期五所在的日期
			c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		else if (num.equals("6")) // 返回星期六所在的日期
			c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		else if (num.equals("0")) // 返回星期日所在的日期
			c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
	}

	/**
	 * 根据一个日期，返回是星期几的字符串
	 * 
	 * @param sdate
	 * @return
	 */
	public static String getWeek(Date sdate) {
		// 再转换为时间
		Calendar c = Calendar.getInstance();
		c.setTime(sdate);
		return new SimpleDateFormat("EEEE").format(c.getTime());
	}

	/**
	 * 根据一个日期，返回是星期几的字符串
	 * 
	 * @param sdate
	 * @return
	 */
	public static String getWeek(String sdate) {
		// 再转换为时间
		Date date = VeDate.strToDate(sdate);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// int hour=c.get(Calendar.DAY_OF_WEEK);
		// hour中存的就是星期几了，其范围 1~7
		// 1=星期日 7=星期六，其他类推
		return new SimpleDateFormat("EEEE").format(c.getTime());
	}

	public static String getWeekStr(String sdate) {
		String str = "";
		str = VeDate.getWeek(sdate);
		if ("1".equals(str)) {
			str = "星期日";
		} else if ("2".equals(str)) {
			str = "星期一";
		} else if ("3".equals(str)) {
			str = "星期二";
		} else if ("4".equals(str)) {
			str = "星期三";
		} else if ("5".equals(str)) {
			str = "星期四";
		} else if ("6".equals(str)) {
			str = "星期五";
		} else if ("7".equals(str)) {
			str = "星期六";
		}
		return str;
	}

	/**
	 * 两个时间之间的天数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long getDays(String date1, String date2) {
		if (date1 == null || date1.equals(""))
			return 0;
		if (date2 == null || date2.equals(""))
			return 0;
		long day = 0;
		// 转换为标准时间
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = null;
		java.util.Date mydate = null;
		try {
			date = myFormatter.parse(date1);
			mydate = myFormatter.parse(date2);
			day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
			//
		}
		return day;
	}

	/**
	 * 得到两时间之分钟差
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long getMinutes(String date1, String date2) {
		if (date1 == null || date1.equals(""))
			return 0;
		if (date2 == null || date2.equals(""))
			return 0;
		// 转换为标准时间
		SimpleDateFormat myFormatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		java.util.Date date = null;
		java.util.Date mydate = null;
		long day = 0;
		try {
			date = myFormatter.parse(date1);
			mydate = myFormatter.parse(date2);
			day = (date.getTime() - mydate.getTime()) / (60 * 1000);
		} catch (Exception e) {
			//
		}
		return day;
	}

	/**
	 * 形成如下的日历 ， 根据传入的一个时间返回一个结构 星期日 星期一 星期二 星期三 星期四 星期五 星期六 下面是当月的各个时间
	 * 此函数返回该日历第一行星期日所在的日期
	 * 
	 * @param sdate
	 * @return
	 */
	public static String getNowMonth(String sdate) {
		// 取该时间所在月的一号
		sdate = sdate.substring(0, 8) + "01";

		// 得到这个月的1号是星期几
		Date date = VeDate.strToDate(sdate);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int u = c.get(Calendar.DAY_OF_WEEK);
		String newday = VeDate.getNextDay(sdate, (1 - u) + "");
		return newday;
	}

	/**
	 * 取得数据库主键 生成格式为yyyymmddhhmmss+k位随机数
	 * 
	 * @param k
	 *            表示是取几位随机数，可以自己定
	 */

	public static String getNo(int k) {

		return getUserDate("yyyyMMddhhmmss") + getRandom(k);
	}

	/**
	 * 按指定格式处理dateStr
	 * 
	 * @param stamp
	 * 
	 * @return
	 */
	public static String getDateStr(String dateStr, String srcFormat,
			String dstFormat) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(srcFormat);
			Date date = sdf.parse(dateStr);
			sdf = new SimpleDateFormat(dstFormat);
			return sdf.format(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block

			return dateStr;
		}
	}

	/**
	 * 返回一个随机数
	 * 
	 * @param i
	 * @return
	 */
	public static String getRandom(int i) {
		Random jjj = new Random();
		// int suiJiShu = jjj.nextInt(9);
		if (i == 0)
			return "";
		String jj = "";
		for (int k = 0; k < i; k++) {
			jj = jj + jjj.nextInt(9);
		}
		return jj;
	}

	/**
	 * 
	 * @param args
	 */
	public static boolean RightDate(String date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		;
		if (date == null)
			return false;
		if (date.length() > 10) {
			sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		} else {
			sdf = new SimpleDateFormat("yyyy-MM-dd");
		}
		try {
			sdf.parse(date);
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}

	/**
	 * 得到当前时间指定天数后的时间
	 * 
	 * @return yyyy-mm-dd hh:mm
	 */
	public static String getAfterDay(int days) {
		Calendar c1 = Calendar.getInstance();
		Date currentTime = new Date();
		c1.setTime(currentTime);
		c1.add(Calendar.DATE, days);
		SimpleDateFormat yyyy_MM_dd_HH_mm = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm");
		String dateString = yyyy_MM_dd_HH_mm.format(c1.getTime());
		return dateString;
	}

	/**
	 * 得到法定假日
	 * 
	 * @param myYear
	 * @param myMonth
	 * @param myDay
	 * @return
	 */
	public static String getHoliday(int myYear, int myMonth, int myDay) {
		String result = "";
		result = LunarCalendar.getHoliday(myMonth, myDay);
		// 判断是否有法定假日
		/*
		 * if (result.equals("")) { // 为空则再判断是否有二十四节气 result =
		 * LunarCalendar.getSoralTerm(myYear, myMonth, myDay); if
		 * (result.equals("")) { // 为空则得到其日期农历 result =
		 * LunarCalendar.GetDateNL(myYear, myMonth, myDay); result =
		 * result.substring(result.indexOf("月") + 1); } }
		 */
		return result;
	}

	/**
	 * 得到农历
	 * 
	 * @param myYear
	 * @param myMonth
	 * @param myDay
	 * @return
	 */
	public static String getNLInfo(int myYear, int myMonth, int myDay) {
		String result = "农历";
		result += LunarCalendar.GetDateNL(myYear, myMonth, myDay);
		return result;
	}

	/**
	 * 在日期上增加数个整月
	 * 
	 * @param date
	 *            日期
	 * @param n
	 *            月数
	 * @return
	 */
	public static Date addMonth(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, n);
		return cal.getTime();
	}

	/**
	 * 在日期上增加数个整日
	 * 
	 * @param date
	 *            日期
	 * @param n
	 *            天数
	 * @return
	 */
	public static Date addDay(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, n);
		return cal.getTime();
	}

	/**
	 * 对日期(时间)中的日进行加减计算. <br>
	 * 例子: <br>
	 * 如果Date类型的d为 2005年8月20日,那么 <br>
	 * calculateByDate(d,-10)的值为2005年8月10日 <br>
	 * 而calculateByDate(d,+10)的值为2005年8月30日 <br>
	 * 
	 * @param d
	 *            日期(时间).
	 * @param amount
	 *            加减计算的幅度.+n=加n天;-n=减n天.
	 * @return 计算后的日期(时间).
	 */
	public static Date calculateByDate(Date d, int amount) {
		return calculate(d, GregorianCalendar.DATE, amount);
	}

	public static Date calculateByMinute(Date d, int amount) {
		return calculate(d, GregorianCalendar.MINUTE, amount);
	}

	public static Date calculateBySecond(Date d, int amount) {
		return calculate(d, GregorianCalendar.SECOND, amount);
	}

	public static Date calculateByYear(Date d, int amount) {
		return calculate(d, GregorianCalendar.YEAR, amount);
	}

	/**
	 * 得到两个日期相差的天数
	 */
	public static int getBetweenDay(Date date1, Date date2) {
		Calendar d1 = new GregorianCalendar();
		d1.setTime(date1);
		Calendar d2 = new GregorianCalendar();
		d2.setTime(date2);
		int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
		int y2 = d2.get(Calendar.YEAR);
		if (d1.get(Calendar.YEAR) != y2) {
			do {
				days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);
				d1.add(Calendar.YEAR, 1);
			} while (d1.get(Calendar.YEAR) != y2);
		}
		return days;
	}

	/**
	 * 对日期(时间)中由field参数指定的日期成员进行加减计算. <br>
	 * 例子: <br>
	 * 如果Date类型的d为 2005年8月20日,那么 <br>
	 * calculate(d,GregorianCalendar.YEAR,-10)的值为1995年8月20日 <br>
	 * 而calculate(d,GregorianCalendar.YEAR,+10)的值为2015年8月20日 <br>
	 * 
	 * @param d
	 *            日期(时间).
	 * @param field
	 *            日期成员. <br>
	 *            日期成员主要有: <br>
	 *            年:GregorianCalendar.YEAR <br>
	 *            月:GregorianCalendar.MONTH <br>
	 *            日:GregorianCalendar.DATE <br>
	 *            时:GregorianCalendar.HOUR <br>
	 *            分:GregorianCalendar.MINUTE <br>
	 *            秒:GregorianCalendar.SECOND <br>
	 *            毫秒:GregorianCalendar.MILLISECOND <br>
	 * @param amount
	 *            加减计算的幅度.+n=加n个由参数field指定的日期成员值;-n=减n个由参数field代表的日期成员值.
	 * @return 计算后的日期(时间).
	 */
	private static Date calculate(Date d, int field, int amount) {
		if (d == null)
			return null;
		GregorianCalendar g = new GregorianCalendar();
		g.setTime(d);
		g.add(field, amount);
		return g.getTime();
	}

	/***************************************************************************
	 * //nd=1表示返回的值中包含年度 //yf=1表示返回的值中包含月份 //rq=1表示返回的值中包含日期 //format表示返回的格式 1
	 * 以年月日中文返回 2 以横线-返回 // 3 以斜线/返回 4 以缩写不带其它符号形式返回 // 5 以点号.返回
	 **************************************************************************/
	/*
	 * public static String getStringDateMonth(String sdate, String nd, String
	 * yf, String rq, String format) { Date currentTime = new Date();
	 * SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); String
	 * dateString = formatter.format(currentTime); String s_nd =
	 * dateString.substring(0, 4); // 年份 String s_yf = dateString.substring(5,
	 * 7); // 月份 String s_rq = dateString.substring(8, 10); // 日期 String sreturn
	 * = ""; roc.util.MyChar mc = new roc.util.MyChar(); if (sdate == null ||
	 * sdate.equals("") || !mc.Isdate(sdate)) { // 处理空值情况 if (nd.equals("1")) {
	 * sreturn = s_nd; // 处理间隔符 if (format.equals("1")) sreturn = sreturn + "年";
	 * else if (format.equals("2")) sreturn = sreturn + "-"; else if
	 * (format.equals("3")) sreturn = sreturn + "/"; else if
	 * (format.equals("5")) sreturn = sreturn + "."; } // 处理月份 if
	 * (yf.equals("1")) { sreturn = sreturn + s_yf; if (format.equals("1"))
	 * sreturn = sreturn + "月"; else if (format.equals("2")) sreturn = sreturn +
	 * "-"; else if (format.equals("3")) sreturn = sreturn + "/"; else if
	 * (format.equals("5")) sreturn = sreturn + "."; } // 处理日期 if
	 * (rq.equals("1")) { sreturn = sreturn + s_rq; if (format.equals("1"))
	 * sreturn = sreturn + "日"; } } else { // 不是空值，也是一个合法的日期值，则先将其转换为标准的时间格式
	 * sdate = roc.util.RocDate.getOKDate(sdate); s_nd = sdate.substring(0, 4);
	 * // 年份 s_yf = sdate.substring(5, 7); // 月份 s_rq = sdate.substring(8, 10);
	 * // 日期 if (nd.equals("1")) { sreturn = s_nd; // 处理间隔符 if
	 * (format.equals("1")) sreturn = sreturn + "年"; else if
	 * (format.equals("2")) sreturn = sreturn + "-"; else if
	 * (format.equals("3")) sreturn = sreturn + "/"; else if
	 * (format.equals("5")) sreturn = sreturn + "."; } // 处理月份 if
	 * (yf.equals("1")) { sreturn = sreturn + s_yf; if (format.equals("1"))
	 * sreturn = sreturn + "月"; else if (format.equals("2")) sreturn = sreturn +
	 * "-"; else if (format.equals("3")) sreturn = sreturn + "/"; else if
	 * (format.equals("5")) sreturn = sreturn + "."; } // 处理日期 if
	 * (rq.equals("1")) { sreturn = sreturn + s_rq; if (format.equals("1"))
	 * sreturn = sreturn + "日"; } } return sreturn; }
	 */

	/*
	 * public static String getNextMonthDay(String sdate, int m) { sdate =
	 * getOKDate(sdate); int year = Integer.parseInt(sdate.substring(0, 4)); int
	 * month = Integer.parseInt(sdate.substring(5, 7)); month = month + m; if
	 * (month < 0) { month = month + 12; year = year - 1; } else if (month > 12)
	 * { month = month - 12; year = year + 1; } String smonth = ""; if (month <
	 * 10) smonth = "0" + month; else smonth = "" + month; return year + "-" +
	 * smonth + "-10"; }
	 */

	/*
	 * public static String getOKDate(String sdate) { if (sdate == null ||
	 * sdate.equals("")) return getStringDateShort();
	 * 
	 * if (!VeStr.Isdate(sdate)) { sdate = getStringDateShort(); } // 将“/”转换为“-”
	 * sdate = VeStr.Replace(sdate, "/", "-"); // 如果只有8位长度，则要进行转换 if
	 * (sdate.length() == 8) sdate = sdate.substring(0, 4) + "-" +
	 * sdate.substring(4, 6) + "-" + sdate.substring(6, 8); SimpleDateFormat
	 * formatter = new SimpleDateFormat("yyyy-MM-dd"); ParsePosition pos = new
	 * ParsePosition(0); Date strtodate = formatter.parse(sdate, pos); String
	 * dateString = formatter.format(strtodate); return dateString; }
	 */
}
