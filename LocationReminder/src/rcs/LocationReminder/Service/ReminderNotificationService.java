package rcs.LocationReminder.Service;

import java.util.Calendar;
import java.util.List;

import rcs.LocationReminder.LocationUpdateUtils;
import rcs.LocationReminder.R;
import rcs.LocationReminder.BO.ReminderBO;
import rcs.LocationReminder.Data.DataBridge;
import rcs.LocationReminder.Data.LocationReminderDataModel;
import rcs.LocationReminder.Shared.SharedApplicationSettings;
import rcs.LocationReminder.general.ApplicationSettings;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ReminderNotificationService extends Service {

	private final String TAG = this.getClass().getName();
	private String ns = Context.NOTIFICATION_SERVICE;
	private NotificationManager mNotificationManager;
	ReminderBO ReminderBO = null;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent receivedIntent, int startId) {
		super.onStart(receivedIntent, startId);
		performTask(receivedIntent);

	}

	/*
	 * @Override public int onStartCommand(Intent receivedIntent, int flags, int
	 * startId) { super.onStartCommand(receivedIntent, flags, startId);
	 * performTask(receivedIntent); return START_NOT_STICKY; }
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void performTask(Intent receivedIntent) {
		ReminderBO = null;
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "received notification from location manager");
		DataBridge databridge = new DataBridge(getApplicationContext());
		long ReminderID = ApplicationSettings.INAVLID_Reminder_BO_ID;
		boolean flag = false;
		if (null != receivedIntent) {
			ReminderID = receivedIntent.getLongExtra(
					ApplicationSettings.keyExchangeReminderBO,
					ApplicationSettings.INAVLID_Reminder_BO_ID);
			String[] selectionArgs = { ReminderID + "" };
			List<ReminderBO> ReminderList = databridge.getReminders(
					LocationReminderDataModel.Reminders._ID + "=?",
					selectionArgs);
			if (null != ReminderList && ReminderList.size() > 0)
				ReminderBO = ReminderList.get(0);
			else
				Log.e(TAG, "Unable to retrieve any reminder list.");

			if (null != ReminderBO) {
				if (ReminderBO.isTimeInfoPresent()) {
					// check if current time falls between the specified
					// date/time range on the reminder
					Calendar currentTime = Calendar.getInstance();
					if (SharedApplicationSettings.MODE_DEVELOPMENT) {
						Log.d(TAG, "Current time = " + currentTime.toString()
								+ "\nFrom time on reminder = "
								+ ReminderBO.getFromDate().toString()
								+ "\nTo time on reminder = "
								+ ReminderBO.getToDate().toString());
					}
					
					Calendar fromTime = Calendar.getInstance();
					Calendar toTime = Calendar.getInstance();
					
					fromTime.setTime(ReminderBO.getFromDate());
					fromTime.add(Calendar.MILLISECOND, -1);
					
					toTime.setTime(ReminderBO.getToDate());
					toTime.add(Calendar.MILLISECOND, 1);
					
					if (currentTime.after(fromTime)
							&& currentTime.before(toTime)) {
						if (SharedApplicationSettings.MODE_DEVELOPMENT) {
							Log.d(TAG,
									"Current time falls within the band specified by reminder");
						}
						flag = true;
					} else {
						if (SharedApplicationSettings.MODE_DEVELOPMENT) {
							Log.d(TAG,
									"Current time does not fall within the band specified by the reminder.");
						}
					}

				} else {
					if (SharedApplicationSettings.MODE_DEVELOPMENT) {
						Log.d(TAG, "Reminder is not time sensitive.");
					}
					flag = true;
				}
			} else {
				Log.e(TAG, "Unable to retrieve any Reminder.");
			}
		} else {
			Log.e(TAG, "Null intent received.");
		}

		if (!flag) {
			Log.e(TAG,
					"A notification will not be created for intent received for Reminder with ID. Refer to log for more details."
							+ ReminderID);
		} else {

			mNotificationManager = (NotificationManager) getSystemService(ns);

			int icon = R.drawable.notification;
			long when = System.currentTimeMillis();
			CharSequence tickerText = "Location Based Reminder";

			Notification notification = new Notification(icon, tickerText, when);
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.defaults |= Notification.FLAG_SHOW_LIGHTS;
			notification.defaults |= Notification.FLAG_ONLY_ALERT_ONCE;
			notification.defaults |= Notification.FLAG_AUTO_CANCEL;
			Context context = getApplicationContext();
			CharSequence contentTitle = getResources().getString(
					R.string.MSG_TitleNotification);
			CharSequence contentText = ReminderBO.getReminderName();
			PendingIntent contentIntent = LocationUpdateUtils
					.createPendingIntentForCallingManageReminderScreen(
							getApplicationContext(),
							ReminderBO,
							getResources().getString(
									R.string.intent_manage_reminder),
							ApplicationSettings.CRUD_NOTIFY);
			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);
			mNotificationManager.notify(ReminderBO.getShortID(), notification);
		}
	}
}
