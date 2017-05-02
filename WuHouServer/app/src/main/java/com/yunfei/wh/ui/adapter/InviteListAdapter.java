package com.yunfei.wh.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.prj.sdk.util.DateUtil;
import com.prj.sdk.util.StringUtil;
import com.yunfei.wh.R;
import com.yunfei.wh.net.bean.InviteListBean;

/**
 * 邀请好友列表 适配器
 * 
 * @author LiaoBo
 * 
 */
public class InviteListAdapter extends BaseAdapter {

	private Context					mContext;
	private LayoutInflater			inflater;
	private List<InviteListBean>	mBeans;

	public InviteListAdapter(Context context, List<InviteListBean> Beans) {
		this.mBeans = Beans;
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
		private TextView	tv_username, tv_date, tv_phone;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final InviteListBean temp = mBeans.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.lv_share_list_item, null);
			holder = new ViewHolder();
			holder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
			holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
			holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_username.setText(StringUtil.doEmpty(temp.username));
		holder.tv_date.setText(StringUtil.notEmpty(temp.createTimestamp) ? DateUtil.getSecond(temp.createTimestamp) : "");
		holder.tv_phone.setText(StringUtil.doEmpty(temp.phoneNum));

		return convertView;
	}

}
