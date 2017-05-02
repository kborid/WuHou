package com.yunfei.wh.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunfei.wh.R;
import com.yunfei.wh.net.bean.UserAddrs;
import com.yunfei.wh.ui.activity.AddressEditActivity;

import java.util.List;

/**
 * 地址管理适配器
 *
 * @author LiaoBo
 */
public class AddressManageAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<UserAddrs> mBeans;
    private OnEditAddressItemChange l = null;

    public interface OnEditAddressItemChange {
        void onDeleteChange(int index);

        void onSetDefault(int index);
    }

    public void setOnEditAddressItemChange(OnEditAddressItemChange l) {
        this.l = l;
    }

    public AddressManageAdapter(Context context, List<UserAddrs> mBeans) {
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
        private TextView tv_name, tv_phone, tv_address, tv_edit, tv_delete;
        private LinearLayout layoutDefault;
        private CheckBox checkBox;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final UserAddrs temp = mBeans.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.lv_address_manage_item, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.tv_edit = (TextView) convertView.findViewById(R.id.tv_edit);
            holder.tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
            holder.layoutDefault = (LinearLayout) convertView.findViewById(R.id.layoutDefault);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText(temp.name);
        holder.tv_phone.setText(temp.phone);
        holder.tv_address.setText(temp.allCopyAddress);

        if (temp.def) {
            holder.layoutDefault.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(true);
        } else {
            holder.layoutDefault.setVisibility(View.GONE);
            holder.checkBox.setChecked(false);
        }

        holder.tv_edit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, AddressEditActivity.class);
                mIntent.putExtra("item", temp);
                mContext.startActivity(mIntent);
            }
        });

        holder.tv_delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("提示").setMessage("是否确定删除？").create();
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (l != null) {
                            l.onDeleteChange(position);
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.show();
            }
        });

        holder.checkBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (l != null) {
                    l.onSetDefault(position);
                }
            }
        });

        return convertView;
    }
}
