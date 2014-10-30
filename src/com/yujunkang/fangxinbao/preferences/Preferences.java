package com.yujunkang.fangxinbao.preferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.method.Method;
import com.yujunkang.fangxinbao.method.VeDate;
import com.yujunkang.fangxinbao.model.Baby;
import com.yujunkang.fangxinbao.model.BluetoothDeviceInfo;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.UserAccount;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public class Preferences {
	private static final String TAG = "Preferences";
	public static final String FILE_PREFERENCE_COMMON_SETTING = "file_setting_common";
	public static final String PREFERENCE_IS_FIRST_RUN = "is_first_run";
	public static final String FILE_PREFERENCE_DEVICE_SETTING = "file_device_setting";
	/**
	 * 用户
	 */
	public static final String PREFERENCE_LOGIN = "user_login";
	public static final String PREFERENCE_PASSWORD = "user_password";

	public static final String PREFERENCE_ID = "id";
	public static final String PREFERENCE_USER_EMAIL = "user_email";
	public static final String PREFERENCE_USER_PHONE = "user_phone";
	public static final String PREFERENCE_USER_NATIONALITY = "user_nationality";
	public static final String PREFERENCE_USER_NATIONALITYCODE = "user_nationalitycode";
	public static final String PREFERENCE_USER_NATIONALITY_ENGNAME = "user_nationality_engname";
	public static final String PREFERENCE_USER_NATIONALITY_SIMPLE_CODE = "user_nationality_simplecode";
	public static final String PREFERENCE_USER_BABY = "user_baby";
	public static final String PREFERENCE_USER_TEMPERATURE = "user_temperature";
	/**
	 * 最后一次选择的宝宝id
	 */
	public static final String PREFERENCE_LAST_BABY_ID = "preference_last_baby_id";

	/**
	 * http公共参数
	 */
	public static final String PREFERENCE_COMMON_UID = "common_uid";
	public static final String PREFERENCE_COMMON_DVERC = "common_dverc";
	public static final String PREFERENCE_COMMON_DVERT = "common_dvert";
	public static final String PREFERENCE_LOCALE = "preference_locale";

	public static final String PREFERENCE_TEMPERATURE_COMMON_DATA = "preference_temperature_data";
	public static final String PREFERENCE_TEMPERATURE_UPDATE_DATE = "preference_temperature_update_date";
	public static final String PREFERENCE_TEMPERATURE_UPDATE_VERSION = "preference_temperature_update_version";
	
	
	

	/**
	 * 设置参数
	 */
	public static final String PREFERENCE_MAX_TEMPERATURE = "preference_max_temperature";
	public static final String PREFERENCE_MIN_TEMPERATURE = "preference_min_temperature";
	public static final String PREFERENCE_TEMPERATURE_TYPE = "preference_temperature_type";
	public static final String PREFERENCE_TEMPERATURE_RANGE_VERSION = "preference_temperature_range_version";
	public static final String PREFERENCE_REMOTE_SETTING_USER_MANUAL_ZH = "preference_remote_setting_user_manual_zh";
	public static final String PREFERENCE_REMOTE_SETTING_USER_MANUAL_EN = "preference_remote_setting_user_manual_en";
	public static final String PREFERENCE_REMOTE_SETTING_DEVICE_URL = "preference_remote_setting_device_url";
	public static final String PREFERENCE_FIRSR_SHOW_SLIDING_PROMPT = "preference_first_show_sliding_prompt";
	public static final String PREFERENCE_SERVICE_PHONE_ZH = "preference_service_phone_zh";
	public static final String PREFERENCE_SERVICE_PHONE_EN = "preference_service_phone_en";
	public static final String PREFERENCE_MEASUREMENT_PERIOD = "preference_measurement_interval";
	public static final String PREFERENCE_ALARM_ENABLE = "preference_alarm_enable";
	/**
	 * 设备
	 */
	public static final String PREFERENCE_DEVICE_INFO = "preference_device_info";

	/*
	 * 设置指定文件和字段的字符串
	 */
	public static void setString(Context context, String filename,
			String field, String value) {
		if (TextUtils.isEmpty(value)) {
			return;
		}
		Editor edit = getPreferencesEditor(filename, context);
		edit.putString(field, value).commit();
	}

	/*
	 * 获取指定文件和字段的字符串
	 */
	public static String getString(Context context, String filename,
			String field) {
		return getString(context, filename, field, "");
	}

	/*
	 * 获取指定文件和字段的字符串，可以设置默认值
	 */
	public static String getString(Context context, String filename,
			String field, String defaultValue) {
		return getPreferences(filename, context).getString(field, defaultValue);
	}

	public static SharedPreferences getPreferences(String filename,
			Context context) {
		return !TextUtils.isEmpty(filename) ? context.getSharedPreferences(
				filename, Context.MODE_PRIVATE) : PreferenceManager
				.getDefaultSharedPreferences(context);
	}

	public static Editor getPreferencesEditor(String filename, Context context) {
		return getPreferences(filename, context).edit();

	}
	
	

	/**
	 * 注销用户信息
	 * 
	 * @param context
	 * @return
	 */
	public static boolean logoutUser(Context context) {
		Editor editor = FangXinBaoApplication.getApplication(context)
				.getPrefs().edit();
		editor.clear();
		editor.commit();
		FangXinBaoApplication.getApplication(context).getNetWorKManager()
				.setCredentials(null, null);
		return true;
	}

	public static User getUserInfo(Context context) {
		String userId = getString(context, null, PREFERENCE_ID);
		if (!TextUtils.isEmpty(userId)) {
			User user = new User();
			user.setId(userId);
			Country country = new Country();
			country.setName(getString(context, null,
					PREFERENCE_USER_NATIONALITY));
			country.setCountryCode(getString(context, null,
					PREFERENCE_USER_NATIONALITYCODE));
			country.setCountrySimpleCode(getString(context, null,
					PREFERENCE_USER_NATIONALITY_SIMPLE_CODE));
			country.setEngName(getString(context, null,
					PREFERENCE_USER_NATIONALITY_ENGNAME));
			user.setCountry(country);
			user.setPhone(getString(context, null, PREFERENCE_USER_PHONE));
			user.setEmail(getString(context, null, PREFERENCE_USER_EMAIL));
			String baby = getString(context, null, PREFERENCE_USER_BABY);
			if (!TextUtils.isEmpty(baby)) {
				Gson gson = new Gson();
				Group<Baby> babyGroup = gson.fromJson(baby,
						new TypeToken<Group<Baby>>() {
						}.getType());
				user.setBaBies(babyGroup);
				LoggerTool.d(TAG,
						"baby count: " + String.valueOf(babyGroup.size()));
			} else {
				user.setBaBies(new Group<Baby>());
			}
			return user;
		}
		return null;
	}

	public static User getUserCommonInfo(Context context) {
		String userId = getString(context, null, PREFERENCE_ID);
		if (!TextUtils.isEmpty(userId)) {
			User user = new User();
			user.setId(userId);
			Country country = new Country();
			country.setName(getString(context, null,
					PREFERENCE_USER_NATIONALITY));
			country.setCountryCode(getString(context, null,
					PREFERENCE_USER_NATIONALITYCODE));
			country.setCountrySimpleCode(getString(context, null,
					PREFERENCE_USER_NATIONALITY_SIMPLE_CODE));
			country.setEngName(getString(context, null,
					PREFERENCE_USER_NATIONALITY_ENGNAME));
			user.setCountry(country);
			user.setPhone(getString(context, null, PREFERENCE_USER_PHONE));
			user.setEmail(getString(context, null, PREFERENCE_USER_EMAIL));
			return user;
		}
		return null;
	}

	/**
	 * 保存用户名和账号
	 * 
	 * @param context
	 * @param login
	 * @param password
	 * @param countryCode
	 * @return
	 */
	public static boolean storeLoginAndPassword(Context context, String login,
			String password, String countryCode) {
		Editor editor = FangXinBaoApplication.getApplication(context)
				.getPrefs().edit();
		if (Utils.isMobile(login)) {
			StringBuilder sb = new StringBuilder(countryCode);
			sb.append(",");
			sb.append(login);
			login = sb.toString();
		}
		if (!TextUtils.isEmpty(login)) {
			editor.putString(PREFERENCE_LOGIN, login);
		}

		if (!TextUtils.isEmpty(password)) {
			editor.putString(PREFERENCE_PASSWORD, password);
		}
		if (!editor.commit()) {
			LoggerTool.d(TAG, "storeLoginAndPassword commit failed");
			return false;
		}
		FangXinBaoApplication.getApplication(context).getNetWorKManager()
				.setCredentials(login, password);
		return true;
	}

	/**
	 * 获取登录账号信息
	 * 
	 * @param context
	 * @return
	 */
	public static UserAccount getUserAccount(Context context) {
		UserAccount account = null;
		String login = getString(context, null, PREFERENCE_LOGIN);
		if (!TextUtils.isEmpty(login)) {
			account = new UserAccount();
			account.setAccount(login);
			if (Utils.isEmail(login)) {
				account.setLoginType(UserAccount.LOGINTYPE_EMAIL);
			} else {
				account.setLoginType(UserAccount.LOGINTYPE_PHONE);
			}
			return account;

		}
		return null;
	}

	public static String getPassword(Context context) {

		return getString(context, null, PREFERENCE_PASSWORD);
	}

	public static void storeDeviceInfo(Context context, String deviceName,
			String macAddress) {
		Group<BluetoothDeviceInfo> result = getDeviceInfo(context);
		for (BluetoothDeviceInfo item : result) {
			if (item.getDeviceAddress().equals(macAddress)) {
				return;
			}
		}
		result.add(new BluetoothDeviceInfo(deviceName, macAddress));
		Editor editor = getPreferencesEditor(FILE_PREFERENCE_DEVICE_SETTING,
				context);
		Gson gson = new Gson();
		editor.putString(PREFERENCE_DEVICE_INFO, gson.toJson(result));
		editor.commit();
	}

	public static Group<BluetoothDeviceInfo> getDeviceInfo(Context context) {
		String deviceInfo = getString(context, FILE_PREFERENCE_DEVICE_SETTING,
				PREFERENCE_DEVICE_INFO);
		if (!TextUtils.isEmpty(deviceInfo)) {
			Gson gson = new Gson();
			Group<BluetoothDeviceInfo> deviceGroup = gson.fromJson(deviceInfo,
					new TypeToken<Group<BluetoothDeviceInfo>>() {
					}.getType());
			LoggerTool.d(TAG,
					"device count: " + String.valueOf(deviceGroup.size()));
			return deviceGroup;

		} else {
			return new Group<BluetoothDeviceInfo>();
		}

	}

	/**
	 * 保存用户信息
	 * 
	 * @param context
	 * @param user
	 */
	@SuppressLint("CommitPrefEdits")
	public static void storeUser(Context context, User user) {
		if (user != null && user.getId() != null) {
			Editor editor = FangXinBaoApplication.getApplication(context)
					.getPrefs().edit();
			editor.putString(PREFERENCE_ID, user.getId());
			if (!TextUtils.isEmpty(user.getEmail())) {
				editor.putString(PREFERENCE_USER_EMAIL, user.getEmail());
			}
			else
			{
				editor.putString(PREFERENCE_USER_EMAIL, "");
			}
			if (!TextUtils.isEmpty(user.getPhone())) {
				editor.putString(PREFERENCE_USER_PHONE, user.getPhone());
			}
			Country country = user.getCountry();
			if (country != null) {
				if (!TextUtils.isEmpty(country.getName())) {
					editor.putString(PREFERENCE_USER_NATIONALITY, user
							.getCountry().getName());
				}
				if (!TextUtils.isEmpty(country.getCountryCode())) {
					editor.putString(PREFERENCE_USER_NATIONALITYCODE, user
							.getCountry().getCountryCode());
				}
				if (!TextUtils.isEmpty(country.getCountrySimpleCode())) {
					editor.putString(PREFERENCE_USER_NATIONALITY_SIMPLE_CODE,
							user.getCountry().getCountrySimpleCode());
				}
				if (!TextUtils.isEmpty(country.getEngName())) {
					editor.putString(PREFERENCE_USER_NATIONALITY_ENGNAME, user
							.getCountry().getEngName());
				}
			}
			if (!TextUtils.isEmpty(user.getTemperature())) {
				editor.putString(PREFERENCE_USER_TEMPERATURE,
						user.getTemperature());
			}
			if (user.getBaBies() != null && user.getBaBies().size() > 0) {
				Gson gson = new Gson();
				editor.putString(PREFERENCE_USER_BABY,
						gson.toJson(user.getBaBies()));
			}
			editor.commit();
			LoggerTool.d(TAG, "Setting user info");
		} else {
			LoggerTool.d(TAG, "Unable to lookup user.");
		}
	}

	public static String getLastChooseBabyId(Context context) {
		return getString(context, null, PREFERENCE_LAST_BABY_ID);
	}

	public static void storeLastChooseBabyId(Context context, String id) {
		setString(context, null, PREFERENCE_LAST_BABY_ID, id);
	}

	/**
	 * 取得显示温度的方式(摄氏度，华氏度)
	 */
	public static int getTemperatureType(Context context) {
		return getPreferences(FILE_PREFERENCE_COMMON_SETTING, context).getInt(
				PREFERENCE_TEMPERATURE_TYPE, 0);
	}

	/**
	 * 保存温度显示形式
	 */
	public static void storeTemperatureType(Context context, int type) {

		try {
			Editor edit = getPreferencesEditor(FILE_PREFERENCE_COMMON_SETTING,
					context);
			edit.putInt(PREFERENCE_TEMPERATURE_TYPE, type);
			edit.commit();
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}
	/**
	 * 取得显示温度的方式(摄氏度，华氏度)
	 */
	public static int getMeasuermentInterval(Context context) {
		return getPreferences(FILE_PREFERENCE_COMMON_SETTING, context).getInt(
				PREFERENCE_MEASUREMENT_PERIOD,3);
	}

	/**
	 * 保存温度显示形式
	 */
	public static void storeMeasuermentInterval(Context context, int time) {

		try {
			Editor edit = getPreferencesEditor(FILE_PREFERENCE_COMMON_SETTING,
					context);
			edit.putInt(PREFERENCE_MEASUREMENT_PERIOD, time);
			edit.commit();
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}
	
	
	/**
	 * 获取高温报警设置
	 */
	public static float getMaxTemperature(Context context) {
		return getPreferences(FILE_PREFERENCE_COMMON_SETTING, context)
				.getFloat(PREFERENCE_MAX_TEMPERATURE, DataConstants.DEFAULT_MAX_TEMPERATURE);
	}

	/**
	 * 保存高温报警设置
	 */
	public static void storeMaxTemperature(Context context, float temperature) {

		try {
			Editor edit = getPreferencesEditor(FILE_PREFERENCE_COMMON_SETTING,
					context);
			edit.putFloat(PREFERENCE_MAX_TEMPERATURE, temperature);
			edit.commit();
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}
	
	/**
	 * 是否打开报警声音
	 */
	public static boolean IsAlarmEnable(Context context) {
		return getPreferences(FILE_PREFERENCE_COMMON_SETTING, context)
				.getBoolean(PREFERENCE_ALARM_ENABLE, true);
	}

	/**
	 * 设置打开报警声音
	 */
	public static void setAlarmEnable(Context context, boolean enable) {

		try {
			Editor edit = getPreferencesEditor(FILE_PREFERENCE_COMMON_SETTING,
					context);
			edit.putBoolean(PREFERENCE_ALARM_ENABLE, enable);
			edit.commit();
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}
	
	
	/**
	 * 是否第一次显示滑动提示
	 */
	public static boolean IsFirstShowSlidingPrompt(Context context) {
		return TextUtils.isEmpty(getPreferences(FILE_PREFERENCE_COMMON_SETTING, context)
				.getString(PREFERENCE_FIRSR_SHOW_SLIDING_PROMPT, null))?true:false;
	}

	/**
	 * 保存低温报警设置
	 */
	public static void storeFirstShowSlidingPrompt(Context context) {

		try {
			Editor edit = getPreferencesEditor(FILE_PREFERENCE_COMMON_SETTING,
					context);
			edit.putString(PREFERENCE_FIRSR_SHOW_SLIDING_PROMPT, "1");
			edit.commit();
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}
	/**
	 * 获取低温报警设置
	 */
	public static float getMinTemperature(Context context) {
		return getPreferences(FILE_PREFERENCE_COMMON_SETTING, context)
				.getFloat(PREFERENCE_MIN_TEMPERATURE, DataConstants.DEFAULT_MIN_TEMPERATURE);
	}

	/**
	 * 保存低温报警设置
	 */
	public static void storeMinTemperature(Context context, float temperature) {

		try {
			Editor edit = getPreferencesEditor(FILE_PREFERENCE_COMMON_SETTING,
					context);
			edit.putFloat(PREFERENCE_MIN_TEMPERATURE, temperature);
			edit.commit();
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}

	/**
	 * 获取高温报警设置
	 */
	public static float getIsFirstRunFlag(Context context) {
		return getPreferences(FILE_PREFERENCE_COMMON_SETTING, context)
				.getFloat(PREFERENCE_MAX_TEMPERATURE, -1);
	}

	/**
	 * 取得uid参数
	 */
	public static String getUid(Context context) {
		String uid = "";
		try {
			uid = getString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_COMMON_UID, "");
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return uid;
	}

	/**
	 * 保存uid参数
	 */
	public static void storeUid(Context context, String uid) {

		try {
			setString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_COMMON_UID, uid);
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}

	/**
	 * 取得dverc参数
	 */
	public static String getDverc(Context context) {
		String dverc = "";
		try {
			dverc = getString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_COMMON_DVERC, "0.0");
			// dverc = "0.0";
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return dverc;
	}

	/**
	 * 保存dverc参数(国家)
	 */
	public static void storeDverc(Context context, String version) {
		try {
			setString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_COMMON_DVERC, version);
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}
	
	/**
	 * 用户手册中文
	 */
	public static String getUserManual_ZH(Context context) {
		String result = "";
		try {
			result = getString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_REMOTE_SETTING_USER_MANUAL_ZH, "");
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return result;
	}

	/**
	 * 保存用户手册中文
	 */
	public static void storeUserManual_ZH(Context context, String url) {
		try {
			setString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_REMOTE_SETTING_USER_MANUAL_ZH, url);
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}
	
	/**
	 * 用户手册英文
	 */
	public static String getUserManual_EN(Context context) {
		String result = "";
		try {
			result = getString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_REMOTE_SETTING_USER_MANUAL_EN, "");
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return result;
	}

	/**
	 * 保存用户手册英文
	 */
	public static void storeUserManual_EN(Context context, String url) {
		try {
			setString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_REMOTE_SETTING_USER_MANUAL_EN, url);
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}
	
	
	/**
	 * 用户客服电话中文
	 */
	public static String getServicePhone_ZH(Context context) {
		String result = "";
		try {
			result = getString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_SERVICE_PHONE_ZH, "");
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return result;
	}

	/**
	 * 保存客服电话中文
	 */
	public static void storeServicePhone_ZH(Context context, String phone) {
		try {
			setString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_SERVICE_PHONE_ZH, phone);
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}
	
	/**
	 * 客服电话英文
	 */
	public static String getServicePhone_EN(Context context) {
		String result = "";
		try {
			result = getString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_SERVICE_PHONE_EN, "");
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return result;
	}

	/**
	 * 客服电话
	 */
	public static void storeServicePhone_EN(Context context, String phone) {
		try {
			setString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_SERVICE_PHONE_EN, phone);
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}
	
	public static String getServicePhone(Context context)
	{
		if (Method.IsChinese(context)) {
			return getServicePhone_ZH(context);
		} else {
			return getServicePhone_EN(context);
		}
	}
	
	/**
	 * 用户手册英文
	 */
	public static String getDeviceUrl(Context context) {
		String result = "";
		try {
			result = getString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_REMOTE_SETTING_DEVICE_URL, "");
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return result;
	}

	/**
	 * 保存用户手册英文
	 */
	public static void storeDeviceUrl(Context context, String url) {
		try {
			setString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_REMOTE_SETTING_DEVICE_URL, url);
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}
	
	/**
	 * 取得温度参数
	 */
	public static String getDvert(Context context) {

		String dvert = "";
		try {
			dvert = getString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_COMMON_DVERT, "0.0");
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return dvert;
	}

	public static String getTemperatureRangeVersion(Context context) {
		String dvero = "";
		try {
			// 保存温度数据
			dvero= getString(context, FILE_PREFERENCE_COMMON_SETTING,
						PREFERENCE_TEMPERATURE_RANGE_VERSION, null);
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return dvero;
	}
	
	
	
	
	/**
	 * 
	 */
	public static String getIsFirstRun(Context context) {

		try {
			return getString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_IS_FIRST_RUN, null);
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return null;
	}

	/**
	 * 
	 */
	public static void storeIsFirstRun(Context context) {
		try {
			setString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_IS_FIRST_RUN, "1");
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}

	/**
	 * 保存dvert参数(温度)
	 */
	public static void storeDvert(Context context, String version) {
		try {
			setString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_COMMON_DVERT, version);
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
	}

	/**
	 * 保存同步统计数据的日期
	 * 
	 * @param context
	 * @param version
	 * @param data
	 */
	public static void storeSynchronousDate(Context context) {
		try {
			// 保存温度数据
			setString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_TEMPERATURE_UPDATE_DATE,
					VeDate.getStringDateShort());
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}

	}

	/**
	 * 获取同步统计数据的日期
	 * 
	 * @param context
	 * @param version
	 * @param data
	 */
	public static String getSynchronousDate(Context context) {
		String date = "";
		try {
			date = getString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_TEMPERATURE_UPDATE_DATE, "");
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return date;

	}

	/**
	 * 保存同步统计数据的版本号
	 * 
	 * @param context
	 * @param version
	 * @param data
	 */
	public static void storeSynchronousVersion(Context context, String version) {
		try {
			// 保存温度数据
			setString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_TEMPERATURE_UPDATE_VERSION, version);
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}

	}

	/**
	 * 删除
	 * 
	 * @param context
	 * @param version
	 * @param data
	 */
	public static void clearSynchronousSetting(Context context) {
		try {
			
			Editor edit = getPreferencesEditor(FILE_PREFERENCE_COMMON_SETTING,
					context);
			edit.putString(PREFERENCE_TEMPERATURE_UPDATE_VERSION, "");
			edit.putString(PREFERENCE_TEMPERATURE_UPDATE_DATE, "");
			edit.commit();
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}

	}
	
	/**
	 * 获取同步统计数据的版本号
	 * 
	 * @param context
	 * @param version
	 * @param data
	 */
	public static String getSynchronousVersion(Context context) {
		String version = "";
		try {
			version = getString(context, FILE_PREFERENCE_COMMON_SETTING,
					PREFERENCE_TEMPERATURE_UPDATE_VERSION, "");
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}
		return version;

	}

	/**
	 * 保存温度基础数据
	 * 
	 * @param context
	 * @param version
	 * @param data
	 */
	public static void updateTemperatureCommonData(Context context,
			String data, String version) {
		try {
			// 保存温度数据
			if (!TextUtils.isEmpty(data)) {
				setString(context, FILE_PREFERENCE_COMMON_SETTING,
						PREFERENCE_TEMPERATURE_COMMON_DATA, data);
				if (!TextUtils.isEmpty(version)) {
					storeDvert(context, version);
				}
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}

	}

	/**
	 * 获取温度基础数据
	 * 
	 * @param context
	 * @param version
	 * @param data
	 */
	public static String getTemperatureCommonData(Context context) {
		return getString(context, FILE_PREFERENCE_COMMON_SETTING,
				PREFERENCE_TEMPERATURE_COMMON_DATA);
	}

	/**
	 * 保存温度的高温，低温设置版本号
	 * 
	 * @param context
	 * @param version
	 * @param data
	 */
	public static void updateTemperatureRangeVersion(Context context,
			String version) {
		try {
			// 保存温度数据
			if (!TextUtils.isEmpty(version)) {
				setString(context, FILE_PREFERENCE_COMMON_SETTING,
						PREFERENCE_TEMPERATURE_RANGE_VERSION, version);
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		}

	}
	
	

	public static void loginUser(Context context, String login,
			String password, User user) {
		if (Preferences
				.storeLoginAndPassword(context, login, password, user
						.getCountry() != null ? user.getCountry()
						.getCountryCode() : "")) {
			storeUser(context, user);
		}
	}

}
