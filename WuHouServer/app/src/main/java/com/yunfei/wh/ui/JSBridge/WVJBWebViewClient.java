package com.yunfei.wh.ui.JSBridge;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.prj.sdk.app.AppContext;
import com.prj.sdk.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * webview 与js交互桥梁创建，实现数据的对接交互
 *
 * @author LiaoBo
 */
@SuppressLint({"SetJavaScriptEnabled", "NewApi"})
public class WVJBWebViewClient extends WebViewClient {

    private final String kTag = "WVJBWebViewClient";
    private static final String kInterface = "WVJBInterface";
    private static final String kCustomProtocolScheme = "wvjbscheme";
    private static final String kQueueHasMessage = "__WVJB_QUEUE_MESSAGE__";

    private boolean logging = LogUtil.isDebug();

    protected WebView webView;

    private ArrayList<WVJBMessage> startupMessageQueue = null;
    private Map<String, WVJBResponseCallback> responseCallbacks = null;
    private Map<String, WVJBHandler> messageHandlers = null;
    private long uniqueId = 0;
    private WVJBHandler messageHandler;
    private MyJavascriptInterface myInterface = new MyJavascriptInterface();
    private String mNativeJS = null;
    protected double mLatitude, mLongitude;

    /**
     * 执行js回调函数
     */
    public interface WVJBResponseCallback {
        void callback(Object data);
    }

    /**
     * js请求处理函数
     */
    public interface WVJBHandler {
        /**
         * @param data     请求参数
         * @param callback 回调接口
         */
        void request(Object data, WVJBResponseCallback callback);
    }

    public WVJBWebViewClient(WebView webView) {
        this(webView, null);
    }

    public WVJBWebViewClient(WebView webView, WVJBHandler messageHandler) {
        this.webView = webView;
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setSupportZoom(true);
        this.webView.getSettings().setBuiltInZoomControls(true);
        this.webView.getSettings().setDisplayZoomControls(false);
        this.webView.addJavascriptInterface(myInterface, kInterface);
        this.responseCallbacks = new HashMap<String, WVJBResponseCallback>();
        this.messageHandlers = new HashMap<String, WVJBHandler>();
        this.startupMessageQueue = new ArrayList<WVJBMessage>();
        this.messageHandler = messageHandler;
    }

    public void enableLogging() {
        logging = true;
    }

    public void send(Object data) {
        send(data, null);
    }

    /**
     * java 向 js发送数据并回调
     *
     * @param data
     * @param responseCallback
     */
    public void send(Object data, WVJBResponseCallback responseCallback) {
        sendData(data, responseCallback, null);
    }

    public void callHandler(String handlerName) {
        callHandler(handlerName, null, null);
    }

    public void callHandler(String handlerName, Object data) {
        callHandler(handlerName, data, null);
    }

    /**
     * 调用JavaScript注册处理程序
     *
     * @param handlerName
     * @param data
     * @param responseCallback
     */
    public void callHandler(String handlerName, Object data, WVJBResponseCallback responseCallback) {
        sendData(data, responseCallback, handlerName);
    }

    /**
     * 注册处理程序，使JavaScript可以调用
     *
     * @param handlerName
     * @param handler
     */
    public void registerHandler(String handlerName, WVJBHandler handler) {
        if (handlerName == null || handlerName.length() == 0 || handler == null)
            return;
        messageHandlers.put(handlerName, handler);
    }

    private void sendData(Object data, WVJBResponseCallback responseCallback, String handlerName) {
        if (data == null && (handlerName == null || handlerName.length() == 0))
            return;
        WVJBMessage message = new WVJBMessage();
        if (data != null) {
            message.data = data;
        }
        if (responseCallback != null) {
            String callbackId = "objc_cb_" + (++uniqueId);
            responseCallbacks.put(callbackId, responseCallback);
            message.callbackId = callbackId;
        }
        if (handlerName != null) {
            message.handlerName = handlerName;
        }
        queueMessage(message);
    }

    private void queueMessage(WVJBMessage message) {
        if (startupMessageQueue != null) {
            startupMessageQueue.add(message);
        } else {
            dispatchMessage(message);
        }
    }

    /**
     * 消息处理
     *
     * @param message
     */
    private void dispatchMessage(WVJBMessage message) {
        log("SEND", message.responseData);
        String messageJSON = message2JSONObject(message).toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"").replaceAll("\'", "\\\\\'").replaceAll("\n", "\\\\\n")
                .replaceAll("\r", "\\\\\r").replaceAll("\f", "\\\\\f");

        log("SEND", messageJSON);

