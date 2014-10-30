package com.yujunkang.fangxinbao.method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.json.JSONArray;

import com.baidu.frontia.api.FrontiaSocialShare;
import com.baidu.frontia.api.FrontiaSocialShareContent;
import com.baidu.frontia.api.FrontiaAuthorization.MediaType;
import com.baidu.frontia.api.FrontiaSocialShareListener;


import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yujunkang.fangxinbao.R;

import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.compare.CompareUrlParams;
import com.yujunkang.fangxinbao.database.DBHelper;
import com.yujunkang.fangxinbao.model.CommonData;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.ISinaWeiboShare;
import com.yujunkang.fangxinbao.model.ShareData;
import com.yujunkang.fangxinbao.model.ShareItem;
import com.yujunkang.fangxinbao.model.TemperatureCommonData;
import com.yujunkang.fangxinbao.model.TemperatureStatusDesc;
import com.yujunkang.fangxinbao.parser.GroupParser;
import com.yujunkang.fangxinbao.parser.TemperatureCommonDataPareser;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.DataConstants.TemperatureLevel;
import com.yujunkang.fangxinbao.utility.DataConstants.TemperatureType;
import com.yujunkang.fangxinbao.utility.DialogHelper;
import com.yujunkang.fangxinbao.utility.JniEncrypt;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.ShareUtils;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

/**
 * 
 * @date 2014-5-24
 * @author xieb
 * 
 */
public class Method {
	static final String TAG = "Method";

	/**
	 * 批量打包会用到
	 * @param context
	 * @return
	 */
	public static String getSource() {
		return DataConstants.SOURCE;
		
	}

	/**
	 * 取得imei参数
	 */
	public static String getImei(Context context) {
		String imei = "";
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();
			if (TextUtils.isEmpty(deviceid) || deviceid.contains("unknown")) {
				// 取不到imei或imei不正确时，用androidId替代
				deviceid = "aid"
						+ Secure.getString(context.getContentResolver(),
								Secure.ANDROID_ID);
			}
			imei = java.net.URLEncoder.encode(deviceid + "", "UTF-8");
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return imei;
	}

