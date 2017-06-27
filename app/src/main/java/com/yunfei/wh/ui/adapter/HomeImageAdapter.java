package com.yunfei.wh.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prj.sdk.net.image.ImageLoader;
import com.prj.sdk.util.Utils;
import com.yunfei.wh.R;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.net.bean.MainBannerBean;

import java.util.List;

/**
 * @author kborid
 * @date 2016/8/16
 */
public class HomeImageAdapter extends BaseAdapter {

    private Context context;
    private List<MainBannerBean> list;

    public HomeImageAdapter(Context context, List<MainBannerBean> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_home_image_item, null);
            viewHolder.iv_serverbg = (ImageView) convertView.findViewById(R.id.iv_serverbg);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.iv_serverbg.getLayoutParams();
            params.height = (int) ((float) Utils.mScreenWidth * 314 / 640);
            viewHolder.iv_serverbg.setLayoutParams(params);
            viewHolder.tv_main = (TextView) convertView.findViewById(R.id.tv_main);
            viewHolder.tv_sub = (TextView) convertView.findViewById(R.id.tv_sub);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        viewHolder.tv_main.setText(list.get(position).bnname);
//        String desc = list.get(position).bndesc;
//        if (desc != null && !desc.equals("")) {
//            viewHolder.tv_sub.setVisibility(View.VISIBLE);
//            viewHolder.tv_sub.setText(list.get(position).bndesc);
//        }
        getItemIcon(list.get(position).imgurls, viewHolder.iv_serverbg);
        return convertView;
    }

    private void getItemIcon(String imgurls, final ImageView bg) {
        String url;
        if (imgurls != null) {
            url = NetURL.getApi() + imgurls;
            bg.setImageResource(R.drawable.ic_logo_placeholder);
            ImageLoader.getInstance().loadBitmap(new ImageLoader.ImageCallback() {
                @Override
                public void imageCallback(Bitmap bm, String url, String imageTag) {
                    if (bm != null) {
                        bg.setImageBitmap(bm);
                    }
                }
            }, url, url);
        }
    }

    private class ViewHolder {
        ImageView iv_serverbg;
        TextView tv_main;
        TextView tv_sub;
    }
}
