package com.yunfei.wh.ui.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.prj.sdk.util.ThumbnailUtil;
import com.yunfei.wh.R;

/**
 * 添加照片适配器
 * 
 * @author LiaoBo
 */
public class AddPhotoGrideViewAdapter extends BaseAdapter {
	private Context			mContext;
	private LayoutInflater	inflater;
	private List<Uri>		mBeans;
	private final int		MAX	= 9;

	public AddPhotoGrideViewAdapter(Context context, List<Uri> list) {
		this.mBeans = list;
		this.mContext = context;
		this.inflater = LayoutInflater.from(mContext);
	}

	public int getCount() {
		if (mBeans == null || mBeans.size() < 1) {
			return 1;
		} else if (mBeans.size() < MAX) {
			return mBeans.size() + 1;
		} else {
			return MAX;
		}
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
		private ImageView	iv_photo;// iv_delete;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null || true) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.gv_add_photo, null);
			holder.iv_photo = (ImageView) convertView.findViewById(R.id.iv_photo);
//			holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
//		if (position == getCount() - 1 && mBeans.size() !=  MAX) {
//			holder.iv_delete.setVisibility(View.GONE);
//		} else {
//			holder.iv_delete.setVisibility(View.VISIBLE);
//		}

		try {
			if (position == getCount() - 1 && mBeans.size() !=  MAX) {
				holder.iv_photo.setImageResource(R.drawable.add_img);
			} else {
				final Uri temp = mBeans.get(position);
				Bitmap bm = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), temp);
				holder.iv_photo.setImageBitmap(ThumbnailUtil.getImageThumbnail(bm, 80, 80));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

//		holder.iv_delete.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				mBeans.remove(position);
//				notifyDataSetChanged();
//			}
//		});

		return convertView;
	}
}
