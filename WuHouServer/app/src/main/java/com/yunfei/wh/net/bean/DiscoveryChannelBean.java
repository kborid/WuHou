package com.yunfei.wh.net.bean;

/**
 * Created by kborid on 2016/5/23.
 */
public class DiscoveryChannelBean {
    public String id;
    public String name;
    public String createDate;
    public String updateDate;
    public int showCount = 10;

    public int getShowCount() {
        return showCount;
    }

    public void setShowCount(int showCount) {
        this.showCount = showCount;
    }
}
