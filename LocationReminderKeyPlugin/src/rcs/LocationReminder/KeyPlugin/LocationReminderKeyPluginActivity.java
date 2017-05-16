package rcs.LocationReminder.KeyPlugin;

import rcs.LocationReminder.Shared.SharedApplicationSettings;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class LocationReminderKeyPluginActivity extends Activity {
	private final String TAG = this.getClass().getName();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(SharedApplicationSettings.MODE_DEVELOPMENT){
			Log.d(TAG, "Location Based Reminder Unlock Activity Initialized");
		}
		
		Intent unlockIntent = new Intent();
		unlockIntent.setAction(SharedApplicationSettings.Unlock_Intent_action_Name);
		unlockIntent.putExtra(SharedApplicationSettings.unlock_status_key_name,
				!SharedApplicationSettings.unlock_status_lock_value);
		sendBroadcast(unlockIntent);

		if(SharedApplicationSettings.MODE_DEVELOPMENT){
			Log.d(TAG, "Broadcast Sent");
		}
        finish();
    }
}