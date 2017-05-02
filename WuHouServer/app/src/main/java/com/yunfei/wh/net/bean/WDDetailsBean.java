package com.yunfei.wh.net.bean;

import java.util.List;

/**
 * 有问必答详情
 * 
 * @author LiaoBo
 * 
 */
public class WDDetailsBean {
	public List<MoReply>		moReplyList;//回复列表
	public List<MoRecommandApp>	moRecommandAppList; // 推荐的服务

	public static class MoReply {
		public String	replyId;
		public String	observeId;
		public String	replierName;
		public String	replyTime;		// 回复日期
		public String	replyTimeDate;
		public String	replyContent;	// 回复内容
		public String	isDeleted;
		public int		agreeCount;	// 赞的数量
		public boolean	isAgree;		// 是否已赞过，如果未登录，则值始终为false未赞
	}

	public static class MoRecommandApp {
		public String	id;
		public String	observe_id;
		public String	appid;
		public String	appname;
		public String	appurls;
		public String	calltype;
	}
}
