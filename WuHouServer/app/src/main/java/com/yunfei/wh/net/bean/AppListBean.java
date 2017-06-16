package com.yunfei.wh.net.bean;

/**
 * 应用列表
 * 
 * @author LiaoBo
 * 
 */
public class AppListBean {
	// public String name;
	// public String appid;
	// public String internalver; // 最新应用版本号
	// public String appver; // 最新应用版本号(平台手动录入)
	// public String entry; // 入口地址“
	// public String entryid; // 应用程序标识ID“
	// public String isshowback; // 是否显示返回菜单(1:显示 0:不显示)
	// public String menuconfig; // 菜单配置项(回首页,回上一页,收藏,评价)
	// public String isleaf; // 是否叶子节点(0：否， 1：是 )
	// public String packageid; // 对应的应用程序包ID
	// public String packagetype; // 对应的应用程序包类型：Z012V3；wap；web
	// public String isimportant;
	// public String icon; // 菜单图标
	// public String IsRepost;
	// public String IsOnLine;
	// public String depth;
	// public int order;
	// public String pingYing;
	// public int Identity; // 用户访问用户权限(0:匿名用户能访问 1:注册用户能访问 3:实名用户能访问)
	// public Object children; // 下一级子菜单集合

	public String	id;			// 应用id
	public int		pid;

	public String	name;			// 应用名称
	public String	descstr;		//
	public String	linkurls;		// 应用路径
	public String	imgurls1;		// 应用img
	public String	imgurls2;		// 应用img
	public String	config;		//
	public String	versionid;		// 版本
	public String	isextendlink;
	public int		menutype;		// 类型

	// ---针对全部应用字段名不一样
	public String	appname;		// 应用名称
	public String	appurls;		// 应用路径
	public String	imgurls;		// 应用img
	public String	appdesc;		// 应用描述

}
