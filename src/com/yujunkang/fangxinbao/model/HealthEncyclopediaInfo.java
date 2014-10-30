package com.yujunkang.fangxinbao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.yujunkang.fangxinbao.utility.ParcelUtils;

/**
 * 
 * @date 2014-7-28
 * @author xieb
 * 
 */
public class HealthEncyclopediaInfo extends BaseData implements Parcelable {
	/**
	 * 标题
	 */
	private String title = "";
	/**
	 * 编号
	 */
	private String id = "";
	/**
		 * 
		 */
	private String url = "";

	/**
		 * 
		 */
	private boolean isfav;
	/**
	 * 
	 */
	private String infoDesc = "";

	private String iconUrl = "";

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getInfoDesc() {
		return infoDesc;
	}

	public void setInfoDesc(String infoDesc) {
		this.infoDesc = infoDesc;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean Isfavorite() {
		return isfav;
	}

	public void setIsfav(boolean isfav) {
		this.isfav = isfav;
	}

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

	private HealthEncyclopediaInfo(Parcel in) {
		id = ParcelUtils.readStringFromParcel(in);
		title = ParcelUtils.readStringFromParcel(in);
		url = ParcelUtils.readStringFromParcel(in);
		isfav = in.readInt() == 1 ? true : false;
		infoDesc = ParcelUtils.readStringFromParcel(in);
		iconUrl = ParcelUtils.readStringFromParcel(in);
	}

	public HealthEncyclopediaInfo() {
		// TODO Auto-generated constructor stub
	}

	public static final Creator<HealthEncyclopediaInfo> CREATOR = new Parcelable.Creator<HealthEncyclopediaInfo>() {
		public HealthEncyclopediaInfo createFromParcel(Parcel in) {
			return new HealthEncyclopediaInfo(in);
		}

		@Override
		public HealthEncyclopediaInfo[] newArray(int size) {
			return new HealthEncyclopediaInfo[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		ParcelUtils.writeStringToParcel(out, id);
		ParcelUtils.writeStringToParcel(out, title);
		ParcelUtils.writeStringToParcel(out, url);
		out.writeInt(isfav ? 1 : 0);
		ParcelUtils.writeStringToParcel(out, infoDesc);
		ParcelUtils.writeStringToParcel(out, iconUrl);

	}

}
