package com.yujunkang.fangxinbao.activity;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.control.WrapWebView;
import com.yujunkang.fangxinbao.control.WrapWebView.WebViewChromeListener;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * @date 2014-8-11
 * @author xieb
 * 
 */
public class WebViewActivity extends ActivityWrapper {

	private static final String TAG = "WebViewActivity";
	public static final String INTENT_EXTRA_URL = DataConstants.PACKAGE_NAME
			+ ".WebViewActivity.INTENT_EXTRA_URL";
	public static final String INTENT_EXTRA_TITLE = DataConstants.PACKAGE_NAME
			+ ".WebViewActivity.INTENT_EXTRA_TITLE";
	private WrapWebView wv_desc;
	private View waitingView;
	private WebView mWebView;

	private ProgressBar progress_loading;
	private TextView tv_title;
	private String url;
	private String mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_activity);
		Intent intent = getIntent();
		if (intent.hasExtra(INTENT_EXTRA_URL)) {
			url = intent.getStringExtra(INTENT_EXTRA_URL);
		} else {
			finish();
			return;
		}
		if (intent.hasExtra(INTENT_EXTRA_TITLE)) {
			mTitle = intent.getStringExtra(INTENT_EXTRA_TITLE);
		}
		initControl();
	}

	private void initControl() {
		wv_desc = (WrapWebView) findViewById(R.id.wv_desc);
		mWebView = wv_desc.getWebView();
		tv_title = (TextView) findViewById(R.id.tv_title);
		if (!TextUtils.isEmpty(url)) {
			LoggerTool.d(TAG, url);
			StringBuilder sbUrl = new StringBuilder(url);
			if (!url.contains("?")) {
				sbUrl.append("?");
			} else {
				if (!url.endsWith("&") && !url.endsWith("?")) {
					sbUrl.append("&");
				}
			}
			sbUrl.append("iseeu=1");
			wv_desc.setListener(new WebViewChromeListener() {
				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {

				}

				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {

				}

				@Override
				public void onPageFinished(WebView view, String url) {
					// wv_desc.loadUrl("javascript:function get_html(){window.Android.excuteHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');}\nwindow.setTimeout(get_html, 500);");
				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}

				@Override
				public void onReceivedTitle(WebView view, String title) {
					if (!TextUtils.isEmpty(mTitle)) {
						tv_title.setText(mTitle);
					} else {
						tv_title.setText(title);
					}

				}

				@Override
				public void onReceivedShareInfo(String title,
						String webShareLink, String webShareDes,
						Bitmap webShareImg) {
					// TODO Auto-generated method stub
					
				}
			});
			wv_desc.loadUrl(sbUrl.toString());

		}

	}

	@Override
	public void onClick(View v) {

	}

}
