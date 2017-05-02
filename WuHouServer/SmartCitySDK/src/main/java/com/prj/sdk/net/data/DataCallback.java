package com.prj.sdk.net.data;

import com.prj.sdk.net.bean.ResponseData;

/**
 * UI数据回调函数
 * 
 * @author admin
 * 
 */
public interface DataCallback {

	// 数据请求前调用
	public void preExecute(ResponseData request);

	// 数据回调成功
	public void notifyMessage(ResponseData request, ResponseData response) throws Exception;

	// 数据回调成功
	public void notifyError(ResponseData request, ResponseData response, Exception e);

}