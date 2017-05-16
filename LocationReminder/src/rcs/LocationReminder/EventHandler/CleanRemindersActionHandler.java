package rcs.LocationReminder.EventHandler;

import rcs.LocationReminder.LocationUpdateUtils;
import rcs.LocationReminder.R;
import rcs.LocationReminder.Data.DataBridge;
import rcs.LocationReminder.Data.LocationReminderDataModel;
import rcs.LocationReminder.general.ApplicationSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

public class CleanRemindersActionHandler implements OnClickListener {

	private Context _context;
	
	public CleanRemindersActionHandler(Context context){
		_context=context;
	}
	
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case (DialogInterface.BUTTON_POSITIVE): {
			DataBridge dataBridge = new DataBridge(_context);
			String[] selectionArgs = { ApplicationSettings.tracker_reminder_name };
			if (LocationUpdateUtils.isAppUnlocked(_context.getApplicationContext()))
				dataBridge.deleteAllReminders();
			else
				dataBridge.deleteReminders(dataBridge.getReminders(
						LocationReminderDataModel.Reminders.Reminder_NAME
								+ " <> ?", selectionArgs));
			Toast.makeText(_context,
					_context.getResources().getString(R.string.MSG_DeleteReminders),
					Toast.LENGTH_SHORT).show();
			dialog.dismiss();
			break;
		}
		case (DialogInterface.BUTTON_NEGATIVE): {
			dialog.dismiss();
			break;
		}
		}

	}

}
