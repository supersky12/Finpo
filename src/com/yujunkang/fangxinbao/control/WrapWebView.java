package com.yujunkang.fangxinbao.control;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.baidu.frontia.api.FrontiaSocialShareContent;
import com.baidu.frontia.api.FrontiaSocialShareContent.FrontiaIMediaObject;
import com.baidu.frontia.api.FrontiaSocialShareContent.FrontiaIQQReqestType;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.app.FangXinBaoApplication;
import com.yujunkang.fangxinbao.cache.CacheableBitmapDrawable;
import com.yujunkang.fangxinbao.model.ShareData;
import com.yujunkang.fangxinbao.task.AsyncTaskWithLoadingDialog;
import com.yujunkang.fangxinbao.utility.BitmapUtils;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.ShareUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.GeolocationPermissions.Callback;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * @date 2014-7-29
 * @author xieb
 * 
 */
public class WrapWebView extends LinearLayout {
	private static final String TAG = "WrapWebView";
	private WebViewChromeListener mListener;
	private WebView mWebView;
	private View waitingView;

	private ProgressBar progress_loading;

	private int exactlyWidth = 0;
	private int exactlyHeigth = 0;
	private String webShareTitle = "";// 网页当中包含的分享标题
	private Bitmap webShareImg = null;// 网页当中包含的分享图片链接
	private String webShareDes = "";// 网页当中包含的分享内容描述
	private String webShareLink = "";// 网页当中包含的分享链接
	private Bitmap webShareWeiboImg = null;// 网页当中包含的微博分享图片
	private String pageUrl;
	private StateHolder mStateHolder = new StateHolder();
	private boolean isFetchHtml = false;

