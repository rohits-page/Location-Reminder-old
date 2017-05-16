package rcs.LocationReminder.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rcs.LocationReminder.BO.ReminderBO;
import rcs.LocationReminder.Shared.SharedApplicationSettings;
import rcs.LocationReminder.general.ApplicationSettings;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class DataBridge {
	private final String TAG = this.getClass().getName();
	private ContentResolver dataProvider = null;

	public DataBridge(Context context) {
		dataProvider = context.getContentResolver();
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Data Provider for context " + context
					+ " is now available. It is "
					+ dataProvider.getClass().getName());
	}

	public List<Map<String, String>> getContentsOfTableRemindersAsListMap(
			String... sortOrder) {
		List<Map<String, String>> tableData = new ArrayList<Map<String, String>>();
		try {
			tableData = transformCursorToListMap(getContentsOfTableReminders(
					null, null, sortOrder));
		} catch (IllegalArgumentException iae) {
			Log.e(TAG, iae.getMessage());
		}
		return tableData;
	}

	public List<String> getReminderNames(String selection,
			String selectionArgs[], String... sortOrder) {
		List<String> ReminderNames = new ArrayList<String>();
		Cursor cur = null;
		try {
			cur = getContentsOfTableReminders(selection, selectionArgs,
					sortOrder);
		} catch (IllegalArgumentException iae) {
			Log.e(TAG, iae.getMessage());
		}
		if (null != cur && cur.moveToFirst()) {
			int colIndx = cur
					.getColumnIndex(LocationReminderDataModel.Reminders.Reminder_NAME);
			do {
				ReminderNames.add(cur.getString(colIndx));

			} while (cur.moveToNext());
		}
		if (null != cur)
			cur.close();
		return ReminderNames;
	}

	public List<Long> getReminderIDs(String selection, String selectionArgs[],
			String... sortOrder) {
		List<Long> ReminderIDs = new ArrayList<Long>();
		Cursor cur = null;
		try {
			cur = getContentsOfTableReminders(selection, selectionArgs,
					sortOrder);
		} catch (IllegalArgumentException iae) {
			Log.e(TAG, iae.getMessage());
		}
		if (null != cur && cur.moveToFirst()) {
			int colIndx = cur
					.getColumnIndex(LocationReminderDataModel.Reminders._ID);
			do {
				ReminderIDs.add(cur.getLong(colIndx));

			} while (cur.moveToNext());
		}
		if (null != cur)
			cur.close();
		return ReminderIDs;
	}

	/**
	 * Returns the Sequence Values for various tables
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	public List<Long> getSequenceValues(String selection,
			String selectionArgs[], String... sortOrder) {
		List<Long> sequenceValues = new ArrayList<Long>();
		Cursor cur = null;
		try {
			cur = getContentsOfSQLiteSequence(selection, selectionArgs,
					sortOrder);
		} catch (IllegalArgumentException iae) {
			Log.e(TAG, iae.getMessage());
		}
		if (null != cur && cur.moveToFirst()) {
			int colIndx = cur
					.getColumnIndex(LocationReminderDataModel.sqlite_sequence.SEQUENCE_VALUE);
			do {
				sequenceValues.add(cur.getLong(colIndx));

			} while (cur.moveToNext());
		}
		if (null != cur)
			cur.close();
		return sequenceValues;
	}

	public List<ReminderBO> getReminders(String selection,
			String selectionArgs[], String... sortOrder) {
		List<ReminderBO> Reminders = new ArrayList<ReminderBO>();
		Cursor cur = null;
		try {
			cur = getContentsOfTableReminders(selection, selectionArgs,
					sortOrder);
		} catch (IllegalArgumentException iae) {
			Log.e(TAG, iae.getMessage());
		}
		if (null != cur && cur.moveToFirst()) {
			int colIndxID = cur
					.getColumnIndex(LocationReminderDataModel.Reminders._ID);
			int colIndxName = cur
					.getColumnIndex(LocationReminderDataModel.Reminders.Reminder_NAME);
			int colIndxDesc = cur
					.getColumnIndex(LocationReminderDataModel.Reminders.Reminder_DECSRIPTION);
			int colIndxAddress = cur
					.getColumnIndex(LocationReminderDataModel.Reminders.Reminder_ADDRESS);
			int colIndxLatE6 = cur
					.getColumnIndex(LocationReminderDataModel.Reminders.Reminder_LATITUDEE6);
			int colIndxLonE6 = cur
					.getColumnIndex(LocationReminderDataModel.Reminders.Reminder_LONGITUDEE6);
			int colIndxRadii = cur
					.getColumnIndex(LocationReminderDataModel.Reminders.Reminder_RADII);
			int colIndxUSMetric = cur
					.getColumnIndex(LocationReminderDataModel.Reminders.Reminder_IMPERIAL_METRICS);
			int colIndxOneTimeReminder = cur
					.getColumnIndex(LocationReminderDataModel.Reminders.ONE_TIME_Reminder);
			int colIndxReminderTimingInfoPresent = cur
					.getColumnIndex(LocationReminderDataModel.Reminders.Reminder_Timing_Info_Present);
			int colIndxAllDayEvent = cur
					.getColumnIndex(LocationReminderDataModel.Reminders.All_Day_event);
			int colIndxReminderWindowStartDateTime = cur
					.getColumnIndex(LocationReminderDataModel.Reminders.Reminder_Window_Start_DateTime);
			int colIndxReminderWindowEndDateTime = cur
					.getColumnIndex(LocationReminderDataModel.Reminders.Reminder_Window_End_DateTime);
			do {
				ReminderBO ReminderBO = new ReminderBO();

				ReminderBO.setID(cur.getLong(colIndxID));
				ReminderBO.setReminderName(cur.getString(colIndxName));
				ReminderBO.setReminderDescription(cur.getString(colIndxDesc));
				ReminderBO.setReminderAddress(cur.getString(colIndxAddress));
				ReminderBO.setReminderGeoPoint(new GeoPoint(cur
						.getInt(colIndxLatE6), cur.getInt(colIndxLonE6)));
				ReminderBO.setReminderRadii(cur.getFloat(colIndxRadii));
				ReminderBO.setUSMetrics(cur.getInt(colIndxUSMetric));
				ReminderBO.setOneTimeReminder(cur
						.getInt(colIndxOneTimeReminder));
				ReminderBO.setTimeInfoPresent(cur
						.getInt(colIndxReminderTimingInfoPresent));
				ReminderBO.setAllDayEvent(cur.getInt(colIndxAllDayEvent));
				if (ReminderBO.isTimeInfoPresent()) {
					SimpleDateFormat sdf = new SimpleDateFormat(
							ApplicationSettings.dateTimeFormat);
					try {
						ReminderBO
								.setFromDate(sdf.parse(cur
										.getString(colIndxReminderWindowStartDateTime)));
					} catch (ParseException e) {
						Log.e(TAG,
								"Error occured while parsing the datetime "
										+ cur.getString(colIndxReminderWindowStartDateTime));
					}

					try {
						ReminderBO.setToDate(sdf.parse(cur
								.getString(colIndxReminderWindowEndDateTime)));
					} catch (ParseException e) {
						Log.e(TAG,
								"Error occured while parsing the datetime "
										+ cur.getString(colIndxReminderWindowEndDateTime));
					}
				}

				Reminders.add(ReminderBO);

			} while (cur.moveToNext());
		}
		if (null != cur)
			cur.close();

		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Found " + Reminders.size()
					+ " Reminders that match the given criterion");

		return Reminders;
	}

	/**
	 * It will add the ReminderBO to the table Reminders If the ID of the
	 * ReminderBO != -1, else it will update the row with corresponding ID
	 * 
	 * @param ReminderBO
	 * @return
	 */
	public Uri addUpdateReminder(ReminderBO ReminderBO) {
		Uri newReminderUri = null;
		Uri tableUri = null;
		if (null != ReminderBO && SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Adding Reminder BO\n" + ReminderBO.toString());
		if (null != ReminderBO) {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "Adding Reminder BO\n" + ReminderBO.toString());
			ContentValues cv = new ContentValues();
			tableUri = LocationReminderDataModel.Reminders.CONTENT_URI_Reminders;

			cv.put(LocationReminderDataModel.Reminders.Reminder_NAME,
					ReminderBO.getReminderName());
			cv.put(LocationReminderDataModel.Reminders.Reminder_DECSRIPTION,
					ReminderBO.getReminderDescription());
			cv.put(LocationReminderDataModel.Reminders.Reminder_ADDRESS,
					ReminderBO.getReminderAddress());
			cv.put(LocationReminderDataModel.Reminders.Reminder_LATITUDEE6,
					ReminderBO.getReminderLatitudeE6());
			cv.put(LocationReminderDataModel.Reminders.Reminder_LONGITUDEE6,
					ReminderBO.getReminderLongitudeE6());
			cv.put(LocationReminderDataModel.Reminders.Reminder_RADII,
					ReminderBO.getReminderRadii());
			if (ReminderBO.isImperialMetrics())
				cv.put(LocationReminderDataModel.Reminders.Reminder_IMPERIAL_METRICS,
						ApplicationSettings.INT_TRUE);
			else
				cv.put(LocationReminderDataModel.Reminders.Reminder_IMPERIAL_METRICS,
						ApplicationSettings.INT_FALSE);
			if (ReminderBO.isOneTimeReminder())
				cv.put(LocationReminderDataModel.Reminders.ONE_TIME_Reminder,
						ApplicationSettings.INT_TRUE);
			else
				cv.put(LocationReminderDataModel.Reminders.ONE_TIME_Reminder,
						ApplicationSettings.INT_FALSE);
			if (ReminderBO.isTimeInfoPresent()) {
				cv.put(LocationReminderDataModel.Reminders.Reminder_Timing_Info_Present,
						ApplicationSettings.INT_TRUE);
				SimpleDateFormat sdf = new SimpleDateFormat(
						ApplicationSettings.dateTimeFormat);
				cv.put(LocationReminderDataModel.Reminders.Reminder_Window_Start_DateTime,
						sdf.format(ReminderBO.getFromDate()));
				cv.put(LocationReminderDataModel.Reminders.Reminder_Window_End_DateTime,
						sdf.format(ReminderBO.getToDate()));
			} else {
				cv.put(LocationReminderDataModel.Reminders.Reminder_Timing_Info_Present,
						ApplicationSettings.INT_FALSE);
			}

			if (ReminderBO.isAllDayEvent()) {
				cv.put(LocationReminderDataModel.Reminders.All_Day_event,
						ApplicationSettings.INT_TRUE);
			} else {
				cv.put(LocationReminderDataModel.Reminders.All_Day_event,
						ApplicationSettings.INT_FALSE);
			}

			if (ReminderBO.getID() == ApplicationSettings.INAVLID_Reminder_BO_ID) {
				// Create a new row in the table
				newReminderUri = dataProvider.insert(tableUri, cv);
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					Log.d(TAG, "Added the Reminder details to DB : "
							+ newReminderUri.toString());
			} else {
				// Update an existing row in the table
				newReminderUri = Uri
						.parse(tableUri + "//" + ReminderBO.getID());
				if (1 != dataProvider.update(newReminderUri, cv, null, null)) {
					newReminderUri = null;
					Log.d(TAG, "Unable to update the Reminder details to DB");
				} else {
					if (SharedApplicationSettings.MODE_DEVELOPMENT)
						Log.d(TAG, "Updated the Reminder details to DB : "
								+ newReminderUri.toString());
				}
			}

		} else {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG,
						"Null ReminderBO received, hence not adding anything");
		}
		return newReminderUri;
	}

	public List<Uri> addUpdateReminder(List<ReminderBO> ReminderList) {
		List<Uri> uriList = new ArrayList<Uri>();
		if (null != ReminderList) {
			for (int index = 0; index < ReminderList.size(); index++) {
				uriList.add(addUpdateReminder(ReminderList.get(index)));
			}
		}
		return uriList;
	}

	/**
	 * Delete all the Reminders in the list
	 * 
	 * @param boc
	 * @return
	 */
	public boolean deleteReminders(List<ReminderBO> boc) {
		boolean flag = true;
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG,
					"Deleting all the supplied Reminders, count= " + boc.size());
		for (int i = 0; i < boc.size(); i++) {
			ReminderBO bo = boc.get(i);
			try {
				if (!deleteReminder(bo)) {
					Log.e(TAG, "Was unable to delete the BO for Reminder name "
							+ bo.getReminderName());
					flag = false;
				}
			} catch (Exception ex) {
				Log.e(TAG,
						"Swallowing exception that occured while adding BO for "
								+ bo.getReminderName()
								+ ", continuing with next ReminderBO", ex);
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * Delete the specified Reminder
	 * 
	 * @param ReminderBO
	 * @return
	 * 
	 * */
	public boolean deleteReminder(ReminderBO ReminderBO) {
		boolean flag = false;
		Uri tableUri = null;
		String where = "";

		if (null != ReminderBO
				&& ReminderBO.getID() != ApplicationSettings.INAVLID_Reminder_BO_ID) {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "Deleting Reminder BO\n" + ReminderBO.toString());
			String id = ReminderBO.getID() + "";
			tableUri = LocationReminderDataModel.Reminders.CONTENT_URI_Reminders;
			where = LocationReminderDataModel.Reminders._ID + " = ?";
			flag = (1 == dataProvider.delete(tableUri, where,
					new String[] { id }));
		} else {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "This BO does no exsist in DB");
			flag = true;
		}

		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "result of deletion " + flag);

		return flag;
	}

	/**
	 * Delete all Reminders from the table
	 * 
	 * @return
	 */
	public void deleteAllReminders() {
		dataProvider.delete(
				LocationReminderDataModel.Reminders.CONTENT_URI_Reminders,
				null, null);
	}

	/**
	 * Pass the where clause as "arg1=? AND arg2=?...", pass the values for ? in
	 * a separate array. Currently only AND is supported.
	 * 
	 * @param selection
	 *            : can be null (where clause)
	 * @param selectionArgs
	 *            : should be null only if selection=null and should contain the
	 *            same number of arguments as '?' in the WHERE clause
	 * @param sortOrder
	 *            : optional
	 * @return
	 */
	private Cursor getContentsOfTableReminders(String selection,
			String[] selectionArgs, String... sortOrder)
			throws IllegalArgumentException {
		return getContentsOfTable(
				LocationReminderDataModel.DATABASE_TABLE_REMINDERS, selection,
				selectionArgs, sortOrder);
	}

	/**
	 * Pass the where clause as "arg1=? AND arg2=?...", pass the values for ? in
	 * a separate array. Currently only AND is supported.
	 * 
	 * @param selection
	 *            : can be null (where clause)
	 * @param selectionArgs
	 *            : should be null only if selection=null and should contain the
	 *            same number of arguments as '?' in the WHERE clause
	 * @param sortOrder
	 *            : optional
	 * @return
	 */
	private Cursor getContentsOfSQLiteSequence(String selection,
			String[] selectionArgs, String... sortOrder)
			throws IllegalArgumentException {
		return getContentsOfTable(
				LocationReminderDataModel.DATABASE_TABLE_SQLITE_SEQUENCE,
				selection, selectionArgs, sortOrder);
	}

	/**
	 * Pass the where clause as "arg1=? AND arg2=?...", pass the values for ? in
	 * a separate array. Currently only AND is supported.
	 * 
	 * @param tableName
	 *            : Name of Table
	 * @param selection
	 *            : can be null (where clause)
	 * @param selectionArgs
	 *            : should be null only if selection=null and should contain the
	 *            same number of arguments as '?' in the WHERE clause
	 * @param sortOrder
	 *            : optional
	 * @return
	 */
	private Cursor getContentsOfTable(String tableName, String selection,
			String[] selectionArgs, String... sortOrder)
			throws IllegalArgumentException {
		Cursor cur = null;
		String sortBy = null;

		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Retrieving " + tableName);

		if (null != selection && null == selectionArgs)
			throw new IllegalArgumentException(
					"Received Null selectionArgs for a non null WHERE clause");

		if (null != selection
				&& (selection.split("AND").length != selectionArgs.length))
			throw new IllegalArgumentException(
					"Passed unequal number of arguments for the supplied WHERE clause");

		Uri tableUri = null;
		if (tableName
				.equalsIgnoreCase(LocationReminderDataModel.DATABASE_TABLE_REMINDERS)) {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "Querying table Reminders");
			tableUri = LocationReminderDataModel.Reminders.CONTENT_URI_Reminders;
		} else if (tableName
				.equalsIgnoreCase(LocationReminderDataModel.DATABASE_TABLE_SQLITE_SEQUENCE)) {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "Querying table SQLITE_SEQUENCE");
			tableUri = LocationReminderDataModel.sqlite_sequence.CONTENT_URI_SQLiteSequence;
		}

		if (null != tableUri)
			cur = dataProvider.query(tableUri, null, selection, selectionArgs,
					sortBy);
		// cur =
		// context.managedQuery(LocationReminderDataModel.Reminders.CONTENT_URI_Reminders,
		// null,selection, selectionArgs, sortBy);
		return cur;
	}

	/*
	 * Utility Methods
	 */

	/**
	 * Transforms a cursor into an ArrayList object. Each item contains a
	 * HashMap. The Map represents every row of the cursor. The column
	 * names,values are transformed to Key,value pairs of the HashMap. The
	 * HashMap at location=0 contains the ColumnNames. For a cursor with ZERO
	 * rows, the return object will only contain the column names. NOTE: All
	 * values are returned as STRING.
	 * 
	 * @param cur
	 * @return
	 */
	private List<Map<String, String>> transformCursorToListMap(Cursor cur) {
		List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();

		Map<String, String> columnNames = new HashMap<String, String>();
		for (int columnIndex = 0; columnIndex < cur.getColumnCount(); columnIndex++) {
			columnNames.put(cur.getColumnName(columnIndex), null);
		}

		listMap.add(columnNames);

		if (cur.moveToFirst()) {
			do {
				Map<String, String> rowValues = new HashMap<String, String>();
				for (int columnIndex = 0; columnIndex < cur.getColumnCount(); columnIndex++) {
					rowValues.put(cur.getColumnName(columnIndex),
							cur.getString(columnIndex));
				}
				listMap.add(rowValues);
			} while (cur.moveToNext());
		}
		cur.close();

		return listMap;
	}

}
