package com.yujunkang.fangxinbao.widget.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.utility.LoggerTool;

/**
 * 
 * @date 2014-9-1
 * @author xieb
 * 
 */
public class SearchCountryAdapter extends BaseGroupAdapter<Country> {
	private Context mContext;

	public SearchCountryAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	class ViewHolder {
		TextView tv_countryName;
		TextView tv_countryEngName;
		TextView tv_countryCode;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.select_country_list_item, null);
			holder = new ViewHolder();
			holder.tv_countryName = (TextView) convertView
					.findViewById(R.id.tv_country_name);
			holder.tv_countryEngName = (TextView) convertView
					.findViewById(R.id.tv_country_engname);
			holder.tv_countryCode = (TextView) convertView
					.findViewById(R.id.tv_country_code);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		LoggerTool.d("CountryListAdapter", "getChildView");
		Country item = (Country) getItem(position);
		String name = item.getName();
		if (!TextUtils.isEmpty(name)) {
			holder.tv_countryName.setVisibility(View.VISIBLE);
			holder.tv_countryName.setText(name);
		} else {
			holder.tv_countryName.setVisibility(View.GONE);
		}
		String engname = item.getEngName();
		if (!TextUtils.isEmpty(engname)) {
			holder.tv_countryEngName.setVisibility(View.VISIBLE);
			holder.tv_countryEngName.setText(engname);
		} else {
			holder.tv_countryEngName.setVisibility(View.GONE);
		}

		String countryCode = item.getCountryCode();
		if (!TextUtils.isEmpty(countryCode)) {
			holder.tv_countryCode.setVisibility(View.VISIBLE);
			holder.tv_countryCode.setText("+" + countryCode);
		} else {
			holder.tv_countryCode.setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public boolean isEnabled(int position) {
		Country item = (Country) getItem(position);
		String name = item.getName();
		if(!TextUtils.isEmpty(name)&&!name.equals(mContext.getString(R.string.search_country_no_result)))
		{
			return true;
		}
		return false;
	}
	
	
	
}
