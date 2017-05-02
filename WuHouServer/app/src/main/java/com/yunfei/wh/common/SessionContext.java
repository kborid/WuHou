package com.yunfei.wh.common;

import com.alibaba.fastjson.JSON;
import com.prj.sdk.util.SharedPreferenceUtil;
import com.prj.sdk.util.StringUtil;
import com.yunfei.wh.net.bean.AppCategoryListBean;
import com.yunfei.wh.net.bean.AppListBean;
import com.yunfei.wh.net.bean.DiscoveryChannelBean;
import com.yunfei.wh.net.bean.MessageBean;
import com.yunfei.wh.net.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存全局数据
 *
 * @author LiaoBo
 */
public class SessionContext {

    public static UserInfo mUser;                // 用户信息
    private static String mTicket;               // 票据信息
    private static List<AppCategoryListBean> mAllCagetoryList;    // 首页所有类型应用
    private static List<AppListBean> mAllHomeAppList;   // 首页所有应用
    private static List<AppCategoryListBean> mAllCommuCagetoryList; //社区所有类型应用
    private static List<AppListBean> mAllCommuAppList; // 获取社区所有应用列表
    private static List<AppCategoryListBean> mAllInvestmentServiceList; //办事大厅所有类型应用
    private static List<AppListBean> mAllInvestmentAppList; // 办事大厅所有应用
    private static List<DiscoveryChannelBean> mDiscoreryChannelList;
    private static List<MessageBean> mMessageList;// 消息列表


    /**
     * 获取区域信息
     *
     * @param i 1：id ； 2：name
     * @return
     */
    public static String getAreaInfo(int i) {
        String s;
        if (i == 1) {
            s = SharedPreferenceUtil.getInstance().getString("areaCode", "", true);// 默认成都 510000 510100
        } else {
            s = SharedPreferenceUtil.getInstance().getString("areaName", "", true);// 默认成都
        }

        return s;
    }

    /**
     * 设置区域信息
     *
     * @param areaCode
     * @param name
     */
    public static void setAreaCode(String areaCode, String name) {
        SharedPreferenceUtil.getInstance().setString("areaCode", areaCode, true);
        SharedPreferenceUtil.getInstance().setString("areaName", name, true);
    }

    public static List<MessageBean> getAllMessageList() {
        if (mMessageList == null) {
            mMessageList = new ArrayList<>();
        }
        return mMessageList;
    }

    public static void setAllMessageList(List<MessageBean> item) {
        if (mMessageList == null) {
            mMessageList = new ArrayList<>();
        } else {
            mMessageList.clear();
        }
        mMessageList.addAll(item);
    }

    public static List<AppCategoryListBean> getAllCommuCagetoryList() {
        if (mAllCommuCagetoryList == null) {
            mAllCommuCagetoryList = new ArrayList<>();
        }
        return mAllCommuCagetoryList;
    }

    public static void setAllCommuCagetoryList(List<AppCategoryListBean> item) {
        if (mAllCommuCagetoryList == null) {
            mAllCommuCagetoryList = new ArrayList<>();
        } else {
            mAllCommuCagetoryList.clear();
        }
        mAllCommuCagetoryList.addAll(item);
    }

    public static List<AppCategoryListBean> getAllInvestmentServiceList() {
        if (mAllInvestmentServiceList == null) {
            mAllInvestmentServiceList = new ArrayList<>();
        }
        return mAllInvestmentServiceList;
    }

    public static void setAllInvestmentServiceList(List<AppCategoryListBean> item) {
        if (mAllInvestmentServiceList == null) {
            mAllInvestmentServiceList = new ArrayList<>();
        } else {
            mAllInvestmentServiceList.clear();
        }
        mAllInvestmentServiceList.addAll(item);
    }

    /**
     * 获取办事大厅所有应用列表
     */
    public static List<AppListBean> getAllInvestmentAppList() {
        if (mAllInvestmentAppList == null) {
            mAllInvestmentAppList = new ArrayList<>();
        }
        return mAllInvestmentAppList;
    }

