package com.yunfei.wh.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.prj.sdk.net.image.ImageLoader;
import com.yunfei.wh.R;
import com.yunfei.wh.ui.custom.CircleImageView;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.net.bean.NewsCommentInfoBean;

import java.util.List;

/**
 * Created by kborid on 2016/6/12.
 */
public class DiscoveryCommentsAdapter extends BaseAdapter {

    private Context context;
    private List<NewsCommentInfoBean> list;

    public DiscoveryCommentsAdapter(Context context, List<NewsCommentInfoBean> list) {
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.discovery_comment_item, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_photo = (CircleImageView) convertView.findViewById(R.id.iv_photo);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_name.setText(list.get(position).userName);
        viewHolder.tv_time.setText(list.get(position).updateDateApp);
        viewHolder.tv_content.setText(list.get(position).contents);

        String url = null, tag = null;
        if (list.get(position).photosIMGPath != null) {
            url = NetURL.getApi() + list.get(position).photosIMGPath;
            tag = url + position;
            viewHolder.iv_photo.setImageResource(R.drawable.ic_logo_placeholder);
            viewHolder.iv_photo.setTag(tag);
            viewHolder.iv_photo.setTag(R.id.image_url, url);
            ImageLoader.getInstance().loadBitmap(new ImageLoader.ImageCallback() {
                @Override
                public void imageCallback(Bitmap bm, String url, String imageTag) {
                    if (bm != null) {
                        notifyDataSetChanged();
                    }
                }
            }, url, tag);
        }

        return convertView;
    }

    private class ViewHolder {
        CircleImageView iv_photo;
        TextView tv_name;
        TextView tv_time;
        TextView tv_content;
    }
}
