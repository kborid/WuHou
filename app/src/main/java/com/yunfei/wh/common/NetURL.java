package com.yunfei.wh.common;

import com.prj.sdk.app.AppContext;
import com.prj.sdk.util.SharedPreferenceUtil;
import com.yunfei.wh.R;
import com.yunfei.wh.ui.fragment.TabHomeFragment;
import com.yunfei.wh.ui.fragment.TabServerCenterFragment;

/**
 * 接口常量地址
 *
 * @author LiaoBo
 */
public final class NetURL {

	public static String getApi() {
		final String[] SERVER_USRS = {AppContext.mAppContext.getString(R.string.uat_server_url), AppContext.mAppContext.getString(R.string.release_server_url), "http://192.168.1.222/"};

		if (AppConst.ISDEVELOP) {
            int status = SharedPreferenceUtil.getInstance().getInt(AppConst.APPTYPE, 0);
            return SERVER_USRS[status];
        } else {
            return SERVER_USRS[1];
        }
	}

    public static String getWhHmApi() {
        return AppContext.mAppContext.getString(R.string.comm_server_url);
    }

    public static String getWeatherInfoApi() {
        return "http://www.zaichengdu.com/";
    }

	public static String			API_LINK				= getApi();
    public static String            COMM_API_LINK           = getWhHmApi();
    public static String            WEATHER_API             = getWeatherInfoApi();

    public static final String      PORTAL                  = AppContext.mAppContext.getString(R.string.portal);
    public static final String      COMM_PORTAL             = AppContext.mAppContext.getString(R.string.comm_portal);
	public static final String		WH_PORTAL_SERVICE		= API_LINK + PORTAL + "service/";// 武侯portal地址
    public static final String		WH_PORTAL_PUBLIC   		= API_LINK + PORTAL + "public/";
    public static final String      HM_PROTAL_SERVICE       = COMM_API_LINK + COMM_PORTAL + "service/";
    public static final String      HM_PROTAL_PUBLIC        = COMM_API_LINK + COMM_PORTAL + "public/";
	public static final String		SMPAY_SERVICE			= API_LINK + "smpay/service/";// smpay地址
	public static final String		APP_SCITY_CN			= "http://app.scity.cn";
	public static final String		UPLOAD					= API_LINK + "img/base64upload";										// H5上传图片到服务器
	public static final String		IS_SC					= APP_SCITY_CN + "/center_appservice/service/CW0210";					// 校验是否收藏
	public static final String		ADD_SC					= APP_SCITY_CN + "/center_appservice/service/CW0203";					// 添加收藏
	public static final String		REMOVE_SC				= APP_SCITY_CN + "/center_appservice/service/CW0204";					// 取消收藏

    //---------------------------------用户信息、ticket相关-----------------------------------------
    public static final String		CHECK_PHONE				= WH_PORTAL_SERVICE + "CW9002";// 手机号是否被占用
    public static final String		GET_YZM					= WH_PORTAL_SERVICE + "CW9003";// 获取验证码
    public static final String		REGISTER				= WH_PORTAL_SERVICE + "CW9005";// 注册
    public static final String		GET_TICKET				= WH_PORTAL_SERVICE + "CW9006";// 登录获取票据
    public static final String      YZM_LOGIN               = WH_PORTAL_SERVICE + "CW9008"; //短信验证码登录
    public static final String		REMOVE_TICKET			= WH_PORTAL_SERVICE + "CW9009";// 注销票据
    public static final String		GET_USER_INFO			= WH_PORTAL_SERVICE + "CW9012";// 获取用户信息
    public static final String		UPDATA_USER_INFO		= WH_PORTAL_SERVICE + "CW9013";// 修改用户信息
    public static final String		VERIFY_PWD				= WH_PORTAL_SERVICE + "CW9014";// 验证用户登录密码
    public static final String		UPDATA_PHONE			= WH_PORTAL_SERVICE + "CW9015";// 修改绑定手机号
    public static final String		UPDATA_LOGIN_PWD		= WH_PORTAL_SERVICE + "CW9018";// 修改密码
    public static final String		FORGET_PWD				= WH_PORTAL_SERVICE + "CW9021";// 忘记密码
    public static final String		UPDATA_PHOTO			= WH_PORTAL_SERVICE + "CW9022";// 修改头像

