# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\LI\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-optimizationpasses 5          # 指定代码的压缩级别
-dontusemixedcaseclassnames   # 是否使用大小写混合
-dontpreverify           # 混淆时是否做预校验
-verbose                # 混淆时是否记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keepclasseswithmembernames class * {  # 保持 native 方法不被混淆
    native <methods>;
}
-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {     # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

-dontwarn android.support.**
-keep class android.support.** {*;}

#==================gson==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

#==================protobuf======================
-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}

-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *; }

-dontwarn cn.easyar.engine.**
-keep class cn.easyar.engine.** { *; }

-dontwarn com.google.zxing.**
-keep class com.google.zxing.** { *; }

#友盟统计sdk
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
#JavascriptInterface的混淆处理
#-keep class com.yunfei.wh.ui.activity.HtmlActivity$HostJsDeal {
#    *;
#}

#友盟分享start

-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**

#-libraryjars libs/SocialSDK_QQZone_2.jar

-keep enum com.facebook.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.view.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**

-keep class com.facebook.**
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**

-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}

-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}

-keep public class com.yunfei.wh.R$*{
    public static final int *;
}

#友盟分享end

#OkHttp,Okio混淆start
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}
-dontwarn okio.**
#okhttp混淆end

#jpush混淆start
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
#jpush混淆end

#webview相关兼容扩展混淆
-dontwarn com.prj.sdk.widget.webview.**
-keep class com.prj.sdk.widget.webview.** { *; }

#支付宝混淆start
#-libraryjars libs/alipaySDK-20151014.jar
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.mobilesecuritysdk.*
-dontwarn com.alipay.apmobilesecuritysdk.face**
-keep class com.alipay.apmobilesecuritysdk.face.**{*;}
-dontwarn android.net.SSLCertificateSocketFactory
#支付宝end

#-keepattributes InnerClasses
#-keep class a
#-keep class b
#-keep class c
#-keep class d
#-keep class e
#-keep class f
#-keep class g
#-keep class h
#-keep class i
#-keep class j
#-keep class k
#-keep class l
#-keep class m
#-keep class n
#-keep class o
#-keep class p
#-keep class q
#-keep class r
#-keep class s

-keep class com.yunfei.wh.net.bean.** {*;}

-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses

-keepclassmembers class * {
public <methods>;
public <fields>;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}
-keepattributes InnerClasses
-keep class **.R$* {
    <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

-keep class com.newrelic.** { *; }
-dontwarn com.newrelic.**

-keep class org.apache.http.** { *; }
-keep class android.net.http.** { *; }
-dontwarn org.apache.http.**
-dontwarn android.net.http.**

#---- 高德Map 混淆---
#定位
-keep class com.amap.api.location.**{*;}
-dontwarn com.amap.api.location.**
-keep class com.amap.api.fence.**{*;}
-dontwarn com.amap.api.fence.**
-keep class com.autonavi.aps.amapapi.model.**{*;}
-dontwarn com.autonavi.aps.amapapi.model.**
#2D地图
-keep class com.amap.api.maps2d.**{*;}
-dontwarn com.amap.api.maps2d.**
-keep class com.amap.api.mapcore2d.**{*;}
-dontwarn com.amap.api.mapcore2d.**
#3D地图
-keep class com.amap.api.maps.**{*;}
-keep class com.autonavi.amap.mapcore.*{*;}
-keep class com.amap.api.trace.**{*;}
#导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}
#搜索
-keep class com.amap.api.services.**{*;}
