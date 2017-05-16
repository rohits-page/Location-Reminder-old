package rcs.LocationReminder.Service;

import java.util.List;

import javax.crypto.Cipher;

import rcs.LocationReminder.LocationUpdateUtils;
import rcs.LocationReminder.R;
import rcs.LocationReminder.BO.ReminderBO;
import rcs.LocationReminder.Data.DataBridge;
import rcs.LocationReminder.Shared.SharedApplicationSettings;
import rcs.LocationReminder.general.ApplicationSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class StartupIntentReceiver extends BroadcastReceiver {

	private final String TAG = this.getClass().getName();
	private Cipher dcipher, ecipher;

	@Override
	public void onReceive(Context context, Intent receivedIntent) {
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG,
					"Received intent for Boot Completed\nCreating proximity Reminders.");
		// Fetch all the Reminders from database and create proximity Reminders
		// for
		// all of them
		DataBridge databridge = new DataBridge(context);

		List<ReminderBO> reminderList = databridge.getReminders(null, null);
		if (null != reminderList) {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG,
						"Creating proxmimty Reminders for "
								+ reminderList.size() + " Reminders");

			LocationUpdateUtils.syncAsPerTracker(context);

			for (int index = 0; index < reminderList.size(); index++) {
				boolean flagCreateReminder = false;
				ReminderBO reminderBO = reminderList.get(index);
				if (LocationUpdateUtils.isAppUnlocked(context)) {
					// do not create reminders for alerts that have names
					// "DELETED"
					if (!reminderBO.getReminderName().equalsIgnoreCase(
							ApplicationSettings.tracker_reminder_name))
						flagCreateReminder = true;
				} else {
					if (reminderBO.getID() <= ApplicationSettings.FREE_APP_REMINDER_LIMIT) {
						// do not create reminders for alerts that have names
						// "DELETED"
						if (!reminderBO.getReminderName().equalsIgnoreCase(
								ApplicationSettings.tracker_reminder_name))
							flagCreateReminder = true;
					} else {
						if (SharedApplicationSettings.MODE_DEVELOPMENT)
							Log.d(TAG, "Not recreating reminder for reminder "
									+ reminderBO.toString()
									+ " as the ID>limit and it is free App");
					}
				}
				if (flagCreateReminder) {
					if (SharedApplicationSettings.MODE_DEVELOPMENT)
						Log.d(TAG, "Creating proximity Reminder for "
								+ reminderBO.toString());
					LocationUpdateUtils.createProximityReminderForReminder(
							context, reminderBO);
					if (SharedApplicationSettings.MODE_DEVELOPMENT)
						Log.d(TAG, "Proximity Reminder created");
				} else {
					if (SharedApplicationSettings.MODE_DEVELOPMENT)
						Log.d(TAG, "not recreating reminder");
				}

			}
		} else {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG,
						"No Reminders in the database, hence no proximity Reminders created");
		}

		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.MSG_ServiceLocationReminderSuccessfulInit),
					Toast.LENGTH_SHORT).show();
	}
}
