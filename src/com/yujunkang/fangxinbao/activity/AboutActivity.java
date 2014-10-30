package com.yujunkang.fangxinbao.activity;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.control.TableItemView;
import com.yujunkang.fangxinbao.method.Method;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.UiUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/**
 * 
 * @date 2014-6-17
 * @author xieb
 * 
 */
public class AboutActivity extends ActivityWrapper {
	
	private static final String TAG = "AboutActivity";
	public static final String INTENT_EXTRA_NEW_VERSION = DataConstants.PACKAGE_NAME
			+ ".AboutActivity.INTENT_EXTRA_NEW_VERSION";
	private TableItemView btn_sina_weibo;
	private TableItemView btn_call_phone;
	private View btn_check_version;
	private View btn_send_feedback;
	private View btn_manual;
	private TextView tv_version;
	private View iv_version;
	
	
	private boolean hasNewVersion = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		Intent intent = getIntent();
		if(intent.hasExtra(INTENT_EXTRA_NEW_VERSION));
		{
			hasNewVersion = intent.getBooleanExtra(INTENT_EXTRA_NEW_VERSION, false);
			LoggerTool.d(TAG, "yes");
		}
		initControl();
		ensureUi();
		
	}

	private void initControl() {
		btn_sina_weibo = (TableItemView) findViewById(R.id.btn_sina_weibo);
		btn_call_phone = (TableItemView) findViewById(R.id.btn_call_phone);
		btn_check_version = findViewById(R.id.btn_check_version);
		btn_send_feedback = findViewById(R.id.btn_send_feedback);
		btn_manual = findViewById(R.id.btn_manual);
		tv_version = (TextView) findViewById(R.id.tv_version);
		iv_version= findViewById(R.id.iv_version);
		btn_sina_weibo.setOnClickListener(this);
		btn_call_phone.setOnClickListener(this);
		btn_check_version.setOnClickListener(this);
		btn_manual.setOnClickListener(this);
		btn_send_feedback.setOnClickListener(this);
	}

	private void ensureUi() {
		StringBuilder versionSb = new StringBuilder(
				getString(R.string.app_name));
		versionSb.append(" ");
		versionSb.append(DataConstants.CVER);
		tv_version.setText(versionSb.toString());
		btn_sina_weibo.setRightText("@"
				+ getString(R.string.about_sina_fangxinbao));
		btn_call_phone.setRightText(Preferences
				.getServicePhone(getSelfContext()));
		if(hasNewVersion)
		{
			iv_version.setVisibility(View.VISIBLE);
		}
		else
		{
			iv_version.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sina_weibo: {
			startWebViewActivity(getString(R.string.weibo_url), null);
			break;
		}
		case R.id.btn_call_phone: {
			String servicePhone = Preferences.getServicePhone(getSelfContext());
			if (!TextUtils.isEmpty(servicePhone)) {
				Intent intent = new Intent(Intent.ACTION_DIAL,
						Uri.parse("tel://" + servicePhone));
				startActivity(intent);
			}

			break;
		}
		case R.id.btn_check_version: {
			if (hasNewVersion) {
				UmengUpdateAgent.forceUpdate(getSelfContext());
			} else {
				UiUtils.showAlertDialog(getString(R.string.no_new_version), getSelfContext());
			}
			break;
		}
		case R.id.btn_send_feedback: {
			startFeedbackActivity();
			break;
		}
		case R.id.btn_manual: {
			String url = Method.getUsrManual(getSelfContext());
			if (!TextUtils.isEmpty(url)) {
				// startWebViewActivity(url,getString(R.string.user_manual_title));
				startWebViewActivity(url);
			} else {
				UiUtils.showAlertDialog(getString(R.string.use_manual_error),
						getSelfContext());
			}
			break;
		}
		}

	}

	private void startWebViewActivity(String url) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		Uri content_url = Uri.parse(url);
		intent.setData(content_url);

		startActivity(intent);

	}

	private void startWebViewActivity(String url, String title) {
		Intent intent = new Intent(getSelfContext(), WebViewActivity.class);
		intent.putExtra(WebViewActivity.INTENT_EXTRA_URL, url);
		intent.putExtra(WebViewActivity.INTENT_EXTRA_TITLE, title);

		startActivity(intent);

	}

	private void startFeedbackActivity() {
		Intent intent = new Intent(getSelfContext(), FeedBackActivity.class);
		startActivity(intent);

	}

}
