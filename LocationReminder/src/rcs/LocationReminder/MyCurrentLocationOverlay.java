package rcs.LocationReminder;

import rcs.LocationReminder.Shared.SharedApplicationSettings;
import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class MyCurrentLocationOverlay extends MyLocationOverlay {

	private final String TAG = this.getClass().getName();
	private MapView _mapView;
	private Activity _activity;
	private LocationManager _locationManager;

	public MyCurrentLocationOverlay(Activity activity, MapView mapView,
			LocationManager locationManager) {
		super(activity, mapView);
		this._activity = activity;
		this._mapView = mapView;
		this._locationManager = locationManager;
	}

	@Override
	public synchronized void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		if (SharedApplicationSettings.MODE_DEVELOPMENT) {
			Log.d(TAG, "Location Update event received new location ("
					+ location.getLatitude() + "," + location.getLongitude());
		}
		try {
			_mapView.getController().animateTo(getMyLocation());
		} catch (Exception exc) {
			Log.e(TAG, "Error updating location on map " + exc.getMessage());
		}
	}

	@Override
	public synchronized boolean runOnFirstFix(Runnable runnable) {
		super.runOnFirstFix(runnable);
		_mapView.getController().animateTo(getMyLocation());
		return true;
	}

	@Override
	public boolean onTap(GeoPoint point, MapView map) {
		super.onTap(point, map);
		if (SharedApplicationSettings.MODE_DEVELOPMENT) {
			Log.d(TAG, "MyLocationOverlay : OnTap");
		}
		LocationUpdateUtils
				.addMarkerForAddressOnMap(_activity, _mapView, point);
		return true;
	}

	@Override
	public void onProviderDisabled(String provider) {
		super.onProviderDisabled(provider);
		if (null == LocationUpdateUtils.setProvider(_locationManager)) {
			LocationUpdateUtils
					.buildReminderMessageNoLocationProviderEnabled(_activity);
		}
	}
}
