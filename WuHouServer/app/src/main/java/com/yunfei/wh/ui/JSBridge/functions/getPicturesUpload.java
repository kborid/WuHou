package com.yunfei.wh.ui.JSBridge.functions;

import java.io.FileNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.prj.sdk.constants.InfoType;
import com.prj.sdk.net.bean.ResponseData;
import com.prj.sdk.net.data.DataCallback;
import com.prj.sdk.net.data.DataLoader;
import com.prj.sdk.util.Base64;
import com.prj.sdk.util.ThumbnailUtil;
import com.prj.sdk.widget.CustomToast;
import com.yunfei.wh.common.AppConst;
import com.yunfei.wh.common.NetURL;
import com.yunfei.wh.ui.activity.HtmlActivity;
import com.yunfei.wh.ui.dialog.GetPicDialog;
import com.yunfei.wh.ui.dialog.ProgressDialog;
import com.yunfei.wh.ui.JSBridge.WVJBWebViewClient;

/**
 * 获取图片文件并上传
 * 
 * @author LiaoBo
 */
public class getPicturesUpload implements HtmlActivity.ActivityResult, WVJBWebViewClient.WVJBHandler, DataCallback {
	private WVJBWebViewClient.WVJBResponseCallback mCallback;
	private Context					mContext;
	private Uri						mCameraUri;		// 相机模式的图片uri
	private ProgressDialog mProgressDialog;
	/**
	 * 构造函数，获取上下文
	 * 
	 * @param context
	 */
	public getPicturesUpload(Context context) {
		mContext = context;
	}

	@Override
	public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
		if (callback == null)
			return;
		mCallback = callback;
		((HtmlActivity) mContext).setActivityForResult(this);// 设置图片数据回调
		mProgressDialog = new ProgressDialog(mContext);
		setPicture();
	}

	/**
	 * 获取图片
	 * 
	 */
	public void setPicture() {
		GetPicDialog picDialog = new GetPicDialog(((HtmlActivity) mContext));
		mCameraUri = picDialog.getPicPathUri();
		picDialog.showDialog();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
			case AppConst.ACTIVITY_GET_IMAGE :// 图库
				Uri sdcardTempUri = data.getData();
				if (sdcardTempUri != null) {
					ContentResolver resolver = mContext.getContentResolver();
					try {
						Bitmap mBitmap = MediaStore.Images.Media.getBitmap(resolver, sdcardTempUri);
						uploadImg(mBitmap);
						mBitmap.recycle();
						mBitmap = null;
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					CustomToast.show("获取图片失败", 0);
				}
				break;
			case AppConst.ACTIVITY_IMAGE_CAPTURE :// 相机
				ContentResolver resolver = mContext.getContentResolver();
				try {
					Bitmap mBitmap = MediaStore.Images.Media.getBitmap(resolver, mCameraUri);
					uploadImg(mBitmap);
					mBitmap.recycle();
					mBitmap = null;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;

			default :
				break;
		}
	}

	/**
	 * 上传图片
	 * 
	 * @param bm
	 * @throws RuntimeException
	 * @throws JSONException
	 */
	private void uploadImg(Bitmap bm) throws JSONException, RuntimeException {
		ResponseData data = new ResponseData();
		data.path = NetURL.UPLOAD;// "http://192.168.1.118:8080/img/base64upload";//
		data.isLocal = true;
		data.type = InfoType.POST_REQUEST.toString();
		JSONObject mJson = new JSONObject();
		mJson.put("data", Base64.encodeToString(ThumbnailUtil.getImageThumbnailBytes(ThumbnailUtil.getImageThumbnail(bm, 480, 800), 85)));
		mJson.put("dirName", "");
		data.data = mJson.toString();
		data.flag = 1;

		DataLoader.getInstance().loadData(this, data);
	}
	@Override
	public void preExecute(ResponseData request) {
//		mProgressDialog.setMessage("正在上传图片，请稍候...");
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	@Override
	public void notifyMessage(ResponseData request, ResponseData response) throws Exception {
		mProgressDialog.dismiss();
		JSONObject mJson = new JSONObject();
		mJson.put("imageUrl", response.data.toString());
		mCallback.callback(mJson.toString());
	}

	@Override
	public void notifyError(ResponseData request, ResponseData response, Exception e) {
		mProgressDialog.dismiss();
		CustomToast.show("图片上传失败，请重试", 0);
	}

}