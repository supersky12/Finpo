package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.yujunkang.fangxinbao.activity.user.UserMainActivity;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.database.TemperatureDataHelper;
import com.yujunkang.fangxinbao.method.Method;
import com.yujunkang.fangxinbao.model.CommonData;
import com.yujunkang.fangxinbao.model.MonthStatisticsData;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.TypeUtils;



/**
 * 
 * @date 2014-8-12
 * @author xieb
 * 
 */
public class MonthStatisticsDataParser extends AbstractParser<MonthStatisticsData>{
	public MonthStatisticsDataParser() {

		
	}

	@Override
	public MonthStatisticsData parse(JSONObject json) throws JSONException {
		MonthStatisticsData result = new MonthStatisticsData();

		if (json.has("max")) {
			result.setRecentData(new GroupParser(new TemperatureDataParser()).parse(json
					.getJSONArray("max")));
		}
		if (json.has("ver")) {
			result.setVersion(json.getString("ver"));
		}
		return result;
	}

	@Override
	public void onParserComplete(Context context, MonthStatisticsData result) {
		TemperatureDataHelper dbHelper = TemperatureDataHelper.getDBInstance(context);
		User user = Preferences.getUserCommonInfo(context);
		if (result != null && result.getRecentData() != null
				&& result.getRecentData().size() > 0)
		{
			boolean success = dbHelper.batchInsertMonthStatisticsData(result.getRecentData(), user.getId());
			if(success)
			{
				Preferences.storeSynchronousDate(context);
				Preferences.storeSynchronousVersion(context, result.getVersion());
			}
		}
	}
}


