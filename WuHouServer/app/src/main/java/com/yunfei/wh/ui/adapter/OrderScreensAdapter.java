package com.yunfei.wh.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunfei.wh.R;
import com.yunfei.wh.net.bean.MainBannerBean;

import java.util.List;

/**
 * @author kborid
 * @date 2016/9/26
 */
public class OrderScreensAdapter extends BaseAdapter {
    private Context context;
    private List<MainBannerBean> list;

    public OrderScreensAdapter(Context context, List<MainBannerBean> list) {
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
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_orderscreens_item, null);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.text.setText(list.get(position).bnname);

        return convertView;
    }

    private class ViewHolder {
        TextView text;
    }

}
