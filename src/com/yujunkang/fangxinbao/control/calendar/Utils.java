package com.yujunkang.fangxinbao.control.calendar;

import java.util.Calendar;



/**
 * 
 * @date 2014-7-28
 * @author xieb
 * 
 */
public class Utils {
	public static String LeftPad_Tow_Zero(int str) {
		java.text.DecimalFormat format = new java.text.DecimalFormat("00");
		return format.format(str);
	}
	
	/**
	 * @param mPageNumber 
	 * @param calendar2 
	 * @return 
	 */
	public static Calendar getSelectCalendar(int mPageNumber) {
		Calendar calendar = Calendar.getInstance();
		if (mPageNumber > Integer.MAX_VALUE/2) {
			for (int i = 0; i < mPageNumber - Integer.MAX_VALUE/2; i++) {
				calendar = setNextViewItem(calendar);
			}
		} else if (mPageNumber < Integer.MAX_VALUE/2) {
			for (int i = 0; i < Integer.MAX_VALUE/2 - mPageNumber; i++) {
				calendar = setPrevViewItem(calendar);
			}
		} else {
			
		}
		return calendar;
	}
	
	// 上一个月
		private static Calendar setPrevViewItem(Calendar calendar) {
			int iMonthViewCurrentMonth = calendar.get(Calendar.MONTH);
			int iMonthViewCurrentYear = calendar.get(Calendar.YEAR);
			iMonthViewCurrentMonth--;// 当前选择月--
			
			// 如果当前月为负数的话显示上一年
			if (iMonthViewCurrentMonth == -1) {
				iMonthViewCurrentMonth = 11;
				iMonthViewCurrentYear--;
			}
			calendar.set(Calendar.DAY_OF_MONTH, 1); // 设置日为当月1日
			calendar.set(Calendar.MONTH, iMonthViewCurrentMonth); // 设置月
			calendar.set(Calendar.YEAR, iMonthViewCurrentYear); // 设置年
			return calendar;
		}

		private static Calendar setNextViewItem(Calendar calendar) {
			int iMonthViewCurrentMonth = calendar.get(Calendar.MONTH);
			int iMonthViewCurrentYear = calendar.get(Calendar.YEAR);
			iMonthViewCurrentMonth++;
			if (iMonthViewCurrentMonth == 12) {
				iMonthViewCurrentMonth = 0;
				iMonthViewCurrentYear++;
			}
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, iMonthViewCurrentMonth);
			calendar.set(Calendar.YEAR, iMonthViewCurrentYear);
			return calendar;
		}

	
}


