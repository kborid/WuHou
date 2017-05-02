package com.yunfei.wh.net.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 有问必答 实体
 * 
 * @author LiaoBo
 * 
 */
public class YWBDBean {
	public int			pageNo;	// 当前分页
	public String		pageSize;	// 分页的大小
	public int			totalCount; // 查询结果总页数
	public int			first;
	public boolean		desc;
	public String		totalPages;
	public boolean		hasNext;
	public int			nextPage;
	public boolean		hasPre;
	public int			prePage;

	public List<Result>	result;	// 查询结果（微观列表）

	public static class Result implements Serializable {
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1L;
		public String				content;
		public String				title;
		public String				userId;
		public String				userName;
		public String				auditTime;					// 审核时间
		public String				auditTimeDate;				// ": "Jul 28, 2015 2:37:44 PM"
		public String				happenTime;				// 微观时间（yyyy-MM-dd）
		public long					happenTimeDate;
		public String				observeId;					// 微观ID
		public String				status;					// 状态（01：草稿 02:待审核 03：已回复 04：已驳回 05：后台删除 06:用户删除 07：政府删除）
		public String				govStatus;					// 转政府状态类型 （01 | NULL 未批转、02 已批转、03 正在办理、04 办理完毕）
		public String				supportAmount;				// 关注数
		public String				location;					// 位置
		public String				photoUrl;					// 微观附件的地址（是完整地址），以“,”（英文的逗号）隔开
		public String				auditComment;				// 审核评论（已驳回不为空）

	}

}
