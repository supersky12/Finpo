package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.model.HealthEncyclopediaClass;



/**
 * 
 * @date 2014-7-28
 * @author xieb
 * 
 */
public class HealthEncyclopediaClassParser extends AbstractParser<HealthEncyclopediaClass> {
	public HealthEncyclopediaClassParser() {
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public HealthEncyclopediaClass parse(JSONObject json) throws JSONException {
		HealthEncyclopediaClass result = new HealthEncyclopediaClass();
		if (json.has("id")) {
			result.setId(json.getString("id"));
		}
		if (json.has("title")) {
			result.setTitle(json.getString("title"));
		}
		
		return result;
	}

}


