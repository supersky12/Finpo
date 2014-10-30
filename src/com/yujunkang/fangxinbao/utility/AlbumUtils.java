package com.yujunkang.fangxinbao.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.ImageBucket;
import com.yujunkang.fangxinbao.model.ImageItem;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.ConditionVariable;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * 
 * @date 2014-6-5
 * @author xieb
 * 
 */
public class AlbumUtils {
	private static final String TAG = "AlbumUtils";
	private static final String MAPS_PACKAGE_NAME = "com.google.android.apps.maps";
	private static final String MAPS_CLASS_NAME = "com.google.android.maps.MapsActivity";

	private static final String MIME_TYPE_IMAGE = "image/*";
	private static final String MIME_TYPE_VIDEO = "video/*";
	private static final String MIME_TYPE_ALL = "*/*";
	private static final String DIR_TYPE_IMAGE = "vnd.android.cursor.dir/image";
	private static final String DIR_TYPE_VIDEO = "vnd.android.cursor.dir/video";

	private static final String PREFIX_PHOTO_EDITOR_UPDATE = "editor-update-";
	private static final String PREFIX_HAS_PHOTO_EDITOR = "has-editor-";

	private static final String KEY_CAMERA_UPDATE = "camera-update";
	private static final String KEY_HAS_CAMERA = "has-camera";

	private static Context sContext;

	static float sPixelDensity = -1f;

	public static void initialize(Context context) {
		sContext = context;
		if (sPixelDensity < 0) {
			DisplayMetrics metrics = new DisplayMetrics();
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			wm.getDefaultDisplay().getMetrics(metrics);
			sPixelDensity = metrics.density;
		}
	}

	public static float dpToPixel(float dp) {
		return sPixelDensity * dp;
	}

	public static int dpToPixel(int dp) {
		return Math.round(dpToPixel((float) dp));
	}

