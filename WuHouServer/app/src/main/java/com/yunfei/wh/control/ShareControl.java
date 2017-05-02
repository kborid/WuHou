package com.yunfei.wh.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.prj.sdk.widget.CustomToast;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.yunfei.wh.R;

/**
 * 分享操作
 */
public class ShareControl {
    private final UMSocialService mController;
    private static ShareControl mInstance = null;
    private Context mAct;

    private ShareControl(Context context) {
        mController = UMServiceFactory.getUMSocialService("com.umeng.share", RequestType.SOCIAL);
        mAct = context;
    }

    /**
     * 获得实例的唯一全局访问点
     */
    public static ShareControl getInstance(Context context) {
        if (mInstance == null) {
            // 增加类锁,保证只初始化一次
            synchronized (ShareControl.class) {
                if (mInstance == null) {
                    mInstance = new ShareControl(context);
                }
            }
        }
        return mInstance;
    }

    public UMSocialService getUMServerController() {
        if (mController != null) {
            return mController;
        }
        return null;
    }

    /**
     * @return
     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary, image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title : 要分享标题 summary : 要分享的文字概述 image url :
     * 图片地址 [以上三个参数至少填写一个] targetUrl : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
     */
    public void addQQQZonePlatform() {
        String appId = mAct.getString(R.string.qq_appid);
        String appKey = mAct.getString(R.string.qq_appkey);
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler((Activity) mAct, appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com/social");
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
//        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler((Activity) mAct, appId, appKey);
//        qZoneSsoHandler.addToSocialSDK();
    }

    /**
     * @return
     * @功能描述 : 添加微信平台分享
     */
    public void addWXPlatform() {
        String appId = mAct.getString(R.string.wx_appid);
        String appSecret = mAct.getString(R.string.wx_appsecret);
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(mAct, appId, appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(mAct, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    public void addSinaPlatform() {
        //设置新浪SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
    }

    /**
     * 发短信：调用系统短信分享，不使用友盟，因为友盟只要进入页面，页面就自动提示分享中、发送成功、分享成功
     */
    public void sendSMS(String shareContent) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
        // sendIntent.putExtra("address", "123456"); // 电话号码，这行去掉的话，默认就没有电话
        // 短信内容
        sendIntent.putExtra("sms_body", shareContent);
        sendIntent.setType("vnd.android-dir/mms-sms");
        mAct.startActivity(sendIntent);
    }

    /**
     * 根据不同的平台设置不同的分享内容</br>
     *
     * @param shareContent 分享文本类容
     * @param title        分享标题
     * @param targetUrl    目标地址
     * @param imgId        图片地址
     */
    public void setShareContent(String shareContent, String title, String targetUrl, int imgId) {
        try {
            UMImage urlImage = new UMImage(mAct, imgId);
            // 微信
            WeiXinShareContent weixinContent = new WeiXinShareContent();
            weixinContent.setShareContent(shareContent);
            weixinContent.setTitle(title);
            weixinContent.setTargetUrl(targetUrl);
            weixinContent.setShareImage(urlImage);
            mController.setShareMedia(weixinContent);

            // 设置朋友圈分享的内容
            CircleShareContent circleMedia = new CircleShareContent();
            circleMedia.setShareContent(shareContent);
            circleMedia.setTitle(title);
            circleMedia.setShareImage(urlImage);
            circleMedia.setTargetUrl(targetUrl);
            mController.setShareMedia(circleMedia);

            // QQ
            QQShareContent qqShareContent = new QQShareContent();
            qqShareContent.setShareContent(shareContent);
            qqShareContent.setTitle(title);
            qqShareContent.setShareImage(urlImage);
            qqShareContent.setTargetUrl(targetUrl);
            mController.setShareMedia(qqShareContent);

            // QQ空间
            QZoneShareContent qZoneShareContent = new QZoneShareContent();
            qZoneShareContent.setShareContent(shareContent);
            qZoneShareContent.setTitle(title);
            qZoneShareContent.setShareImage(urlImage);
            qZoneShareContent.setTargetUrl(targetUrl);
            mController.setShareMedia(qZoneShareContent);

            // sina微博
            SinaShareContent sinaShareContent = new SinaShareContent();
            sinaShareContent.setTitle(title);
            sinaShareContent.setShareContent(shareContent);
            sinaShareContent.setShareImage(urlImage);
            sinaShareContent.setTargetUrl(targetUrl);
            mController.setShareMedia(sinaShareContent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接分享，底层分享接口。如果分享的平台是新浪、腾讯微博、豆瓣、人人，则直接分享，无任何界面弹出； 其它平台分别启动客户端分享</br>
     *
     * @param platform 分享的平台
     */
    public void directShare(SHARE_MEDIA platform) {
        mController.directShare(mAct, platform, new SnsPostListener() {

            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                String showText = "分享成功";
                if (eCode != StatusCode.ST_CODE_SUCCESSED) {
                    showText = "分享失败 ";
                }
                CustomToast.show(showText, Toast.LENGTH_SHORT);
            }
        });
    }
    /*弹框分享*/
    public void postShare(SHARE_MEDIA platform) {
        mController.postShare(mAct, platform, new SnsPostListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int eCode, SocializeEntity socializeEntity) {
                String showText = "分享成功";
                if (eCode != StatusCode.ST_CODE_SUCCESSED) {
                    showText = "分享失败 ";
                }
                CustomToast.show(showText, Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * 销毁Listeners
     */
    public void destroy() {
        mController.getConfig().cleanListeners();
    }
}