	/**
	 * 取得sid参数
	 */
	public static String getSid(Context context, List<NameValuePair> params) {

		try {
			Collections.sort(params, new CompareUrlParams());
			int dataCount = params.size();
			String[] keys = new String[dataCount];
			for (int index = 0; index < dataCount; index++) {
				keys[index] = params.get(index).getValue();
			}
			String csid = JniEncrypt.getInstance(context)
					.getEncryptString(keys);
			LoggerTool.d(TAG, csid);
			return csid;

		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return null;
	}

	private String getVersionName(Context context) {

		try {
			// 获取packagemanager的实例
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo;
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
			String version = packInfo.versionName;
			return version;
		} catch (NameNotFoundException e) {

		}
		return "";
	}

	/**
	 * 是中文还是英文
	 */
	public static boolean IsChinese(Context context) {
		Locale locale = FangXinBaoApplication.getApplication(context)
				.getLocale();
		try {
			String language = locale.getLanguage();
			if (language.contains("zh") || language.equals("zh")) {
				return true;
			}
		} catch (Exception ex) {
			return true;
		}
		return false;
	}

	public static String getTimeStamp(Context context) {
		long now = System.currentTimeMillis();
		FangXinBaoApplication app = FangXinBaoApplication
				.getApplication(context);
		long result = now + app.getTimeStampOffset();
		return String.valueOf(result);
	}

	public static String getUsrManual(Context context) {
		if (IsChinese(context)) {
			return Preferences.getUserManual_ZH(context);
		}
		return Preferences.getUserManual_EN(context);
	}

	/**
	 * 
	 * @param context
	 * @param version
	 * @param data
	 */
	public static void updateCommonData(Context context, CommonData data) {
		try {
			boolean success = false;
			// 保存国家数据
			if (data.getCountries() != null && data.getCountries().size() > 0) {
				DBHelper databaseHelper = DBHelper.getDBInstance(context);
				success = databaseHelper
						.batchInsertCountry(data.getCountries());
				if (success) {
					// 保存国家数据版本号
					Preferences.storeDverc(context, data.getCountryVersion());
				}
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}

	}

	/**
	 * 根据温度得到温度状态
	 * 
	 * @param context
	 * @param temperature
	 * @return
	 */
	public static TemperatureLevel getTemperatureLevel(Context context,
			float temperature) {
		TemperatureLevel result = TemperatureLevel.LOW;
		try {
			String temperatureData = Preferences
					.getTemperatureCommonData(context);
			if (!TextUtils.isEmpty(temperatureData)) {
				Group<TemperatureCommonData> datas = new GroupParser(
						new TemperatureCommonDataPareser())
						.parse(new JSONArray(temperatureData));
				if (datas != null) {
					for (TemperatureCommonData item : datas) {
						if (item.inRange(temperature)) {
							return item.getTemperatureLevel();
						}
					}
				}
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return result;
	}

	/**
	 * 分享公共对话框
	 * 
	 * @param context
	 * @param shareMenuList
	 * @param shareData
	 */
	public static void showShareDialog(final Context context,
			final ShareData shareData, final FrontiaSocialShare socialShare,
			final FrontiaSocialShareContent mImageContent,
			final FrontiaSocialShareListener shareListener,
			final ISinaWeiboShare sinaShare) {

		final IWXAPI api = WXAPIFactory.createWXAPI(context,
				DataConstants.APP_ID, true);
		api.registerApp(DataConstants.APP_ID);

		final List<ShareItem> shareMenuList = ShareUtils.getShareObjList(
				context, api, shareData);
		if (shareMenuList.size() == 0) {
			return;
		}
		LoggerTool.d("Method", mImageContent.getContent());
		View gridMenuView = View
				.inflate(context, R.layout.share_gridview, null);
		GridView menuGrid = (GridView) gridMenuView.findViewById(R.id.gridview);
		menuGrid.setAdapter(getShareMenuAdapter(context, shareMenuList));
		final Dialog shareDialog = DialogHelper.popDialogGridMenuBottom(
				context, gridMenuView, null);
		menuGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ShareItem myObj = shareMenuList.get(position);
				if (myObj.shareType == ShareItem.SHARE_TYPE_SINA_WEIBO) {
					if (sinaShare != null) {
						sinaShare.executeShare();
					}
				} else if (myObj.shareType == ShareItem.SHARE_TYPE_QQ) {
					socialShare.share(mImageContent,
							MediaType.QQFRIEND.toString(), shareListener, true);
				} else if (myObj.shareType == ShareItem.SHARE_TYPE_SMS) {
					Uri smsToUri = Uri.parse("smsto:");

					Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

					intent.putExtra("sms_body", mImageContent.getContent());

					context.startActivity(intent);
				} else if (myObj.shareType == ShareItem.SHARE_TYPE_EMAIL) {
					socialShare.share(mImageContent,
							MediaType.EMAIL.toString(), shareListener, true);
				} else if (myObj.shareType == ShareItem.SHARE_TYPE_WEIXIN) {
					socialShare.share(mImageContent,
							MediaType.WEIXIN.toString(), shareListener, true);
					// 2种方式都可以用
					// final IWXAPI api = WXAPIFactory.createWXAPI(context,
					// DataConstants.WEIXIN_APP_ID, true);
					// api.registerApp(DataConstants.WEIXIN_APP_ID);
					// try {
					// if (api.isWXAppInstalled()) {
					// WXMediaMessage msg = null;
					// if (shareData.getWeixinApiType() == ShareData.WEBPAGE) {
					// WXWebpageObject webpage = new WXWebpageObject();
					// if (!TextUtils.isEmpty(shareData.getWeixinUrl())) {
					// webpage.webpageUrl = shareData
					// .getWeixinUrl();
					// } else {
					// webpage.webpageUrl = "www.baidu.com";
					// }
					// msg = new WXMediaMessage(webpage);
					// } else if (shareData.getWeixinApiType() ==
					// ShareData.IMAGE) {
					// WXImageObject image = new WXImageObject();
					// if (shareData.getWeixinBytes() != null) {
					// image.imageData = shareData
					// .getWeixinBytes();
					// } else if (shareData.getWeixinImg() != null) {
					// image.imageData = BitmapUtils
					// .bmpToByteArray(shareData
					// .getWeixinImg());
					// }
					//
					// if (!TextUtils.isEmpty(shareData.getWeixinUrl())) {
					// image.imageUrl = "www.baidu.com";
					// }
					//
					// msg = new WXMediaMessage(image);
					// }
					//
					// if (msg != null) {
					// if (!TextUtils.isEmpty(shareData.getWeixinMsg())) {
					// msg.description = shareData.getWeixinMsg();
					// }
					//
					// if (!TextUtils.isEmpty(shareData
					// .getWeixinTitle())) {
					// msg.title = shareData.getWeixinTitle();
					// }
					//
					// if (shareData.getWeixinBytes() != null) {
					// msg.thumbData = shareData.getWeixinBytes();
					// } else if (shareData.getWeixinImg() != null) {
					// Bitmap scaledImg = BitmapUtils
					// .scaleBmp(shareData.getWeixinImg());
					// msg.thumbData = BitmapUtils
					// .bmpToByteArray(scaledImg);
					// } else {
					// Drawable icon = context
					// .getResources()
					// .getDrawable(R.drawable.ic_launcher);
					// Bitmap scaledImg = BitmapUtils
					// .scaleBmp(BitmapUtils
					// .drawableToBitmap(icon));
					// msg.thumbData = BitmapUtils
					// .bmpToByteArray(scaledImg);
					// }
					// }
					// SendMessageToWX.Req req = new SendMessageToWX.Req();
					// req.transaction = "webpage"
					// + System.currentTimeMillis();
					// req.message = msg;
					// api.sendReq(req);
					// }
					// } catch (Exception e) {
					// LoggerTool.e("SearchDetail_Adapter", "", e);
					// }

				} else if (myObj.shareType == ShareItem.SHARE_TYPE_WEIXIN_FRIEND) {
					socialShare.share(mImageContent,
							MediaType.WEIXIN_FRIEND.toString(), shareListener,
							true);
				}
				shareDialog.dismiss();
			}
		});

	}

	public static SimpleAdapter getShareMenuAdapter(Context context,
			List<ShareItem> menuList) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (ShareItem item : menuList) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", getShareImageResourceId(item.shareType));
			map.put("itemText", item.shareName);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(context, data,
				R.layout.share_grid_menu_item, new String[] { "itemImage",
						"itemText" }, new int[] { R.id.item_image,
						R.id.item_text });
		return simperAdapter;
	}

