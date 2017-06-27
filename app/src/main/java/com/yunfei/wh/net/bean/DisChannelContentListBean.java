package com.yunfei.wh.net.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kborid on 2016/5/31.
 */
public class DisChannelContentListBean {

    public String channelID;
    public int currentPageNo;
    public int totalPage;
    public boolean status;

    public List<ContentListBean> contentList;

    public DisChannelContentListBean() {
        if (contentList == null) {
            contentList = new ArrayList<>();
        } else {
            contentList.clear();
        }
    }
}
