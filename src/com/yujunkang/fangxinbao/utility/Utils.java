package com.yujunkang.fangxinbao.utility;

import java.io.Closeable;
import java.io.InterruptedIOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;

/**
 * 
 * @date 2014-6-5
 * @author xieb
 * 
 */
public class Utils {
	private static final String TAG = "Utils";
	private static final long POLY64REV = 0x95AC9329AC4BC9B5L;
	private static final long INITIALCRC = 0xFFFFFFFFFFFFFFFFL;

	private static long[] sCrcTable = new long[256];

	private static final boolean IS_DEBUG_BUILD = Build.TYPE.equals("eng")
			|| Build.TYPE.equals("userdebug");

	private static final String MASK_STRING = "********************************";

	// Throws AssertionError if the input is false.
	public static void assertTrue(boolean cond) {
		if (!cond) {
			throw new AssertionError();
		}
	}

	// Throws AssertionError if the input is false.
	public static void assertTrue(boolean cond, String message, Object... args) {
		if (!cond) {
			throw new AssertionError(args.length == 0 ? message
					: String.format(message, args));
		}
	}

	// Throws NullPointerException if the input is null.
	public static <T> T checkNotNull(T object) {
		if (object == null)
			throw new NullPointerException();
		return object;
	}

	// Returns true if two input Object are both null or equal
	// to each other.
	public static boolean equals(Object a, Object b) {
		return (a == b) || (a == null ? false : a.equals(b));
	}

	// Returns true if the input is power of 2.
	// Throws IllegalArgumentException if the input is <= 0.
	public static boolean isPowerOf2(int n) {
		if (n <= 0)
			throw new IllegalArgumentException();
		return (n & -n) == n;
	}

	// Returns the next power of two.
	// Returns the input if it is already power of 2.
	// Throws IllegalArgumentException if the input is <= 0 or
	// the answer overflows.
	public static int nextPowerOf2(int n) {
		if (n <= 0 || n > (1 << 30))
			throw new IllegalArgumentException();
		n -= 1;
		n |= n >> 16;
		n |= n >> 8;
		n |= n >> 4;
		n |= n >> 2;
		n |= n >> 1;
		return n + 1;
	}

	// Returns the previous power of two.
	// Returns the input if it is already power of 2.
	// Throws IllegalArgumentException if the input is <= 0
	public static int prevPowerOf2(int n) {
		if (n <= 0)
			throw new IllegalArgumentException();
		return Integer.highestOneBit(n);
	}

	// Returns the euclidean distance between (x, y) and (sx, sy).
	public static float distance(float x, float y, float sx, float sy) {
		float dx = x - sx;
		float dy = y - sy;
		return (float) Math.hypot(dx, dy);
	}

	// Returns the input value x clamped to the range [min, max].
	public static int clamp(int x, int min, int max) {
		if (x > max)
			return max;
		if (x < min)
			return min;
		return x;
	}

	// Returns the input value x clamped to the range [min, max].
	public static float clamp(float x, float min, float max) {
		if (x > max)
			return max;
		if (x < min)
			return min;
		return x;
	}

	// Returns the input value x clamped to the range [min, max].
	public static long clamp(long x, long min, long max) {
		if (x > max)
			return max;
		if (x < min)
			return min;
		return x;
	}

	public static boolean isOpaque(int color) {
		return color >>> 24 == 0xFF;
	}

