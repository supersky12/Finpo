package com.yujunkang.fangxinbao.model;

import java.io.Serializable;
import java.util.List;





/**
 * 
 * @date 2014-8-15
 * @author xieb
 * 
 */
public class ImageBucket implements Serializable,BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7515481474029117487L;
	private int count = 0;
	private String bucketName;
	private List<ImageItem> imageList;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public List<ImageItem> getImageList() {
		return imageList;
	}
	public void setImageList(List<ImageItem> imageList) {
		this.imageList = imageList;
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


