package net.aquaries.androidstudiotest.Utilities;

import java.util.ArrayList;

/**
 * Created by Bell on 3/21/2016.
 * Utilities related to mathematics.
 */
public abstract class MathUtil {
	/**
	 * Returns the measure in radians of the supplied degree angle.
	 * @param rad An angle in degrees.
	 * @return The radian measure of the angle.
	 */
	public static float toRadians(double rad) {
		return (float) Math.toRadians(rad);
	}

	/**
	 * Returns the measure in degrees of the supplied radian angle.
	 * @param rad An angle in radians.
	 * @return The degree measure of the angle.
	 */
	public static float toDegrees(double rad) {
		return (float) Math.toDegrees(rad);
	}

	/**
	 * Returns the sine of the argument in degrees.
	 * @param deg The angle whose sine has to be computed, in degrees.
	 * @return The sine of the argument.
	 */
	public static float sin(double deg) {
		return (float) Math.sin(toRadians(deg));
	}

	/**
	 * Returns the cosine of the argument in degrees.
	 * @param deg The angle whose cosine has to be computed, in degrees.
	 * @return The cosine of the argument.
	 */
	public static float cos(double deg) {
		return (float) Math.cos(toRadians(deg));
	}

	/**
	 * Returns the tangent of the argument in degrees.
	 * @param deg The angle whose tangent has to be computed, in degrees.
	 * @return The tangent of the argument.
	 */
	public static float tan(double deg) {
		return (float) Math.tan(toRadians(deg));
	}

	/**
	 * Returns the arctan2 of the arguments.
	 * @param y The numerator of the value whose atan has to be computed.
	 * @param x The denominator of the value whose atan has to be computed.
	 * @return The arc tangent of {@code y/x}.
	 */
	public static float atan2(double y, double x) {
		return (float) toDegrees(Math.atan2(y, x));
	}

	public static class AverageAngle {
		private int mLength, mStart = 0, mSize = 0;
		private float[] mData;
		private float mSumSin, mSumCos, mRet;

		/**
		 * @param length n
		 */
		public AverageAngle(int length) {
			mLength = length;
			mData = new float[length];
		}

		/**
		 * Supply a number
		 * @param data The number supplied
		 */
		public void put(float data) {
			if (mSize == mLength) remove();
			insert(data);
			update();
		}

		/**
		 * @return The average over the latest n numbers supplied.
		 */
		public float get() {
			if (mSize == 0)
				return Float.NaN;
			return mRet;
		}

		protected void update() {
			mRet = atan2(mSumSin, mSumCos);
		}

		protected void insert(float data) {
			mSumSin += sin(data); mSumCos += cos(data);
			mData[(mStart + mSize) % mLength] = data;
			mSize++;
		}

		protected void remove() {
			float data = mData[mStart];
			mSumSin -= sin(data); mSumCos -= cos(data);
			mStart = (mStart + 1) % mLength;
			mSize--;
		}
	}
}