	public static <T> void swap(T[] array, int i, int j) {
		T temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	public static void swap(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	/**
	 * A function thats returns a 64-bit crc for string
	 * 
	 * @param in
	 *            input string
	 * @return a 64-bit crc value
	 */
	public static final long crc64Long(String in) {
		if (in == null || in.length() == 0) {
			return 0;
		}
		return crc64Long(getBytes(in));
	}

	static {
		// http://bioinf.cs.ucl.ac.uk/downloads/crc64/crc64.c
		long part;
		for (int i = 0; i < 256; i++) {
			part = i;
			for (int j = 0; j < 8; j++) {
				long x = ((int) part & 1) != 0 ? POLY64REV : 0;
				part = (part >> 1) ^ x;
			}
			sCrcTable[i] = part;
		}
	}

	public static final long crc64Long(byte[] buffer) {
		long crc = INITIALCRC;
		for (int k = 0, n = buffer.length; k < n; ++k) {
			crc = sCrcTable[(((int) crc) ^ buffer[k]) & 0xff] ^ (crc >> 8);
		}
		return crc;
	}

	public static byte[] getBytes(String in) {
		byte[] result = new byte[in.length() * 2];
		int output = 0;
		for (char ch : in.toCharArray()) {
			result[output++] = (byte) (ch & 0xFF);
			result[output++] = (byte) (ch >> 8);
		}
		return result;
	}

	public static void closeSilently(Closeable c) {
		if (c == null)
			return;
		try {
			c.close();
		} catch (Throwable t) {
			LoggerTool.w(TAG, "close fail", t);
		}
	}

	public static int compare(long a, long b) {
		return a < b ? -1 : a == b ? 0 : 1;
	}

	public static int ceilLog2(float value) {
		int i;
		for (i = 0; i < 31; i++) {
			if ((1 << i) >= value)
				break;
		}
		return i;
	}

	public static int floorLog2(float value) {
		int i;
		for (i = 0; i < 31; i++) {
			if ((1 << i) > value)
				break;
		}
		return i - 1;
	}

	public static void closeSilently(ParcelFileDescriptor fd) {
		try {
			if (fd != null)
				fd.close();
		} catch (Throwable t) {
			LoggerTool.w(TAG, "fail to close", t);
		}
	}

	public static void closeSilently(Cursor cursor) {
		try {
			if (cursor != null)
				cursor.close();
		} catch (Throwable t) {
			LoggerTool.w(TAG, "fail to close", t);
		}
	}

	public static float interpolateAngle(float source, float target,
			float progress) {
		// interpolate the angle from source to target
		// We make the difference in the range of [-179, 180], this is the
		// shortest path to change source to target.
		float diff = target - source;
		if (diff < 0)
			diff += 360f;
		if (diff > 180)
			diff -= 360f;

		float result = source + diff * progress;
		return result < 0 ? result + 360f : result;
	}

	public static float interpolateScale(float source, float target,
			float progress) {
		return source + progress * (target - source);
	}

	public static String ensureNotNull(String value) {
		return value == null ? "" : value;
	}

	// Used for debugging. Should be removed before submitting.
	public static void debug(String format, Object... args) {
		if (args.length == 0) {
			LoggerTool.d(TAG, format);
		} else {
			LoggerTool.d(TAG, String.format(format, args));
		}
	}

	public static boolean isNullOrEmpty(String exifMake) {
		return TextUtils.isEmpty(exifMake);
	}

	public static boolean hasSpaceForSize(long size) {
		String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state)) {
			return false;
		}

		String path = Environment.getExternalStorageDirectory().getPath();
		try {
			StatFs stat = new StatFs(path);
			return stat.getAvailableBlocks() * (long) stat.getBlockSize() > size;
		} catch (Exception e) {
			LoggerTool.i(TAG, "Fail to access external storage", e);
		}
		return false;
	}

	public static void waitWithoutInterrupt(Object object) {
		try {
			object.wait();
		} catch (InterruptedException e) {
			LoggerTool.w(TAG, "unexpected interrupt: " + object);
		}
	}

	public static void shuffle(int array[], Random random) {
		for (int i = array.length; i > 0; --i) {
			int t = random.nextInt(i);
			if (t == i - 1)
				continue;
			int tmp = array[i - 1];
			array[i - 1] = array[t];
			array[t] = tmp;
		}
	}

	public static boolean handleInterrruptedException(Throwable e) {
		// A helper to deal with the interrupt exception
		// If an interrupt detected, we will setup the bit again.
		if (e instanceof InterruptedIOException
				|| e instanceof InterruptedException) {
			Thread.currentThread().interrupt();
			return true;
		}
		return false;
	}

