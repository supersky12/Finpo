package com.yujunkang.fangxinbao.activity.user;

import java.io.File;
import java.util.ArrayList;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.ImageItem;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.widget.adapter.ImageItemAdapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * 
 * @date 2014-6-2
 * @author xieb
 * 
 */
public class PhotoAlbumActivity extends ActivityWrapper {
	private static final String TAG = "PhotoAlbumActivity";
	public static final String INTENT_EXTRA_IMAGE_LIST = DataConstants.PACKAGE_NAME
			+ ".PhotoAlbumActivity.INTENT_EXTRA_EMAIL";
	private static final int REQUEST_ACTIVITY_CROP_IMAGE = 1;
	private GridView mGridView;
	private ImageItemAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_album_activity);
		mGridView = (GridView) findViewById(R.id.grid_photo);
		Intent intent = getIntent();

		if (intent.hasExtra(INTENT_EXTRA_IMAGE_LIST)) {
			Group<ImageItem> datas = new Group<ImageItem>();
			ArrayList<ImageItem> imageList = (ArrayList) intent
					.getSerializableExtra(INTENT_EXTRA_IMAGE_LIST);
			datas.addAll(imageList);
			mAdapter = new ImageItemAdapter(getSelfContext());
			mGridView.setAdapter(mAdapter);
			mAdapter.setGroup(datas);
			// 单击显示图片
			mGridView
					.setOnItemClickListener(new PhotoItemImageOnClickListener());
		} else {
			mGridView.setVisibility(View.GONE);
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_ACTIVITY_CROP_IMAGE: {
				setResult(RESULT_OK);
				finish();
				break;
			}

			}

		}
	}

	// 单击项显示图片事件监听器
	private class PhotoItemImageOnClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			LoggerTool.i("info", id + "+++++++id");
			ImageItem item = (ImageItem) parent.getItemAtPosition(position);
			try {
				Intent intent = new Intent(PhotoAlbumActivity.this,
						PictureCropActivity.class);
				intent.setDataAndNormalize(Uri.fromFile(new File(item.getImagePath())));
				startActivityForResult(intent, REQUEST_ACTIVITY_CROP_IMAGE);
			} catch (Exception e) {
				LoggerTool.e(TAG, e.getMessage());
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}
