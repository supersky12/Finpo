package com.yujunkang.fangxinbao.activity.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;


import com.baidu.frontia.api.FrontiaSocialShareContent;
import com.baidu.frontia.api.FrontiaSocialShareListener;
import com.baidu.frontia.api.FrontiaSocialShareContent.FrontiaIMediaObject;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.activity.base.SinaShareActivity;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.app.FangXinBaoSettings;
import com.yujunkang.fangxinbao.control.TemperatureCureView;
import com.yujunkang.fangxinbao.control.calendar.CalendarCardPager;
import com.yujunkang.fangxinbao.control.calendar.CalendarCardPager.OnDateSelectedListener;
import com.yujunkang.fangxinbao.database.DBHelper;
import com.yujunkang.fangxinbao.database.TemperatureDataHelper;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.method.Method;
import com.yujunkang.fangxinbao.method.VeDate;
import com.yujunkang.fangxinbao.model.Baby;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.TemperatureData;
import com.yujunkang.fangxinbao.model.Tip;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.parser.PerDayTemperatureDataParser;
import com.yujunkang.fangxinbao.parser.TipParser;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedBackgroundListener;
import com.yujunkang.fangxinbao.task.AsyncTaskWithLoadingDialog;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.utility.CalendarUtil;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;
import com.yujunkang.fangxinbao.utility.FocusChangedUtils;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.ShareUtils;
import com.yujunkang.fangxinbao.utility.UiUtils;

/**
 * 
 * @date 2014-6-16
 * @author xieb
 * 
 */
public class BabyHistoryRecordActivity extends SinaShareActivity {
	private static final String TAG = "BabyHistoryRecordActivity";
	public static final String INTENT_EXTRA_BABY = DataConstants.PACKAGE_NAME
			+ ".BabyHistoryRecordActivity.INTENT_EXTRA_BABY";
	public static final String INTENT_EXTRA_MONTH_DATA = DataConstants.PACKAGE_NAME
			+ ".BabyHistoryRecordActivity.INTENT_EXTRA_MONTH_DATA";

	private static final int REQUEST_ACTIVITY_CODE_EDIT_TIP = 1;
	/**
	 * 控件
	 */
	private CalendarCardPager calendar_view;
	private TextView tv_title;
	private TemperatureCureView temperature_curveView;
	private TextView btn_more;
	private TextView tv_week;
	private TextView tv_holiday;
	private TextView tv_birthday;
	private View ll_holiday;
	private View ll_edit_tip;
	private View ll_note;
	private View tv_add;
	private TextView tv_note;
	private View btn_edit_note;
	private View tv_empty_data;
	private View btn_today;
	private View sliding_prompt;
	private View btn_share;
	private View ll_cure;

