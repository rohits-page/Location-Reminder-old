package rcs.LocationReminder.EventHandler;

import rcs.LocationReminder.R;
import rcs.LocationReminder.ReminderDetailsListAdapter;
import rcs.LocationReminder.BO.ReminderBO;
import rcs.LocationReminder.Shared.SharedApplicationSettings;
import rcs.LocationReminder.general.ApplicationSettings;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;


public class ReminderListEventHandler implements OnItemLongClickListener,
		OnItemSelectedListener {

	private final String TAG = this.getClass().getName();

	private Activity context = null;
	private ReminderDetailsListAdapter adla = null;

	public ReminderListEventHandler(Activity context, ReminderDetailsListAdapter adla) {
		this.context = context;
		this.adla = adla;
	}

	public void onItemSelected(AdapterView<?> parentView, View childView,
			int position, long id) {
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Item selected " + position);
		//adla.setSelected(position);

	}

	public void onNothingSelected(AdapterView<?> parentView) {

	}

	public boolean onItemLongClick(AdapterView<?> parentView, View childView,
			int position, long id) {
		// Get the selected row value and then initiate activity Workout in VIEW
		// mode
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "LongClick Event notification received");
		if (position > -1) {
			ReminderBO bo = adla.getItem(position);
			if (null != bo) {
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					Log.d(TAG, "Initiating Activity to View details of Reminder "
							+ bo.getID());
				Intent intent = new Intent();
				intent.setAction(context.getResources().getString(R.string.intent_manage_reminder));
				intent.putExtra(ApplicationSettings.CRUD_MODE, ApplicationSettings.CRUD_VIEW);
				Bundle bundle = new Bundle();
				bundle.putSerializable(ApplicationSettings.keyExchangeReminderBO, bo);
				intent.putExtra(ApplicationSettings.bundleReminderBO,
						bundle);
				context.startActivityForResult(intent, ApplicationSettings.REQUEST_CODE_MANAGE_Reminder);
			}
		}
		return false;
	}

}
