package com.yujunkang.fangxinbao.activity;

import java.util.ArrayList;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.user.ChangePhoneLoginActivity;
import com.yujunkang.fangxinbao.activity.user.EditEmailActivity;
import com.yujunkang.fangxinbao.activity.user.FetchVerifyCodeActivity;
import com.yujunkang.fangxinbao.activity.user.ModifyPasswordActivity;
import com.yujunkang.fangxinbao.activity.user.UserMainActivity;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.bluetoothlegatt.BluetoothLeService;
import com.yujunkang.fangxinbao.bluetoothlegatt.BluetoothleWrapManger;
import com.yujunkang.fangxinbao.bluetoothlegatt.DeviceScanActivity;
import com.yujunkang.fangxinbao.control.PhoneTextView;
import com.yujunkang.fangxinbao.control.TableItemView;
import com.yujunkang.fangxinbao.control.dialog.ISimpleDialogCancelListener;
import com.yujunkang.fangxinbao.control.dialog.ProgressDialogFragment;
import com.yujunkang.fangxinbao.control.dialog.ProgressDialogFragment.ProgressDialogBuilder;
import com.yujunkang.fangxinbao.model.BluetoothDeviceInfo;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.DataConstants.TemperatureType;
import com.yujunkang.fangxinbao.utility.DialogHelper;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.OnPageNotifyListener;
import com.yujunkang.fangxinbao.utility.TypeUtils;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;
import com.yujunkang.fangxinbao.utility.DataConstants.EditEmailLanucherType;
import com.yujunkang.fangxinbao.utility.DataConstants.SettingTemperatureLanucherType;
import com.yujunkang.fangxinbao.widget.adapter.BluetoothDeviceAdapter;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * 
 * @date 2014-6-18
 * @author xieb
 * 
 */