	/**
	 * @return String with special XML characters escaped.
	 */
	public static String escapeXml(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, len = s.length(); i < len; ++i) {
			char c = s.charAt(i);
			switch (c) {
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '\"':
				sb.append("&quot;");
				break;
			case '\'':
				sb.append("&#039;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String getUserAgent(Context context) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			throw new IllegalStateException("getPackageInfo failed");
		}
		return String.format("%s/%s; %s/%s/%s/%s; %s/%s/%s",
				packageInfo.packageName, packageInfo.versionName, Build.BRAND,
				Build.DEVICE, Build.MODEL, Build.ID, Build.VERSION.SDK,
				Build.VERSION.RELEASE, Build.VERSION.INCREMENTAL);
	}

	public static String[] copyOf(String[] source, int newSize) {
		String[] result = new String[newSize];
		newSize = Math.min(source.length, newSize);
		System.arraycopy(source, 0, result, 0, newSize);
		return result;
	}

	public static PendingIntent deserializePendingIntent(byte[] rawPendingIntent) {
		Parcel parcel = null;
		try {
			if (rawPendingIntent != null) {
				parcel = Parcel.obtain();
				parcel.unmarshall(rawPendingIntent, 0, rawPendingIntent.length);
				return PendingIntent.readPendingIntentOrNullFromParcel(parcel);
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("error parsing PendingIntent");
		} finally {
			if (parcel != null)
				parcel.recycle();
		}
	}

	public static byte[] serializePendingIntent(PendingIntent pendingIntent) {
		Parcel parcel = null;
		try {
			parcel = Parcel.obtain();
			PendingIntent.writePendingIntentOrNullToParcel(pendingIntent,
					parcel);
			return parcel.marshall();
		} finally {
			if (parcel != null)
				parcel.recycle();
		}
	}

	// Mask information for debugging only. It returns
	// <code>info.toString()</code> directly
	// for debugging build (i.e., 'eng' and 'userdebug') and returns a mask
	// ("****")
	// in release build to protect the information (e.g. for privacy issue).
	public static String maskDebugInfo(Object info) {
		if (info == null)
			return null;
		String s = info.toString();
		int length = Math.min(s.length(), MASK_STRING.length());
		return IS_DEBUG_BUILD ? s : MASK_STRING.substring(0, length);
	}

	/**
	 * 手机号验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isMobile(String str) {

		Pattern p = Build.VERSION.SDK_INT >= 8 ? Patterns.PHONE : Pattern
				.compile("(\\+[0-9]+[\\- \\.]*)?" + "(\\([0-9]+\\)[\\- \\.]*)?"
						+ "([0-9][0-9\\- \\.][0-9\\- \\.]+[0-9])");
		Matcher m = null;
		boolean b = false;

		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * 电话号码验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isPhone(String str) {
		Pattern p1 = null, p2 = null;
		Matcher m = null;
		boolean b = false;
		p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$"); // 验证带区号的
		p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$"); // 验证没有区号的
		if (str.length() > 9) {
			m = p1.matcher(str);
			b = m.matches();
		} else {
			m = p2.matcher(str);
			b = m.matches();
		}
		return b;
	}

	public static boolean isEmail(String str) {
		Pattern emailPattern = Pattern
				.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
						+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
						+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
		return emailPattern.matcher(str).matches();
	}

	/**
	 * 获取运营商
	 * 
	 * @param context
	 * @return
	 */
	public static String getCountryCode(Context context) {
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		int phoneType = telManager.getPhoneType();
		if (phoneType != TelephonyManager.PHONE_TYPE_CDMA) {

			try {
				return telManager.getNetworkCountryIso();

			} catch (Exception ex) {
				return "其他";
			}

		}
		return "";
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static float computePaintTextHeight(Paint p) {
		FontMetrics indexfm = p.getFontMetrics();
		return Math.abs(indexfm.bottom - indexfm.top);
	}

	/**
	 * 取出字符串后面的小数点和零 如123.0变成123
	 * 
	 * @param s
	 * @return
	 */
	public static String subZeroAndDot(String s) {
		try {
			if (s.indexOf(".") > 0) {
				s = s.replaceAll("0+?$", "");// 去掉多余的0
				s = s.replaceAll("[.]$", "");// 如最后一位是.则去掉
			}
		} catch (Exception e) {
			Log.e(TAG, "", e);
			return s;
		}
		return s;
	}

	/**
	 * 温度摄氏度转换成华氏
	 * 
	 * @param s
	 * @return
	 */
	public static double temperatureCToF(double temperature) {
		try {
			return temperature * 9 / 5f + 32;
		} catch (Exception e) {
			Log.e(TAG, "", e);

		}
		return -1;
	}
	
	
	public static int getDimensionInDip(Context context, int dip) {
		int dimension = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources()
						.getDisplayMetrics());
		return dimension;
	}
}
