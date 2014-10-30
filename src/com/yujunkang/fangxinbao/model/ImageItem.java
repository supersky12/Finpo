package com.yujunkang.fangxinbao.model;

import java.io.Serializable;



/**
 * 
 * @date 2014-8-15
 * @author xieb
 * 
 */
public class ImageItem implements Serializable,BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6550340682299896195L;
	private String imageId;
	private String thumbnailPath;
	private String imagePath;
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setDesc(String desc) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int getCode() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void setCode(int code) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}


