package com.yunfei.wh.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.bumptech.glide.Glide;
import com.prj.sdk.util.Utils;
import com.yunfei.wh.R;

import java.util.ArrayList;

public class DiscoveryPictureAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> list;
    private static final int TYPE_COLUMN_ONE = 0;
    private static final int TYPE_COLUMN_TWO = 1;
    private static final int TYPE_COLUMN_THREE = 2;

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
            return TYPE_COLUMN_ONE;
        } else if (size == 2 || size == 4) {
            return TYPE_COLUMN_TWO;
        } else {
            return TYPE_COLUMN_THREE;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.discover_pic_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            LayoutParams params = (LayoutParams) viewHolder.imageView.getLayoutParams();
            if (type == TYPE_COLUMN_ONE) {
                params.width = (Utils.mScreenWidth - Utils.dip2px(40)) / 3 * 2;
                params.height = params.width;
            } else {
                params.width = (Utils.mScreenWidth - Utils.dip2px(40)) / 3;
                params.height = params.width;
            }
            viewHolder.imageView.setLayoutParams(params);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext).load(tempUrl).into(viewHolder.imageView);
        return convertView;
    }
}
