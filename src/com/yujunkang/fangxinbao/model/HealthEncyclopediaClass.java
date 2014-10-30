package com.yujunkang.fangxinbao.model;

import com.yujunkang.fangxinbao.utility.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/**
 * 
 * @date 2014-7-28
 * @author xieb
 * 
 */
public class HealthEncyclopediaClass extends BaseData implements Parcelable {
	/**
	 * 编号
	 */
	private String title = "";
	/**
	 * 名称
	 */
	private String id = "";

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	private HealthEncyclopediaClass(Parcel in) {
		id = ParcelUtils.readStringFromParcel(in);
		title = ParcelUtils.readStringFromParcel(in);

	}

	public HealthEncyclopediaClass() {
		// TODO Auto-generated constructor stub
	}

	public static final Creator<HealthEncyclopediaClass> CREATOR = new Parcelable.Creator<HealthEncyclopediaClass>() {
		public HealthEncyclopediaClass createFromParcel(Parcel in) {
			return new HealthEncyclopediaClass(in);
		}

		@Override
		public HealthEncyclopediaClass[] newArray(int size) {
			return new HealthEncyclopediaClass[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		ParcelUtils.writeStringToParcel(out, id);
		ParcelUtils.writeStringToParcel(out, title);

	}

}
