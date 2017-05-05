package com.prj.sdk.net.image;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore.Images;

import com.prj.sdk.constants.InfoType;
import com.prj.sdk.net.cache.DiskLruCache;
import com.prj.sdk.net.cache.DiskLruCache.DiskCacheType;
import com.prj.sdk.net.cache.KeyedLock;
import com.prj.sdk.net.cache.LruCache;
import com.prj.sdk.net.cache.LruCache.CacheType;
import com.prj.sdk.net.http.HttpHelper;
import com.prj.sdk.util.NetworkUtil;
import com.prj.sdk.util.ThumbnailUtil;
import com.prj.sdk.util.Utils;
import com.prj.sdk.util.concurrent.LIFOLinkedBlockingQueue;

import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 数据异步加载
 *
 * @author yue
 */
public class ImageLoader {

    private static final String TAG = ImageLoader.class.getName();

    private static ImageLoader mInstance = null;
    private final int MAX_TASK_LIMIT = 3;
    private final ThreadPoolExecutor mPoolExecutor;
    private final Handler mImageHandler;

    private final KeyedLock<String> mLock = new KeyedLock<String>();
    private final int DEFAULT_CACHE_SIZE = (int) Math.min(Runtime.getRuntime().maxMemory() / 4, 16 * 1024 * 1024);
    private final LruCache<String, Bitmap> mMemCache;
    private final DiskLruCache<String, Bitmap> mDiskCache;

