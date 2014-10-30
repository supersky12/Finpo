package com.yujunkang.fangxinbao.parser;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.yujunkang.fangxinbao.model.RemoteSetting;

/**
 * 
 * @date 2014-7-31
 * @author xieb
 * 
 */
public class RemoteSettingParser extends AbstractParser<RemoteSetting> {

	public RemoteSettingParser() {

	}

	@Override
	public RemoteSetting parse(JSONObject json) throws JSONException {
		RemoteSetting result = new RemoteSetting();

		if (json.has("low")) {
			result.setMinTemperature(json.getString("low"));
		}
		if (json.has("high")) {
			result.setMaxTemperature(json.getString("high"));
		}
		if (json.has("user_manual_en")) {
			result.setUser_manual_en(json.getString("user_manual_en"));
		}
		if (json.has("user_manual_zh")) {
			result.setUser_manual_zh(json.getString("user_manual_zh"));
		}
		if (json.has("device_url")) {
			String deviceUrl = json.getString("device_url");
			if (!TextUtils.isEmpty(deviceUrl) && !deviceUrl.equals("null")) {
				result.setDevice_url(deviceUrl);
			}
		}
		if (json.has("service_phone_en")) {
			result.setServicePhone_en(json.getString("service_phone_en"));
		}
		if (json.has("service_phone_zh")) {
			result.setServicePhone_zh(json.getString("service_phone_zh"));
		}
		return result;
	}

}
