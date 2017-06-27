package com.yunfei.wh.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prj.sdk.net.image.ImageLoader;
import com.prj.sdk.util.DisplayUtil;
import com.prj.sdk.util.StringUtil;
import com.yunfei.wh.R;
import com.yunfei.wh.ui.custom.CircleImageView;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.net.bean.ContentListBean;
import com.yunfei.wh.tools.HtmlParse;
import com.yunfei.wh.control.UnescapeControl;
import com.yunfei.wh.ui.activity.ImageScaleActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kborid on 2016/5/31.
 */
public class DiscoveryListAdapter extends BaseAdapter {
    private Context context;
    private List<ContentListBean> list;

    private DiscoveryPictureAdapter picAdapter;
    private HashMap<String, ArrayList<String>> imageMap = new HashMap<>();

    public DiscoveryListAdapter(Context context, List<ContentListBean> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.discovery_new_item, null);
            viewHolder.iv_photo = (CircleImageView) convertView.findViewById(R.id.iv_photo);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolder.gridview = (GridView) convertView.findViewById(R.id.gridview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ContentListBean bean = list.get(position);
        // 获取频道头像
        if (!StringUtil.isEmpty(bean.imgPath)) {
            String url = NetURL.getApi() + bean.imgPath;
            if (url != null && url.length() > 0) {
                ImageLoader.getInstance().loadBitmap(new ImageLoader.ImageCallback() {
                    @Override
                    public void imageCallback(Bitmap bm, String url, String imageTag) {
                        if (bm != null) {
                            viewHolder.iv_photo.setImageBitmap(bm);
                        }
                    }
                }, url);
            }
        }
        viewHolder.tv_title.setText(bean.title);
        viewHolder.tv_time.setText(bean.createDateApp);

        // 富文本解析
        String contentStr = UnescapeControl.unescape(bean.contents);

        // 文本内容
        String tvString = HtmlParse.trimHtml2Txt(contentStr);
        viewHolder.tv_content.setText(tvString);
        ArrayList<String> imgList = HtmlParse.getImgList(contentStr);
        for (int i = 9; i < imgList.size(); i++) {
            imgList.remove(i);
        }

        imageMap.put(String.valueOf(position), imgList);
        if (imgList != null && imgList.size() != 0) {
            int size = imgList.size();
            viewHolder.gridview.setVisibility(View.VISIBLE);
            if (size == 2 || size == 4) {
                viewHolder.gridview.setNumColumns(2);
                viewHolder.gridview.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.dip2px(150), LinearLayout.LayoutParams.WRAP_CONTENT));
            } else if (size == 1) {
                viewHolder.gridview.setNumColumns(1);
                viewHolder.gridview.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.dip2px(150), LinearLayout.LayoutParams.WRAP_CONTENT));
            } else {
                viewHolder.gridview.setNumColumns(3);
                viewHolder.gridview.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtil.dip2px(230), LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            picAdapter = new DiscoveryPictureAdapter(context, imgList);
            viewHolder.gridview.setAdapter(picAdapter);

            viewHolder.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int gridViewIndex, long id) {
                    Intent intent = new Intent(context, ImageScaleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("INDEX", (int) parent.getAdapter().getItemId(gridViewIndex));
                    bundle.putStringArrayList("URL", imageMap.get(String.valueOf(position)));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        } else {
            viewHolder.gridview.setVisibility(View.GONE);
        }

        return convertView;
    }

    private class ViewHolder {
        CircleImageView iv_photo;
        TextView tv_title;
        TextView tv_time;
        TextView tv_content;
        GridView gridview;
    }
}
