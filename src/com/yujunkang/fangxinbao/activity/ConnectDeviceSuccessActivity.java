package com.yujunkang.fangxinbao.activity;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.user.UserMainActivity;
import com.yujunkang.fangxinbao.bluetoothlegatt.DeviceScanActivity;
import com.yujunkang.fangxinbao.utility.FocusChangedUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

/**
 * 
 * @date 2014-8-13
 * @author xieb
 * 
 */
public class ConnectDeviceSuccessActivity extends ActivityWrapper {

	private View btn_next_step;
	private Bundle data;;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next_step: {
			Intent intent = new Intent(getSelfContext(), UserMainActivity.class);
			intent.putExtras(data);
			startActivity(intent);
			finish();
			break;
		}
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		View btn_back = findViewById(R.id.btn_back);
		if (btn_back != null) {
			FocusChangedUtils.setViewFocusChanged(btn_back);
			FocusChangedUtils.expandTouchArea(btn_back, 20, 20, 20, 20);
			btn_back.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getSelfContext(),
							UserMainActivity.class);
					intent.putExtras(data);
					startActivity(intent);
					finish();
				}
			});
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connect_device_success_activity);
		ensureUi();
		Intent intent = getIntent();
		data = intent.getExtras();
	}

	private void ensureUi() {
		btn_next_step = findViewById(R.id.btn_next_step);
		btn_next_step.setOnClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(getSelfContext(), UserMainActivity.class);
			intent.putExtras(data);
			startActivity(intent);
			finish();
		}
		return true;
	}

}
