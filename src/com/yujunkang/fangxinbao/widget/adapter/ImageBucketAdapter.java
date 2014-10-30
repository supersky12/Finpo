package com.yujunkang.fangxinbao.widget.adapter;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.cache.CacheableBitmapDrawable;
import com.yujunkang.fangxinbao.control.NetworkedCacheableImageView;
import com.yujunkang.fangxinbao.control.NetworkedCacheableImageView.OnImageLoadedListener;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.model.ImageBucket;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.ImageResizer;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.widget.adapter.CountryListAdapter.ViewHolder;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @date 2014-8-15
 * @author xieb
 * 
 */
public class ImageBucketAdapter extends BaseGroupAdapter<ImageBucket> {
	private Context mContext;

	public ImageBucketAdapter(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_image_bucket, null);
			holder = new ViewHolder();
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_count = (TextView) convertView
					.findViewById(R.id.tv_count);
			holder.iv_photo = (NetworkedCacheableImageView) convertView
					.findViewById(R.id.iv_photo);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ImageBucket item = (ImageBucket) getItem(position);

		holder.tv_count.setText(String.valueOf(item.getCount()));
		holder.tv_name.setText(item.getBucketName());

		if (item.getImageList() != null && item.getImageList().size() > 0) {
			String thumbPath = item.getImageList().get(0).getThumbnailPath();
			String sourcePath = item.getImageList().get(0).getImagePath();
			holder.iv_photo.getLayoutParams().height = DataConstants.DEVICE_WIDTH / 3;
			if (!TextUtils.isEmpty(thumbPath)) {
				holder.iv_photo.loadImage(thumbPath, null,
						new OnImageLoadedListener() {
							@Override
							public void onImageLoaded(
									CacheableBitmapDrawable result) {
								
							}

							@Override
							public void onImageSet(
									CacheableBitmapDrawable result) {
								
							}
						}, true);
			} else if (!TextUtils.isEmpty(sourcePath)) {
				holder.iv_photo.loadImage(sourcePath, ImageResizer
						.computeSampleSize(sourcePath,
								DataConstants.DEVICE_WIDTH / 3,
								DataConstants.DEVICE_WIDTH / 3),
						new OnImageLoadedListener() {
							@Override
							public void onImageLoaded(
									CacheableBitmapDrawable result) {
							
							}
							@Override
							public void onImageSet(
									CacheableBitmapDrawable result) {
								
							}
						}, true);
			} else {
				holder.iv_photo.setImageBitmap(null);
			}
		} else {
			holder.iv_photo.setImageBitmap(null);
		}
		return convertView;
	}

	class ViewHolder {
		private NetworkedCacheableImageView iv_photo;

		private TextView tv_name;
		private TextView tv_count;
	}
}
