package com.yujunkang.fangxinbao.model;

import com.yujunkang.fangxinbao.utility.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/**
 * 
 * @date 2014-8-7
 * @author xieb
 * 
 */
public class BluetoothDeviceInfo implements BaseModel, Parcelable {
	private String deviceName;
	private String deviceAddress;
	private boolean connected = false;

	public BluetoothDeviceInfo(String deviceName, String deviceAddress) {
		super();
		this.deviceName = deviceName;
		this.deviceAddress = deviceAddress;
	}

	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDesc(String desc) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCode(int code) {
		// TODO Auto-generated method stub

	}

	
	
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceAddress() {
		return deviceAddress;
	}

	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeStringToParcel(dest, deviceName);
		ParcelUtils.writeStringToParcel(dest, deviceAddress);
		dest.writeInt(connected ? 1 : 0);

	}

	private BluetoothDeviceInfo(Parcel in) {
		deviceName = ParcelUtils.readStringFromParcel(in);
		deviceAddress = ParcelUtils.readStringFromParcel(in);
		connected = in.readInt() == 1 ? true : false;
	}

	public static final Creator<BluetoothDeviceInfo> CREATOR = new Parcelable.Creator<BluetoothDeviceInfo>() {
		public BluetoothDeviceInfo createFromParcel(Parcel in) {
			return new BluetoothDeviceInfo(in);
		}

		@Override
		public BluetoothDeviceInfo[] newArray(int size) {
			return new BluetoothDeviceInfo[size];
		}
	};

}
