package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.yujunkang.fangxinbao.model.TemperatureCommonData;
import com.yujunkang.fangxinbao.utility.DataConstants.TemperatureLevel;



/**
 * 
 * @date 2014-7-22
 * @author xieb
 * 
 */
public class TemperatureCommonDataPareser extends AbstractParser<TemperatureCommonData> {

	public TemperatureCommonDataPareser() {

		
	}

	@Override
	public TemperatureCommonData parse(JSONObject json) throws JSONException {
		TemperatureCommonData result = new TemperatureCommonData();

		if (json.has("en")) {
			result.setDesc_en(new TemperatureStatusDescParser().parse(json.getJSONObject("en")));
		}
		if (json.has("zh")) {
			result.setDesc_zh(new TemperatureStatusDescParser().parse(json.getJSONObject("zh")));
		}
		if (json.has("t")) {
			result.setRange(json.getJSONArray("t"));
		}
		if (json.has("c")) {
			int s = json.getInt("c");
			result.setTemperatureLevel(TemperatureLevel.valueOf(s));
		}
		return result;
	}

}


