package com.yunfei.wh.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.yunfei.wh.R;
import com.yunfei.wh.ui.activity.SchoolMapDetailActivity;

/**
 * @author kborid
 * @date 2016/11/4 0004
 */
public class PinInfoWindowAdapter implements AMap.InfoWindowAdapter {

    private Context context;

    public PinInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        View infoWindow = LayoutInflater.from(context).inflate(R.layout.comm_pininfo_layout, null);

        LinearLayout pin_lay = (LinearLayout) infoWindow.findViewById(R.id.pin_lay);
        TextView tv_title = (TextView) infoWindow.findViewById(R.id.tv_title);
        TextView tv_detail = (TextView) infoWindow.findViewById(R.id.tv_detail);
        TextView tv_address = (TextView) infoWindow.findViewById(R.id.tv_address);
        tv_title.setText(marker.getTitle());
        tv_address.setText(marker.getSnippet());

        /*tv_detail*/pin_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SchoolMapDetailActivity.class);
                intent.putExtra("name", marker.getTitle());
                intent.putExtra("id", String.valueOf(marker.getPeriod()));
                context.startActivity(intent);
            }
        });

        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
