package com.prj.sdk.util;

/**
 * 分治法求数组的最小最大值
 * 
 * @author LiaoBo
 * @body 2014-4-21
 */
public class DivideAndConquer {

	/**
	 * @param array
	 *            数组
	 * @param begin
	 *            数组开始比较元素下标
	 * @param end
	 *            数据结束比较元素下标
	 * @return
	 */
	public static MinMax getMinMax(int[] array, int begin, int end) {

		if (end - begin <= 1) {

			if (array[begin] > array[end]) {

				return new MinMax(array[end], array[begin]);

			} else {

				return new MinMax(array[begin], array[end]);
			}
		} else {

			int mid = (begin + end) / 2;

			MinMax left = getMinMax(array, begin, mid);
			MinMax right = getMinMax(array, mid, end);

			double min = 0, max = 0;

			min = left.getMin() > right.getMin() ? right.getMin() : left.getMin();
			max = left.getMax() > right.getMax() ? left.getMax() : right.getMax();

			return new MinMax(min, max);
		}

	}

	public static class MinMax {

		public MinMax(double min, double max) {

			mMin = min;
			mMax = max;
		}

		public double getMin() {

			return mMin;
		}

		public void setMin(double value) {

			mMin = value;
		}

		public double getMax() {

			return mMax;
		}

		public void setMax(double value) {

			mMax = value;
		}

		private double	mMin;
		private double	mMax;
	}
}
