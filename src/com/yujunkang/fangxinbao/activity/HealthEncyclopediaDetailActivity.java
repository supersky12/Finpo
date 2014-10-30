package com.yujunkang.fangxinbao.activity;

import com.baidu.frontia.api.FrontiaSocialShareContent;
import com.baidu.frontia.api.FrontiaSocialShareListener;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.base.SinaShareActivity;
import com.yujunkang.fangxinbao.control.WrapWebView;
import com.yujunkang.fangxinbao.control.WrapWebView.WebViewChromeListener;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.model.HealthEncyclopediaInfo;
import com.yujunkang.fangxinbao.parser.BaseDataParser;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;
import com.yujunkang.fangxinbao.utility.FocusChangedUtils;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.ShareUtils;
import com.yujunkang.fangxinbao.utility.UiUtils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * @date 2014-7-28
 * @author xieb
 * 
 */
public class HealthEncyclopediaDetailActivity extends SinaShareActivity {

	private static final String TAG = "HealthEncyclopediaDetailActivity";
	public static final String INTENT_EXTRA_INFO = DataConstants.PACKAGE_NAME
			+ ".HealthEncyclopediaDetailActivity.INTENT_EXTRA_INFO";
	public static final String INTENT_EXTRA_FAVORITE = DataConstants.PACKAGE_NAME
			+ ".HealthEncyclopediaDetailActivity.INTENT_EXTRA_FAVORITE";
	private WrapWebView wv_detail;
	private View btn_share;
	private View btn_favorite;
	private View waitingView;

	private TextView tv_title;
	private ProgressBar progress_loading;
	private FrontiaSocialShareContent baiduShareContent = null;
	private HealthEncyclopediaInfo Data = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.health_encyclopedia_detail_activity);
		initControl();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();
		Intent intent = getIntent();
		if (intent.hasExtra(INTENT_EXTRA_INFO)) {
			Data = (HealthEncyclopediaInfo) intent
					.getParcelableExtra(INTENT_EXTRA_INFO);

		}
	}

	private void initControl() {
		btn_share = findViewById(R.id.btn_share);
		btn_favorite = findViewById(R.id.btn_favorite);
		wv_detail = (WrapWebView) findViewById(R.id.wv_detail);
		tv_title = (TextView) findViewById(R.id.tv_title);
		btn_share.setOnClickListener(this);

		btn_favorite.setOnClickListener(this);
		FocusChangedUtils.setViewFocusChanged(btn_share);
		FocusChangedUtils.setViewFocusChanged(btn_favorite);
		String url = Data.getUrl();
		if (!TextUtils.isEmpty(url)) {
			StringBuilder sbUrl = new StringBuilder(url);
			if (!url.contains("?")) {
				sbUrl.append("?");
			} else {
				if (!url.endsWith("&") && !url.endsWith("?")) {
					sbUrl.append("&");
				}
			}
			sbUrl.append("iseeu=1");
			LoggerTool.d(TAG, sbUrl.toString());
			wv_detail.setListener(new WebViewChromeListener() {
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
					// wv_detail.loadUrl("javascript:function get_html(){window.Android.excuteHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');}\nwindow.setTimeout(get_html, 500);");
					// webView.loadUrl("javascript:window.Android.excuteHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");

				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void onReceivedTitle(WebView view, String title) {
					tv_title.setText(title);

				}

				@Override
				public void onReceivedShareInfo(String title,
						String webShareLink, String webShareDes,
						Bitmap webShareImg) {
					if (!TextUtils.isEmpty(webShareLink) && webShareImg != null
							&& !TextUtils.isEmpty(title)) {
						btn_share.setVisibility(View.VISIBLE);
					} else {
						btn_share.setVisibility(View.GONE);
					}

				}
			});

			wv_detail.loadUrl(sbUrl.toString(), true);
		}
		if (Data.Isfavorite()) {
			btn_favorite.setVisibility(View.GONE);
		} else {
			btn_favorite.setVisibility(View.VISIBLE);
		}
	}

	private void doFavorite() {
		FangXinBaoAsyncTask<BaseData> task = FangXinBaoAsyncTask
				.createInstance(getSelfContext(), UrlManager.URL_FAVORITE,
						new BaseDataParser(), getString(R.string.loading));
		task.putParameter("id", Data.getId());
		task.setOnFinishedListener(new OnFinishedListener<BaseData>() {
			@Override
			public void onFininshed(BaseData result) {
				if (result != null) {
					if (result.code == 1) {
						btn_favorite.setVisibility(View.GONE);
						Bundle data = new Bundle();
						data.putParcelable(INTENT_EXTRA_FAVORITE, Data);
						sendRouteNotificationRoute(
								new String[] {
										HealthEncyclopediaActivity.class
												.getName(),
										SearchHealthEncyclopediaActivity.class
												.getName(),
										FavoriteHealthInfoListActivity.class
												.getName() },
												
								CommonAction.ACTION_FAVORITE, data);
						UiUtils.showAlertDialog(
								getString(R.string.favorite_success),
								getSelfContext());
					} else {
						UiUtils.showAlertDialog(result.desc, getSelfContext());
					}
				}
			}
		});
		task.safeExecute();
		putAsyncTask(task);
	}

	/**
	 * 授权回调
	 */
	private FrontiaSocialShareListener shareListener = new FrontiaSocialShareListener() {

		@Override
		public void onSuccess() {
			Log.d(TAG, "share success");
		}

		@Override
		public void onFailure(int errCode, String errMsg) {
			Log.d(TAG, String.format("share errCode : %s ,share errMsg : %s ",
					errCode, errMsg));
		}

		@Override
		public void onCancel() {
			Log.d(TAG, "cancel ");
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_share: {
			baiduShareContent = wv_detail.getShareContent();
			ShareUtils.showShareContent(getSelfContext(), baiduShareContent,
					shareListener, this);
			break;
		}
		case R.id.btn_favorite: {
			doFavorite();
			break;
		}
		}

	}

	@Override
	public void executeShare() {
		doWebpageShare(baiduShareContent.getTitle(), baiduShareContent.getLinkUrl(), baiduShareContent.getContent(),
			baiduShareContent.getImageData());
//		doMultiMessageShare(baiduShareContent.getTitle(),
//				baiduShareContent.getLinkUrl(), baiduShareContent.getContent(),
//				baiduShareContent.getImageData());
	}
}