	public WrapWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.wrap_webview, this);

		setOrientation(LinearLayout.VERTICAL);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		setLayoutParams(params);
		ensureUi();
		init();
	}

	public WrapWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public WrapWebView(Context context) {
		super(context);
		init();
	}

	public WebViewChromeListener getListener() {
		return mListener;
	}

	public void setListener(WebViewChromeListener Listener) {
		this.mListener = Listener;
	}

	private void ensureUi() {
		waitingView = LayoutInflater.from(getContext()).inflate(
				R.layout.webview_loading, null);
		progress_loading = (ProgressBar) findViewById(R.id.progress_loading);
		mWebView = (WebView) findViewById(R.id.webview);
	}

	public WebView getWebView() {
		return mWebView;
	}

	public void loadAd(String url, int width, int height) {
		mWebView.loadUrl(url);
		exactlyWidth = width;
		exactlyHeigth = height;
	}

	public void loadUrl(String url) {
		loadUrl(url, false);
	}

	public void loadUrl(String url, boolean fetchHtml) {
		mWebView.loadUrl(url);
		isFetchHtml = fetchHtml;
	}

	private void init() {

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setAllowFileAccess(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setSupportZoom(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setDatabaseEnabled(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setLightTouchEnabled(true);
		// 启用地理定位
		webSettings.setGeolocationEnabled(true);
		mWebView.addJavascriptInterface(new WebAppInterface(), "Android");
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (mListener != null) {
					mListener.shouldOverrideUrlLoading(view, url);
				}
				return true;

			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				// 消除进度框
				if (waitingView.getParent() != null) {
					((Activity) getContext()).getWindowManager().removeView(
							waitingView);
				}
				if (mListener != null) {
					mListener.onReceivedError(view, errorCode, description,
							failingUrl);
				}
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if (mListener != null) {
					mListener.onPageStarted(view, url, favicon);
				}
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				LoggerTool.d(TAG, url);
				if (exactlyWidth > 0 && exactlyHeigth > 0) {
					AlphaAnimation alphaAni = new AlphaAnimation(0.0f, 1.0f);
					alphaAni.setDuration(1000);
					startAnimation(alphaAni);
					int width = getWidth();
					int height = Math.round(width * exactlyHeigth
							/ exactlyWidth);
					LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
					params.height = height;
					setLayoutParams(params);
				}
				if (isFetchHtml) {
					view.loadUrl("javascript:window.Android.excuteHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
				}
				if (mListener != null) {
					mListener.onPageFinished(view, url);
				}
			}
		});
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress < 100) {
					if (progress_loading.getVisibility() == View.GONE)
						progress_loading.setVisibility(View.VISIBLE);
					progress_loading.setProgress(newProgress);
				} else {
					progress_loading.setProgress(100);

					Handler handler = new Handler() {
						@Override
						public void handleMessage(Message msg) {
							super.handleMessage(msg);
							Animation animation = AnimationUtils.loadAnimation(
									getContext(), R.anim.webview_progress);
							progress_loading.startAnimation(animation);
							progress_loading.setVisibility(View.GONE);

							// 消除进度框
							if (waitingView.getParent() != null) {
								((Activity) getContext()).getWindowManager()
										.removeView(waitingView);
							}
						}
					};
					handler.sendEmptyMessageDelayed(1, 500);
				}
				super.onProgressChanged(view, newProgress);
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				if (mListener != null) {
					mListener.onReceivedTitle(view, title);
				}

			}

			@Override
			public void onGeolocationPermissionsShowPrompt(String origin,
					Callback callback) {
				callback.invoke(origin, true, false);
				super.onGeolocationPermissionsShowPrompt(origin, callback);
			}

		});

		mWebView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				LoggerTool.d(TAG, url);
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				getContext().startActivity(intent);
			}
		});

		if (waitingView.getParent() == null) {
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_APPLICATION,
					WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
							| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
					PixelFormat.TRANSLUCENT);
			try {
				((Activity) getContext()).getWindowManager().addView(
						waitingView, lp);
			} catch (Exception e) {
				LoggerTool.e(TAG, "Exception", e);
			}
		}
	}

	public interface WebViewChromeListener {

		public boolean shouldOverrideUrlLoading(WebView view, String url);

		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl);

		public void onPageStarted(WebView view, String url, Bitmap favicon);

		public void onPageFinished(WebView view, String url);

		public void onReceivedTitle(WebView view, String title);

		public void onReceivedShareInfo(String title, String webShareLink,
				String webShareDes, Bitmap webShareImg);

	}

	public ShareData getShareData() {
		ShareData shareData = new ShareData();

		// 微博相关
		shareData.setWeiboText(webShareTitle);
		shareData.setWeiboImg(webShareWeiboImg);

		// 微信相关
		shareData.setWeixinTitle(webShareTitle);
		shareData.setWeixinUrl(webShareLink);
		shareData.setWeixinMsg(webShareDes);
		shareData.setWeixinBytes(BitmapUtils.bmpToByteArray(webShareImg));
		shareData.setWeixinApiType(ShareData.WEBPAGE);

		// 朋友圈相关
		shareData.setFriendcircleTitle(webShareTitle);
		shareData.setFriendcircleUrl(webShareLink);
		shareData.setFriendcircleMsg(webShareDes);
		shareData.setFriendcircleBytes(BitmapUtils.bmpToByteArray(webShareImg));
		shareData.setFriendcircleApiType(ShareData.WEBPAGE);

		// 邮件相关
		shareData.setMailSubject(webShareTitle);
		shareData.setMailContent(webShareDes);
		// shareData.setMailImg(shareImg);

		return shareData;
	}

	/**
	 * 初始化网页当中的分享节点信息
	 */
	private void initWebShareInfo(String html) {
		String shareImageUrl = getWebShareInfo(html,
				"<div id=\"fenxiang_img\">");
		webShareTitle = getWebShareInfo(html, "<div id=\"fenxiang_title\">");
		webShareDes = getWebShareInfo(html, "<div id=\"fenxiang_desc\">");
		webShareLink = getWebShareInfo(html, "<div id=\"fenxiang_link\">");
		initWebShareInfo(webShareTitle, shareImageUrl, webShareDes,
				webShareLink);
	}

	/**
	 * 根据指定标记查找分享相关信息
	 * 
	 * @param url
	 * @param tag
	 * @return
	 */
	private String getWebShareInfo(String html, String tag) {
		if (TextUtils.isEmpty(html) || TextUtils.isEmpty(tag)) {
			return "";
		}

		String result = "";
		if (html.contains(tag)) {
			int start = html.indexOf(tag);

			String temp = html.substring(start);
			if (!TextUtils.isEmpty(temp) && temp.contains("</div>")) {
				String[] share = temp.split("</div>");
				if (share != null) {
					for (String str : share) {
						if (!TextUtils.isEmpty(str) && str.startsWith(tag)) {
							result = str.substring(tag.length());
							break;
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * 初始化网页当中的分享节点信息
	 */
	private void initWebShareInfo(String title, String shareImageUrl,
			String fenxiangdesc, String fenxianglink) {
		LoggerTool.d(TAG, shareImageUrl);
		if (!TextUtils.isEmpty(shareImageUrl)) {
			webShareImg = BitmapUtils.getShareImage(shareImageUrl);
		} else {
			webShareImg = null;
		}
		webShareTitle = title;
		webShareDes = fenxiangdesc;
		webShareLink = fenxianglink;

	}

	public FrontiaSocialShareContent getShareContent() {
		FrontiaSocialShareContent shareContent = ShareUtils.getShareContent(
				getContext(), webShareTitle, webShareDes, webShareLink,
				webShareImg);
		shareContent.setWXMediaObjectType(FrontiaIMediaObject.TYPE_URL);
		shareContent.setQQRequestType(FrontiaIQQReqestType.TYPE_DEFAULT);
		return shareContent;
	}

	/**
	 * 解析html
	 * 
	 * 
	 */
	public class FetchHtmlTask extends
			AsyncTaskWithLoadingDialog<String, Void, Void> {

		public FetchHtmlTask(Activity context, boolean dialogEnable) {
			super(context, dialogEnable);
			// TODO Auto-generated constructor stub
		}

		Context mContext;

		@Override
		protected Void doInBackground(String... arg0) {
			String html = arg0[0];

			initWebShareInfo(html);
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mStateHolder.cancelFetchHtmlTask();
			if (mListener != null) {
				mListener.onReceivedShareInfo(webShareTitle, webShareLink,
						webShareDes, webShareImg);
			}
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			mStateHolder.cancelFetchHtmlTask();
		}

	}

	private final class WebAppInterface {

		@JavascriptInterface
		public void excuteHTML(final String html) {
			LoggerTool.d(TAG, html);
			getHandler().post(new Runnable() {

				@Override
				public void run() {
					mStateHolder.startFetchHtmlTask(html);
				}
			});

		}

	}

	private class StateHolder {
		private FetchHtmlTask mFetchHtmlTask;
		private boolean mIsFetchHtmlTaskRunning = false;

		public StateHolder() {

		}

		public void startFetchHtmlTask(String html) {
			if (mIsFetchHtmlTaskRunning == false) {
				mIsFetchHtmlTaskRunning = true;
				mFetchHtmlTask = new FetchHtmlTask((Activity) getContext(),
						false);
				mFetchHtmlTask.safeExecute(html);
			}
		}

		public void cancelFetchHtmlTask() {
			if (mFetchHtmlTask != null) {
				mFetchHtmlTask.cancel(true);
				mFetchHtmlTask = null;

			}
			mIsFetchHtmlTaskRunning = false;
		}

		public void cancelAllTasks() {

			cancelFetchHtmlTask();
		}
	}

}
