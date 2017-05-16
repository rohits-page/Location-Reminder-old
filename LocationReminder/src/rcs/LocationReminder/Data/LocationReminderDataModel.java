package rcs.LocationReminder.Data;

import rcs.LocationReminder.general.ApplicationSettings;
import android.net.Uri;
import android.provider.BaseColumns;

public final class LocationReminderDataModel {

	public static final String DATABASE_NAME = "Location Reminder";
	public static final int DATABASE_VERSION = 2;

	public static final String DATABASE_TABLE_REMINDERS = "Reminders";
	public static final String DATABASE_TABLE_SQLITE_SEQUENCE = "SQLITE_SEQUENCE";

	// This class cannot be instantiated
	private LocationReminderDataModel() {
	}

	/**
	 * Reminder_List table
	 */
	public static final class Reminders implements BaseColumns {
		// This class cannot be instantiated
		private Reminders() {
		}

		/**
		 * The content:// style URL for tables
		 */
		public static final Uri CONTENT_URI_Reminders = Uri
				.parse("content://" + ApplicationSettings.AUTHORITY + "/"
						+ LocationReminderDataModel.DATABASE_TABLE_REMINDERS);

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.locationReminder";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * note.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.locationReminder";

		/**
		 * The name of Reminder
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String Reminder_NAME = "name";

		/**
		 * Reminder description
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String Reminder_DECSRIPTION = "description";

		
		/**
		 * Reminder Address
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String Reminder_ADDRESS = "address";
		
		/**
		 * Reminder Radius
		 * <P>
		 * Type: FLOAT
		 * </P>
		 */
		public static final String Reminder_RADII = "radius";
		
		/**
		 * Reminder Location LatitudeE6
		 * <P>
		 * Type: INT
		 * </P>
		 */
		public static final String Reminder_LATITUDEE6 = "latitudeE6";
		
		
		/**
		 * Reminder Location LongitudeE6
		 * <P>
		 * Type: INT
		 * </P>
		 */
		public static final String Reminder_LONGITUDEE6 = "longitudeE6";
		
		
		/**
		 * Reminder Metric System
		 * <P>
		 * Type: INT
		 * </P>
		 */
		public static final String Reminder_IMPERIAL_METRICS = "ImperialMetrics";
				
		/**
		 * Reminder One Time Occurrence
		 * <P>
		 * Type: INT
		 * </P>
		 */
		public static final String ONE_TIME_Reminder = "OneTimeReminder";
		
		/**
		 * Reminder Add Timing Information
		 * <P>
		 * Type: INT
		 * </P>
		 */
		public static final String Reminder_Timing_Info_Present = "ReminderTimingInfoPresent";
		
		/**
		 * All Day Event
		 * <P>
		 * Type: INT
		 * </P>
		 */
		public static final String All_Day_event = "AllDayEvent";
		
		/**
		 * Reminder Window Start DateTime
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String Reminder_Window_Start_DateTime = "ReminderWindowStartDateTime";
		
		/**
		 * Reminder Window End DateTime
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String Reminder_Window_End_DateTime = "ReminderWindowEndDateTime";
		
		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = _ID + " ASC";
	}

	
	/**
	 * Reminder_List table
	 */
	public static final class sqlite_sequence implements BaseColumns {
		// This class cannot be instantiated
		private sqlite_sequence() {
		}

		/**
		 * The content:// style URL for tables
		 */
		public static final Uri CONTENT_URI_SQLiteSequence = Uri
				.parse("content://" + ApplicationSettings.AUTHORITY + "/"
						+ LocationReminderDataModel.DATABASE_TABLE_SQLITE_SEQUENCE);

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.locationReminder";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * note.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.locationReminder";

		/**
		 * The name of table
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String TABLE_NAME = "name";

		/**
		 * Reminder description
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String SEQUENCE_VALUE = "seq";

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = TABLE_NAME + " ASC";
	}
}
