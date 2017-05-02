package com.yunfei.wh.net.bean;

import java.util.List;

/**
 * 城市切换实体
 * 
 * @author LiaoBo
 * 
 */
public class CitySwitchBean {
	public String	areaId;
	public String	areaCode;
	public String	name;
	public String	uatDomain;
	public String	spell;
	public int		State;		// 操作系统 1:代表 未开通2:代表 待开通3:代表 已开通
	public String	Leve;
	public String	Father;
	public boolean	IsContract;
	public List<ChildList>	Child;
	public int		Operator;
	
	public static class ChildList{
		public String	areaId;
		public String	areaCode;
		public String	name;
		public String	uatDomain;
		public String	spell;
		public int		State;		// 操作系统 1:代表 未开通2:代表 待开通3:代表 已开通
		public String	Leve;
		public String	Father;
		public boolean	IsContract;
		public int		Operator;
	}
}
