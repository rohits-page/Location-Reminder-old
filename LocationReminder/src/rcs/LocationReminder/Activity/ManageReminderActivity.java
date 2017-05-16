package rcs.LocationReminder.Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rcs.LocationReminder.LocationUpdateUtils;
import rcs.LocationReminder.R;
import rcs.LocationReminder.BO.ReminderBO;
import rcs.LocationReminder.Data.DataBridge;
import rcs.LocationReminder.Data.LocationReminderDataModel;
import rcs.LocationReminder.EventHandler.DialogUpdateExistingReminderHandler;
import rcs.LocationReminder.Shared.SharedApplicationSettings;
import rcs.LocationReminder.general.ApplicationSettings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.TabActivity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ManageReminderActivity extends TabActivity implements
		OnClickListener, OnCheckedChangeListener, OnDateSetListener,
		OnTimeSetListener, OnTabChangeListener {

	private final String TAG = this.getClass().getName();
	private ReminderBO reminderBO = null;

	private DataBridge dataBridge = null;

	// fields
	private TextView txtReminderAddress = null;
	private TextView txtReminderName = null;
	private TextView txtReminderDescription = null;
	private TextView txtReminderRadii = null;
	private TextView txtActivityTitle = null;
	private TextView lblDistUnits = null;
	private Button btn1 = null;
	private Button btn2 = null;
	private Button btn3 = null;
	private Button btnAddDateFrom = null;
	private Button btnAddTimeFrom = null;
	private Button btnAddDateTo = null;
	private Button btnAddTimeTo = null;
	private CheckBox chkOneTimeReminder = null;
	private CheckBox chkAddTimeInfo = null;
	private CheckBox chkAllDayEvent = null;
	private TextView txtMsg = null;

	private Calendar dateTimeFrom = null;
	private Calendar dateTimeTo = null;

	private int receivedCRUDMode = ApplicationSettings.CRUD_INVALID;

	private Intent receivedIntent = null;

	private TabHost tabHost;

	private int cDay, cMonth, cYear, tHour, tMin;
	boolean flagDateFrom, flagDateTo, flagTimeFrom, flagTimeTo;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.TRANSPARENT);
		setContentView(R.layout.manage_reminder);

		tabHost = getTabHost();
		tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
		tabHost.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.tab_bg));
		tabHost.addTab(tabHost.newTabSpec("Basic").setIndicator("Basic")
				.setContent(R.id.layout_tab_basic_details));
		tabHost.addTab(tabHost.newTabSpec("Timing").setIndicator("Timing")
				.setContent(R.id.layout_tab_time_details));
		tabHost.setCurrentTab(0);
		tabHost.setOnTabChangedListener(this);
		tabStyling();

		dataBridge = new DataBridge(this);

		txtActivityTitle = (TextView) findViewById(R.id.lblManageReminderActivityName);
		txtReminderName = (TextView) findViewById(R.id.txtReminderName);
		txtReminderDescription = (TextView) findViewById(R.id.txtReminderDescription);
		txtReminderRadii = (TextView) findViewById(R.id.txtRadius);
		txtReminderAddress = (TextView) findViewById(R.id.txtAddress);
		lblDistUnits = (TextView) findViewById(R.id.lblDistanceUnit);

		btn1 = (Button) findViewById(R.id.btnAction1);
		btn2 = (Button) findViewById(R.id.btnAction2);
		btn3 = (Button) findViewById(R.id.btnAction3);
		btnAddDateFrom = (Button) findViewById(R.id.btnAddDateFrom);
		btnAddTimeFrom = (Button) findViewById(R.id.btnAddTimeFrom);
		btnAddDateTo = (Button) findViewById(R.id.btnAddDateTo);
		btnAddTimeTo = (Button) findViewById(R.id.btnAddTimeTo);

		txtReminderName.requestFocus();
		chkOneTimeReminder = (CheckBox) findViewById(R.id.chkOneTimeReminder);
		chkAddTimeInfo = (CheckBox) findViewById(R.id.chkAddTime);
		chkAllDayEvent = (CheckBox) findViewById(R.id.chkAllDayEvent);
		txtMsg = (TextView) findViewById(R.id.txtMsg);

		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btnAddDateFrom.setOnClickListener(this);
		btnAddTimeFrom.setOnClickListener(this);
		btnAddDateTo.setOnClickListener(this);
		btnAddTimeTo.setOnClickListener(this);

		chkOneTimeReminder.setOnCheckedChangeListener(this);
		chkAddTimeInfo.setOnCheckedChangeListener(this);
		chkAllDayEvent.setOnCheckedChangeListener(this);

		receivedIntent = getIntent();
		if (null != receivedIntent) {
			Bundle bundle = receivedIntent
					.getBundleExtra(ApplicationSettings.bundleReminderBO);
			if (null != bundle) {

				String key = ApplicationSettings.keyExchangeReminderBO;
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					Log.d(TAG, "Retreiving object with StaticKey " + key);

				if (bundle.containsKey(key)) {
					reminderBO = (ReminderBO) bundle.getSerializable(key);

					if (SharedApplicationSettings.MODE_DEVELOPMENT)
						Log.d(TAG,
								"Displaying Reminder " + reminderBO.toString());

					if (reminderBO.getID() != ApplicationSettings.INAVLID_Reminder_BO_ID) {
						// Verify if this BO actually still exists
						String[] selectionArgs = { reminderBO.getID() + "" };
						List<ReminderBO> checkBOList = dataBridge
								.getReminders(
										LocationReminderDataModel.Reminders._ID
												+ " =?", selectionArgs);
						if (checkBOList.size() > 0) {
							ReminderBO checkBO = checkBOList.get(0);
							if (!checkBO.equals(reminderBO)
									&& checkBO.getID() == reminderBO.getID()) {
								if (SharedApplicationSettings.MODE_DEVELOPMENT) {
									Log.d(TAG, "Original Reminder\n"
											+ reminderBO.toString());
									Log.d(TAG,
											"Updated Reminder\n"
													+ checkBO.toString());
								}
								checkBO.setShortID(reminderBO.getShortID());
								reminderBO = checkBO;
								if (SharedApplicationSettings.MODE_DEVELOPMENT){
									Log.d(TAG, "Reminder updated");
								Toast.makeText(
										this,
										getResources().getString(
												R.string.MSG_ReminderUpdated),
										Toast.LENGTH_SHORT).show();
								}
							}
						} else {
							if (SharedApplicationSettings.MODE_DEVELOPMENT) {
								Log.d(TAG,
										"The requested BO does not exist, hence exiting.");

								Toast.makeText(
										this,
										getResources()
												.getString(
														R.string.MSG_ReminderNoLongerExists),
										Toast.LENGTH_LONG).show();
							}
							setResult(RESULT_CANCELED);
							startActivity(new Intent(getApplicationContext(),
									MapOverlayActivity.class));
							finish();
						}
					}
					receivedCRUDMode = receivedIntent.getIntExtra(
							ApplicationSettings.CRUD_MODE,
							ApplicationSettings.CRUD_INVALID);
					updateUIPerCRUDMode();
				} else {

					if (SharedApplicationSettings.MODE_DEVELOPMENT)
						Log.d(TAG, "Unable to find reminderBO for StaticKey "
								+ key);
					setResult(RESULT_CANCELED);
					finish();
				}
			} else {
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					Log.d(TAG, "Null Bundle: No Reminder details passed");
				setResult(RESULT_CANCELED);
				finish();
			}
		} else {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "Null receivedIntent : No Reminder details passed");
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
	}

	private void tabStyling() {
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = ApplicationSettings.tab_height;
			TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i)
					.findViewById(android.R.id.title);
			if (null != tv) {
				//tv.setTextColor(getResources().getColorStateList((R.color.tab_text_selector)));
			}
		}
		changeTextStyleOnselectedTab();
	}

	private String getDisplayDate(Calendar date) {
		if (null != date)
			return getDisplayDate(date.getTime());
		else
			return ApplicationSettings.blankDate;

	}

	private String getDisplayDate(Date date) {
		String retDate = "";
		if (chkAddTimeInfo.isChecked()) {
			SimpleDateFormat format = new SimpleDateFormat("EEE, MMM dd yyyy");
			if (null != date)
				retDate = format.format(date);
			else
				retDate = ApplicationSettings.blankDate;
		} else {
			retDate = ApplicationSettings.blankDate;
		}
		return retDate;
	}

	private String getDisplayTime(Calendar date) {

		return getDisplayTime(date.getTime());
	}

	private String getDisplayTime(Date date) {
		String retTime = "";
		if (chkAddTimeInfo.isChecked()) {
			if (!chkAllDayEvent.isChecked()) {
				if (null != date) {
					SimpleDateFormat format = new SimpleDateFormat("KK:mm aa");
					retTime = format.format(date);
				} else
					retTime = ApplicationSettings.blankTime;
			} else {
				retTime = ApplicationSettings.zeroTime;
			}

		} else {
			retTime = "--/--/----";
		}
		return retTime;
	}

	private void updateUIPerCRUDMode() {

		// select the unit for distance
		String[] imperialCountryList = getResources().getStringArray(
				R.array.listCountryImperialSystem);
		String currentCountry = getResources().getConfiguration().locale
				.getDisplayCountry();

		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Current Country " + currentCountry);

		reminderBO.setImperialMetrics(false);
		for (int i = 0; i < imperialCountryList.length; i++) {
			if (imperialCountryList[i].equalsIgnoreCase(currentCountry)) {
				reminderBO.setImperialMetrics(true);
				break;
			}
		}

		if (reminderBO.isImperialMetrics())
			lblDistUnits.setText("MI");
		else
			lblDistUnits.setText("KM");

		switch (receivedCRUDMode) {
		case ApplicationSettings.CRUD_CREATE: {
			setTitle("Create Reminder");
			txtActivityTitle.setText(getTitle());

			if (null != reminderBO.getReminderName())
				txtReminderName.setText(reminderBO.getReminderName());
			else
				txtReminderName.setText("");

			if (null != reminderBO.getReminderDescription())
				txtReminderDescription.setText(reminderBO
						.getReminderDescription());
			else
				txtReminderDescription.setText("");

			if (0 != reminderBO.getReminderRadii())
				txtReminderRadii.setText(reminderBO.getReminderRadii() + "");
			else
				txtReminderRadii.setText("");

			txtReminderAddress.setText(reminderBO.getReminderAddress());
			chkOneTimeReminder.setChecked(reminderBO.isOneTimeReminder());

			btn1.setText("Save");
			btn1.setVisibility(View.VISIBLE);
			btn3.setText("Cancel");
			btn3.setVisibility(View.VISIBLE);
			btn2.setVisibility(View.INVISIBLE);

			txtReminderName.setEnabled(true);
			txtReminderDescription.setEnabled(true);
			txtReminderRadii.setEnabled(true);

			if (null == dateTimeFrom) {
				dateTimeFrom = Calendar.getInstance();
				if (null != reminderBO.getFromDate())
					dateTimeFrom.setTime(reminderBO.getFromDate());
			}
			if (null == dateTimeTo) {
				dateTimeTo = Calendar.getInstance();
				if (null != reminderBO.getToDate())
					dateTimeTo.setTime(reminderBO.getToDate());
				else
					dateTimeTo.add(Calendar.DAY_OF_MONTH, 1);
			}

			chkAddTimeInfo.setEnabled(true);
			chkOneTimeReminder.setEnabled(true);
			chkAddTimeInfo.setChecked(reminderBO.isTimeInfoPresent());
			chkAllDayEvent.setChecked(reminderBO.isAllDayEvent());

			if (null != reminderBO.getFromDate()) {
				btnAddDateFrom
						.setText(getDisplayDate(reminderBO.getFromDate()));
				btnAddTimeFrom
						.setText(getDisplayTime(reminderBO.getFromDate()));
			} else {
				btnAddDateFrom.setText(getDisplayDate(dateTimeFrom));
				btnAddTimeFrom.setText(getDisplayTime(dateTimeFrom));
			}

			if (null != reminderBO.getToDate()) {
				btnAddDateTo.setText(getDisplayDate(reminderBO.getToDate()));
				btnAddTimeTo.setText(getDisplayTime(reminderBO.getToDate()));
			} else {
				btnAddDateTo.setText(getDisplayDate(dateTimeTo));
				btnAddTimeTo.setText(getDisplayTime(dateTimeTo));
			}

			break;
		}
		case ApplicationSettings.CRUD_DELETE: {
			setTitle("Delete Reminder");
			txtActivityTitle.setText(getTitle());

			txtReminderName.setText(reminderBO.getReminderName());
			txtReminderDescription.setText(reminderBO.getReminderDescription());
			txtReminderRadii.setText(reminderBO.getReminderRadii() + "");
			txtReminderAddress.setText(reminderBO.getReminderAddress());

			if (null == dateTimeFrom) {
				dateTimeFrom = Calendar.getInstance();
				if (null != reminderBO.getFromDate())
					dateTimeFrom.setTime(reminderBO.getFromDate());
			}
			if (null == dateTimeTo) {
				dateTimeTo = Calendar.getInstance();
				if (null != reminderBO.getToDate())
					dateTimeTo.setTime(reminderBO.getToDate());
				else
					dateTimeTo.add(Calendar.DAY_OF_MONTH, 1);
			}

			chkOneTimeReminder.setChecked(reminderBO.isOneTimeReminder());
			chkAddTimeInfo.setChecked(reminderBO.isTimeInfoPresent());
			chkAllDayEvent.setChecked(reminderBO.isAllDayEvent());

			if (null != reminderBO.getFromDate()) {
				btnAddDateFrom
						.setText(getDisplayDate(reminderBO.getFromDate()));
				btnAddTimeFrom
						.setText(getDisplayTime(reminderBO.getFromDate()));
			} else {
				btnAddDateFrom.setText(getDisplayDate(dateTimeFrom));
				btnAddTimeFrom.setText(getDisplayTime(dateTimeFrom));
			}

			if (null != reminderBO.getToDate()) {
				btnAddDateTo.setText(getDisplayDate(reminderBO.getToDate()));
				btnAddTimeTo.setText(getDisplayTime(reminderBO.getToDate()));
			} else {
				btnAddDateTo.setText(getDisplayDate(dateTimeTo));
				btnAddTimeTo.setText(getDisplayTime(dateTimeTo));
			}

			btn1.setText("Delete");
			btn1.setVisibility(View.VISIBLE);
			btn3.setText("Cancel");
			btn3.setVisibility(View.VISIBLE);
			btn2.setVisibility(View.INVISIBLE);

			txtReminderName.setEnabled(false);
			txtReminderDescription.setEnabled(false);
			txtReminderRadii.setEnabled(false);
			chkOneTimeReminder.setEnabled(false);
			chkAddTimeInfo.setEnabled(false);

			chkAddTimeInfo.setEnabled(false);
			chkAllDayEvent.setEnabled(false);
			btnAddDateFrom.setEnabled(false);
			btnAddDateTo.setEnabled(false);
			btnAddTimeFrom.setEnabled(false);
			btnAddTimeTo.setEnabled(false);

			break;
		}
		case ApplicationSettings.CRUD_EDIT: {
			setTitle("Update Reminder");
			txtActivityTitle.setText(getTitle());

			txtReminderName.setText(reminderBO.getReminderName());
			txtReminderDescription.setText(reminderBO.getReminderDescription());
			txtReminderRadii.setText(reminderBO.getReminderRadii() + "");
			txtReminderAddress.setText(reminderBO.getReminderAddress());

			if (null == dateTimeFrom) {
				dateTimeFrom = Calendar.getInstance();
				if (null != reminderBO.getFromDate())
					dateTimeFrom.setTime(reminderBO.getFromDate());
			}
			if (null == dateTimeTo) {
				dateTimeTo = Calendar.getInstance();
				if (null != reminderBO.getToDate())
					dateTimeTo.setTime(reminderBO.getToDate());
				else
					dateTimeTo.add(Calendar.DAY_OF_MONTH, 1);
			}

			chkOneTimeReminder.setChecked(reminderBO.isOneTimeReminder());
			chkAddTimeInfo.setChecked(reminderBO.isTimeInfoPresent());
			chkAllDayEvent.setChecked(reminderBO.isAllDayEvent());

			if (null != reminderBO.getFromDate()) {
				btnAddDateFrom
						.setText(getDisplayDate(reminderBO.getFromDate()));
				btnAddTimeFrom
						.setText(getDisplayTime(reminderBO.getFromDate()));
			} else {
				btnAddDateFrom.setText(getDisplayDate(dateTimeFrom));
				btnAddTimeFrom.setText(getDisplayTime(dateTimeFrom));
			}

			if (null != reminderBO.getToDate()) {
				btnAddDateTo.setText(getDisplayDate(reminderBO.getToDate()));
				btnAddTimeTo.setText(getDisplayTime(reminderBO.getToDate()));
			} else {
				btnAddDateTo.setText(getDisplayDate(dateTimeTo));
				btnAddTimeTo.setText(getDisplayTime(dateTimeTo));
			}

			btn1.setText("Save");
			btn1.setVisibility(View.VISIBLE);
			btn3.setText("Cancel");
			btn3.setVisibility(View.VISIBLE);
			btn2.setText("Delete");
			btn2.setVisibility(View.VISIBLE);

			txtReminderName.setEnabled(true);
			txtReminderDescription.setEnabled(true);
			txtReminderRadii.setEnabled(true);

			chkOneTimeReminder.setEnabled(true);
			chkAddTimeInfo.setEnabled(true);

			if (chkAddTimeInfo.isChecked()) {
				btnAddDateFrom.setEnabled(true);
				btnAddDateTo.setEnabled(true);
				chkAllDayEvent.setEnabled(true);
				if (chkAllDayEvent.isChecked()) {
					btnAddTimeFrom.setEnabled(false);
					btnAddTimeTo.setEnabled(false);
				} else {
					btnAddTimeFrom.setEnabled(true);
					btnAddTimeTo.setEnabled(true);
				}
			} else {
				btnAddDateFrom.setEnabled(false);
				btnAddDateTo.setEnabled(false);
				chkAllDayEvent.setEnabled(false);
				btnAddTimeTo.setEnabled(false);
				btnAddTimeFrom.setEnabled(false);
			}

			break;
		}
		case ApplicationSettings.CRUD_NOTIFY: {
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(reminderBO.getShortID());
			setTitle("Reminder Notification");
			txtActivityTitle.setText(getTitle());

			txtReminderName.setText(reminderBO.getReminderName());
			txtReminderDescription.setText(reminderBO.getReminderDescription());
			txtReminderRadii.setText(reminderBO.getReminderRadii() + "");
			txtReminderAddress.setText(reminderBO.getReminderAddress());

			if (null == dateTimeFrom) {
				dateTimeFrom = Calendar.getInstance();
				if (null != reminderBO.getFromDate())
					dateTimeFrom.setTime(reminderBO.getFromDate());
			}
			if (null == dateTimeTo) {
				dateTimeTo = Calendar.getInstance();
				if (null != reminderBO.getToDate())
					dateTimeTo.setTime(reminderBO.getToDate());
				else
					dateTimeTo.add(Calendar.DAY_OF_MONTH, 1);
			}

			chkOneTimeReminder.setChecked(reminderBO.isOneTimeReminder());
			chkAddTimeInfo.setChecked(reminderBO.isTimeInfoPresent());
			chkAllDayEvent.setChecked(reminderBO.isAllDayEvent());

			if (null != reminderBO.getFromDate()) {
				btnAddDateFrom
						.setText(getDisplayDate(reminderBO.getFromDate()));
				btnAddTimeFrom
						.setText(getDisplayTime(reminderBO.getFromDate()));
			} else {
				btnAddDateFrom.setText(getDisplayDate(dateTimeFrom));
				btnAddTimeFrom.setText(getDisplayTime(dateTimeFrom));
			}

			if (null != reminderBO.getToDate()) {
				btnAddDateTo.setText(getDisplayDate(reminderBO.getToDate()));
				btnAddTimeTo.setText(getDisplayTime(reminderBO.getToDate()));
			} else {
				btnAddDateTo.setText(getDisplayDate(dateTimeTo));
				btnAddTimeTo.setText(getDisplayTime(dateTimeTo));
			}

			btn1.setText("OK");
			btn1.setVisibility(View.VISIBLE);

			if (!reminderBO.isOneTimeReminder()) {
				btn2.setText("Delete");
				btn2.setVisibility(View.VISIBLE);
				btn3.setText("Edit");
				btn3.setVisibility(View.VISIBLE);
			} else{
				btn2.setVisibility(View.INVISIBLE);
				btn3.setVisibility(View.INVISIBLE);
			}
			
			txtReminderName.setEnabled(false);
			txtReminderDescription.setEnabled(false);
			txtReminderRadii.setEnabled(false);
			chkOneTimeReminder.setEnabled(false);

			chkAddTimeInfo.setEnabled(false);
			chkAllDayEvent.setEnabled(false);
			btnAddDateFrom.setEnabled(false);
			btnAddDateTo.setEnabled(false);
			btnAddTimeFrom.setEnabled(false);
			btnAddTimeTo.setEnabled(false);

			break;
		}
		case ApplicationSettings.CRUD_VIEW: {
			setTitle("View Reminder");
			txtActivityTitle.setText(getTitle());

			txtReminderName.setText(reminderBO.getReminderName());
			txtReminderDescription.setText(reminderBO.getReminderDescription());
			txtReminderRadii.setText(reminderBO.getReminderRadii() + "");
			txtReminderAddress.setText(reminderBO.getReminderAddress());

			if (null == dateTimeFrom) {
				dateTimeFrom = Calendar.getInstance();
				if (null != reminderBO.getFromDate())
					dateTimeFrom.setTime(reminderBO.getFromDate());
			}
			if (null == dateTimeTo) {
				dateTimeTo = Calendar.getInstance();
				if (null != reminderBO.getToDate())
					dateTimeTo.setTime(reminderBO.getToDate());
				else
					dateTimeTo.add(Calendar.DAY_OF_MONTH, 1);
			}

			chkOneTimeReminder.setChecked(reminderBO.isOneTimeReminder());
			chkAddTimeInfo.setChecked(reminderBO.isTimeInfoPresent());
			chkAllDayEvent.setChecked(reminderBO.isAllDayEvent());

			if (null != reminderBO.getFromDate()) {
				btnAddDateFrom
						.setText(getDisplayDate(reminderBO.getFromDate()));
				btnAddTimeFrom
						.setText(getDisplayTime(reminderBO.getFromDate()));
			} else {
				btnAddDateFrom.setText(getDisplayDate(dateTimeFrom));
				btnAddTimeFrom.setText(getDisplayTime(dateTimeFrom));
			}

			if (null != reminderBO.getToDate()) {
				btnAddDateTo.setText(getDisplayDate(reminderBO.getToDate()));
				btnAddTimeTo.setText(getDisplayTime(reminderBO.getToDate()));
			} else {
				btnAddDateTo.setText(getDisplayDate(dateTimeTo));
				btnAddTimeTo.setText(getDisplayTime(dateTimeTo));
			}

			btn1.setText("OK");
			btn1.setVisibility(View.VISIBLE);

			if (!reminderBO.getReminderName().equalsIgnoreCase(
					ApplicationSettings.tracker_reminder_name)) {
				btn3.setText("Edit");
				btn3.setVisibility(View.VISIBLE);
				btn2.setText("Delete");
				btn2.setVisibility(View.VISIBLE);
			} else {
				if (LocationUpdateUtils.isAppUnlocked(getApplicationContext())) {
					btn2.setText("Delete");
					btn2.setVisibility(View.VISIBLE);
					btn3.setVisibility(View.INVISIBLE);
				} else {
					btn2.setVisibility(View.INVISIBLE);
					btn3.setVisibility(View.INVISIBLE);
				}
			}

			txtReminderName.setEnabled(false);
			txtReminderDescription.setEnabled(false);
			txtReminderRadii.setEnabled(false);
			chkOneTimeReminder.setEnabled(false);

			chkAddTimeInfo.setEnabled(false);
			chkAllDayEvent.setEnabled(false);
			btnAddDateFrom.setEnabled(false);
			btnAddDateTo.setEnabled(false);
			btnAddTimeFrom.setEnabled(false);
			btnAddTimeTo.setEnabled(false);

			break;
		}
		default: {
			setResult(RESULT_CANCELED);
			finish();
			break;
		}
		}
	}

	private void saveReminderUIDetailsToReminderBO() {
		reminderBO.setReminderName(txtReminderName.getText().toString());
		reminderBO.setReminderDescription(txtReminderDescription.getText()
				.toString());
		reminderBO.setOneTimeReminder(chkOneTimeReminder.isChecked());

		reminderBO.setTimeInfoPresent(chkAddTimeInfo.isChecked());
		reminderBO.setAllDayEvent(chkAllDayEvent.isChecked());

		reminderBO.setFromDate(dateTimeFrom);
		reminderBO.setToDate(dateTimeTo);

		try {
			reminderBO.setReminderRadii(Float.parseFloat(txtReminderRadii
					.getText().toString()));
		} catch (Exception exc) {
			Toast.makeText(this,
					"Setting Radius=1" + lblDistUnits.getText().toString(),
					Toast.LENGTH_SHORT).show();
			reminderBO.setReminderRadii(1);
		}
	}

	/**
	 * When buttons are clicked
	 */
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnAction1: {
			switch (receivedCRUDMode) {
			case ApplicationSettings.CRUD_CREATE: {
				if (validateEnteredData()) {
					saveReminderUIDetailsToReminderBO();

					// check if an Reminder for same location previously exists
					String selection = LocationReminderDataModel.Reminders.Reminder_LATITUDEE6
							+ "=? AND "
							+ LocationReminderDataModel.Reminders.Reminder_LONGITUDEE6
							+ "=?";
					String[] selectionArgs = {
							reminderBO.getReminderLatitudeE6() + "",
							reminderBO.getReminderLongitudeE6() + "" };
					List<ReminderBO> prevReminderBOs = dataBridge.getReminders(
							selection, selectionArgs);
					if (SharedApplicationSettings.MODE_DEVELOPMENT)
						Log.d(TAG, "Found " + prevReminderBOs.size()
								+ " Reminders that match the given criterion");
					if (prevReminderBOs.size() > 0) {
						if (SharedApplicationSettings.MODE_DEVELOPMENT)
							Log.d(TAG, "Show Dialog for options");
						final AlertDialog.Builder builder = new AlertDialog.Builder(
								this);
						// pass the BO at position 0 to be updated

						builder.setMessage(getResources().getString(
								R.string.MSG_DuplicateReminder));
						builder.setCancelable(false);
						builder.setNegativeButton("Update Existing",
								new DialogUpdateExistingReminderHandler(this,
										prevReminderBOs));
						ArrayList<ReminderBO> boList = new ArrayList<ReminderBO>();
						boList.add(reminderBO);
						builder.setPositiveButton("Create New",
								new DialogUpdateExistingReminderHandler(this,
										boList));
						builder.setNeutralButton("Quit",
								new DialogUpdateExistingReminderHandler(this,
										null));
						final AlertDialog Reminder = builder.create();
						Reminder.show();
					} else {
						LocationUpdateUtils
								.addUpdateReminderBOAndEndActivityAppropriately(
										this, reminderBO);
					}
				}
				break;
			}
			case ApplicationSettings.CRUD_EDIT: {
				if (validateEnteredData()) {
					saveReminderUIDetailsToReminderBO();
					LocationUpdateUtils
							.addUpdateReminderBOAndEndActivityAppropriately(
									this, reminderBO);
				}
				break;
			}
			case ApplicationSettings.CRUD_DELETE: {
				LocationUpdateUtils
						.deleteReminderBOCancelPendingIntentAndEndActivityAppropriately(
								this, reminderBO, receivedCRUDMode!=ApplicationSettings.CRUD_NOTIFY);
				break;
			}
			case ApplicationSettings.CRUD_NOTIFY: {
				if (reminderBO.isOneTimeReminder()) {
					LocationUpdateUtils
							.deleteReminderBOCancelPendingIntentAndEndActivityAppropriately(
									this, reminderBO,receivedCRUDMode!=ApplicationSettings.CRUD_NOTIFY);
				} else {
					setResult(RESULT_OK);
					this.finish();
				}

				break;
			}
			case ApplicationSettings.CRUD_VIEW: {
				setResult(RESULT_OK);
				this.finish();
				break;
			}
			default:
				break;
			}
			break;
		}
		case R.id.btnAction2: {
			switch (receivedCRUDMode) {
			case ApplicationSettings.CRUD_CREATE: {

				break;
			}
			case ApplicationSettings.CRUD_EDIT: {
				LocationUpdateUtils
						.deleteReminderBOCancelPendingIntentAndEndActivityAppropriately(
								this, reminderBO,receivedCRUDMode!=ApplicationSettings.CRUD_NOTIFY);
				break;
			}
			case ApplicationSettings.CRUD_DELETE: {
				LocationUpdateUtils
						.deleteReminderBOCancelPendingIntentAndEndActivityAppropriately(
								this, reminderBO,receivedCRUDMode!=ApplicationSettings.CRUD_NOTIFY);
				break;
			}
			case ApplicationSettings.CRUD_NOTIFY: {
				LocationUpdateUtils
						.deleteReminderBOCancelPendingIntentAndEndActivityAppropriately(
								this, reminderBO,receivedCRUDMode!=ApplicationSettings.CRUD_NOTIFY);
				break;
			}
			case ApplicationSettings.CRUD_VIEW: {
				LocationUpdateUtils
						.deleteReminderBOCancelPendingIntentAndEndActivityAppropriately(
								this, reminderBO,receivedCRUDMode!=ApplicationSettings.CRUD_NOTIFY);
				break;
			}
			default:
				break;
			}
			break;
		}
		case R.id.btnAction3: {
			switch (receivedCRUDMode) {
			case ApplicationSettings.CRUD_CREATE: {
				setResult(RESULT_CANCELED);
				this.finish();
				break;
			}
			case ApplicationSettings.CRUD_EDIT: {
				setResult(RESULT_CANCELED);
				this.finish();
				break;
			}
			case ApplicationSettings.CRUD_DELETE: {
				setResult(RESULT_CANCELED);
				this.finish();
				break;
			}
			case ApplicationSettings.CRUD_NOTIFY: {
				receivedCRUDMode = ApplicationSettings.CRUD_EDIT;
				updateUIPerCRUDMode();
				break;
			}
			case ApplicationSettings.CRUD_VIEW: {
				receivedCRUDMode = ApplicationSettings.CRUD_EDIT;
				updateUIPerCRUDMode();
				break;
			}
			default:
				break;
			}
			break;
		}
		case R.id.btnAddDateFrom: {
			flagDateFrom = true;
			flagDateTo = false;
			showDateDialog(dateTimeFrom);
			break;
		}
		case R.id.btnAddDateTo: {
			flagDateTo = true;
			flagDateFrom = false;
			showDateDialog(dateTimeTo);
			break;
		}
		case R.id.btnAddTimeFrom: {
			flagTimeTo = false;
			flagTimeFrom = true;
			showTimeDialog(dateTimeFrom);
			break;
		}
		case R.id.btnAddTimeTo: {
			flagTimeTo = true;
			flagTimeFrom = false;
			showTimeDialog(dateTimeTo);
			break;
		}
		default:
			break;
		}
	}

	private void showDateDialog(Calendar date) {
		cDay = date.get(Calendar.DAY_OF_MONTH);
		cMonth = date.get(Calendar.MONTH);
		cYear = date.get(Calendar.YEAR);
		showDialog(ApplicationSettings.DIALOG_CALENDAR_ID);
	}

	private void showTimeDialog(Calendar date) {
		tHour = date.get(Calendar.HOUR_OF_DAY);
		tMin = date.get(Calendar.MINUTE);
		showDialog(ApplicationSettings.DIALOG_TIME_ID);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case ApplicationSettings.DIALOG_CALENDAR_ID: {
			DatePickerDialog dateDialog = (DatePickerDialog) dialog;
			dateDialog.updateDate(cYear, cMonth, cDay);
			break;
		}
		case ApplicationSettings.DIALOG_TIME_ID: {
			TimePickerDialog timeDialog = (TimePickerDialog) dialog;
			timeDialog.updateTime(tHour, tMin);
			break;
		}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog retVal = null;
		switch (id) {
		case ApplicationSettings.DIALOG_CALENDAR_ID: {
			retVal = new DatePickerDialog(this, this, cYear, cMonth, cDay);
			break;
		}
		case ApplicationSettings.DIALOG_TIME_ID: {
			retVal = new TimePickerDialog(this, this, tHour, tMin, false);
			break;
		}
		}
		return retVal;
	}

	private boolean validateEnteredData() {
		boolean flag = false;

		switch (receivedCRUDMode) {
		case ApplicationSettings.CRUD_CREATE:
		case ApplicationSettings.CRUD_EDIT: {
			// Reminder Name should be specified
			if (null != txtReminderName.getText().toString()
					&& txtReminderName.getText().toString().trim().length() > 0)
				flag = true;
			else
				Toast.makeText(
						this,
						getResources()
								.getString(R.string.MSG_InvalidReminder_1),
						Toast.LENGTH_SHORT).show();
		}
			break;
		default:
			flag = true;
		}
		return flag;
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.chkOneTimeReminder: {
			if (!isChecked) {
				if (!LocationUpdateUtils.isAppUnlocked(getApplicationContext())) {
					chkOneTimeReminder.setChecked(true);
					LocationUpdateUtils.showPurchaseDialog(
							this,
							getResources().getString(
									R.string.MSG_TitleUnsupportedOperation),
							getResources().getString(
									R.string.MSG_UnsupportedRecurringReminder)
									+ "\n"
									+ getResources().getString(
											R.string.MSG_StdPurchasMessage),
							false, false);
				}
			}
			break;
		}
		case R.id.chkAddTime: {
			if (isChecked) {
				btnAddDateFrom.setEnabled(true);
				btnAddDateTo.setEnabled(true);
				btnAddTimeFrom.setEnabled(true);
				btnAddTimeTo.setEnabled(true);
				chkAllDayEvent.setEnabled(true);
				txtMsg.setText("");
			} else {
				btnAddDateFrom.setEnabled(false);
				btnAddDateTo.setEnabled(false);
				btnAddTimeFrom.setEnabled(false);
				btnAddTimeTo.setEnabled(false);
				chkAllDayEvent.setEnabled(false);
				txtMsg.setText(getString(R.string.MSG_ReminderNoTimingInfo));
				if (chkAllDayEvent.isChecked())
					chkAllDayEvent.setChecked(false);
			}

			saveReminderUIDetailsToReminderBO();
			updateUIPerCRUDMode();
			break;
		}
		case R.id.chkAllDayEvent: {
			if (isChecked) {
				btnAddTimeFrom.setEnabled(false);
				btnAddTimeTo.setEnabled(false);

				dateTimeFrom.set(Calendar.HOUR_OF_DAY, 0);
				dateTimeFrom.set(Calendar.MINUTE, 0);
				dateTimeFrom.set(Calendar.MILLISECOND, 0);

				dateTimeTo.set(Calendar.HOUR_OF_DAY, 0);
				dateTimeTo.set(Calendar.MINUTE, 0);
				dateTimeTo.set(Calendar.MILLISECOND, 0);
			} else {
				btnAddTimeFrom.setText(getDisplayTime(dateTimeFrom));
				btnAddTimeTo.setText(getDisplayTime(dateTimeTo));
				btnAddTimeFrom.setEnabled(true);
				btnAddTimeTo.setEnabled(true);
			}

			saveReminderUIDetailsToReminderBO();
			updateUIPerCRUDMode();
			break;
		}
		}

	}

	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Calendar newDate = Calendar.getInstance();

		if (flagDateFrom) {
			dateTimeFrom.set(Calendar.MONTH, monthOfYear);
			dateTimeFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			dateTimeFrom.set(Calendar.YEAR, year);

			if (dateTimeTo.before(dateTimeFrom)) {
				dateTimeTo.setTimeInMillis(dateTimeFrom.getTimeInMillis()
						+ (24 * 60 * 60 * 1000));
			}
		}

		if (flagDateTo) {
			newDate.set(Calendar.HOUR_OF_DAY,
					dateTimeTo.get(Calendar.HOUR_OF_DAY));
			newDate.set(Calendar.MINUTE, dateTimeTo.get(Calendar.MINUTE));
			newDate.set(Calendar.SECOND, dateTimeTo.get(Calendar.SECOND));
			newDate.set(Calendar.MILLISECOND,
					dateTimeFrom.get(Calendar.MILLISECOND));
			newDate.set(Calendar.MONTH, monthOfYear);
			newDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			newDate.set(Calendar.YEAR, year);

			Calendar currentDate = Calendar.getInstance();

			if (newDate.after(currentDate)) {
				if (newDate.after(dateTimeFrom)) {
					dateTimeTo.set(Calendar.MONTH, monthOfYear);
					dateTimeTo.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					dateTimeTo.set(Calendar.YEAR, year);
				} else {
					Toast.makeText(
							this,
							getString(R.string.MSG_ReminderToFromOutOfSequence),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, getString(R.string.MSG_ReminderInPast),
						Toast.LENGTH_SHORT).show();
			}
		}
		removeDialog(ApplicationSettings.DIALOG_CALENDAR_ID);

		saveReminderUIDetailsToReminderBO();
		updateUIPerCRUDMode();

	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		Calendar newDate = Calendar.getInstance();

		if (flagTimeFrom) {
			dateTimeFrom.set(Calendar.HOUR_OF_DAY, hourOfDay);
			dateTimeFrom.set(Calendar.MINUTE, minute);
			dateTimeFrom.set(Calendar.SECOND, 0);
			dateTimeFrom.set(Calendar.MILLISECOND, 0);

			if (dateTimeTo.before(dateTimeFrom)) {
				dateTimeTo.setTimeInMillis(dateTimeFrom.getTimeInMillis()
						+ (24 * 60 * 60 * 1000));
			}
		}

		if (flagTimeTo) {
			newDate.set(Calendar.DAY_OF_MONTH,
					dateTimeTo.get(Calendar.DAY_OF_MONTH));
			newDate.set(Calendar.MONTH, dateTimeTo.get(Calendar.MONTH));
			newDate.set(Calendar.YEAR, dateTimeTo.get(Calendar.YEAR));
			newDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
			newDate.set(Calendar.MINUTE, minute);
			newDate.set(Calendar.SECOND, 00);
			newDate.set(Calendar.MILLISECOND, 00);

			Calendar currentDate = Calendar.getInstance();

			if (newDate.after(currentDate)) {
				if (newDate.after(dateTimeFrom)) {
					dateTimeTo.set(Calendar.HOUR_OF_DAY, hourOfDay);
					dateTimeTo.set(Calendar.MINUTE, minute);
					dateTimeTo.set(Calendar.SECOND, 0);
					dateTimeTo.set(Calendar.MILLISECOND, 0);
				} else {
					Toast.makeText(
							this,
							getString(R.string.MSG_ReminderToFromOutOfSequence),
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast.makeText(this, getString(R.string.MSG_ReminderInPast),
						Toast.LENGTH_SHORT).show();
			}
		}
		removeDialog(ApplicationSettings.DIALOG_TIME_ID);

		saveReminderUIDetailsToReminderBO();
		updateUIPerCRUDMode();
	}

	public void onTabChanged(String arg0) {
		saveReminderUIDetailsToReminderBO();
		updateUIPerCRUDMode();
		changeTextStyleOnselectedTab();

	}

	private void changeTextStyleOnselectedTab() {
		TextView tv=null;
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = ApplicationSettings.tab_height;
			tv = (TextView) tabHost.getTabWidget().getChildAt(i)
					.findViewById(android.R.id.title);
			if (null != tv) {
				tv.setTextAppearance(this,
						ApplicationSettings.tab_text_style_unselected);
				tv.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
			}
		}
		tv = (TextView) tabHost.getCurrentTabView().findViewById(
				android.R.id.title);
		if (null != tv) {
			tv.setTextAppearance(this,
					ApplicationSettings.tab_text_style_selected);
			tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
		}
	}
}