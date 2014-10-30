package com.yujunkang.fangxinbao.activity.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.NotificationManager;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.frontia.api.FrontiaSocialShareContent;
import com.baidu.frontia.api.FrontiaSocialShareContent.FrontiaIMediaObject;
import com.baidu.frontia.api.FrontiaSocialShareContent.FrontiaIQQReqestType;
import com.baidu.frontia.api.FrontiaSocialShareListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateStatus;
import com.umeng.update.UpdateResponse;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.AboutActivity;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.activity.HealthEncyclopediaActivity;
import com.yujunkang.fangxinbao.activity.SettingActivity;
import com.yujunkang.fangxinbao.activity.WebViewActivity;
import com.yujunkang.fangxinbao.activity.base.SinaShareActivity;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.app.FangXinBaoSettings;
import com.yujunkang.fangxinbao.app.TemperatureNotificationBuilder;
import com.yujunkang.fangxinbao.bluetoothlegatt.BluetoothLeService;
import com.yujunkang.fangxinbao.bluetoothlegatt.BluetoothleWrapManger;
import com.yujunkang.fangxinbao.bluetoothlegatt.DeviceScanActivity;
import com.yujunkang.fangxinbao.compare.TemperatureComparator;
import com.yujunkang.fangxinbao.compare.TemperatureTimeComparator;
import com.yujunkang.fangxinbao.control.NoCrashDrawerLayout;
import com.yujunkang.fangxinbao.control.TemperatureCheckRecordView;
import com.yujunkang.fangxinbao.control.TemperatureCheckRecordView.OnTimeupListener;
import com.yujunkang.fangxinbao.control.TemperatureCureView;
import com.yujunkang.fangxinbao.control.dialog.ISimpleDialogListener;
import com.yujunkang.fangxinbao.control.image.RoundedNetWorkImageView;
import com.yujunkang.fangxinbao.database.TemperatureDataHelper;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.method.Method;
import com.yujunkang.fangxinbao.method.VeDate;
import com.yujunkang.fangxinbao.model.Baby;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.TemperatureCommonData;
import com.yujunkang.fangxinbao.model.TemperatureData;
import com.yujunkang.fangxinbao.model.TemperatureStatusDesc;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.parser.BabyRecentTemperatureParser;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedBackgroundListener;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;
import com.yujunkang.fangxinbao.utility.DataConstants.TemperatureLevel;
import com.yujunkang.fangxinbao.utility.FocusChangedUtils;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.OnPageNotifyListener;
import com.yujunkang.fangxinbao.utility.ShareUtils;
import com.yujunkang.fangxinbao.utility.TypeUtils;
import com.yujunkang.fangxinbao.utility.UiUtils;

/**
 * 
 * @date 2014-5-26
 * @author xieb
 * 
 */
