

package com.yujunkang.fangxinbao.control.calendar;

import java.util.Date;

/**  {@link MonthView}. */
class MonthCellDescriptor {
	public enum RangeState {
		NONE, FIRST, MIDDLE, LAST
	}

	private final Date date;
	private final int value;
	private final boolean isCurrentMonth;
	private boolean isSelected;
	private final boolean isToday;
	private final boolean isSelectable;
	private boolean isHighlighted;
	private RangeState rangeState; 
	private float temperature;
	MonthCellDescriptor(Date date, boolean currentMonth, boolean selectable,
			boolean selected, boolean today, boolean highlighted, int value,
			RangeState rangeState,float temperature) {
		this.date = date;
		isCurrentMonth = currentMonth;
		isSelectable = selectable;
		isHighlighted = highlighted;
		isSelected = selected;
		isToday = today;
		this.value = value;
		this.rangeState = rangeState;
		this.temperature = temperature;
	}

	public Date getDate() {
		return date;
	}

	public boolean isCurrentMonth() {
		return isCurrentMonth;
	}

	public boolean isSelectable() {
		return isSelectable;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	boolean isHighlighted() {
		return isHighlighted;
	}

	void setHighlighted(boolean highlighted) {
		isHighlighted = highlighted;
	}

	public boolean isToday() {
		return isToday;
	}

	public RangeState getRangeState() {
		return rangeState;
	}

	public void setRangeState(RangeState rangeState) {
		this.rangeState = rangeState;
	}

	public int getValue() {
		return value;
	}

	
	
	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	@Override
	public String toString() {
		return "MonthCellDescriptor{" + "date=" + date + ", value=" + value
				+ ", isCurrentMonth=" + isCurrentMonth + ", isSelected="
				+ isSelected + ", isToday=" + isToday + ", isSelectable="
				+ isSelectable + ", isHighlighted=" + isHighlighted
				+ ", rangeState=" + rangeState + '}';
	}
}
