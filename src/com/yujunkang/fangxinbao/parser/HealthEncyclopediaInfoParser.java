package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.yujunkang.fangxinbao.model.HealthEncyclopediaInfo;

/**
 * 
 * @date 2014-7-28
 * @author xieb
 * 
 */
public class HealthEncyclopediaInfoParser extends
		AbstractParser<HealthEncyclopediaInfo> {
	public HealthEncyclopediaInfoParser() {

		// TODO Auto-generated constructor stub
	}

	@Override
	public HealthEncyclopediaInfo parse(JSONObject json) throws JSONException {
		HealthEncyclopediaInfo result = new HealthEncyclopediaInfo();
		if (json.has("id")) {
			result.setId(json.getString("id"));
		}
		if (json.has("title")) {
			result.setTitle(json.getString("title"));
		}
		if (json.has("url")) {
			result.setUrl(json.getString("url"));
		}
		if (json.has("isfav")) {
			result.setIsfav(json.getBoolean("isfav"));
		}
		if (json.has("metadesc")) {
			result.setInfoDesc(json.getString("metadesc"));
		}
		if (json.has("images")) {
			result.setIconUrl(json.getString("images"));
		}
		return result;
	}

}
