package com.yunfei.wh.net.bean;

import java.util.List;

/**
 * @author kborid
 * @date 2016/12/28 0028
 */
public class SchoolDetailInfoBean {
    public String addr;
    public List<AttachmentBean> attachment;
    public int id;
    public String imgurl;
    public String introduct;
    public String latitude;
    public String longitude;
    public String name;
    public String nature;
    public String postcode;
    public String section;
    public String tel;
    public String website;
    public List<String> piclist;

    public static class AttachmentBean {
        public String iconUrl;
        public String title;
        public List<DetailBean> detail;
    }

    public static class DetailBean {
        public String title;
        public String content;
    }
}
