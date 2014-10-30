package com.yujunkang.fangxinbao.control.image;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;


public final class RoundedTransformationBuilder {

 
  private final DisplayMetrics mDisplayMetrics;

  private float mCornerRadius = 0;
  private boolean mOval = false;
  private float mBorderWidth = 0;
  private ColorStateList mBorderColor =
      ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
  private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_CENTER;

  public RoundedTransformationBuilder() {
    mDisplayMetrics = Resources.getSystem().getDisplayMetrics();
  }

  public RoundedTransformationBuilder scaleType(ImageView.ScaleType scaleType) {
    mScaleType = scaleType;
    return this;
  }

 
  public RoundedTransformationBuilder cornerRadius(float radiusPx) {
    mCornerRadius = radiusPx;
    return this;
  }

  /**
   * 设置圆角
   */
  public RoundedTransformationBuilder cornerRadiusDp(float radiusDp) {
    mCornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radiusDp, mDisplayMetrics);
    return this;
  }

  /**
   * 设置边框
   */
  public RoundedTransformationBuilder borderWidth(float widthPx) {
    mBorderWidth = widthPx;
    return this;
  }

 
  public RoundedTransformationBuilder borderWidthDp(float widthDp) {
    mBorderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthDp, mDisplayMetrics);
    return this;
  }


  /**
   * 设置边框颜色
   */
  public RoundedTransformationBuilder borderColor(int color) {
    mBorderColor = ColorStateList.valueOf(color);
    return this;
  }

  public RoundedTransformationBuilder borderColor(ColorStateList colors) {
    mBorderColor = colors;
    return this;
  }

  public RoundedTransformationBuilder oval(boolean oval) {
    mOval = oval;
    return this;
  }

 
}
