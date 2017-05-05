package com.prj.sdk.net.image;

import android.graphics.Bitmap;

import com.prj.sdk.net.image.ImageLoader.ImageCallback;
import com.prj.sdk.net.image.ImageLoader.LoadType;

/**
 * 保存UI层的数据请求
 * 
 * @author yue
 * 
 */
public class ImageRequest {

	
	public ImageCallback	callback;	// 回调接口
	public String			url;		// 请求url
	public String          imageTag;   // ImageView tag
	public int				width;      // 返回的目标图片的宽度 -1
	public int				height;     // 返回的目标图片的高度 -1
    public int             round;      // 图片圆角效果  -1
    public boolean         limitMax;   // false:缩放到指定大小 true:不超过最大宽高
    public LoadType mType;             //加载类型
	public Bitmap			bm;		    // 请求返回的图片数据

	public ImageRequest(ImageCallback callback, String url, String imageTag, int width, int height, int round, boolean limitMax, LoadType mType) {        
		this.callback = callback;
		this.url = url;
		this.imageTag = imageTag;
		this.width = width;
		this.height = height;
		this.round = round;		
		this.limitMax = limitMax;
		this.mType = mType;
	}	
}
