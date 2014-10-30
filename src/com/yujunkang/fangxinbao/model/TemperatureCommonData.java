package com.yujunkang.fangxinbao.model;

import org.json.JSONArray;
import org.json.JSONException;

import com.yujunkang.fangxinbao.utility.DataConstants.TemperatureLevel;
import com.yujunkang.fangxinbao.utility.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 
 * @date 2014-7-22
 * @author xieb
 * 
 */
public class TemperatureCommonData extends BaseData implements Parcelable {

	private TemperatureLevel temperatureLevel;
	private TemperatureStatusDesc desc_zh;
	private TemperatureStatusDesc desc_en;
	private JSONArray range;

	public JSONArray getRange() {
		return range;
	}

	public void setRange(JSONArray range) {
		this.range = range;
	}

	public TemperatureStatusDesc getDesc_zh() {
		return desc_zh;
	}

	public void setDesc_zh(TemperatureStatusDesc desc_zh) {
		this.desc_zh = desc_zh;
	}

	public TemperatureStatusDesc getDesc_en() {
		return desc_en;
	}

	public void setDesc_en(TemperatureStatusDesc desc_en) {
		this.desc_en = desc_en;
	}

	public TemperatureLevel getTemperatureLevel() {
		return temperatureLevel;
	}

	public void setTemperatureLevel(TemperatureLevel temperatureLevel) {
		this.temperatureLevel = temperatureLevel;
	}

	public boolean inRange(float value) {
		if (range != null && range.length() == 2) {
			try {
				double filtermintemperature = range.getDouble(0);
				double filtermaxtemperature = range.getDouble(1);
				if (filtermintemperature == 0) {
					if (value <= filtermaxtemperature) {
						return true;
					}
				} else if (filtermaxtemperature == 0) {
					if (value >= filtermintemperature) {
						return true;
					}
				} else {
					if (value >= filtermintemperature
							&& value <= filtermaxtemperature) {
						return true;
					}
				}
			} catch (Exception ex) {
			}
		}
		return false;
	}

	private TemperatureCommonData(Parcel in) {
		try {
			range = new JSONArray(ParcelUtils.readStringFromParcel(in));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (in.readInt() == 1) {
			desc_zh = in.readParcelable(TemperatureStatusDesc.class
					.getClassLoader());
		}
		if (in.readInt() == 1) {
			desc_en = in.readParcelable(TemperatureStatusDesc.class
					.getClassLoader());
		}
		temperatureLevel = (TemperatureLevel) in.readSerializable();
	}

	public TemperatureCommonData() {
		// TODO Auto-generated constructor stub
	}

	public static final Creator<TemperatureCommonData> CREATOR = new Parcelable.Creator<TemperatureCommonData>() {
		public TemperatureCommonData createFromParcel(Parcel in) {
			return new TemperatureCommonData(in);
		}

		@Override
		public TemperatureCommonData[] newArray(int size) {
			return new TemperatureCommonData[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		ParcelUtils.writeStringToParcel(out, range != null ? range.toString()
				: "");
		if (desc_zh != null) {
			out.writeInt(1);
			out.writeParcelable(desc_zh, flags);
		} else {
			out.writeInt(0);
		}
		if (desc_en != null) {
			out.writeInt(1);
			out.writeParcelable(desc_en, flags);
		} else {
			out.writeInt(0);
		}
		out.writeSerializable(temperatureLevel);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
