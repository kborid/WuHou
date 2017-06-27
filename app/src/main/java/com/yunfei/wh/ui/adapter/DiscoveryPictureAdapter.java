package com.yunfei.wh.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.prj.sdk.net.image.ImageLoader;
import com.prj.sdk.net.image.ImageLoader.ImageCallback;
import com.prj.sdk.util.DisplayUtil;
import com.prj.sdk.util.StringUtil;
import com.yunfei.wh.R;

import java.util.ArrayList;

public class DiscoveryPictureAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> list;
    private final int TYP1 = 0, TYP2 = 1, TYP3 = 2;

    public DiscoveryPictureAdapter(Context context, ArrayList<String> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        int size = list.size();
        if (size == 1) {
            return TYP1;
        } else if (size == 2 || size == 4) {
            return TYP2;
        } else {
            return TYP3;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    private final class ViewHolder {
        ImageView imageView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        int type = getItemViewType(position);
        String tempUrl = list.get(position);

        if (convertView == null) {
            switch (type) {
                case TYP1:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.discover_pic_item, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                    LayoutParams params = (LayoutParams) viewHolder.imageView.getLayoutParams();
                    params.height = DisplayUtil.dip2px(150);
                    params.width = DisplayUtil.dip2px(150);
                    viewHolder.imageView.setLayoutParams(params);
                    convertView.setTag(viewHolder);
                    break;
                case TYP2:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.discover_pic_item, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                    convertView.setTag(viewHolder);
                    break;
                case TYP3:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.discover_pic_item, parent, false);
                    viewHolder = new ViewHolder();
                    viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                    convertView.setTag(viewHolder);
                    break;
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setImgView(viewHolder.imageView, tempUrl);

        return convertView;
    }

    /**
     * 设置图片
     *
     * @param view
     * @param url
     */
    private void setImgView(final ImageView view, String url) {
        if (StringUtil.isEmpty(url)) {
            return;
        }
        view.setImageResource(R.drawable.ic_logo_placeholder);
        ImageLoader.getInstance().loadBitmap(new ImageCallback() {
            @Override
            public void imageCallback(Bitmap bm, String url, String imageTag) {
                if (bm != null) {
                    view.setImageBitmap(bm);
                    notifyDataSetChanged();
                }
            }

        }, url, url, 480, 800, 0);
    }
}
