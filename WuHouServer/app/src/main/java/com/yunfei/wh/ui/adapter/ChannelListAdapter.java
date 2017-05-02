package com.yunfei.wh.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunfei.wh.R;
import com.yunfei.wh.net.bean.DiscoveryChannelBean;

import java.util.List;

/**
 * Created by kborid on 2016/5/20.
 */
public class ChannelListAdapter extends BaseAdapter {
    private Context context;
    private List<DiscoveryChannelBean> list;
    private int mCurrentIndex;

    public ChannelListAdapter(Context context, List<DiscoveryChannelBean> list, int index) {
        this.context = context;
        this.list = list;
        this.mCurrentIndex = index;
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
        final HoldView holdView;
        if (convertView == null) {
            holdView = new HoldView();
            convertView = LayoutInflater.from(context).inflate(R.layout.discovery_channel_item, null);
            holdView.tv_channel = (TextView) convertView.findViewById(R.id.tv_channel);
            holdView.iv_check = (ImageView) convertView.findViewById(R.id.iv_check);
            convertView.setTag(holdView);
        } else {
            holdView = (HoldView) convertView.getTag();
        }
        holdView.tv_channel.setText(list.get(position).name);

        if (position == mCurrentIndex) {
            convertView.setSelected(true);
        } else {
            convertView.setSelected(false);
        }

        if (convertView.isSelected()) {
            holdView.tv_channel.setTextColor(context.getResources().getColor(R.color.main_color_wh));
            holdView.iv_check.setVisibility(View.VISIBLE);
        } else {
            holdView.tv_channel.setTextColor(Color.BLACK);
            holdView.iv_check.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private class HoldView {
        TextView tv_channel;
        ImageView iv_check;
    }

    public void setCurrentChannelIndex(int index) {
        if (mCurrentIndex != index) {
            mCurrentIndex = index;
        }
    }
}