	public static int meterToPixel(float meter) {
		// 1 meter = 39.37 inches, 1 inch = 160 dp.
		return Math.round(dpToPixel(meter * 39.37f * 160));
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

	// Below are used the detect using database in the render thread. It only
	// works most of the time, but that's ok because it's for debugging only.

	private static volatile Thread sCurrentThread;
	private static volatile boolean sWarned;

	public static void setRenderThread() {
		sCurrentThread = Thread.currentThread();
	}

	public static void assertNotInRenderThread() {
		if (!sWarned) {
			if (Thread.currentThread() == sCurrentThread) {
				sWarned = true;
				Log.w(TAG, new Throwable("Should not do this in render thread"));
			}
		}
	}

	private static final double RAD_PER_DEG = Math.PI / 180.0;
	private static final double EARTH_RADIUS_METERS = 6367000.0;

	public static double fastDistanceMeters(double latRad1, double lngRad1,
			double latRad2, double lngRad2) {
		if ((Math.abs(latRad1 - latRad2) > RAD_PER_DEG)
				|| (Math.abs(lngRad1 - lngRad2) > RAD_PER_DEG)) {
			return accurateDistanceMeters(latRad1, lngRad1, latRad2, lngRad2);
		}
		// Approximate sin(x) = x.
		double sineLat = (latRad1 - latRad2);

		// Approximate sin(x) = x.
		double sineLng = (lngRad1 - lngRad2);

		// Approximate cos(lat1) * cos(lat2) using
		// cos((lat1 + lat2)/2) ^ 2
		double cosTerms = Math.cos((latRad1 + latRad2) / 2.0);
		cosTerms = cosTerms * cosTerms;
		double trigTerm = sineLat * sineLat + cosTerms * sineLng * sineLng;
		trigTerm = Math.sqrt(trigTerm);

		// Approximate arcsin(x) = x
		return EARTH_RADIUS_METERS * trigTerm;
	}

	public static double accurateDistanceMeters(double lat1, double lng1,
			double lat2, double lng2) {
		double dlat = Math.sin(0.5 * (lat2 - lat1));
		double dlng = Math.sin(0.5 * (lng2 - lng1));
		double x = dlat * dlat + dlng * dlng * Math.cos(lat1) * Math.cos(lat2);
		return (2 * Math.atan2(Math.sqrt(x), Math.sqrt(Math.max(0.0, 1.0 - x))))
				* EARTH_RADIUS_METERS;
	}

	public static final double toMile(double meter) {
		return meter / 1609;
	}

	public static String formatLatitudeLongitude(String format,
			double latitude, double longitude) {
		// We need to specify the locale otherwise it may go wrong in some
		// language
		// (e.g. Locale.FRENCH)
		return String.format(Locale.ENGLISH, format, latitude, longitude);
	}

	public static void showOnMap(Context context, double latitude,
			double longitude) {
		try {
			// We don't use "geo:latitude,longitude" because it only centers
			// the MapView to the specified location, but we need a marker
			// for further operations (routing to/from).
			// The q=(lat, lng) syntax is suggested by geo-team.
			String uri = formatLatitudeLongitude(
					"http://maps.google.com/maps?f=q&q=(%f,%f)", latitude,
					longitude);
			ComponentName compName = new ComponentName(MAPS_PACKAGE_NAME,
					MAPS_CLASS_NAME);
			Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri))
					.setComponent(compName);
			context.startActivity(mapsIntent);
		} catch (ActivityNotFoundException e) {
			// Use the "geo intent" if no GMM is installed
			Log.e(TAG, "GMM activity not found!", e);
			String url = formatLatitudeLongitude("geo:%f,%f", latitude,
					longitude);
			Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			context.startActivity(mapsIntent);
		}
	}

	public static void setViewPointMatrix(float matrix[], float x, float y,
			float z) {
		// The matrix is
		// -z, 0, x, 0
		// 0, -z, y, 0
		// 0, 0, 1, 0
		// 0, 0, 1, -z
		Arrays.fill(matrix, 0, 16, 0);
		matrix[0] = matrix[5] = matrix[15] = -z;
		matrix[8] = x;
		matrix[9] = y;
		matrix[10] = matrix[11] = 1;
	}

	public static int getBucketId(String path) {
		return path.toLowerCase().hashCode();
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
			Log.i(TAG, "Fail to access external storage", e);
		}
		return false;
	}

	public static void assertInMainThread() {
		if (Thread.currentThread() == sContext.getMainLooper().getThread()) {
			throw new AssertionError();
		}
	}

	public static void doubleToRational(double value, long[] output) {
		// error is a magic number to control the tollerance of error
		doubleToRational(value, output, 0.00001);
	}

	private static void doubleToRational(double value, long[] output,
			double error) {
		long number = (long) value;
		value -= number;
		if (value < 0.000001 || error > 1) {
			output[0] = (int) (number + value + 0.5);
			output[1] = 1;
		} else {
			doubleToRational(1.0 / value, output, error / value);
			number = number * output[0] + output[1];
			output[1] = output[0];
			output[0] = number;
		}
	}

	public static float interpolateScale(float source, float target,
			float progress) {
		return source + progress * (target - source);
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

	/**
	 * 得到缩略图
	 */
	public static Group<ImageBucket> getThumbnail() {
		Group<ImageBucket> result = new Group<ImageBucket>();
		try {
			HashMap<String, String> thumbnailList = new HashMap<String, String>();
			HashMap<String, ImageBucket> bucketList = new HashMap<String, ImageBucket>();

			ContentResolver cResolver = sContext.getContentResolver();
			String[] projection = { Thumbnails._ID, Thumbnails.IMAGE_ID,
					Thumbnails.DATA };
			Cursor cursor = cResolver.query(Thumbnails.EXTERNAL_CONTENT_URI,
					projection, null, null, null);

			if (cursor == null) {
				return result;
			}
			if (cursor.moveToFirst()) {
				int image_id;
				String image_path;
				int image_idColumn = cursor.getColumnIndex(Thumbnails.IMAGE_ID);
				int dataColumn = cursor.getColumnIndex(Thumbnails.DATA);
				do {
					image_id = cursor.getInt(image_idColumn);
					image_path = cursor.getString(dataColumn);
					thumbnailList.put(String.valueOf(image_id), image_path);
				} while (cursor.moveToNext());
			} else {
				return result;
			}
			if (!cursor.isClosed()) {
				cursor.close();
			}
			// 构造相册索引
			String columns[] = new String[] { Media._ID, Media.BUCKET_ID,
					Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME,
					Media.TITLE, Media.SIZE, Media.BUCKET_DISPLAY_NAME };
			// 得到一个游标
			cursor = cResolver.query(Media.EXTERNAL_CONTENT_URI, columns, null,
					null, null);
			if (cursor == null) {
				return result;
			}
			if (cursor.moveToFirst()) {
				// 获取指定列的索引
				File file = null;
				int photoIDIndex = cursor.getColumnIndexOrThrow(Media._ID);
				int photoPathIndex = cursor.getColumnIndexOrThrow(Media.DATA);
				int bucketDisplayNameIndex = cursor
						.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
				int bucketIdIndex = cursor
						.getColumnIndexOrThrow(Media.BUCKET_ID);
				do {
					String _id = cursor.getString(photoIDIndex);

					String path = cursor.getString(photoPathIndex);

					String bucketName = cursor
							.getString(bucketDisplayNameIndex);
					String bucketId = cursor.getString(bucketIdIndex);

					ImageBucket bucket = bucketList.get(bucketId);
					if (bucket == null) {
						bucket = new ImageBucket();
						bucketList.put(bucketId, bucket);
						bucket.setImageList(new ArrayList<ImageItem>());
						bucket.setBucketName(bucketName);
					}
					file = new File(path);
					bucket.setCount(bucket.getCount() + 1);
					ImageItem imageItem = new ImageItem();
					imageItem.setImageId(_id);
					if (file.exists()) {
						bucket.setCount(bucket.getCount() + 1);
						imageItem.setImagePath(path);
						imageItem.setThumbnailPath(thumbnailList.get(_id));
					} else {
						imageItem.setImagePath(thumbnailList.get(_id));
						imageItem.setThumbnailPath(thumbnailList.get(_id));
					}
					bucket.getImageList().add(imageItem);
				} while (cursor.moveToNext());
				Iterator<Entry<String, ImageBucket>> itr = bucketList
						.entrySet().iterator();
				while (itr.hasNext()) {
					Map.Entry<String, ImageBucket> entry = (Map.Entry<String, ImageBucket>) itr
							.next();
					result.add(entry.getValue());
				}
			}
			if (!cursor.isClosed()) {
				cursor.close();
			}
		} catch (Exception ex) {

		}
		return result;
	}

}
