package com.yunfei.whsc.common;

/**
 * 缓存全局数据
 */
public class SessionContext {

    private static String mTicket;               // 票据信息

    /**
     * 获取访问票据
     *
     * @return
     */
    public static String getTicket() {
        return mTicket;
    }

    public static void setTicket(String ticket) {
        mTicket = ticket;
    }
}
