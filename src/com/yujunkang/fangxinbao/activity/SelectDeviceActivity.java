package com.yujunkang.fangxinbao.activity;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.user.UserMainActivity;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.FocusChangedUtils;
import com.yujunkang.fangxinbao.utility.OnPageNotifyListener;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

/**
 * 
 * @date 2014-8-14
 * @author xieb
 * 
 */
public class SelectDeviceActivity extends ActivityWrapper {
	private View btn_buy;
	private View btn_select_device;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_select_device: {
			Intent intent = new Intent(getSelfContext(),
					UserWearDeviceActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.btn_buy: {
			String device_url = Preferences.getDeviceUrl(getSelfContext());
			
			if (!TextUtils.isEmpty(device_url)) {
				Intent intent = new Intent(getSelfContext(),
						WebViewActivity.class);
				intent.putExtra(WebViewActivity.INTENT_EXTRA_URL, device_url);
				startActivity(intent);
			}
			break;
		}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_device_activity);
		ensureUi();
	}

	private void ensureUi() {
		btn_select_device = findViewById(R.id.btn_select_device);
		btn_buy = findViewById(R.id.btn_buy);
		btn_select_device.setOnClickListener(this);
		btn_buy.setOnClickListener(this);
		FocusChangedUtils.setViewFocusChanged(btn_buy);
		FocusChangedUtils.setViewFocusChanged(btn_select_device);
		String device_url = Preferences.getDeviceUrl(getSelfContext());
		if (TextUtils.isEmpty(device_url))
		{
			btn_buy.setVisibility(View.GONE);
		}
	}

	@Override
	public OnPageNotifyListener generatePageNotifyListener() {
		return new OnPageNotifyListener() {
			@Override
			public void onNotify(int what, Bundle data) {
				switch (what) {

				case CommonAction.ACTION_CONNECT_DEVICE_SUCCESS: {
					finish();
					break;
				}
				}

			}
		};
	}
}
