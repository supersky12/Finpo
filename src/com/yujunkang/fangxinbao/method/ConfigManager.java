package com.yujunkang.fangxinbao.method;

import java.io.InputStream;
import org.json.JSONArray;
import com.yujunkang.fangxinbao.database.DBHelper;
import com.yujunkang.fangxinbao.parser.CountryParser;
import com.yujunkang.fangxinbao.parser.GroupParser;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.TypeUtils;

import android.content.Context;
import android.text.TextUtils;

/**
 * 
 * @date 2014-5-26
 * @author xieb
 * 
 */
public class ConfigManager {
	private static ConfigManager _instance;
	private static Context _context;

	/**
	 * 把本地保存的数据文件保存到数据库里
	 * 
	 * @return
	 */
	private void init() {
		try {
			if (TextUtils.isEmpty(Preferences.getIsFirstRun(_context))) {
				// 读取本地配置文件
				InputStream in = _context.getAssets().open("country.json");
				String countryData = TypeUtils.convertInputStreamToString(in);
				JSONArray json = new JSONArray(countryData);
				GroupParser parser = new GroupParser(new CountryParser());
				DBHelper dbHelper = DBHelper.getDBInstance(_context);
				boolean successful = dbHelper.batchInsertCountry(parser
						.parse(json));
				if (successful) {
					Preferences.storeIsFirstRun(_context);
				}
			}
		} catch (Exception ex) {

		}
	}

	public synchronized static ConfigManager getInstance(Context context) {
		if (_instance == null) {
			_instance = new ConfigManager(context);
		}
		return _instance;
	}

	ConfigManager(Context context) {
		_context = context;
		init();
	}

}
