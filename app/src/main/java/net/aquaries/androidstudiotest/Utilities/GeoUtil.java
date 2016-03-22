package net.aquaries.androidstudiotest.Utilities;

import net.aquaries.androidstudiotest.Device.LocationProvider;

/**
 * Created by Bell on 3/21/2016.
 * Utilities related to geography
 */
public class GeoUtil {
	public static final LocationProvider.Location LOCATION_KAABA = new LocationProvider.Location(21.422487, 39.826206, 301.);

	public static float angleTo(LocationProvider.Location from, LocationProvider.Location to) {
		double num = MathUtil.sin(to.longitude - from.longitude);
		double denom = MathUtil.cos(from.latitude) * MathUtil.tan(to.latitude) - MathUtil.sin(from.latitude) * MathUtil.cos(to.longitude - from.longitude);
		return MathUtil.atan2(num, denom);
	}

	public static float qibla(LocationProvider.Location from) {
		return angleTo(from, LOCATION_KAABA);
	}
}
