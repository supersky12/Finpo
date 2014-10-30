package com.yujunkang.fangxinbao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;


import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.control.IndexScrollBar;
import com.yujunkang.fangxinbao.control.IndexScrollBar.ScrollListener;
import com.yujunkang.fangxinbao.control.PinnedHeaderExpandableListView;
import com.yujunkang.fangxinbao.control.PinnedHeaderExpandableListView.OnHeaderUpdateListener;
import com.yujunkang.fangxinbao.control.SearchBarView;
import com.yujunkang.fangxinbao.control.SearchBarView.OnEditTextOnFocusChangeListener;
import com.yujunkang.fangxinbao.database.DBHelper;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.widget.adapter.CountryListAdapter;
import com.yujunkang.fangxinbao.widget.adapter.SearchCountryAdapter;

/**
 * 
 * @date 2014-5-28
 * @author xieb
 * 
 */
public class SelectCountryActivity extends ActivityWrapper implements
		ExpandableListView.OnChildClickListener,
		ExpandableListView.OnGroupClickListener, OnHeaderUpdateListener {

	private static final String TAG = "SelectCountryActivity";
	public static final String INTENT_EXTRA_COUNTRY = DataConstants.PACKAGE_NAME
			+ ".SelectCountryActivity.INTENT_EXTRA_COUNTRY";
	/**
	 * 控件
	 */
	private IndexScrollBar iv_group_index;
	private TextView tv_title;
	private TextView tv_index_in_center;
	private PinnedHeaderExpandableListView expandableListView;
	private CountryListAdapter mCountryListAdapter;
	private ViewGroup mHeaderView;
	private SearchBarView lay_select_country_bar;
	private View activityRootView;
	private View list_container;
	private View search_result_list_container;
	private SearchCountryAdapter mSearchCountryAdapter;
	private ListView search_result_list;
	
	/**
	 * 
	 */
	private DBHelper mDatabaseHelper = null;
	private Group<Group<Country>> countryDataSource = new Group<Group<Country>>();
	private Group<Country> mSearchResultList = new Group<Country>();
	
	// private Group<Group<Country>> searchResultCities = new
	// Group<Group<Country>>();
	private Handler mHandler = new Handler();
	private Runnable mRemoveWindow = new Runnable() {
		public void run() {
			tv_index_in_center.setVisibility(View.INVISIBLE);
		}
	};

	ScrollListener indexScroll = new ScrollListener() {
		@Override
		public void onScroll(String sectionStr) {
			if (mCountryListAdapter != null) {
				int groupCount = countryDataSource.size();
				for (int position = 0; position < groupCount; position++)
					if (sectionStr.equals(countryDataSource.get(position)
							.getType())) {
						expandableListView.setSelectedGroup(position);
						tv_index_in_center.setText(sectionStr);
						tv_index_in_center.setVisibility(View.VISIBLE);
						mHandler.removeCallbacks(mRemoveWindow);
						mHandler.postDelayed(mRemoveWindow, 2000);
						return;
					}
			}
		}

	};

	TextWatcher inputWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			searchCountry(s.toString());

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_country_activity);
		init();
		ensureUi();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	private void init() {
		mDatabaseHelper = DBHelper.getDBInstance(this);
		initCitiesDataSource();
	}

	private void ensureUi() {
		search_result_list_container = findViewById(R.id.search_result_list_container);
		search_result_list =(ListView)findViewById(R.id.search_result_list);
		list_container = findViewById(R.id.list_container);
		activityRootView = findViewById(R.id.root_layout);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					private int preHeight = 0;

					@Override
					public void onGlobalLayout() {
						int heightDiff = activityRootView.getRootView()
								.getHeight() - activityRootView.getHeight();
						LoggerTool.d(TAG, "height differ = " + heightDiff);
						// 在数据相同时，减少发送重复消息。因为实际上在输入法出现时会多次调用这个onGlobalLayout方法。
						if (preHeight == heightDiff) {
							return;
						}
						preHeight = heightDiff;
						if (heightDiff > 200) {
							iv_group_index.setVisibility(View.GONE);
						} else {
							iv_group_index.setVisibility(View.VISIBLE);
						}
					}
				});
		tv_index_in_center = (TextView) findViewById(R.id.tv_index_in_center);
		iv_group_index = (IndexScrollBar) findViewById(R.id.scroll_group_index);
		iv_group_index.setFocusable(true);
		iv_group_index.setOnScrollListener(indexScroll);
		expandableListView = (PinnedHeaderExpandableListView) findViewById(R.id.expandablelist);

		mCountryListAdapter = new CountryListAdapter(this);
		expandableListView.setAdapter(mCountryListAdapter);
		mCountryListAdapter.setGroup(countryDataSource);
		//搜索列表 
		mSearchCountryAdapter = new SearchCountryAdapter(getSelfContext());
		search_result_list .setAdapter(mSearchCountryAdapter);
		mSearchCountryAdapter.setGroup(mSearchResultList);
		search_result_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(list_container.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				Country country = mSearchResultList.get(position);
				if (country != null) {
					Intent intent = new Intent();
					intent.putExtra(INTENT_EXTRA_COUNTRY, country);
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
			}
		});	
		// 展开所有group
		for (int i = 0, count = expandableListView.getCount(); i < count; i++) {
			expandableListView.expandGroup(i);
		}

		expandableListView.setOnHeaderUpdateListener(this);
		expandableListView.setOnChildClickListener(this);
		expandableListView.setOnGroupClickListener(this);
		lay_select_country_bar = (SearchBarView) findViewById(R.id.lay_select_country_bar);
		lay_select_country_bar.addTextWatcher(inputWatcher);
		lay_select_country_bar
				.setOnEditTextOnFocusChangeListener(new OnEditTextOnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							iv_group_index.setVisibility(View.GONE);
						} else {
							iv_group_index.setVisibility(View.VISIBLE);
						}

					}
				});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	/**
	 * 初始化城市数据
	 */
	@SuppressWarnings("unchecked")
	private void initCitiesDataSource() {
		String[] ss = new String[] { "A", "B", "C", "D", "E", "F", "G", "H",
				"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y", "Z" };
		int dataCount = ss.length;
		for (int index = 0; index < dataCount; index++) {
			Group<Country> data = mDatabaseHelper
					.queryCountryByFirstLetter(ss[index]);
			if (data != null && data.size() > 0) {
				countryDataSource.add(data);
				data.setType(ss[index]);
			}
		}
	}


	private void searchCountry(String condition) {
		if (!TextUtils.isEmpty(condition)) {
			mSearchResultList.clear();
			
			mSearchResultList.addAll(mDatabaseHelper.queryCountryByInputCondition(condition));
			
			if (mSearchResultList.size() == 0) {
				Country emptyCountry = new Country();
				emptyCountry
						.setName(getString(R.string.search_country_no_result));
				mSearchResultList.add(emptyCountry);
			}
			mSearchCountryAdapter.notifyDataSetChanged();
			list_container.setVisibility(View.GONE);
			search_result_list_container.setVisibility(View.VISIBLE);
		} else {
			search_result_list_container.setVisibility(View.GONE);
			list_container.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void updatePinnedHeader(int firstVisibleGroupPos) {
		if (firstVisibleGroupPos >= 0) {
			Group firstVisibleGroup = (Group) mCountryListAdapter
					.getGroup(firstVisibleGroupPos);
			TextView textView = (TextView) getPinnedHeader().findViewById(
					R.id.tv_group_name);
			textView.setText(firstVisibleGroup.getType());
		}
	}

	@Override
	public View getPinnedHeader() {
		if (mHeaderView == null) {
			mHeaderView = (ViewGroup) getLayoutInflater().inflate(
					R.layout.select_country_group_item, null);
			mHeaderView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
		return mHeaderView;
	}

	@Override
	public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2,
			long arg3) {
		return true;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {

		Country data = (Country) parent.getExpandableListAdapter().getChild(
				groupPosition, childPosition);
		Intent intent = new Intent();
		intent.putExtra(INTENT_EXTRA_COUNTRY, data);
		setResult(Activity.RESULT_OK, intent);
		finish();
		return false;
	}

}