	/**
	 * 按分享菜单名返回对应资源图片id
	 * 
	 * @param shareName
	 * @return
	 */
	private static int getShareImageResourceId(int sharetype) {
		int resourceId = -1;
		switch (sharetype) {
		case ShareItem.SHARE_TYPE_QQ: {
			resourceId = R.drawable.icon_share_qq;
			break;
		}
		case ShareItem.SHARE_TYPE_SINA_WEIBO: {
			resourceId = R.drawable.icon_share_sina;
			break;
		}
		case ShareItem.SHARE_TYPE_SMS: {
			resourceId = R.drawable.icon_share_sms;
			break;
		}
		case ShareItem.SHARE_TYPE_WEIXIN: {
			resourceId = R.drawable.icon_share_weixin;
			break;
		}
		case ShareItem.SHARE_TYPE_WEIXIN_FRIEND: {
			resourceId = R.drawable.icon_share_weixin_friend;
			break;
		}
		}

		return resourceId;
	}

	/**
	 * 屏幕截图
	 * 
	 * @return
	 */
	public static Bitmap catchScreen(Context context) {

		Bitmap result = null;
		View shareView = ((Activity) context).getWindow().getDecorView();
		Bitmap tempBit = Bitmap.createBitmap(shareView.getWidth(),
				shareView.getHeight(), Bitmap.Config.RGB_565);
		LoggerTool.d("catchscreen", "DecorView: " + shareView.getHeight());
		Canvas c = new Canvas(tempBit);
		shareView.layout(0, 0, shareView.getWidth(), shareView.getHeight());
		shareView.draw(c);

		// 获取状态栏高度
		Rect frame = new Rect();
		((Activity) context).getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		// 获取屏幕长和高
		int width = ((Activity) context).getWindowManager().getDefaultDisplay()
				.getWidth();
		int height = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getHeight();
		LoggerTool.d("catchscreen", "DecorView:Display" + height);
		// 去掉标题栏
		try {
			result = Bitmap.createBitmap(tempBit, 0, statusBarHeight, width,
					height - statusBarHeight);
		} catch (Exception ex) {
			result = Bitmap.createBitmap(tempBit, 0, statusBarHeight, width,
					tempBit.getHeight() - statusBarHeight);
		}
		return result;
	}

