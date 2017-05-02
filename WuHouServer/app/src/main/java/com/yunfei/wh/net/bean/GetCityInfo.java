package com.yunfei.wh.net.bean;

/**
 * 获取某一个城市信息
 * 
 * @author LiaoBo
 * 
 */
public class GetCityInfo {
	public String	areaId;				// 地区ID
	public String	AreaCode;				// 地区号
	public String	CityName;				// 城市名称
	public String	Departement;			// 省份名称
	public String	AreaDescription;		// 地区描述
	public String	Father;				// 省市ID
	public String	Spell;					// 城市名称拼音
	public int	State;					// 操作系统 1:代表 未开通2:代表 待开通3:代表 已开通
	public String	Type;					// 地区类型: 1代表 可签约 2代表 不可签约
	public boolean	IsIdentity;			// 是否实名认证: True:代表 支持 false:代表 不支持
	public String	CityCardRegisterURL;	// 实名卡注册URL
	public String	CityCardBindingURL;	// 实名卡绑定URL
	public String	senderId;				// SendId

}
