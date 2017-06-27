package com.yunfei.wh.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.prj.sdk.util.Utils;
import com.yunfei.wh.R;

/**
 * 1:解决嵌套，重写ListView与GridView，让其失去滑动特性
 * 2:重写dispatchDraw方法，重绘child view，利用Paint进行绘制网格线
 */
public class MyGridViewWidget extends GridView {
    private int rowsCount;

    public MyGridViewWidget(Context context) {
        super(context);
    }

    public MyGridViewWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GridViewWidgetRows);
        rowsCount = array.getInt(R.styleable.GridViewWidgetRows_rowscount, 3);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        Paint localPaint;
        localPaint = new Paint();
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setStrokeWidth(1);
        localPaint.setColor(getResources().getColor(R.color.grid_item_line));


        int childCount = getChildCount();

        if (rowsCount == 2) {
            for (int i = 0; i < childCount; i++) {
                View cellView = getChildAt(i);
                // 画竖线
                if (i % 3 != 0) {
                    // 从上到下 画 左边边框
                    int padValue = Utils.dip2px(20);
                    canvas.drawLine(cellView.getLeft(), cellView.getTop() + padValue, cellView.getLeft(), cellView.getBottom() - padValue * 2, localPaint);
                }

                // 画横线
                if ((i - 1) / rowsCount >= 1) {
                    // 从左到右 画 上边边框
                    canvas.drawLine(cellView.getLeft(), cellView.getTop(), cellView.getRight(), cellView.getTop(), localPaint);
                }
            }
        } else {
            if (childCount <= 0) {
                return;
            }
            int column = getWidth() / getChildAt(0).getWidth();
            for (int i = 0; i < childCount; i++) {
                View cellView = getChildAt(i);
                // 画竖线
                if (i % 3 != 0) {
                    // 从上到下 画 左边边框
                    canvas.drawLine(cellView.getLeft(), cellView.getTop(), cellView.getLeft(), cellView.getBottom(), localPaint);
                }

                // 画横线
                if (i / column >= 1) {
                    // 从左到右 画 上边边框
                    canvas.drawLine(cellView.getLeft(), cellView.getTop(), cellView.getRight(), cellView.getTop(), localPaint);
                }
            }

            // 补齐不满一行的边框绘制
            int spaceCount = column - childCount % column;
            int realRows = 0;
            if (childCount % column == 0) {
                realRows = childCount / column;
            } else {
                realRows = childCount / column + 1;
            }
            View lastView = getChildAt(childCount - 1);
            for (int i = 0; i < spaceCount; i++) {
                if (realRows > 1) {
                    // 绘制最后一行空缺单元的上边框
                    canvas.drawLine(lastView.getRight() + lastView.getWidth() * i, lastView.getTop(), lastView.getRight() + lastView.getWidth() * (i + 1), lastView.getTop(), localPaint);
                }
            }
            // 绘制最后一个view的右边框
            canvas.drawLine(lastView.getRight(), lastView.getTop(), lastView.getRight(), lastView.getBottom(), localPaint);
        }
    }
}