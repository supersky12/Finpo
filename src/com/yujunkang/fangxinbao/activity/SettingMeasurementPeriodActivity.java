package com.yujunkang.fangxinbao.activity;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.control.SettingTemperatureView;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.TypeUtils;
import com.yujunkang.fangxinbao.utility.DataConstants.SettingTemperatureLanucherType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SettingMeasurementPeriodActivity extends ActivityWrapper {
	public static final String INTENT_EXTRA_TIME = DataConstants.PACKAGE_NAME
			+ ".SettingMeasurementPeriodActivity.INTENT_EXTRA_TIME";
	private View btn_add;
	private View btn_reduce;
	private TextView tv_period;
	private View btn_confirm;
	private int mTimeValue;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_measurement_period_activity);
		initControl();
		ensureUi();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();
		
		mTimeValue = Preferences.getMeasuermentInterval(getSelfContext());
	}

	private void initControl() {
		btn_add = findViewById(R.id.btn_add);
		btn_reduce = findViewById(R.id.btn_reduce);
		tv_period = (TextView) findViewById(R.id.tv_period);
		btn_confirm = findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
	}

	public void ensureUi() {

		btn_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mTimeValue < 10) {
					mTimeValue++;
					tv_period.setText(getString(R.string.measurement_interval_unit, String.valueOf(mTimeValue)));
				}
			}
		});

		btn_reduce.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mTimeValue > 1) {
					mTimeValue--;
					tv_period.setText(getString(R.string.measurement_interval_unit, String.valueOf(mTimeValue)));
				}
			}
		});
		
		
		tv_period.setText(getString(R.string.measurement_interval_unit, String.valueOf(mTimeValue)));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm: {
			Preferences.storeMeasuermentInterval(getSelfContext(), mTimeValue);
			setResult(Activity.RESULT_OK);
			finish();
			break;
		}
		}
	}

}
