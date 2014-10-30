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
public class TemperatureRange extends BaseData implements Parcelable {
	private String minTemperature;
	private String maxTemperature;
	
	
	
	public TemperatureRange(Parcel in) {
		minTemperature = ParcelUtils.readStringFromParcel(in);
		maxTemperature = ParcelUtils.readStringFromParcel(in);
	}

	public TemperatureRange() {
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

	
	
	public static final Creator<TemperatureRange> CREATOR = new Parcelable.Creator<TemperatureRange>() {
		public TemperatureRange createFromParcel(Parcel in) {
			return new TemperatureRange(in);
		}

		@Override
		public TemperatureRange[] newArray(int size) {
			return new TemperatureRange[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		ParcelUtils.writeStringToParcel(out, minTemperature);
		ParcelUtils.writeStringToParcel(out, maxTemperature);
	}


}


