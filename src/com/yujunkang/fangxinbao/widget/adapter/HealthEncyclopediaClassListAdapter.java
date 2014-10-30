package com.yujunkang.fangxinbao.widget.adapter;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.model.HealthEncyclopediaClass;
import com.yujunkang.fangxinbao.model.Sex;
import com.yujunkang.fangxinbao.widget.adapter.SexListAdapter.ViewHolder;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @date 2014-7-28
 * @author xieb
 * 
 */
public class HealthEncyclopediaClassListAdapter extends
		BaseGroupAdapter<HealthEncyclopediaClass> {
	private Context mContext;
	private HealthEncyclopediaClass mSelectedClass;

	public HealthEncyclopediaClassListAdapter(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.encyclopedia_class_list_item, null);
			holder = new ViewHolder();
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_class_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HealthEncyclopediaClass item = (HealthEncyclopediaClass) getItem(position);
		holder.tv_title.setText(item.getTitle());
		boolean isSelected = false;
		if (mSelectedClass != null
				&& mSelectedClass.getId().equals(item.getId())) {
			isSelected = true;
		}
		if (isSelected) {
			holder.tv_title.setSelected(true);

		} else {
			holder.tv_title.setSelected(false);
		}
		return convertView;
	}

	public void setSelectedClass(HealthEncyclopediaClass classtype) {
		mSelectedClass = classtype;
	}

	class ViewHolder {
		TextView tv_title;
	}
}