    public static void setAllInvestmentAppList(List<AppListBean> item) {
        if (mAllInvestmentAppList == null) {
            mAllInvestmentAppList = new ArrayList<>();
        } else {
            mAllInvestmentAppList.clear();
        }
        SessionContext.mAllInvestmentAppList.addAll(item);
    }

    /**
     * 获取社区所有应用列表
     */
    public static List<AppListBean> getAllCommuAppList() {
        if (mAllCommuAppList == null) {
            mAllCommuAppList = new ArrayList<>();
        }
        return mAllCommuAppList;
    }

    public static void setAllCommuAppList(List<AppListBean> item) {
        if (mAllCommuAppList == null) {
            mAllCommuAppList = new ArrayList<>();
        } else {
            mAllCommuAppList.clear();
        }
        SessionContext.mAllCommuAppList.addAll(item);
    }

    public static void setAllCategoryList(List<AppCategoryListBean> item) {
        if (mAllCagetoryList == null) {
            mAllCagetoryList = new ArrayList<>();
        } else {
            mAllCagetoryList.clear();
        }
        SessionContext.mAllCagetoryList.addAll(item);
    }

    public static List<AppCategoryListBean> getAllCategoryList() {
        if (mAllCagetoryList == null) {
            mAllCagetoryList = new ArrayList<>();
        }
        return mAllCagetoryList;
    }

    public static List<AppListBean> getAllHomeAppList() {
        if (mAllHomeAppList == null) {
            mAllHomeAppList = new ArrayList<>();
        }
        return mAllHomeAppList;
    }

    public static void setAllHomeAppList(List<AppListBean> item) {
        if (mAllHomeAppList == null) {
            mAllHomeAppList = new ArrayList<>();
        } else {
            mAllHomeAppList.clear();
        }
        SessionContext.mAllHomeAppList.addAll(item);
    }

    public static void setChannelList(List<DiscoveryChannelBean> item) {
        if (mDiscoreryChannelList == null) {
            mDiscoreryChannelList = new ArrayList<>();
        } else {
            mDiscoreryChannelList.clear();
        }
        SessionContext.mDiscoreryChannelList.addAll(item);
    }

    public static List<DiscoveryChannelBean> getChannelList() {
        if (mDiscoreryChannelList == null) {
            mDiscoreryChannelList = new ArrayList<>();
        }
        return mDiscoreryChannelList;
    }

    /**
     * 是否登录
     *
     * @return
     */
    public static boolean isLogin() {
        boolean ret = false;
        if (StringUtil.notEmpty(getTicket()) && mUser != null && mUser.USERBASIC != null) {
            ret = true;
        }
        return ret;
    }

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

    /**
     * 初始化用户数据
     */
    public static void initUserInfo() {
        String json = SharedPreferenceUtil.getInstance().getString(AppConst.USER_INFO, "", true);
        String ticket = SharedPreferenceUtil.getInstance().getString(AppConst.ACCESS_TICKET, "", true);
        if (StringUtil.notEmpty(json) && StringUtil.notEmpty(ticket)) {
            mUser = JSON.parseObject(json, UserInfo.class);
            setTicket(ticket);
        }
    }

    /**
     * 清除用户数据和状态
     */
    public static void cleanUserInfo() {
        mUser = null;
        mTicket = null;
        SharedPreferenceUtil.getInstance().setString(AppConst.LAST_LOGIN_DATE, "", false);// 置空登录时间
        SharedPreferenceUtil.getInstance().setString(AppConst.USER_INFO, "", false);
        SharedPreferenceUtil.getInstance().setString(AppConst.ACCESS_TICKET, "", true);
        SharedPreferenceUtil.getInstance().setString(AppConst.THIRDPARTYBIND, "", false);// 第三方绑定信息
    }

    /**
     * 销毁数据
     */
    public static void destroy() {
        mUser = null;
        if (mAllCagetoryList != null) {
            mAllCagetoryList.clear();
        }
        if (mAllCommuCagetoryList != null) {
            mAllCommuCagetoryList.clear();
        }
        if (mAllCommuAppList != null) {
            mAllCommuAppList.clear();
        }
        if (mDiscoreryChannelList != null) {
            mDiscoreryChannelList.clear();
        }
    }
}
