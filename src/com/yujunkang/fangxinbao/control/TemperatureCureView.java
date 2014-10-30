package com.yujunkang.fangxinbao.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.compare.TemperatureComparator;
import com.yujunkang.fangxinbao.compare.TemperatureTimeComparator;
import com.yujunkang.fangxinbao.method.Method;
import com.yujunkang.fangxinbao.model.TemperatureCommonData;

import com.yujunkang.fangxinbao.model.TemperatureData;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.DataConstants.TemperatureLevel;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.TypeUtils;
import com.yujunkang.fangxinbao.utility.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout.LayoutParams;

public class TemperatureCureView extends View {
	private final static String TAG = "TemperatureCureView";
	private float minTemperature = 0;

	private final static int PADDING_LEFT = 30;
	private final static int PADDING_RIGHT = 20;
	private final static int PADDING_TOP = 20;

	private final static int PADDING_SMALL = 5;
	private final static int PADDING_MIDDLE = 10;
	private final static int PADDING_LARGE = 15;

	private static int PADDING_LEFT_PX = 0;
	private static int PADDING_RIGHT_PX = 0;
	private static int PADDING_TOP_PX = 0;
	private static int PADDING_SMALL_PX = 0;
	private static int PADDING_MIDDLE_PX = 0;
	private static int PADDING_LARGE_PX = 0;

	private static ArrayList<TemperatureData> constantsTemperature = new ArrayList<TemperatureData>();

	private int _dataCount = 0;

	private int _circle;
	// 图标的圆
	private Bitmap _circleBitmap;
	// 图标的圆
	private Bitmap _circleNormalBitmap;
	// 图标的圆
	private Bitmap _circleHighBitmap;

	private int _totalWidth;

	private Context mContext;
	// 线
	private Paint _linePaint = new Paint();

	// 图表上的温度
	private Paint _temperaturePaint = new Paint();
	private float _temperaturePaintHeight;
	// 时间段
	private Paint _timePaint = new Paint();
	private float _timePaintHeight;

	private BitmapFactory.Options _iconOption = new BitmapFactory.Options();

	// Y坐标的温度
	private Paint _temperatureYPaint = new Paint();
	private float _temperatureYPaintHeight;
	private float _temperatureYTextStartPointY;
	private Paint _horizontaLlinePaint = new Paint();
	private Paint _bitmapPaint = new Paint();
	
	// 数据字体大小 如 时间

	private float _lineWidth;
	private int _lineColor;

	// 文字的行间距

	private float _paddingBottomTemperature;

	private float _paddingTopTemperature;

	private float _paddingTopTime;
	private float _paddingBottomTime;

	private float _totalHeight;
	private float _proportion;
	/**
	 * 每个点的x轴间距
	 * 
	 */
	private float _viewportSize;

	/**
	 * 每个刻度的垂直距离
	 */
	private float _VPerSpacing;

	private float _startPointX;
	private float _startPointY;
	private float _startYCurePointX;
	private float _pathStartX;
	private float _pathStartY;
	private float _offsetY;
	private float _rectWidth;
	private float _rectHeight;

	private ArrayList<TemperatureData> mDataList = new ArrayList<TemperatureData>();
	private GestureDetector gestureScanner;
	private float _chartHeight;
	int circleBitmapOffsetY;
	int circleBitmapOffsetX;
	// 坐标
	private float _timeTextPointY;

	private float _temperatureTextStartPointY;

	private float _minTemperatureFloat;

	private float _maxTemperatureFloat;

	private int _gradientColorOnEnd;
	private int _gradientColorOnMiddle;
	private int _gradientColorOnStart;

	private float lastTouchEventX;

	private boolean scrollingStarted;
	private float _viewportStart = 0;
	private Paint _pathPaint;

	private Paint _circleLowPaint;
	private Rect _clientRect = null;
	private float _timeTextWidth;
	private List<TemperatureCommonData> temperatureLevels;
	boolean isClick = false;
	private int mTouchSlopSquare;

