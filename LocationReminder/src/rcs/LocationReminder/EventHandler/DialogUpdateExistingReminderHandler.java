package rcs.LocationReminder.EventHandler;

import java.io.Serializable;
import java.util.List;

import rcs.LocationReminder.LocationUpdateUtils;
import rcs.LocationReminder.R;
import rcs.LocationReminder.BO.ReminderBO;
import rcs.LocationReminder.general.ApplicationSettings;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;

public class DialogUpdateExistingReminderHandler implements OnClickListener {

	/*
	 * NegativeButton - Update Existing Reminder PositiveButton - Create New Reminder
	 * NeutralButton - Quit
	 */

	private List<ReminderBO> ReminderList = null;
	private Activity activity = null;

	public DialogUpdateExistingReminderHandler(Activity activity, List<ReminderBO> ReminderBOList) {
		this.ReminderList = ReminderBOList;
		this.activity = activity;
	}

	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE: {
			// continue creating the current Reminder
			LocationUpdateUtils.addUpdateReminderBOAndEndActivityAppropriately(activity, ReminderList.get(0));
			break;
		}
		case DialogInterface.BUTTON_NEGATIVE: {
			// Update the existing Reminder
			Intent intent = new Intent();
			intent.setAction(activity.getResources().getString(
					R.string.intent_reminder_list));
			Bundle bundle = new Bundle();
			bundle.putSerializable(ApplicationSettings.keyExchangeReminderList,
					(Serializable) ReminderList);
			intent.putExtra(ApplicationSettings.bundleReminderBO, bundle);
			activity.startActivity(intent);
			activity.finish();
			break;
		}
		case DialogInterface.BUTTON_NEUTRAL: {
			// exit this Reminder creation process
			activity.setResult(Activity.RESULT_CANCELED);
			activity.finish();
			break;
		}
		}

	}

}
