package com.yujunkang.fangxinbao.app;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.user.UserMainActivity;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.TypeUtils;

import android.content.Context;
import android.content.Intent;

/**
 * 
 * @date 2014-8-6
 * @author xieb
 * 
 */
public class TemperatureNotificationBuilder extends
		SystemNotificationBuilderBase {

	private float mTemperature;
	
	private static final String TAG = "TemperatureNotificationBuilder";
	
	public static final int NOTIFICATION_ID = 1;
	/**
	 * @param context
	 */
	public TemperatureNotificationBuilder(Context context, float temperature) {
		super(context);
		mTemperature = temperature;
	}

	/**
	 * @param context
	 */
	public TemperatureNotificationBuilder(Context context) {
		super(context);
		

	}
	
	/**
	 * @param content
	 */
	public TemperatureNotificationBuilder(Context context, String content) {
		super(context, content);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getContentText() {
		int temperatureType = Preferences.getTemperatureType(getContext());

		String temperatureStr = TypeUtils.getTemperatureScaleValueStr(1,
				mTemperature, FangXinBaoApplication.getApplication(getContext())
						.getLocale(), temperatureType, getContext());
		return getContext()
				.getString(R.string.notification_content, temperatureStr);

	}

	
	
	public float getTemperature() {
		return mTemperature;
	}

	public void setTemperature(float Temperature) {
		this.mTemperature = Temperature;
	}

	@Override
	protected Intent getIntent() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getContext(), UserMainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}

}
