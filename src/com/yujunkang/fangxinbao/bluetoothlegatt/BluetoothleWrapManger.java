package com.yujunkang.fangxinbao.bluetoothlegatt;

import java.util.List;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.SettingActivity;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.model.BluetoothDeviceInfo;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 
 * @date 2014-8-1
 * @author xieb
 * 
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothleWrapManger {
	private static final String TAG = "BluetoothleWrapManger";
	// Stops scanning after 10 seconds.
	public static final long SCAN_PERIOD = 5000;

	private static BluetoothleWrapManger _instance;
	private BluetoothLeService _service;
	private BluetoothGattService _batteryService;
	private BluetoothGattService _HTSService;
	private BluetoothGattCharacteristic _batteryReadCharacteristic;
	private BluetoothGattCharacteristic _HTSIndicateCharacteristic;
	private BluetoothGattCharacteristic _HTSReadCharacteristic;
	private BluetoothGattCharacteristic _HTSWriteCharacteristic;
	private BluetoothGattCharacteristic _HTSResponseCharacteristic;
	private Context mContext;
	private Handler mHandler;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothManager mBluetoothManager;
	private LeScanCallback mLeScanCallback;
	private boolean mPerformScanPeriod = false;
	private String mActivityName = null;
	private Runnable stopScanRunnable = new Runnable() {
		@Override
		public void run() {
			if (mPerformScanPeriod && !TextUtils.isEmpty(mActivityName)) {
				FangXinBaoApplication.notifyHandlers(
						new String[] { mActivityName },
						DataConstants.CommonAction.ACTION_SCAN_TIMEOUT, null);
			}
			mBluetoothAdapter.stopLeScan(mLeScanCallback);

		}
	};

	public synchronized static BluetoothleWrapManger getInstance(Context context) {
		if (_instance == null) {
			_instance = new BluetoothleWrapManger(context);
		}
		return _instance;
	}

	BluetoothleWrapManger(Context context) {
		mContext = context;
		if (mContext == null) {
			return;
		}
		mBluetoothManager = (BluetoothManager) context
				.getSystemService(Context.BLUETOOTH_SERVICE);

	}

	public void initBluetoothService(BluetoothLeService service) {
		_service = service;
		initGattService();
	}

	public void readBatteryPower() {
		if (_batteryReadCharacteristic != null) {
			final int charaProp = _batteryReadCharacteristic.getProperties();
			if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
				_service.readCharacteristic(_batteryReadCharacteristic);
			}
			if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
				_service.setCharacteristicNotification(
						_batteryReadCharacteristic, true);
			}
		}
	}

	public void startReadTemperature() {
		indicateHTS(true);
	}

	public void stopReadTemperature() {
		indicateHTS(false);
	}

	private void indicateHTS(boolean notify) {
		if (_HTSIndicateCharacteristic != null) {
			final int charaProp = _HTSIndicateCharacteristic.getProperties();
			if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
				_service.setCharacteristicNotification(
						_HTSIndicateCharacteristic, notify);
			}
		}
	}

	public void prepareBluetoothForWrite() {
		if (_HTSWriteCharacteristic != null&&_HTSResponseCharacteristic!=null) {
			int charaProp = _HTSWriteCharacteristic.getProperties();
			if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
				_service.setCharacteristicNotification(
						_HTSWriteCharacteristic, true);
			}
//			charaProp = _HTSResponseCharacteristic.getProperties();
//			if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//				_service.setCharacteristicNotification(
//						_HTSResponseCharacteristic, true);
//			}
		}
	}

	public void enableBluetoothForResponse()
	{
		if (_HTSWriteCharacteristic != null&&_HTSResponseCharacteristic!=null) {
			int charaProp = _HTSResponseCharacteristic.getProperties();
			if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
				_service.setCharacteristicNotification(
						_HTSResponseCharacteristic, true);
			}
		}
	}
	
	public void resetBluetooth()
	{
		
	}
	
	public void reset() {
		// _service = null;
		_batteryService = null;
		_HTSService = null;
		_batteryReadCharacteristic = null;
		_HTSIndicateCharacteristic = null;
		_HTSReadCharacteristic = null;
		_HTSWriteCharacteristic = null;
		_HTSResponseCharacteristic = null;
	}

	private void initGattService() {
		if (_service != null) {
			List<BluetoothGattService> gattServices = _service
					.getSupportedGattServices();
			if (gattServices == null) {
				return;
			}
			String uuid = null;
			for (BluetoothGattService gattService : gattServices) {
				uuid = gattService.getUuid().toString();
				if (uuid.equals(BluetoothleGattAttributes.BATTERY_SERVICE)) {
					_batteryService = gattService;

				} else if (uuid
						.equals(BluetoothleGattAttributes.HEALTH_THEMOMETER_SERVICE)) {
					_HTSService = gattService;
				}
				List<BluetoothGattCharacteristic> gattCharacteristics = gattService
						.getCharacteristics();
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					uuid = gattCharacteristic.getUuid().toString();
					if (uuid.equals(BluetoothleGattAttributes.BATTERY_CHARACTERISTIC_CONFIG)) {
						_batteryReadCharacteristic = gattCharacteristic;

					} else if (uuid
							.equals(BluetoothleGattAttributes.HEALTH_THEMOMETER_CHARACTERISTIC_CONFIG)) {
						_HTSIndicateCharacteristic = gattCharacteristic;
					}
					else if (uuid
							.equals(BluetoothleGattAttributes.CLIENT_CHARACTERISTIC_SPS_NOTIFY)) {
						_HTSWriteCharacteristic = gattCharacteristic;
					}
					else if (uuid
							.equals(BluetoothleGattAttributes.CLIENT_CHARACTERISTIC_SPS_RESPONSE)) {
						_HTSResponseCharacteristic = gattCharacteristic;
					}
				}

			}
		}

	}

	public void disconnect() {
		if (_service != null) {
			_service.disconnect();
		}
	}

	public boolean isConnected(BluetoothDeviceInfo deviceinfo) {
		boolean isConnected = false;
		if (_service != null && deviceinfo != null) {
			if (mBluetoothManager != null) {
				List<BluetoothDevice> devices = mBluetoothManager
						.getConnectedDevices(BluetoothProfile.GATT_SERVER);
				if (devices != null
						&& !TextUtils.isEmpty(deviceinfo.getDeviceAddress())) {
					for (BluetoothDevice device : devices) {
						if (device.getAddress().equals(
								deviceinfo.getDeviceAddress())) {
							isConnected = true;
							break;
						}
					}
				}
			}
		}
		deviceinfo.setConnected(isConnected);
		return isConnected;
	}

	public boolean isCurrentConnected(Context context) {
		Group<BluetoothDeviceInfo> deviceList = Preferences
				.getDeviceInfo(context);
		if (_service != null && deviceList != null && deviceList.size() > 0) {
			if (mBluetoothManager != null) {
				List<BluetoothDevice> devices = mBluetoothManager
						.getConnectedDevices(BluetoothProfile.GATT_SERVER);
				if (devices != null) {
					for (BluetoothDevice device : devices) {
						for (BluetoothDeviceInfo deviceInfo : deviceList) {
							if (device.getAddress().equals(
									deviceInfo.getDeviceAddress())) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	public static boolean IsSupportBluetoothleDevice(Context context) {
		// 检查扫描使用蓝牙设备的权限
		if (!context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(context, R.string.ble_not_supported,
					Toast.LENGTH_SHORT).show();
			return false;
		}

		final BluetoothManager bluetoothManager = (BluetoothManager) context
				.getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter Adapter = bluetoothManager.getAdapter();
		// 检查蓝牙功能是否被支持
		if (Adapter == null) {
			Toast.makeText(context, R.string.error_bluetooth_not_supported,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	public static boolean isBluetoothDeviceEnable(Context context) {
		BluetoothManager bluetoothManager = (BluetoothManager) context
				.getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
		return mBluetoothAdapter.isEnabled();
	}

	private void removeScanPeriod() {
		if (mHandler != null && mLeScanCallback != null) {
			mHandler.removeCallbacks(stopScanRunnable);
		}
	}

	public void stopPerformScanPeriod() {
		mPerformScanPeriod = false;
	}

	public void startScanDevice(boolean enable, Handler uiHandler,
			LeScanCallback callback, boolean performPeriod, String activityName) {
		mHandler = uiHandler;
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		mLeScanCallback = callback;
		mActivityName = activityName;
		mPerformScanPeriod = performPeriod;
		removeScanPeriod();
		// 检查蓝牙功能是否被支持
		if (enable) {
			mHandler.postDelayed(stopScanRunnable,
					BluetoothleWrapManger.SCAN_PERIOD);
			mBluetoothAdapter.startLeScan(callback);
		} else {
			mBluetoothAdapter.stopLeScan(callback);
		}
	}
}
