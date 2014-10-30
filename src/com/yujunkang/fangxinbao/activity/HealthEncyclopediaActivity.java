package com.yujunkang.fangxinbao.activity;

import java.util.ArrayList;


import com.umeng.update.UmengUpdateAgent;
import com.yujunkang.fangxinbao.R;

import com.yujunkang.fangxinbao.control.NoCrashDrawerLayout;
import com.yujunkang.fangxinbao.control.pulltorefresh.PullToRefreshBase;
import com.yujunkang.fangxinbao.control.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.yujunkang.fangxinbao.control.pulltorefresh.PullToRefreshListView;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.HealthEncyclopediaClass;
import com.yujunkang.fangxinbao.model.HealthEncyclopediaInfo;
import com.yujunkang.fangxinbao.model.HealthEncyclopediaInfoListResult;
import com.yujunkang.fangxinbao.model.TemperatureData;
import com.yujunkang.fangxinbao.parser.HealthEncyclopediaClassListParser;
import com.yujunkang.fangxinbao.parser.HealthEncyclopediaInfoListParser;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.OnPageNotifyListener;
import com.yujunkang.fangxinbao.utility.UiUtils;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;
import com.yujunkang.fangxinbao.widget.adapter.HealthEncyclopediaClassListAdapter;
import com.yujunkang.fangxinbao.widget.adapter.HealthEncyclopediaListAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @date 2014-7-28
 * @author xieb
 * 
 */
