package com.yujunkang.fangxinbao.compare;

import java.util.Comparator;

import com.yujunkang.fangxinbao.model.TemperatureData;

/**
 * 
 * @date 2014-8-28
 * @author xieb
 * 
 */
public class TemperatureComparator implements Comparator<TemperatureData> {
	@Override
	public int compare(TemperatureData data1, TemperatureData data2) {
		try {
			String temperature1 = data1.getTemperature();
			String temperature2 = data2.getTemperature();
			float d1 = Float.parseFloat(temperature1);
			float d2 = Float.parseFloat(temperature2);
			if (d1 > d2) {
				return 1;
			} else if (d1 == d2) {
				return 0;
			} else {
				return -1;
			}
		} catch (Exception ex) {
			return 0;
		}
	}

}