public class UserMainActivity extends SinaShareActivity implements
		ISimpleDialogListener, View.OnClickListener {
	private static final String TAG = "UserMainActivity";
	public static final String INTENT_EXTRA_USER = DataConstants.PACKAGE_NAME
			+ ".UserMainActivity.INTENT_EXTRA_USER";
	private static final String INTENT_EXTRA_CONNECT_STATUS = DataConstants.PACKAGE_NAME
			+ ".UserMainActivity.INTENT_EXTRA_CONNECT_STATUS";
	private static final String INTENT_EXTRA_DEVICE_ADDRESS = DataConstants.PACKAGE_NAME
			+ ".UserMainActivity.INTENT_EXTRA_DEVICE_ADDRESS";
	public static final String ACTION_UPDATE_SUCCESS = DataConstants.PACKAGE_NAME
			+ ".UserMainActivity.ACTION_UPDATE_SUCCESS";

	private static final int ACTIVITY_REQUEST_BABYINFO = 1;
	private static final int ACTIVITY_REQUEST_CONNECT_DEVICE = 2;
	private static final int DIALOG_DISCONNECTED_CODE = 3;
	private static final int ALARM_REPEAT_TIMES = 4;
	private static final int DIALOG_CLOSE_ACTIVITY_CODE = 200;
	/**
	 * 控件
	 */
	private View btn_account;
	private TextView btn_health;
	private TextView btn_health_history;
	private TextView btn_health_info;
	private TextView btn_settings;
	private TextView btn_about;
	private View btn_menu;
	private View btn_share;
	private TextView tv_baby_name;
	private TextView tv_temperature_desc;
	private View rl_title;
	private TemperatureCheckRecordView tv_refresh_record;
	private TextView tv_temperature_status;
	private TemperatureCureView temperature_curveView;
	private RoundedNetWorkImageView iv_babyPhoto;
	private TextView tv_temperature_continued;
	private TextView tv_temperature_max;
	private View lay_cure;
	private View lay_recent;
	private View btn_scan_device;
	private TextView tv_disconnection;
	private TextView tv_temperature;
	private TextView tv_unit;
	private MediaPlayer mCurrentMediaPlayer;
	private View btn_temperature_desc;
	private NoCrashDrawerLayout mDrawerLayout;
	private LinearLayout lay_menu;
	private TextView tv_temperature_label;
	private ImageView iv_main_logo;
	private TextView tv_threedays_temperature;
	private TextView tv_continued_time;
	private TextView tv_max_temperature;
	private TextView tv_temperature_hours;
	private TextView tv_temperature_unit;
	private Dialog mExitDialog;

	private User mUserInfo;
	private boolean hasBaby = false;
	public static Baby mCurrentBaby;
	private StateHolder mStateHolder = new StateHolder();
	private Handler uiHandler = new Handler();
	private float currentTemperature;
	private TemperatureCommonData currentTemperatureStatus;
	private TemperatureStatusDesc currentTemperatureStatusDesc;
	private TemperatureDataHelper DataManager;
	private HashMap<String, String> monthDatas = null;
	private boolean hasNewVersion = false;
	private Bitmap captureScreen;
	private String shareTextContent;
	private Group<TemperatureData> recentData = new Group<TemperatureData>();
	private Group<TemperatureData> recentThreeDaysData = new Group<TemperatureData>();
	/**
	 * 蓝牙相关
	 */
	private String mDeviceAddress;
	private BluetoothLeService mBluetoothLeService;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private boolean mConnected = false;
	private boolean mBindService = false;
	private BluetoothleWrapManger mBluetoothleManger;
	private PowerManager pManager;
	private WakeLock mWakeLock;
	private TemperatureNotificationBuilder mNotification;
	private NotificationManager mNotiManager;
	private float maxTemperature;
	private float minTemperature;
	private int alarmRepeatTimes = 0;
	private boolean isUserDisConnected = false;
	private boolean isBatteryAvailable = true;
	private String currentMaxTemperature;
	private int continueFeverTime = 0;
	private float continueFeverTimeF = 0;
	private int mBatteryRate = 0;

	// 管理服务的生命周期.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBindService = true;
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.isConnected()) {
				if (!mBluetoothLeService.initialize()) {
					LoggerTool.e(TAG, "Unable to initialize Bluetooth");
				} else {
					// 连接蓝牙设备
					mBluetoothLeService.connect(mDeviceAddress);
				}
			} else {
				startReadTemperature();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mConnected = false;
			Bundle data = new Bundle();
			data.putBoolean(SettingActivity.INTENT_EXTRA_SERVICE_STATUS,
					mConnected);
			sendRouteNotificationRoute(
					new String[] { SettingActivity.class.getName() },
					CommonAction.ACTION_SERVICE_STATUS_CHANGE, data);

			mBluetoothLeService = null;
			tv_refresh_record.stopCountDown();
			releaseWakeLock();
			updateConnectionState();
			stopProgressDialog();
		}
	};

	// Handles various events fired by the Service.
	// ACTION_GATT_CONNECTED: connected to a GATT server.
	// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
	// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
	// ACTION_DATA_AVAILABLE: received data from the device. This can be a
	// result of read
	// or notification operations.
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

			}
			if (BluetoothLeService.ACTION_BATTERY_DATA_UNAVAILABLE
					.equals(action)) {
				if (intent
						.hasExtra(BluetoothLeService.INTENT_EXTRA_BATTERY_DATA)) {
					mBatteryRate = intent.getIntExtra(
							BluetoothLeService.INTENT_EXTRA_BATTERY_DATA, 0);
				}
				isBatteryAvailable = false;

			}
			if (BluetoothLeService.ACTION_BATTERY_DATA_AVAILABLE.equals(action)) {
				if (intent
						.hasExtra(BluetoothLeService.INTENT_EXTRA_BATTERY_DATA)) {
					mBatteryRate = intent.getIntExtra(
							BluetoothLeService.INTENT_EXTRA_BATTERY_DATA, 0);
				}
				isBatteryAvailable = true;
			}
			// 断开链接
			else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				mConnected = false;
				LoggerTool.d(TAG, "GATT DISCONNECTED.");
				tv_refresh_record.stopCountDown();
				updateConnectionState();
				stopProgressDialog();
				if (!isBatteryAvailable) {
					FangXinBaoApplication.notifyHandlerPool(
							CommonAction.ALERT_BATTERY, null);
					return;
				}
				if (isUserDisConnected == false && isBatteryAvailable) {
					LoggerTool.d(TAG, "RECONNECTING GATT. ");
					bindBluetoothService();
				}
			} else if (BluetoothLeService.ACTION_READY_FOR_READ.equals(action)) {
				mConnected = true;
				isUserDisConnected = false;
				LoggerTool.d(TAG, "ACTION_READY_FOR_READ.");
				stopProgressDialog();
				acquireWakeLock();
				updateConnectionState();
				tv_refresh_record.stopCountDown();
				tv_refresh_record.startCountDown();
				if (mBluetoothleManger != null) {
					mBluetoothleManger.startReadTemperature();
				}
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				if (intent
						.hasExtra(BluetoothLeService.INTENT_EXTRA_TEMPERATURE_DATA)) {
					float temperature = intent.getFloatExtra(
							BluetoothLeService.INTENT_EXTRA_TEMPERATURE_DATA,
							-1);
					// 过滤下数据
					if (Method.isTemperatureValid(temperature)) {
						currentTemperature = temperature;
						updateTemperatureStatus();
					} else {
						if (FangXinBaoSettings.CODE_DEBUG) {
							currentTemperature = 40f;
							updateTemperatureStatus();
						}
					}
				}

			}
		}
	};

	private final BroadcastReceiver mUpdateDataSuccessReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			// 没办法就在这个界面读统计数据，缓解界面卡
			if (hasBaby) {
				monthDatas = DataManager.getMAXTemperatureDataByMonth(
						mCurrentBaby.getId(), null);
			}
		}
	};

	/**
	 * 关闭侧滑菜单
	 */
	private Runnable closeMenu = new Runnable() {
		@Override
		public void run() {
			mDrawerLayout.closeDrawer(Gravity.LEFT);

		}
	};

	/**
	 * 授权回调
	 */
	private FrontiaSocialShareListener shareListener = new FrontiaSocialShareListener() {

		@Override
		public void onSuccess() {
			LoggerTool.d(TAG, "share success");
		}

		@Override
		public void onFailure(int errCode, String errMsg) {
			LoggerTool.d(TAG, String.format(
					"share errCode : %s ,share errMsg : %s ", errCode, errMsg));
		}

		@Override
		public void onCancel() {
			LoggerTool.d(TAG, "cancel ");
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LoggerTool.d(TAG, "onCreate");
		setContentView(R.layout.user_main_activity);
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		registerReceiver(mUpdateDataSuccessReceiver, new IntentFilter(
				ACTION_UPDATE_SUCCESS));
		if (savedInstanceState != null) {
			mConnected = savedInstanceState
					.getBoolean(INTENT_EXTRA_CONNECT_STATUS);
			if (mConnected) {
				mDeviceAddress = savedInstanceState
						.getString(INTENT_EXTRA_DEVICE_ADDRESS);

			}
		}
		init();
		initControl();
		ensureUi();
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				
				case UpdateStatus.Yes: // has update
					LoggerTool.d(TAG, updateInfo.updateLog);
					updateInfo.updateLog=parserUpdateInfo(updateInfo.updateLog);
					LoggerTool.d(TAG, updateInfo.updateLog);
					UmengUpdateAgent.showUpdateDialog(getSelfContext(),
							updateInfo);
					hasNewVersion = true;
					break;
				case UpdateStatus.No: // has no update

					break;
				case UpdateStatus.NoneWifi: // none wifi

					break;
				case UpdateStatus.Timeout: // time out

					break;
				}
			}
		});
		UmengUpdateAgent.update(this);
	}

	@Override
	protected void initData() {
		super.initData();
		Intent data = getIntent();

		if (data.hasExtra(INTENT_EXTRA_USER)) {
			mUserInfo = (User) data.getParcelableExtra(INTENT_EXTRA_USER);
		} else {
			mUserInfo = Preferences.getUserInfo(getSelfContext());
		}
		if (data.hasExtra(BluetoothLeService.INTENT_EXTRA_DEVICE_ADDRESS)) {
			mDeviceAddress = data
					.getStringExtra(BluetoothLeService.INTENT_EXTRA_DEVICE_ADDRESS);

		}

	}

	private String parserUpdateInfo(String data)
	{
		try {
			JSONObject  update = new JSONObject(data);
			if(Method.IsChinese(getSelfContext()))
			{
				return update.getString("zh");
			}
			else
			{
				return update.getString("en");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return data;
		}
		
	}
	
	/**
	 * 程序被系统杀死的时候
	 * 
	 * @param outState
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		outState.putBoolean(INTENT_EXTRA_CONNECT_STATUS, mConnected);
		outState.putString(INTENT_EXTRA_DEVICE_ADDRESS, mDeviceAddress);

	}

	@SuppressWarnings("unused")
	private void init() {
		TemperatureDataHelper dataHelper = TemperatureDataHelper
				.getDBInstance(getSelfContext());
		mNotification = new TemperatureNotificationBuilder(getSelfContext());
		mNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mBluetoothleManger = BluetoothleWrapManger.getInstance(mApplication);
		DataManager = TemperatureDataHelper.getDBInstance(getSelfContext());

	}

	private void initControl() {
		btn_menu = findViewById(R.id.btn_menu);
		iv_main_logo = (ImageView) findViewById(R.id.iv_main_logo);
		btn_share = findViewById(R.id.btn_share);
		rl_title = findViewById(R.id.rl_title);
		// 温度状态
		tv_temperature_desc = (TextView) findViewById(R.id.tv_temperature_desc);
		tv_temperature_status = (TextView) findViewById(R.id.tv_temperature_status);
		tv_refresh_record = (TemperatureCheckRecordView) findViewById(R.id.tv_refresh_record);
		btn_temperature_desc = findViewById(R.id.btn_temperature_desc);
		tv_temperature = (TextView) findViewById(R.id.tv_temperature);
		tv_unit = (TextView) findViewById(R.id.tv_unit);
		tv_unit.setText(Method.getTemperatureUnit(getSelfContext()));
		// 扫描设备
		btn_scan_device = findViewById(R.id.btn_scan_device);
		//
		tv_disconnection = (TextView) findViewById(R.id.tv_disconnection);
		temperature_curveView = (TemperatureCureView) findViewById(R.id.temperature_curveView);
		// 持续发热时间
		tv_temperature_continued = (TextView) findViewById(R.id.tv_temperature_continued);
		// 最高温度
		tv_temperature_max = (TextView) findViewById(R.id.tv_temperature_max);
		tv_threedays_temperature = (TextView) findViewById(R.id.tv_threedays_temperature);
		tv_continued_time = (TextView) findViewById(R.id.tv_continued_time);
		tv_max_temperature = (TextView) findViewById(R.id.tv_max_temperature);
		tv_temperature_hours = (TextView) findViewById(R.id.tv_temperature_hours);
		tv_temperature_unit = (TextView) findViewById(R.id.tv_temperature_unit);

		lay_cure = findViewById(R.id.lay_cure);
		lay_recent = findViewById(R.id.lay_recent);
		tv_temperature_label = (TextView) findViewById(R.id.tv_temperature_label);
		btn_menu.setOnClickListener(this);

		btn_share.setOnClickListener(this);
		Method.enableView(btn_share);
		btn_scan_device.setOnClickListener(this);
		btn_temperature_desc.setOnClickListener(this);
		FocusChangedUtils.setViewFocusChanged(btn_scan_device);
		initMenuControl();
	}

	/**
	 * 初始化菜单控件
	 */
	private void initMenuControl() {
		lay_menu = (LinearLayout) findViewById(R.id.lay_menu);
		mDrawerLayout = (NoCrashDrawerLayout) findViewById(R.id.drawer_layout);
		btn_account = findViewById(R.id.btn_account);
		iv_babyPhoto = (RoundedNetWorkImageView) findViewById(R.id.iv_baby_photo);
		tv_baby_name = (TextView) findViewById(R.id.tv_baby_name);
		// 宝宝健康
		btn_health = (TextView) findViewById(R.id.btn_health);
		btn_health_history = (TextView) findViewById(R.id.btn_health_history);
		btn_health_info = (TextView) findViewById(R.id.btn_health_info);
		btn_settings = (TextView) findViewById(R.id.btn_settings);
		btn_about = (TextView) findViewById(R.id.btn_about);

		btn_account.setOnClickListener(this);
		btn_health.setOnClickListener(this);
		btn_health_history.setOnClickListener(this);
		btn_health_info.setOnClickListener(this);
		btn_settings.setOnClickListener(this);
		btn_about.setOnClickListener(this);
	}

	private void ensureUi() {
		ensureTitleUi();
		getLastChooseBaby();
		ensureSlidingMenuUi();
		ensureTemperatureStatusUi();
		ensureTemperatureCureUi(true);
		// 有升级提醒的时候会弹dialog，这里请求信息的时候就不弹dialog
		fetchRecentData(false);
		if (!TextUtils.isEmpty(mDeviceAddress)) {
			bindBluetoothService();
		}
		mApplication.requestMonthStatisticsData();
	}

	private void startReadTemperature() {
		mConnected = true;
		Bundle data = new Bundle();
		data.putBoolean(SettingActivity.INTENT_EXTRA_SERVICE_STATUS, mConnected);
		sendRouteNotificationRoute(
				new String[] { SettingActivity.class.getName() },
				CommonAction.ACTION_SERVICE_STATUS_CHANGE, data);
		mBluetoothleManger.reset();
		BluetoothleWrapManger.getInstance(mApplication);
		mBluetoothleManger.initBluetoothService(mBluetoothLeService);
		acquireWakeLock();
		updateConnectionState();
		tv_refresh_record.stopCountDown();
		tv_refresh_record.startCountDown();
		if (mBluetoothleManger != null) {
			mBluetoothleManger.startReadTemperature();
		}
	}

	private void reset() {

		if (mConnected) {
			tv_refresh_record.resetCountDown();
			tv_refresh_record.startCountDown();
		}
		currentMaxTemperature = null;
		continueFeverTime = 0;
		continueFeverTimeF = 0;
	}

	private void changeSettingButtonState(int buttonId) {
		int childCount = lay_menu.getChildCount();
		for (int index = 0; index < childCount; index++) {
			View childView = lay_menu.getChildAt(index);
			if (childView.getId() != buttonId) {
				childView.setSelected(false);
			} else {
				childView.setSelected(true);
			}
		}
	}

	/**
	 * 初始化菜单控件
	 */
	private void ensureTitleUi() {
		if (Method.IsChinese(getSelfContext())) {
			iv_main_logo.setImageResource(R.drawable.user_main_logo);
		} else {
			iv_main_logo.setImageResource(R.drawable.user_main_logo_en);
		}
		FocusChangedUtils.setViewFocusChanged(btn_menu);

		FocusChangedUtils.setViewFocusChanged(btn_share);
		FocusChangedUtils.expandTouchArea(btn_menu, 30, 10, 30, 10);

		FocusChangedUtils.expandTouchArea(btn_share);
	}

	private void ensureTemperatureStatusUi() {
		tv_refresh_record.setOnTimeupListener(new OnTimeupListener() {
			@Override
			public void onTimeup() {
				// 测定的时间到
				if (Method.isTemperatureValid(currentTemperature)) {
					storeTemperature();
					sendBroadcast(new Intent(
							FangXinBaoApplication.INTENT_ACTION_CHECK_TEMPERATURE));
					onMeasureTemperatureComplete();

				} else {
					tv_refresh_record.startCountDown();
				}

			}
		});
	}

	private void sendNotificationMessage() {
		if (!isForeground) {

			mNotification.setTemperature(currentTemperature);

			mNotiManager
					.notify(TemperatureNotificationBuilder.NOTIFICATION_TYPE_MEASURE_TEMPERATURE,
							TemperatureNotificationBuilder.NOTIFICATION_ID,
							mNotification.create());

		}
	}

	private void storeTemperature() {
		TemperatureDataHelper dataHelper = TemperatureDataHelper
				.getDBInstance(getSelfContext());
		TemperatureData temperatureData = new TemperatureData();
		temperatureData
				.setTime(String.valueOf(System.currentTimeMillis() / 1000));
		temperatureData.setTemperature(String.valueOf(currentTemperature));
		TemperatureCommonData tempStatus = Method.getTemperatureLevelStatus(
				currentTemperature, getSelfContext());
		temperatureData.setTemperatureLevel(tempStatus.getTemperatureLevel());
		// recentThreeDaysData.add(temperatureData);
		dataHelper.insertTemperatureData(mUserInfo.getId(),
				mCurrentBaby.getId(), temperatureData, false);

		try {
			String time = temperatureData.getTime();
			long lcc_time = Long.valueOf(time);
			temperatureData.setTime(VeDate.DateToStr(
					new Date(lcc_time * 1000L).getTime(), "HH:mm"));
			recentData.add(temperatureData);
		} catch (Exception ex) {
			temperatureData.setTime("");
			recentData.add(temperatureData);
		}
	}

	private void getLastChooseBaby() {
		if (mUserInfo != null && mUserInfo.getBaBies() != null
				&& mUserInfo.getBaBies().size() > 0) {
			hasBaby = true;
			Group<Baby> babies = mUserInfo.getBaBies();
			String babyid = Preferences.getLastChooseBabyId(getSelfContext());
			if (!TextUtils.isEmpty(babyid)) {
				for (Baby item : babies) {
					if (item.getId().equals(babyid)) {
						mCurrentBaby = item;
						return;
					}
				}
				mCurrentBaby = mUserInfo.getBaBies().get(0);
			} else {
				mCurrentBaby = mUserInfo.getBaBies().get(0);
			}

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& !mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
			// showOKOrCancelDialog(getString(R.string.exit_app),
			// getString(R.string.dialog_cancel_text),
			// getString(R.string.exit_app_prompt),
			// DIALOG_CLOSE_ACTIVITY_CODE);
			showExitDialog();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ACTIVITY_REQUEST_CONNECT_DEVICE://
			{
				if (data != null) {
					// 链接蓝牙服务器
					mDeviceAddress = data
							.getStringExtra(DeviceScanActivity.INTENT_EXTRA_DEVICE_ADDRESS);
					startProgressDialog(
							getString(R.string.connecting_bluetooth_device_loading),
							true, -1000, false);
					bindBluetoothService();
				}
				break;
			}
			}
		}
	}

	private void showExitDialog() {
		ArrayList<View> menuList = new ArrayList<View>();

		TextView btn_exit = (TextView) LayoutInflater.from(getSelfContext())
				.inflate(R.layout.dialog_bottom_button_view, null);
		btn_exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mExitDialog != null && mExitDialog.isShowing()) {
					mExitDialog.dismiss();
				}
				finish();
			}
		});
		btn_exit.setText(getString(R.string.exit_app));

		menuList.add(btn_exit);
		TextView btn_cancel = (TextView) LayoutInflater.from(getSelfContext())
				.inflate(R.layout.dialog_bottom_button_view, null);
		btn_cancel.setText(getString(R.string.dialog_cancel_text));
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mExitDialog != null && mExitDialog.isShowing()) {
					mExitDialog.dismiss();
				}

			}
		});
		menuList.add(btn_cancel);
		mExitDialog = mDialog.popButtonListDialogMenu(menuList);
	}

	/**
	 * 侧滑菜单
	 */
	private void ensureSlidingMenuUi() {
		if (hasBaby) {
			iv_babyPhoto.loadImage(mCurrentBaby.getPhoto(), false, null);
			tv_baby_name.setText(mCurrentBaby.getNickname());
		}

	}

	private void ensureTemperatureCureUi(boolean showLastTemperature) {
		if (recentData != null) {
			// 不显示最新温度的描述
			lay_recent.setVisibility(View.VISIBLE);

			lay_cure.setVisibility(View.VISIBLE);
			temperature_curveView.setVisibility(View.VISIBLE);
			if (showLastTemperature) {
				temperature_curveView.showLastTemperatureData(recentData);
			} else {
				temperature_curveView.initData(recentData);
			}
			// 持续时间

			if (continueFeverTimeF > 0) {
				BigDecimal decimal = new BigDecimal(continueFeverTimeF);
				tv_temperature_continued.setText(String.valueOf(decimal
						.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue()));
			} else {
				tv_temperature_continued.setText("-  -");
			}
			tv_temperature_unit.setText(Method
					.getTemperatureUnit(getSelfContext()));
			// 最高温度
			if (!TextUtils.isEmpty(currentMaxTemperature)) {
				tv_temperature_max.setText(TypeUtils.getTemperatureScaleValue(
						1, currentMaxTemperature, getSelfContext()));
			} else {
				tv_temperature_max.setText("-  -");
			}
		}

	}

	private void bindBluetoothService() {
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		if (mBindService == false) {
			bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		}
		if (mBluetoothLeService != null) {
			if (mBluetoothLeService.isConnected()) {
				startReadTemperature();
				return;
			}
			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
			LoggerTool.d(TAG, "Connect request result=" + result);
		}
	}

	/**
	 * 获取最新的温度数据
	 */
	private void fetchRecentData(boolean showDialog) {
		FangXinBaoAsyncTask<Group<TemperatureData>> mTask;
		if (showDialog) {
			mTask = FangXinBaoAsyncTask.createInstance(getSelfContext(),
					UrlManager.URL_FETCH_RECENT_TEMPERATURE_DATA,
					new BabyRecentTemperatureParser(),
					getString(R.string.loading));
		} else {
			mTask = FangXinBaoAsyncTask.createInstance(getSelfContext(),
					UrlManager.URL_FETCH_RECENT_TEMPERATURE_DATA,
					new BabyRecentTemperatureParser(), false);
		}
		mTask.putParameter("bbid", mCurrentBaby.getId());
		mTask.setOnFinishedBackgroundListener(new OnFinishedBackgroundListener<Group<TemperatureData>>() {
			@Override
			public void onFinishedBackground(Group<TemperatureData> result) {
				recentThreeDaysData = DataManager
						.queryRecentThreeDaysTemperatureData(getSelfContext(),
								mCurrentBaby.getId());

			}
		});
		mTask.setOnFinishedListener(new OnFinishedListener<Group<TemperatureData>>() {
			@Override
			public void onFininshed(Group<TemperatureData> result) {
				recentData.clear();
				currentMaxTemperature = null;
				continueFeverTime = 0;
				if (result == null) {
					ensureTemperatureCureUi(false);
				} else {
					if (result.code == 1) {
						recentData.addAll(result);
						resolveTemperatureData();
						ensureTemperatureCureUi(true);
					} else {
						UiUtils.showAlertDialog(result.desc, getSelfContext());
					}
				}
			}
		});
		mTask.safeExecute();
		putAsyncTask(mTask);
	}

	private void resolveTemperatureData() {
		if (recentThreeDaysData.size() > 0) {
			Collections.sort(recentThreeDaysData, new TemperatureComparator());
			currentMaxTemperature = recentThreeDaysData.get(
					recentThreeDaysData.size() - 1).getTemperature();
			Collections.sort(recentThreeDaysData,
					new TemperatureTimeComparator());
			int feverTime = -1;
			int notFeverTime = -1;
			int count = recentThreeDaysData.size();

			try {
				for (int index = count - 1; index >= 0; index--) {
					TemperatureData item = recentThreeDaysData.get(index);
					TemperatureLevel level = item.getTemperatureLevel();
					if (level != null
							&& (level == TemperatureLevel.FEVER || level == TemperatureLevel.HIGH_FEVER)) {
						if (feverTime == -1) {
							feverTime = Integer.parseInt(item.getTime());
						}

					} else {
						notFeverTime = Integer.parseInt(item.getTime());
						if (feverTime != -1 && notFeverTime != -1) {
							continueFeverTime = continueFeverTime
									+ (feverTime - notFeverTime);
							feverTime = -1;
							notFeverTime = -1;
						}
					}
				}
				continueFeverTimeF = continueFeverTime / 3600f;
			} catch (Exception ex) {
				LoggerTool.d(TAG, ex.getMessage());
			}

		}
	}

	private void updateConnectionState() {
		if (mConnected) {
			tv_disconnection.setVisibility(View.GONE);
			tv_refresh_record.setVisibility(View.VISIBLE);
		} else {
			tv_disconnection.setVisibility(View.VISIBLE);
			tv_refresh_record.setVisibility(View.GONE);
		}
	}

	private void changeLanguage() {
		if (Method.IsChinese(getSelfContext())) {
			iv_main_logo.setImageResource(R.drawable.user_main_logo);
		} else {
			iv_main_logo.setImageResource(R.drawable.user_main_logo_en);
		}
		btn_health.setText(getString(R.string.settings_health));
		btn_health_info.setText(getString(R.string.settings_health_info));
		btn_health_history.setText(getString(R.string.settings_health_history));
		btn_about.setText(getString(R.string.settings_about));
		btn_settings.setText(getString(R.string.settings_preferences));
		tv_temperature_label
				.setText(getString(R.string.onedays_temperature_info));
		tv_disconnection.setText(getString(R.string.reconnection_device));
		tv_threedays_temperature
				.setText(getString(R.string.threedays_temperature_info));
		tv_continued_time.setText(getString(R.string.continued_time));
		tv_max_temperature.setText(getString(R.string.max_temperature));
		tv_temperature_hours.setText(getString(R.string.hours));
		updateTemperatureStatus();
	}

	private void onMeasureTemperatureComplete() {
		updateConnectionState();
		updateTemperatureStatus();
		alertUnusualTemperature();
		sendNotificationMessage();
		tv_refresh_record.startCountDown();
	}

	/**
	 * 更新温度状态ui
	 */
	private void updateTemperatureStatus() {
		if (Method.isTemperatureValid(currentTemperature)) {
			tv_temperature.setText(TypeUtils.getTemperatureScaleValue(1,
					currentTemperature, getSelfContext()));
			currentTemperatureStatus = Method.getTemperatureLevelStatus(
					currentTemperature, getSelfContext());
			if (currentTemperatureStatus != null) {
				Method.enableView(btn_share);
				switch (currentTemperatureStatus.getTemperatureLevel()) {
				case LOW: {
					btn_scan_device
							.setBackgroundResource(R.drawable.temperature_normal_content_bg);
					rl_title.setBackgroundResource(R.drawable.temperature_normal_title_bg);
					break;
				}
				case NORMAL: {
					btn_scan_device
							.setBackgroundResource(R.drawable.temperature_normal_content_bg);
					rl_title.setBackgroundResource(R.drawable.temperature_normal_title_bg);
					break;
				}
				case FEVER: {
					btn_scan_device
							.setBackgroundResource(R.drawable.temperature_middle_content_bg);
					rl_title.setBackgroundResource(R.drawable.temperature_middle_title_bg);
					break;
				}
				case HIGH_FEVER: {
					btn_scan_device
							.setBackgroundResource(R.drawable.temperature_high_content_bg);
					rl_title.setBackgroundResource(R.drawable.temperature_high_title_bg);
					break;
				}
				default:
					break;
				}
				currentTemperatureStatusDesc = Method
						.getTemperatureDescByLocal(currentTemperatureStatus,
								getSelfContext());
				btn_temperature_desc.setVisibility(View.VISIBLE);
				if (currentTemperatureStatusDesc != null) {

					tv_temperature_status.setText(currentTemperatureStatusDesc
							.getShortDesc());
					tv_temperature_desc.setText(currentTemperatureStatusDesc
							.getLongDesc());
				} else {
					tv_temperature_status
							.setText(getString(R.string.temperature_status_unknown));
					tv_temperature_desc.setText("");
				}
			}
		}
	}

	/**
	 * 温度异常报警
	 */
	private void alertUnusualTemperature() {

		if (currentTemperatureStatus != null) {
			String prompt = "";
			switch (currentTemperatureStatus.getTemperatureLevel()) {

			case LOW: {
				btn_scan_device
						.setBackgroundResource(R.drawable.temperature_normal_content_bg);
				rl_title.setBackgroundResource(R.drawable.temperature_normal_title_bg);
				prompt = getString(R.string.prompt_measure_temperature_attention);

				break;
			}
			case NORMAL: {
				btn_scan_device
						.setBackgroundResource(R.drawable.temperature_normal_content_bg);
				rl_title.setBackgroundResource(R.drawable.temperature_normal_title_bg);
				prompt = getString(R.string.prompt_measure_temperature);

				break;
			}
			case FEVER: {
				btn_scan_device
						.setBackgroundResource(R.drawable.temperature_middle_content_bg);
				rl_title.setBackgroundResource(R.drawable.temperature_middle_title_bg);
				prompt = getString(R.string.prompt_measure_temperature_attention);

				break;
			}
			case HIGH_FEVER: {
				btn_scan_device
						.setBackgroundResource(R.drawable.temperature_high_content_bg);
				rl_title.setBackgroundResource(R.drawable.temperature_high_title_bg);
				prompt = getString(R.string.prompt_measure_temperature_attention);

				break;
			}
			default:
				break;
			}
			Bundle data = new Bundle();
			data.putString(FangXinBaoApplication.INTENT_EXTRA_ALERT_MESSAGE,
					prompt);
			sendNotificationBroad(CommonAction.ALERT_MEASURE_TEMPERATURE, data);
			maxTemperature = Preferences.getMaxTemperature(getSelfContext());
			minTemperature = Preferences.getMinTemperature(getSelfContext());
			if (currentTemperature < minTemperature
					|| currentTemperature > maxTemperature) {
				playSound();
			}
		}
	}

	private void startHealthEncyclopediaInfoActivity() {
		Intent intent = new Intent(getSelfContext(),
				HealthEncyclopediaActivity.class);
		startActivity(intent);
	}

	private void startSettingsActivity() {
		Intent intent = new Intent(getSelfContext(), SettingActivity.class);
		intent.putExtra(SettingActivity.INTENT_EXTRA_CONNECTED, mConnected);
		startActivity(intent);
	}

	private void startAboutActivity() {
		Intent intent = new Intent(getSelfContext(), AboutActivity.class);
		intent.putExtra(AboutActivity.INTENT_EXTRA_NEW_VERSION, hasNewVersion);
		startActivity(intent);
	}

	private void startBabyHistoryRecorderActivity() {
		Intent intent = new Intent(getSelfContext(),
				BabyHistoryRecordActivity.class);
		intent.putExtra(BabyHistoryRecordActivity.INTENT_EXTRA_BABY,
				mCurrentBaby);
		intent.putExtra(BabyHistoryRecordActivity.INTENT_EXTRA_MONTH_DATA,
				monthDatas);
		startActivity(intent);
	}

	private void startBabyInfoActivity() {
		Intent intent = new Intent(getSelfContext(), UserBabyInfoActivity.class);
		intent.putExtra(INTENT_EXTRA_USER, mUserInfo);
		intent.putExtra(UserBabyInfoActivity.INTENT_EXTRA_BABY, mCurrentBaby);
		startActivity(intent);
	}

	public OnPageNotifyListener generatePageNotifyListener() {
		return new OnPageNotifyListener() {
			@Override
			public void onNotify(int what, Bundle data) {
				switch (what) {
				case CommonAction.UPDATE_BABY_INFO: {
					// 更新宝宝信息
					if (data != null) {
						Baby baby = (Baby) data
								.getParcelable(UserBabyInfoActivity.INTENT_EXTRA_BABY);
						Group<Baby> babies = mUserInfo.getBaBies();
						for (Baby item : babies) {
							if (item.getId().equals(baby.getId())) {
								try {
									String itemPhoto = item.getPhoto();
									item.setBorn(baby.getBorn());
									item.setNickname(baby.getNickname());
									item.setPhoto(baby.getPhoto());
									item.setSex(baby.getSex());
									// 判断是否需要更新图片
									if (!itemPhoto.equals(baby.getPhoto())) {
										iv_babyPhoto.loadImage(baby.getPhoto(),
												false, null);
									}
									tv_baby_name.setText(baby.getNickname());
								} catch (Exception ex) {

								}
							}

						}
						mCurrentBaby = baby;
					}
					break;
				}
				case CommonAction.ACTIVITY_USERMAN_UPDATE_USER_INFO: {
					// 更新宝宝信息
					mUserInfo = Preferences.getUserInfo(getSelfContext());
					getLastChooseBaby();
					monthDatas = DataManager.getMAXTemperatureDataByMonth(
							mCurrentBaby.getId(), null);
					ensureSlidingMenuUi();
					break;
				}
				case CommonAction.ADD_BABY: {
					// 添加宝宝信息
					mUserInfo = Preferences.getUserInfo(getSelfContext());
					if (data != null) {
						Baby baby = (Baby) data
								.getParcelable(UserBabyInfoActivity.INTENT_EXTRA_BABY);
						hasBaby = true;
						mCurrentBaby = baby;
						monthDatas = DataManager.getMAXTemperatureDataByMonth(
								mCurrentBaby.getId(), null);
						reset();
						temperature_curveView.initData(null);
						ensureSlidingMenuUi();
					}
					break;
				}
				case CommonAction.SWITCH_BABY: {
					if (data != null) {
						Baby baby = (Baby) data
								.getParcelable(UserBabyInfoActivity.INTENT_EXTRA_BABY);
						hasBaby = true;
						mCurrentBaby = baby;
						monthDatas = DataManager.getMAXTemperatureDataByMonth(
								mCurrentBaby.getId(), null);
						reset();
						fetchRecentData(false);
						ensureSlidingMenuUi();
					}
					break;
				}
				case CommonAction.ACTION_BIND_BLUETOOTH_SERVICE: {
					if (data != null) {
						if (!mConnected) {
							mDeviceAddress = data
									.getString(SettingActivity.INTENT_EXTRA_DEVICE_ADDRESS);
							bindBluetoothService();
						}
					}
					break;
				}
				case CommonAction.ACTION_STOP_ALERT: {
					try {
						// 关闭报警声音
						if (mCurrentMediaPlayer != null
								&& mCurrentMediaPlayer.isPlaying()) {
							mCurrentMediaPlayer.stop();
							mCurrentMediaPlayer.release();
						}
					} catch (Exception ex) {

					}
					break;
				}
				case CommonAction.ACTION_REFRESH_RECENT_DATA: {
					continueFeverTime = 0;
					continueFeverTimeF = 0;
					recentThreeDaysData = DataManager
							.queryRecentThreeDaysTemperatureData(
									getSelfContext(), mCurrentBaby.getId());
					resolveTemperatureData();
					ensureTemperatureCureUi(true);
					break;
				}
				case CommonAction.ACTION_CHANGE_LANGUAGE: {
					// startActivity(getIntent());
					changeLanguage();
					break;
				}
				case CommonAction.ACTION_USER_DISCONNECTED: {
					isUserDisConnected = true;
					break;
				}
				case CommonAction.ACTION_CHANGE_TEMPERATURE_TYPE: {
					// 最高温度
					if (!TextUtils.isEmpty(currentMaxTemperature)) {
						tv_temperature_max
								.setText(TypeUtils
										.getTemperatureScaleValue(1,
												currentMaxTemperature,
												getSelfContext()));
					} else {
						tv_temperature_max.setText("-  -");
					}
					tv_temperature_unit.setText(Method
							.getTemperatureUnit(getSelfContext()));
					tv_unit.setText(Method
							.getTemperatureUnit(getSelfContext()));
					temperature_curveView.caculateStartPointX();
					temperature_curveView.invalidate();
					break;
				}
				case CommonAction.ACTION_CHANGE_MEASUREMENT_INTERVAL: {

					if (mConnected) {
						tv_refresh_record.stopCountDown();
						tv_refresh_record.startCountDown();
					}
					break;
				}

				}

			}
		};
	}

	/**
	 * 锁屏控制
	 */
	private void acquireWakeLock() {
		if (mConnected) {
			if (mWakeLock == null) {
				pManager = ((PowerManager) getSystemService(POWER_SERVICE));
				mWakeLock = pManager.newWakeLock(
						PowerManager.SCREEN_BRIGHT_WAKE_LOCK
								| PowerManager.ON_AFTER_RELEASE, TAG);
				mWakeLock.acquire();
			}
		}
	}

	/**
	 * 锁屏控制
	 */
	private void releaseWakeLock() {
		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
		}
		//
		mWakeLock = null;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		acquireWakeLock();
		isForeground = true;
		LoggerTool.d(TAG, "onResume");
		changeSettingButtonState(R.id.btn_health);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		releaseWakeLock();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LoggerTool.d(TAG, "onDestroy");
		mStateHolder.cancelAlltasks();
		if (mCurrentBaby != null) {
			Preferences.storeLastChooseBabyId(getSelfContext(),
					mCurrentBaby.getId());
		}
		unregisterReceiver(mGattUpdateReceiver);
		unregisterReceiver(mUpdateDataSuccessReceiver);
		if (mBindService) {
			unbindService(mServiceConnection);
		}
		mBluetoothLeService = null;
		tv_refresh_record.stopCountDown();
		mNotiManager
				.cancel(TemperatureNotificationBuilder.NOTIFICATION_TYPE_MEASURE_TEMPERATURE,
						TemperatureNotificationBuilder.NOTIFICATION_ID);
		if (captureScreen != null && !captureScreen.isRecycled()) {
			captureScreen.recycle();
		}
	}

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		super.onPositiveButtonClicked(requestCode);
		if (requestCode != ActivityWrapper.DIALOG_CLOSE_ALERT_TEMPERATURE) {
			switch (requestCode) {
			case DIALOG_DISCONNECTED_CODE: {
				isUserDisConnected = true;
				mBluetoothleManger.disconnect();
				break;
			}

			case DIALOG_CLOSE_ACTIVITY_CODE: {
				finish();
				break;
			}
			}
		}

	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {

	}

	private IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_READY_FOR_READ);
		intentFilter
				.addAction(BluetoothLeService.ACTION_BATTERY_DATA_UNAVAILABLE);
		intentFilter
				.addAction(BluetoothLeService.ACTION_BATTERY_DATA_AVAILABLE);
		return intentFilter;
	}

	private void playSound() {
		if (Preferences.IsAlarmEnable(getSelfContext())) {
			alarmRepeatTimes = 0;
			mCurrentMediaPlayer = MediaPlayer.create(getSelfContext(),
					R.raw.auto_alarm);
			mCurrentMediaPlayer
					.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							// alarmRepeatTimes++;
							// if (alarmRepeatTimes < ALARM_REPEAT_TIMES) {
							// mCurrentMediaPlayer.start();
							// } else {
							// try {
							// mCurrentMediaPlayer.stop();
							// mCurrentMediaPlayer.release();
							// } catch (Exception ex) {
							//
							// }
							// }
							try {
								mCurrentMediaPlayer.stop();
								mCurrentMediaPlayer.release();
							} catch (Exception ex) {

							}
						}
					});
			if (null != mCurrentMediaPlayer) {
				mCurrentMediaPlayer.start();
			}
		}
	}

	private class StateHolder {

		public StateHolder() {

		}

		public void cancelAlltasks() {

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_menu: {
			if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
			} else {
				mDrawerLayout.openDrawer(Gravity.LEFT);
			}
			break;
		}
		case R.id.btn_temperature_desc: {
			if (currentTemperatureStatus != null) {

				TemperatureStatusDesc desc = Method.getTemperatureDescByLocal(
						currentTemperatureStatus, getSelfContext());
				Intent intent = new Intent(getSelfContext(),
						WebViewActivity.class);
				intent.putExtra(WebViewActivity.INTENT_EXTRA_URL, desc.getUrl());
				startActivity(intent);
			}
			break;
		}
		case R.id.btn_share: {
			// mBluetoothleManger.disconnect();
			// ShareData share = null;
			// if (FangXinBaoSettings.CODE_DEBUG) {
			// share = new ShareData();
			// share.setWeixinTitle("放心宝");
			// share.setWeixinUrl("www.baidu.com");
			// share.setWeixinMsg("测试");
			// Bitmap imageData = BitmapFactory.decodeResource(
			// getResources(), R.drawable.ic_launcher);
			// share.setWeixinBytes(BitmapUtils.bmpToByteArray(imageData));
			// share.setWeixinApiType(ShareData.WEBPAGE);
			// }
			if (FangXinBaoSettings.CODE_DEBUG) {
				currentTemperatureStatusDesc = FangXinBaoApplication
						.getApplication(getSelfContext()).getTemperatureLevel()
						.get(2).getDesc_zh();
			}
			if (currentTemperatureStatusDesc != null) {
				shareTextContent = getString(R.string.share_user_main,
						currentTemperature,
						currentTemperatureStatusDesc.getShortDesc());
			} else {
				shareTextContent = getString(R.string.app_name);
			}
			// 释放内存
			if (captureScreen != null && !captureScreen.isRecycled()) {
				captureScreen.recycle();
			}
			captureScreen = Method.catchScreen(getSelfContext());
			FrontiaSocialShareContent shareContent = ShareUtils
					.getShareContent(getSelfContext(),
							getString(R.string.app_name), shareTextContent,
							"22222", captureScreen);// 百度分享必须要传ulr，但需求上不需要url，这里写个错的参数
			shareContent.setWXMediaObjectType(FrontiaIMediaObject.TYPE_IMAGE);
			shareContent.setQQRequestType(FrontiaIQQReqestType.TYPE_IMAGE);
			ShareUtils.showShareContent(getSelfContext(), shareContent,
					shareListener, this);
			break;
		}
		case R.id.btn_scan_device: {
			if (!mConnected) {
				Intent intent = new Intent(getSelfContext(),
						DeviceScanActivity.class);
				startActivityForResult(intent, ACTIVITY_REQUEST_CONNECT_DEVICE);
			} else {
				showOKOrCancelDialog(getString(R.string.button_disconnected),
						getString(R.string.button_continue_measure),
						getString(R.string.disconnected_device_prompt),
						DIALOG_DISCONNECTED_CODE);
			}
			break;
		}
		case R.id.btn_account: {
			if (hasBaby) {
				startBabyInfoActivity();
			} else {
				UiUtils.showAlertDialog("", getSelfContext());
			}
			changeSettingButtonState(R.id.btn_account);
			uiHandler.post(closeMenu);
			break;
		}
		case R.id.btn_health: {
			// startHealthEncyclopediaInfoActivity();
			// uiHandler.postDelayed(closeMenu, 500);
			// changeSettingButtonState(R.id.btn_account);
			break;
		}
		case R.id.btn_health_info: {
			startHealthEncyclopediaInfoActivity();
			uiHandler.post(closeMenu);
			changeSettingButtonState(R.id.btn_health_info);
			break;
		}
		case R.id.btn_health_history: {
			startBabyHistoryRecorderActivity();
			uiHandler.postDelayed(closeMenu, 200);
			changeSettingButtonState(R.id.btn_health_history);
			break;
		}
		case R.id.btn_about: {
			startAboutActivity();
			uiHandler.post(closeMenu);
			changeSettingButtonState(R.id.btn_about);
			break;
		}
		case R.id.btn_settings: {
			startSettingsActivity();
			uiHandler.post(closeMenu);
			changeSettingButtonState(R.id.btn_settings);
			break;
		}
		}
	}

	@Override
	public void executeShare() {
		doMultiMessageShare(shareTextContent, captureScreen);

	}
}
