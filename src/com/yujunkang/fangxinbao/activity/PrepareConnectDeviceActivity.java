package com.yujunkang.fangxinbao.activity;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.bluetoothlegatt.DeviceScanActivity;
import com.yujunkang.fangxinbao.utility.OnPageNotifyListener;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 
 * @date 2014-8-13
 * @author xieb
 * 
 */
public class PrepareConnectDeviceActivity extends ActivityWrapper {
	private View btn_next_step;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next_step: {
			Intent intent = new Intent(getSelfContext(),
					DeviceScanActivity.class);
			intent.putExtra(DeviceScanActivity.INTENT_EXTRA_LAUNCHER_TYPE, 3);
			startActivity(intent);
			break;
		}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prepare_connect_device_activity);
		ensureUi();
	}

	private void ensureUi() {
		btn_next_step = findViewById(R.id.btn_next_step);
		btn_next_step.setOnClickListener(this);
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
