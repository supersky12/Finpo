package com.yujunkang.fangxinbao.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboShareException;
import com.sina.weibo.sdk.utils.Utility;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.model.ISinaWeiboShare;
import com.yujunkang.fangxinbao.utility.BitmapUtils;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;

/**
 * 
 * @date 2014-8-18
 * @author xieb
 * 
 */
public abstract class SinaShareActivity extends ActivityWrapper implements
		IWeiboHandler.Response, ISinaWeiboShare {
	/** 微博微博分享接口实例 */
	private IWeiboShareAPI mWeiboShareAPI = null;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// 创建微博分享接口实例
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this,
				DataConstants.SINA_APP_ID);
		// 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
		// 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
		// NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
		

		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
		// 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
		// 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
		// 失败返回 false，不调用上述回调
		if (savedInstanceState != null) {
			mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
		}
	}

	/**
	 * @see {@link Activity#onNewIntent}
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		// 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
		// 来接收微博客户端返回的数据；执行成功，返回 true，并调用
		// {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
		mWeiboShareAPI.handleWeiboResponse(intent, this);
	}

	/**
	 * 接收微客户端博请求的数据。 当微博客户端唤起当前应用并进行分享时，该方法被调用。
	 * 
	 * @param baseRequest
	 *            微博请求数据对象
	 * @see {@link IWeiboShareAPI#handleWeiboRequest}
	 */
	@Override
	public void onResponse(BaseResponse baseResp) {
		LoggerTool.d(getClass().getSimpleName(), "onResponse:"
				+ baseResp.errCode);
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			Toast.makeText(this, R.string.weibosdk_toast_share_success,
					Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			Toast.makeText(this, R.string.weibosdk_toast_share_canceled,
					Toast.LENGTH_LONG).show();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			Toast.makeText(
					this,
					getString(R.string.weibosdk_toast_share_failed)
							+ "Error Message: " + baseResp.errMsg,
					Toast.LENGTH_LONG).show();
			break;
		}
	}

	public void doTextShare(String message) {
		try {
			mWeiboShareAPI.registerApp();
			// 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
			if (!mWeiboShareAPI.isWeiboAppInstalled()) {
				mWeiboShareAPI
						.registerWeiboDownloadListener(new IWeiboDownloadListener() {
							@Override
							public void onCancel() {

							}

						});
			} else {
				if (mWeiboShareAPI.checkEnvironment(true)) {
					if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
						// 1. 初始化微博的分享消息
						// 用户可以分享文本、图片、网页、音乐、视频中的一种
						WeiboMessage weiboMessage = new WeiboMessage();

						weiboMessage.mediaObject = getTextObj(message);
						sendMessage(weiboMessage);
					} else {
						Toast.makeText(this,
								R.string.weibosdk_not_support_api_hint,
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		} catch (WeiboShareException e) {

			Toast.makeText(getSelfContext(), e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	public void doImageShare(Bitmap image) {
		try {
			mWeiboShareAPI.registerApp();
			// 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
			// 如果未安装微博客户端，设置下载微博对应的回调
			if (!mWeiboShareAPI.isWeiboAppInstalled()) {
				mWeiboShareAPI
						.registerWeiboDownloadListener(new IWeiboDownloadListener() {
							@Override
							public void onCancel() {

							}

						});
			} else {
				if (mWeiboShareAPI.checkEnvironment(true)) {
					if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
						// 1. 初始化微博的分享消息
						// 用户可以分享文本、图片、网页、音乐、视频中的一种
						WeiboMessage weiboMessage = new WeiboMessage();

						weiboMessage.mediaObject = getImageObj(image);
						sendMessage(weiboMessage);
					} else {
						Toast.makeText(this,
								R.string.weibosdk_not_support_api_hint,
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		} catch (WeiboShareException e) {

			Toast.makeText(getSelfContext(), e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	public void doMultiMessageShare(String mesage, Bitmap image) {
		try {
			mWeiboShareAPI.registerApp();
			// 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
			// 如果未安装微博客户端，设置下载微博对应的回调
			if (!mWeiboShareAPI.isWeiboAppInstalled()) {
				mWeiboShareAPI
						.registerWeiboDownloadListener(new IWeiboDownloadListener() {
							@Override
							public void onCancel() {

							}

						});
			} else {
				if (mWeiboShareAPI.checkEnvironment(true)) {
					if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
						int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
						if (supportApi >= 10351 /* ApiUtils.BUILD_INT_VER_2_2 */) {
							// 1. 初始化微博的分享消息
							WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
							if (!TextUtils.isEmpty(mesage)) {
								weiboMessage.textObject = getTextObj(mesage);
							}
							if (image != null) {
								weiboMessage.imageObject = getImageObj(image);
							}
							sendMultiMessage(weiboMessage);
						} else {
							doImageShare(image);
						}
					} else {
						Toast.makeText(this,
								R.string.weibosdk_not_support_api_hint,
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		} catch (WeiboShareException e) {
			Toast.makeText(getSelfContext(), e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	public void doMultiMessageShare(String title, String url, String message,
			Bitmap image) {
		try {
			mWeiboShareAPI.registerApp();
			// 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
			if (!mWeiboShareAPI.isWeiboAppInstalled()) {
				mWeiboShareAPI
						.registerWeiboDownloadListener(new IWeiboDownloadListener() {
							@Override
							public void onCancel() {

							}

						});
			} else {
				if (mWeiboShareAPI.checkEnvironment(true)) {
					if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
						int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
						if (supportApi >= 10351 /* ApiUtils.BUILD_INT_VER_2_2 */) {
							// 1. 初始化微博的分享消息
							WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
							if (image != null) {
								weiboMessage.imageObject = getImageObj(image);
							}
							if (!TextUtils.isEmpty(url)) {
								weiboMessage.mediaObject = getWebpageObj(title,
										url, message, image);
							}
							sendMultiMessage(weiboMessage);
						} else {
							doImageShare(image);
						}
					} else {
						Toast.makeText(this,
								R.string.weibosdk_not_support_api_hint,
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		} catch (WeiboShareException e) {
			Toast.makeText(getSelfContext(), e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。 注意：当
	 * {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
	 * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
	 * 
	 */
	private void sendMultiMessage(WeiboMultiMessage weiboMessage) {
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;
		mWeiboShareAPI.sendRequest(request);
	}

	public void doWebpageShare(String title, String url, String message,
			Bitmap image) {
		try {
			mWeiboShareAPI.registerApp();
			// 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
			if (!mWeiboShareAPI.isWeiboAppInstalled()) {
				mWeiboShareAPI
						.registerWeiboDownloadListener(new IWeiboDownloadListener() {
							@Override
							public void onCancel() {

							}

						});
			} else {
				if (mWeiboShareAPI.checkEnvironment(true)) {
					if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
						// 1. 初始化微博的分享消息
						// 用户可以分享文本、图片、网页、音乐、视频中的一种
						WeiboMessage weiboMessage = new WeiboMessage();

						weiboMessage.mediaObject = getWebpageObj(title, url,
								message, image);
						sendMessage(weiboMessage);
					} else {
						Toast.makeText(this,
								R.string.weibosdk_not_support_api_hint,
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		} catch (WeiboShareException e) {

			Toast.makeText(getSelfContext(), e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。
	 * 
	 * @see {@link #sendMultiMessage} 或者 {@link #sendSingleMessage}
	 */
	private void sendMessage(WeiboMessage message) {

		SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.message = message;

		mWeiboShareAPI.sendRequest(request);
	}

	/**
	 * 创建文本消息对象。
	 * 
	 * @return 文本消息对象。
	 */
	private TextObject getTextObj(String text) {
		TextObject textObject = new TextObject();
		textObject.text = text;
		return textObject;
	}

	/**
	 * 创建图片消息对象。
	 * 
	 * @return 图片消息对象。
	 */
	private ImageObject getImageObj(Bitmap image) {
		ImageObject imageObject = new ImageObject();

		imageObject.setImageObject(image);
		return imageObject;
	}

	/**
	 * 创建多媒体（网页）消息对象。
	 * 
	 * @return 多媒体（网页）消息对象。
	 */
	private WebpageObject getWebpageObj(String title, String url,
			String message, Bitmap image) {

		WebpageObject mediaObject = new WebpageObject();
		mediaObject.identify = Utility.generateGUID();
		mediaObject.title = title;
		mediaObject.description = message;
		mediaObject.thumbData = BitmapUtils.bmpToByteArray(image);
		// 设置 Bitmap 类型的图片到视频对象里
		// mediaObject.setThumbImage(image);
		mediaObject.actionUrl = url;
		mediaObject.defaultText = message;
		return mediaObject;
	}

}