    // ----------------------------------------使用第三方登录绑定--------------------------------
    public static final String		BIND_CHECK				= WH_PORTAL_SERVICE + "CW9025";// 检查是否绑定了第三方
    public static final String		BIND_ACCESS				= WH_PORTAL_SERVICE + "CW9026";// 绑定三方访问,返回访问票据
    public static final String      BIND_WX                 = WH_PORTAL_SERVICE + "CW90261";//绑定微信
    public static final String		BIND_LIST				= WH_PORTAL_SERVICE + "CW1013";// 绑定列表
    public static final String		VALIDATE_TICKET			= WH_PORTAL_SERVICE + "CW1014";// 判断票据是否过期
    public static final String		UNBIND					= WH_PORTAL_SERVICE + "CW1015";// 解绑
    public static final String		BIND					= WH_PORTAL_SERVICE + "CW1016";// 绑定
    public static final String		SERVICE_DATA			= WH_PORTAL_SERVICE + "CW10121";// 获得所属的服务
    // --------------------------------------首页-----------------------------------------------
    public static final String		WH_BANNER				= WH_PORTAL_SERVICE + "CW1006";
    // --------------------------------------天气-----------------------------------------------
    public static final String		WEATHER_SERVER			= WEATHER_API + "center_weatherserver/service/CW0101";					// 天气
    // --------------------------------------社区-----------------------------------------------
    public static final String      HM_BANNER               = HM_PROTAL_SERVICE + "CW1006";
    public static final String      ALL_HM_SERVER           = HM_PROTAL_SERVICE + "CW1012";
    public static final String      COMMUNITY_LIST          = HM_PROTAL_SERVICE + "CW1050";
    public static final String      COMMUNITY_SERVICE       = HM_PROTAL_SERVICE + "CW1051";
	// --------------------------------------发现-----------------------------------------------
	public static final String      DIS_CHANNEL             = WH_PORTAL_SERVICE + "CW6001";
	public static final String      DIS_CHANNELS_ID         = WH_PORTAL_SERVICE + "CW6002";
    public static final String      DIS_COMMENTS_ID         = WH_PORTAL_SERVICE + "CW6003";
    public static final String      DIS_PUBLISH             = WH_PORTAL_SERVICE + "CW6004";
    public static final String      DIS_SUPPORT             = WH_PORTAL_SERVICE + "CW6006";
    public static final String      DIS_CANCEL_SUPPORT      = WH_PORTAL_SERVICE + "CW6007";
    public static final String      DIS_SUPPORT_STATUS      = WH_PORTAL_SERVICE + "CW6008";
    // --------------------------------------消息-----------------------------------------------
    public static final String      NOTIFY_COUNT            = WH_PORTAL_SERVICE + "NOTIFY0001";
    public static final String      NOTIFY_LIST             = WH_PORTAL_SERVICE + "NOTIFY0002";
    // --------------------------------------我的-----------------------------------------------
    public static final String      MY_ORDER                = WH_PORTAL_PUBLIC + "yyph/h5/yyph_myTakeNo.jsp";
    public static final String      MY_NUMBER               = WH_PORTAL_PUBLIC + "yyph/h5/yyph_myTakeNo2.jsp";
    public static final String      MY_ISSUER               = API_LINK + PORTAL + "yjqj/service/Wap.myTrades.do";
    public static final String      MY_DELEGATE             = WH_PORTAL_PUBLIC + "agency/h5/agency_myorder.jsp";
    public static final String		UPLOAD_IMG			    = WH_PORTAL_SERVICE + "CW1705";
    public static final String		IDENTITY_VERIFICATION	= WH_PORTAL_SERVICE + "CW0234";// 实名认证
    public static final String		INVITE_LIST				= WH_PORTAL_SERVICE + "CW9027";// 邀请人员列表
    public static final String		WH_FEEDBACK				= WH_PORTAL_SERVICE + "CW9023";
    // -------------------------------------地址管理---------------------------------------------------
    public static final String		ADD_ADDRESS				= WH_PORTAL_SERVICE + "UA0001";// 新增地址
    public static final String		EDIT_ADDRESS			= WH_PORTAL_SERVICE + "UA0002";// 编辑地址
    public static final String		DELETE_ADDRESS			= WH_PORTAL_SERVICE + "UA0003";// 删除地址
    public static final String		SELECT_ADDRESS			= WH_PORTAL_SERVICE + "UA0004";// 查询全部地址

    //-----------------------------------------学校地图-------------------------------------------------
    public static final String      SCHOOL_MAP              = WH_PORTAL_SERVICE + "Map0001";
    public static final String      SCHOOL_MAP_DETAIL       = WH_PORTAL_SERVICE + "Map0002";

	// -----------------------------------WEB协议 H5页------------------------------------------------
	public static final String		ABOUT_AS				= WH_PORTAL_PUBLIC + "content/h5/ContentTable_index.jsp?type=gywm";// 关于我们
	public static final String		PROBLEM					= WH_PORTAL_PUBLIC + "content/h5/ContentTable_index.jsp?type=cjwt";// 常见问题
	public static final String		REGISTER_AGEMENNT		= WH_PORTAL_PUBLIC + "content/h5/ContentTable_index.jsp?type=zcxy";// 注册协议
	public static final String		IDENTITY_PROTOCOL		= WH_PORTAL_PUBLIC + "content/h5/ContentTable_index.jsp?type=smrz";// 实名认证协议
    public static final String      SERVER_CENTER           = WH_PORTAL_PUBLIC + "h5/whsq/dtgg.jsp";
    public static final String      IDENTITY_VERIFICATE     = "http://www.cdwh.org/ht/ident/html/index.html";

	// -------------------------------------------app信息（强制升级、邀请信息等）、广告---------------------------------
	public static final String		APP_INFO				= WH_PORTAL_SERVICE + "CW1010";// app基本信息
	public static final String		ADVERTISEMENT			= WH_PORTAL_SERVICE + "CW1011";// 广告

	// --------------------------------------------设置缓存的URL-----------------------------------
	public static final String[]	CACHE_URL = {
            WH_BANNER + TabHomeFragment.BANNER_HOME_TOP,
            WH_BANNER + TabHomeFragment.BANNER_SERVER,
            WH_BANNER + TabServerCenterFragment.BANNER_TOP,
            WH_BANNER + TabServerCenterFragment.BANNER_CENTER_BC,
            HM_BANNER,
            SERVICE_DATA + TabHomeFragment.SERVERDATA_TYPE,
            SERVICE_DATA + TabServerCenterFragment.SERVERDATA_TYPE,
            ALL_HM_SERVER,
            DIS_CHANNEL,
            NOTIFY_LIST
    };
}