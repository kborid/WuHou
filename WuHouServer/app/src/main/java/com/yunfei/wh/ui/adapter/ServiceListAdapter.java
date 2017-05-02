package com.yunfei.wh.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunfei.wh.R;
import com.yunfei.wh.control.ServerDispatchControl;
import com.yunfei.wh.net.bean.AppListBean;

import java.util.List;

/**
 * 服务列表适配器
 *
 * @author LiaoBo
 */
public class ServiceListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<AppListBean> mBeans;

    public ServiceListAdapter(Context context, List<AppListBean> mBeans) {
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

    public final class ViewHolder {
        private TextView tv_title, tv_circle;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final AppListBean temp = mBeans.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.lv_service_list_item, null);
            holder = new ViewHolder();
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_circle = (TextView) convertView.findViewById(R.id.tv_circle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int p = position + 1;
        int resId;
        if (p % 5 == 0) {
            resId = R.drawable.circle_darkgreen_bg;
        } else if (p % 4 == 0) {
            resId = R.drawable.circle_yellow_bg;
        } else if (p % 3 == 0) {
            resId = R.drawable.circle_origen_bg;
        } else if (p % 2 == 0) {
            resId = R.drawable.circle_green_bg;
        } else {
            resId = R.drawable.circle_blue_bg;
        }
        holder.tv_circle.setBackgroundResource(resId);

        if (temp.appname != null && temp.appname.length() > 0) {
            String title = temp.appname;
            holder.tv_title.setText(title);
            holder.tv_circle.setText(String.valueOf(title.charAt(0)));//
        }

        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ServerDispatchControl.serverEventDispatcher(mContext, temp);
            }
        });
        return convertView;
    }
}
