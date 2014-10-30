package com.yujunkang.fangxinbao.model;

import com.yujunkang.fangxinbao.utility.DataConstants.TemperatureLevel;
import com.yujunkang.fangxinbao.utility.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/**
 * 
 * @date 2014-6-22
 * @author xieb
 * 
 */
public class TemperatureData extends BaseData implements Parcelable {
	/**
	 * 时间戳
	 */
	private String time;
	private String temperature;
	private String userid;
	private String babyid;
	private String memo;
	private boolean isShow=false;
	private TemperatureLevel temperatureLevel;
	
	
	
	public TemperatureLevel getTemperatureLevel() {
		return temperatureLevel;
	}

	public void setTemperatureLevel(TemperatureLevel temperatureLevel) {
		this.temperatureLevel = temperatureLevel;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getBabyid() {
		return babyid;
	}

	public void setBabyid(String babyid) {
		this.babyid = babyid;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	private TemperatureData(Parcel in) {
		time = ParcelUtils.readStringFromParcel(in);
		temperature = ParcelUtils.readStringFromParcel(in);
		userid= ParcelUtils.readStringFromParcel(in);
		babyid= ParcelUtils.readStringFromParcel(in);
		memo = ParcelUtils.readStringFromParcel(in);
		temperatureLevel = (TemperatureLevel)in.readSerializable();
	}

	public TemperatureData() {
		// TODO Auto-generated constructor stub
	}

	public static final Creator<TemperatureData> CREATOR = new Parcelable.Creator<TemperatureData>() {
		public TemperatureData createFromParcel(Parcel in) {
			return new TemperatureData(in);
		}

		@Override
		public TemperatureData[] newArray(int size) {
			return new TemperatureData[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		ParcelUtils.writeStringToParcel(out, time);
		ParcelUtils.writeStringToParcel(out, temperature);
		ParcelUtils.writeStringToParcel(out, userid);
		ParcelUtils.writeStringToParcel(out, babyid);
		ParcelUtils.writeStringToParcel(out, memo);
		out.writeSerializable(temperatureLevel);
		
	}

	@Override
	public int describeContents() {
		return 0;
	}

}
