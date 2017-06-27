package com.prj.sdk.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * bitmap处理工具类
 * 
 * @author LiaoBo
 */
public class BitmapUtils {
	/**
	 * 图片灰化处理:ColorMatrix类实现图像处理软件中的滤镜效果，通过ColorMatrix类可以对位图中的每个像素进行变换处理，达到特殊的滤镜效果
	 * 
	 * @param mBitmap
	 * @return
	 */
	public static Bitmap getGrayBitmap(Bitmap mBitmap) {
		Bitmap mGrayBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Config.ARGB_8888);
		Canvas mCanvas = new Canvas(mGrayBitmap);
		Paint mPaint = new Paint();

		// 创建颜色变换矩阵
		ColorMatrix mColorMatrix = new ColorMatrix();
		// 设置灰度影响范围
		mColorMatrix.setSaturation(0);
		// 创建颜色过滤矩阵
		ColorMatrixColorFilter mColorFilter = new ColorMatrixColorFilter(mColorMatrix);
		// 设置画笔的颜色过滤矩阵
		mPaint.setColorFilter(mColorFilter);
		// 使用处理后的画笔绘制图像
		mCanvas.drawBitmap(mBitmap, 0, 0, mPaint);

		return mGrayBitmap;
	}

	public static Bitmap getAlphaBitmap(Drawable drawable, int color) {
		BitmapDrawable mBitmapDrawable = (BitmapDrawable) drawable;
		return getAlphaBitmap(mBitmapDrawable.getBitmap(), color);
	}

	/**
	 * 提取图像Alpha位图 :extractAlpha()方法，可以把位图中的Alpha部分提取出来作为一个新的位图，然后与填充颜色后的Paint结合重新绘制一个新图像
	 * 
	 * @param mBitmap
	 * @param color
	 *            提取为指定颜色
	 * @return
	 */
	public static Bitmap getAlphaBitmap(Bitmap mBitmap, int color) {
		Bitmap mAlphaBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Config.ARGB_8888);

		Canvas mCanvas = new Canvas(mAlphaBitmap);
		Paint mPaint = new Paint();

		mPaint.setColor(color);
		// 从原位图中提取只包含alpha的位图
		Bitmap alphaBitmap = mBitmap.extractAlpha();
		// 在画布上（mAlphaBitmap）绘制alpha位图
		mCanvas.drawBitmap(alphaBitmap, 0, 0, mPaint);

		return mAlphaBitmap;
	}

	/**
	 * 图像倒影:使用Matrix类可以很容易实现图像的倒影效果。主要是Matrix的preScale方法的使用，给它设置负数缩放比例，图像就会进行反转,设置Shader添加渐变效果
	 * 
	 * @return
	 */
	public static Bitmap getReflectedBitmap(Bitmap mBitmap) {
		int width = mBitmap.getWidth();
		int height = mBitmap.getHeight();

		Matrix matrix = new Matrix();
		// 图片缩放，x轴变为原来的1倍，y轴为-1倍,实现图片的反转
		matrix.preScale(1, -1);

		// 创建反转后的图片Bitmap对象，图片高是原图的一半。
		// Bitmap mInverseBitmap = Bitmap.createBitmap(mBitmap, 0, height/2, width, height/2, matrix, false);
		// 创建标准的Bitmap对象，宽和原图一致，高是原图的1.5倍。
		// 注意两种createBitmap的不同
		// Bitmap mReflectedBitmap = Bitmap.createBitmap(width, height*3/2, Config.ARGB_8888);

		Bitmap mInverseBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
		Bitmap mReflectedBitmap = Bitmap.createBitmap(width, height * 2, Config.ARGB_8888);

		// 把新建的位图作为画板
		Canvas mCanvas = new Canvas(mReflectedBitmap);
		// 绘制图片
		mCanvas.drawBitmap(mBitmap, 0, 0, null);
		mCanvas.drawBitmap(mInverseBitmap, 0, height, null);

		// 添加倒影的渐变效果
		Paint mPaint = new Paint();
		Shader mShader = new LinearGradient(0, height, 0, mReflectedBitmap.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.MIRROR);
		mPaint.setShader(mShader);
		// 设置叠加模式
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		// 绘制遮罩效果
		mCanvas.drawRect(0, height, width, mReflectedBitmap.getHeight(), mPaint);

		return mReflectedBitmap;
	}
}
