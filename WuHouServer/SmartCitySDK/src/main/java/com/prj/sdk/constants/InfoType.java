package com.prj.sdk.constants;

/**
 * 各种信息类型
 * 
 * @author Liao
 * 
 */
public enum InfoType {
	POST_REQUEST("post"), GET_REQUEST("get"), PUT_REQUEST("put"), DELETE_REQUEST("delete"), // 请求类型
	;
	
	private String	type;

	private InfoType(String type) {
		this.type = type;
	}

	public String toString() {
		return type;
	}
}
