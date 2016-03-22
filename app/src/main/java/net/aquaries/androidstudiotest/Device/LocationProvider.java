package net.aquaries.androidstudiotest.Device;

import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import net.aquaries.androidstudiotest.Utilities.Util;

/**
 * Created by Bell on 3/21/2016.
 * Provide a Location object when location is updated
 */
public class LocationProvider implements LocationListener {
	/**
	 * latitude and longitude in degrees
	 * altitude in meters
	 */
	public static class Location {
		public double latitude, longitude, altitude;

		public Location() {
			reset();
		}

		public Location(double latitude, double longitude, double altitude) {
			set(latitude, longitude, altitude);
		}

		public void set(double latitude, double longitude, double altitude) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.altitude = altitude;
		}

		public void reset() {
			latitude = Double.NaN;
		}

		public boolean invalid() {
			return Double.isNaN(latitude);
		}

		public Location clone() {
			return new Location(this);
		}

		protected Location(Location copy) {
			set(copy.latitude, copy.longitude, copy.altitude);
		}
	}

	public static interface CallBack extends Util.CallBack<Location> {}

	protected final Activity mContext;
	protected final LocationManager mLocationManager;

	protected android.location.Location mLocation;
	protected Util.IntervalCallBack<Location> mCallBack;

	protected Location ret = null;

	/**
	 * @param context Current Activity (e.g. MainActivity.this)
	 */
	public LocationProvider(Activity context) {
		mContext = context;
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}

	/**
	 * Start location updating
	 * Must be called in Activity.onResume()
	 */
	public void onResume() {
		Permission.request(mContext, Permission.PERMISSION_LOCATION, new Permission.RequestCallBack() {
			@Override
			public void onResult(boolean granted) {
				if (!granted) return;

				for (final String provider : mLocationManager.getProviders(true)) {
					if (LocationManager.GPS_PROVIDER.equals(provider)
							|| LocationManager.PASSIVE_PROVIDER.equals(provider)
							|| LocationManager.NETWORK_PROVIDER.equals(provider)) {
						try {
							if (mLocation == null)
								onLocationChanged(mLocationManager.getLastKnownLocation(provider));
							mLocationManager.requestLocationUpdates(provider, 0, 0, LocationProvider.this);
						} catch (SecurityException e) {
						}
					}
				}
			}
		});
	}

	/**
	 * Stop location updating
	 * Must be called in Activity.onPause()
	 */
	public void onPause() {
		try {
			if (Permission.check(mContext, Permission.PERMISSION_LOCATION))
				mLocationManager.removeUpdates(this);
		} catch (SecurityException e) {
		}
		onLocationChanged(null);
	}

	/**
	 * @return Last known location (For read only) or null if not available
	 */
	public Location get() {
		if (ret != null && ret.invalid()) {
			if (mLocation == null)
				return ret = null;

			ret.set(mLocation.getLatitude(), mLocation.getLongitude(), mLocation.getAltitude());
		}
		return ret;
	}

	/**
	 * Set a callback
	 * @param callBack The callback to call when location is updated
	 * @param interval The minimum interval between two calls
	 */
	public void setCallBack(CallBack callBack, long interval) {
		mCallBack = new Util.IntervalCallBack<Location>(callBack, interval);
	}

	@Override
	public void onLocationChanged(android.location.Location location) {
		mLocation = location;
		update(mLocation != null);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	protected void update(boolean valid) {
		if (valid) {
			if (ret == null)
				ret = new Location();
			else
				ret.reset();

			if (mCallBack.canCall()) mCallBack.onCall(get());
		}
		else
			ret = null;
	}
}
