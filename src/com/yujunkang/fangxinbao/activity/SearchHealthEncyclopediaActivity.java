package com.yujunkang.fangxinbao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.yujunkang.fangxinbao.R;

import com.yujunkang.fangxinbao.control.SearchBarView;
import com.yujunkang.fangxinbao.control.SearchBarView.OnInputChangeListener;
import com.yujunkang.fangxinbao.http.UrlManager;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.HealthEncyclopediaInfo;
import com.yujunkang.fangxinbao.model.HealthEncyclopediaInfoListResult;
import com.yujunkang.fangxinbao.parser.HealthEncyclopediaInfoListParser;
import com.yujunkang.fangxinbao.task.FangXinBaoAsyncTask;
import com.yujunkang.fangxinbao.task.AsyncTaskWrapper.OnFinishedListener;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.OnPageNotifyListener;
import com.yujunkang.fangxinbao.utility.UiUtils;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;
import com.yujunkang.fangxinbao.widget.adapter.HealthEncyclopediaListAdapter;

/**
 * 
 * @date 2014-7-29
 * @author xieb
 * 
 */
public class SearchHealthEncyclopediaActivity extends ActivityWrapper {
	private static final String TAG = "SearchHealthEncyclopediaActivity";
	private ListView mListView;
	private HealthEncyclopediaListAdapter mAdapter;
	private SearchBarView lay_search_info_bar;
	private View mFooterView;

	private HealthEncyclopediaInfo selectedHealthEncyclopediaInfo;
	private StateHolder mStateHolder = new StateHolder();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_health_encyclopedia_activity);
		initControl();
	}

	private void initControl() {

		mListView = (ListView) findViewById(R.id.list);

		mAdapter = new HealthEncyclopediaListAdapter(this);
		mFooterView = LayoutInflater.from(getSelfContext()).inflate(
				R.layout.footer_list_loading, null);
		// mListView.addFooterView(mFooterView, null, false);
		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount == totalItemCount
						&& hasMoreInfo() && totalItemCount > 0) {
					startSearchTask();
				}

			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}
		});
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectedHealthEncyclopediaInfo = (HealthEncyclopediaInfo) parent
						.getItemAtPosition(position);
				startHealthEncyclopediaDetailActivity();
			}
		});
		lay_search_info_bar = (SearchBarView) findViewById(R.id.lay_search_info_bar);
		lay_search_info_bar
				.setOnInputChangeListener(new OnInputChangeListener() {

					@Override
					public void onActionSearch(String key) {
						if (!TextUtils.isEmpty(key)) {
							mStateHolder.init();
							startSearchTask();
						} else {
							UiUtils.showAlertDialog(
									getString(R.string.search_healthinfo_not_allow_empty_key),
									getSelfContext());
						}

					}
				});
	}

	private void startHealthEncyclopediaDetailActivity() {
		Intent intent = new Intent(getSelfContext(),
				HealthEncyclopediaDetailActivity.class);
		intent.putExtra(HealthEncyclopediaDetailActivity.INTENT_EXTRA_INFO,
				selectedHealthEncyclopediaInfo);
		startActivity(intent);
	}

	private boolean hasMoreInfo() {
		return mStateHolder.isHasNext();
	}

	private void onTaskCompleted(HealthEncyclopediaInfoListResult result) {
		if (result != null) {
			mStateHolder.setHasNext(result.isHasNext());
			mStateHolder.setPageIndex();
			if (hasMoreInfo() == false) {
				if (mListView.getFooterViewsCount() > 0 && mFooterView != null) {
					mListView.removeFooterView(mFooterView);
				}
			}
			if (mStateHolder.isBindData() == false) {
				mStateHolder.clear();
				mStateHolder.setResults(result.getDataList());
				putSearchResultsInAdapter(mStateHolder.getResults());
			} else {
				mStateHolder.setResults(result.getDataList());
				mAdapter.setGroup(mStateHolder.getResults());
			}

		}
	}

	public void putSearchResultsInAdapter(
			Group<HealthEncyclopediaInfo> searchResults) {

		LoggerTool.d(TAG, "putSearchResultsInAdapter");
		mListView.setAdapter(mAdapter);
		mAdapter.setGroup(searchResults);
		mStateHolder.setOnBind(true);
		if (hasMoreInfo()) {
			mListView.removeFooterView(mFooterView);
			mFooterView = LayoutInflater.from(this).inflate(
					R.layout.footer_list_loading, null);
			mListView.addFooterView(mFooterView, null, false);
		} else {
			mListView.removeFooterView(mFooterView);
		}
		if (mAdapter.getCount() == 0) {
			UiUtils.showAlertDialog("", getSelfContext());
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	private void startSearchTask() {
		FangXinBaoAsyncTask<HealthEncyclopediaInfoListResult> task = FangXinBaoAsyncTask
				.createInstance(getSelfContext(),
						UrlManager.URL_SEARCH_HEALTH_INFO,
						new HealthEncyclopediaInfoListParser(),
						getString(R.string.loading));
		task.putParameter("kw", lay_search_info_bar.getSearchKey());
		task.putParameter("page", String.valueOf(mStateHolder.getPageIndex()));
		task.setOnFinishedListener(new OnFinishedListener<HealthEncyclopediaInfoListResult>() {
			@Override
			public void onFininshed(HealthEncyclopediaInfoListResult result) {
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

	@Override
	public OnPageNotifyListener generatePageNotifyListener() {
		return new OnPageNotifyListener() {
			@Override
			public void onNotify(int what, Bundle data) {
				switch (what) {

				case CommonAction.ACTION_FAVORITE: {
					if (selectedHealthEncyclopediaInfo != null) {
						selectedHealthEncyclopediaInfo.setIsfav(true);
					}
					// if(data != null)
					// {
					// doFavorite((HealthEncyclopediaInfo)data.get(HealthEncyclopediaDetailActivity.INTENT_EXTRA_FAVORITE));
					// }
					break;
				}
				}

			}
		};
	}

	private class StateHolder {

		private boolean mHasNext;
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
			mHasNext = false;
			pageIndex = 1;
		}

		public void clear() {
			mDataList.clear();
		}

		public void setHasNext(boolean hasNext) {
			mHasNext = hasNext;
		}

		public Group<HealthEncyclopediaInfo> getResults() {
			return mDataList;
		}

		public boolean isHasNext() {
			return mHasNext;
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
