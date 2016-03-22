package net.aquaries.androidstudiotest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import net.aquaries.androidstudiotest.Device.LocationProvider;
import net.aquaries.androidstudiotest.Device.OrientationProvider;
import net.aquaries.androidstudiotest.Utilities.GeoUtil;
import net.aquaries.androidstudiotest.Utilities.Util;

import java.io.StringWriter;

public class MainActivity extends AppCompatActivity {
	TextView tvMainSensorInfo;

	LocationProvider mLocationProvider;
	OrientationProvider mOrientationProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});

		tvMainSensorInfo = (TextView) findViewById(R.id.tvMainSensorInfo);

		mLocationProvider = new LocationProvider(this);
		mOrientationProvider = new OrientationProvider(this);

		mLocationProvider.setCallBack(new LocationProvider.CallBack() {
			@Override
			public void onCall(LocationProvider.Location location) {
				update();
			}
		}, 200);

		mOrientationProvider.setCallBack(new OrientationProvider.CallBack() {
			@Override
			public void onCall(OrientationProvider.Orientation orientation) {
				update();
			}
		}, 200);
	}

	private void update() {
		String loc = "Location Unavailable.\n"; LocationProvider.Location location = mLocationProvider.get();
		if (location != null)
			loc = String.format("Location: (%.2f, %.2f, %.2f). Qibla: %.2f\n",
					location.latitude, location.longitude, location.altitude,
					GeoUtil.qibla(location));

		String orient = "Orientation Unavailable.\n"; OrientationProvider.Orientation orientation = mOrientationProvider.get();
		if (orientation != null)
			orient = String.format("Orientation: (%.2f, %.2f, %.2f)\n", orientation.azimuth, orientation.pitch, orientation.roll);

		String kaaba = "Qibla Unavailable.\n";
		if (location != null && orientation != null)
			kaaba = String.format("Qibla on Phone: %.2f\n", GeoUtil.qibla(location) - orientation.azimuth);

		tvMainSensorInfo.setText(loc + orient + kaaba);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocationProvider.onResume();
		mOrientationProvider.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationProvider.onPause();
		mOrientationProvider.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
