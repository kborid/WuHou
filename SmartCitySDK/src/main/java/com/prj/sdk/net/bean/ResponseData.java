package com.prj.sdk.net.bean;

import java.util.Map;

/**
 * 服务器请求数据及返回
 * 
 * @author Liao
 * 
 */
public class ResponseData {
	public int					flag;
	public int					retry		= 1;	// 失败后尝试加载次数
	public boolean				isForm;				// 是否是form表单数据提交
	public String				path;
	public String				key;
	public String				type;
	public Map<String, Object>	header;

	public boolean				isLocal;
	public String				code;		// 出错代码

	public Object				body;
	public Object				head;
	public Object				data;		// 数据对象
}
