package com.yujunkang.fangxinbao.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.HealthEncyclopediaClass;



/**
 * 
 * @date 2014-7-28
 * @author xieb
 * 
 */
public class HealthEncyclopediaClassListParser extends AbstractParser<Group<HealthEncyclopediaClass>> {

	public HealthEncyclopediaClassListParser() {

		
	}



	@Override
	public Group<HealthEncyclopediaClass> parse(JSONArray array) throws JSONException {
		return new GroupParser(new HealthEncyclopediaClassParser()).parse(array);
	}



	@Override
	public Group<HealthEncyclopediaClass> parse(JSONObject json) throws JSONException {
		// TODO Auto-generated method stub
		return new Group<HealthEncyclopediaClass>();
	}

}


