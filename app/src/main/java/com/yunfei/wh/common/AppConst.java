package com.yunfei.wh.common;

import com.prj.sdk.BuildConfig;
import com.prj.sdk.util.LogUtil;

/**
 * 项目常量
 *
 * @author LiaoBo
 */
public final class AppConst {
    public static final boolean ISDEVELOP = BuildConfig.DEBUG;                                    // 开发者模式

    public static final String APPTYPE = "type";                                                // 类型：0代表UAT，1代表生产

    public static final String MAIN_IMG_DATA = "MAIN_IMG_DATA";                                        // 首页图片缓存
    public static final String LAST_USE_VERSIONCODE = "LAST_USE_VERSIONCODE";                                // 上一次使用的版本号
    public static final String ACCESS_TICKET = "accessTicket";                                        // 记录用户登录ticket
    public static final String USERNAME = "username";                                            // 用户名
    public static final String LAST_LOGIN_DATE = "LAST_LOGIN_DATE";                                    // 上次登录时间
    public static final String USER_PHOTO_URL = "user_photo_url";                                    // 用户头像地址
    public static final String USER_INFO = "user_info";                                            // 用户信息
    public static final String ADVERTISEMENT_INFO = "ad_info";                                            // 广告信息
    public static final String APP_INFO = "app _info";                                            // app信息

    public static final String APPID = "ARD-028-0002";//重庆"ARD-023-0001";										// appid
    public static final String VERSION = "2.0";                                                // version
    public static final String APPKEY = "9184a293402e8127346e32f4b074a137402e8127346e32f4";//重庆"84934ae8d1e384b1b058147a95cdb0c9d1e384b1b058147a";
    public static final String COUNT = "20";                                                // 分页加载数量
    public static final String PUSH_ENABLE = "PUSH_ENABLE";                                        // 是否开启消息推送
    public static final String THIRDPARTYBIND = "ThirdPartyBind";                                    // 第三方账号绑定列表

    public static final String LOCATION_LON = "location_lon";
    public static final String LOCATION_LAT = "loncation_lat";
    public static final String SITEID =  "siteId";

    public static final int ACTIVITY_IMAGE_CAPTURE = 100001;
    public static final int ACTIVITY_GET_IMAGE = 100002;
    public static final int ACTIVITY_TAILOR = 100003;
    // 广播
    public static final String ACTION_PAY_STATUS = "ACTION_PAY_STATUS";                                    // 支付状态

    // 更新时间
    public static final int UPDATE_TIME = 2 * 60 * 60 * 1000;

    // BUSINESSTYPE
    public static final String PHONE_BIND = "37555";//手机绑定
    public static final String PHONE_BIND_REGISTER = "33983";//注册绑定手机
    public static final String FIND_PWD = "35548";//找回密码
    public static final String YZM_LOGIN = "127508";
    public static final String BIND_WX = "127508";

}
