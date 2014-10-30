/**
 * Copyright 2010 xieb
 */
package com.yujunkang.fangxinbao.app;

import com.yujunkang.fangxinbao.R;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


/**
 * @author xieb (xieb@huoli.com)
 * @date 2011-1-21
 */
public abstract class SystemNotificationBuilderBase implements
		SystemNotificationBuilder, ContextProvider {
	private static final String TAG = "SystemNotificationBuilderBase";
	private String mContentText;
	private Context mContext;
	public static final String NOTIFICATION_TYPE_MEASURE_TEMPERATURE = "measure_temperature";
	public static final String NOTIFICATION_TYPE_SYNCHRO = "synchro";

	public SystemNotificationBuilderBase(Context context) {
		this(context, null);
	}

	public SystemNotificationBuilderBase(Context context, String content) {
		this.mContext = context;
		this.mContentText = content;
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return mContext;
	}

	@Override
	public Notification create() {
		// TODO Auto-generated method stub
		Notification notification = new Notification();
		notification.icon = getIcon();
		notification.tickerText = getContentText();
		notification.defaults = Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.flags = getFlags();
		PendingIntent pi = PendingIntent.getActivity(this.mContext, 0,
				getIntent(), 0);
		String title = getContentTitle();
		String contentext = getContentText();
		notification.setLatestEventInfo(this.mContext, title, contentext, pi);
		return notification;
	}

	protected String getContentText() {
		return mContentText;
	}

	protected String getContentTitle() {
		return getContext().getString(R.string.app_name);
	}

	protected int getFlags() {
		return Notification.FLAG_AUTO_CANCEL;
	}

	protected int getIcon() {
		return R.drawable.ic_launcher;
	}

	protected abstract Intent getIntent();


}
