package net.aquaries.androidstudiotest.Device;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;

/**
 * Created by Bell on 3/21/2016.
 * Manages permissions.
 */
public abstract class Permission {
	public static final int PERMISSION_LOCATION = 0x1;

	protected static HashMap<Integer, String[]> mPermissions = new HashMap<Integer, String[]>();
	protected static HashMap<Integer, RequestCallBack> mCallbacks = new HashMap<Integer, RequestCallBack>();

	public interface RequestCallBack {
		void onResult(boolean granted);
	}

	public static boolean check(Activity context, int requestCode) {
		String[] permissions = mPermissions.get(requestCode);
		if (permissions == null) return false;

		boolean granted = true;
		for (String permission : permissions)
			if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
				granted = false;
		return granted;
	}

	/**
	 * Request permission
	 * @param context Current Activity (e.g. MainActivity.this)
	 * @param requestCode The permission code defined as PERMISSION_ in this class
	 * @param callback The callback to be called when permission is granted or denied.
	 */
	public static void request(Activity context, int requestCode, RequestCallBack callback) {
		if (check(context, requestCode)) {
			callback.onResult(true);
			return;
		}

		String[] permissions = mPermissions.get(requestCode);
		if (permissions == null) return;

		boolean rationale = false;
		for (String permission : permissions)
			if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission))
				rationale = true;
		if (rationale) {
			//TODO Display "Why the f**k didn't you grant permission?!"
		}
		else {
			mCallbacks.put(requestCode, callback);
			ActivityCompat.requestPermissions(context, permissions, requestCode);
		}
	}

	/**
	 * All Activities must call this function to handle permission requests
	 * @param context Current Activity (e.g. MainActivity.this)
	 * @param requestCode Same as Activity.onRequestPermissionsResult()
	 * @param permissions Same as Activity.onRequestPermissionsResult()
	 * @param grantResults Same as Activity.onRequestPermissionsResult()
	 */
	public void onRequestPermissionsResult(Activity context, int requestCode, String[] permissions, int[] grantResults) {
		boolean granted = (grantResults.length > 0);
		for (int result : grantResults)
			if (result != PackageManager.PERMISSION_GRANTED)
				granted = false;
		mCallbacks.get(requestCode).onResult(granted);
	}

	static {
		mPermissions.put(PERMISSION_LOCATION, new String[] {
				Manifest.permission.ACCESS_COARSE_LOCATION,
				Manifest.permission.ACCESS_FINE_LOCATION,
		});
	}
}