	private boolean isExpand = true;
	private int remailHeight;
	private int calendarOriginalHeight;// 日历控件的原始高度
	private Baby mBaby = null;
	private Date mSelectedDate = null;
	private Tip mCurrentTip = null;
	private Handler handler = new Handler();
	private StateHolder mStateHolder = new StateHolder();
	private TemperatureDataHelper DataManager;
	private HashMap<String, String> monthDatas = null;
	private User mUser;
	private Bitmap captureScreen;
	private String shareTextContent;
	private int todayItem = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.baby_health_history_activity);
		init();
		initControl();
		ensureUi();
		// 延迟计算曲线图的高度
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				calendarOriginalHeight = calendar_view.getHeight();
				remailHeight = calendar_view.getRemainHeight();
				LayoutParams params = (LayoutParams) temperature_curveView
						.getLayoutParams();
				params.height = calendarOriginalHeight - remailHeight;
				temperature_curveView.setLayoutParams(params);
				startGetDayTemperatureTask(mSelectedDate);
				todayItem = calendar_view.getCurrentItem();
			}
		}, 500);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();
		Intent intent = getIntent();
		if (intent.hasExtra(INTENT_EXTRA_BABY)) {
			mBaby = intent.getParcelableExtra(INTENT_EXTRA_BABY);
		}
		if (intent.hasExtra(INTENT_EXTRA_MONTH_DATA)) {
			monthDatas = (HashMap<String, String>) intent
					.getSerializableExtra(INTENT_EXTRA_MONTH_DATA);
		}
		DataManager = TemperatureDataHelper.getDBInstance(getSelfContext());
		mUser = Preferences.getUserCommonInfo(getSelfContext());
	}

	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 编辑备忘录
		case R.id.btn_edit_note: {
			startEditTipActivity(2);
			break;
		}
		// 添加备忘录
		case R.id.ll_note: {
			startEditTipActivity(1);
			break;
		}
		case R.id.sliding_prompt: {
			Preferences.storeFirstShowSlidingPrompt(getSelfContext());
			sliding_prompt.setVisibility(View.GONE);
			break;
		}
		case R.id.btn_share: {
			// ShareData share = null;
			// if (FangXinBaoSettings.CODE_DEBUG) {
			// share = new ShareData();
			// share.setWeixinTitle("放心宝");
			// share.setWeixinUrl("www.baidu.com");
			// share.setWeixinMsg("测试");
			// Bitmap imageData = BitmapFactory.decodeResource(
			// getResources(), R.drawable.ic_launcher);
			// share.setWeixinBytes(BitmapUtils.bmpToByteArray(imageData));
			// share.setWeixinApiType(ShareData.WEBPAGE);
			// }

			if (mCurrentTip != null
					&& !TextUtils.isEmpty(mCurrentTip.getContent())) {
				shareTextContent = mCurrentTip.getContent();
			} else {
				shareTextContent = getString(R.string.app_name);
			}
			// 释放内存
			if (captureScreen != null && !captureScreen.isRecycled()) {
				captureScreen.recycle();
			}
			captureScreen = Method.catchScreen(getSelfContext());
			FrontiaSocialShareContent shareContent = ShareUtils
					.getShareContent(getSelfContext(),
							getString(R.string.app_name), shareTextContent,
							getString(R.string.share_url), captureScreen);
			shareContent.setWXMediaObjectType(FrontiaIMediaObject.TYPE_IMAGE);
			shareContent.setQQRequestType(FrontiaIMediaObject.TYPE_IMAGE);
			ShareUtils.showShareContent(getSelfContext(), shareContent,
					shareListener, this);
			break;
		}
		case R.id.btn_today: {
			int currentItem = calendar_view.getCurrentItem();
			if (currentItem != todayItem) {
				calendar_view.setCurrentItem(todayItem,true);
			}
			break;
		}

		case R.id.btn_more: {
			if (isExpand) {

				final ViewGroup.LayoutParams lp = calendar_view
						.getLayoutParams();
				final int originalHeight = calendarOriginalHeight;
				ValueAnimator animator = ValueAnimator.ofInt(originalHeight,
						remailHeight);
				animator.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(final Animator animator) {
						isExpand = false;
						
					}

					@Override
					public void onAnimationStart(Animator animation) {
						// TODO Auto-generated method stub
						super.onAnimationStart(animation);
						ll_cure.setVisibility(View.VISIBLE);
					}

				});
				animator.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(
							final ValueAnimator valueAnimator) {
						lp.height = (Integer) valueAnimator.getAnimatedValue();
						calendar_view.setLayoutParams(lp);
					}
				});
				animator.setDuration(600);
				animator.start();
				btn_more.setText(getString(R.string.temperature_expand));
			} else {
				ValueAnimator heightAnimator = ValueAnimator.ofInt(
						remailHeight, calendarOriginalHeight);
				heightAnimator.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(final ValueAnimator animation) {
						ViewGroup.LayoutParams layoutParams = calendar_view
								.getLayoutParams();
						layoutParams.height = (Integer) animation
								.getAnimatedValue();
						calendar_view.setLayoutParams(layoutParams);
					}
				});
				heightAnimator.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						isExpand = true;
						ll_cure.setVisibility(View.GONE);
					}
				});
				heightAnimator.setDuration(600);
				heightAnimator.start();
				btn_more.setText(getString(R.string.temperature_collapse));
			}

			break;
		}
		}
	}

	/**
	 * 授权回调
	 */
	private FrontiaSocialShareListener shareListener = new FrontiaSocialShareListener() {

		@Override
		public void onSuccess() {
			LoggerTool.d(TAG, "share success");
		}

		@Override
		public void onFailure(int errCode, String errMsg) {
			LoggerTool.d(TAG, String.format(
					"share errCode : %s ,share errMsg : %s ", errCode, errMsg));
		}

		@Override
		public void onCancel() {
			LoggerTool.d(TAG, "cancel ");
		}
	};

	/**
	 * 启动编辑备忘录窗体
	 * 
	 * @param type
	 *            1:添加 2:编辑
	 */
	private void startEditTipActivity(int type) {
		Intent intent = new Intent(getSelfContext(),
				EditHealthTipActivity.class);
		if (type == 1) {
			intent.putExtra(EditHealthTipActivity.INTENT_EXTRA_BABYID,
					mBaby.getId());
		} else if (type == 2) {
			intent.putExtra(EditHealthTipActivity.INTENT_EXTRA_TIP, mCurrentTip);
		}
		intent.putExtra(EditHealthTipActivity.INTENT_EXTRA_DATE, mSelectedDate);
		startActivityForResult(intent, REQUEST_ACTIVITY_CODE_EDIT_TIP);
	}

	private void init() {
		// 初始化日历控件
		mSelectedDate = new Date();

	}

	private void initControl() {
		btn_more = (TextView) findViewById(R.id.btn_more);
		btn_today = findViewById(R.id.btn_today);
		btn_today.setOnClickListener(this);
		calendar_view = (CalendarCardPager) findViewById(R.id.calendar_view);
		tv_title = (TextView) findViewById(R.id.tv_title);
		sliding_prompt = findViewById(R.id.sliding_prompt);
		sliding_prompt.setOnClickListener(this);
		btn_more.setOnClickListener(this);
		temperature_curveView = (TemperatureCureView) findViewById(R.id.temperature_curveView);
		// 节日
		ll_holiday = findViewById(R.id.ll_holiday);
		tv_week = (TextView) findViewById(R.id.tv_week);
		tv_holiday = (TextView) findViewById(R.id.tv_holiday);
		tv_birthday = (TextView) findViewById(R.id.tv_birthday);

		ll_holiday = findViewById(R.id.ll_holiday);
		tv_empty_data = findViewById(R.id.tv_empty_data);
		// 备忘录
		ll_note = findViewById(R.id.ll_note);
		tv_add = findViewById(R.id.tv_add);
		ll_edit_tip = findViewById(R.id.ll_edit_tip);
		tv_note = (TextView) findViewById(R.id.tv_note);
		ll_cure = findViewById(R.id.ll_cure);
		btn_edit_note = findViewById(R.id.btn_edit_note);
		btn_share = findViewById(R.id.btn_share);
		btn_share.setOnClickListener(this);
		FocusChangedUtils.setViewFocusChanged(btn_share);
	}

	private void ensureUi() {
		calendar_view.init(mSelectedDate, monthDatas);
		if (Preferences.IsFirstShowSlidingPrompt(getSelfContext())) {
			sliding_prompt.setVisibility(View.VISIBLE);
		} else {
			sliding_prompt.setVisibility(View.GONE);
		}
		tv_title.setText(VeDate.DateToStr(new Date().getTime(),
				getString(R.string.month_name_format)));
		calendar_view.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, arg0 - Integer.MAX_VALUE / 2);
				tv_title.setText(VeDate.DateToStr(cal.getTime().getTime(),
						getString(R.string.month_name_format)));

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		calendar_view.setDateSelectedListener(new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDateSelected(Date date) {
				if (!VeDate.dateToStr(mSelectedDate).equals(
						VeDate.dateToStr(date))) {
					mSelectedDate = date;
					palyAnimation();
					ensureHolidayUi();
					startGetDayTemperatureTask(mSelectedDate);
					// String memo = DataManager.getMemoInfoByDay(mBaby.getId(),
					// VeDate.dateToStr(mSelectedDate));
					startGetDayTipTask(mSelectedDate);
				}
			}
		});

		
		startGetDayTipTask(mSelectedDate);
		ensureHolidayUi();
	}

	private void ensureHolidayUi() {
		if (mSelectedDate != null) {
			tv_week.setText(VeDate.getWeek(mSelectedDate));
			if (Method.IsChinese(getSelfContext())) {
				tv_holiday.setVisibility(View.VISIBLE);
				tv_holiday.setText(CalendarUtil.getNLInfo(
						VeDate.getYearOfDate(mSelectedDate),
						VeDate.getMonthOfDate(mSelectedDate),
						VeDate.getDayOfDate(mSelectedDate)));
			} else {
				tv_holiday.setVisibility(View.GONE);
			}
			StringBuilder sb = new StringBuilder();
			sb.append(VeDate.getYearOfTwoDates(mSelectedDate,
					VeDate.strToDate(mBaby.getBorn())));
			sb.append(getString(R.string.unit_days));
			tv_birthday.setText(sb.toString());

		}
	}

	/**
	 * 备忘录
	 */
	private void ensureTipUi() {
		if (mCurrentTip != null && !TextUtils.isEmpty(mCurrentTip.getContent())) {
			ll_edit_tip.setVisibility(View.VISIBLE);
			tv_note.setText(mCurrentTip.getContent());
			tv_add.setVisibility(View.GONE);
			ll_note.setOnClickListener(null);
			ll_note.setClickable(false);
			btn_edit_note.setOnClickListener(this);
		} else {
			ll_edit_tip.setVisibility(View.GONE);
			tv_add.setVisibility(View.VISIBLE);
			ll_note.setOnClickListener(this);
			ll_note.setClickable(true);
		}
	}

	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_ACTIVITY_CODE_EDIT_TIP: {
				if (data != null) {
					if (data.hasExtra(EditHealthTipActivity.INTENT_EXTRA_TIP)) {
						mCurrentTip = (Tip) data
								.getParcelableExtra(EditHealthTipActivity.INTENT_EXTRA_TIP);
						ensureTipUi();
					}

				}
				break;
			}

			}

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mStateHolder.cancelAlltasks();
		if (captureScreen != null && !captureScreen.isRecycled()) {
			captureScreen.recycle();
		}
	}

	private void startGetDayTipTask(Date queryDate) {
		FangXinBaoAsyncTask<Tip> mTask = FangXinBaoAsyncTask.createInstance(
				getSelfContext(), UrlManager.URL_GET_DAY_TIP, new TipParser(),
				false);
		Calendar time = Calendar.getInstance();
		time.setTime(queryDate);
		time.set(Calendar.HOUR, 0);
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		mTask.putParameter("bbid", mBaby.getId());
		mTask.putParameter("date",
				String.valueOf(time.getTime().getTime() / 1000));
		mTask.setOnFinishedListener(new OnFinishedListener<Tip>() {
			@Override
			public void onFininshed(Tip result) {
				mCurrentTip = result;
				ensureTipUi();
			}
		});
		mTask.safeExecute();
		putAsyncTask(mTask);
	}

	/**
	 * 请求每天的温度数据
	 * 
	 * @param queryDate
	 */
	private void startGetDayTemperatureTask(Date queryDate) {
		FangXinBaoAsyncTask<Group<TemperatureData>> mTask = FangXinBaoAsyncTask
				.createInstance(getSelfContext(),
						UrlManager.URL_FETCH_DAY_TEMPERATURE_DATA,
						new PerDayTemperatureDataParser(),
						false);
		mTask.putParameter("bbid", mBaby.getId());
		mTask.putParameter("date",
				String.valueOf(VeDate.getDataStamp(queryDate)));
		LoggerTool.d(TAG, String.valueOf(VeDate.getDataStamp(queryDate)));
		mTask.setOnFinishedBackgroundListener(new OnFinishedBackgroundListener<Group<TemperatureData>>() {
			@Override
			public void onFinishedBackground(Group<TemperatureData> result) {
				if (result != null && result.code == 1) {
					// 保存数据库
					DataManager.batchInsertTemperatureData(mUser.getId(),
							mBaby.getId(), result, true);
					result.clear();
					result.addAll(DataManager.queryTemperatureDataByDate(
							mBaby.getId(), VeDate.dateToStr(mSelectedDate)));
				} else {
					// 如果服务器没返回数据,就读取数据库的
					result.addAll(DataManager.queryTemperatureDataByDate(
							mBaby.getId(), VeDate.dateToStr(mSelectedDate)));
					result.setCode(1);
				}
			}
		});
		mTask.setOnFinishedListener(new OnFinishedListener<Group<TemperatureData>>() {
			@Override
			public void onFininshed(Group<TemperatureData> result) {
				if (result != null) {
					if (result.code == 1) {
						if (result.size() > 0) {
							temperature_curveView.showLastTemperatureData(result);
							temperature_curveView.setVisibility(View.VISIBLE);
							tv_empty_data.setVisibility(View.GONE);
						} else {
							temperature_curveView.setVisibility(View.GONE);
							tv_empty_data.setVisibility(View.VISIBLE);
						}
						long now = System.currentTimeMillis();
						long threeDays = now - 3 * 24 * 60 * 60 * 1000;
						if(mSelectedDate.getTime()>threeDays)
						{
							sendRouteNotificationRoute(new String[]{UserMainActivity.class.getName()}, CommonAction.ACTION_REFRESH_RECENT_DATA,null);
						}
					} else {
						UiUtils.showAlertDialog(result.desc, getSelfContext());
					}
				}
			}
		});
		mTask.safeExecute();
		putAsyncTask(mTask);
	}

	private void palyAnimation() {
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(ll_holiday, "translationX", 0,
				ll_holiday.getWidth()), ObjectAnimator.ofFloat(ll_holiday,
				"translationX", ll_holiday.getWidth(), 0));
		set.setStartDelay(300);
		set.setDuration(1000);
		set.start();
	}

	private class QueryTemperatureDataTask extends
			AsyncTaskWithLoadingDialog<Void, Void, Group<TemperatureData>> {

		public QueryTemperatureDataTask() {
			super(getSelfContext(), null, true, false);

		}

		@Override
		protected Group<TemperatureData> doInBackground(Void... params) {

			return DataManager.queryTemperatureDataByDate(mBaby.getId(),
					VeDate.dateToStr(mSelectedDate));
		}

		@Override
		protected void onPostExecute(Group<TemperatureData> result) {
			super.onPostExecute(result);
			mStateHolder.cancelQueryTemperatureDataTask();

		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mStateHolder.cancelQueryTemperatureDataTask();

		}
	}

	private class StateHolder {

		private QueryTemperatureDataTask mQueryTemperatureDataTask; // 得到验证码
		boolean isQueryTemperatureDataTaskRunning = false;

		public StateHolder() {

		}

		public void startQueryTemperatureDataTask() {
			if (!isQueryTemperatureDataTaskRunning) {
				isQueryTemperatureDataTaskRunning = true;
				mQueryTemperatureDataTask = new QueryTemperatureDataTask();
				mQueryTemperatureDataTask.safeExecute();
			}
		}

		public void cancelQueryTemperatureDataTask() {
			if (mQueryTemperatureDataTask != null) {
				mQueryTemperatureDataTask.cancel(true);
				mQueryTemperatureDataTask = null;
			}
			isQueryTemperatureDataTaskRunning = false;

		}

		public void cancelAlltasks() {

			cancelQueryTemperatureDataTask();
		}
	}

	@Override
	public void executeShare() {
		doMultiMessageShare(shareTextContent, captureScreen);

	}
}