public class SettingActivity extends ActivityWrapper implements
		ISimpleDialogCancelListener {
	private static final String TAG = "SettingActivity";
	public static final String INTENT_EXTRA_CONNECTED = DataConstants.PACKAGE_NAME
			+ ".SettingActivity.INTENT_EXTRA_CONNECTED";
	public static final String INTENT_EXTRA_DEVICE_ADDRESS = DataConstants.PACKAGE_NAME
			+ ".SettingActivity.INTENT_EXTRA_DEVICE_ADDRESS";
	public static final String INTENT_EXTRA_DEVICE_BATTERY = DataConstants.PACKAGE_NAME
			+ ".SettingActivity.INTENT_EXTRA_DEVICE_BATTERY";
	public static final String INTENT_EXTRA_SERVICE_STATUS = DataConstants.PACKAGE_NAME
			+ ".SettingActivity.INTENT_EXTRA_SERVICE_STATUS";
	private static final int REQUEST_ACTIVITY_CODE_SETTING_TEMPERATURE = 1;
	private static final int REQUEST_ACTIVITY_CODE_BINDING_EMAIL = 2;
	private static final int REQUEST_ACTIVITY_CODE_SETTING_LANGUAGE = 3;
	private static final int REQUEST_ACTIVITY_CONNECT_DEVICE = 4;

	private static final int REQUEST_ENABLE_BT = 5;
	private static final int REQUEST_ACTIVITY_MEASUREMENT_PERIOD = 6;
	private static final int DIALOG_CONNECTING_DEVICE_CODE = 5;

	private TableItemView btn_temperature_switch;
	private TableItemView btn_max_temperature;
	private TableItemView btn_min_temperature;
	private View btn_modify_phone;
	private View btn_modify_password;
	private PhoneTextView tv_phone;
	private TextView tv_password;
	// private TableItemView btn_binding_sina;
	private TableItemView btn_email;
	private View btn_modify_language;
	private View btn_logout;
	private View icon_logout;
	private Dialog mTemperatureSwitchDialog;
	private Dialog mDisconnectBluetoothServiceDialog;

	private TableItemView item_battery;
	private ListView mListView;
	private BluetoothDeviceAdapter mAdapter;
	private View mFooterView;
	private View btn_connect_new;
	private ProgressDialogFragment dialogFragment;
	private TextView tv_language;
	private TextView tv_battery_desc;
	private TableItemView btn_temperature_period;
	private Switch switch_alarm;

	/**
 * 
 */
	private User mUserInfo;
	private SettingTemperatureLanucherType settingTemperatureType;
	private int temperatureType;
	private float highTemperature;
	private float minTemperature;
	private boolean isConnectedDevice = false;
	private Group<BluetoothDeviceInfo> deviceList = null;

	private Handler mHandler = new Handler();
	private BluetoothDeviceInfo selectedDeviceInfo;
	private BluetoothleWrapManger mBluetoothleManger;
	private boolean mScanning = false;
	private int batteryRate = 0;
	private boolean showDisconnectedDialog = true;

	private final BroadcastReceiver mRequestBatteryReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_BATTERY_DATA_AVAILABLE.equals(action)
					|| BluetoothLeService.ACTION_BATTERY_DATA_UNAVAILABLE
							.equals(action)) {
				if (intent
						.hasExtra(BluetoothLeService.INTENT_EXTRA_BATTERY_DATA)) {
					batteryRate = intent.getIntExtra(
							BluetoothLeService.INTENT_EXTRA_BATTERY_DATA, 0);
					ensureBatteryUi();
				}
				if (BluetoothLeService.ACTION_BATTERY_DATA_UNAVAILABLE
						.equals(action)) {
					showDisconnectedDialog = false;
				}
				else
				{
					showDisconnectedDialog = true;
				}
			}
			// 断开链接
			else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				LoggerTool.d(TAG, "GATT DISCONNECTED.");
				isConnectedDevice = false;
				deviceList = Preferences.getDeviceInfo(getSelfContext());
				tv_battery_desc.setVisibility(View.GONE);
				mAdapter.setGroup(deviceList);
				item_battery.setRightText("--%");
				if (showDisconnectedDialog) {
					DialogHelper
							.showSuccessAlertDialogInCenter(
									getString(R.string.disconnected_from_bluetooth_service),
									getSelfContext(), true, -1);
				}
				stopProgressDialog();
			} else if (BluetoothLeService.ACTION_READY_FOR_READ.equals(action)) {
				LoggerTool.d(TAG, "GATT CONNECTED.");
				isConnectedDevice = true;
				showDisconnectedDialog = true;
				stopProgressDialog();
				deviceList = Preferences.getDeviceInfo(getSelfContext());
				mAdapter.setGroup(deviceList);
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						mBluetoothleManger.readBatteryPower();
					}
				}, 1000);

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter
				.addAction(BluetoothLeService.ACTION_BATTERY_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_READY_FOR_READ);
		intentFilter.addAction(BluetoothLeService.ACTION_BATTERY_DATA_UNAVAILABLE);
		
		registerReceiver(mRequestBatteryReceiver, intentFilter);
		if (savedInstanceState != null) {
			batteryRate = savedInstanceState
					.getInt(INTENT_EXTRA_DEVICE_BATTERY);
		}
		init();
		initControl();
		ensureUi();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();
		Intent data = getIntent();
		if (data.hasExtra(INTENT_EXTRA_CONNECTED)) {
			isConnectedDevice = data.getBooleanExtra(INTENT_EXTRA_CONNECTED,
					false);
		}
	}

	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		if (isConnectedDevice) {
			mBluetoothleManger.readBatteryPower();
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isForeground = true;
		tv_language.setText(mApplication.getLocale().getDisplayName());
	}

	private void init() {
		mBluetoothleManger = BluetoothleWrapManger.getInstance(mApplication);
		mUserInfo = Preferences.getUserCommonInfo(getSelfContext());
		// 温度相关
		temperatureType = Preferences.getTemperatureType(getSelfContext());
		highTemperature = Preferences.getMaxTemperature(getSelfContext());
		minTemperature = Preferences.getMinTemperature(getSelfContext());

		deviceList = Preferences.getDeviceInfo(getSelfContext());
	}

	private void initControl() {
		mListView = (ListView) findViewById(R.id.lv_setting);
		mFooterView = LayoutInflater.from(getSelfContext()).inflate(
				R.layout.setting_list_footer_view, null);
		btn_connect_new = mFooterView.findViewById(R.id.btn_connect_new);
		btn_temperature_switch = (TableItemView) mFooterView
				.findViewById(R.id.btn_temperature_switch);
		btn_max_temperature = (TableItemView) mFooterView
				.findViewById(R.id.btn_max_temperature);
		btn_min_temperature = (TableItemView) mFooterView
				.findViewById(R.id.btn_min_temperature);
		item_battery = (TableItemView) mFooterView
				.findViewById(R.id.item_battery);
		btn_modify_phone = mFooterView.findViewById(R.id.btn_modify_phone);
		tv_phone = (PhoneTextView) mFooterView.findViewById(R.id.tv_phone);
		btn_modify_password = mFooterView
				.findViewById(R.id.btn_modify_password);
		tv_password = (TextView) mFooterView.findViewById(R.id.tv_password);
		tv_battery_desc = (TextView) mFooterView
				.findViewById(R.id.tv_battery_desc);
		btn_email = (TableItemView) mFooterView.findViewById(R.id.btn_email);
		btn_modify_language = mFooterView
				.findViewById(R.id.btn_modify_language);
		btn_logout = mFooterView.findViewById(R.id.btn_logout);
		tv_language = (TextView) mFooterView.findViewById(R.id.tv_language);
		btn_temperature_period = (TableItemView) mFooterView
				.findViewById(R.id.btn_temperature_period);
		switch_alarm = (Switch) mFooterView.findViewById(R.id.switch_alarm);
		btn_temperature_switch.setOnClickListener(this);
		btn_max_temperature.setOnClickListener(this);
		btn_min_temperature.setOnClickListener(this);
		btn_modify_phone.setOnClickListener(this);
		btn_modify_password.setOnClickListener(this);
		// btn_binding_sina.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		btn_modify_language.setOnClickListener(this);
		btn_connect_new.setOnClickListener(this);
		btn_temperature_period.setOnClickListener(this);
	}

	private void ensureUi() {
		mAdapter = new BluetoothDeviceAdapter(getSelfContext());
		mListView.addFooterView(mFooterView, null, false);
		mListView.setAdapter(mAdapter);
		mAdapter.setGroup(deviceList);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectedDeviceInfo = (BluetoothDeviceInfo) parent
						.getItemAtPosition(position);
				if (!selectedDeviceInfo.isConnected()) {
					if (mBluetoothleManger.isCurrentConnected(getSelfContext())) {
						sendRouteNotificationRoute(
								new String[] { UserMainActivity.class.getName() },
								CommonAction.ACTION_USER_DISCONNECTED, null);
						mBluetoothleManger.disconnect();
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								prepareScanLeDevice();
							}
						}, 2000);
					} else {
						prepareScanLeDevice();
					}
				} else {
					showDisconnectBluetoothService();
				}
			}
		});
		// 电量
		item_battery.setShowIcon(false);
		// 设置手机号
		String phone = mUserInfo.getPhone();
		if (!TextUtils.isEmpty(phone)) {
			tv_phone.setFilled(true);
			tv_phone.setText(phone);
		} else {
			tv_phone.setText("");
		}
		// 设置密码
		String password = Preferences.getPassword(getSelfContext());
		if (!TextUtils.isEmpty(phone)) {
			tv_password.setText(password);
		}
		// 设置邮箱
		ensureEmailUi();
		if (temperatureType == TemperatureType.Celsius.ordinal()) {
			btn_temperature_switch
					.setRightText(getString(R.string.button_degreecelsius));
		} else {
			btn_temperature_switch
					.setRightText(getString(R.string.button_degreefahrenheit));
		}
		// 报警时间间隔
		int interval = Preferences.getMeasuermentInterval(getSelfContext());
		btn_temperature_period.setRightText(getString(
				R.string.measurement_interval_unit, String.valueOf(interval)));
		switch_alarm.setChecked(Preferences.IsAlarmEnable(getSelfContext()));
		// 报警开关设置
		switch_alarm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				Preferences.setAlarmEnable(getSelfContext(), arg1);
			}
		});
		ensureTemperatureUi();
		ensureBatteryUi();
	}

	private void ensureEmailUi() {
		mUserInfo = Preferences.getUserCommonInfo(getSelfContext());
		String email = mUserInfo.getEmail();
		if (TextUtils.isEmpty(email)) {
			btn_email.setClickable(true);
			btn_email.setOnClickListener(this);
		} else {
			btn_email.setClickable(false);
			btn_email.setShowIcon(false);
			btn_email.setRightText(email);
		}
	}

	private void ensureBatteryUi() {
		if (batteryRate != 0) {
			final TypedArray a = getTheme().obtainStyledAttributes(
					new int[] { R.attr.normalTextColorReferenceValue });

			if (batteryRate < 50) {
				tv_battery_desc.setText(getString(R.string.change_battery));
				tv_battery_desc.setTextColor(Color.RED);
				item_battery.setRightTextColor(Color.RED);
			} else {
				tv_battery_desc.setText(getString(R.string.battery_full));
				tv_battery_desc.setTextColor(a.getColor(0, Color.BLACK));
				item_battery.setRightTextColor(a.getColor(0, Color.BLACK));
			}
			item_battery.setRightText(String.valueOf(batteryRate) + "%");
			tv_battery_desc.setVisibility(View.VISIBLE);
			a.recycle();
		} else {
			item_battery.setRightText("- -%");
			tv_battery_desc.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		if (batteryRate > 0) {
			outState.putInt(INTENT_EXTRA_DEVICE_BATTERY, batteryRate);
		}

	}

	private void ensureTemperatureUi() {
		// 温度相关
		if (highTemperature > 0) {
			btn_max_temperature.setRightText(TypeUtils
					.getTemperatureScaleValueStr(1, highTemperature,
							mApplication.getLocale(), temperatureType,
							getSelfContext()));
		}
		if (minTemperature > 0) {
			btn_min_temperature.setRightText(TypeUtils
					.getTemperatureScaleValueStr(1, minTemperature,
							mApplication.getLocale(), temperatureType,
							getSelfContext()));
		}

	}

	private void startSettingTemperatureActivity(
			SettingTemperatureLanucherType type, float value) {
		settingTemperatureType = type;
		Intent intent = new Intent(getSelfContext(),
				SettingTemperatureActivity.class);
		intent.putExtra(SettingTemperatureActivity.INTENT_EXTRA_LAUNCHERTYPE,
				type);
		intent.putExtra(SettingTemperatureActivity.INTENT_EXTRA_TEMPERATURE,
				value);
		startActivityForResult(intent,
				REQUEST_ACTIVITY_CODE_SETTING_TEMPERATURE);
	}

	private void startSettingLanguageActivity() {

		Intent intent = new Intent(getSelfContext(),
				SettingLanguageActivity.class);

		startActivityForResult(intent, REQUEST_ACTIVITY_CODE_SETTING_LANGUAGE);
	}

	private void startModifyPhoneActivity() {

		Intent intent = new Intent(getSelfContext(),
				ChangePhoneLoginActivity.class);
		startActivity(intent);
	}

	private void startModifPasswordActivity() {

		Intent intent = new Intent(getSelfContext(),
				ModifyPasswordActivity.class);
		startActivity(intent);
	}

	private void startBindEmailActivity() {

		Intent intent = new Intent(getSelfContext(), EditEmailActivity.class);
		intent.putExtra(EditEmailActivity.INTENT_EXTRA_EMAIL_LANUCHER_TYPE,
				EditEmailLanucherType.BINDING);
		startActivityForResult(intent, REQUEST_ACTIVITY_CODE_BINDING_EMAIL);
	}

	private void showTemperatureSwitchMenu() {
		ArrayList<View> menuList = new ArrayList<View>();

		TextView btn_DegreeCelsius = (TextView) LayoutInflater.from(
				getSelfContext()).inflate(R.layout.dialog_bottom_button_view,
				null);
		btn_DegreeCelsius.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				temperatureType = TemperatureType.Celsius.ordinal();
				ensureTemperatureUi();
				// 保存设置
				Preferences.storeTemperatureType(getSelfContext(),
						temperatureType);
				mTemperatureSwitchDialog.dismiss();
				btn_temperature_switch
						.setRightText(getString(R.string.button_degreecelsius));
				sendRouteNotificationRoute(
						new String[] { UserMainActivity.class.getName() },
						CommonAction.ACTION_CHANGE_TEMPERATURE_TYPE, null);
			}
		});
		btn_DegreeCelsius.setText(getString(R.string.button_degreecelsius));

		menuList.add(btn_DegreeCelsius);

		TextView btn_DegreesFahrenheit = (TextView) LayoutInflater.from(
				getSelfContext()).inflate(R.layout.dialog_bottom_button_view,
				null);
		btn_DegreesFahrenheit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				temperatureType = TemperatureType.Fahrenheit.ordinal();
				ensureTemperatureUi();
				// 保存设置
				Preferences.storeTemperatureType(getSelfContext(),
						temperatureType);
				mTemperatureSwitchDialog.dismiss();
				btn_temperature_switch
						.setRightText(getString(R.string.button_degreefahrenheit));
				sendRouteNotificationRoute(
						new String[] { UserMainActivity.class.getName() },
						CommonAction.ACTION_CHANGE_TEMPERATURE_TYPE, null);
			}
		});
		btn_DegreesFahrenheit
				.setText(getString(R.string.button_degreefahrenheit));

		menuList.add(btn_DegreesFahrenheit);

		TextView btn_cancel = (TextView) LayoutInflater.from(getSelfContext())
				.inflate(R.layout.dialog_bottom_button_view, null);
		btn_cancel.setText(getString(R.string.dialog_cancel_text));
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mTemperatureSwitchDialog != null
						&& mTemperatureSwitchDialog.isShowing()) {
					mTemperatureSwitchDialog.dismiss();
				}

			}
		});
		menuList.add(btn_cancel);
		mTemperatureSwitchDialog = mDialog.popButtonListDialogMenu(menuList);
	}

	private void showDisconnectBluetoothService() {
		ArrayList<View> menuList = new ArrayList<View>();

		TextView btn_disconnect = (TextView) LayoutInflater.from(
				getSelfContext()).inflate(R.layout.dialog_bottom_button_view,
				null);
		btn_disconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendRouteNotificationRoute(
						new String[] { UserMainActivity.class.getName() },
						CommonAction.ACTION_USER_DISCONNECTED, null);
				mBluetoothleManger.disconnect();
				if (mDisconnectBluetoothServiceDialog != null
						&& mDisconnectBluetoothServiceDialog.isShowing()) {
					mDisconnectBluetoothServiceDialog.dismiss();
				}
			}
		});
		btn_disconnect.setText(getString(R.string.button_disconnected));
		btn_disconnect.setTextColor(Color.RED);
		menuList.add(btn_disconnect);
		TextView btn_cancel = (TextView) LayoutInflater.from(getSelfContext())
				.inflate(R.layout.dialog_bottom_button_view, null);
		btn_cancel.setText(getString(R.string.dialog_cancel_text));
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mDisconnectBluetoothServiceDialog != null
						&& mDisconnectBluetoothServiceDialog.isShowing()) {
					mDisconnectBluetoothServiceDialog.dismiss();
				}

			}
		});
		menuList.add(btn_cancel);
		mDisconnectBluetoothServiceDialog = mDialog
				.popButtonListDialogMenu(menuList);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_ACTIVITY_CODE_SETTING_TEMPERATURE: {
				if (data != null) {
					float temperatureValue = data
							.getFloatExtra(
									SettingTemperatureActivity.INTENT_EXTRA_TEMPERATURE,
									-1);

					if (settingTemperatureType == SettingTemperatureLanucherType.Max) {
						highTemperature = temperatureValue;
						btn_max_temperature.setRightText(TypeUtils
								.getTemperatureScaleValueStr(1,
										temperatureValue,
										mApplication.getLocale(),
										temperatureType, getSelfContext()));
						Preferences.storeMaxTemperature(getSelfContext(),
								temperatureValue);

					} else if (settingTemperatureType == SettingTemperatureLanucherType.Min) {
						minTemperature = temperatureValue;
						btn_min_temperature.setRightText(TypeUtils
								.getTemperatureScaleValueStr(1,
										temperatureValue,
										mApplication.getLocale(),
										temperatureType, getSelfContext()));
						Preferences.storeMinTemperature(getSelfContext(),
								temperatureValue);
					}
				}
				break;
			}
			case REQUEST_ACTIVITY_CODE_SETTING_LANGUAGE: {
				finish();
				Intent intent = new Intent(getSelfContext(),
						SettingActivity.class);
				intent.putExtra(INTENT_EXTRA_CONNECTED, isConnectedDevice);
				startActivity(intent);

				break;
			}
			case REQUEST_ACTIVITY_CONNECT_DEVICE: {
				if (data != null) {
					// 链接蓝牙服务器
					String deviceAddress = data
							.getStringExtra(DeviceScanActivity.INTENT_EXTRA_DEVICE_ADDRESS);
					Bundle intent = new Bundle();
					intent.putString(INTENT_EXTRA_DEVICE_ADDRESS, deviceAddress);
					sendRouteNotificationRoute(
							new String[] { UserMainActivity.class.getName() },
							CommonAction.ACTION_BIND_BLUETOOTH_SERVICE, intent);
					startProgressDialog(
							getString(R.string.connecting_bluetooth_device_loading),
							true, -1000, false);
				}
				break;
			}

			case REQUEST_ENABLE_BT: {
				scanLeDevice(true);
				break;
			}
			case REQUEST_ACTIVITY_CODE_BINDING_EMAIL: {
				ensureEmailUi();
				break;
			}
			case REQUEST_ACTIVITY_MEASUREMENT_PERIOD: {
				int interval = Preferences
						.getMeasuermentInterval(getSelfContext());
				btn_temperature_period.setRightText(getString(
						R.string.measurement_interval_unit,
						String.valueOf(interval)));
				FangXinBaoApplication.INTERVAL_ACTIVE_DELAY = interval * 60 * 1000;
				sendRouteNotificationRoute(
						new String[] { UserMainActivity.class.getName() },
						CommonAction.ACTION_CHANGE_MEASUREMENT_INTERVAL, null);
				break;
			}

			default:
				//
				break;
			}
		}
	}

	@Override
	public OnPageNotifyListener generatePageNotifyListener() {
		return new OnPageNotifyListener() {
			@Override
			public void onNotify(int what, Bundle data) {
				switch (what) {
				// 修改手机号成功
				case CommonAction.ACTIVITY_MODIFY_PHONE_SUCCESS: {
					DialogHelper.showSuccessAlertDialogInCenter(
							getString(R.string.modify_phone_success_tip),
							getSelfContext(), true, -1);
					if (data != null
							&& data.containsKey(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE)) {
						String phone = data
								.getString(FetchVerifyCodeActivity.INTENT_EXTRA_PHONE);
						tv_phone.setText(phone);
						DialogHelper.showSuccessAlertDialogInCenter(
								getString(R.string.modify_phone_success_tip),
								getSelfContext(), true, -1);
					}

					break;
				}
				case CommonAction.ACTION_SCAN_TIMEOUT: {
					stopProgressDialog();
					showConfirmDialog(getString(R.string.button_confirm),
							getString(R.string.scan_not_found_bluetooth_device));
					break;
				}
				case CommonAction.ACTION_SERVICE_STATUS_CHANGE: {
					if (data != null
							&& data.containsKey(INTENT_EXTRA_SERVICE_STATUS)) {
						isConnectedDevice = data
								.getBoolean(INTENT_EXTRA_SERVICE_STATUS);
					}

					break;
				}
				}

			}
		};
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mRequestBatteryReceiver);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.btn_temperature_period: {
			Intent intent = new Intent(getSelfContext(),
					SettingMeasurementPeriodActivity.class);
			startActivityForResult(intent, REQUEST_ACTIVITY_MEASUREMENT_PERIOD);
			break;
		}
		case R.id.btn_connect_new: {
			selectedDeviceInfo = null;
			prepareScanLeDevice();
			break;
		}

		case R.id.btn_temperature_switch: {
			showTemperatureSwitchMenu();
			break;
		}
		case R.id.btn_max_temperature: {
			startSettingTemperatureActivity(SettingTemperatureLanucherType.Max,
					highTemperature);
			break;
		}
		case R.id.btn_min_temperature: {
			startSettingTemperatureActivity(SettingTemperatureLanucherType.Min,
					minTemperature);
			break;
		}
		case R.id.btn_modify_phone: {
			startModifyPhoneActivity();
			break;
		}
		case R.id.btn_modify_password: {
			startModifPasswordActivity();
			break;
		}
		case R.id.btn_email: {
			startBindEmailActivity();
			break;
		}
		case R.id.btn_modify_language: {
			startSettingLanguageActivity();
			break;
		}
		case R.id.btn_logout:
			sendNotificationBroad(DataConstants.CommonAction.CLOSE_ALL_ACTIVITY);
			Preferences.logoutUser(getSelfContext());
			Preferences.clearSynchronousSetting(getSelfContext());
			Intent intent = new Intent(getSelfContext(), MainActivity.class);
			startActivity(intent);
			break;
		}

	}

	private void prepareScanLeDevice() {
		// 判断是否为连接新设备
		if (selectedDeviceInfo == null) {
			Intent intent = new Intent(getSelfContext(),
					DeviceScanActivity.class);
			startActivityForResult(intent, REQUEST_ACTIVITY_CONNECT_DEVICE);
			return;
		}
		boolean isSupport = BluetoothleWrapManger
				.IsSupportBluetoothleDevice(getSelfContext());
		if (!isSupport) {
			return;
		}
		boolean isEnable = BluetoothleWrapManger
				.isBluetoothDeviceEnable(getSelfContext());
		if (!isEnable) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			return;
		}
		scanLeDevice(true);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void scanLeDevice(final boolean enable) {
		stopProgressDialog();
		if (enable) {
			startProgressDialog(
					getString(R.string.connecting_bluetooth_device_loading),
					true, DIALOG_CONNECTING_DEVICE_CODE, true);
		}
		mScanning = enable;
		mBluetoothleManger.startScanDevice(enable, mHandler, mLeScanCallback,
				enable, SettingActivity.class.getName());
	}

	/**
	 * 扫描蓝牙设备回调
	 */
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			LoggerTool.d(TAG, "mLeScanCallback");
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mScanning) {
						if (selectedDeviceInfo != null
								&& selectedDeviceInfo.getDeviceAddress()
										.equals(device.getAddress())) {
							// 停止扫描
							mScanning = false;
							mBluetoothleManger.startScanDevice(false, mHandler,
									mLeScanCallback, false, null);
							Bundle data = new Bundle();
							data.putString(INTENT_EXTRA_DEVICE_ADDRESS,
									selectedDeviceInfo.getDeviceAddress());
							sendRouteNotificationRoute(
									new String[] { UserMainActivity.class
											.getName() },
									CommonAction.ACTION_BIND_BLUETOOTH_SERVICE,
									data);

						}

					} else {
						stopProgressDialog();
					}
				}
			});
		}

	};

	@Override
	public void onCancelled(int requestCode) {
		if (requestCode == DIALOG_CONNECTING_DEVICE_CODE) {
			// 停止扫描
			mScanning = false;
			mBluetoothleManger.startScanDevice(false, mHandler,
					mLeScanCallback, false, null);
			sendRouteNotificationRoute(
					new String[] { UserMainActivity.class.getName() },
					CommonAction.ACTION_USER_DISCONNECTED, null);
			mBluetoothleManger.disconnect();
			stopProgressDialog();
		}
	}

}
