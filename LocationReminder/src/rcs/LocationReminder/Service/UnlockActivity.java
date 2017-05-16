package rcs.LocationReminder.Service;

import rcs.LocationReminder.LocationUpdateUtils;
import rcs.LocationReminder.R;
import rcs.LocationReminder.Shared.SharedApplicationSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


/**
 * It receives the Unlock Code from the Paid App and updates the appropriate
 * variable
 * 
 * @author rohsharm
 * 
 */
public class UnlockActivity extends BroadcastReceiver {
	private final String TAG = this.getClass().getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Received intent from the Paid App");

		LocationUpdateUtils.writeBooleanToSharedPreferences(
				SharedApplicationSettings.unlock_status_key_name,
				!SharedApplicationSettings.unlock_status_lock_value,
				context.getApplicationContext());

		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Application unlocked");

		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.MSG_UnlockSuccessful), Toast.LENGTH_SHORT)
					.show();

	}

}