        executeJavascript("WebViewJavascriptBridge._handleMessageFromObjC('" + messageJSON + "');");
    }

    private JSONObject message2JSONObject(WVJBMessage message) {
        JSONObject jo = new JSONObject();
        try {
            if (message.callbackId != null) {
                jo.put("callbackId", message.callbackId);
            }
            if (message.data != null) {
                jo.put("data", message.data);
            }
            if (message.handlerName != null) {
                jo.put("handlerName", message.handlerName);
            }
            if (message.responseId != null) {
                jo.put("responseId", message.responseId);
            }
            if (message.responseData != null) {
                jo.put("responseData", message.responseData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    private WVJBMessage JSONObject2WVJBMessage(JSONObject jo) {
        WVJBMessage message = new WVJBMessage();
        try {
            if (jo.has("callbackId")) {
                message.callbackId = jo.getString("callbackId");
            }
            if (jo.has("data")) {
                message.data = jo.get("data");
            }
            if (jo.has("handlerName")) {
                message.handlerName = jo.getString("handlerName");
            }
            if (jo.has("responseId")) {
                message.responseId = jo.getString("responseId");
            }
            if (jo.has("responseData")) {
                message.responseData = jo.get("responseData");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }

    private void flushMessageQueue() {
        String script = "WebViewJavascriptBridge._fetchQueue()";
        executeJavascript(script, new JavascriptCallback() {
            public void onReceiveValue(String messageQueueString) {
                if (messageQueueString == null || messageQueueString.length() == 0)
                    return;
                processQueueMessage(messageQueueString);
            }
        });
    }

    private void processQueueMessage(String messageQueueString) {
        try {
            JSONArray messages = new JSONArray(messageQueueString);
            for (int i = 0; i < messages.length(); i++) {
                JSONObject jo = messages.getJSONObject(i);

                log("RCVD", jo);

                WVJBMessage message = JSONObject2WVJBMessage(jo);
                if (message.responseId != null) {
                    WVJBResponseCallback responseCallback = responseCallbacks.remove(message.responseId);
                    if (responseCallback != null) {
                        responseCallback.callback(message.responseData);
                    }
                } else {
                    WVJBResponseCallback responseCallback = null;
                    if (message.callbackId != null) {
                        final String callbackId = message.callbackId;
                        responseCallback = new WVJBResponseCallback() {
                            @Override
                            public void callback(Object data) {
                                WVJBMessage msg = new WVJBMessage();
                                msg.responseId = callbackId;
                                msg.responseData = data;
                                queueMessage(msg);
                            }
                        };
                    }

                    WVJBHandler handler;
                    if (message.handlerName != null) {
                        handler = messageHandlers.get(message.handlerName);
                    } else {
                        handler = messageHandler;
                    }
                    if (handler != null) {
                        handler.request(message.data, responseCallback);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void log(String action, Object json) {
        if (!logging)
            return;
        String jsonString = String.valueOf(json);
        if (jsonString.length() > 500) {
            LogUtil.i(kTag, action + ": " + jsonString.substring(0, 500) + " [...]");
        } else {
            LogUtil.i(kTag, action + ": " + jsonString);
        }
    }

    public void executeJavascript(String script) {
        executeJavascript(script, null);
    }

    /**
     * 执行Javascript
     *
     * @param script
     * @param callback
     */
    public void executeJavascript(final String script, final JavascriptCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(script, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    System.out.println("OnReceiveValue value1 = " + value);
                    if (callback != null) {
                        if (value != null && value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1).replaceAll("\\\\", "");
                            System.out.println("OnReceiveValue value2 = " + value);
                        }
                        callback.onReceiveValue(value);
                    }
                }
            });
        } else {
            if (callback != null) {
                myInterface.addCallback(++uniqueId + "", callback);
                // webView.loadUrl("javascript:window." + kInterface + ".onResultForScript(" + uniqueId + "," + script + ")");
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        StringBuffer sb = new StringBuffer();
                        sb.append("javascript:window.").append(kInterface).append(".onResultForScript(").append(uniqueId).append(",").append(script).append(")");
                        webView.loadUrl(sb.toString());
                    }
                });
            } else {
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("javascript:" + script);
                    }
                });
            }
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        try {
            if (mNativeJS == null) {
                InputStream is = webView.getContext().getAssets().open("WebViewJavascriptBridge.js.txt");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                mNativeJS = new String(buffer);
            }
            executeJavascript(mNativeJS);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (startupMessageQueue != null) {
            for (int i = 0; i < startupMessageQueue.size(); i++) {
                dispatchMessage(startupMessageQueue.get(i));
            }
            startupMessageQueue = null;
        }
        super.onPageFinished(view, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith(kCustomProtocolScheme)) {
            if (url.indexOf(kQueueHasMessage) > 0) {
                flushMessageQueue();
            }
            return true;
        } else {
            try {
                // 调用拨号程序
                if (url.startsWith("tel:") || url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("wtai://wp/mc;")) {
                    Uri uri = null;
                    if (url.startsWith("wtai://wp/mc;")) {
                        uri = Uri.parse(WebView.SCHEME_TEL + url.substring("wtai://wp/mc;".length()));
                    } else {
                        uri = Uri.parse(url);
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    AppContext.mAppContext.startActivity(intent);
                    return true;
                }
                if (url.startsWith("alipays://") || url.startsWith("https://ds.alipay.com") || url.startsWith("intent://platformapi")) {
                    Uri uri = null;
                    if (url.startsWith("intent://platformapi")) {
                        uri = Uri.parse(view.getUrl());
                    } else {
                        uri = Uri.parse(url);
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                    return true;
                }

//                view.loadUrl(url);// 设置点击网页里面的链接还是在当前的webview里跳转
//                return true;
//                return super.shouldOverrideUrlLoading(view, url);
                return false;
            } catch (Exception localException) {
                localException.printStackTrace();
                return false;
            }
        }
    }

    private class WVJBMessage {
        Object data = null;
        String callbackId = null;
        String handlerName = null;
        String responseId = null;
        Object responseData = null;
    }

    private class MyJavascriptInterface {
        Map<String, JavascriptCallback> map = new HashMap<String, JavascriptCallback>();

        public void addCallback(String key, JavascriptCallback callback) {
            map.put(key, callback);
        }

        @JavascriptInterface
        public void onResultForScript(String key, String value) {
            LogUtil.i(kTag, "onResultForScript: " + value);
            JavascriptCallback callback = map.remove(key);
            if (callback != null)
                callback.onReceiveValue(value);
        }
    }

    /**
     * Js回调处理
     */
    public interface JavascriptCallback {
        /**
         * 接收获取接收信息
         *
         * @param value 接收到得js的值
         */
        public void onReceiveValue(String value);
    }
}
