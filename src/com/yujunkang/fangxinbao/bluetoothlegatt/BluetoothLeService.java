/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yujunkang.fangxinbao.bluetoothlegatt;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.app.FangXinBaoSettings;
import com.yujunkang.fangxinbao.method.Method;
import com.yujunkang.fangxinbao.model.TemperatureCommonData;
import com.yujunkang.fangxinbao.model.TemperatureData;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.TypeUtils;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;
import com.yujunkang.fangxinbao.activity.SelectDeviceActivity;
import com.yujunkang.fangxinbao.activity.UserWearDeviceActivity;
import com.yujunkang.fangxinbao.activity.PrepareConnectDeviceActivity;
import com.yujunkang.fangxinbao.activity.user.UserMainActivity;
import com.yujunkang.fangxinbao.database.TemperatureDataHelper;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
	private final static String TAG = "BluetoothLeService";

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothleWrapManger mBluetoothleWrapManger;

	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;
	private int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;
	private static int MIN_BATTERY_RATE = 35;

	public final static String ACTION_GATT_CONNECTED = DataConstants.PACKAGE_NAME
			+ "BluetoothLeService.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = DataConstants.PACKAGE_NAME
			+ "BluetoothLeService.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = DataConstants.PACKAGE_NAME
			+ "BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_READY_FOR_READ = DataConstants.PACKAGE_NAME
			+ "BluetoothLeService.ACTION_READY_FOR_READ";

	public final static String ACTION_DATA_AVAILABLE = DataConstants.PACKAGE_NAME
			+ "BluetoothLeService.ACTION_DATA_AVAILABLE";
	public final static String ACTION_BATTERY_DATA_AVAILABLE = DataConstants.PACKAGE_NAME
			+ "BluetoothLeService.ACTION_BATTERY_DATA_AVAILABLE";
	public final static String ACTION_WRITE_DATA_AVAILABLE = DataConstants.PACKAGE_NAME
			+ "BluetoothLeService.ACTION_WRITE_DATA_AVAILABLE";
	public final static String ACTION_BATTERY_DATA_UNAVAILABLE = DataConstants.PACKAGE_NAME
			+ "BluetoothLeService.ACTION_BATTERY_UNAVAILABLE";
	public final static String INTENT_EXTRA_BATTERY_DATA = DataConstants.PACKAGE_NAME
			+ "BluetoothLeService.INTENT_EXTRA_DATA";
	public final static String INTENT_EXTRA_TEMPERATURE_DATA = DataConstants.PACKAGE_NAME
			+ "BluetoothLeService.INTENT_EXTRA_TEMPERATURE_DATA";

	public final static String INTENT_EXTRA_DEVICE_ADDRESS = DataConstants.PACKAGE_NAME
			+ "BluetoothLeService.INTENT_EXTRA_DEVICE_ADDRESS";

	public final static UUID UUID_BATTERY_CHARACTERISTIC_CONFIG = UUID
			.fromString(BluetoothleGattAttributes.BATTERY_CHARACTERISTIC_CONFIG);
	public final static UUID UUID_HEALTH_THEMOMETER_CHARACTERISTIC_CONFIG = UUID
			.fromString(BluetoothleGattAttributes.HEALTH_THEMOMETER_CHARACTERISTIC_CONFIG);
	public final static UUID UUID_CLIENT_CHARACTERISTIC_CONFIG = UUID
			.fromString(BluetoothleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG);
	public final static UUID UUID_CLIENT_CHARACTERISTIC_SPS_NOTIFY = UUID
			.fromString(BluetoothleGattAttributes.CLIENT_CHARACTERISTIC_SPS_NOTIFY);
	public final static UUID UUID_CLIENT_CHARACTERISTIC_SPS_RESPONSE = UUID
			.fromString(BluetoothleGattAttributes.CLIENT_CHARACTERISTIC_SPS_RESPONSE);

	private BluetoothGattCharacteristic _HTSWriteCharacteristic;
	private BluetoothGattCharacteristic _HTSResonseCharacteristic;
	private boolean isOnStart = false;
	private boolean isStartReadTemperature = false;
	private TemperatureDataHelper dataHelper;

	// private boolean isEnableNotify_UUID_CLIENT_CHARACTERISTIC_SPS_NOTIFY =
	// false;
	// private boolean isEnableNotify_UUID_CLIENT_CHARACTERISTIC_SPS_RESPONSE =
	// false;

	// Implements callback methods for GATT events that the app cares about. For
	// example,
	// connection change and services discovered.
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction);
				LoggerTool.i(TAG, "Connected to GATT server.");
				// Attempts to discover services after successful connection.
				LoggerTool.i(TAG, "Attempting to start service discovery:"
						+ mBluetoothGatt.discoverServices());

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				// 每次重新连接温度计的时候，先要更新温度计的时间-读取保存的温度记录-删除记录-开始测量温度
				isStartReadTemperature = false;
				LoggerTool.i(TAG, "Disconnected from GATT server.");
				close();
				mBluetoothleWrapManger.reset();
				broadcastUpdate(intentAction);

			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Preferences.storeDeviceInfo(BluetoothLeService.this,
						mBluetoothGatt.getDevice().getName(), mBluetoothGatt
								.getDevice().getAddress());
				// broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
				LoggerTool.w(TAG, "onServicesDiscovered received: " + status);
				mBluetoothleWrapManger.reset();
				mBluetoothleWrapManger
						.initBluetoothService(BluetoothLeService.this);
				// 开始就读取电池的电量
				mBluetoothleWrapManger.readBatteryPower();
			} else {
				LoggerTool.w(TAG, "onServicesDiscovered received: " + status);
				FangXinBaoApplication.notifyHandlers(
						new String[] { DeviceScanActivity.class.getName() },
						CommonAction.ACTION_CONNECT_DEVICE_FAILED, null);
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {

			super.onCharacteristicWrite(gatt, characteristic, status);
			LoggerTool.i(TAG, "onCharacteristicWrite.");
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {

			super.onDescriptorRead(gatt, descriptor, status);
			LoggerTool.i(TAG, "onDescriptorRead.");
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {

			LoggerTool.i(TAG, "onDescriptorWrite.status: " + status);
			UUID characteristicUuid = descriptor.getCharacteristic().getUuid();
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (UUID_CLIENT_CHARACTERISTIC_SPS_NOTIFY
						.equals(characteristicUuid)) {
					_HTSWriteCharacteristic = descriptor.getCharacteristic();

					mBluetoothleWrapManger.enableBluetoothForResponse();
				} else if (UUID_CLIENT_CHARACTERISTIC_SPS_RESPONSE
						.equals(characteristicUuid)) {
					_HTSResonseCharacteristic = descriptor.getCharacteristic();
					// 开始测量温度前更新温度计时间
					setCharacteristicData(_HTSWriteCharacteristic,
							BluetoothleGattAttributes.Cmd_UpdateTime);
				}
			} else {
				// 设置SPS(0xFFB0)服务失败，就直接开始测量温度
				broadcastUpdate(ACTION_READY_FOR_READ);
			}

		}

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {

			super.onReliableWriteCompleted(gatt, status);
			LoggerTool.i(TAG, "onReliableWriteCompleted.");
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {

			super.onReadRemoteRssi(gatt, rssi, status);
			LoggerTool.i(TAG, "onReadRemoteRssi.");
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			LoggerTool.i(TAG, "onCharacteristicRead.");
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(characteristic);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			LoggerTool.i(TAG, "onCharacteristicChanged.");
			broadcastUpdate(characteristic);
		}
	};

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	public boolean isConnected() {
		return mConnectionState == STATE_CONNECTED;
	}

	public void setConnectionState(int mConnectionState) {
		this.mConnectionState = mConnectionState;
	}

	private void broadcastUpdate(
			final BluetoothGattCharacteristic characteristic) {

		Intent intent = null;
		LoggerTool.d(TAG, "uuid : " + characteristic.getUuid().toString());
		if (UUID_BATTERY_CHARACTERISTIC_CONFIG.equals(characteristic.getUuid())) {

			byte[] value = characteristic.getValue();
			if (value != null) {
				try {
					int EnergyRate = characteristic.getIntValue(
							BluetoothGattCharacteristic.FORMAT_UINT8, 0);
					LoggerTool.d(TAG, String.format(
							"Battery Service Energy rate: %d", EnergyRate));
					if (EnergyRate < MIN_BATTERY_RATE) {
						intent = new Intent(ACTION_BATTERY_DATA_UNAVAILABLE);
						intent.putExtra(INTENT_EXTRA_BATTERY_DATA, EnergyRate);
						sendBroadcast(intent);
						disconnect();
					} else {
						intent = new Intent(ACTION_BATTERY_DATA_AVAILABLE);
						intent.putExtra(INTENT_EXTRA_BATTERY_DATA, EnergyRate);
						sendBroadcast(intent);
						if (!isStartReadTemperature) {
							mBluetoothleWrapManger.prepareBluetoothForWrite();
						}
					}

				} catch (Exception ex) {
					LoggerTool.e(TAG, ex.getMessage());
				}

			}

		} else if (UUID_HEALTH_THEMOMETER_CHARACTERISTIC_CONFIG
				.equals(characteristic.getUuid())) {
			isStartReadTemperature = true;
			byte[] data = characteristic.getValue();
			// 解析温度数据
			if (data != null && data.length > 0) {
				try {
					LoggerTool.d(TAG, "data.length : " + data.length);
					int temperature = 0, offset = 1;

					temperature |= (data[offset++] & 0xFF);

					temperature |= ((data[offset++] & 0xFF) << 8);

					temperature |= ((data[offset++] & 0xFF) << 16);
					float realTemperature = (float) (temperature * Math.pow(10,
							(data[offset])));
					LoggerTool.d(TAG, "realTemperature : " + realTemperature);
					intent = new Intent(ACTION_DATA_AVAILABLE);
					if (FangXinBaoSettings.CODE_DEBUG) {
						intent.putExtra(INTENT_EXTRA_TEMPERATURE_DATA,
								realTemperature + 13.0f);
					} else {
						intent.putExtra(INTENT_EXTRA_TEMPERATURE_DATA,
								realTemperature);
					}
					sendBroadcast(intent);
				} catch (Exception ex) {

				}
			}

		} else if (UUID_CLIENT_CHARACTERISTIC_SPS_NOTIFY.equals(characteristic
				.getUuid())) {
			byte[] data = characteristic.getValue();
			handleResponse(data);
		}
		// 读取温度数据
		else if (UUID_CLIENT_CHARACTERISTIC_SPS_RESPONSE.equals(characteristic
				.getUuid())) {
			try {
				byte[] data = characteristic.getValue();
				// 前4位是时间，后4位是温度
				if (data.length == 8) {
					byte[] time = new byte[4];
					System.arraycopy(data, 0, time, 0, 4);
					int timeInteger = TypeUtils.bytesToInt(time, 0);
					LoggerTool.d(TAG, "timeInteger : " + timeInteger);
					User userinfo = Preferences.getUserInfo(this);
					TemperatureData temperatureData = new TemperatureData();
					temperatureData.setTime(String.valueOf(timeInteger));
					long timelong = timeInteger * 1000l;
					LoggerTool.d(TAG, "time : " + timelong);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"MM/dd/yyyy HH:mm:ss");
					// 前面的time是秒数，先乘1000得到毫秒数，再转为java.util.Date类型
					Date dt = new Date(timelong);
					String sDateTime = sdf.format(dt); // 得到精确到秒的表示：08/31/2006
														// 21:08:00
					LoggerTool.d(TAG, "time : " + sDateTime);
					byte[] temperature = new byte[4];

					System.arraycopy(data, 4, temperature, 0, 4);

					int result = 0, offset = 0;

					result |= (temperature[offset++] & 0xFF);

					result |= ((temperature[offset++] & 0xFF) << 8);

					result |= ((temperature[offset++] & 0xFF) << 16);
					float realTemperature = (float) (result * Math.pow(10,
							(temperature[offset])));
					LoggerTool.d(TAG, "data.length : " + data.length);
					LoggerTool.d(TAG, "temperature : " + realTemperature);
					if (Method.isTemperatureValid(realTemperature)) {
						temperatureData.setTemperature(String
								.valueOf(realTemperature));
						dataHelper.insertTemperatureData(userinfo.getId(),
								UserMainActivity.mCurrentBaby.getId(),
								temperatureData, false);
					}
				} else if (data.length == 4) {
					StringBuilder stringBuilder = new StringBuilder(data.length);
					for (byte byteChar : data) {
						stringBuilder.append(byteChar);
					}
					if (stringBuilder.toString().equals("0000")) {
						// 记录读取完,这里只有成功的判断
						setCharacteristicData(_HTSWriteCharacteristic,
								BluetoothleGattAttributes.Cmd_DeleteAllRecord);
						// broadcastUpdate(ACTION_READY_FOR_READ);
					}
				}
				if (data != null && data.length > 0) {

					final StringBuilder stringBuilder = new StringBuilder(
							data.length);
					for (byte byteChar : data)
						stringBuilder.append(String.format("%02X ", byteChar));
					LoggerTool.d(TAG, "result : " + stringBuilder.toString());

				}
			} catch (Exception ex) {

			}
		} else {
			// For all other profiles, writes the data formatted in HEX.
			final byte[] data = characteristic.getValue();
			LoggerTool.d(TAG, "data.length : " + data.length);
			if (data != null && data.length > 0) {
				final StringBuilder stringBuilder = new StringBuilder(
						data.length);
				for (byte byteChar : data)
					stringBuilder.append(String.format("%02X ", byteChar));
			}
		}

	}

	/**
	 * 处理操作蓝牙
	 * 
	 * @param data
	 */
	private void handleResponse(byte[] data) {

		if (data != null && data.length > 0) {
			int len = data.length;
			boolean result = false;
			LoggerTool.d(TAG, "data.length : " + data.length);
			int cmd = (int) data[0];
			if (len >= 2) {
				result = ((int) data[1]) == BluetoothleGattAttributes.Response_Success ? true
						: false;
			}
			if (cmd == BluetoothleGattAttributes.Cmd_EnableRecord) {
				if (result) {
					LoggerTool.d(TAG, "Cmd_EnableRecord  success. " + cmd);
				}
				// 请求读取保存的温度数据
				setCharacteristicData(_HTSWriteCharacteristic,
						BluetoothleGattAttributes.Cmd_QueryAllRecord);
			}
			if (cmd == BluetoothleGattAttributes.Cmd_DeleteAllRecord) {
				if (result) {
					LoggerTool.d(TAG, "Cmd_DeleteAllRecord  success. " + cmd);
				}
				// 删除记录后开始测量温度
				broadcastUpdate(ACTION_READY_FOR_READ);
			}
			if (cmd == BluetoothleGattAttributes.Cmd_UpdateTime) {
				if (result) {
					LoggerTool.d(TAG, "Cmd_UpdateTime  success. " + cmd);
				}
				if (isOnStart) {
					Bundle remoteData = new Bundle();
					remoteData.putString(INTENT_EXTRA_DEVICE_ADDRESS,
							mBluetoothDeviceAddress);
					FangXinBaoApplication.notifyHandlers(new String[] {
							SelectDeviceActivity.class.getName(),
							DeviceScanActivity.class.getName(),
							UserWearDeviceActivity.class.getName(),
							PrepareConnectDeviceActivity.class.getName() },
							CommonAction.ACTION_CONNECT_DEVICE_SUCCESS,
							remoteData);
					broadcastUpdate(ACTION_READY_FOR_READ);
				} else {
					// 设置保存温度
					setCharacteristicData(_HTSWriteCharacteristic,
							BluetoothleGattAttributes.Cmd_EnableRecord);
				}
			}
			if (cmd == BluetoothleGattAttributes.Cmd_QueryAllRecord) {
				try {
					if (result) {
						LoggerTool
								.d(TAG, "Cmd_QueryAllRecord  success. " + cmd);
						byte[] countByte = new byte[4];
						System.arraycopy(data, 2, countByte, 0, 4);
						if (TypeUtils.bytesToInt(countByte, 0) == 0) {
							broadcastUpdate(ACTION_READY_FOR_READ);
						} else {
							LoggerTool.d(TAG, "Cmd_QueryAllRecord  count:  "
									+ TypeUtils.bytesToInt(countByte, 0));
						}

					} else {
						broadcastUpdate(ACTION_READY_FOR_READ);
					}
				} catch (Exception ex) {
					broadcastUpdate(ACTION_READY_FOR_READ);
				}
			}
			// System.arraycopy(data, 1, result, 0, data.length-1);
			final StringBuilder stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));
			LoggerTool.d(TAG, "result : " + stringBuilder.toString());

		}
	}

	public class LocalBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		isOnStart = false;
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that
		// BluetoothGatt.close() is called
		// such that resources are cleaned up properly. In this particular
		// example, close() is
		// invoked when the UI is disconnected from the Service.
		close();
		return super.onUnbind(intent);
	}

	public BluetoothGatt getBluetoothGatt() {
		return mBluetoothGatt;
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				LoggerTool.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			LoggerTool.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		mBluetoothleWrapManger = BluetoothleWrapManger
				.getInstance(getApplication());
		dataHelper = TemperatureDataHelper.getDBInstance(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (initialize()) {
			// 这里判断是否通过绑定启动的服务
			if (intent != null) {
				if (intent
						.hasExtra(DeviceScanActivity.INTENT_EXTRA_DEVICE_ADDRESS)) {
					isOnStart = true;
					mBluetoothDeviceAddress = intent
							.getStringExtra(DeviceScanActivity.INTENT_EXTRA_DEVICE_ADDRESS);
					new Thread(new Runnable() {
						@Override
						public void run() {
							connect(mBluetoothDeviceAddress);
						}
					}).start();

				}
			}
		} else {
			// 初始化失败
			FangXinBaoApplication.notifyHandlers(
					new String[] { DeviceScanActivity.class.getName() },
					CommonAction.ACTION_CONNECT_DEVICE_FAILED, null);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LoggerTool.d(TAG, "onDestroy");
		disconnect();
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			LoggerTool.w(TAG,
					"BluetoothAdapter not initialized or unspecified address.");
			return false;
		}
		if (mBluetoothDeviceAddress != null
				&& address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			LoggerTool.d(TAG,
					"Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			LoggerTool.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		LoggerTool.d(TAG, "Trying to create a new connection.");
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			LoggerTool.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		LoggerTool.w(TAG, "mBluetoothGatt is closed.");
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			LoggerTool.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 * 
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			LoggerTool.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		// This is specific to Heart Rate Measurement.
		if (UUID_HEALTH_THEMOMETER_CHARACTERISTIC_CONFIG.equals(characteristic
				.getUuid())) {
			LoggerTool.d(TAG, "BluetoothAdapter writeDescriptor:"
					+ characteristic.getUuid());
			BluetoothGattDescriptor descriptor = characteristic
					.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIG);
			descriptor
					.setValue(enabled ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
							: new byte[] { 0x00, 0x00 });
			mBluetoothGatt.writeDescriptor(descriptor);

		} else if (UUID_CLIENT_CHARACTERISTIC_SPS_NOTIFY.equals(characteristic
				.getUuid())
				|| UUID_CLIENT_CHARACTERISTIC_SPS_RESPONSE
						.equals(characteristic.getUuid())) {
			LoggerTool.d(TAG, "BluetoothAdapter writeDescriptor:"
					+ characteristic.getUuid());

			BluetoothGattDescriptor descriptor = characteristic
					.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIG);

			descriptor
					.setValue(enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
							: BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
	}

	public void setCharacteristicData(
			BluetoothGattCharacteristic characteristic, int cmd) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}

		if (UUID_CLIENT_CHARACTERISTIC_SPS_NOTIFY.equals(characteristic
				.getUuid())) {
			LoggerTool.d(TAG, "BluetoothAdapter writeCharacteristic:" + cmd);
			// 更新温度计时间
			if (cmd == BluetoothleGattAttributes.Cmd_UpdateTime) {

				int time = (int) (System.currentTimeMillis() / 1000);

				byte[] param = TypeUtils.intToBytes(time);
				byte[] data = new byte[5];
				data[0] = (byte) cmd;
				System.arraycopy(param, 0, data, 1, 4);
				// 设置数据内容
				characteristic.setValue(data);
				mBluetoothGatt.writeCharacteristic(characteristic);
			} else if (cmd == BluetoothleGattAttributes.Cmd_EnableRecord) {
				characteristic.setValue(new byte[] { (byte) cmd, 0x01 });

				mBluetoothGatt.writeCharacteristic(characteristic);
			} else if (cmd == BluetoothleGattAttributes.Cmd_DeleteAllRecord) {
				characteristic.setValue(new byte[] { (byte) cmd });
				mBluetoothGatt.writeCharacteristic(characteristic);
			}
			// 读取温度数据
			else if (cmd == BluetoothleGattAttributes.Cmd_QueryAllRecord) {
				characteristic.setValue(new byte[] { (byte) cmd });
				// 往蓝牙模块写入数据
				mBluetoothGatt.writeCharacteristic(characteristic);
			}
		}

	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 * 
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;

		return mBluetoothGatt.getServices();
	}
}
