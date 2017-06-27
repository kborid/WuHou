package com.yunfei.wh.net.bean;

import java.util.List;

/**
 * 余额查询列表数据
 * 
 * @author LiaoBo
 */
public class BalanceInquireBean {

	public int			pageNo;	// 当前分页
	public String		pageSize;	// 分页的大小
	public int			totalCount; // 查询结果总页数
	public List<Result>	result;	// 查询结果（微观列表）

	public static class Result {
		public float	amount;			// 金额
		public String	createtime;
		public long		createtimemills;
		public String	typechar;			// 描述
		public String	type;
		public String	statuschar;		// 状态
		public String	siteid;
		public String	state;
		public int		negativeOrPositive;//正负

	}
}
