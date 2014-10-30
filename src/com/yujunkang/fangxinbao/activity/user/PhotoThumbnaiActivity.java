package com.yujunkang.fangxinbao.activity.user;

import java.io.Serializable;
import java.util.List;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.activity.ActivityWrapper;
import com.yujunkang.fangxinbao.cache.CacheableBitmapDrawable;
import com.yujunkang.fangxinbao.control.NetworkedCacheableImageView;
import com.yujunkang.fangxinbao.control.NetworkedCacheableImageView.OnImageLoadedListener;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.ImageBucket;
import com.yujunkang.fangxinbao.model.ImageItem;
import com.yujunkang.fangxinbao.utility.AlbumUtils;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.UiUtils;
import com.yujunkang.fangxinbao.widget.adapter.ImageBucketAdapter;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter.ViewBinder;

/**
 * 
 * @date 2014-8-15
 * @author xieb
 * 
 */
public class PhotoThumbnaiActivity extends ActivityWrapper {
	private static final String TAG = "PhotoThumbnaiActivity";
	private static final int REQUEST_ACTIVITY_CROP_IMAGE = 1;
	private GridView mGridView;
	private ImageBucketAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_album_activity);
		Group<ImageBucket> images = AlbumUtils.getThumbnail();
		if (images != null && images.size() > 0) {
			mGridView = (GridView) findViewById(R.id.grid_photo);
			mAdapter = new ImageBucketAdapter(getSelfContext());
			mGridView.setAdapter(mAdapter);

			mAdapter.setGroup(images);
			// 单击显示图片
			mGridView
					.setOnItemClickListener(new PhotoItemImageOnClickListener());
		} else {
			UiUtils.showAlertDialog(getString(R.string.empty_gallery), getSelfContext());
			finish();
			return;
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
			ImageBucket item = (ImageBucket) parent.getItemAtPosition(position);

			try {
				Intent intent = new Intent(getSelfContext(),
						PhotoAlbumActivity.class);
				intent.putExtra(PhotoAlbumActivity.INTENT_EXTRA_IMAGE_LIST,
						(Serializable) item.getImageList());
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
