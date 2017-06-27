package com.yunfei.wh.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.prj.sdk.util.DateUtil;
import com.yunfei.wh.R;
import com.yunfei.wh.net.bean.MessageBean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author kborid
 * @date 2016/5/24
 */
public class MessageListAdapter extends BaseAdapter {
    private Context context;
    private List<MessageBean> list;

    public MessageListAdapter(Context context, List<MessageBean> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.message_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_msg_title);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_msg_content);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_title.setText(list.get(position).title);

        try {
            String time = list.get(position).recvtime; // Aug 25, 2016 5:07:18 PM
            DateFormat df = new SimpleDateFormat("MMM dd,yyyy KK:mm:ss aa", Locale.ENGLISH);
            Date date = df.parse(time);
            viewHolder.tv_time.setText(DateUtil.date2Str(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.tv_content.setText(list.get(position).content);

        return convertView;
    }

    class ViewHolder {
        private TextView tv_title;
        private TextView tv_time;
        private TextView tv_content;
    }
}
