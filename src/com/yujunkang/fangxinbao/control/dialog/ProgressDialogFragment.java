package com.yujunkang.fangxinbao.control.dialog;

import com.yujunkang.fangxinbao.R;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 
 * @date 2014-6-20
 * @author xieb
 * 
 */
public class ProgressDialogFragment extends BaseDialogFragment {

	protected final static String ARG_MESSAGE = "message";
	protected final static String ARG_TITLE = "title";
	protected final static String ARG_CANCELABLE = "cancelable";
	protected int mRequestCode;
	private int mButtonBackgroundColorNormal;
	private int mButtonBackgroundColorPressed;
	private int mButtonBackgroundColorFocused;

	public static ProgressDialogBuilder createBuilder(Context context,
			FragmentManager fragmentManager) {
		return new ProgressDialogBuilder(context, fragmentManager);
	}

	@Override
	protected Builder build(Builder builder) {
		final Resources res = getResources();
		final int defaultMessageTextColor = res.getColor(
				R.color.sdl_message_text_dark);
		final TypedArray a = getActivity().getTheme().obtainStyledAttributes(
				null, R.styleable.DialogStyle, R.attr.sdlDialogStyle, 0);
		final int messageTextColor = a.getColor(
				R.styleable.DialogStyle_messageTextColor,
				defaultMessageTextColor);
		
		final int defaultButtonBackgroundColorNormal = res.getColor(R.color.sdl_button_normal_dark);
		final int defaultButtonBackgroundColorPressed = res.getColor(R.color.sdl_button_pressed_dark);
		final int defaultButtonBackgroundColorFocused = res.getColor(R.color.sdl_button_focused_dark);
		mButtonBackgroundColorNormal = a.getColor(R.styleable.DialogStyle_buttonBackgroundColorNormal, defaultButtonBackgroundColorNormal);
		mButtonBackgroundColorPressed = a.getColor(R.styleable.DialogStyle_buttonBackgroundColorPressed, defaultButtonBackgroundColorPressed);
		mButtonBackgroundColorFocused = a.getColor(R.styleable.DialogStyle_buttonBackgroundColorFocused, defaultButtonBackgroundColorFocused);
		a.recycle();

		final LayoutInflater inflater = builder.getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_part_progress, null,
				false);
		final TextView tvMessage = (TextView) view
				.findViewById(R.id.sdl__message);
		tvMessage.setText(getArguments().getString(ARG_MESSAGE));
		tvMessage.setTextColor(messageTextColor);
		boolean showCancelButton = getArguments().getBoolean(ARG_CANCELABLE,
				false);
		View sdl_cancelPanel= view.findViewById(R.id.sdl_cancelPanel);
		if (showCancelButton) {
			sdl_cancelPanel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ISimpleDialogCancelListener listener = getCancelListener();
					if (listener != null) {
						listener.onCancelled(mRequestCode);
					}

				}
			});
			sdl_cancelPanel.setVisibility(View.VISIBLE);
			sdl_cancelPanel.setBackgroundDrawable(getButtonBackground());
		} else {
			sdl_cancelPanel.setVisibility(View.GONE);
		}
		builder.setView(view);
		builder.setTitle(getArguments().getString(ARG_TITLE));
		return builder;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() == null) {
			throw new IllegalArgumentException(
					"use ProgressDialogBuilder to construct this dialog");
		}
		final Fragment targetFragment = getTargetFragment();
		mRequestCode = targetFragment != null ? getTargetRequestCode()
				: getArguments().getInt(BaseDialogBuilder.ARG_REQUEST_CODE, 0);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		ISimpleDialogCancelListener listener = getCancelListener();
		if (listener != null) {
			listener.onCancelled(mRequestCode);
		}
	}

	protected ISimpleDialogCancelListener getCancelListener() {
		final Fragment targetFragment = getTargetFragment();
		if (targetFragment != null) {
			if (targetFragment instanceof ISimpleDialogCancelListener) {
				return (ISimpleDialogCancelListener) targetFragment;
			}
		} else {
			if (getActivity() instanceof ISimpleDialogCancelListener) {
				return (ISimpleDialogCancelListener) getActivity();
			}
		}
		return null;
	}

	private StateListDrawable getButtonBackground() {
		int[] pressedState = {android.R.attr.state_pressed};
		int[] focusedState = {android.R.attr.state_focused};
		int[] defaultState = {android.R.attr.state_enabled};
		ColorDrawable colorDefault = new ColorDrawable(mButtonBackgroundColorNormal);
		ColorDrawable colorPressed = new ColorDrawable(mButtonBackgroundColorPressed);
		ColorDrawable colorFocused = new ColorDrawable(mButtonBackgroundColorFocused);
		StateListDrawable background = new StateListDrawable();
		background.addState(pressedState, colorPressed);
		background.addState(focusedState, colorFocused);
		background.addState(defaultState, colorDefault);
		return background;
	}
	
	
	public static class ProgressDialogBuilder extends
			BaseDialogBuilder<ProgressDialogBuilder> {

		private String mTitle;
		private String mMessage;
		private boolean mShowCancelButton;

		protected ProgressDialogBuilder(Context context,
				FragmentManager fragmentManager) {
			super(context, fragmentManager, ProgressDialogFragment.class);
		}

		@Override
		protected ProgressDialogBuilder self() {
			return this;
		}

		public ProgressDialogBuilder setTitle(int titleResourceId) {
			mTitle = mContext.getString(titleResourceId);
			return this;
		}

		public ProgressDialogBuilder setTitle(String title) {
			mTitle = title;
			return this;
		}

		public ProgressDialogBuilder setMessage(int messageResourceId) {
			mMessage = mContext.getString(messageResourceId);
			return this;
		}

		public ProgressDialogBuilder setMessage(String message) {
			mMessage = message;
			return this;
		}

		public ProgressDialogBuilder setShowCancelButton(
				boolean showCancelButton) {
			mShowCancelButton = showCancelButton;
			return this;
		}

		@Override
		protected Bundle prepareArguments() {
			Bundle args = new Bundle();
			args.putString(SimpleDialogFragment.ARG_MESSAGE, mMessage);
			args.putString(SimpleDialogFragment.ARG_TITLE, mTitle);
			args.putBoolean(ARG_CANCELABLE, mShowCancelButton);
			return args;
		}
	}
}
