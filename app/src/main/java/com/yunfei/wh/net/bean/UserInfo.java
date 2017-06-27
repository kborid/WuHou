package com.yunfei.wh.net.bean;

import com.prj.sdk.util.LogUtil;
import com.yunfei.wh.common.NetURL;

/**
 * 用户信息
 * 
 * @author LiaoBo
 * 
 */
public class UserInfo {

	public UserBasic	USERBASIC;
	public UserAuth		USERAUTH;
	public LocalUser	LOCALUSER;

	public static class UserBasic {
		public String	id;
		public String	login;
		public String	name;
		public String	levelstatus;		// 01-普通用户，02-实名认证中，03-实名用户 ,04-认证被驳回，可重新申请
		public String	headphotourl;
		public String	state;
		public String	channelid;
		public String	sex;				// 用户性别 01：男，02：女
		public String	birthday;
		public String	marry;
		public String	registertime;
		public String	modifytime;
		public String	lastlogintime;
		public Object	isfirstlogin;
		public String	nickname;			// 昵称
		public String	username;			// 账户
		public String	realname;			// 真实姓名
		public String	idcardcode;		// 身份证号
		public String	idcardphotofront;	// 身份证正面
		public String	idcardphotoback;	// 背面
//		public double 	amount;//余额

		public String getPhotoUrl() {
			StringBuilder url = new StringBuilder();
			LogUtil.d("UserInfo.class ", headphotourl);
			url.append(NetURL.API_LINK).append(headphotourl);// .append("?").append(lastlogintime);
			return url.toString();
		}
	}

	public static class UserAuth {
		public String	userid;
		public String	login;
		public String	password;
		public String	mobilenum;
		public String	mobileisbound;
		public String	email;
		public String	emailisbound;
		public String	idcardcode;
		public String	spassword;
		public String	updatetime;
		public String	flag;
		public String	cardnum;
		public String	pwdstrength;
	}

	public static class LocalUser {
		public String	id;
		public String	login;
		public String	telphone;
		public String	residence;	// 所在地
		public String	isroamuser;
		public String	modifytime;
	}
}
