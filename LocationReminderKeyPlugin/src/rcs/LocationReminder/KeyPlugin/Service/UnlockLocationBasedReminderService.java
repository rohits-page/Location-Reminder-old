package rcs.LocationReminder.KeyPlugin.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UnlockLocationBasedReminderService extends BroadcastReceiver {

	private final String TAG = this.getClass().getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent unlockIntent=new Intent();
		unlockIntent.setAction("android.RCS.LocationReminder.Intent.Action.UnlockApp");
		unlockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(unlockIntent);

	}

}
