package rcs.LocationReminder.EventHandler;

import rcs.LocationReminder.LocationUpdateUtils;
import rcs.LocationReminder.BO.ReminderBO;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class DeleteReminderActionHandler implements OnClickListener {

	private final String TAG = this.getClass().getName();

	private Activity _activity;
	private ReminderBO _reminderBO;

	/**
	 * 
	 * @param activity
	 * @param reminderBO
	 */
	public DeleteReminderActionHandler(Activity activity, ReminderBO reminderBO) {
		_activity = activity;
		_reminderBO = reminderBO;
	}

	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE: {
			LocationUpdateUtils.deleteReminderBOCancelPendingIntentNoPromptAndEndActivityAppropriately(_activity,_reminderBO,false);
			dialog.dismiss();
			_activity.finish();
			break;
		}

		case DialogInterface.BUTTON_NEGATIVE: {
			dialog.dismiss();
			break;
		}
		}
	}

}
