package com.yujunkang.fangxinbao.control;

import java.math.BigDecimal;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.TypeUtils;
import com.yujunkang.fangxinbao.utility.Utils;
import com.yujunkang.fangxinbao.utility.DataConstants.SettingTemperatureLanucherType;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @date 2014-6-18
 * @author xieb
 * 
 */
public class SettingTemperatureView extends LinearLayoutControlWrapView {
	private float increase = 0.1f;

	private View btn_add;
	private View btn_reduce;
	private TextView tv_temperature;

	private float temperatureValue;
	private int temperatureType;
	private float maxTemperature;
	private float minTemperature;
	private SettingTemperatureLanucherType operationType;

	public SettingTemperatureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mInflater.inflate(R.layout.settings_temperature_view, this);
		setGravity(Gravity.CENTER_VERTICAL);
		setOrientation(LinearLayout.HORIZONTAL);
		// 温度相关
		temperatureType = Preferences.getTemperatureType(context);
		maxTemperature = Preferences.getMaxTemperature(context);
		minTemperature = Preferences.getMinTemperature(context);
		maxTemperature=TypeUtils.getRealScaleValue(1,maxTemperature);
		minTemperature=TypeUtils.getRealScaleValue(1,minTemperature);
		ensureUI();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		final int totalWidth = MeasureSpec.getSize(widthMeasureSpec);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(totalWidth, (int) getContext().getResources()
				.getDimension(R.dimen.table_item_height));
	}

	public void ensureUI() {
		btn_add = findViewById(R.id.btn_add);
		btn_reduce = findViewById(R.id.btn_reduce);
		tv_temperature = (TextView) findViewById(R.id.tv_temperature);
		btn_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (operationType == SettingTemperatureLanucherType.Max) {
					if (temperatureValue < DataConstants.DEFAULT_MAX_TEMPERATURE) {
						temperatureValue = temperatureValue + increase;
						temperatureValue=TypeUtils.getRealScaleValue(1,temperatureValue);
						tv_temperature.setText(getTemperatureValue());
					}
				} else {
					float compareValue = TypeUtils.getRealScaleValue(1,maxTemperature-increase);
					if (temperatureValue < compareValue) {
						temperatureValue = temperatureValue + increase;
						temperatureValue=TypeUtils.getRealScaleValue(1,temperatureValue);
						tv_temperature.setText(getTemperatureValue());
					}
				}

			}
		});

		btn_reduce.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (operationType == SettingTemperatureLanucherType.Max) {
					float compareValue = TypeUtils.getRealScaleValue(1,minTemperature+increase);
					if (temperatureValue > compareValue) {
						temperatureValue = temperatureValue - increase;
						temperatureValue=TypeUtils.getRealScaleValue(1,temperatureValue);
						tv_temperature.setText(getTemperatureValue());
					}
				} else {
					if (temperatureValue > DataConstants.DEFAULT_MIN_TEMPERATURE) {
						temperatureValue = temperatureValue - increase;
						temperatureValue=TypeUtils.getRealScaleValue(1,temperatureValue);
						tv_temperature.setText(getTemperatureValue());
					}
				}
			}
		});
	}

	
	
	public SettingTemperatureLanucherType getOperationType() {
		return operationType;
	}

	public void setOperationType(SettingTemperatureLanucherType operationType) {
		this.operationType = operationType;
		temperatureValue =operationType == SettingTemperatureLanucherType.Max?maxTemperature:minTemperature;
		tv_temperature.setText(getTemperatureValue());
	}

	private String getTemperatureValue() {
		try {
			return TypeUtils.getTemperatureScaleValueStr(1, temperatureValue,
					FangXinBaoApplication.getApplication(getContext())
							.getLocale(), temperatureType, getContext());
		} catch (Exception ex) {

		}
		return "";
	}

	public float getValue() {
		return temperatureValue;
	}

	public void setTemperatureValue(float temperatureValue) {
		this.temperatureValue = temperatureValue;
		tv_temperature.setText(getTemperatureValue());
	}

}
