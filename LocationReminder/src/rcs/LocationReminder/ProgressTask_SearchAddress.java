package rcs.LocationReminder;

import java.util.HashMap;

import rcs.LocationReminder.Shared.SharedApplicationSettings;

import android.app.Activity;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class ProgressTask_SearchAddress implements ProgressTask {

	private final String TAG = getClass().getName();

	private float completion = 0;
	private Activity activity;
	private int TASK_STATE = PROGRESS_TASK_NOT_STARTED;
	private String selectedAddress;
	private HashMap<String, GeoPoint> possibleAddresses;

	public ProgressTask_SearchAddress(Activity activity,
			String selectedAddress) {
		this.activity = activity;
		this.selectedAddress = selectedAddress;
		TASK_STATE = PROGRESS_TASK_NOT_STARTED;
	}

	public float getCompletionPercentage() {
		return completion;
	}

	public void performTask() {
		TASK_STATE = PROGRESS_TASK_RUNNING;

		if (null != selectedAddress && selectedAddress.length() > 0)
			selectedAddress = selectedAddress.trim();

		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Search for address " + selectedAddress);
		possibleAddresses = LocationUpdateUtils
				.getMatchingAddersses(activity, selectedAddress);
		
		completion = 100;
		TASK_STATE = PROGRESS_TASK_DONE;

	}

	public int getTaskState() {
		return TASK_STATE;
	}

	public Object getTaskResult() {
		return possibleAddresses;
	}

}