public class HealthEncyclopediaActivity extends ActivityWrapper implements
		View.OnClickListener {

	private static final String TAG = "HealthEncyclopediaActivity";
	private static final int REQUEST_ACTIVITY_DETAIL = 1;
	private ListView menuListview;
	private TextView tv_title;
	private View btn_more;
	private NoCrashDrawerLayout mDrawerLayout;
	private boolean isFirstShowDialog = true;

	/**
	 * 百科分类
	 */
	private HealthEncyclopediaClassListAdapter mEncyclopediaClassAdapter;
	/**
	 * 搜索
	 */
	private View btn_search;
	private View btn_bookmark;
	private View mFooterView;
	private PullToRefreshListView mPullRefreshListView;
	private HealthEncyclopediaListAdapter mHealthEncyclopediaListAdapter;
	private ListView actualListView;

	private HealthEncyclopediaClass selectedHealthEncyclopediaClass;
	private HealthEncyclopediaInfo selectedHealthEncyclopediaInfo;
	private StateHolder mStateHolder = new StateHolder();
	private Group<HealthEncyclopediaInfo> currentInfoList;
	private Runnable closeMenu = new Runnable() {

		@Override
		public void run() {
			mDrawerLayout.closeDrawer(Gravity.RIGHT);

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.health_encyclopedia_activity);
		initControl();
		startFetchEncyclopediaClassListTask();

	}

	private void initControl() {
		mDrawerLayout = (NoCrashDrawerLayout) findViewById(R.id.drawer_layout);
		tv_title = (TextView) findViewById(R.id.tv_title);
		btn_more = findViewById(R.id.btn_more);
		btn_more.setOnClickListener(this);
		mHealthEncyclopediaListAdapter = new HealthEncyclopediaListAdapter(
				getSelfContext());
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						mStateHolder.init();
						startFetchEncyclopediaListTask();
					}
				});
		actualListView = mPullRefreshListView.getRefreshableView();
		mFooterView = LayoutInflater.from(getSelfContext()).inflate(
				R.layout.footer_list_loading, null);
		actualListView.addFooterView(mFooterView, null, false);
		actualListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount == totalItemCount
						&& hasMoreInfo() && totalItemCount > 0) {
					startFetchEncyclopediaListTask();
				}

			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}
		});
		actualListView.setAdapter(mHealthEncyclopediaListAdapter);
		actualListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectedHealthEncyclopediaInfo = (HealthEncyclopediaInfo) parent
						.getItemAtPosition(position);
				startHealthEncyclopediaDetailActivity();
			}
		});
		initMenuControl();
	}

	/**
	 * 初始化菜单控件
	 */
	private void initMenuControl() {
		menuListview = (ListView) findViewById(R.id.list);
		mEncyclopediaClassAdapter = new HealthEncyclopediaClassListAdapter(
				getSelfContext());
		View heardView = LayoutInflater.from(getSelfContext()).inflate(
				R.layout.encyclopedia_class_list_header, null);
		menuListview.addHeaderView(heardView);
		menuListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectedHealthEncyclopediaClass = (HealthEncyclopediaClass) parent
						.getItemAtPosition(position);
				mEncyclopediaClassAdapter
						.setSelectedClass(selectedHealthEncyclopediaClass);
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
				tv_title.setText(selectedHealthEncyclopediaClass.getTitle());
				mEncyclopediaClassAdapter.notifyDataSetChanged();
				mStateHolder.init();
				startFetchEncyclopediaListTask();
			}
		});
		btn_search = heardView.findViewById(R.id.btn_search);
		btn_bookmark = heardView.findViewById(R.id.btn_bookmark);
		btn_search.setOnClickListener(this);
		btn_bookmark.setOnClickListener(this);
		menuListview.setAdapter(mEncyclopediaClassAdapter);
	}

	private void startHealthEncyclopediaDetailActivity() {
		Intent intent = new Intent(getSelfContext(),
				HealthEncyclopediaDetailActivity.class);
		intent.putExtra(HealthEncyclopediaDetailActivity.INTENT_EXTRA_INFO,
				selectedHealthEncyclopediaInfo);
		startActivity(intent);
	}

	private void startSearchHealthEncyclopediaActivity() {
		Intent intent = new Intent(getSelfContext(),
				SearchHealthEncyclopediaActivity.class);
		startActivity(intent);
	}

	private void startFetchEncyclopediaClassListTask() {
		FangXinBaoAsyncTask<Group<HealthEncyclopediaClass>> task = FangXinBaoAsyncTask
				.createInstance(getSelfContext(),
						UrlManager.URL_FETCH_HEALTH_INFO_CLASS,
						new HealthEncyclopediaClassListParser(),
						false);
		task.setOnFinishedListener(new OnFinishedListener<Group<HealthEncyclopediaClass>>() {
			@Override
			public void onFininshed(Group<HealthEncyclopediaClass> result) {
				if (result == null) {
					UiUtils.showAlertDialog(
							getString(R.string.http_normal_failed),
							getSelfContext());
				} else {
					if (result.code == 1) {
						if (result.size() > 0) {
							selectedHealthEncyclopediaClass = result.get(0);
							tv_title.setText(selectedHealthEncyclopediaClass
									.getTitle());
							mEncyclopediaClassAdapter
									.setSelectedClass(selectedHealthEncyclopediaClass);
							mEncyclopediaClassAdapter.setGroup(result);
							startFetchEncyclopediaListTask();
						}
					} else {
						UiUtils.showAlertDialog(result.getDesc(),
								getSelfContext());
					}
				}

			}
		});
		task.safeExecute();
		putAsyncTask(task);
	}

	/**
	 * 百科某分类下文章列表
	 */
	private void startFetchEncyclopediaListTask() {
		FangXinBaoAsyncTask<HealthEncyclopediaInfoListResult> task;
		if(isFirstShowDialog)
		{
			 task = FangXinBaoAsyncTask
					.createInstance(getSelfContext(),
							UrlManager.URL_FETCH_HEALTH_INFO,
							new HealthEncyclopediaInfoListParser(),
							false);
		}
		else
		{
			 task = FangXinBaoAsyncTask
					.createInstance(getSelfContext(),
							UrlManager.URL_FETCH_HEALTH_INFO,
							new HealthEncyclopediaInfoListParser(),
							getString(R.string.loading));
		}
		task.putParameter("cid", selectedHealthEncyclopediaClass.getId());
		task.putParameter("page", String.valueOf(mStateHolder.getPageIndex()));
		task.setOnFinishedListener(new OnFinishedListener<HealthEncyclopediaInfoListResult>() {
			@Override
			public void onFininshed(HealthEncyclopediaInfoListResult result) {
				mPullRefreshListView.onRefreshComplete();
				isFirstShowDialog = false;
				if (result == null) {
					UiUtils.showAlertDialog(
							getString(R.string.http_normal_failed),
							getSelfContext());
				} else {
					if (result.code == 1) {
						onTaskCompleted(result);
					} else {
						UiUtils.showAlertDialog(result.getDesc(),
								getSelfContext());
					}
				}

			}
		});
		task.safeExecute();
		putAsyncTask(task);
	}

	private boolean hasMoreInfo() {
		return mStateHolder.isHasNext();
	}

	private void onTaskCompleted(HealthEncyclopediaInfoListResult result) {
		if (result != null) {
			mStateHolder.setHasNext(result.isHasNext());
			mStateHolder.setPageIndex();
			if (hasMoreInfo() == false) {
				if (actualListView.getFooterViewsCount() > 0
						&& mFooterView != null) {
					actualListView.removeFooterView(mFooterView);
				}
			}
			if (mStateHolder.isBindData() == false) {
				mStateHolder.clear();
				mStateHolder.setResults(result.getDataList());
				putSearchResultsInAdapter(mStateHolder.getResults());
			} else {
				mStateHolder.setResults(result.getDataList());
				mHealthEncyclopediaListAdapter.setGroup(mStateHolder
						.getResults());
			}
			currentInfoList = result.getDataList();

		}
	}

	public void putSearchResultsInAdapter(
			Group<HealthEncyclopediaInfo> searchResults) {

		LoggerTool.d(TAG, "putSearchResultsInAdapter");
		actualListView.setAdapter(mHealthEncyclopediaListAdapter);
		mHealthEncyclopediaListAdapter.setGroup(searchResults);
		mStateHolder.setOnBind(true);
		if (hasMoreInfo()) {
			actualListView.removeFooterView(mFooterView);
			mFooterView = LayoutInflater.from(this).inflate(
					R.layout.footer_list_loading, null);
			actualListView.addFooterView(mFooterView, null, false);
		} else {
			actualListView.removeFooterView(mFooterView);
		}
		if (mHealthEncyclopediaListAdapter.getCount() == 0) {
			UiUtils.showAlertDialog("", getSelfContext());
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search: {
			startSearchHealthEncyclopediaActivity();
			break;
		}
		case R.id.btn_bookmark: {

			ArrayList<HealthEncyclopediaInfo> result = mStateHolder
					.filterFavorite();
			if (result == null || result.size() == 0) {
				UiUtils.showAlertDialog(getString(R.string.no_favorite),
						getSelfContext());
				return;
			}
			Intent intent = new Intent(getSelfContext(),
					FavoriteHealthInfoListActivity.class);
			intent.putExtra(
					FavoriteHealthInfoListActivity.INTENT_EXTRA_FAVORITE,
					result);
			startActivity(intent);
			break;
		}
		case R.id.btn_more: {
			if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
			} else {
				mDrawerLayout.openDrawer(Gravity.RIGHT);
			}
			break;
		}
		}

	}

	@Override
	public OnPageNotifyListener generatePageNotifyListener() {
		return new OnPageNotifyListener() {
			@Override
			public void onNotify(int what, Bundle data) {
				switch (what) {

				case CommonAction.ACTION_FAVORITE: {
					// if (selectedHealthEncyclopediaInfo != null) {
					// selectedHealthEncyclopediaInfo.setIsfav(true);
					// }
					if (data != null) {
						mStateHolder
								.doFavorite((HealthEncyclopediaInfo) data
										.get(HealthEncyclopediaDetailActivity.INTENT_EXTRA_FAVORITE));
					}

					break;
				}
				}

			}
		};
	}

	private class StateHolder {

		private boolean IshasNext;
		private Group<HealthEncyclopediaInfo> mDataList = new Group<HealthEncyclopediaInfo>();

		private boolean mOnbind = false;
		private int pageIndex = 1;

		public StateHolder() {

		}

		public int getPageIndex() {
			return pageIndex;
		}

		public void setPageIndex() {
			pageIndex++;
		}

		public void init() {
			mOnbind = false;
			IshasNext = false;
			pageIndex = 1;
		}

		public void clear() {
			mDataList.clear();
		}

		public void setHasNext(boolean hasNext) {
			IshasNext = hasNext;
		}

		public Group<HealthEncyclopediaInfo> getResults() {
			return mDataList;
		}

		public ArrayList<HealthEncyclopediaInfo> filterFavorite() {
			ArrayList<HealthEncyclopediaInfo> result = new ArrayList<HealthEncyclopediaInfo>();
			if (mDataList != null && mDataList.size() > 0) {

				for (HealthEncyclopediaInfo item : mDataList) {
					if (item.Isfavorite()) {
						result.add(item);
					}
				}

			}
			return result;
		}

		public void doFavorite(HealthEncyclopediaInfo info) {
			if (mDataList != null && mDataList.size() > 0) {

				for (HealthEncyclopediaInfo item : mDataList) {
					if (item.getId().equals(info.getId())) {
						item.setIsfav(true);
					}
				}

			}
		}

		public boolean isHasNext() {
			return IshasNext;
		}

		public void setResults(Group<HealthEncyclopediaInfo> results) {
			if (results != null && results.size() > 0) {
				mDataList.addAll(results);
			}

		}

		public boolean isBindData() {
			return mOnbind;
		}

		public void setOnBind(boolean isBind) {
			mOnbind = true;
		}

	}

}
