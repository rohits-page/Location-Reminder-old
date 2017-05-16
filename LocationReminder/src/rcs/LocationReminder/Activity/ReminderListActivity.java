package rcs.LocationReminder.Activity;

import java.util.List;

import rcs.LocationReminder.R;
import rcs.LocationReminder.ReminderDetailsListAdapter;
import rcs.LocationReminder.BO.ReminderBO;
import rcs.LocationReminder.Data.DataBridge;
import rcs.LocationReminder.Data.LocationReminderDataModel;
import rcs.LocationReminder.EventHandler.ReminderListEventHandler;
import rcs.LocationReminder.Shared.SharedApplicationSettings;
import rcs.LocationReminder.general.ApplicationSettings;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ReminderListActivity extends Activity {

	private final String TAG = this.getClass().getName();

	private ListView listViewReminderList = null;
	private List<ReminderBO> ReminderList = null;
	private DataBridge databridge = null;
	private ReminderDetailsListAdapter adla = null;
	private ReminderListEventHandler aleh = null;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFormat(PixelFormat.TRANSPARENT);

		// Inflate our UI from its XML layout description.
		setContentView(R.layout.reminder_list);

		databridge = new DataBridge(this);
		listViewReminderList = (ListView) findViewById(R.id.listViewReminders);
		Button btnExit = (Button) findViewById(R.id.btnExitReminderListView);
		if (null != btnExit)
			btnExit.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					setResult(RESULT_OK);
					finish();
				}
			});
		Intent receivedIntent = getIntent();

		if (null != receivedIntent) {
			Bundle bundle = receivedIntent
					.getBundleExtra(ApplicationSettings.keyExchangeReminderList);
			if (null == bundle) {
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					Log.d(TAG, "No bundle received");
				ReminderList = databridge.getReminders(null, null);
			} else {
				// A list of Reminders was sent with this bundle
				try {
					ReminderList = (List<ReminderBO>) bundle
							.getSerializable(ApplicationSettings.keyExchangeReminderList);
				} catch (Exception exc) {
					Log.e(TAG, exc.getMessage());
				}
			}
		} else {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "No intent received");
			ReminderList = databridge.getReminders(null, null);
		}

		if (null != ReminderList && ReminderList.size() > 0) {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "Value of listview " + listViewReminderList);

			adla = new ReminderDetailsListAdapter(this, listViewReminderList,
					ReminderList);
			aleh = new ReminderListEventHandler(this, adla);

			listViewReminderList.setAdapter(adla);

			listViewReminderList.setOnItemSelectedListener(aleh);
			listViewReminderList.setOnItemLongClickListener(aleh);
		} else {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG,
						"Unable to retrieve the Reminders to be displayed, hence exiting");
			Toast.makeText(this,
					getResources().getString(R.string.MSG_NoReminder),
					Toast.LENGTH_LONG).show();
			setResult(RESULT_OK);
			finish();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		case (RESULT_OK): {
			// Refresh the ReminderBO's contained in here
			String ReminderIDs = "";
			for (int index = 0; index < ReminderList.size(); index++) {
				if (index < ReminderList.size() - 1)
					ReminderIDs += ReminderList.get(index).getID() + ",";
				else
					ReminderIDs += ReminderList.get(index).getID();
			}
			String[] selectionArgs = { ReminderIDs };
			ReminderList = databridge.getReminders(
					LocationReminderDataModel.Reminders._ID + " in (?)",
					selectionArgs);
			adla.updateData(ReminderList);
			listViewReminderList.setAdapter(adla);
			break;
		}
		default:
			break;
		}

	}
}
