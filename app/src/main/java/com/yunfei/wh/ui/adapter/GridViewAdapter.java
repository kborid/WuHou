package com.yunfei.wh.ui.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.prj.sdk.app.AppContext;
import com.prj.sdk.db.SQLiteTemplate;
import com.prj.sdk.net.down.DownCallback;
import com.prj.sdk.net.down.DownLoaderTask;
import com.prj.sdk.net.image.ImageLoader;
import com.prj.sdk.net.image.ImageLoader.ImageCallback;
import com.prj.sdk.util.ThumbnailUtil;
import com.prj.sdk.util.Utils;
import com.prj.sdk.widget.CustomToast;
import com.prj.sdk.zip.ZipExtractorCallback;
import com.prj.sdk.zip.ZipExtractorTask;
import com.yunfei.wh.R;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.control.BundleNavi;
import com.yunfei.wh.control.ServerDispatchControl;
import com.yunfei.wh.net.bean.AppListBean;
import com.yunfei.wh.ui.activity.AllServiceActivity;
import com.yunfei.wh.ui.activity.HtmlActivity;

import java.io.File;
import java.util.List;

/**
 * 首页推荐应用九宫格展示 适配器
 *
 * @author LiaoBo
 */
public class GridViewAdapter extends BaseAdapter implements DownCallback, ZipExtractorCallback {
    private Context mContext;
    private LayoutInflater inflater;
    private List<AppListBean> mBeans;
    private ZipExtractorTask zipTask;
    private DownLoaderTask downTask;
    private String entrance;            // html程序入口
    private SQLiteTemplate mSQLiteTemplate;
    private String internalver;
    private String appver;
    private String flag;

    public GridViewAdapter(Context context, List<AppListBean> mBeans, String flag) {
        this.mBeans = mBeans;
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
        this.mSQLiteTemplate = SQLiteTemplate.getInstance(AppContext.mDBManager);
        this.flag = flag;
    }

    public int getCount() {
        return mBeans.size();
    }

    // 列表项
    public Object getItem(int position) {
        return mBeans.get(position);
    }

    // 列表id
    public long getItemId(int position) {
        return position;
    }

    public final class ViewHolder {
        private TextView tv_title;
        private ImageView imageView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final AppListBean temp = mBeans.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gv_hot_service_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.img);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            // holder.img.setLayoutParams(new GridView.LayoutParams(MDMUtils.mScreenWidth / 4, MDMUtils.mScreenWidth / 4));//
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // if (temp.isshowback != null && temp.isshowback.equals("0")) {// 0不显示
        // return convertView;
        // }

        holder.tv_title.setText(temp.appname);

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (temp.appurls.equals("AllServiceActivity")) {
                    Intent intent = new Intent(mContext, AllServiceActivity.class);
                    BundleNavi.getInstance().putInt("index", temp.pid);
                    mContext.startActivity(intent);
                } else {
                    ServerDispatchControl.serverEventDispatcher(mContext, temp);
                }
            }
        });

        // 图片绑定
        if (temp.appurls.equals("AllServiceActivity"))

        {
            holder.imageView.setImageResource(R.drawable.ic_service_more);
        } else

        {
            String url = null, tag;
            if (temp.imgurls != null) {
                if (flag.equals("HOME")) {
                    url = NetURL.API_LINK + temp.imgurls;
                } else if (flag.equals("COMMUNITY")) {
                    url = NetURL.getWhHmApi() + temp.imgurls;
                }
                tag = url + position;

                Bitmap bm = ImageLoader.getInstance().getCacheBitmap(url);
                if (bm != null) {
                    holder.imageView.setImageBitmap(ThumbnailUtil.getRoundImage(bm));
                    holder.imageView.setTag(null);
                    holder.imageView.setTag(R.id.image_url, null);
                } else {
                    holder.imageView.setImageResource(R.drawable.ic_logo_placeholder);
                    holder.imageView.setTag(tag);
                    holder.imageView.setTag(R.id.image_url, url);
                    ImageLoader.getInstance().loadBitmap(new ImageCallback() {
                        @Override
                        public void imageCallback(Bitmap bm, String url, String imageTag) {
                            if (bm != null) {
                                notifyDataSetChanged();
                            }
                        }
                    }, url, tag);
                }
            }
        }

        return convertView;
    }

    /**
     * 下载或解压处理或进入应用
     */
