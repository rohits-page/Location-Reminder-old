package rcs.LocationReminder.Overlay;

import java.util.ArrayList;

import rcs.LocationReminder.R;
import rcs.LocationReminder.BO.ReminderBO;
import rcs.LocationReminder.Shared.SharedApplicationSettings;
import rcs.LocationReminder.general.ApplicationSettings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
public class MapReminderOverlay extends ItemizedOverlay {

	private final String TAG = this.getClass().getName();

	private ArrayList<OverlayItem> _overlays;
	private Activity _activity = null;

	public MapReminderOverlay(Drawable defaultMarker, Activity activity) {
		// you want the center-point at the bottom of the image to be the point
		// at which it's attached to the map coordinates
		super(boundCenterBottom(defaultMarker));
		_activity = activity;
		_overlays = new ArrayList<OverlayItem>();
		populate();
	}

	public void addOverlay(OverlayItem overlay) {
		_overlays.add(overlay);
		populate();
		// When the populate() method executes, it will call createItem(int) in
		// the ItemizedOverlay to retrieve each OverlayItem.
	}

	public void clearOverlay() {
		_overlays.clear();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return _overlays.get(i);
	}

	@Override
	public int size() {
		return _overlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		// When the user taps on the overlay
		super.onTap(index);
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "MapReminderOverlay : Tap Event " + index);
		if (index >= 0) {
			final OverlayItem item = _overlays.get(index);
			if (null != item) {
				AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
				builder.setMessage(
						_activity.getResources().getString(
								R.string.MSG_ConfirmCreateReminder)
								+ " " + item.getSnippet() + " ?")
						.setCancelable(true)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										ReminderBO ReminderBO = new ReminderBO();
										ReminderBO.setReminderAddress(item
												.getSnippet());
										ReminderBO.setReminderGeoPoint(item
												.getPoint());
										// Call Manage Reminder
										Intent intent = new Intent();
										intent.setAction(_activity
												.getResources()
												.getString(
														R.string.intent_manage_reminder));
										intent.putExtra(
												ApplicationSettings.CRUD_MODE,
												ApplicationSettings.CRUD_CREATE);
										Bundle bundle = new Bundle();
										bundle.putSerializable(
												ApplicationSettings.keyExchangeReminderBO,
												ReminderBO);
										intent.putExtra(
												ApplicationSettings.bundleReminderBO,
												bundle);
										_activity
												.startActivityForResult(
														intent,
														ApplicationSettings.REQUEST_CODE_CREATE_Reminder);
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog Reminder = builder.create();
				Reminder.show();
			}
		}
		return true;
	}

}
