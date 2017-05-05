package com.prj.sdk.net.down;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.NetworkUtil;
import com.prj.sdk.util.Utils;

/**
 * 异步下载任务
 * 
 * @author LiaoBo
 * 
 */
public class DownLoaderTask extends AsyncTask<Void, Integer, Long> {
	private final String					TAG			= "DownLoaderTask";
	private URL								mUrl;
	private File							mFile;
	private ProgressDialog					mDialog;
	private int								mProgress	= 0;
	private ProgressReportingOutputStream	mOutputStream;
	private boolean							mReplaceAll;
	private DownCallback					mDownCallback;
	private int								mDownStatus;					// 下载状态： 0取消,1成功，2失败
	private String							fileName;
	private Context							mContext;
	private final int						TIMEOUT		= 10000;			// 设置连接下载超时

	public DownLoaderTask(Context context, String url, String fileName, boolean replaceAll, DownCallback mDownCallback) {
		super();
		this.mReplaceAll = replaceAll;
		this.mDownCallback = mDownCallback;
		this.fileName = fileName;
		this.mContext = context;
		try {
			mUrl = new URL(url);
			if (fileName == null) {
				fileName = new File(mUrl.getFile()).getName();
			}
			mFile = new File(Utils.getFolderDir("zip"), fileName);
			LogUtil.d(TAG, "out=" + Utils.getFolderDir("zip") + ", name=" + fileName + ",mUrl.getFile()=" + mUrl.getFile());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPreExecute() {
		// super.onPreExecute();

		if (mDialog == null) {
			mDialog = new ProgressDialog(mContext);
			// mDialog.setTitle("正在下载...");
			// mDialog.setMessage(mFile.getName());
			mDialog.setMessage("正在加载，请稍候...");
			// mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.setCancelable(false);
			mDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});
			// mDialog.setCancelable(false);
			mDialog.show();
		}
	}

	@Override
	protected Long doInBackground(Void... params) {
		return download();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// super.onProgressUpdate(values);
		if (mDialog == null)
			return;
		if (values.length > 1) {
			int contentLength = values[1];
			if (contentLength == -1) {
				mDialog.setIndeterminate(true);
			} else {
				mDialog.setMax(contentLength);
			}
		} else {
			mDialog.setProgress(values[0].intValue());
		}
	}

	@Override
	protected void onPostExecute(Long result) {
		// super.onPostExecute(result);
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		if (isCancelled())
			return;
		if (mDownCallback != null)
			mDownCallback.down(mUrl.getFile(), mFile.toString(), mDownStatus, fileName);

	}

	private long download() {
		URLConnection connection = null;
		int bytesCopied = 0;
		try {
			if (NetworkUtil.isNetworkAvailable()) {
				connection = mUrl.openConnection();
				connection.setConnectTimeout(TIMEOUT);
				connection.setReadTimeout(TIMEOUT);
				int length = connection.getContentLength();
				if (!mReplaceAll && mFile.exists() && length == mFile.length()) {
					LogUtil.d(TAG, "file '" + mFile.getName() + "' already exits!!");
					return 0l;
				}
				mOutputStream = new ProgressReportingOutputStream(mFile);
				publishProgress(0, length);// 进度条每次更新,执行中创建新线程处理onProgressUpdate()
				bytesCopied = copy(connection.getInputStream(), mOutputStream);
				if (bytesCopied != length && length != -1) {
					LogUtil.e(TAG, "Download incomplete bytesCopied=" + bytesCopied + ", length" + length);
				}
				mOutputStream.close();
				mDownStatus = 1;
			} else {
				throw new ConnectException("网络连接失败，请检查网络");
			}
		} catch (IOException e) {
			mDownStatus = 2;
			e.printStackTrace();
		}
		return bytesCopied;
	}

	private int copy(InputStream input, OutputStream output) {
		byte[] buffer = new byte[1024 * 8];
		BufferedInputStream in = new BufferedInputStream(input, 1024 * 8);
		BufferedOutputStream out = new BufferedOutputStream(output, 1024 * 8);
		int count = 0, n = 0;
		try {
			while ((n = in.read(buffer, 0, 1024 * 8)) != -1) {
				out.write(buffer, 0, n);
				count += n;
			}
			out.flush();
		} catch (IOException e) {
			mDownStatus = 2;
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				mDownStatus = 2;
				e.printStackTrace();
			}
		}
		return count;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		mDownStatus = 0;
		if (mDownCallback != null)
			mDownCallback.down(mUrl.getFile(), mFile.toString(), mDownStatus, fileName);
	}

	private final class ProgressReportingOutputStream extends FileOutputStream {
		public ProgressReportingOutputStream(File file) throws FileNotFoundException {
			super(file);
		}

		@Override
		public void write(byte[] buffer, int byteOffset, int byteCount) throws IOException {
			// TODO Auto-generated method stub
			super.write(buffer, byteOffset, byteCount);
			mProgress += byteCount;
			publishProgress(mProgress);
		}

	}

}