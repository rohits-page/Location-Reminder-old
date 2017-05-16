package rcs.LocationReminder.EventHandler;

import rcs.LocationReminder.LocationUpdateUtils;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class MultipleAddressFoundHandler implements OnClickListener {

	private final String TAG = this.getClass().getName();

	private GeoPoint[] geoPoints;
	private CharSequence[] items;
	private MapView mapView;
	private Activity activity;

	public MultipleAddressFoundHandler(Activity activity,
			GeoPoint[] geo_points, CharSequence[] items, MapView mapview) {
		this.geoPoints = geo_points;
		this.mapView = mapview;
		this.items = items;
		this.activity = activity;
	}

	public void onClick(DialogInterface dialog, int which) {
		if (null != geoPoints)
			LocationUpdateUtils.handleSelectedAddress(items[which].toString(), geoPoints[which],mapView,activity);
		else
			LocationUpdateUtils.handleSelectedAddress(items[which].toString(), null,mapView,activity);

	}

	
}
