package com.prj.sdk.net.http;

import com.alibaba.fastjson.JSONObject;
import com.prj.sdk.util.StringUtil;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * OkHttp 封装类
 *
 * @author LiaoBo
 */
public class OkHttpClientUtil {
    private static final String TAG = "OkHttpClientUtil";
    private static final int DEFAULT_READ_TIMEOUT_MILLIS = 10 * 1000; // 10s
    private static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 10 * 1000; // 10s
    private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 10 * 1000; // 10s

    private static OkHttpClientUtil mInstance;
    private OkHttpClient mOkHttpClient;

    private OkHttpClientUtil() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        builder.readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        builder.writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        mOkHttpClient = builder.build();
    }

    public static OkHttpClientUtil getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientUtil.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取OkHttpClient对象
     *
     * @return
     */
    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 同步请求
     *
     * @param request
     * @return
     */
    public Response sync(Request request) {
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 异步请求
     *
     * @param request
     * @param responseCallback
     */
    public void async(Request request, Callback responseCallback) {
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private Headers dealHeaders(Map<String, Object> header) {
        Map<String, String> temp = new HashMap<String, String>();
        if (header != null) {
            for (String key : header.keySet()) {
                if (StringUtil.isEmpty(key) || StringUtil.isEmpty(String.valueOf(header.get(key)))) {
                    continue;
                }

                temp.put(key, String.valueOf(header.get(key)));
            }
        }
        return Headers.of(temp);
    }

    // ============GET============
    public Request buildGetRequest(String url, Map<String, Object> header) {
        Request request = new Request.Builder().url(url).headers(dealHeaders(header)).get().build();
        return request;
    }

    public Response get(String url, Map<String, Object> header) {
        Request request = buildGetRequest(url, header);
        return sync(request);
    }

    public void getAsyn(String url, Map<String, Object> header, Callback responseCallback) {
        Request request = buildGetRequest(url, header);
        async(request, responseCallback);
    }

    // ============POST============
    public Request buildPostRequest(String url, Map<String, Object> header, String mJson) {
        MediaType mMediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mMediaType, mJson);
        Request request = new Request.Builder().url(url).headers(dealHeaders(header)).post(requestBody).build();
        return request;
    }

    public Request buildPostRequest(String url, Map<String, Object> header, byte[] data) {
        MediaType mMediaType = MediaType.parse("application/octet-stream; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mMediaType, data);
        Request request = new Request.Builder().url(url).headers(dealHeaders(header)).post(requestBody).build();
        return request;
    }

    public Request buildPostFormRequest(String url, Map<String, Object> header, JSONObject mJson) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : mJson.keySet()) {
            if (StringUtil.isEmpty(key) || StringUtil.isEmpty(mJson.getString(key))) {
                continue;
            }

            String value = mJson.getString(key);
            builder.add(key, value);
        }

        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).headers(dealHeaders(header)).post(requestBody).build();
    }

    public Request buildPostMultipartFormRequest(String url, Map<String, Object> header, JSONObject mJson) {
        MultipartBody.Builder builder = new MultipartBody.Builder()/*.type(MultipartBuilder.FORM)*/;
        for (String key : mJson.keySet()) {
            if (StringUtil.isEmpty(key)) {
                continue;
            }

            if (mJson.get(key) instanceof File) {
                File mFile = (File) mJson.get(key);
                if (!mFile.exists()) {
                    continue;
                }
                RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(mFile.getName())), mFile);
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\"; filename=\"" + mFile.getName() + "\""), fileBody);
            } else {
                String value = mJson.getString(key);
                if (StringUtil.isEmpty(value)) {
                    continue;
                }

                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""), RequestBody.create(null, value));
            }
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).headers(dealHeaders(header)).post(requestBody).build();
    }

    public Response post(String url, Map<String, Object> header, String mJson) {
        Request request = buildPostRequest(url, header, mJson);
        return sync(request);
    }

    public Response post(String url, Map<String, Object> header, byte[] data) {
        Request request = buildPostRequest(url, header, data);
        return sync(request);
    }

    public Response post(String url, Map<String, Object> header, JSONObject mJson) {
        Request request = buildPostFormRequest(url, header, mJson);
        return sync(request);
    }

    public Response postMultipart(String url, Map<String, Object> header, JSONObject mJson) {
        Request request = buildPostMultipartFormRequest(url, header, mJson);
        return sync(request);
    }

    public void postAsyn(String url, Map<String, Object> header, String mJson, Callback responseCallback) {
        Request request = buildPostRequest(url, header, mJson);
        async(request, responseCallback);
    }

    public void postAsyn(String url, Map<String, Object> header, byte[] data, Callback responseCallback) {
        Request request = buildPostRequest(url, header, data);
        async(request, responseCallback);
    }

    public void postAsyn(String url, Map<String, Object> header, JSONObject mJson, Callback responseCallback) {
        Request request = buildPostFormRequest(url, header, mJson);
        async(request, responseCallback);
    }

    public void postMultipartAsyn(String url, Map<String, Object> header, JSONObject mJson, Callback responseCallback) {
        Request request = buildPostMultipartFormRequest(url, header, mJson);
        async(request, responseCallback);
    }

    // ============DELETE============
    public Request buildDeleteRequest(String url, Map<String, Object> header) {
        Request request = new Request.Builder().url(url).headers(dealHeaders(header)).delete().build();
        return request;
    }

    public Response delete(String url, Map<String, Object> header) {
        Request request = buildDeleteRequest(url, header);
        return sync(request);
    }

    public void deleteAsyn(String url, Map<String, Object> header, Callback responseCallback) {
        Request request = buildDeleteRequest(url, header);
        async(request, responseCallback);
    }

    // ============PUT============
    public Request buildPutRequest(String url, Map<String, Object> header, String mJson) {
        MediaType mMediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mMediaType, mJson);
        Request request = new Request.Builder().url(url).headers(dealHeaders(header)).put(requestBody).build();
        return request;
    }

    public Request buildPutRequest(String url, Map<String, Object> header, byte[] data) {
        MediaType mMediaType = MediaType.parse("application/octet-stream; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mMediaType, data);
        Request request = new Request.Builder().url(url).headers(dealHeaders(header)).put(requestBody).build();
        return request;
    }

    public Request buildPutFormRequest(String url, Map<String, Object> header, JSONObject mJson) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : mJson.keySet()) {
            if (StringUtil.isEmpty(key) || StringUtil.isEmpty(mJson.getString(key))) {
                continue;
            }

            String value = mJson.getString(key);
            builder.add(key, value);
        }

        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).headers(dealHeaders(header)).put(requestBody).build();
    }

    public Request buildPutMultipartFormRequest(String url, Map<String, Object> header, JSONObject mJson) {
        MultipartBody.Builder builder = new MultipartBody.Builder();/*.type(MultipartBody.Builder.FORM);*/
        for (String key : mJson.keySet()) {
            if (StringUtil.isEmpty(key)) {
                continue;
            }

            if (mJson.get(key) instanceof File) {
                File mFile = (File) mJson.get(key);
                if (!mFile.exists()) {
                    continue;
                }
                RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(mFile.getName())), mFile);
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\"; filename=\"" + mFile.getName() + "\""), fileBody);
            } else {
                String value = mJson.getString(key);
                if (StringUtil.isEmpty(value)) {
                    continue;
                }

                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""), RequestBody.create(null, value));
            }
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).headers(dealHeaders(header)).put(requestBody).build();
    }

    public Response put(String url, Map<String, Object> header, String mJson) {
        Request request = buildPutRequest(url, header, mJson);
        return sync(request);
    }

    public Response put(String url, Map<String, Object> header, byte[] data) {
        Request request = buildPutRequest(url, header, data);
        return sync(request);
    }

    public Response put(String url, Map<String, Object> header, JSONObject mJson) {
        Request request = buildPutFormRequest(url, header, mJson);
        return sync(request);
    }

    public Response putMultipart(String url, Map<String, Object> header, JSONObject mJson) {
        Request request = buildPutMultipartFormRequest(url, header, mJson);
        return sync(request);
    }

    public void putAsyn(String url, Map<String, Object> header, String mJson, Callback responseCallback) {
        Request request = buildPutRequest(url, header, mJson);
        async(request, responseCallback);
    }

    public void putAsyn(String url, Map<String, Object> header, byte[] data, Callback responseCallback) {
        Request request = buildPutRequest(url, header, data);
        async(request, responseCallback);
    }

    public void putAsyn(String url, Map<String, Object> header, JSONObject mJson, Callback responseCallback) {
        Request request = buildPutFormRequest(url, header, mJson);
        async(request, responseCallback);
    }

    public void putMultipartAsyn(String url, Map<String, Object> header, JSONObject mJson, Callback responseCallback) {
        Request request = buildPutMultipartFormRequest(url, header, mJson);
        async(request, responseCallback);
    }
}