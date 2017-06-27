package com.prj.sdk.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.prj.sdk.util.LogUtil;
import com.prj.sdk.util.Utils;

/**
 * 解压文件
 * 
 * @author LiaoBo
 * 
 */
public class ZipExtractorTask extends AsyncTask<Void, Integer, Long> {
	private final String			TAG			= "ZipExtractorTask";
	private final File				mInput;
	private final File				mOutput;
	private final ProgressDialog	mDialog;
	private int						mProgress	= 0;
	private boolean					mReplaceAll;
	private ZipExtractorCallback	mZipExtractorCallback;
	private int						mStatus;							// 0取消，1成功，2失败

	public ZipExtractorTask(Context context, String in, String out, boolean replaceAll, ZipExtractorCallback mZipExtractorCallback) {
		super();
		this.mZipExtractorCallback = mZipExtractorCallback;
		mInput = new File(in);
		mInput.deleteOnExit();// 虚拟机关闭删除文件
		if (out == null) {
			mOutput = new File(Utils.getFolderDir("resource"));
		} else {
			mOutput = new File(out);
		}
		if (!mOutput.exists()) {
			if (!mOutput.mkdirs()) {
				LogUtil.d(TAG, "Failed to make directories:" + mOutput.getAbsolutePath());
			}
		}
		if (context != null) {
			mDialog = new ProgressDialog(context);
		} else {
			mDialog = null;
		}
		mReplaceAll = replaceAll;
	}

	@Override
	protected Long doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return unzip();
	}

	@Override
	protected void onPostExecute(Long result) {
		// TODO Auto-generated method stub
		// super.onPostExecute(result);
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		if (isCancelled())
			return;
		if (mZipExtractorCallback != null)
			mZipExtractorCallback.unZip(mInput.toString(), mOutput.toString(), mStatus);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		// super.onPreExecute();
		if (mDialog != null) {
			mDialog.setMessage("正在解压，请稍候...");
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.setCancelable(false);
			mDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});
			mDialog.show();
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// super.onProgressUpdate(values);
		if (mDialog == null)
			return;
		if (values.length > 1) {
			int max = values[1];
			mDialog.setMax(max);
		} else
			mDialog.setProgress(values[0].intValue());
	}

	private long unzip() {
		long extractedSize = 0L;
		Enumeration<ZipEntry> entries;
		ZipFile zip = null;
		try {
			zip = new ZipFile(mInput);
			long uncompressedSize = getOriginalSize(zip);
			publishProgress(0, (int) uncompressedSize);

			entries = (Enumeration<ZipEntry>) zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory()) {
					continue;
				}
				File destination = new File(mOutput, entry.getName());
				if (!destination.getParentFile().exists()) {
					LogUtil.d(TAG, "make=" + destination.getParentFile().getAbsolutePath());
					destination.getParentFile().mkdirs();
				}
				if (destination.exists() && !mReplaceAll) {
					LogUtil.d(TAG, "file '" + mInput.getName() + "' already exits!!");
					return 0L;
				}
				ProgressReportingOutputStream outStream = new ProgressReportingOutputStream(destination);
				extractedSize += copy(zip.getInputStream(entry), outStream);
				outStream.close();
			}
			mStatus = 1;
		} catch (ZipException e) {
			mStatus = 2;
			e.printStackTrace();
		} catch (IOException e) {
			mStatus = 2;
			e.printStackTrace();
		} finally {
			try {
				zip.close();
			} catch (IOException e) {
				mStatus = 2;
				e.printStackTrace();
			}
		}
		return extractedSize;
	}

	private long getOriginalSize(ZipFile file) {
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) file.entries();
		long originalSize = 0l;
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (entry.getSize() >= 0) {
				originalSize += entry.getSize();
			}
		}
		return originalSize;
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
			mStatus = 2;
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				mStatus = 2;
				e.printStackTrace();
			}
		}
		return count;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		mStatus = 0;
		if (mZipExtractorCallback != null)
			mZipExtractorCallback.unZip(mInput.toString(), mOutput.toString(), mStatus);
	}

	private final class ProgressReportingOutputStream extends FileOutputStream {
		public ProgressReportingOutputStream(File file) throws FileNotFoundException {
			super(file);
			// TODO Auto-generated constructor stub
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