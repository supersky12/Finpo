package com.yujunkang.fangxinbao.control;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.yujunkang.fangxinbao.R;

import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.method.VeDate;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 
 * @date 2014-8-4
 * @author xieb
 * 
 */
public class TemperatureCheckRecordView extends TextView {
	private Timer mTimer = null;
	private Handler mHandler = null;
	private Date mLastCheckDate;
	private Date mExpireDate;// 过期时间
	private OnTimeupListener mTimeupListener;
	private Context mContext;

	public TemperatureCheckRecordView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	/*
	 * 时间到达
	 */
	private void onTimup() {
		mLastCheckDate = new Date();
		stopCountDown();
		mHandler = null;

		if (mTimeupListener != null) {
			mTimeupListener.onTimeup();
		}
	}

	public void setOnTimeupListener(OnTimeupListener timeup) {
		mTimeupListener = timeup;
	}

	/*
	 * 更新文字
	 */
	private void updateText() {

		int gap = getGap();
		String time = VeDate.convertSecondesToMinSecFormat(gap);

		if (mLastCheckDate == null) {
			setText(String.format(mContext.getString(R.string.first_checked),
					time));
		} else {
			String currentCheckedTime = VeDate.DateToStr(
					mLastCheckDate.getTime(), "HH:mm");
			setText(String.format(mContext.getString(R.string.checked),
					currentCheckedTime, time));
		}

	}

	private int getGap() {
		int gap = 0;
		try {
			Calendar now = Calendar.getInstance();
			now.setTime(new Date());
			gap = (int) ((mExpireDate.getTime() - now.getTimeInMillis()) / 1000);
			if (gap < 0) {
				gap = 0;
			}
		} catch (Exception e) {
		}
		return gap;
	}

	/**
	 * 开启倒计时
	 */
	public void startCountDown() {
		if (mHandler == null) {
			mExpireDate = VeDate.calculateBySecond(new Date(),
					FangXinBaoApplication.INTERVAL_ACTIVE_DELAY / 1000);
			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					Date now = new Date();
					if (now.after(mExpireDate)) {
						onTimup();
					} else {
						updateText();
					}
					super.handleMessage(msg);
				};
			};
		}

		if (mTimer == null) {
			mTimer = new Timer(true);
			mTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					Message message = new Message();
					message.what = 1;
					mHandler.sendMessage(message);
				}
			}, 0, 1000);
		}
	}

	public void stopCountDown() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		mHandler = null;
		// mLastCheckDate = null;
	}

	public void resetCountDown() {
		stopCountDown();
		mLastCheckDate = null;
	}

	/**
	 * 时间倒计时
	 */
	public interface OnTimeupListener {
		void onTimeup();
	}

}