//    public void doDeal(String appid, String url) {
//        try {
//            if (Utils.isFolderDir("resource/" + appid)) {// 存在跳转
//                StringBuilder sb = new StringBuilder();
//                sb.append(Utils.getFolderDir("resource")).append(appid);
//                sb.append(File.separator).append(getFileName(sb.toString()));
//                sb.append(entrance);
//                sb.insert(0, "file://");
//                Intent intent = new Intent(mContext, HtmlActivity.class);// HtmlActivity WebActivity
//                // intent.putExtra("path", "file:///android_asset/cd_apps_water/webapp/default" + entryid);
//                intent.putExtra("path", sb.toString());
//                mContext.startActivity(intent);
//            } else if (Utils.isFolderDir("zip/" + appid)) {// 有下载就解压
//                if (zipTask == null || zipTask.getStatus() != AsyncTask.Status.RUNNING) {
//                    zipTask = new ZipExtractorTask(mContext, Utils.getFolderDir("zip") + appid, Utils.getFolderDir("resource") + appid, true, this);
//                    zipTask.execute();
//                }
//            } else {// 下载
//                downFile(url, appid);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    @Override
    public void down(String url, String local, int down_status, String filename) {
        if (down_status == 1) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", filename);
                contentValues.put("internalver", internalver);
                contentValues.put("appver", appver);
                if (mSQLiteTemplate.isExistsByField("down_log", "id", filename)) {// 记录数据

                    mSQLiteTemplate.updateById("down_log", filename, contentValues);
                } else {
                    mSQLiteTemplate.insert("down_log", contentValues);
                }

                if (zipTask == null || zipTask.getStatus() != AsyncTask.Status.RUNNING) {
                    zipTask = new ZipExtractorTask(mContext, local, Utils.getFolderDir("resource") + filename, true, this);
                    zipTask.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (down_status == 0) {
            // CustomToast.show("下载任务已取消", 0);
        } else {
            CustomToast.show("下载失败，已取消", 0);
        }
    }

    @Override
    public void unZip(String inPath, String outPath, int status) {
        if (status == 1) {
            StringBuilder sb = new StringBuilder();
            sb.append("file://").append(outPath).append(File.separator).append(getFileName(outPath)).append(entrance);
            Intent intent = new Intent(mContext, HtmlActivity.class);
            intent.putExtra("path", sb.toString());
            mContext.startActivity(intent);
        } else if (status == 0) {
            // CustomToast.show("解压任务已取消", 0);
        } else {
            CustomToast.show("解压失败，请重试", 0);
        }
    }

    /**
     * 获取当前文件目录子目录文件名
     *
     * @param file
     */
    public String getFileName(String file) {
        File tempFile = new File(file);
        if (tempFile.isDirectory()) // 是不是目录
            return tempFile.list()[0];// 返回该目录下所有文件及文件夹数组中的0个元素的目录名
        return "";
    }

    /**
     * 判断升级处理
     */
/*    public void doJudgeUpgrade(String appver, String appid, String url) {
        try {

            DownInfoBean temp = mSQLiteTemplate.queryForObject(new RowMapper<DownInfoBean>() {
                @Override
                public DownInfoBean mapRow(Cursor cursor, int index) {
                    DownInfoBean temp = new DownInfoBean();
                    // temp.id=cursor.getString(cursor.getColumnIndex("id"));
                    temp.internalver = cursor.getString(cursor.getColumnIndex("internalver"));
                    temp.appver = cursor.getString(cursor.getColumnIndex("appver"));
                    return temp;
                }
            }, "select * from down_log where id= ? ", new String[]{appid});
            if (temp == null) {// 初次下载，或未解压 ，直接再次下载
                // doDeal(appid, url);
                downFile(url, appid);
            } else {
                if (appver.compareTo(temp.appver) > 0) {// 有更新，重新下载
                    downFile(url, appid);
                } else {
                    doDeal(appid, url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**
     * 下载文件
     */
//    private void downFile(final String url, final String appid) {
//        if (downTask == null || downTask.getStatus() != AsyncTask.Status.RUNNING) {
//            if (NetworkUtil.isNetworkAvailable()) {
//                if (NetworkUtil.isWifi()) {
//                    downTask = new DownLoaderTask(mContext, url, appid, true, this);
//                    downTask.execute();
//                } else {
//                    downTask = new DownLoaderTask(mContext, url, appid, true, this);
//                    CustomDialog mTip = new CustomDialog(mContext);
//                    mTip.setBtnText("取消", "确定");
//                    mTip.show(mContext.getResources().getText(R.string.dialog_tip).toString());
//                    mTip.setCanceledOnTouchOutside(false);
//                    mTip.setListeners(new CustomDialog.onCallBackListener() {
//
//                        @Override
//                        public void rightBtn(CustomDialog dialog) {
//                            dialog.dismiss();
//                            downTask.execute();
//                        }
//
//                        @Override
//                        public void leftBtn(CustomDialog dialog) {
//                            dialog.dismiss();
//                            downTask.cancel(true);
//                        }
//                    });
//                }
//            } else {
//                CustomToast.show("暂无网络...", 0);
//            }
//        }
//    }
}
