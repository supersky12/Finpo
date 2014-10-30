package com.yujunkang.fangxinbao.compare;

import java.util.Comparator;

import com.yujunkang.fangxinbao.model.TemperatureData;



/**
 * 
 * @date 2014-9-1
 * @author xieb
 * 
 */
public class TemperatureTimeComparator  implements Comparator<TemperatureData> {
	@Override
	public int compare(TemperatureData data1, TemperatureData data2) {
		try {
			String time1 = data1.getTime();
			String time2 = data2.getTime();
			float d1 = Long.parseLong(time1);
			float d2 = Long.parseLong(time2);
			if (d1 < d2) {
				return -1;
			} else if (d1 == d2) {
				return 0;
			} else {
				return 1;
			}
		} catch (Exception ex) {
			return 0;
		}
	}

}