	/**
	 * 判断微信版本是否大于指定版本数，以此来判断是否能够使用朋友圈功能
	 * 
	 * @param weixinVer
	 * @return
	 */
	public static boolean friendIsEnable(int weixinVer) {
		boolean result = false;
		if (weixinVer > DataConstants.WEIXIN_FRIEND_VER) {
			result = true;
		}
		return result;
	}

	public static TemperatureCommonData getTemperatureLevelStatus(float temperature,
			Context context) {
		try {
			Group<TemperatureCommonData> temperatureCommonDatas = FangXinBaoApplication.getApplication(context).getTemperatureLevel();
			if (temperatureCommonDatas != null
					&& temperatureCommonDatas.size() > 0) {
				for (TemperatureCommonData item : temperatureCommonDatas) {
					if (item.inRange(temperature)) {
						return item;
					}
				}
			}
		} catch (Exception ex) {

		}
		return null;
	}

	public static String getTemperatureUnit(Context context, int temperatureType) {
		int temperatureFormat = R.string.temperature_unit_c;
		try {
			if (temperatureType == TemperatureType.Fahrenheit.ordinal()) {
				temperatureFormat = R.string.temperature_unit_f;
			}
		} catch (Exception ex) {

		}
		return context.getString(temperatureFormat);
	}

	public static String getTemperatureUnit(Context context) {

		try {
			int temperatureType = Preferences.getTemperatureType(context);
			return getTemperatureUnit(context, temperatureType);
		} catch (Exception ex) {
			return context.getString(R.string.temperature_unit_c);
		}

	}

	public static TemperatureStatusDesc getTemperatureDescByLocal(
			TemperatureCommonData data, Context context) {
		if (data != null) {
			return IsChinese(context) ? data.getDesc_zh() : data.getDesc_en();
		}
		return null;
	}

	public static void enableView(View view) {
		AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 1.0f);
		alphaDown.setDuration(0);
		alphaDown.setFillAfter(true);
		view.startAnimation(alphaDown);
		view.setClickable(true);
		view.setEnabled(true);
	}

	public static void disableListItemView(View view) {
		AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 0.4f);
		alphaDown.setDuration(0);
		alphaDown.setFillAfter(true);
		view.startAnimation(alphaDown);

		view.setEnabled(false);
	}

	public static void enableListItemView(View view) {
		AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 1.0f);
		alphaDown.setDuration(0);
		alphaDown.setFillAfter(true);
		view.startAnimation(alphaDown);

		view.setEnabled(true);
	}

	public static void disableView(View view) {
		AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 0.4f);
		alphaDown.setDuration(0);
		alphaDown.setFillAfter(true);
		view.startAnimation(alphaDown);
		view.setClickable(false);
		view.setEnabled(false);
	}

	public static boolean isTemperatureValid(float data) {
		return (data < 100 && data > 20);
	}

}