    private ImageLoader() {
        mPoolExecutor = new ThreadPoolExecutor(0, MAX_TASK_LIMIT, 0L, TimeUnit.MILLISECONDS, new LIFOLinkedBlockingQueue<Runnable>());
        mMemCache = new LruCache<String, Bitmap>(DEFAULT_CACHE_SIZE, CacheType.SIZE);
        mDiskCache = new DiskLruCache<String, Bitmap>(new File(Utils.getFolderDir("imageCache")), 50 * 1024 * 1024, DiskCacheType.SIZE);

        mImageHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 获得实例的唯一全局访问点
     *
     * @return
     */
    public static ImageLoader getInstance() {
        if (mInstance == null) {
            // 增加类锁,保证只初始化一次
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader();
                }
            }
        }
        return mInstance;
    }

    public Bitmap loadBitmap(ImageCallback callback, String imageUrl, String imageTag, int width, int height, int round, boolean limitMax, LoadType mType) {
        try {
            width = width < 0 ? 0 : width;
            height = height < 0 ? 0 : height;
            round = round < 0 ? 0 : round;

            Bitmap bm = getCacheBitmap(imageUrl, width, height, limitMax);

            if (bm == null) {
                ImageRequest request = new ImageRequest(callback, imageUrl, imageTag, width, height, round, limitMax, mType);
                insertRequestAtFrontOfQueue(request);
            } else {
                callback.imageCallback(bm, imageUrl, imageTag);
            }
            return bm;
        } catch (Exception e) {
            e.printStackTrace();
            mDiskCache.remove(imageUrl);
        }
        return null;
    }

    public Bitmap loadBitmap(ImageCallback callback, String imageUrl, String imageTag, int width, int height, int round) {
        return loadBitmap(callback, imageUrl, imageTag, width, height, round, false, LoadType.ALL);
    }

    public Bitmap loadBitmap(ImageCallback callback, String imageUrl) {
        return loadBitmap(callback, imageUrl, imageUrl, 0, 0, 0, false, LoadType.ALL);
    }

    public Bitmap loadBitmap(ImageCallback callback, String imageUrl, String imageTag) {
        return loadBitmap(callback, imageUrl, imageTag, 0, 0, 0, false, LoadType.ALL);
    }

    public Bitmap getCacheBitmap(String imageUrl) {
        return getCacheBitmap(imageUrl, 0, 0, false);
    }

    public Bitmap getCacheBitmap(String imageUrl, int width, int height) {
        return getCacheBitmap(imageUrl, width, height, false);
    }

    public Bitmap getCacheBitmap(String imageUrl, int width, int height, boolean limitMax) {
        try {
            width = width < 0 ? 0 : width;
            height = height < 0 ? 0 : height;

            if (imageUrl == null) {
                return null;
            }

            Bitmap bm = mMemCache.get(imageUrl);
            if (bm == null) {
                File mFile = new File(imageUrl);
                if (mFile.exists()) {
                    byte[] data = StreamUtil.convertToBytes(mFile);
                    bm = ThumbnailUtil.getImageThumbnail(data, width, height, limitMax);

                    if (bm != null) {
                        mMemCache.put(imageUrl, bm);
                    }
                } else {
                    bm = mDiskCache.get(imageUrl);

                    if (bm != null) {
                        bm = ThumbnailUtil.getImageThumbnail(bm, width, height, limitMax);
                        mMemCache.put(imageUrl, bm);
                    }
                }
            }

            return bm;
        } catch (Exception e) {
            // TODO: handle exception
        } catch (OutOfMemoryError oom) {
            // TODO: handle exception
        }
        return null;
    }

    public void putCacheBitmap(String imageUrl, Bitmap bm) {
        try {
            mMemCache.put(imageUrl, bm);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void putDiskBitmap(String imageUrl, Bitmap bm) {
        try {
            mDiskCache.put(imageUrl, bm);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * 把任务请求加入任务队列头部
     *
     * @param request
     */
    private void insertRequestAtFrontOfQueue(ImageRequest request) {
        mPoolExecutor.execute(new RequestTask(request));
    }

    /**
     * 和服务器交互线程
     *
     * @author yue
     */
    private class RequestTask extends Thread {

        private ImageRequest request;
        public HttpHelper mHttpHelper;

        public RequestTask(ImageRequest request) {
            this.request = request;
            mHttpHelper = new HttpHelper();
//			mHttpHelper.mType = request.mType;
        }

        @Override
        public void run() {
            try {
                onPreExecute();
                doInBackground();
                onPostExecute();
            } catch (Exception e) {
                mMemCache.remove(request.url);
            } catch (OutOfMemoryError e2) {
                e2.printStackTrace();
            } finally {
                handleMessage(request);
            }
        }

        /**
         * 数据请求之前调用
         */
        protected void onPreExecute() {

        }

        /**
         * 数据请求后调用
         */
        protected void onPostExecute() {

        }

        /**
         * 核心执行方法
         *
         * @throws Exception
         */
        protected void doInBackground() throws Exception {
            try {
                Bitmap bm = mMemCache.get(request.url);// 只检测内存即可
                mLock.lock(request.url);
                byte[] data = null;
                if (bm == null && Utils.checkUrl(request.url) && NetworkUtil.isNetworkAvailable()) {
                    data = mHttpHelper.executeHttpRequest(request.url, InfoType.GET_REQUEST.toString(), null, null, false);
                } else {
                    File mFile = new File(request.url);
                    if (mFile.exists()) {
                        if (request.url.endsWith(".mp4")) {
                            Bitmap temp = ThumbnailUtils.createVideoThumbnail(mFile.getAbsolutePath(), Images.Thumbnails.MINI_KIND);
                            data = ThumbnailUtil.getImageThumbnailBytes(temp, 100);
                        } else {
                            data = StreamUtil.convertToBytes(mFile);
                        }
                    }
                }

                if (data != null) {
                    try {
                        bm = ThumbnailUtil.getImageThumbnail(data, request.width, request.height, request.limitMax);

                        if (request.round > 0) {
                            bm = ThumbnailUtil.getRoundImage(bm, request.round);
                        }
                    } catch (OutOfMemoryError oom) {
                        clearMemory();
                    }
                    mMemCache.put(request.url, bm);
                    if (Utils.isSDCardEnable()) {
                        mDiskCache.put(request.url, bm);
                    }
                }
                request.bm = bm;
            } finally {
                mLock.unlock(request.url);
            }
        }

    }

    public void remove(String imageUrl) {
        mMemCache.remove(imageUrl);
        mDiskCache.remove(imageUrl);
    }

    public void removeMem(String imageUrl) {
        mMemCache.remove(imageUrl);
    }

    public void removeDisk(String imageUrl) {
        mDiskCache.remove(imageUrl);
    }

    public void clearMemory() {
        mMemCache.clear();
    }

    /**
     * 清除请求队列中的任务
     */
    public void clearImageRequests() {
        mPoolExecutor.getQueue().clear();
        mMemCache.clear();
    }

    public void shutDown() {
        mMemCache.clear();
        mPoolExecutor.shutdown();
    }

    /**
     * 向UI层发送消息
     *
     * @param request 请求参数
     */
    private void handleMessage(final ImageRequest request) {
        // LoggerUtil.d(TAG, "图片请求返回:" + " url:" + request.url);
        mImageHandler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    if (request.callback != null)
                        request.callback.imageCallback(request.bm, request.url, request.imageTag);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        });

    }

    // 对外界开放的回调接口
    public interface ImageCallback {
        // 注意 此方法是用来设置目标对象的图像资源
        public void imageCallback(Bitmap bm, String url, String imageTag);
    }

    public enum LoadType {
        ALL,
        CURRENT_VIEW
    }
}