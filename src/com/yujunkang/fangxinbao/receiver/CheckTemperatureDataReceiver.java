package com.yujunkang.fangxinbao.receiver;


import com.yujunkang.fangxinbao.activity.user.UserMainActivity;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.app.FangXinBaoSettings;
import com.yujunkang.fangxinbao.task.UploadTemperatureTask;
import com.yujunkang.fangxinbao.utility.LoggerTool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * @date 2014-7-28
 * @author xieb
 * 
 */
public class CheckTemperatureDataReceiver extends BroadcastReceiver {
	private static final boolean DEBUG = FangXinBaoSettings.PINGS_DEBUG;
	private static final String TAG = "CheckTemperatureDataReceiver";
	private FangXinBaoApplication mApplication;
	private Context mContext;
	public static final int NOTIFY_MESSAGE_ID = 1;
	public static final int SYNCHRO_MESSAGE_ID = 2;
	private StateHolder mStateHolder = new StateHolder();

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		
		LoggerTool.i(TAG, "notify test.");
		
		mApplication=	FangXinBaoApplication.getApplication(mContext);
		mApplication.synchroTemperatureData();
		//startTask();
	}

	private void startTask() {
		mStateHolder.startUploadTemperatureTask();
	}


	private class StateHolder {
		private UploadTemperatureTask mUploadTemperatureTask; //
		boolean isUploadTemperatureTaskRunning = false;

		public StateHolder() {

		}

		public void startUploadTemperatureTask() {
			if (!isUploadTemperatureTaskRunning) {
				isUploadTemperatureTaskRunning = true;
				mUploadTemperatureTask = new UploadTemperatureTask(mContext,false);
				mUploadTemperatureTask.safeExecute();
			}
		}

		public void cancelUploadTemperatureTask() {
			if (mUploadTemperatureTask != null) {
				mUploadTemperatureTask.cancel(true);
				mUploadTemperatureTask = null;
			}
			isUploadTemperatureTaskRunning = false;

		}

		public void cancelAlltasks() {
			cancelUploadTemperatureTask();
		}
	}
}
