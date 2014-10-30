package com.yujunkang.fangxinbao.activity;

import java.util.Locale;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.activity.user.UserMainActivity;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;

/**
 * 
 * @date 2014-6-19
 * @author xieb
 * 
 */
public class SettingLanguageActivity extends ActivityWrapper {
	private ListView mListView;
	private LanguagesAdapter mAdapter;

	private Locale[] languages = null;
	private Locale selectLanguage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_language_activity);
		init();
		initControl();
		ensureUi();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	private void init() {

		languages = new Locale[] { Locale.CHINESE, Locale.ENGLISH };
		selectLanguage = mApplication.getLocale();
	}

	private void initControl() {
		mListView = (ListView) findViewById(R.id.lv_language);
		mAdapter = new LanguagesAdapter();
		mListView.setAdapter(mAdapter);
	}

	private void ensureUi() {
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Locale selectedItem = languages[position];
				Locale locale = mApplication.getLocale();
				String language = locale.getLanguage();
				if (!TextUtils.isEmpty(language)
						&& (!language.contains(selectedItem.getLanguage()) || !language
								.equals(selectedItem.getLanguage()))) {

					selectLanguage = selectedItem;
					mApplication.setLocale(selectLanguage);
					Locale.setDefault(selectLanguage);
					Configuration config = getBaseContext().getResources()
							.getConfiguration();
					config.locale = selectLanguage;
					getResources()
							.updateConfiguration(
									config,
									getBaseContext().getResources()
											.getDisplayMetrics());
					DataConstants.init(getSelfContext());
					mAdapter.notifyDataSetChanged();
					sendRouteNotificationRoute(
							new String[] { UserMainActivity.class.getName() },
							CommonAction.ACTION_CHANGE_LANGUAGE, null);
					setResult(Activity.RESULT_OK);
					finish();
				}
			}
		});
	}

	class LanguagesAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return languages.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return languages[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getSelfContext()).inflate(
						R.layout.languages_list_item, null);
				holder = new ViewHolder();
				holder.tv_language = (TextView) convertView
						.findViewById(R.id.tv_language);
				holder.iv_choose = (ImageView) convertView
						.findViewById(R.id.iv_choose);
				holder.iv_language = (ImageView) convertView
						.findViewById(R.id.iv_language);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Locale item = languages[position];

			holder.tv_language.setText(item.getDisplayLanguage());

			if (selectLanguage.getLanguage().contains(item.getLanguage())
					|| selectLanguage.getLanguage().equals(item.getLanguage())) {
				holder.iv_choose.setVisibility(View.VISIBLE);
			} else {
				holder.iv_choose.setVisibility(View.INVISIBLE);
			}
			holder.iv_language
					.setImageResource(getIconResid(item.getLanguage()));
			return convertView;
		}

		private int getIconResid(String language) {
			String sourceOnName = getPackageName() + ":drawable/language_"
					+ language;
			int iconSource = getResources().getIdentifier(sourceOnName, null,
					null);
			return iconSource;
		}

		class ViewHolder {
			TextView tv_language;
			ImageView iv_choose;
			ImageView iv_language;

		}

	}

}
