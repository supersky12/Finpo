package com.yujunkang.fangxinbao.widget.adapter;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.control.NetworkedCacheableImageView;
import com.yujunkang.fangxinbao.model.HealthEncyclopediaClass;
import com.yujunkang.fangxinbao.model.HealthEncyclopediaInfo;
import com.yujunkang.fangxinbao.widget.adapter.HealthEncyclopediaClassListAdapter.ViewHolder;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 
 * @date 2014-7-28
 * @author xieb
 * 
 */
public class HealthEncyclopediaListAdapter extends
		BaseGroupAdapter<HealthEncyclopediaInfo> {
	private Context mContext;

	public HealthEncyclopediaListAdapter(Context context) {
		super(context);
		mContext = context;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.health_encyclopedia_list_item, null);
			holder = new ViewHolder();
			holder.iv_icon = (NetworkedCacheableImageView) convertView
					.findViewById(R.id.iv_icon);
			holder.tv_info_title = (TextView) convertView
					.findViewById(R.id.tv_info_title);
			holder.tv_info_desc = (TextView) convertView
					.findViewById(R.id.tv_info_desc);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HealthEncyclopediaInfo item = (HealthEncyclopediaInfo) getItem(position);
		String title = item.getTitle();
		if (!TextUtils.isEmpty(title)) {
			holder.tv_info_title.setText(title);

		}
		String info_desc = item.getInfoDesc();
		if (!TextUtils.isEmpty(info_desc)) {
			holder.tv_info_desc.setText(info_desc);
			holder.tv_info_desc.setVisibility(View.VISIBLE);
		} else {
			holder.tv_info_desc.setVisibility(View.GONE);
		}
		String icon_url = item.getIconUrl();
		if (!TextUtils.isEmpty(icon_url)) {
			holder.iv_icon.loadImage(icon_url, false, null);
			holder.iv_icon.setVisibility(View.VISIBLE);
		} else {
			holder.iv_icon.setVisibility(View.GONE);
		}
		return convertView;
	}

	class ViewHolder {
		NetworkedCacheableImageView iv_icon;
		TextView tv_info_title;
		TextView tv_info_desc;
	}

}
