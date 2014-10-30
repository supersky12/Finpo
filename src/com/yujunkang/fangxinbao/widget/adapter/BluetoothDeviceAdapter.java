package com.yujunkang.fangxinbao.widget.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.bluetoothlegatt.BluetoothleWrapManger;
import com.yujunkang.fangxinbao.model.BluetoothDeviceInfo;
import com.yujunkang.fangxinbao.model.HealthEncyclopediaClass;
import com.yujunkang.fangxinbao.widget.adapter.HealthEncyclopediaClassListAdapter.ViewHolder;

/**
 * 
 * @date 2014-8-7
 * @author xieb
 * 
 */
public class BluetoothDeviceAdapter extends
		BaseGroupAdapter<BluetoothDeviceInfo> {
	private Context mContext;

	public BluetoothDeviceAdapter(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder Holder;
		// General ListView optimization code.
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.list_item_device, null);
			Holder = new ViewHolder();
			Holder.deviceAddress = (TextView) convertView
					.findViewById(R.id.device_address);
			Holder.deviceName = (TextView) convertView
					.findViewById(R.id.device_name);
			Holder.ll_device_status = (LinearLayout) convertView
					.findViewById(R.id.ll_device_status);
			Holder.tv_device_status = (TextView) convertView
					.findViewById(R.id.tv_device_status);
			Holder.iv_device_status = (ImageView) convertView
					.findViewById(R.id.iv_device_status);
			convertView.setTag(Holder);
		} else {
			Holder = (ViewHolder) convertView.getTag();
		}

		BluetoothDeviceInfo device = (BluetoothDeviceInfo) getItem(position);

		String deviceName = device.getDeviceName();
		//这里做下特殊处理
		if(!TextUtils.isEmpty(deviceName)&&deviceName.equals("AIRHEALTH"))
		{
			deviceName ="FinePo";
		}
		if (deviceName != null && deviceName.length() > 0) {
			Holder.deviceName.setText(deviceName);
		} else {
			Holder.deviceName.setText(R.string.unknown_device);
		}
		Holder.deviceAddress.setText(device.getDeviceAddress());
		Holder.ll_device_status.setVisibility(View.VISIBLE);
		boolean isConnected = BluetoothleWrapManger.getInstance(
				FangXinBaoApplication.getApplication(mContext)).isConnected(
				device);
		if (isConnected) {

			Holder.tv_device_status.setText(mContext
					.getString(R.string.connected_status));
			Holder.tv_device_status.setTextColor(mContext.getResources().getColor(R.color.blue_light));
			Holder.iv_device_status.setImageResource(R.drawable.icon_connected);
		} else {
			Holder.tv_device_status.setText(mContext
					.getString(R.string.disconnected_status));
			Holder.iv_device_status
					.setImageResource(R.drawable.icon_disconnected);
			Holder.tv_device_status.setTextColor(Color.RED);
		}
		return convertView;
	}

	class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
		LinearLayout ll_device_status;
		TextView tv_device_status;
		ImageView iv_device_status;
	}
}