	private MotionEvent mCurrentDownEvent;
	private HashMap<Integer, Rect> circleRect = new HashMap<Integer, Rect>();
	private float[] _offsetPointY = null;
	private float lastMotionX;
	private float lastMotionY;

	public TemperatureCureView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TemperatureCureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		// TODO Auto-generated constructor stub
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TemperatureView);
		try {
			_circle = a.getResourceId(
					R.styleable.TemperatureView_connectionImage,
					R.drawable.icon_temperature_low);

			_lineWidth = a.getFloat(
					R.styleable.TemperatureView_lineStrokeWidth, 2);
			_lineColor = a.getColor(R.styleable.TemperatureView_lineColor,
					Color.parseColor("#2ea7e0"));
			int timePaintTextSize = Utils.dip2px(mContext,
					a.getFloat(R.styleable.TemperatureView_timeTextSize, 14f));
			int timeTextColor = a.getColor(
					R.styleable.TemperatureView_timeTextColor, Color.BLACK);
			generateTextPaint(_timePaint, timePaintTextSize, timeTextColor);
			int temperaturePaintTextSize = Utils.dip2px(mContext, a.getFloat(
					R.styleable.TemperatureView_temperatureTextSize, 16f));
			int temperatureTextColor = a.getColor(
					R.styleable.TemperatureView_temperatureTextColor,
					Color.BLACK);
			generateTextPaint(_temperaturePaint, temperaturePaintTextSize,
					temperatureTextColor);
			int temperatureYPaintTextSize = Utils.dip2px(mContext, a.getFloat(
					R.styleable.TemperatureView_temperatureYTextSize, 16f));
			int temperatureYTextColor = a.getColor(
					R.styleable.TemperatureView_temperatureYTextColor,
					Color.BLACK);
			generateTextPaint(_temperatureYPaint, temperatureYPaintTextSize,
					temperatureYTextColor);
			float horizontaLlineWidth = a.getFloat(
					R.styleable.TemperatureView_horizontalLineStrokeWidth, 2);
			_horizontaLlinePaint.setColor(temperatureYTextColor);
			_horizontaLlinePaint.setAntiAlias(true);
			_horizontaLlinePaint.setStrokeWidth(horizontaLlineWidth);
			_proportion = a.getFloat(R.styleable.TemperatureView_proportion,
					1.0f);
			_gradientColorOnStart = a.getColor(
					R.styleable.TemperatureView_gradientColorOnStart,
					Color.GREEN);
			_gradientColorOnMiddle = a.getColor(
					R.styleable.TemperatureView_gradientColorOnMiddle,
					Color.GREEN);
			_gradientColorOnEnd = a
					.getColor(R.styleable.TemperatureView_gradientColorOnEnd,
							Color.WHITE);
			_totalHeight = a.getFloat(R.styleable.TemperatureView_totalHeight,
					480);
			_totalHeight = Utils.dip2px(mContext, _totalHeight);
			_viewportSize = a.getFloat(R.styleable.TemperatureView_hSpacing,
					120);
			_viewportSize = Utils.dip2px(mContext, _viewportSize);
		} finally {
			a.recycle();
		}
		init();
	}

	private void generateTextPaint(Paint paint, int textSize, int textColor) {
		paint.setTextSize(textSize);
		paint.setColor(textColor);
		paint.setAntiAlias(true);
	}

	private void init() {
		constantsTemperature.clear();

		final ViewConfiguration configuration = ViewConfiguration
				.get(getContext());

		int touchSlop = configuration.getScaledTouchSlop();
		mTouchSlopSquare = touchSlop;
		minTemperature = Preferences.getMinTemperature(getContext());
		// 初始化固定的温度
		TemperatureData temp = new TemperatureData();
		temp.setTemperature("38.5");

		constantsTemperature.add(temp);
		temp = new TemperatureData();
		temp.setTemperature("35.3");
		constantsTemperature.add(temp);
		temp = new TemperatureData();
		temp.setTemperature("37.5");
		constantsTemperature.add(temp);
		// 初始化间距参数
		PADDING_LEFT_PX = Utils.dip2px(mContext, PADDING_LEFT);
		PADDING_RIGHT_PX = Utils.dip2px(mContext, PADDING_RIGHT);

		PADDING_TOP_PX = Utils.dip2px(mContext, PADDING_TOP);
		PADDING_SMALL_PX = Utils.dip2px(mContext, PADDING_SMALL);
		PADDING_MIDDLE_PX = Utils.dip2px(mContext, PADDING_MIDDLE);
		PADDING_LARGE_PX = Utils.dip2px(mContext, PADDING_LARGE);

		
//		PADDING_LEFT_PX = PADDING_LEFT;
//		PADDING_RIGHT_PX = PADDING_RIGHT;
//
//		PADDING_TOP_PX =  PADDING_TOP;
//		PADDING_SMALL_PX =  PADDING_SMALL;
//		PADDING_MIDDLE_PX = PADDING_MIDDLE;
//		PADDING_LARGE_PX = PADDING_LARGE;
		
		_paddingBottomTemperature = PADDING_MIDDLE_PX;
		_paddingTopTemperature = PADDING_MIDDLE_PX;
		_paddingTopTime = PADDING_SMALL_PX * 6;
		_paddingBottomTime = PADDING_SMALL_PX * 6;
		_offsetY = PADDING_SMALL_PX;
		_bitmapPaint.setAntiAlias(true);
		_bitmapPaint.setStyle(Style.FILL);
		if (_circle != -1) {
			_circleBitmap = BitmapFactory.decodeResource(getResources(),
					_circle);
			_circleNormalBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.icon_temperature_normal);
			_circleHighBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.icon_temperature_high);
			int height = _circleBitmap.getHeight();
			int width = _circleBitmap.getWidth();
			circleBitmapOffsetY = height / 2;
			circleBitmapOffsetX = width / 2;
		}

		_circleLowPaint = new Paint();
		_circleLowPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		_circleLowPaint.setColor(Color.parseColor("#70d6d3"));
		_circleLowPaint.setStyle(Paint.Style.STROKE);
		_circleLowPaint.setStrokeWidth(Utils.dip2px(getContext(), 1));

		// 线
		_linePaint.setColor(_lineColor);
		_linePaint.setAntiAlias(true);
		_linePaint.setStrokeWidth(_lineWidth);
		// 温度文字的高度
		_temperaturePaintHeight = Utils
				.computePaintTextHeight(_temperaturePaint);

		// 时间

		_timePaintHeight = Utils.computePaintTextHeight(_timePaint);
		_temperatureYPaintHeight = Utils
				.computePaintTextHeight(_temperatureYPaint);

		// 时间为固定格式HH:mm
		_timeTextWidth = _timePaint.measureText("HH:mm");
		calculateTimeTextPointY();
		
		caculateStartPointX();
		_startPointY = _totalHeight - _timePaintHeight - _paddingBottomTime
				- _paddingTopTime;
		_temperatureTextStartPointY = _startPointY - _paddingBottomTemperature
				- _temperaturePaintHeight
				- _temperaturePaint.getFontMetrics().top;
		_temperatureYTextStartPointY = _startPointY - _temperatureYPaintHeight
				/ 2 - _temperatureYPaint.getFontMetrics().top;
		_chartHeight = _totalHeight - PADDING_TOP_PX - _temperaturePaintHeight
				- _paddingBottomTemperature - _paddingTopTime
				- _timePaintHeight - _paddingBottomTime;
		_pathStartY = _timeTextPointY - _timePaintHeight / 2f
				- _timePaint.getFontMetrics().top;
		_iconOption.inSampleSize = 3;

		_rectHeight = _totalHeight;

		int colors[] = new int[3];
		float positions[] = new float[3];

		// 第1个点
		colors[0] = _gradientColorOnStart;
		positions[0] = 0;

		// 第2个点
		colors[1] = _gradientColorOnMiddle;
		positions[1] = 0.5f;

		// 第3个点
		colors[2] = _gradientColorOnEnd;
		positions[2] = 1;
		LinearGradient _lgPath = new LinearGradient(0, -_rectHeight - _offsetY,
				0, 0, colors, positions, TileMode.MIRROR);
		_pathPaint = new Paint();
		_pathPaint.setShader(_lgPath);
	}

	public void setProportion(float value) {
		init();
		invalidate();
	}

	public void caculateStartPointX()
	{
		//切换摄氏度和华氏度,温度文字的长度有可能不一样，取最长的文字为起始点
		Collections.sort(constantsTemperature, new TemperatureComparator());
		try {
			String maxTemperature = constantsTemperature.get(constantsTemperature.size() - 1)
					.getTemperature();
			maxTemperature = TypeUtils.getTemperatureScaleValue(1, maxTemperature,
					getContext());
			float temperatureYTextWidth = _temperatureYPaint.measureText(maxTemperature);
			_startPointX = PADDING_LEFT_PX + temperatureYTextWidth;
			_pathStartX = _startPointX;
		}
		catch(Exception ex)
		{
			
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		LoggerTool.d(TAG, "onMeasure");
		final int _totalWidth = MeasureSpec.getSize(widthMeasureSpec);
		LayoutParams params = (LayoutParams) getLayoutParams();
		if (params.height != LayoutParams.WRAP_CONTENT) {
			_totalHeight = params.height;
		}
		setMeasuredDimension(_totalWidth, (int) _totalHeight);

	}

	private void calculateTimeTextPointY() {
		_timeTextPointY = _totalHeight - _timePaintHeight - _paddingBottomTime
				- _timePaint.getFontMetrics().top;

	}

	private float calculateTextPointX(int index, float textWidth) {
		return this._startPointX + _viewportSize * index - textWidth / 2f;
	}

	@Override
	public void setLayoutParams(android.view.ViewGroup.LayoutParams params) {
		// TODO Auto-generated method stub
		super.setLayoutParams(params);
		if (params.height != LayoutParams.WRAP_CONTENT) {
			_totalHeight = params.height;
			init();
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		LoggerTool.d(TAG, "onDraw");
		drawRegionPath(canvas);
		for (TemperatureData itemY : constantsTemperature) {
			float YTextPointY = getLineYTextPointY(itemY);
			String temperatureY = itemY.getTemperature();
			temperatureY = TypeUtils.getTemperatureScaleValue(1, temperatureY,
					getContext());
			float temperatureYTextWidth = _temperatureYPaint.measureText(temperatureY);
			_startYCurePointX = PADDING_LEFT_PX - temperatureYTextWidth / 2f;
			canvas.drawText(temperatureY, _startYCurePointX, YTextPointY,
					_temperatureYPaint);
			canvas.drawLine(_startYCurePointX + temperatureYTextWidth,
					getLinePointY(itemY), 10000, getLinePointY(itemY),
					_horizontaLlinePaint);
		}
		if (_dataCount > 0) {
			drawTemperature(canvas);
		}
	}

	// 画体温曲线
	private void drawRegionPath(Canvas canvas) {
		if (_dataCount > 0) {
			canvas.save();
			if (_clientRect == null) {
				_clientRect = new Rect((int) (0 - _viewportStart),
						-(int) (_rectHeight + _offsetY),
						(int) (_rectWidth - _viewportStart), 0);
			} else {
				_clientRect.set((int) (0 - _viewportStart),
						-(int) (_rectHeight + _offsetY),
						(int) (_rectWidth - _viewportStart), 0);
			}
			// 这里用相对坐标简单点,把坐标y,时间的字baselineAligned=bottom
			canvas.translate(_pathStartX, _pathStartY);
			Path path = new Path(); // Path对象
			// 起始点
			path.moveTo(0 - _viewportStart, 0);
			for (int index = 0; index < _dataCount; index++) {
				// TemperatureData item = mDataList.get(index);
				path.lineTo(_viewportSize * index - _viewportStart,
						-_offsetPointY[index] - _paddingTopTime
								- _timePaintHeight - 1);
			}
			path.lineTo(_viewportSize * (_dataCount - 1) - _viewportStart, 0);
			path.lineTo(0 - _viewportStart, 0);
			path.close();
			canvas.clipPath(path, Region.Op.INTERSECT);
			canvas.drawRect(_clientRect, _pathPaint);
			canvas.restore();
		}
	}

	// 画体温曲线
	private void drawTemperature(Canvas canvas) {
		circleRect.clear();
		float startX = 0;
		float startY = 0;
		for (int index = 0; index < _dataCount; index++) {
			startX = _startPointX + _viewportSize * index - _viewportStart;

			TemperatureData item = mDataList.get(index);
			if (item.getTemperatureLevel() == null) {
				item.setTemperatureLevel(getTemperatureLevel(item));
			}
			startY = _startPointY - _offsetPointY[index];
			if (index < (_dataCount - 1)) {
				// 画线
				float endLineX = startX + _viewportSize;
				canvas.drawLine(startX, startY, endLineX, _startPointY
						- _offsetPointY[index + 1], _linePaint);
			}
			// 画圆
			drawCircle(canvas, item.getTemperatureLevel(), startX, startY,
					index);
			// 画温度
			if (isShowTemperature(item, item.getTemperatureLevel())) {
				String temperature = item.getTemperature();
				temperature = TypeUtils.getTemperatureScaleValue(1,
						temperature, getContext());
				float temperatureTextWidth = _temperaturePaint
						.measureText(temperature);
				float temperatureY = _temperatureTextStartPointY
						- _offsetPointY[index];
				canvas.drawText(temperature,
						startX - temperatureTextWidth / 2f, temperatureY,
						_temperaturePaint);
			}
			// 画时间文字
			String timeString = item.getTime();
			LoggerTool.d(TAG, timeString);
			if (!TextUtils.isEmpty(timeString)) {
				canvas.drawText(timeString,
						calculateTextPointX(index, _timeTextWidth)
								- _viewportStart, _timeTextPointY, _timePaint);
			}
		}
	}

	/**
	 * @param event
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean handled = false;
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			if (mCurrentDownEvent != null) {
				mCurrentDownEvent.recycle();
			}
			mCurrentDownEvent = MotionEvent.obtain(event);
			isClick = true;
			lastMotionY = y;
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			final int deltaX = (int) (x - mCurrentDownEvent.getX());
			final int deltaY = (int) (y - mCurrentDownEvent.getY());
			int distance = (deltaX * deltaX) + (deltaY * deltaY);
			if (distance > mTouchSlopSquare * 6) {
				isClick = false;
			}

			break;
		}
		case MotionEvent.ACTION_UP: {
			if (isClick) {
				LoggerTool.d(TAG, " event.getX():" + event.getX()
						+ " event.getY()" + event.getY());
				for (Map.Entry<Integer, Rect> entry : circleRect.entrySet()) {
					int key = entry.getKey();
					Rect value = entry.getValue();
					if (value.contains((int) event.getX(), (int) event.getY())) {
						TemperatureData clickTemperature = mDataList.get(key);
						clickTemperature.setShow(!clickTemperature.isShow());
						invalidate();
					}
				}
			}
			break;
		}
		}
		if (!handled) {
			if ((event.getAction() & MotionEvent.ACTION_DOWN) == MotionEvent.ACTION_DOWN
					&& (event.getAction() & MotionEvent.ACTION_MOVE) == 0) {
				scrollingStarted = true;
				handled = true;
				getParent().requestDisallowInterceptTouchEvent(true);
			}
			if ((event.getAction() & MotionEvent.ACTION_UP) == MotionEvent.ACTION_UP) {
				scrollingStarted = false;
				lastTouchEventX = 0;
				handled = true;
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			if ((event.getAction() & MotionEvent.ACTION_MOVE) == MotionEvent.ACTION_MOVE) {
				// float disY = Math.abs(y - lastMotionY);
				// if (disY > Math.abs(x - lastTouchEventX)
				// && disY > mTouchSlopSquare) {
				// getParent().requestDisallowInterceptTouchEvent(false);
				// } else {
				// getParent().requestDisallowInterceptTouchEvent(true);
				// }
				if (scrollingStarted) {
					if (lastTouchEventX != 0 && !isClick) {
						onMoveGesture(event.getX() - lastTouchEventX);
					}
					lastTouchEventX = event.getX();
					handled = true;
				}
			}
			if (handled && !isClick) {
				invalidate();
			}
		} else {
			// currently scaling
			scrollingStarted = false;
			lastTouchEventX = 0;
		}
		return handled;
	}

	private void onMoveGesture(float f) {
		if (_viewportSize != 0
				&& _rectWidth >= DataConstants.DEVICE_WIDTH - _startPointX) {
			_viewportStart = _viewportStart - f;
			if (_viewportStart < 0) {
				_viewportStart = 0;
			} else if (_viewportStart + _viewportSize * 2 > _rectWidth) {
				_viewportStart = _rectWidth - _viewportSize * 2;
			}
			LoggerTool.d(TAG, "viewportStart :" + _viewportStart);
		}
		invalidate();
	}

	/**
	 * 画圆
	 * 
	 * @param data
	 * @return
	 */
	private void drawCircle(Canvas canvas, TemperatureLevel data, float x,
			float y, int index) {
		try {

			Bitmap circleBitmap = getBitmapResIdByTemperatureLevel(data);
			// 这里扩大点击区域
			float left = x - circleBitmapOffsetX - mTouchSlopSquare * 3;
			float top = y - circleBitmapOffsetY - mTouchSlopSquare * 6;
			float right = left + circleBitmapOffsetX * 2 + mTouchSlopSquare *3;
			float bottom = top + circleBitmapOffsetY * 2 + mTouchSlopSquare * 6;
			if (x >= 0 && x <= DataConstants.DEVICE_WIDTH) {
				Rect rect = new Rect((int) left, (int) top, (int) right,
						(int) bottom);
				circleRect.put(index, rect);
			}
			canvas.drawBitmap(circleBitmap, x - circleBitmapOffsetX, y
					- circleBitmapOffsetY, _bitmapPaint);

			LoggerTool.d(TAG,
					"drawCircle :" + String.valueOf(x - circleBitmapOffsetX));

		} catch (Exception ex) {

		}
	}

	private boolean isShowTemperature(TemperatureData data,
			TemperatureLevel level) {
		if (level == TemperatureLevel.FEVER
				|| level == TemperatureLevel.HIGH_FEVER) {
			return true;
		}
		// 这里暂时全部返回true
		if (data.isShow()) {
			return true;
		}

		return false;
	}

	/**
	 * 根据不同温度展示不同的圆
	 * 
	 * @param data
	 * @return
	 */
	private Bitmap getBitmapResIdByTemperatureLevel(TemperatureLevel level) {
		if (level != null) {
			switch (level) {
			case LOW: {
				return _circleBitmap;

			}
			case NORMAL: {
				return _circleNormalBitmap;

			}
			case FEVER: {
				return _circleHighBitmap;

			}
			case HIGH_FEVER: {
				return _circleHighBitmap;
			}
			}
		}
		return _circleBitmap;
	}

	private TemperatureLevel getTemperatureLevel(TemperatureData data) {
		float temperature = getTemperature(data.getTemperature());
		TemperatureCommonData temperatureStatus = Method.getTemperatureLevelStatus(
				temperature, getContext());
		if (temperatureStatus != null) {
			return temperatureStatus.getTemperatureLevel();
		}
		return null;
	}

	/**
	 * 线上点的坐标=起始坐标+(温度-最小温度)*每1度温度的间距
	 * 
	 * @param data
	 * @return
	 */
	private float getLinePointY(TemperatureData data) {
		return _startPointY - getOffsetY(data);
	}

	private float getOffsetY(TemperatureData data) {
		return (getTemperature(data.getTemperature()) - _minTemperatureFloat)
				* _VPerSpacing;
	}

	/**
	 * 线上点的坐标=起始坐标+(温度-最小温度)*每1度温度的间距
	 * 
	 * @param data
	 * @return
	 */
	private float getTemperatureTextPointY(TemperatureData data) {
		return _temperatureTextStartPointY
				- (getTemperature(data.getTemperature()) - _minTemperatureFloat)
				* _VPerSpacing;
	}

	/**
	 * 线上点的坐标=起始坐标+(温度-最小温度)*每1度温度的间距
	 * 
	 * @param data
	 * @return
	 */
	public float getLineYTextPointY(TemperatureData data) {
		return this._temperatureYTextStartPointY
				- (getTemperature(data.getTemperature()) - _minTemperatureFloat)
				* _VPerSpacing;
	}

	public void initData(List<TemperatureData> datas) {
		mDataList.clear();
		if (datas != null) {
			mDataList.addAll(datas);
		}
		_viewportStart = 0;
		_dataCount = mDataList.size();
		LoggerTool.d(TAG, "initData : " + _dataCount);
		resolveData();
		invalidate();

	}

	public void showLastTemperatureData(List<TemperatureData> datas) {
		mDataList.clear();
		if (datas != null) {
			mDataList.addAll(datas);
		}
		_dataCount = mDataList.size();
		LoggerTool.d(TAG, "initData : " + _dataCount);
		resolveData();
		_viewportStart = 0;
		if (_dataCount > 0) {
			float lastPointX = _startPointX + _viewportSize * (_dataCount - 1);
			LoggerTool.d(TAG, "lastPointX : " + lastPointX);
			if (lastPointX >= DataConstants.DEVICE_WIDTH) {
				_viewportStart = _rectWidth - _viewportSize * 2;
			}
		}
		invalidate();
	}

	private float getTemperature(String Temperature) {
		try

		{
			return Float.parseFloat(Temperature);
		} catch (Exception ex) {
			return _minTemperatureFloat;
		}
	}

	/**
	 * 得到最高温度和最低温度，好计算每个刻度的间距
	 */
	private void resolveData() {
		ArrayList<TemperatureData> tempData = new ArrayList<TemperatureData>();
		tempData.addAll(mDataList);
		tempData.addAll(constantsTemperature);

		Collections.sort(tempData, new TemperatureComparator());
		try {
			String maxTemperature = tempData.get(tempData.size() - 1)
					.getTemperature();
			String minTemperature = tempData.get(0).getTemperature();
			_maxTemperatureFloat = Float.parseFloat(maxTemperature.substring(0,
					maxTemperature.length()));

			_minTemperatureFloat = Float.parseFloat(minTemperature.substring(0,
					minTemperature.length()));
			if (_maxTemperatureFloat == _minTemperatureFloat) {
				_VPerSpacing = _chartHeight - 100;
			} else {
				_VPerSpacing = _chartHeight
						/ (_maxTemperatureFloat - _minTemperatureFloat);
			}
			_rectWidth = _viewportSize * (_dataCount - 1);
			_offsetPointY = new float[_dataCount];
			Collections.sort(mDataList, new TemperatureTimeComparator());
			for (int index = 0; index < _dataCount; index++) {
				_offsetPointY[index] = getOffsetY(mDataList.get(index));
			}
		} catch (Exception ex) {
			Log.e("TemperatureView", ex.getMessage());
		}

	}

	class Point {
		float offsetX;
		float offsetY;
	}

}
