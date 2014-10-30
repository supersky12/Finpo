package com.yujunkang.fangxinbao.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @date 2014-7-28
 * @author xieb
 * 
 */
public class HealthEncyclopediaInfoListResult extends BaseData implements
		Parcelable {
	private Group<HealthEncyclopediaInfo> dataList = null;
	private boolean hasNext;

	public Group<HealthEncyclopediaInfo> getDataList() {
		return dataList;
	}

	public void setDataList(Group<HealthEncyclopediaInfo> dataList) {
		this.dataList = dataList;
	}

	public boolean isHasNext() {
		return hasNext;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	private HealthEncyclopediaInfoListResult(Parcel in) {
		hasNext = in.readInt() == 1 ? true : false;
		dataList = new Group<HealthEncyclopediaInfo>();
		int dataCount = in.readInt();
		for (int i = 0; i < dataCount; i++) {
			HealthEncyclopediaInfo item = in
					.readParcelable(HealthEncyclopediaInfo.class
							.getClassLoader());
			dataList.add(item);
		}
	}

	public HealthEncyclopediaInfoListResult() {
		// TODO Auto-generated constructor stub
	}

	public static final Creator<HealthEncyclopediaInfoListResult> CREATOR = new Parcelable.Creator<HealthEncyclopediaInfoListResult>() {
		public HealthEncyclopediaInfoListResult createFromParcel(Parcel in) {
			return new HealthEncyclopediaInfoListResult(in);
		}

		@Override
		public HealthEncyclopediaInfoListResult[] newArray(int size) {
			return new HealthEncyclopediaInfoListResult[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(hasNext ? 1 : 0);
		if (dataList != null) {
			out.writeInt(dataList.size());
			for (int i = 0; i < dataList.size(); i++) {
				out.writeParcelable(dataList.get(i), flags);
			}
		} else {
			out.writeInt(0);
		}

	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
}
