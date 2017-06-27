package com.prj.sdk.net.down;


public interface DownCallback {

	public void down(String url, String local, int down_status, String fileName);
	
}