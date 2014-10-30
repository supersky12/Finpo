package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.yujunkang.fangxinbao.model.TemperatureStatusDesc;

public class TemperatureStatusDescParser extends AbstractParser<TemperatureStatusDesc> {

	@Override
	public TemperatureStatusDesc parse(JSONObject json) throws JSONException {
		TemperatureStatusDesc result = new TemperatureStatusDesc();
		if (json.has("shortDesc")) {
			result.setShortDesc(json.getString("shortDesc"));
		}
		if (json.has("longDesc")) {
			result.setLongDesc(json.getString("longDesc"));
		}
		if (json.has("url")) {
			result.setUrl(json.getString("url"));
		}
		return result;
	}

}
