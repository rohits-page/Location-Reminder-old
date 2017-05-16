package rcs.LocationReminder.Data;

import java.util.HashMap;

import rcs.LocationReminder.Shared.SharedApplicationSettings;
import rcs.LocationReminder.general.ApplicationSettings;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class DataProvider extends ContentProvider {

	private final String TAG = this.getClass().getName();

	private static HashMap<String, String> sRemindersProjectionMap,
			sSQLITESequenceProjectionMap;

	private static final int Reminders = 1;
	private static final int Reminders_ID = 2;

	private static final int SQLITESequence = 3;

	private static final UriMatcher sUriMatcher;

	private static DBHelper mOpenHelper;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(ApplicationSettings.AUTHORITY,
				LocationReminderDataModel.DATABASE_TABLE_REMINDERS, Reminders);
		sUriMatcher.addURI(ApplicationSettings.AUTHORITY,
				LocationReminderDataModel.DATABASE_TABLE_REMINDERS + "/#",
				Reminders_ID);
		sUriMatcher.addURI(ApplicationSettings.AUTHORITY,
				LocationReminderDataModel.DATABASE_TABLE_SQLITE_SEQUENCE,
				SQLITESequence);

		/**
		 * Reminders Table
		 */
		sRemindersProjectionMap = new HashMap<String, String>();
		sRemindersProjectionMap.put(LocationReminderDataModel.Reminders._ID,
				LocationReminderDataModel.Reminders._ID);
		sRemindersProjectionMap.put(
				LocationReminderDataModel.Reminders.Reminder_NAME,
				LocationReminderDataModel.Reminders.Reminder_NAME);
		sRemindersProjectionMap.put(
				LocationReminderDataModel.Reminders.Reminder_DECSRIPTION,
				LocationReminderDataModel.Reminders.Reminder_DECSRIPTION);
		sRemindersProjectionMap.put(
				LocationReminderDataModel.Reminders.Reminder_ADDRESS,
				LocationReminderDataModel.Reminders.Reminder_ADDRESS);
		sRemindersProjectionMap.put(
				LocationReminderDataModel.Reminders.Reminder_LATITUDEE6,
				LocationReminderDataModel.Reminders.Reminder_LATITUDEE6);
		sRemindersProjectionMap.put(
				LocationReminderDataModel.Reminders.Reminder_LONGITUDEE6,
				LocationReminderDataModel.Reminders.Reminder_LONGITUDEE6);
		sRemindersProjectionMap.put(
				LocationReminderDataModel.Reminders.Reminder_RADII,
				LocationReminderDataModel.Reminders.Reminder_RADII);
		sRemindersProjectionMap.put(
				LocationReminderDataModel.Reminders.Reminder_IMPERIAL_METRICS,
				LocationReminderDataModel.Reminders.Reminder_IMPERIAL_METRICS);
		sRemindersProjectionMap.put(
				LocationReminderDataModel.Reminders.ONE_TIME_Reminder,
				LocationReminderDataModel.Reminders.ONE_TIME_Reminder);
		sRemindersProjectionMap.put(
				LocationReminderDataModel.Reminders.Reminder_Timing_Info_Present,
				LocationReminderDataModel.Reminders.Reminder_Timing_Info_Present);
		sRemindersProjectionMap.put(
				LocationReminderDataModel.Reminders.All_Day_event,
				LocationReminderDataModel.Reminders.All_Day_event);
		sRemindersProjectionMap.put(
				LocationReminderDataModel.Reminders.Reminder_Window_Start_DateTime,
				LocationReminderDataModel.Reminders.Reminder_Window_Start_DateTime);
		sRemindersProjectionMap.put(
				LocationReminderDataModel.Reminders.Reminder_Window_End_DateTime,
				LocationReminderDataModel.Reminders.Reminder_Window_End_DateTime);
		
		/**
		 * SQLITE_SEQUENCE table
		 */
		sSQLITESequenceProjectionMap = new HashMap<String, String>();
		sSQLITESequenceProjectionMap.put(
				LocationReminderDataModel.sqlite_sequence.TABLE_NAME,
				LocationReminderDataModel.sqlite_sequence.TABLE_NAME);
		sSQLITESequenceProjectionMap.put(
				LocationReminderDataModel.sqlite_sequence.SEQUENCE_VALUE,
				LocationReminderDataModel.sqlite_sequence.SEQUENCE_VALUE);

	}

	@Override
	public boolean onCreate() {
		try {
			mOpenHelper = new DBHelper(getContext());
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "Obtained instance of DBHelper");
			return true;
		} catch (Exception ex) {
			Log.e(TAG,
					"Error occured while creating the Data Content provider ",
					ex);
			return false;
		}
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case Reminders:
			return LocationReminderDataModel.Reminders.CONTENT_TYPE;
		case Reminders_ID:
			return LocationReminderDataModel.Reminders.CONTENT_ITEM_TYPE;
		case SQLITESequence:
			return LocationReminderDataModel.sqlite_sequence.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Illegal or unknown URI " + uri
					+ " passed.");
		}

	}

	/**
	 * Currently supports WHERE clauses with AND only
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setDistinct(true);
		String orderBy = "";
		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		if (null == db) {
			Log.e(TAG, "Unable to find database");
			throw new SQLException("Unable to obtain database");
		}
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG,
					"Obtained database " + db.getPath() + " Version: "
							+ db.getVersion());
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Loking for URI " + uri);

		switch (sUriMatcher.match(uri)) {
		case Reminders:
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "URI => REMINDERS, Setting table "
						+ LocationReminderDataModel.DATABASE_TABLE_REMINDERS);
			qb.setTables(LocationReminderDataModel.DATABASE_TABLE_REMINDERS);
			qb.setProjectionMap(sRemindersProjectionMap);
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = LocationReminderDataModel.Reminders.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			break;
		case Reminders_ID:
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "URI => REMINDERS_ID, Setting table "
						+ LocationReminderDataModel.DATABASE_TABLE_REMINDERS);
			qb.setTables(LocationReminderDataModel.DATABASE_TABLE_REMINDERS);
			qb.setProjectionMap(sRemindersProjectionMap);
			qb.appendWhere(LocationReminderDataModel.Reminders._ID + " = "
					+ uri.getPathSegments().get(1));
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = LocationReminderDataModel.Reminders.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			break;
		case SQLITESequence:
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG,
						"URI => SQLITE_SEQUENCE, Setting table "
								+ LocationReminderDataModel.DATABASE_TABLE_SQLITE_SEQUENCE);
			qb.setTables(LocationReminderDataModel.DATABASE_TABLE_SQLITE_SEQUENCE);
			qb.setProjectionMap(sSQLITESequenceProjectionMap);
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = LocationReminderDataModel.sqlite_sequence.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			break;
		default:
			throw new IllegalArgumentException("Illegal or unknown URI " + uri
					+ " passed.");
		}

		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Query :\n" + qb.toString());
		// String query=qb.buildQueryString(true,qb.getTables(),projection,
		// selection, null, null,orderBy, null);
		// if (SharedApplicationSettings.LOG_MODE_DEBUG)Log.d(TAG,
		// "Query is "+query);
		if (null != selection) {
			String[] whereclauses = selection.split("AND");
			String whereclause = "";
			for (int i = 0; i < whereclauses.length; i++) {
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					Log.d(TAG, "The Received WHERE clause is "
							+ whereclauses[i]);
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					Log.d(TAG, "The corresponding argument is "
							+ selectionArgs[i]);
				if (TextUtils.isDigitsOnly(selectionArgs[i])) {
					whereclause = (i == 0) ? whereclauses[i].replace("?",
							selectionArgs[i]) : whereclause + " AND "
							+ whereclauses[i].replace("?", selectionArgs[i]);
				} else {
					// they can be Comma separated Numeric Values
					String[] values = selectionArgs[i].split(",");
					String selectionArg = "";
					int alldigitsctr = 0;
					for (int j = 0; j < values.length; j++) {
						// either all values should be numeric or alphanumeric
						if (TextUtils.isDigitsOnly(values[j].replace("-", ""))) {
							alldigitsctr++;
						} else {
							selectionArg += "'" + values[j] + "',";
						}
					}
					if (alldigitsctr == values.length) {
						// all are digits
						whereclause = (i == 0) ? whereclauses[i].replace("?",
								selectionArgs[i]) : whereclause
								+ " AND "
								+ whereclauses[i]
										.replace("?", selectionArgs[i]);
					} else {
						// treat them as string
						whereclause = (i == 0) ? whereclauses[i].replace("?",
								selectionArg.substring(0,
										selectionArg.length() - 1))
								: whereclause
										+ " AND "
										+ whereclauses[i]
												.replace(
														"?",
														selectionArg.substring(
																0,
																selectionArg
																		.length() - 1));
					}
				}
			}
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "Prepared Where clause is \n" + whereclause);
			qb.appendWhere(whereclause);
		}
		Cursor c = qb.query(db, projection, null, null, null, null, orderBy);
		// Cursor c=qb.query(db, projection, null, selectionArgs, null, null,
		// null);
		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count = 0;
		String tableName;
		String id;

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		if (null == db) {
			Log.e(TAG, "Unable to find database");
			throw new SQLException("Unable to obtain database");
		}
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG,
					"Obtained DB " + db.getPath() + " Version "
							+ db.getVersion());
		switch (sUriMatcher.match(uri)) {
		case Reminders:
			tableName = LocationReminderDataModel.DATABASE_TABLE_REMINDERS;
			break;
		case Reminders_ID:
			tableName = LocationReminderDataModel.DATABASE_TABLE_REMINDERS;
			id = uri.getPathSegments().get(1);
			where = LocationReminderDataModel.Reminders._ID + "=" + id
					+ (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : "");
			break;
		default:
			throw new IllegalArgumentException("Illegal or unknown URI " + uri
					+ " passed.");
		}
		count = db.delete(tableName, where, whereArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		Uri newRowUri = null;
		String tableName = null;
		Uri tableContentUri = null;

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		if (null == db) {
			Log.e(TAG, "Unable to find database");
			throw new SQLException("Unable to obtain database");
		}
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG,
					"Obtained DB " + db.getPath() + " Version "
							+ db.getVersion());
		switch (sUriMatcher.match(uri)) {
		case Reminders:
			tableName = LocationReminderDataModel.DATABASE_TABLE_REMINDERS;
			tableContentUri = LocationReminderDataModel.Reminders.CONTENT_URI_Reminders;
			break;
		default:
			throw new IllegalArgumentException("Illegal or unknown URI " + uri
					+ " passed.");
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		long rowId = db.insert(tableName, null, values);
		if (rowId > 0) {
			newRowUri = ContentUris.withAppendedId(tableContentUri, rowId);
			getContext().getContentResolver().notifyChange(newRowUri, null);
			return newRowUri;
		} else {
			throw new SQLException("Failed to insert a row into " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		int count = 0;
		String tableName;
		String id;

		switch (sUriMatcher.match(uri)) {
		case Reminders:
			tableName = LocationReminderDataModel.DATABASE_TABLE_REMINDERS;
			break;
		case Reminders_ID:
			tableName = LocationReminderDataModel.DATABASE_TABLE_REMINDERS;
			id = uri.getPathSegments().get(1);
			where = LocationReminderDataModel.Reminders._ID + "=" + id
					+ (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : "");
			break;
		default:
			throw new IllegalArgumentException("Illegal or unknown URI " + uri
					+ " passed.");
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		count = db.update(tableName, values, where, whereArgs);
		getContext().getContentResolver().notifyChange(uri, null);

		return count;

	}

}
