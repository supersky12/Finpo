package com.yujunkang.fangxinbao.task;

import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.yujunkang.fangxinbao.activity.user.UserMainActivity;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.database.DBHelper;
import com.yujunkang.fangxinbao.database.TemperatureDataHelper;
import com.yujunkang.fangxinbao.method.Method;
import com.yujunkang.fangxinbao.method.VeDate;
import com.yujunkang.fangxinbao.model.Baby;
import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.TemperatureData;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.parser.Parser;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

/**
 * 
 * @date 2014-7-24
 * @author xieb
 * 
 */
public class UploadTemperatureTask extends
		AsyncTaskWithLoadingDialog<Void, Void, Void> {
	private final static String TAG = "UploadTemperatureTask";
	private Context mContext;

	public UploadTemperatureTask(Context context, String title,
			boolean isCancelable) {
		super(context, title, isCancelable);
		// TODO Auto-generated constructor stub
	}

	public UploadTemperatureTask(Context context, boolean dialogEnable) {
		super(context, dialogEnable);
		mContext = context;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		executePrepare();
	}

	@Override
	protected Void doInBackground(Void... params) {
		// 开始执行请求
		User user = Preferences.getUserInfo(mContext);
		TemperatureDataHelper dataHelper = TemperatureDataHelper
				.getDBInstance(mContext);
		Group<Baby> babies = user.getBaBies();
		if (babies != null && babies.size() > 0) {
			FangXinBaoApplication app = FangXinBaoApplication
					.getApplication(mContext);
			for (Baby item : babies) {
				// 获取未上传的温度数据
				Group<TemperatureData> datas = dataHelper
						.queryUnUpLoadTemperatureDataByBabyID(item.getId());
				if (datas != null && datas.size() > 0) {
					BaseData result = app
							.getNetWorKManager()
							.uploadTemperature(
									generateTemperatureData(item.getId(), datas));
					if (result.code == 1) {
						LoggerTool.d(TAG, "upload data successful.");
						dataHelper.updateTemperatureDataStatus(item.getId());
					}
				}
			}
			// 同步统计数据
			FangXinBaoApplication.getApplication(mContext).requestMonthStatisticsData();
			//刷新主界面数据
			FangXinBaoApplication.notifyHandlers(new String[]{UserMainActivity.class.getName()}, CommonAction.ACTION_REFRESH_RECENT_DATA, null);
			
		}

		return null;
	}

	private void startSynchroTemperatureData() {
		String updateDate = Preferences.getSynchronousDate(mContext);
		// 判断是否需要同步数据
		if (TextUtils.isEmpty(updateDate)
				|| VeDate.getBetweenDay(new Date(),
						VeDate.strToDateNormal(updateDate)) > 1) {

		}

	}

	private String generateTemperatureData(String babyid,
			Group<TemperatureData> data) {
		JSONObject result = new JSONObject();
		StringBuilder sb = new StringBuilder();
		for (TemperatureData item : data) {
			sb.append(item.getTime());
			sb.append(",");
			sb.append(item.getTemperature());
			sb.append("|");
		}
		try {
			result.put(babyid, sb.substring(0, sb.length() - 1).toString());
			LoggerTool.d(TAG, result.toString());
		} catch (JSONException e) {

		}
		return result.toString();
	}

	public Void manualExcute(Void... params) {
		return doInBackground(params);
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		executeFinished();
	}
}
