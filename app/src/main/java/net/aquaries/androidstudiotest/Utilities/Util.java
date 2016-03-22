package net.aquaries.androidstudiotest.Utilities;

/**
 * Created by Bell on 3/21/2016.
 * Basic utilities.
 */
public abstract class Util {
	protected static final boolean DEBUGGING = true;

	/**
	 * @return If current build is for debugging
	 */
	public static boolean debug() {
		return DEBUGGING;
	}

	/**
	 * Prints a string to the target stream.
	 * @param str The string to print to the target stream.
	 */
	public static void print(String str) {
		System.out.print(str);
	}

	/**
	 * Prints a string followed by a newline.
	 * @param str The string to print to the target stream.
	 */
	public static void println(String str) {
		System.out.println(str);
	}

	/**
	 * Prints a formatted string.
	 * @param format The format string
	 * @param args The list of arguments passed to the formatter
	 */
	public static void printf(String format, Object... args) {
		System.out.printf(format, args);
	}

	/**
	 * Returns an arbitrary clock measure in milliseconds for timeouts, etc.
	 * @return
	 */
	public static long clock() {
		return System.nanoTime() / 1000000;
	}

	/**
	 * A generic callback
	 * @param <T> Argument type of the callback function
	 */
	public interface CallBack<T> {
		/**
		 * @param arg The argument of the callback function
		 */
		void onCall(T arg);
	}

	/**
	 * A callback with a minimum calling interval
	 * @param <T> Argument type of the callback function
	 */
	public static class IntervalCallBack<T> {
		CallBack<T> mCallBack;
		long mInterval, mLast;

		/**
		 * @param callBack A generic callback
		 * @param interval A minimum calling interval in milliseconds
		 */
		public IntervalCallBack(CallBack<T> callBack, long interval) {
			mCallBack = callBack;
			mInterval = interval;
			mLast = 0;
		}

		/**
		 * @return If the minimum calling interval has elapsed
		 */
		public boolean canCall() {
			return (clock() - mLast) >= mInterval;
		}

		/**
		 * Must check canCall() first
		 * @param arg The argument of the callback function
		 */
		public void onCall(T arg) {
			mLast = clock();
			mCallBack.onCall(arg);
		}
	}
}
