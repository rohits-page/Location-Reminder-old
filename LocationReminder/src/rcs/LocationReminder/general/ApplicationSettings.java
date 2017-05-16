package rcs.LocationReminder.general;

import java.util.Random;

import android.location.LocationManager;

public class ApplicationSettings {

	//Allowed number of reminders = specified value-1
	public static int FREE_APP_REMINDER_LIMIT = 20;

	public static String DEFAULT_GPS_PROVIDER = LocationManager.GPS_PROVIDER;
	public static String DEFAULT_NETWORK_PROVIDER = LocationManager.NETWORK_PROVIDER;
	public static long mimimumUpdateTimeLapse = 400;// in MilliSeconds
	public static long minimumUpdateDistance = 1;// in Meters
	public static int defaultLocationUpdateZoomLevel = 16;

	// bundle name that contains Reminder object
	public static String bundleReminderBO = "BUNDLE_ReminderBO";
	public static String keyExchangeReminderBO = "KEY_EXCHANGE_ReminderBO";
	public static String keyExchangeReminderList = "KEY_EXCHANGE_ReminderList";
		
	//bundle key names
	public static String keyReminderDate = "KEY_ReminderDate";
	public static String keyReminderTime = "KEY_ReminderTime";

	// Intent Extra Names
	public static String CRUD_MODE = "CRUD_MODE";

	// Define values for CRUD MODE
	public static final int CRUD_INVALID = 0;
	public static final int CRUD_CREATE = 01;
	public static final int CRUD_VIEW = 02;
	public static final int CRUD_EDIT = 03;
	public static final int CRUD_DELETE = 04;
	public static final int CRUD_NOTIFY = 05;

	// Result Codes
	public static final int REQUEST_CODE_CREATE_Reminder = 01;
	public static final int REQUEST_CODE_VIEW_Reminder = 02;
	public static final int REQUEST_CODE_UPDATE_Reminder = 03;
	public static final int REQUEST_CODE_DELETE_Reminder = 04;
	public static final int REQUEST_CODE_MANAGE_Reminder = 04;
	public static final int REQUEST_CODE_NOTIFY_Reminder = 11;
	public static final int REQUEST_CODE_Reminder_LIST = 21;

	// random number generator
	public static Random generator = new Random();

	// INT_TRUE,INT_FALSE Numeric value
	public static final int INT_TRUE = 1;
	public static final int INT_FALSE = 0;

	// Database
	public static final String AUTHORITY = "com.google.provider.locationreminder";
	public static final int INAVLID_Reminder_BO_ID = -1;

	// Help
	public static final String help_file_name = "location_based_reminder_help_v2.html";

	// ProgressDialog
	public static final int PROGRESS_DIALOG_SPINNER = 0;
	public static final int PROGRESS_DIALOG_HORIZONTAL = 1;
	// Class constants defining state of the thread
	public static final int PROGRESS_THREAD_DONE = 0;
	public static final int PROGRESS_THREAD_RUNNING = 1;

	public static final int PROGRESS_BAR_MAX_VALUE = 100;

	// Shared Preferences
	public static final int value_type_int = 0;
	public static final int value_type_string = 1;
	public static final int value_type_long = 2;
	public static final int value_type_boolean = 3;
	public static final int value_type_float = 4;

	// Constants to prevent people from being able to use free app after
	// reinstalling
	public static final String tracker_reminder_name = "DUMMY_LOCATION_REMINDER";
	public static final String tracker_reminder_description = "FREE APP was reinstalled.\nCreated dummy reminder to reflect what you had prior to uninstalling.\nYou will be able to delete this once you purchase the key.";
	public static final String tracker_file_name = "/sdcard/lba.dat";
	public static final String Algorithm_Name="DES";
	public static String StaticKey = "0a1b2c3d";//must be 8 long
	public static String InitializationVector = "e5f60718";

	
	//Dialog IDs
	public static final int DIALOG_CALENDAR_ID = 0;
	public static final int DIALOG_TIME_ID = 1;
	
	//DateTime Format for storing dates in database
	public static final String dateTimeFormat ="yyyy-MM-dd KK:mm:ss aa";
	public static final String blankDate="--/--/----";
	public static final String blankTime="--:-- **";
	public static final String zeroTime="00:00 AM";

	//Tab Settings
	public static final int tab_height=55;
	public static final int tab_text_style_selected=android.R.style.TextAppearance_Medium_Inverse;
	public static final int tab_text_style_unselected=android.R.style.TextAppearance_Widget_DropDownHint;
}
