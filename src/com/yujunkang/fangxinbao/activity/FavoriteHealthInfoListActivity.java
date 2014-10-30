package com.yujunkang.fangxinbao.activity;

import java.util.ArrayList;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.HealthEncyclopediaInfo;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.OnPageNotifyListener;
import com.yujunkang.fangxinbao.utility.DataConstants.CommonAction;
import com.yujunkang.fangxinbao.widget.adapter.HealthEncyclopediaListAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @date 2014-8-16
 * @author xieb
 * 
 */
public class FavoriteHealthInfoListActivity extends ActivityWrapper {
	public static final String INTENT_EXTRA_FAVORITE = DataConstants.PACKAGE_NAME
			+ ".FavoriteHealthInfoListActivity.INTENT_EXTRA_FAVORITE";
	private static final String TAG = "FavoriteHealthInfoListActivity";
	private ListView mListView;
	private HealthEncyclopediaListAdapter mAdapter;
	private Group<HealthEncyclopediaInfo> dataList = new Group<HealthEncyclopediaInfo>();

	private HealthEncyclopediaInfo selectedHealthEncyclopediaInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite_healthinfo_activity);
		initControl();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();
		Intent intent = getIntent();
		if (intent.hasExtra(INTENT_EXTRA_FAVORITE)) {
			ArrayList<HealthEncyclopediaInfo> data = (ArrayList<HealthEncyclopediaInfo>) intent
					.getSerializableExtra(INTENT_EXTRA_FAVORITE);
			dataList.addAll(data);
		}
	}

	private void initControl() {

		mListView = (ListView) findViewById(R.id.list);
		mAdapter = new HealthEncyclopediaListAdapter(this);
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
		mAdapter.setGroup(dataList);
	}

	private void startHealthEncyclopediaDetailActivity() {
		Intent intent = new Intent(getSelfContext(),
				HealthEncyclopediaDetailActivity.class);
		intent.putExtra(HealthEncyclopediaDetailActivity.INTENT_EXTRA_INFO,
				selectedHealthEncyclopediaInfo);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

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

	private void doFavorite(HealthEncyclopediaInfo info)
	{
		if (dataList != null && dataList.size() > 0) {

			for (HealthEncyclopediaInfo item : dataList) {
				if (item.getId().equals(info.getId())) {
					item.setIsfav(true);
				}
			}

		}
	}
	
}
