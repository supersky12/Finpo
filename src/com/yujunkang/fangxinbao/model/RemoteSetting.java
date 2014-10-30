package com.yujunkang.fangxinbao.model;

import com.yujunkang.fangxinbao.utility.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/**
 * 
 * @date 2014-7-31
 * @author xieb
 * 
 */
public class RemoteSetting extends BaseData implements Parcelable {
	private String minTemperature;
	private String maxTemperature;
	private String user_manual_en;
	private String user_manual_zh;
	private String device_url;
	private String servicePhone_en;
	private String servicePhone_zh;
	
	
	
	public String getServicePhone_en() {
		return servicePhone_en;
	}

	public void setServicePhone_en(String servicePhone_en) {
		this.servicePhone_en = servicePhone_en;
	}

	public String getServicePhone_zh() {
		return servicePhone_zh;
	}

	public void setServicePhone_zh(String servicePhone_zh) {
		this.servicePhone_zh = servicePhone_zh;
	}

	public String getDevice_url() {
		return device_url;
	}

	public void setDevice_url(String device_url) {
		this.device_url = device_url;
	}

	public String getUser_manual_en() {
		return user_manual_en;
	}

	public void setUser_manual_en(String user_manual_en) {
		this.user_manual_en = user_manual_en;
	}

	public String getUser_manual_zh() {
		return user_manual_zh;
	}

	public void setUser_manual_zh(String user_manual_zh) {
		this.user_manual_zh = user_manual_zh;
	}

	public RemoteSetting(Parcel in) {
		minTemperature = ParcelUtils.readStringFromParcel(in);
		maxTemperature = ParcelUtils.readStringFromParcel(in);
		user_manual_en = ParcelUtils.readStringFromParcel(in);
		user_manual_zh = ParcelUtils.readStringFromParcel(in);
		device_url = ParcelUtils.readStringFromParcel(in);
		servicePhone_en= ParcelUtils.readStringFromParcel(in);
		servicePhone_zh= ParcelUtils.readStringFromParcel(in);
	}

	public RemoteSetting() {
		// TODO Auto-generated constructor stub
	}

	public String getMinTemperature() {
		return minTemperature;
	}

	public void setMinTemperature(String minTemperature) {
		this.minTemperature = minTemperature;
	}

	public String getMaxTemperature() {
		return maxTemperature;
	}

	public void setMaxTemperature(String maxTemperature) {
		this.maxTemperature = maxTemperature;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Creator<RemoteSetting> CREATOR = new Parcelable.Creator<RemoteSetting>() {
		public RemoteSetting createFromParcel(Parcel in) {
			return new RemoteSetting(in);
		}

		@Override
		public RemoteSetting[] newArray(int size) {
			return new RemoteSetting[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		ParcelUtils.writeStringToParcel(out, minTemperature);
		ParcelUtils.writeStringToParcel(out, maxTemperature);
		ParcelUtils.writeStringToParcel(out, user_manual_en);
		ParcelUtils.writeStringToParcel(out, user_manual_zh);
		ParcelUtils.writeStringToParcel(out, device_url);
		ParcelUtils.writeStringToParcel(out, servicePhone_en);
		ParcelUtils.writeStringToParcel(out, servicePhone_zh);
	}

}
