package com.yunfei.wh.net.bean;

import java.io.Serializable;

public class WeatherForHomeBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String pmdesc;
	public String savedate_weather;
	public String status1;
	public String status2;
	public String temperature2;
	public String temperature1;
	public String pmdata;
	public String limitnumber;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[" + "pmdesc = " + pmdesc + "savedate_weather = "
				+ savedate_weather + "status1 = " + status1 + "status2 = "
				+ status2 + "temperature1 = " + temperature1
				+ "temperature2 = " + temperature2 + "pmdata = " + pmdata + "limitnumber = " + limitnumber + "]";
	}
}
