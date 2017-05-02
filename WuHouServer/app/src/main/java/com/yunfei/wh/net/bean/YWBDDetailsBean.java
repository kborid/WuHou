package com.yunfei.wh.net.bean;

import java.util.List;

/**
 * 有问必答详情
 * 
 * @author LiaoBo
 * 
 */
public class YWBDDetailsBean {
	public List<MoReply>	moReplyList;

	public static class MoReply {
		public String	replyId;
		public String	observeId;
		public String	replierName;
		public String	replyTime;		// 回复日期
		public String	replyTimeDate;
		public String	replyContent;	// 回复内容
		public String	isDeleted;
	}
}
