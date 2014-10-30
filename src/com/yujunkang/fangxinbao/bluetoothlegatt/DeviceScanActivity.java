package com.yujunkang.fangxinbao.bluetoothlegatt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.activity.ConnectDeviceSuccessActivity;
import com.yujunkang.fangxinbao.activity.PrepareConnectDeviceActivity;
import com.yujunkang.fangxinbao.activity.SelectDeviceActivity;
import com.yujunkang.fangxinbao.activity.SettingActivity;
import com.yujunkang.fangxinbao.activity.UserWearDeviceActivity;
import com.yujunkang.fangxinbao.activity.user.UserBabyInfoActivity;
import com.yujunkang.fangxinbao.activity.user.UserMainActivity;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.control.dialog.ISimpleDialogListener;
import com.yujunkang.fangxinbao.model.Baby;
import com.yujunkang.fangxinbao.model.BluetoothDeviceInfo;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.DialogHelper;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.OnPageNotifyListener;
import com.yujunkang.fangxinbao.utility.UiUtils;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;

/**
 * 扫描并显示可用的蓝牙LE设备
 */
public class DeviceScanActivity extends ActivityWrapper implements
		ISimpleDialogListener {
	public static final String INTENT_EXTRA_DEVICE_ADDRESS = DataConstants.PACKAGE_NAME
			+ ".DeviceScanActivity.INTENT_EXTRA_DEVICE_ADDRESS";
	public static final String INTENT_EXTRA_LAUNCHER_TYPE = DataConstants.PACKAGE_NAME
			+ ".DeviceScanActivity.INTENT_EXTRA_LAUNCHER_TYPE";
	public static final String ACTION_CANCEL_ = DataConstants.PACKAGE_NAME
			+ ".DeviceScanActivity.INTENT_EXTRA_LAUNCHER_TYPE";
	public static final int REQUEST_CODE_RESCAN = 1;
	private LeDeviceListAdapter mLeDeviceListAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private Handler mHandler;
	private ListView mListView;
	private View btn_skip;

	/**
	 * 1:表示扫描全部设备,2:扫描新设备,3:引导
	 */
	private int launcherType = 1;

	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothleWrapManger mBluetoothleWrapManger;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_bluetooth_device_activity);
		mHandler = new Handler();
		Intent intent = getIntent();
		if (intent.hasExtra(INTENT_EXTRA_LAUNCHER_TYPE)) {
			launcherType = intent.getIntExtra(INTENT_EXTRA_LAUNCHER_TYPE, 1);
		}
		// 检查扫描使用蓝牙设备的权限
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
					.show();
			finish();
		}

		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		// 检查蓝牙功能是否被支持
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported,
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		mBluetoothleWrapManger = BluetoothleWrapManger
				.getInstance(mApplication);
		initControl();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 确保蓝牙设备上启用。如果蓝牙当前未启用，
		// 要求用户授予权限来启用它

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			return;
		}

		// Initializes list view adapter.
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		mListView.setAdapter(mLeDeviceListAdapter);
		scanLeDevice(true);
	}

	private void initControl() {
		mListView = (ListView) findViewById(R.id.list);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final BluetoothDevice device = mLeDeviceListAdapter
						.getDevice(position);
				if (device == null)
					return;
				scanLeDevice(false);
				if (launcherType != 3) {
					Intent intent = new Intent();
					intent.putExtra(INTENT_EXTRA_DEVICE_ADDRESS,
							device.getAddress());
					setResult(Activity.RESULT_OK, intent);
					finish();
				} else {
					startProgressDialog(
							getString(R.string.connecting_bluetooth_device_loading),
							true, -1000, false);
					Intent intent = new Intent(getSelfContext(),
							BluetoothLeService.class);
					intent.putExtra(INTENT_EXTRA_DEVICE_ADDRESS,
							device.getAddress());
					startService(intent);
				}
			}
		});
		btn_skip = findViewById(R.id.btn_skip);
		btn_skip.setOnClickListener(this);
		if (launcherType == 3) {
			btn_skip.setVisibility(View.VISIBLE);
		} else {
			btn_skip.setVisibility(View.GONE);
		}
	}

	private void showNotFoundDeviceDialog() {
		showOKOrCancelDialog(getString(R.string.button_retry),
				getString(R.string.button_cancel),
				getString(R.string.scan_not_found_bluetooth_device_message),
				getString(R.string.scan_not_found_bluetooth_device_title),
				REQUEST_CODE_RESCAN);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		   if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
	            finish();
	            return;
	        }
	        super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mLeDeviceListAdapter!=null)
		{
			mLeDeviceListAdapter.clear();
		}
		scanLeDevice(false);
		
	}

	private void scanLeDevice(boolean enable) {
		if (enable) {
			if (launcherType == 3) {
				startProgressDialog(
						getString(R.string.scaning_bluetooth_device_loading),
						true, 0, false);
			} else {
				startProgressDialog(
						getString(R.string.scaning_bluetooth_device_loading),
						false, 0, false);
			}

		}
		mBluetoothleWrapManger.startScanDevice(enable, mHandler,
				mLeScanCallback, true, DeviceScanActivity.class.getName());
	}

	@Override
	public OnPageNotifyListener generatePageNotifyListener() {
		return new OnPageNotifyListener() {
			@Override
			public void onNotify(int what, Bundle data) {
				switch (what) {

				case CommonAction.ACTION_SCAN_TIMEOUT: {
					stopProgressDialog();
					if (mLeDeviceListAdapter.getCount() == 0) {
						showNotFoundDeviceDialog();
					}
					break;
				}
				case CommonAction.ACTION_CONNECT_DEVICE_FAILED: {
					UiUtils.showAlertDialog(
							getString(R.string.connecting_bluetooth_device_failed),
							getSelfContext());
					stopProgressDialog();

					break;
				}
				case CommonAction.ACTION_CONNECT_DEVICE_SUCCESS: {
					stopProgressDialog();
					if (data != null) {
						Intent intent = new Intent(getSelfContext(),
								ConnectDeviceSuccessActivity.class);
						intent.putExtras(data);
						startActivity(intent);
						finish();
					}

					break;
				}
				}

			}
		};
	}

	// 通过扫描发现适配器
	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = DeviceScanActivity.this.getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device) {
			if (!mLeDevices.contains(device)) {
				mLeDevices.add(device);
			}
		}

		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder Holder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.list_item_device, null);
				Holder = new ViewHolder();
				Holder.deviceAddress = (TextView) view
						.findViewById(R.id.device_address);
				Holder.deviceName = (TextView) view
						.findViewById(R.id.device_name);
				view.setTag(Holder);
			} else {
				Holder = (ViewHolder) view.getTag();
			}

			BluetoothDevice device = mLeDevices.get(i);
			String deviceName = device.getName();
			
			if(!TextUtils.isEmpty(deviceName)&&deviceName.equals("AIRHEALTH"))
			{
				deviceName ="FinePo";
			}
			if (deviceName != null && deviceName.length() > 0)
				Holder.deviceName.setText(deviceName);
			else
				Holder.deviceName.setText(R.string.unknown_device);
			Holder.deviceAddress.setText(device.getAddress());
			return view;
		}
	}

	/**
	 * 扫描蓝牙设备回调
	 */
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			boolean isMatch = filterDevice(device);
			if (isMatch) {
				// mBluetoothleWrapManger.stopPerformScanPeriod();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mLeDeviceListAdapter.addDevice(device);
						mLeDeviceListAdapter.notifyDataSetChanged();
					}
				});
			}
		}

	};

	private boolean filterDevice(BluetoothDevice device) {
		if (launcherType == 2) {
			Group<BluetoothDeviceInfo> recordDevices = Preferences
					.getDeviceInfo(getSelfContext());
			if (recordDevices != null && recordDevices.size() > 0) {
				for (BluetoothDeviceInfo item : recordDevices) {
					if (item.getDeviceAddress().equals(device.getAddress())) {
						return false;
					}
				}
			}
		}
		// 检查是否为测量体温的设备
		return true;
	}

	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_skip: {
			FangXinBaoApplication.notifyHandlerPool(
					CommonAction.CLOSE_ALL_ACTIVITY, null);
			Intent intent = new Intent(getSelfContext(), UserMainActivity.class);
			startActivity(intent);
			break;
		}
		}

	}

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		super.onPositiveButtonClicked(requestCode);
		if (REQUEST_CODE_RESCAN == requestCode) {
			scanLeDevice(true);
		}

	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
		if (REQUEST_CODE_RESCAN == requestCode) {
			if (launcherType != 3) {
				finish();
			} else {

			}
		}

	}
}