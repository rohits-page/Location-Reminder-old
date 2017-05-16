package rcs.LocationReminder.Activity;

import java.util.HashMap;

import rcs.LocationReminder.LocationUpdateUtils;
import rcs.LocationReminder.MyCurrentLocationOverlay;
import rcs.LocationReminder.ProgressTask;
import rcs.LocationReminder.ProgressTask_SearchAddress;
import rcs.LocationReminder.ProgressThread;
import rcs.LocationReminder.R;
import rcs.LocationReminder.Data.DataBridge;
import rcs.LocationReminder.EventHandler.CleanRemindersActionHandler;
import rcs.LocationReminder.EventHandler.MultipleAddressFoundHandler;
import rcs.LocationReminder.Overlay.MapReminderOverlay;
import rcs.LocationReminder.Shared.SharedApplicationSettings;
import rcs.LocationReminder.general.ApplicationSettings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class MapOverlayActivity extends MapActivity implements OnKeyListener,
		OnCheckedChangeListener, android.view.View.OnClickListener {
	private final String TAG = this.getClass().getName();

	private MapView mapView;
	private AutoCompleteTextView txtSearchAddress;
	private ToggleButton btnCtrlMyLocation;
	private Button btnSearch;
	private Button btnPurchase;
	private MyCurrentLocationOverlay myCurrentLocationOverlay;
	private MapReminderOverlay mapReminderOverlay;

	private ProgressDialog progDialog;
	private ProgressThread progThread;
	private int typeProgressBar;
	private ProgressTask progressActivity;
	private Activity activity = this;
	private String searchAddress = "";

	// Acquire a reference to the system Location Manager
	LocationManager locationManager = null;
	String provider = null;

	// Handler on the main (UI) thread that will receive messages from the
	// second thread and update the progress.
	final Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			// Get the current value of the variable total from the message data
			// and update the progress bar.
			float total = msg.getData().getFloat("total");
			progDialog.setProgress((int) total);

			if (total < 0) {
				removeDialog(typeProgressBar);
				progThread
						.setProgressThreadState(ApplicationSettings.PROGRESS_THREAD_DONE);
			}

			if (total == ApplicationSettings.PROGRESS_BAR_MAX_VALUE
					&& progThread.getProgressThreadState() != ApplicationSettings.PROGRESS_THREAD_DONE) {
				removeDialog(typeProgressBar);
				txtSearchAddress.setText("");
				progThread
						.setProgressThreadState(ApplicationSettings.PROGRESS_THREAD_DONE);
				try {
					if (progressActivity.getTaskState() == ProgressTask.PROGRESS_TASK_DONE) {
						HashMap<String, GeoPoint> possibleAddresses = (HashMap<String, GeoPoint>) progressActivity
								.getTaskResult();

						CharSequence[] items = null;
						GeoPoint[] geoPoints = null;

						items = new CharSequence[0];
						geoPoints = new GeoPoint[0];
						if (possibleAddresses.size() > 0) {
							items = possibleAddresses.keySet().toArray(items);
							geoPoints = possibleAddresses.values().toArray(
									geoPoints);

							if (possibleAddresses.size() > 1) {
								Toast.makeText(
										getApplicationContext(),
										getResources().getString(
												R.string.MSG_MultipleAddress),
										Toast.LENGTH_SHORT);
								AlertDialog.Builder builder = new AlertDialog.Builder(
										getApplicationContext());
								builder.setTitle("Pick an address");
								builder.setItems(items,
										new MultipleAddressFoundHandler(
												activity, geoPoints, items,
												mapView));
								AlertDialog Reminder = builder.create();
								Reminder.show();
							} else {
								if (SharedApplicationSettings.MODE_DEVELOPMENT)
									Log.d(TAG,
											"Handler ::  handle selected address");
								LocationUpdateUtils.handleSelectedAddress(
										items[0].toString(), geoPoints[0],
										mapView, activity);
							}
						} else {
							// no address returned
							if (SharedApplicationSettings.MODE_DEVELOPMENT)
								Log.d(TAG,
										"Size 0: Address could not be located on Map. Please select another point of interest on the map or enter another address.");
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.MSG_AddressNotFound),
									Toast.LENGTH_LONG).show();
						}
					} else {

					}
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(
									R.string.MSG_AddressNotFound),
							Toast.LENGTH_LONG).show();
				}
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFormat(PixelFormat.TRANSPARENT);

		// Inflate our UI from its XML layout description.
		setContentView(R.layout.map);

		mapView = (MapView) findViewById(R.id.mapView);

		txtSearchAddress = (AutoCompleteTextView) findViewById(R.id.txtSearchAddress);
		txtSearchAddress.setOnKeyListener(this);

		btnCtrlMyLocation = (ToggleButton) findViewById(R.id.btnCtrlCurrentLocation);
		btnCtrlMyLocation.setOnCheckedChangeListener(this);

		btnSearch = (Button) findViewById(R.id.btnSearchAddress);
		btnSearch.setOnClickListener(this);

		btnPurchase = (Button) findViewById(R.id.btnPurchase);
		btnPurchase.setOnClickListener(this);

		new DataBridge(this);

		paintPurchaseButton();

		// Always class initLocationSettings before initMapSettings
		initLocationSettings();

		Drawable drawable = this.getResources().getDrawable(R.drawable.marker);
		mapReminderOverlay = new MapReminderOverlay(drawable, this);
		myCurrentLocationOverlay = new MyCurrentLocationOverlay(this, mapView,
				locationManager);

		initMapSettings();

		LocationUpdateUtils.syncAsPerTracker(this);
	}

	private void initLocationSettings() {
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		provider = LocationUpdateUtils.setProvider(locationManager);
		if (null != provider) {
			mapView.getController().animateTo(
					LocationUpdateUtils
							.convertLocationToGeoPoint(locationManager
									.getLastKnownLocation(provider)));
			mapView.invalidate();
		}
	}

	private void initMapSettings() {
		mapView.getController();

		mapView.setBuiltInZoomControls(true);
		mapView.displayZoomControls(true);
		mapView.getZoomButtonsController().setAutoDismissed(true);
		mapView.setSatellite(false);
		mapView.setClickable(true);
		mapView.setEnabled(true);

		// Add the MyCurrentOverlay First and then add the MapReminderOverla
		// When receiving events, the events will be received in reverse order
		mapView.getOverlays().clear();
		mapView.getOverlays().add(myCurrentLocationOverlay);
		mapView.getOverlays().add(mapReminderOverlay);

		handleMyLocationSwitch();
		myCurrentLocationOverlay.enableCompass();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Activity resuming");
		// Always class initLocationSettings before initMapSettings
		paintPurchaseButton();
		initLocationSettings();
		initMapSettings();

	}

	private void paintPurchaseButton() {
		if (LocationUpdateUtils.isAppUnlocked(getApplicationContext())) {
			btnPurchase.setVisibility(View.INVISIBLE);
			btnPurchase.setText("");
		} else {
			btnPurchase.setVisibility(View.VISIBLE);
			btnPurchase.setText("Purchase");
		}
	}

	private void handleMyLocationSwitch() {
		if (btnCtrlMyLocation.isChecked()) {
			if (null != provider) {
				myCurrentLocationOverlay.enableMyLocation();
				Toast.makeText(this,
						getResources().getString(R.string.MSG_MyLocationON),
						Toast.LENGTH_SHORT).show();
			} else {
				LocationUpdateUtils
						.buildReminderMessageNoLocationProviderEnabled(this);
				btnCtrlMyLocation.setChecked(false);
			}

		} else
			myCurrentLocationOverlay.disableMyLocation();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Activity pausing");
		myCurrentLocationOverlay.disableCompass();
		myCurrentLocationOverlay.disableMyLocation();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * For the Search Address Box
	 */
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		boolean retVal = false;
		switch (keyCode) {
		case (KeyEvent.KEYCODE_ENTER): {
			searchForEnteredAddress();
			// txtSearchAddress.setText("");
			break;
		}
		}// end of switch
		return retVal;
	}

	private void searchForEnteredAddress() {
		String selectedAddress = txtSearchAddress.getText().toString();
		if (null != selectedAddress && selectedAddress.length() > 0)
			selectedAddress = selectedAddress.trim();

		if (null != selectedAddress && selectedAddress.length() > 0) {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "Searching for " + selectedAddress);
			typeProgressBar = ApplicationSettings.PROGRESS_DIALOG_SPINNER;// Spinner
			searchAddress = selectedAddress;
			showDialog(typeProgressBar);
		} else
			txtSearchAddress.setText("");

	}

	/**
	 * For the Toggle Button
	 */
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		handleMyLocationSwitch();
	}

	@Override
	/**
	 * Will be called when an activity that is launched from this activity returns with a result
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ApplicationSettings.REQUEST_CODE_CREATE_Reminder: {
			switch (resultCode) {
			case RESULT_OK: {
				break;
			}
			case RESULT_CANCELED: {
				break;
			}
			default:
				break;
			}
		}
		default:
			break;
		}
	}

	/**
	 * When search button is clicked, it will generate OnClick event
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.btnSearchAddress): {
			searchForEnteredAddress();
			break;
		}
		case (R.id.btnPurchase): {
			LocationUpdateUtils.showPurchaseDialog(this, getResources()
					.getString(R.string.MSG_TitleConfirmPurchase),
					getResources().getString(R.string.MSG_StdPurchasMessage),
					false, false);
			break;
		}
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case R.id.menuViewReminders: {
			intent.setAction(getString(R.string.intent_reminder_list));
			startActivityForResult(intent,
					ApplicationSettings.REQUEST_CODE_Reminder_LIST);
			return true;
		}
		case R.id.menuHelp: {
			Toast.makeText(this,
					getResources().getString(R.string.MSG_HelpWarning),
					Toast.LENGTH_SHORT).show();
			intent.setAction(getResources().getString(R.string.intent_help));
			startActivity(intent);
			return true;
		}
		case R.id.menuQuit: {
			finish();
			return true;
		}
		case R.id.menuClean: {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle(getString(R.string.MSG_ActionConfirmDialogBoxTitle));
			builder.setMessage(getString(R.string.MSG_ActionConfirmDialogBoxMsg));
			builder.setCancelable(false);
			builder.setPositiveButton("Yes", new CleanRemindersActionHandler(
					this));
			builder.setNegativeButton("No", new CleanRemindersActionHandler(
					this));
			AlertDialog Reminder = builder.create();
			Reminder.show();
			return true;
		}
		case R.id.menuAbout:{
			intent.setAction(getResources().getString(R.string.intent_about));
			startActivity(intent);
			return true;
		}
		default:
			return true;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		LocationUpdateUtils.updateTrackerFile(this);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "OnCreate Dialog accessed " + searchAddress);
		progressActivity = new ProgressTask_SearchAddress(this, searchAddress);
		switch (id) {
		case ApplicationSettings.PROGRESS_DIALOG_SPINNER: // Spinner
			progDialog = new ProgressDialog(this);
			progDialog.setCancelable(false);
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setMessage("Searching " + searchAddress);
			progThread = new ProgressThread(handler, progressActivity, 1000);
			progThread.start();
			return progDialog;
		case ApplicationSettings.PROGRESS_DIALOG_HORIZONTAL: // Horizontal
			progDialog = new ProgressDialog(this);
			progDialog.setCancelable(false);
			progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progDialog.setMax(ApplicationSettings.PROGRESS_BAR_MAX_VALUE);
			progDialog.setMessage("Searching " + searchAddress);
			progThread = new ProgressThread(handler, progressActivity, 1000);
			progThread.start();
			return progDialog;
		default:
			return null;
		}
	}

}
