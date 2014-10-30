package com.yujunkang.fangxinbao.widget.adapter;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.cache.CacheableBitmapDrawable;
import com.yujunkang.fangxinbao.control.NetworkedCacheableImageView;
import com.yujunkang.fangxinbao.control.NetworkedCacheableImageView.OnImageLoadedListener;
import com.yujunkang.fangxinbao.model.ImageBucket;
import com.yujunkang.fangxinbao.model.ImageItem;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.ImageResizer;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ImageItemAdapter extends BaseGroupAdapter<ImageItem> {
	private Context mContext;

	public ImageItemAdapter(Context context) {
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

			holder.ll_desc = convertView.findViewById(R.id.ll_desc);
			holder.iv_photo = (NetworkedCacheableImageView) convertView
					.findViewById(R.id.iv_photo);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.ll_desc.setVisibility(View.GONE);
		ImageItem item = (ImageItem) getItem(position);

		String sourcePath = item.getImagePath();
		holder.iv_photo.getLayoutParams().height = DataConstants.DEVICE_WIDTH / 3;
		if (!TextUtils.isEmpty(sourcePath)) {
			holder.iv_photo.loadImage(sourcePath, ImageResizer
					.computeSampleSize(sourcePath,
							DataConstants.DEVICE_WIDTH / 3,
							DataConstants.DEVICE_WIDTH / 3),
					new OnImageLoadedListener() {
						@Override
						public void onImageLoaded(CacheableBitmapDrawable result) {

						}

						@Override
						public void onImageSet(CacheableBitmapDrawable result) {

						}
					}, true);
		} else {
			holder.iv_photo.setImageBitmap(null);
		}

		return convertView;
	}

	class ViewHolder {
		private NetworkedCacheableImageView iv_photo;
		private View ll_desc;

	}

}
