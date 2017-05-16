package rcs.LocationReminder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import rcs.LocationReminder.BO.ReminderBO;
import rcs.LocationReminder.Data.DataBridge;
import rcs.LocationReminder.Data.LocationReminderDataModel;
import rcs.LocationReminder.EventHandler.DeleteReminderActionHandler;
import rcs.LocationReminder.EventHandler.PurchaseActionHandler;
import rcs.LocationReminder.Overlay.MapReminderOverlay;
import rcs.LocationReminder.Shared.SharedApplicationSettings;
import rcs.LocationReminder.general.ApplicationSettings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class LocationUpdateUtils {

	private static String TAG = "rcs.LocationReminder.LocationUpdateUtils";

	public static void showLocationOnMap(Location location, MapView mapView,
			Activity activity, MapReminderOverlay... itemizedOverlay) {
		if (null != location) {
			if (null != itemizedOverlay && itemizedOverlay.length > 0)
				showLocationOnMap(convertLocationToGeoPoint(location), mapView,
						activity, itemizedOverlay[0]);
			else {
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					if (null == itemizedOverlay)
						Log.d(TAG, "Location : Overlay Object is null");
					else
						Log.d(TAG, "Location : Length of Overlay Object is "
								+ itemizedOverlay.length);
				showLocationOnMap(
						new GeoPoint((int) (location.getLatitude() * 1E6),
								(int) (location.getLongitude() * 1E6)),
						mapView, activity);
			}
		} else {
			Toast.makeText(
					activity,
					activity.getResources().getString(
							R.string.MSG_ProviderUnavailable),
					Toast.LENGTH_SHORT).show();
		}
	}

	public static void showLocationOnMap(GeoPoint point, MapView mapView,
			Activity activity, MapReminderOverlay... itemizedOverlay) {
		String address = "";
		if (null != point) {
			MapController mapController = mapView.getController();
			mapController
					.setZoom(ApplicationSettings.defaultLocationUpdateZoomLevel);
			mapController.animateTo(point);
			mapView.invalidate();

			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "GPS Position : " + "(" + point.getLatitudeE6()
						+ "," + point.getLongitudeE6() + ")");

			// Overlay
			if (null != itemizedOverlay && itemizedOverlay.length > 0) {
				address = getAddress(point, activity);
				// show the overlay here
				OverlayItem overlayitem = new OverlayItem(point,
						"GPS Position", address);
				itemizedOverlay[0].clearOverlay();
				itemizedOverlay[0].addOverlay(overlayitem);
				mapView.getOverlays().add(itemizedOverlay[0]);
			} else {
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					if (null == itemizedOverlay)
						Log.d(TAG, "Point : Overlay Object is null");
					else
						Log.d(TAG, "Point : Length of Overlay Object is "
								+ itemizedOverlay.length);
			}

		} else {
			Toast.makeText(
					activity,
					activity.getResources().getString(
							R.string.MSG_ProviderUnavailable),
					Toast.LENGTH_SHORT).show();
		}
	}

	public static String getAddress(GeoPoint point, Context context) {
		// returns the first address
		String address = "";
		Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocation(
					point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6,
					1);
			if (addresses.size() > 0) {
				for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++)
					address += addresses.get(0).getAddressLine(i) + "\n";
			} else
				address = "Address not found";
		} catch (IOException e) {
			Log.e(TAG, "Error retrieving address \n" + e.getMessage());
			address = "Unable to find the address";
		}
		return address;
	}

	/**
	 * Translates Location into GeoPoint Returns (0,0) if a null value is passed
	 * 
	 * @param location
	 * @return
	 */
	public static GeoPoint convertLocationToGeoPoint(Location location) {
		GeoPoint point = null;
		if (null != location) {
			point = new GeoPoint((int) (location.getLatitude() * 1E6),
					(int) (location.getLongitude() * 1E6));
		} else
			point = new GeoPoint(0, 0);
		return point;
	}

	/**
	 * Translates Location into GeoPoint Returns (0,0) if a null value is passed
	 * 
	 * @param location
	 * @return
	 */
	public static Location convertGeoPointToLocation(GeoPoint point) {
		Location location = new Location(LocationManager.GPS_PROVIDER);
		if (null != point) {

			location.setLatitude(point.getLatitudeE6() / 1E6);
			location.setLongitude(point.getLongitudeE6() / 1E6);
		}
		return location;
	}

	/**
	 * It returns the GeoPoint corresponding to a given address. Assumes Locale
	 * will always be set. Will return NULL if it was unable to determine the
	 * location
	 * 
	 * @param activity
	 * @param lookupAddress
	 * @return
	 */
	public static GeoPoint getGeoPointFromAddress(Activity activity,
			String lookupAddress) {
		GeoPoint point = null;
		try {
			Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
			List<Address> addressList = geocoder.getFromLocationName(
					lookupAddress, 1);
			if (null != addressList && !addressList.isEmpty()) {
				// get the first address
				Address address = addressList.get(0);
				point = new GeoPoint((int) (address.getLatitude() * 1E6),
						(int) (address.getLongitude() * 1E6));
			}
		} catch (Exception exc) {
			Log.e(TAG, "Error occured while looking up geopoint for address "
					+ lookupAddress + "\n " + exc.getMessage());
		}
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			if (null != point)
				Log.d(TAG, "GeoPoint corresponding to lookup address "
						+ lookupAddress + "is (" + point.getLatitudeE6() + ","
						+ point.getLongitudeE6() + ")");
			else
				Log.d(TAG, "GeoPoint corresponding to lookup address "
						+ lookupAddress + "is null");
		return point;
	}

	/**
	 * It returns set of Addresses when an address is passed for lookup * @param
	 * context
	 * 
	 * @param lookupAddress
	 * @return
	 */
	public static HashMap<String, GeoPoint> getMatchingAddersses(
			Activity activity, String lookupAddress) {
		HashMap<String, GeoPoint> possibleAddresses = new HashMap<String, GeoPoint>();
		try {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "Searching for address " + lookupAddress);

			Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
			List<Address> addressList = geocoder.getFromLocationName(
					lookupAddress, 1);
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG,
						"Number of matching addresses found "
								+ addressList.size());
			if (null != addressList && !addressList.isEmpty()) {
				for (int i = 0; i < addressList.size(); i++) {
					Address address = addressList.get(i);
					GeoPoint point = new GeoPoint(
							(int) (address.getLatitude() * 1E6),
							(int) (address.getLongitude() * 1E6));
					String streetAddress = "";
					int maxAddressLines = address.getMaxAddressLineIndex();
					if (maxAddressLines >= 0) {
						for (int addLineIndex = 0; addLineIndex < maxAddressLines; addLineIndex++) {
							streetAddress += address
									.getAddressLine(addLineIndex) + "\n";
						}
						if (streetAddress.length() > 1)
							streetAddress = streetAddress.substring(0,
									streetAddress.length() - 1);
					}
					if (!possibleAddresses.containsKey(streetAddress))
						if (SharedApplicationSettings.MODE_DEVELOPMENT)
							Log.d(TAG,
									"Adding " + streetAddress + ","
											+ point.toString());
					possibleAddresses.put(streetAddress, point);
				}
			}
		} catch (Exception exc) {
			Log.e(TAG, "Error occured while looking up geopoint for address "
					+ lookupAddress + "\n " + exc.getMessage());
		}

		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Returning " + possibleAddresses.size()
					+ " number of addresses");
		return possibleAddresses;
	}

	public static boolean addMarkerForAddressOnMap(Activity activity,
			MapView mapView, GeoPoint point) {
		boolean retVal = false;
		if (null != activity && null != mapView && null != point) {
			String address = LocationUpdateUtils.getAddress(point, activity);
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "addMarkerForAdderssMap: Address " + address
						+ " corresponds to " + point.toString());
			retVal = addMarkerForAddressOnMap(activity, mapView, point, address);
		}
		return retVal;
	}

	public static boolean addMarkerForAddressOnMap(Activity activity,
			MapView mapView, GeoPoint point, String address) {
		boolean retVal = false;
		if (null != activity && null != mapView && null != point
				&& null != address) {
			List<Overlay> overlayList = mapView.getOverlays();
			Iterator<Overlay> iterator = overlayList.iterator();
			while (iterator.hasNext()) {
				Overlay overlay = iterator.next();
				if (overlay instanceof MapReminderOverlay) {
					MapReminderOverlay mapReminderOverlay = (MapReminderOverlay) overlay;
					mapReminderOverlay.clearOverlay();
					mapReminderOverlay.addOverlay(new OverlayItem(point,
							"Selected Address", address));
					if (SharedApplicationSettings.MODE_DEVELOPMENT)
						Log.d(TAG, "Adding marker for address " + address);
					Toast.makeText(
							activity,
							activity.getResources().getString(
									R.string.MSG_TapMarker), Toast.LENGTH_SHORT)
							.show();
					retVal = true;

					break;
				}
			}
		}
		return retVal;
	}

	/**
	 * Returns True if a provider can be set If user has disabled both Network
	 * and GPS for location update, method will return False
	 * 
	 * @return
	 */
	public static String setProvider(LocationManager locationManager) {
		String provider = null;
		if (locationManager
				.isProviderEnabled(ApplicationSettings.DEFAULT_GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "GPS is enabled");
		} else {
			if (locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				provider = LocationManager.NETWORK_PROVIDER;
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					Log.d(TAG, "Network is enabled");
			}
		}
		return provider;
	}

	/**
	 * Directs user to enable providers
	 * 
	 * @param activity
	 */
	public static void buildReminderMessageNoLocationProviderEnabled(
			final Activity activity) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(
				activity.getResources()
						.getString(R.string.MSG_ProviderDisabled))
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								activity.startActivity(new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog Reminder = builder.create();
		Reminder.show();
	}

	/**
	 * Creates PendingIntents for the ReminderBO
	 * 
	 * @param context
	 * @param ReminderBO
	 * @return
	 */
	public static PendingIntent createPendingIntentForCallingManageReminderScreen(
			Context context, ReminderBO ReminderBO, String intentAction,
			int CRUD_MODE) {
		Intent notificationIntent = new Intent();
		notificationIntent.setAction(intentAction);
		notificationIntent.putExtra(ApplicationSettings.CRUD_MODE, CRUD_MODE);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Bundle bundle = new Bundle();
		// bundle.putString(SharedApplicationSettings.keyExchangeObject,LocationUpdateUtils.addToSharedRepository(ReminderBO));
		bundle.putSerializable(ApplicationSettings.keyExchangeReminderBO,
				ReminderBO);
		notificationIntent.putExtra(ApplicationSettings.bundleReminderBO,
				bundle);
		PendingIntent contentIntent = PendingIntent.getActivity(context,
				ReminderBO.getShortID(), notificationIntent,
				PendingIntent.FLAG_ONE_SHOT);

		return contentIntent;
	}

	/**
	 * Creates PendingIntents for the ReminderBO to be used in creating
	 * notification
	 * 
	 * @param context
	 * @param ReminderBO
	 * @return
	 */
	public static PendingIntent createPendingIntentForCreatingReminderNotification(
			Context context, ReminderBO ReminderBO) {
		PendingIntent pendingIntent = null;
		int pendingIntentFlag = PendingIntent.FLAG_NO_CREATE;
		Intent intent = new Intent();
		intent.setAction(context.getResources().getString(
				R.string.intent_notify_reminder));
		intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		/*
		 * Bundle bundle = new Bundle();
		 * bundle.putSerializable(SharedApplicationSettings
		 * .keyExchangeReminderBO, ReminderBO);
		 * intent.putExtra(SharedApplicationSettings.bundleReminderBO, bundle);
		 */
		intent.putExtra(ApplicationSettings.keyExchangeReminderBO,
				ReminderBO.getID());
		if (ReminderBO.isOneTimeReminder())
			pendingIntentFlag = PendingIntent.FLAG_ONE_SHOT;
		else
			pendingIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT;

		/**
		 * The Request ID of the Pending Intent is of type INT To be able to
		 * cancel the pending Intent that was earlier created we should be able
		 * to reuse the same request ID. We will be using the ID of ReminderBO
		 * as this request ID. This would work perfect until the ReminderBO ID
		 * reached Integer.MAX_VALUE (65,536). In practice no ReminderBO will
		 * reach that value. For ReminderBO's with ID > Integer.MAX_VALUE we can
		 * not use that. Hence we will use the Short ID as the request ID. We
		 * will not be able to cancel these PendingIntents since ShortID is a
		 * random number. Hence such PendingIntents will stay in system until
		 * the system is restarted. The ReminderNotifcationService is designed
		 * to handle the intents that correspond to deleted ReminderBOs
		 */
		int intentID = Integer.MAX_VALUE;
		if (ReminderBO.getID() > intentID)
			intentID = ReminderBO.getShortID();
		else
			intentID = (int) ReminderBO.getID();

		pendingIntent = PendingIntent.getService(
				context.getApplicationContext(), (int) ReminderBO.getID(),
				intent, pendingIntentFlag);
		return pendingIntent;
	}

	/**
	 * For the given Reminder, it creates a proximity Reminder with the location
	 * manager
	 * 
	 * @param context
	 * @param ReminderBO
	 */
	public static void createProximityReminderForReminder(Context context,
			ReminderBO ReminderBO) {
		Location location = LocationUpdateUtils
				.convertGeoPointToLocation(ReminderBO.getReminderGeoPoint());
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "MAPOverlay > Creating pending intent");
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager.addProximityAlert(
				location.getLatitude(),
				location.getLongitude(),
				ReminderBO.getReminderRadiiInMeters(),
				-1,
				createPendingIntentForCreatingReminderNotification(context,
						ReminderBO));
	}

	public static void addUpdateReminderBOAndEndActivityAppropriately(
			final Activity activity, ReminderBO reminderBO) {
		DataBridge dataBridge = new DataBridge(activity);
		Uri uri = dataBridge.addUpdateReminder(reminderBO);
		boolean createUpdateBO = false;

		if (null != uri) {
			reminderBO.setID(Long.parseLong(uri.getPathSegments().get(1)));
			// Allow limited number of reminders for Free APP
			if (LocationUpdateUtils.isAppUnlocked(activity
					.getApplicationContext())) {
				createUpdateBO = true;
			} else {
				if (reminderBO.getID() > ApplicationSettings.FREE_APP_REMINDER_LIMIT) {
					dataBridge.deleteReminder(reminderBO);
					LocationUpdateUtils.showPurchaseDialog(
							activity,
							activity.getResources().getString(
									R.string.MSG_TitleUnsupportedOperation),
							activity.getResources().getString(
									R.string.MSG_UnsupportedReminder)
									+ "\n"
									+ activity.getResources().getString(
											R.string.MSG_StdPurchasMessage),
							true, true);
					activity.setResult(Activity.RESULT_CANCELED);
				} else
					createUpdateBO = true;
			}

			if (createUpdateBO) {
				LocationUpdateUtils.createProximityReminderForReminder(
						activity, reminderBO);
				activity.setResult(Activity.RESULT_OK);
				Toast.makeText(
						activity,
						"Reminder " + reminderBO.getReminderName()
								+ " successfully created.", Toast.LENGTH_SHORT)
						.show();
				activity.finish();

			}
		} else {
			Toast.makeText(
					activity,
					activity.getResources().getString(
							R.string.MSG_UnableToCreateReminder),
					Toast.LENGTH_LONG).show();
			activity.setResult(Activity.RESULT_CANCELED);
			activity.finish();
		}
	}

	/**
	 * 
	 * @param activity
	 * @param reminderBO
	 * @param showPromt Do not prompt "confirm deletion" if false
	 */
	public static void deleteReminderBOCancelPendingIntentAndEndActivityAppropriately(
			Activity activity, ReminderBO reminderBO, boolean showPrompt) {

		if (showPrompt) {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle(activity
					.getString(R.string.MSG_ActionConfirmDialogBoxTitle));
			builder.setMessage(R.string.MSG_ActionConfirmDialogBoxMsg);
			builder.setCancelable(false);
			builder.setPositiveButton("Yes", new DeleteReminderActionHandler(
					activity, reminderBO));
			builder.setNegativeButton("No", new DeleteReminderActionHandler(
					activity, reminderBO));
			AlertDialog Reminder = builder.create();
			Reminder.show();
		} else {
			deleteReminderBOCancelPendingIntentNoPromptAndEndActivityAppropriately(activity,reminderBO,true);
		}
	}
	
	/**
	 * 
	 * @param activity
	 * @param reminderBO
	 * @param endActivity
	 */
	public static void deleteReminderBOCancelPendingIntentNoPromptAndEndActivityAppropriately(Activity activity, ReminderBO reminderBO,boolean endActivity){
		if (LocationUpdateUtils.deleteReminderBOCancelPendingIntent(
				activity, reminderBO)) {
			activity.setResult(Activity.RESULT_OK);
		} else {
			Toast.makeText(activity,
					activity.getString(R.string.MSG_UnableToDelete),
					Toast.LENGTH_SHORT).show();
			activity.setResult(Activity.RESULT_CANCELED);
		}
		if(endActivity)
			activity.finish();
	}

	/**
	 * 
	 * @param activity
	 * @param reminderBO
	 */
	public static boolean deleteReminderBOCancelPendingIntent(
			Activity activity, ReminderBO reminderBO) {
		boolean flag = false;

		DataBridge databridge = new DataBridge(activity);
		if (databridge.deleteReminder(reminderBO)) {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "Attempt to cancel pendingintent");
			PendingIntent pendingIntent = LocationUpdateUtils
					.createPendingIntentForCreatingReminderNotification(
							activity, reminderBO);
			pendingIntent.cancel();
			flag = true;
		}
		return flag;
	}

	public static void handleSelectedAddress(String selectedAddress,
			GeoPoint point, MapView mapView, Activity activity) {
		if (null != point) {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG,
						" Handle selected address: Address " + selectedAddress
								+ " corresponds to " + point.toString());
			mapView.getController().animateTo(point);
			LocationUpdateUtils.addMarkerForAddressOnMap(activity, mapView,
					point, selectedAddress);
		} else {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG,
						"Address "
								+ selectedAddress
								+ " could not be located on Map. Please select another point of interest on the map or enter another address.");
			Toast.makeText(
					activity,
					activity.getResources().getString(
							R.string.MSG_AddressNotFound), Toast.LENGTH_LONG)
					.show();
		}
	}

	public static void writeStringToSharedPreferences(String key, String value,
			Context context) {
		writeToSharedPreferences(key, value,
				ApplicationSettings.value_type_string, context);
	}

	public static void writeIntToSharedPreferences(String key, Integer value,
			Context context) {
		writeToSharedPreferences(key, value,
				ApplicationSettings.value_type_int, context);
	}

	public static void writeLongToSharedPreferences(String key, Long value,
			Context context) {
		writeToSharedPreferences(key, value,
				ApplicationSettings.value_type_long, context);
	}

	public static void writeBooleanToSharedPreferences(String key,
			Boolean value, Context context) {
		writeToSharedPreferences(key, value,
				ApplicationSettings.value_type_boolean, context);
	}

	public static void writeFloatToSharedPreferences(String key, Float value,
			Context context) {
		writeToSharedPreferences(key, value,
				ApplicationSettings.value_type_float, context);
	}

	private static void writeToSharedPreferences(String key, Object value,
			int value_type, Context context) {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor ed = app_preferences.edit();
		switch (value_type) {
		case ApplicationSettings.value_type_boolean:
			ed.putBoolean(key, (Boolean) value);
			ed.commit();
			break;
		case ApplicationSettings.value_type_int:
			ed.putInt(key, (Integer) value);
			ed.commit();
			break;
		case ApplicationSettings.value_type_long:
			ed.putLong(key, (Long) value);
			ed.commit();
			break;
		case ApplicationSettings.value_type_string:
			ed.putString(key, (String) value);
			ed.commit();
			break;
		case ApplicationSettings.value_type_float:
			ed.putFloat(key, (Float) value);
			ed.commit();
			break;
		}
	}

	public static String getStringFromSharedPreferences(String key,
			Context context, String defaultValue) {
		return (String) getFromSharedPreferences(key,
				ApplicationSettings.value_type_string, context, defaultValue);
	}

	public static Integer getIntFromSharedPreferences(String key,
			Context context, Integer defaultValue) {
		return (Integer) getFromSharedPreferences(key,
				ApplicationSettings.value_type_int, context, defaultValue);
	}

	public static Long getLongFromSharedPreferences(String key,
			Context context, Long defaultValue) {
		return (Long) getFromSharedPreferences(key,
				ApplicationSettings.value_type_long, context, defaultValue);
	}

	public static Boolean getBooleanFromSharedPreferences(String key,
			Context context, Boolean defaultValue) {
		return (Boolean) getFromSharedPreferences(key,
				ApplicationSettings.value_type_boolean, context, defaultValue);
	}

	public static Float getFloatFromSharedPreferences(String key,
			Context context, Float defaultValue) {
		return (Float) getFromSharedPreferences(key,
				ApplicationSettings.value_type_boolean, context, defaultValue);
	}

	private static Object getFromSharedPreferences(String key, int value_type,
			Context context, Object defaultValue) {
		Object retVal = null;
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		switch (value_type) {
		case ApplicationSettings.value_type_boolean:
			retVal = app_preferences.getBoolean(key, (Boolean) defaultValue);
			break;
		case ApplicationSettings.value_type_int:
			retVal = app_preferences.getInt(key, (Integer) defaultValue);
			break;
		case ApplicationSettings.value_type_long:
			retVal = app_preferences.getLong(key, (Long) defaultValue);
			break;
		case ApplicationSettings.value_type_string:
			retVal = app_preferences.getString(key, (String) defaultValue);
			break;
		case ApplicationSettings.value_type_float:
			retVal = app_preferences.getFloat(key, (Float) defaultValue);
			break;
		}
		return retVal;
	}

	public static boolean isAppUnlocked(Context context) {
		return (LocationUpdateUtils.getBooleanFromSharedPreferences(
				SharedApplicationSettings.unlock_status_key_name, context,
				SharedApplicationSettings.unlock_status_lock_value) != SharedApplicationSettings.unlock_status_lock_value);
	}

	/**
	 * 
	 * @param activity
	 * @param title
	 * @param message
	 * @param flagPurchaseActivityClose
	 *            TRUE indicates close the calling activity after the user
	 *            selects Purchase
	 * @param flagNotPurchaseActivityClose
	 *            TRUE indicates close the calling activity after the user
	 *            selects to not purchase
	 */
	public static void showPurchaseDialog(Activity activity, String title,
			String message, boolean flagPurchaseActivityClose,
			boolean flagNotPurchaseActivityClose) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new PurchaseActionHandler(activity,
				flagPurchaseActivityClose));
		builder.setNegativeButton("No", new PurchaseActionHandler(activity,
				flagNotPurchaseActivityClose));
		AlertDialog Reminder = builder.create();
		Reminder.show();
	}

	/**
	 * Updates the tracker file with the latest number of reminders
	 * 
	 * @param context
	 */
	public static void updateTrackerFile(Context context) {
		Cipher ecipher;
		// Write the reminder count to local file

		// using first 8 characters of imei id as key
		String IMEI = ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "IMEI = " + IMEI);
		try {
			if (null == IMEI || Long.parseLong(IMEI) == 0) {
				IMEI = ApplicationSettings.StaticKey;
			}
		} catch (NumberFormatException nfe) {
		}

		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Using Key base = " + IMEI);

		SecretKeySpec keySpec = new SecretKeySpec(IMEI.substring(0, 8)
				.getBytes(), ApplicationSettings.Algorithm_Name);
		IvParameterSpec ivSpec = new IvParameterSpec(
				ApplicationSettings.InitializationVector.getBytes());

		try {
			File root = Environment.getExternalStorageDirectory();
			if (root.canWrite()) {
				File trackerFile = new File(
						ApplicationSettings.tracker_file_name);

				long newValue = 0;

				DataBridge dataBridge = new DataBridge(context);
				/*
				 * List<Long> reminderList = dataBridge.getReminderIDs(null,
				 * null, LocationReminderDataModel.Reminders._ID + " DESC");
				 * //TODO: if (null != reminderList && reminderList.size() > 0)
				 * newValue = reminderList.get(0);
				 */
				String[] selectionArgs = { LocationReminderDataModel.DATABASE_TABLE_REMINDERS };
				List<Long> sequenceValues = dataBridge.getSequenceValues(
						LocationReminderDataModel.sqlite_sequence.TABLE_NAME
								+ "= ?", selectionArgs);
				if (null != sequenceValues && sequenceValues.size() > 0)
					newValue = sequenceValues.get(0);
				// Update the tracker value
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					Log.d(TAG, "Updating tracker with new value " + newValue);
				if (root.canWrite()) {
					ecipher = Cipher
							.getInstance(ApplicationSettings.Algorithm_Name);
					ecipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
					FileOutputStream fos = null;
					if (trackerFile.exists()) {
						fos = new FileOutputStream(
								trackerFile.getAbsolutePath());
					} else {
						fos = new FileOutputStream(
								ApplicationSettings.tracker_file_name);
					}
					CipherOutputStream cos = new CipherOutputStream(fos,
							ecipher);
					cos.write((newValue + "").getBytes());
					cos.close();

					if (SharedApplicationSettings.MODE_DEVELOPMENT)
						Log.d(TAG, "Updated Tracker file with new value "
								+ newValue);
				}
			}
		} catch (Exception exc) {
			Log.e(TAG,
					"Unable to process the tracker file\n" + exc.getMessage());
			exc.printStackTrace();
		}
	}

	public static void syncAsPerTracker(Context context) {
		Cipher dcipher;

		if (!LocationUpdateUtils.isAppUnlocked(context)) {

			// using first 8 characters of imei id as key
			String IMEI = ((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "IMEI = " + IMEI);
			try {
				if (null == IMEI || Long.parseLong(IMEI) == 0) {
					IMEI = ApplicationSettings.StaticKey;
				}
			} catch (NumberFormatException nfe) {
			}

			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "Using Key base = " + IMEI);

			SecretKeySpec keySpec = new SecretKeySpec(IMEI.substring(0, 8)
					.getBytes(), ApplicationSettings.Algorithm_Name);
			IvParameterSpec ivSpec = new IvParameterSpec(
					ApplicationSettings.InitializationVector.getBytes());

			// Read the contents of the Tracker File
			try {
				File root = Environment.getExternalStorageDirectory();
				if (root.canRead()) {
					dcipher = Cipher
							.getInstance(ApplicationSettings.Algorithm_Name);
					dcipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

					if (SharedApplicationSettings.MODE_DEVELOPMENT)
						Log.d(TAG, "Reading Tracker file "
								+ ApplicationSettings.tracker_file_name);
					File trackerFile = new File(
							ApplicationSettings.tracker_file_name);

					String trackerContent = "";
					int trackerValue = 0;
					if (trackerFile.exists()) {
						FileInputStream in = new FileInputStream(
								trackerFile.getAbsolutePath());

						CipherInputStream cis = new CipherInputStream(in,
								dcipher);
						int data = cis.read();
						while (data != -1) {
							trackerContent += (char) data;
							data = cis.read();
						}
						cis.close();
					}

					try {
						trackerValue = Integer.parseInt(trackerContent);
					} catch (NumberFormatException exc) {
						trackerValue = 0;
					}

					long max_id = 0;
					DataBridge dataBridge = new DataBridge(context);
					String[] selectionArgs = { LocationReminderDataModel.DATABASE_TABLE_REMINDERS };
					List<Long> sequenceValues = dataBridge
							.getSequenceValues(
									LocationReminderDataModel.sqlite_sequence.TABLE_NAME
											+ "= ?", selectionArgs);
					if (null != sequenceValues && sequenceValues.size() > 0)
						max_id = sequenceValues.get(0);

					if (SharedApplicationSettings.MODE_DEVELOPMENT)
						Log.d(TAG, "The tracker file has value " + trackerValue
								+ " the DB has reminder with max ID " + max_id);

					/*
					 * Possible scenarios: Max ID< Tracker File : User is
					 * currently using a Free version, he had a free/paid
					 * earlier. Create Dummy Max ID > Tracker File : User is
					 * currently using a Free version, Update Tracker file
					 */

					if (trackerValue > max_id) {
						// Reinstalled the Free APP
						long dummy_bo_maxid = (trackerValue >= ApplicationSettings.FREE_APP_REMINDER_LIMIT) ? ApplicationSettings.FREE_APP_REMINDER_LIMIT
								: trackerValue;
						if (SharedApplicationSettings.MODE_DEVELOPMENT)
							Log.d(TAG, "Creating " + (dummy_bo_maxid - max_id)
									+ " dummy reminders");
						for (long i = max_id; i < dummy_bo_maxid; i++) {
							ReminderBO dummyReminderBO = new ReminderBO();
							dummyReminderBO
									.setReminderName(ApplicationSettings.tracker_reminder_name);
							dummyReminderBO
									.setReminderDescription(ApplicationSettings.tracker_reminder_description);
							dummyReminderBO.setOneTimeReminder(true);
							dummyReminderBO.setReminderRadii(1);
							dummyReminderBO.setReminderAddress(" ");
							dummyReminderBO.setReminderGeoPoint(new GeoPoint(0,
									0));
							dataBridge.addUpdateReminder(dummyReminderBO);
						}
					}

					if (trackerValue < max_id)
						LocationUpdateUtils.updateTrackerFile(context);
				}
			} catch (Exception exc) {
				Log.e(TAG,
						"Unable to process the tracker file\n"
								+ exc.getMessage());
				exc.printStackTrace();
			}

		}
	}
}
