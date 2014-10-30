package com.yujunkang.fangxinbao.utility;

import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;

/**
 * 为带图片的按钮设置触摸和聚焦监听，实现按钮效果。避免写太多的xml文件
 * 
 * @date 2014-6-2
 * @author xieb
 * 
 */
public class FocusChangedUtils {
	public final static float[] BT_SELECTED = new float[] { 1, 0, 0, 0, -50, 0,
		1, 0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0 };
public final static float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0,
		0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

public final static OnFocusChangeListener buttonOnFocusChangeListener = new OnFocusChangeListener() {

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!v.isEnabled()) {
			return;
		}
		if (hasFocus) {
			if (v.getBackground() != null) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			} else {
				setClickStyle(true, v);
			}
		} else {
			if (v.getBackground() != null) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_NOT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			} else {
				setClickStyle(false, v);
			}

		}
	}
};

public final static OnTouchListener buttonOnTouchListener = new OnTouchListener() {
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!v.isEnabled()) {
			return false;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (v.getBackground() != null) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			} else {
				setClickStyle(true, v);
			}
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

		} else {
			if (v.getBackground() != null) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_NOT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			} else {
				setClickStyle(false, v);
			}
		}
		return false;
	}
};

private static void setClickStyle(final boolean isClick, final View v) {
	Handler handler  = v.getHandler();
	v.clearAnimation();
	handler.postDelayed(new Runnable(){
		@Override
		public void run() {
			if (isClick) {
				// 按下效果
				disableListItemView(v);
			} else {
				// 弹起效果
				enableView(v);
				v.clearAnimation();
			}
		}
	}, 100);
}

private static void enableView(View view) {
	AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 1.0f);
	alphaDown.setDuration(0);
	alphaDown.setFillAfter(true);
	view.startAnimation(alphaDown);
}

private static void disableListItemView(View view) {
	AlphaAnimation alphaDown = new AlphaAnimation(1.0f, 0.4f);
	alphaDown.setDuration(0);
	alphaDown.setFillAfter(true);
	view.startAnimation(alphaDown);
}

/**
 * 设置图片控件获取焦点改变状态
 * 
 * @param view
 */
public final static void setViewFocusChanged(View inView) {
	inView.setOnTouchListener(buttonOnTouchListener);
	inView.setOnFocusChangeListener(buttonOnFocusChangeListener);
}

/**
 * 取消图片控件获取焦点改变状态
 * 
 * @param view
 */
public final static void CancelViewFocusChanged(View inView) {
	inView.setOnTouchListener(null);
	inView.setOnFocusChangeListener(null);
}

/**
 * 扩大控件可点区域
 * 
 * @param view
 */
public final static void expandTouchArea(final View view) {
	final View parent = (View) view.getParent();
	parent.post(new Runnable() {

		public void run() {
			final Rect r = new Rect();
			view.getHitRect(r);
			r.top -= 5;
			r.bottom += 5;
			r.left -= 5;
			r.right += 5;
			parent.setTouchDelegate(new TouchDelegate(r, view));
		}
	});
}

public final static void expandTouchArea(final View view, final int left,
		final int top, final int right, final int bottom) {
	final View parent = (View) view.getParent();
	parent.post(new Runnable() {

		public void run() {
			final Rect r = new Rect();
			view.getHitRect(r);
			r.top -= top;
			r.bottom += bottom;
			r.left -= left;
			r.right += right;
			parent.setTouchDelegate(new TouchDelegate(r, view));
		}
	});
}
}
