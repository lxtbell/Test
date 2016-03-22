package net.aquaries.androidstudiotest.Device;

import android.app.Activity;
import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import net.aquaries.androidstudiotest.Utilities.MathUtil;
import net.aquaries.androidstudiotest.Utilities.Util;

/**
 * Created by Bell on 3/21/2016.
 * Provide an Orientation object when device orientation is updated
 */
public class OrientationProvider implements SensorEventListener {
	/**
	 * azimuth The angle between TRUE north and
	 * TODO Specify
	 */
	public static class Orientation {
		public float azimuth, pitch, roll;

		public Orientation() {
			reset();
		}

		public void set(float _azimuth, float _pitch, float _roll) {
			azimuth = _azimuth;
			pitch = _pitch;
			roll = _roll;
		}

		public void reset() {
			azimuth = Float.NaN;
		}

		public boolean invalid() {
			return Float.isNaN(azimuth);
		}

		public Orientation clone() {
			return new Orientation(this);
		}

		protected Orientation(Orientation copy) {
			set(copy.azimuth, copy.pitch, copy.roll);
		}
	}

	public static interface CallBack extends Util.CallBack<Orientation> {} // Typedef

	protected final Activity mContext;
	protected final SensorManager mSensorManager;
	protected final Sensor mSensorAccelerometer;
	protected final Sensor mSensorMagneticField;
	protected final LocationProvider mLocation;

	protected MathUtil.AverageAngle mAzimuth, mPitch, mRoll;

	protected float[] mValuesAccelerometer = new float[3];
	protected float[] mValuesMagneticField = new float[3];
	protected float[] mMatrixR = new float[9];
	protected float[] mMatrixI = new float[9];
	protected float[] mMatrixValues = new float[3];

	protected Util.IntervalCallBack<Orientation> mCallBack;
	protected Orientation ret = null;

	/**
	 * @param context Current Activity (e.g. MainActivity.this)
	 */
	public OrientationProvider(Activity context) {
		this(context, 5);
	}

	/**
	 * @param context Current Activity (e.g. MainActivity.this)
	 * @param smoothing the number of measurements used to calculate a mean for the orientation values
	 */
	public OrientationProvider(Activity context, int smoothing) {
		mContext = context;
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		mLocation = new LocationProvider(context);
		mAzimuth = new MathUtil.AverageAngle(smoothing);
		mPitch = new MathUtil.AverageAngle(smoothing);
		mRoll = new MathUtil.AverageAngle(smoothing);
	}

	/**
	 * Start orientation updating
	 * Must be called in Activity.onResume()
	 */
	public void onResume() {
		mLocation.onResume();
		mSensorManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, mSensorMagneticField, SensorManager.SENSOR_DELAY_UI);
	}

	/**
	 * Stop orientation updating
	 * Must be called in Activity.onPause()
	 */
	public void onPause() {
		mLocation.onPause();
		mSensorManager.unregisterListener(this, mSensorAccelerometer);
		mSensorManager.unregisterListener(this, mSensorMagneticField);
		update(false);
	}

	/**
	 * @return Last known orientation (for read only) or null if not available
	 */
	public Orientation get() {
		if (ret != null && ret.invalid()) {
			boolean success = SensorManager.getRotationMatrix(mMatrixR, mMatrixI, mValuesAccelerometer, mValuesMagneticField);
			if (success) {
				SensorManager.getOrientation(mMatrixR, mMatrixValues);
				mAzimuth.put(MathUtil.toDegrees(mMatrixValues[0]));
				mPitch.put(MathUtil.toDegrees(mMatrixValues[1]));
				mRoll.put(MathUtil.toDegrees(mMatrixValues[2]));
				if (Util.debug()) Util.printf("Current orientation = (%.2f, %.2f, %.2f).\n",
						MathUtil.toDegrees(mMatrixValues[0]),
						MathUtil.toDegrees(mMatrixValues[1]),
						MathUtil.toDegrees(mMatrixValues[2]));
			}
			if (Float.isNaN(mAzimuth.get()))
				return ret = null;

			float declination = 0;
			LocationProvider.Location loc = mLocation.get();
			if (loc != null) {
				GeomagneticField field = new GeomagneticField((float)loc.latitude, (float)loc.longitude, (float)loc.altitude, TimeProvider.currentTimeMillis());
				declination = field.getDeclination();
			}

			if (Util.debug()) Util.printf("Declination = %.2f.\n", declination);
			ret.set(mAzimuth.get() + declination, mPitch.get(), mRoll.get());
		}
		return ret;
	}

	/**
	 * Set a callback
	 * @param callBack The callback to call when location is updated
	 * @param interval The minimum interval between two calls
	 */
	public void setCallBack(CallBack callBack, long interval) {
		mLocation.setCallBack(new LocationProvider.CallBack() {
			@Override
			public void onCall(LocationProvider.Location location) {
				update(true);
			}
		}, interval);
		mCallBack = new Util.IntervalCallBack<Orientation>(callBack, interval);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				System.arraycopy(event.values, 0, mValuesAccelerometer, 0, 3);
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				System.arraycopy(event.values, 0, mValuesMagneticField, 0, 3);
				break;
		}
		update(true);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	protected void update(boolean valid) {
		if (valid) {
			if (ret == null)
				ret = new Orientation();
			else
				ret.reset();

			if (mCallBack.canCall()) mCallBack.onCall(get());
		}
		else
			ret = null;
	}
}
