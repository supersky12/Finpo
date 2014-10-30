package com.yujunkang.fangxinbao.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import com.baidu.frontia.FrontiaApplication;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.umeng.analytics.MobclickAgent;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.user.UserMainActivity;
import com.yujunkang.fangxinbao.bluetoothlegatt.BluetoothleWrapManger;
import com.yujunkang.fangxinbao.cache.BitmapLruCache;
import com.yujunkang.fangxinbao.http.FangXinBaoHttpApi;
import com.yujunkang.fangxinbao.method.VeDate;
import com.yujunkang.fangxinbao.model.CommonData;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.MonthStatisticsData;
import com.yujunkang.fangxinbao.model.TemperatureCommonData;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.parser.GroupParser;
import com.yujunkang.fangxinbao.parser.TemperatureCommonDataPareser;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.task.AsyncTaskWithLoadingDialog;
import com.yujunkang.fangxinbao.task.UploadTemperatureTask;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.utility.AlbumUtils;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.IconUtils;
import com.yujunkang.fangxinbao.utility.JavaLoggingHandler;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public class FangXinBaoApplication extends FrontiaApplication {
	private static final String TAG = "FangXinBaoApplication";
	private static final boolean DEBUG = FangXinBaoSettings.DEBUG;
	static {
		Logger.getLogger("com.yujunkang.fangxinbao").addHandler(
				new JavaLoggingHandler());
		Logger.getLogger("ccom.yujunkang.fangxinbao").setLevel(Level.ALL);
	}
	public static final String INTENT_ACTION_LOGGED_IN = DataConstants.PACKAGE_NAME
			+ ".intent.action.LOGGED_IN";
	public static final String INTENT_ACTION_LOGGED_OUT = DataConstants.PACKAGE_NAME
			+ ".intent.action.LOGGED_OUT";
	public static final String INTENT_ACTION_CHECK_TEMPERATURE = DataConstants.PACKAGE_NAME
			+ ".intent.action.CHECK_TEMPERATURE";
	public static final String INTENT_EXTRA_ALERT_MESSAGE = DataConstants.PACKAGE_NAME
			+ ".FangXinBaoApplication.INTENT_EXTRA_ALERT_MESSAGE";

	private TaskHandler mTaskHandler;
	private HandlerThread mTaskThread;
	public static boolean IS_FOREGROUND = false;
	private SharedPreferences mPrefs;
	private BitmapLruCache mCache;
	private NetWorkManager mNetWorkManager;
	private File cacheLocation;
	private long timeStampOffset = 0;
	private Locale mLocale = null;
	private StateHolder mStateHolder = new StateHolder();
	private BluetoothleWrapManger mBluetoothleManger;
	private Group<TemperatureCommonData> temperatureLevel;
	public static int INTERVAL_ACTIVE_DELAY = 5 * 60 * 1000;

	/*
	 * 存放所有activity的通讯处理器
	 */
	private static Map<String, Handler> mActivityHandlerPool = new HashMap<String, Handler>();
	/**
	 * 异步消息列表
	 * */
	private static List<AysncMessage> mAsynMessagePeddingList = new LinkedList<AysncMessage>();

	@Override
	public void onCreate() {
		super.onCreate();
		if (shouldInit()) {
			AlbumUtils.initialize(this);
			DataConstants.init(this);

			if (FangXinBaoSettings.DEBUG) {
				MobclickAgent.setDebugMode(true);
			}
			INTERVAL_ACTIVE_DELAY = Preferences.getMeasuermentInterval(this) * 60 * 1000;
			mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			initLanguage();

			float screenDensity = getApplicationContext().getResources()
					.getDisplayMetrics().density;
			IconUtils.get().setRequestHighDensityIcons(screenDensity > 1.0f);

			mTaskThread = new HandlerThread(TAG + "-AsyncThread");
			mTaskThread.start();
			mTaskHandler = new TaskHandler(mTaskThread.getLooper());
			// 图片缓存
			loadResourceManagers();

			// 初始化全局统一异常处理
			// CrashHandler.getInstance(this).init();

			loadFangXinBaoApi();
		}
	}

	private boolean shouldInit() {
		ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		String mainProcessName = getPackageName();
		int myPid = android.os.Process.myPid();
		for (RunningAppProcessInfo info : processInfos) {
			if (info.pid == myPid && mainProcessName.equals(info.processName)) {
				return true;
			}
		}
		return false;
	}

	public boolean isReady() {
		User user = Preferences.getUserCommonInfo(this);
		if (user == null) {
			return false;
		}
		if (TextUtils.isEmpty(user.getId())) {
			return false;
		}
		return true;
	}

	private void initLanguage() {
		mLocale = Locale.getDefault();
		if (mLocale.equals(Locale.TRADITIONAL_CHINESE)
				|| mLocale.equals(Locale.SIMPLIFIED_CHINESE)) {
			mLocale = Locale.SIMPLIFIED_CHINESE;
			return;
		}
		// 只支持中文和英文
		if (!mLocale.equals(Locale.CHINA) && !mLocale.equals(Locale.CHINESE)
				&& !mLocale.equals(Locale.ENGLISH)
				&& !mLocale.equals(Locale.SIMPLIFIED_CHINESE)
				&& !mLocale.equals(Locale.TRADITIONAL_CHINESE)) {
			mLocale = Locale.ENGLISH;

		}
	}

	public Locale getLocale() {
		return mLocale;
	}

	public void setLocale(Locale Locale) {
		this.mLocale = Locale;
	}

	public Group<TemperatureCommonData> getTemperatureLevel() {
		if (temperatureLevel == null) {
			String temperatureJson = Preferences.getTemperatureCommonData(this);
			try {
				temperatureLevel = new GroupParser(
						new TemperatureCommonDataPareser())
						.parse(new JSONArray(temperatureJson));
			} catch (Exception e) {

			}
		}
		return temperatureLevel;
	}

	public void setTemperatureLevel(
			Group<TemperatureCommonData> temperatureLevel) {
		this.temperatureLevel = temperatureLevel;
	}

	public void requestStartService() {
		mTaskHandler.sendMessage( //
				mTaskHandler.obtainMessage(TaskHandler.MESSAGE_START_SERVICE));
	}

	public void requestUpdateUser() {
		mTaskHandler.sendEmptyMessage(TaskHandler.MESSAGE_UPDATE_USER);
	}

	public void requestUpdateCommonData() {
		mTaskHandler.sendEmptyMessage(TaskHandler.MESSAGE_START_FETCH_DATA);
	}

	public void synchroTemperatureData() {
		mTaskHandler
				.sendEmptyMessage(TaskHandler.MESSAGE_START_UPLOAD_TEMPERATURE);
	}

	public void requestMonthStatisticsData() {
		mTaskHandler
				.sendEmptyMessage(TaskHandler.MESSAGE_START_FETCH_STATISTICS_DATA);
	}

	public BitmapLruCache getBitmapCache() {
		return mCache;
	}

	public long getTimeStampOffset() {
		return timeStampOffset;
	}

	public void setTimeStampOffset(long timeStampOffset) {
		this.timeStampOffset = timeStampOffset;
	}

	public NetWorkManager getNetWorKManager() {
		return mNetWorkManager;
	}

	public void setNetWorKManager(NetWorkManager NetWorKManager) {
		this.mNetWorkManager = NetWorKManager;
	}

	private void loadFangXinBaoApi() {
		String domain = getResources().getString(R.string.mDomain);
		mNetWorkManager = new NetWorkManager(FangXinBaoHttpApi.createHttpApi(
				domain, this, false));

		LoggerTool.d(TAG, "loadCredentials()");
		String phoneNumber = mPrefs.getString(Preferences.PREFERENCE_LOGIN,
				null);
		String password = mPrefs.getString(Preferences.PREFERENCE_PASSWORD,
				null);
		mNetWorkManager.setCredentials(phoneNumber, password);
	}

	private void loadResourceManagers() {
		
		try {

			if (DEBUG)
				Log.d(TAG, "Attempting to load BitmapLruCache.");
			
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {
				cacheLocation = new File(
						Environment.getExternalStorageDirectory()
								+ DataConstants.FILE_CACHE_PATH);
			} else {
				cacheLocation = new File(getFilesDir()
						+ DataConstants.FILE_CACHE_PATH);
			}
			cacheLocation.mkdirs();

			BitmapLruCache.Builder builder = new BitmapLruCache.Builder(this);
			builder.setMemoryCacheEnabled(true)
					.setMemoryCacheMaxSizeUsingHeapSize();
			builder.setDiskCacheEnabled(true).setDiskCacheLocation(
					cacheLocation);
			mCache = builder.build();
		} catch (IllegalStateException e) {

			LoggerTool.d(TAG,
					"Falling back to NullDiskCache for RemoteResourceManager");
		}
	}

	private enum PeddingMsgType {
		NEW_ASYN_MESSAGE, NEW_HANDLER, REMOVE_HANDLER;
	}

	public static FangXinBaoApplication getApplication(Context context) {
		return (FangXinBaoApplication) context.getApplicationContext();
	}

	public File getCacheLocation() {
		return cacheLocation;
	}

	public void setCacheLocation(File cacheLocation) {
		this.cacheLocation = cacheLocation;
	}

	public SharedPreferences getPrefs() {
		return mPrefs;
	}

	public void setPrefs(SharedPreferences Prefs) {
		this.mPrefs = Prefs;
	}

	/**
	 * 发送异步消息
	 * 
	 * expireTime 几秒以后失效
	 * */
	public static synchronized void sendAsynMessage(Message msg, String flag,
			int expireTime) {
		checkPeddingMessage(PeddingMsgType.NEW_ASYN_MESSAGE, "");
		Handler handler = mActivityHandlerPool.get(flag);
		if (null != handler) {
			handler.sendMessage(msg);
		} else {
			AysncMessage asm = new AysncMessage(msg, flag, expireTime);
			mAsynMessagePeddingList.add(asm);
		}
	}

	/*
	 * 每个Activity对应一个Handler，初始化时候入池
	 */
	public static synchronized void pushHandlerPool(String flag, Handler handler) {
		if (mActivityHandlerPool == null) {
			mActivityHandlerPool = new HashMap<String, Handler>();
		}
		mActivityHandlerPool.put(flag, handler);

		// 当有handler加入的时候应该检查它的msg数据
		checkPeddingMessage(PeddingMsgType.NEW_HANDLER, flag);
	}

	/*
	 * Activity销毁的时候，移除
	 */
	public static synchronized void popHandlerPool(String flag) {
		if (mActivityHandlerPool != null
				&& mActivityHandlerPool.containsKey(flag)) {
			mActivityHandlerPool.remove(flag);
		}

		checkPeddingMessage(PeddingMsgType.REMOVE_HANDLER, flag);
	}

	/**
	 * 检查过期的message
	 * 
	 * NEW_ASYN_MESSAGE newMessage NEW_HANDLER new Handler REMOVE_HANDLER remove
	 * Handler
	 * */
	private static void checkPeddingMessage(PeddingMsgType state, String flag) {

		if (mAsynMessagePeddingList.size() > 0) {
			long currrentClock = SystemClock.elapsedRealtime();
			ArrayList<AysncMessage> removed = new ArrayList<AysncMessage>();
			for (AysncMessage msg : mAsynMessagePeddingList) {
				if (msg.expireTime < currrentClock) {
					removed.add(msg);
					continue;
				}

				switch (state) {
				case NEW_ASYN_MESSAGE:
					break;
				case NEW_HANDLER:
					if (flag.equals(msg.flag)) {
						removed.add(msg);
						Handler handler = mActivityHandlerPool.get(flag);
						if (null != handler) {
							handler.sendMessage(msg.msg);
						}
					}
					break;
				case REMOVE_HANDLER:
					if (flag.equals(msg.flag)) {
						removed.add(msg);
					}
					break;
				}
			}

			if (removed.size() > 0) {
				mAsynMessagePeddingList.removeAll(removed);
			}

		}
	}

	/*
	 * 通知所有Handler处理
	 */
	public static void notifyHandlerPool(int what, Bundle data) {
		if (mActivityHandlerPool == null) {
			Log.d(TAG, "没有Handler可以接受消息, what:" + what);
			return;
		}
		for (Handler handler : mActivityHandlerPool.values()) {
			Message msg = Message.obtain();
			msg.what = what;
			msg.setData(data);
			handler.sendMessageDelayed(msg, 0);
		}
	}

	public static void notifyHandlers(String[] activityNames, int what,
			Bundle data) {
		for (String flag : activityNames) {
			Message msg = Message.obtain();
			msg.what = what;
			msg.setData(data);
			if (mActivityHandlerPool != null
					&& mActivityHandlerPool.containsKey(flag)) {
				mActivityHandlerPool.get(flag).sendMessageDelayed(msg, 0);
			}
		}
	}

	private static class AysncMessage {
		Message msg;
		String flag;
		long expireTime;

		AysncMessage(Message msg, String flag, long expireTime) {
			this.msg = msg;
			this.flag = flag;
			this.expireTime = SystemClock.elapsedRealtime() + expireTime;
		}
	}

	private class TaskHandler extends Handler {

		private static final int MESSAGE_UPDATE_USER = 1;
		private static final int MESSAGE_START_SERVICE = 2;
		private static final int MESSAGE_START_FETCH_DATA = 3;
		private static final int MESSAGE_START_UPLOAD_TEMPERATURE = 4;
		private static final int MESSAGE_START_FETCH_STATISTICS_DATA = 5;

		public TaskHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (DEBUG)
				Log.d(TAG, "handleMessage: " + msg.what);

			switch (msg.what) {
			case MESSAGE_UPDATE_USER:
				try {
					// Update user info
					Log.d(TAG, "Updating user.");

					mStateHolder.startFetchUserInfoTask();
				} catch (Exception e) {
					if (DEBUG)
						Log.d(TAG, "FangxinbaoException", e);
				}
				return;

			case MESSAGE_START_SERVICE:
				return;
			case MESSAGE_START_FETCH_DATA:

				mStateHolder.startFetchCommonDataTask();
				return;
			case MESSAGE_START_UPLOAD_TEMPERATURE:
				mStateHolder.startUploadTemperatureTask();
				return;
			case MESSAGE_START_FETCH_STATISTICS_DATA:
				mStateHolder.startFetchMonthStatisticsDataTask();
				return;

			}

		}
	}

	/**
	 * 获取用户信息
	 * 
	 * @author supersky
	 * 
	 */
	private class FetchUserInfoTask extends
			AsyncTaskWithLoadingDialog<Void, Void, User> {

		public FetchUserInfoTask() {
			super(null, false);

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

		@Override
		protected User doInBackground(Void... params) {

			return mNetWorkManager.user();
		}

		@Override
		protected void onPostExecute(User result) {
			super.onPostExecute(result);
			mStateHolder.cancelFetchUserInfoTask();
			if (result == null) {

			} else {
				if (result.code == 1) {
					LoggerTool.d(TAG, "setting user info.");
					Preferences.storeUser(FangXinBaoApplication.this, result);
					FangXinBaoApplication.notifyHandlers(
							new String[] { UserMainActivity.class.getName() },
							CommonAction.ACTIVITY_USERMAN_UPDATE_USER_INFO,
							null);
				} else {

				}
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mStateHolder.cancelFetchUserInfoTask();
		}
	}

	/**
	 * 获取统计数据
	 * 
	 * @author 
	 * 
	 */
	private class FetchMonthStatisticsDataTask extends
			AsyncTaskWithLoadingDialog<Void, Void, MonthStatisticsData> {

		public FetchMonthStatisticsDataTask() {
			super(null, false);

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

		@Override
		protected MonthStatisticsData doInBackground(Void... params) {
			String updateDate = Preferences
					.getSynchronousDate(FangXinBaoApplication.this);
			String version = Preferences
					.getSynchronousVersion(FangXinBaoApplication.this);
			// 判断是否需要同步数据
			if (TextUtils.isEmpty(updateDate)
					|| VeDate.getBetweenDay(VeDate.strToDate(updateDate),
							new Date()) >= 1) {
				return mNetWorkManager.fetchMonthStatisticsData(version,
						String.valueOf(VeDate.getDataStamp(updateDate)));
			}
			return null;
		}

		@Override
		protected void onPostExecute(MonthStatisticsData result) {
			super.onPostExecute(result);
			mStateHolder.cancelFetchMonthStatisticsDataTask();
			sendBroadcast(new Intent(UserMainActivity.ACTION_UPDATE_SUCCESS));
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mStateHolder.cancelFetchMonthStatisticsDataTask();
		}
	}

	/**
	 * 获取基础数据
	 * 
	 * @author 
	 * 
	 */
	private class FetchCommonDataTask extends
			AsyncTaskWithLoadingDialog<Void, Void, CommonData> {

		public FetchCommonDataTask() {
			super(null, false);

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

		@Override
		protected CommonData doInBackground(Void... params) {
			return mNetWorkManager.fetchCommonData();
		}

		@Override
		protected void onPostExecute(CommonData result) {
			super.onPostExecute(result);
			mStateHolder.cancelFetchCommonDataTask();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mStateHolder.cancelFetchCommonDataTask();
		}
	}

	private class StateHolder {

		private FetchCommonDataTask mFetchCommonDataTask; // 得到基础数据
		boolean isFetchCommonDataTaskRunning = false;
		private FetchUserInfoTask mFetchUserInfoTask; // 得到用户信息
		boolean isFetchUserInfoTaskRunning = false;
		private UploadTemperatureTask mUploadTemperatureTask; //
		boolean isUploadTemperatureTaskRunning = false;
		FetchMonthStatisticsDataTask mFetchMonthStatisticsDataTask;
		boolean isFetchMonthStatisticsDataTaskRunning = false;

		public StateHolder() {

		}

		public void startUploadTemperatureTask() {
			if (!isUploadTemperatureTaskRunning) {
				isUploadTemperatureTaskRunning = true;
				mUploadTemperatureTask = new UploadTemperatureTask(
						FangXinBaoApplication.this, false);
				mUploadTemperatureTask.safeExecute();
				mUploadTemperatureTask
						.setOnFinishedListener(new OnFinishedListener<Void>() {
							@Override
							public void onFininshed(Void result) {
								// TODO Auto-generated method stub
								cancelUploadTemperatureTask();
							}
						});
			}
		}

		public void cancelUploadTemperatureTask() {
			if (mUploadTemperatureTask != null) {
				mUploadTemperatureTask.cancel(true);
				mUploadTemperatureTask = null;
			}
			isUploadTemperatureTaskRunning = false;

		}

		public void startFetchMonthStatisticsDataTask() {
			if (!isFetchMonthStatisticsDataTaskRunning) {
				isFetchMonthStatisticsDataTaskRunning = true;
				mFetchMonthStatisticsDataTask = new FetchMonthStatisticsDataTask();
				mFetchMonthStatisticsDataTask.safeExecute();
			}
		}

		public void cancelFetchMonthStatisticsDataTask() {
			if (mFetchMonthStatisticsDataTask != null) {
				mFetchMonthStatisticsDataTask.cancel(true);
				mFetchMonthStatisticsDataTask = null;
			}
			isFetchMonthStatisticsDataTaskRunning = false;

		}

		public void startFetchCommonDataTask() {
			if (!isFetchCommonDataTaskRunning) {
				isFetchCommonDataTaskRunning = true;
				mFetchCommonDataTask = new FetchCommonDataTask();
				mFetchCommonDataTask.safeExecute();
			}
		}

		public void cancelFetchCommonDataTask() {
			if (mFetchCommonDataTask != null) {
				mFetchCommonDataTask.cancel(true);
				mFetchCommonDataTask = null;
			}
			isFetchCommonDataTaskRunning = false;

		}

		public void startFetchUserInfoTask() {
			if (!isFetchUserInfoTaskRunning) {
				isFetchUserInfoTaskRunning = true;
				mFetchUserInfoTask = new FetchUserInfoTask();
				mFetchUserInfoTask.safeExecute();
			}
		}

		public void cancelFetchUserInfoTask() {
			if (mFetchUserInfoTask != null) {
				mFetchUserInfoTask.cancel(true);
				mFetchUserInfoTask = null;
			}
			isFetchUserInfoTaskRunning = false;

		}

		public void cancelAlltasks() {

			cancelFetchCommonDataTask();
			cancelFetchUserInfoTask();
		}
	}
}
