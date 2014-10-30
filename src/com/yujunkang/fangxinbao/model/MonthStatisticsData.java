package com.yujunkang.fangxinbao.model;



/**
 * 
 * @date 2014-8-12
 * @author xieb
 * 
 */
public class MonthStatisticsData extends BaseData {

	private Group<TemperatureData> recentData;
	private String version;
	public Group<TemperatureData> getRecentData() {
		return recentData;
	}
	public void setRecentData(Group<TemperatureData> recentData) {
		this.recentData = recentData;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	
}


