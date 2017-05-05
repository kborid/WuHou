package com.prj.sdk.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import com.prj.sdk.net.image.StreamUtil;

/**
 * 使用较小的内存，来构建缩略图
 *
 * 
 * @author LiaoBo
 */
@SuppressWarnings("unchecked")
public class ThumbnailUtil {

	public static final int getImageRotation(String filePath) {
		int rotation = 0;
		try {
			ExifInterface exif = new ExifInterface(filePath);
			int imageExifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			if (imageExifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
				rotation = 270;
			} else if (imageExifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
				rotation = 90;
			} else if (imageExifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
				rotation = 180;
			}
		} catch (Exception e) {

		}
		return rotation;
	}

	public static final Bitmap getImageRotation(String filePath, int rotation) {
		try {
			Bitmap bm = BitmapFactory.decodeFile(filePath);
			return getImageRotation(bm, rotation, null);
		} catch (Exception e) {
			// TODO: handle exception
		} catch (OutOfMemoryError e2) {

		}
		return null;
	}

	public static final Bitmap getImageRotation(Bitmap bm, int rotation) {
		return getImageRotation(bm, rotation, null);
	}

	public static final Bitmap getImageRotation(Bitmap bm, int rotation, Bitmap.Config mConfig) {
		try {
			if (rotation > 0) {
				Matrix mat = new Matrix();
				mat.postRotate(rotation);
				bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), mat, true);
				if (mConfig != null) {
					bm = bm.copy(mConfig, false);
				}
				return bm;
			} else {
				return bm;
			}
		} catch (Exception e) {

		} catch (OutOfMemoryError e2) {

		}
		return bm;
	}

	public static final Bitmap getImageThumbnail(Object obj, int mMaxWidth, int mMaxHeight){
		return getImageThumbnail(obj,mMaxWidth,mMaxHeight,false);
	}

	public static final Bitmap getImageThumbnail(Object obj, int mMaxWidth, int mMaxHeight, boolean scaleTarget) throws OutOfMemoryError {
		Bitmap bm = null;
		try {
			BitmapFactory.Options opts = getBitmapFactoryOptions(obj);
			int actualWidth = opts.outWidth;
			int actualHeight = opts.outHeight;
			
			int[] desiredWH = getDesiredWH(mMaxWidth, mMaxHeight, actualWidth, actualHeight);
			int desiredWidth = desiredWH[0];
			int desiredHeight = desiredWH[1];
			
			// Decode to the nearest power of two scaling factor.
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = getImageSampleSize(obj, mMaxWidth, mMaxHeight);
			
			Bitmap tempBitmap = null;
			if (obj instanceof String) {
				String filePath = obj.toString();
				tempBitmap = BitmapFactory.decodeFile(filePath, opts);
			} else if (obj instanceof byte[]) {
				byte[] data = (byte[]) obj;
				tempBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
				data = null;
			} else if (obj instanceof Bitmap) {
				Bitmap temp = (Bitmap) obj;
				tempBitmap = temp;
			}

			if(scaleTarget && tempBitmap != null && (tempBitmap.getWidth() != desiredWidth || tempBitmap.getHeight() != desiredHeight)) {
				bm = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
				tempBitmap.recycle();
			} else {
				if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {
					bm = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
					tempBitmap.recycle();
				} else {
					bm = tempBitmap;
				}
			}
		} catch (Exception e) {

		} catch (OutOfMemoryError e2) {
			throw e2;
		}
		return bm;
	}

	public static final BitmapFactory.Options getBitmapFactoryOptions(Object obj) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		opts.inPurgeable = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		if (obj instanceof String) {
			String filePath = obj.toString();
			BitmapFactory.decodeFile(filePath, opts);
		} else if (obj instanceof byte[]) {
			byte[] data = (byte[]) obj;
			BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		} else if (obj instanceof Bitmap) {
			Bitmap bm = (Bitmap) obj;
			opts.outWidth = bm.getWidth();
			opts.outHeight = bm.getHeight();
		}
		opts.inJustDecodeBounds = false;
		return opts;
	}

	/**
	 * 获取缩放比
	 *
	 * @param obj
	 * @param mMaxWidth
	 * @param mMaxHeight
	 * @return
	 */
	public static final int getImageSampleSize(Object obj, int mMaxWidth, int mMaxHeight) {
		int inSampleSize = 1;
		if (mMaxWidth == 0 && mMaxHeight == 0) {
			inSampleSize = 1;
		} else {
			BitmapFactory.Options opts = getBitmapFactoryOptions(obj);
			int actualWidth = opts.outWidth;
			int actualHeight = opts.outHeight;

			int[] desiredWH = getDesiredWH(mMaxWidth, mMaxHeight, actualWidth, actualHeight);
			int desiredWidth = desiredWH[0];
			int desiredHeight = desiredWH[1];

			inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
		}
		return inSampleSize;
	}

	private static int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
		double wr = (double) actualWidth / desiredWidth;
		double hr = (double) actualHeight / desiredHeight;
		double ratio = Math.min(wr, hr);
		float n = 1.0f;
		while ((n * 2) <= ratio) {
			n *= 2;
		}

		return (int) n;
	}

	/**
	 * 获取缩略图二进制数组
	 *
	 * @param data
	 * @param width
	 * @param height
	 * @return
	 */
	public static final byte[] getImageThumbnailBytes(byte[] data, int width, int height, int quality) {
		try {
			Bitmap bm = getImageThumbnail(data, width, height, false);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			byte[] bmBytes = baos.toByteArray();
			baos.close();
			bm.recycle();
			return bmBytes;
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError oom) {
		}
		return data;
	}

	public static final byte[] getImageThumbnailBytes(Bitmap bm, int quality) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			byte[] bmBytes = baos.toByteArray();
			baos.flush();
			baos.close();
			bm.recycle();
			return bmBytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap getRoundImage(Bitmap bitmap, int round) {
		Bitmap output = null;
		try {
			output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			final float roundPx = round;

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);

			return output;
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}

		return output;
	}

	public static Bitmap getRoundImage(Bitmap bitmap) {
		try {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			float roundPx;
			float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
			if (width <= height) {
				roundPx = width / 2;
				top = 0;
				bottom = width;
				left = 0;
				right = width;
				height = width;
				dst_left = 0;
				dst_top = 0;
				dst_right = width;
				dst_bottom = width;
			} else {
				roundPx = height / 2;
				float clip = (width - height) / 2;
				left = clip;
				right = width - clip;
				top = 0;
				bottom = height;
				width = height;
				dst_left = 0;
				dst_top = 0;
				dst_right = height;
				dst_bottom = height;
			}

			Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
			final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
			final RectF rectF = new RectF(dst);

			paint.setAntiAlias(true);

			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, src, dst, paint);
			return output;
		} catch (Exception e) {
			// TODO: handle exception
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static int[] getDesiredWH(int mMaxWidth, int mMaxHeight, int actualWidth, int actualHeight) {
		int[] maxWH = new int[2];
		if (mMaxWidth == 0 && mMaxHeight == 0) {
			mMaxWidth = actualWidth;
			mMaxHeight = actualHeight;
		} else if (mMaxWidth == 0 && mMaxHeight != 0) {
			mMaxWidth = (int)(mMaxHeight * actualWidth / (float)actualHeight);
		} else if (mMaxWidth != 0 && mMaxHeight == 0) {
			mMaxHeight = (int)(mMaxWidth * actualHeight / (float)actualWidth);
		} else {
			float scaleX = (float) mMaxWidth / actualWidth;
			float scaleY = (float) mMaxHeight / actualHeight;
			if(scaleX >= scaleY) {
				mMaxWidth = (int) (scaleY * actualWidth);
				mMaxHeight = (int) (scaleY * actualHeight);
			} else {
				mMaxWidth = (int) (scaleX * actualWidth);
				mMaxHeight = (int) (scaleX * actualHeight);
			}
		}

		maxWH[0] = mMaxWidth;
		maxWH[1] = mMaxHeight;
		return maxWH;
	}

	/**
	 * 文件转换
	 *
	 * @param fromFile
	 * @param toFile
	 * @param width
	 * @param height
	 * @param quality
	 * @return
	 */
	public static boolean transImage(String fromFile, String toFile, int width, int height, int quality) {
		boolean flag = true;
		try {
			int rotation = ThumbnailUtil.getImageRotation(fromFile);
			if (rotation == 90 || rotation == 270) {
				width = width ^ height;
				height = width ^ height;
				width = width ^ height;
			}

			Bitmap bm = null;
			// 原始文件缩放
			byte[] data = StreamUtil.convertToBytes(fromFile);
			bm = ThumbnailUtil.getImageThumbnail(data, width, height, false);
			if (rotation > 0) {
				bm = ThumbnailUtil.getImageRotation(bm, rotation);
			}
			flag = StreamUtil.saveBitmap(toFile, bm, quality);
			if (bm != null && !bm.isRecycled()) {
				bm.recycle();
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = StreamUtil.copyFile(fromFile, toFile);
		} catch (OutOfMemoryError oom) {
		}
		return flag;
	}
	public static String compressImage(String path, String newPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int inSampleSize = 1;
        int maxSize = 2000;
        if (options.outWidth > maxSize || options.outHeight > maxSize) {
          int widthScale = (int) Math.ceil(options.outWidth * 1.0 / maxSize);
          int heightScale = (int) Math.ceil(options.outHeight * 1.0 / maxSize);
          inSampleSize = Math.max(widthScale, heightScale);
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int newW = w;
        int newH = h;
        if (w > maxSize || h > maxSize) {
          if (w > h) {
            newW = maxSize;
            newH = (int) (newW * h * 1.0 / w);
          } else {
            newH = maxSize;
            newW = (int) (newH * w * 1.0 / h);
          }
        }
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, newW, newH, false);
        FileOutputStream outputStream = null;
        try {
          outputStream = new FileOutputStream(newPath);
          newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } finally {
          try {
            if (outputStream != null) {
              outputStream.close();
            }
          } catch (IOException e) {
            e.printStackTrace();
          }

        }
        recycle(newBitmap);
        recycle(bitmap);
        return newPath;
      }

      public static void recycle(Bitmap bitmap) {
        // 先判断是否已经回收
        if (bitmap != null && !bitmap.isRecycled()) {
          // 回收并且置为null
          bitmap.recycle();
        }
        System.gc();
      }

  	public static String getPicPath(Context context, Uri selectedImage) {
		String picturePath = "";
		Cursor cursor = null;
		try {
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			picturePath = cursor.getString(columnIndex);
			// cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (cursor != null) {
					cursor.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return picturePath;
	}


}
