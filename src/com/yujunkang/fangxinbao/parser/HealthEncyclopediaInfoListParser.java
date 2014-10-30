package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.yujunkang.fangxinbao.model.HealthEncyclopediaInfoListResult;




/**
 * 
 * @date 2014-7-28
 * @author xieb
 * 
 */
public class HealthEncyclopediaInfoListParser extends AbstractParser<HealthEncyclopediaInfoListResult>{
public HealthEncyclopediaInfoListParser() {

		
	}

	@Override
	public HealthEncyclopediaInfoListResult parse(JSONObject json) throws JSONException {
		HealthEncyclopediaInfoListResult result = new HealthEncyclopediaInfoListResult();

		if (json.has("list")) {
			result.setDataList(new GroupParser(new HealthEncyclopediaInfoParser()).parse(json
					.getJSONArray("list")));
		}
		if (json.has("hasNext")) {
			result.setHasNext(json.getBoolean("hasNext"));
		}
		return result;
	}
}


