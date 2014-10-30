package com.yujunkang.fangxinbao.utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

/**
 * 
 * @date 2014-7-8
 * @author xieb
 * 
 */
public class BitmapUtils {
	public final static String TAG = "BitmapUtils";

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable == null) {
			return null;
		}

		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);

		return bitmap;

	}

	public static byte[] bmpToByteArray(final Bitmap bmp) {
		if (bmp == null) {
			return null;
		}

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		// bmp.compress(CompressFormat.JPEG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 得到微信适配图片尺寸
	 * 
	 * @param myBitmap
	 * @param newImageSize
	 */
	public static void getBitmapScaleLenght(Bitmap myBitmap, int[] newImageSize) {
		if (myBitmap != null) {
			if (myBitmap.getWidth() > myBitmap.getHeight()) {
				// 宽大于高
				if (myBitmap.getWidth() > DataConstants.THUMB_SIZE) {
					newImageSize[0] = DataConstants.THUMB_SIZE;
					newImageSize[1] = Math.round((float) myBitmap.getHeight()
							/ (float) myBitmap.getWidth()
							* DataConstants.THUMB_SIZE);
				} else {
					newImageSize[0] = myBitmap.getWidth();
					newImageSize[1] = myBitmap.getHeight();
				}
			} else {
				// 宽小于高
				if (myBitmap.getHeight() > DataConstants.THUMB_SIZE) {
					newImageSize[0] = Math.round((float) myBitmap.getWidth()
							/ (float) myBitmap.getHeight()
							* DataConstants.THUMB_SIZE);
					newImageSize[1] = DataConstants.THUMB_SIZE;
				} else {
					newImageSize[0] = myBitmap.getWidth();
					newImageSize[1] = myBitmap.getHeight();
				}

			}
		}
	}

	/**
	 * 缩放图片尺寸，便于微信上传
	 * 
	 * @param src
	 * @return
	 */
	public static Bitmap scaleBmp(Bitmap src) {
		if (src == null) {
			return null;
		}

		int[] newImgSize = new int[] { 0, 0 };
		getBitmapScaleLenght(src, newImgSize);
		Bitmap thumbBmp = Bitmap.createScaledBitmap(src, newImgSize[0],
				newImgSize[1], true);

		return thumbBmp;
	}

	/**
	 * 获取分享的图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getShareImage(String url) {
		int[] newImageSize = new int[] { 0, 0 };
		Bitmap thumbBmp = null;
		Bitmap result = null;
		if (!TextUtils.isEmpty(url)) {
			Bitmap bmp = null;
			try {
				bmp = BitmapFactory.decodeStream(new URL(url).openStream());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				LoggerTool.d(TAG, e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LoggerTool.d(TAG, e.getMessage());
			}
			if (bmp != null) {
				getBitmapScaleLenght(bmp, newImageSize);
				thumbBmp = Bitmap.createScaledBitmap(bmp, newImageSize[0],
						newImageSize[1], true);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				thumbBmp.compress(Bitmap.CompressFormat.PNG, 90, stream);
				byte[] data = stream.toByteArray();

				result = BitmapFactory.decodeByteArray(data, 0, data.length);
				thumbBmp.recycle();
				bmp.recycle();
			}
		}
		return result;
	}

}
