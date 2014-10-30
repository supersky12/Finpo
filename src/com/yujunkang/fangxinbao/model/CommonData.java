package com.yujunkang.fangxinbao.model;

import com.yujunkang.fangxinbao.utility.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @date 2014-5-24
 * @author xieb
 * 
 */
public class CommonData extends BaseData implements Parcelable {

	private long mTimestamp = 0;
	private Group<Country> mCountries = null;
	private String countryVersion;
	private String temperatureVersion;
	private String temperatureMaxOrMinVersion;
	
	private Group<TemperatureCommonData> mTemperatureCommonDatas = null;
	private RemoteSetting settings;
	
	
	
	
	public RemoteSetting getRemoteSetting() {
		return settings;
	}

	public void setRemoteSetting(RemoteSetting range) {
		this.settings = range;
	}

	public String getTemperatureMaxOrMinVersion() {
		return temperatureMaxOrMinVersion;
	}

	public void setTemperatureMaxOrMinVersion(String temperatureMaxOrMinVersion) {
		this.temperatureMaxOrMinVersion = temperatureMaxOrMinVersion;
	}

	public Group<TemperatureCommonData> getTemperatureCommonDatas() {
		return mTemperatureCommonDatas;
	}

	public void setTemperatureCommonDatas(
			Group<TemperatureCommonData> TemperatureCommonDatas) {
		this.mTemperatureCommonDatas = TemperatureCommonDatas;
	}

	
	public String getCountryVersion() {
		return countryVersion;
	}

	public void setCountryVersion(String countryVersion) {
		this.countryVersion = countryVersion;
	}

	public String getTemperatureVersion() {
		return temperatureVersion;
	}

	public void setTemperatureVersion(String temperatureVersion) {
		this.temperatureVersion = temperatureVersion;
	}

	public long getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(long timestamp) {
		this.mTimestamp = timestamp;
	}

	public Group<Country> getCountries() {
		return mCountries;
	}

	public void setCountries(Group<Country> Countries) {
		this.mCountries = Countries;
	}

	public CommonData() {
	}

	private CommonData(Parcel in) {

		mTimestamp = in.readLong();
		mCountries = new Group<Country>();
		int numCountries = in.readInt();
		for (int i = 0; i < numCountries; i++) {
			Country country = in.readParcelable(Country.class.getClassLoader());
			mCountries.add(country);
		}
		countryVersion = ParcelUtils.readStringFromParcel(in);
		temperatureVersion = ParcelUtils.readStringFromParcel(in);
		temperatureMaxOrMinVersion= ParcelUtils.readStringFromParcel(in);
		
		mTemperatureCommonDatas = new Group<TemperatureCommonData>();
		int numTemperatureDatas = in.readInt();
		for (int i = 0; i < numTemperatureDatas; i++) {
			TemperatureCommonData item = in.readParcelable(TemperatureCommonData.class.getClassLoader());
			mTemperatureCommonDatas.add(item);
		}
		
		if (in.readInt() == 1) {
			settings = in.readParcelable(RemoteSetting.class.getClassLoader());
		}
	}

	public static final Creator<CommonData> CREATOR = new Parcelable.Creator<CommonData>() {
		public CommonData createFromParcel(Parcel in) {
			return new CommonData(in);
		}

		@Override
		public CommonData[] newArray(int size) {
			return new CommonData[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(mTimestamp);
		if (mCountries != null) {
			out.writeInt(mCountries.size());
			for (int i = 0; i < mCountries.size(); i++) {
				out.writeParcelable(mCountries.get(i), flags);
			}
		} else {
			out.writeInt(0);
		}
		ParcelUtils.writeStringToParcel(out, countryVersion);
		ParcelUtils.writeStringToParcel(out, temperatureVersion);
		ParcelUtils.writeStringToParcel(out, temperatureMaxOrMinVersion);
		if (mTemperatureCommonDatas != null) {
			out.writeInt(mTemperatureCommonDatas.size());
			for (int i = 0; i < mTemperatureCommonDatas.size(); i++) {
				out.writeParcelable(mTemperatureCommonDatas.get(i), flags);
			}
		} else {
			out.writeInt(0);
		}
		if (settings != null) {
			out.writeInt(1);
			out.writeParcelable(settings, flags);
		} else {
			out.writeInt(0);
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

}
