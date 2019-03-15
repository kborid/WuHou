package com.prj.sdk.net.http;

import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.constants.InfoType;
import com.prj.sdk.util.StringUtil;

import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * http请求封装类 增加代理处理 URL验证 超时控制等
 */
public class HttpHelper {
    private static final String TAG = HttpHelper.class.getName();

    private Request request;
    private Response response;

    public HttpHelper() {
    }

    public byte[] executeHttpRequest(String url, String httpType, Map<String, Object> header, Object mEntity, boolean isForm) {
        ResponseBody mResponseBody = null;
        Response response = null;
        try {
            response = getResponse(url, httpType, header, mEntity, isForm);
            mResponseBody = response != null && response.isSuccessful() ? response.body() : null;
            return mResponseBody != null ? mResponseBody.bytes() : null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mResponseBody != null) {
                mResponseBody.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 执行请求，获取服务器响应数据
     */
    public Response getResponse(String url, String httpType, Map<String, Object> header, Object mEntity, boolean isForm) {
        try {
            if (InfoType.GET_REQUEST.toString().equals(httpType)) {
                if (mEntity instanceof JSONObject) {
                    JSONObject mJson = (JSONObject) mEntity;
                    StringBuffer params = new StringBuffer();
                    for (String key : mJson.keySet()) {
                        params.append(key).append("=").append(mJson.getString(key) != null ? mJson.getString(key) : "").append("&");
                    }
                    if (StringUtil.notEmpty(url)) {
                        if (url.contains("?")) {
                            if (url.endsWith("&")) {
                                url += params.toString();
                            } else {
                                url += "&" + params.toString();
                            }
                        } else {
                            url += "?" + params.toString();
                        }
                    }
                }
                request = OkHttpClientUtil.getInstance().buildGetRequest(url, header);
            } else if (InfoType.POST_REQUEST.toString().equals(httpType)) {
                if (mEntity instanceof String) {
                    String mJson = (String) mEntity;
                    request = OkHttpClientUtil.getInstance().buildPostRequest(url, header, mJson);
                } else {
                    if (mEntity instanceof JSONObject) {
                        JSONObject mJson = (JSONObject) mEntity;
                        if (isForm) {
                            request = OkHttpClientUtil.getInstance().buildPostMultipartFormRequest(url, header, mJson);
                        } else {
                            request = OkHttpClientUtil.getInstance().buildPostFormRequest(url, header, mJson);
                        }
                    } else if (mEntity instanceof byte[]) {
                        byte[] data = (byte[]) mEntity;
                        request = OkHttpClientUtil.getInstance().buildPostRequest(url, header, data);
                    } else if (mEntity == null) {
                        request = OkHttpClientUtil.getInstance().buildPostRequest(url, header, new byte[]{});
                    }
                }
            } else {
                System.out.println("warning!!!!!!" + httpType);
            }

            response = OkHttpClientUtil.getInstance().sync(request);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void disconnect() {
        try {
            OkHttpClientUtil.getInstance().getOkHttpClient().dispatcher().cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

}
