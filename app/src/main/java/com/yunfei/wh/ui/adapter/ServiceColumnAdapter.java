package com.yunfei.wh.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.prj.sdk.net.image.ImageLoader;
import com.prj.sdk.net.image.ImageLoader.ImageCallback;
import com.prj.sdk.util.BitmapUtils;
import com.yunfei.wh.R;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.net.bean.AppCategoryListBean;

import java.util.List;

/**
 * 服务栏目适配器
 *
 * @author LiaoBo
 */
public class ServiceColumnAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<AppCategoryListBean> mBeans;
    // 默认选中
    private int defCheckedPosition = 0;

    public ServiceColumnAdapter(Context context, List<AppCategoryListBean> mBeans) {
        this.mBeans = mBeans;
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
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

    /**
     * 记录默认选择项
     */
    public final void recordDefCheckedItem(int position) {
        defCheckedPosition = position;
    }

    public final class ViewHolder {
        private TextView tv_column;
        private ImageView iv_icon, iv_pitch;
        private View pitch_view_line;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final AppCategoryListBean temp = mBeans.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.lv_service_column_item, null);
            holder = new ViewHolder();
            holder.tv_column = (TextView) convertView.findViewById(R.id.tv_column);
            holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.iv_pitch = (ImageView) convertView.findViewById(R.id.iv_pitch);
            holder.pitch_view_line = (View) convertView.findViewById(R.id.pitch_view_line);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_column.setText(temp.catalogname);
        // 拼接图片链接
        String imgUrl = new StringBuilder().append(NetURL.API_LINK).append(temp.imgurls1).toString();
        loadBandImage(holder.iv_icon, imgUrl, (imgUrl + position));// 加载图片

        if (position == defCheckedPosition) {
            holder.tv_column.setTextColor(mContext.getResources().getColor(R.color.main_color_wh));
            holder.iv_pitch.setVisibility(View.VISIBLE);
            holder.pitch_view_line.setVisibility(View.VISIBLE);
            Bitmap bm = BitmapUtils.getAlphaBitmap(holder.iv_icon.getDrawable(), mContext.getResources().getColor(R.color.main_color_wh));
            holder.iv_icon.setImageBitmap(bm);
        } else {
            holder.tv_column.setTextColor(0xffa0a0a0);
            holder.iv_pitch.setVisibility(View.INVISIBLE);
            holder.pitch_view_line.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    /***
     * 图片绑定
     */
    public void loadBandImage(ImageView iView, String url, String tag) {
        Bitmap bm = ImageLoader.getInstance().getCacheBitmap(url);
        if (bm != null) {
            iView.setImageBitmap(BitmapUtils.getAlphaBitmap(bm, 0xff000000));//替换颜色
            iView.setTag(null);
            iView.setTag(R.id.image_url, null);
        } else {
            iView.setTag(tag);
            iView.setTag(R.id.image_url, url);
            iView.setImageResource(R.drawable.ic_logo_placeholder);
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