package com.yujunkang.fangxinbao.parser;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.yujunkang.fangxinbao.method.Method;
import com.yujunkang.fangxinbao.method.VeDate;
import com.yujunkang.fangxinbao.model.BabyRecentTemperature;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.HealthEncyclopediaClass;
import com.yujunkang.fangxinbao.model.TemperatureCommonData;
import com.yujunkang.fangxinbao.model.TemperatureData;

/**
 * 
 * @date 2014-7-10
 * @author xieb
 * 
 */
public class BabyRecentTemperatureParser extends
		AbstractParser<Group<TemperatureData>> {

	public BabyRecentTemperatureParser() {

		// TODO Auto-generated constructor stub
	}

	@Override
	public Group<TemperatureData> parse(JSONArray array) throws JSONException {
		return new GroupParser(new TemperatureDataParser()).parse(array);
	}

	@Override
	public Group<TemperatureData> parse(JSONObject json) throws JSONException {
		// TODO Auto-generated method stub
		return new Group<TemperatureData>();
	}

	@Override
	public void onParserComplete(Context context, Group<TemperatureData> result) {
		// TODO Auto-generated method stub
		if (result != null && result.code == 1) {
			for (TemperatureData item : result) {
				try {
					String time = item.getTime();
					long lcc_time = Long.valueOf(time);
					item.setTime(VeDate.DateToStr(
							new Date(lcc_time * 1000L).getTime(), "HH:mm"));
					float temperature = Float.parseFloat(item.getTemperature());
					TemperatureCommonData temperatureStatus = Method
							.getTemperatureLevelStatus(temperature, context);
					if (temperatureStatus != null) {
						item.setTemperatureLevel(temperatureStatus
								.getTemperatureLevel());
					}
				} catch (Exception ex) {
					item.setTime("");
				}

			}
		}
	}

}
