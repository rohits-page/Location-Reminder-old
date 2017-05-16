package rcs.LocationReminder.BO;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import rcs.LocationReminder.general.ApplicationSettings;

import com.google.android.maps.GeoPoint;

public class ReminderBO implements Serializable {

	private static final long serialVersionUID = 1L;
	private long ReminderID = ApplicationSettings.INAVLID_Reminder_BO_ID;
	private int shortReminderID = ApplicationSettings.INAVLID_Reminder_BO_ID;
	private String ReminderName;
	private String ReminderDescription;
	private String ReminderAddress;
	private boolean oneTimeReminder = true;
	private float ReminderRadii;
	private boolean imperialMetrics = true;
	private int latitudeE6;
	private int longitudeE6;
	private Date fromDate = Calendar.getInstance().getTime();
	private Date toDate = Calendar.getInstance().getTime();
	private boolean timeInfoPresent;
	private boolean allDayEvent;

	/**
	 * An ReminderID is generated when the object is instantiated
	 */
	public ReminderBO() {
		shortReminderID = ApplicationSettings.generator.nextInt();
		Calendar date = Calendar.getInstance();
		date.setTime(fromDate);
		date.add(Calendar.DAY_OF_MONTH, 1);
		toDate = date.getTime();
	}

	/**
	 * Returns an ReminderID that is INTEGER representing this object Use this
	 * for tracking notifications and pending intents
	 * 
	 * @return
	 */
	public int getShortID() {
		return shortReminderID;
	}

	public int setShortID(int short_id) {
		return shortReminderID = short_id;
	}

	public long getID() {
		return ReminderID;
	}

	public void setID(long id) {
		ReminderID = id;
	}

	public float getReminderRadii() {
		return ReminderRadii;
	}

	public float getReminderRadiiInMeters() {
		if (imperialMetrics)
			return (float) (ReminderRadii * 1609.344);
		else
			return (float) (ReminderRadii * 1000.000);
	}

	public void setReminderRadii(float ReminderRadii) {
		this.ReminderRadii = ReminderRadii;
	}

	public boolean isImperialMetrics() {
		return imperialMetrics;
	}

	public void setImperialMetrics(boolean imperialMetrics) {
		this.imperialMetrics = imperialMetrics;
	}

	/**
	 * 1=> INT_TRUE, 0=> INT_FALSE
	 * 
	 * @param imperialMetrics
	 */
	public void setUSMetrics(int USMetrics) {
		this.imperialMetrics = (USMetrics == ApplicationSettings.INT_TRUE);
	}

	public String getReminderName() {
		return ReminderName;
	}

	public void setReminderName(String ReminderName) {
		this.ReminderName = ReminderName;
	}

	public String getReminderDescription() {
		return ReminderDescription;
	}

	public void setReminderDescription(String ReminderDescription) {
		this.ReminderDescription = ReminderDescription;
	}

	public GeoPoint getReminderGeoPoint() {
		return new GeoPoint(latitudeE6, longitudeE6);
	}

	public int getReminderLatitudeE6() {
		return latitudeE6;
	}

	public int getReminderLongitudeE6() {
		return longitudeE6;
	}

	public void setReminderGeoPoint(GeoPoint ReminderGeoPoint) {
		this.latitudeE6 = ReminderGeoPoint.getLatitudeE6();
		this.longitudeE6 = ReminderGeoPoint.getLongitudeE6();

	}

	public String getReminderAddress() {
		return ReminderAddress;
	}

	public void setReminderAddress(String ReminderAddress) {
		this.ReminderAddress = ReminderAddress;
	}

	public boolean isOneTimeReminder() {
		return oneTimeReminder;
	}

	public void setOneTimeReminder(boolean oneTime) {
		this.oneTimeReminder = oneTime;
	}

	/**
	 * 1=> INT_TRUE, 0=> INT_FALSE
	 * 
	 * @param oneTime
	 */
	public void setOneTimeReminder(int oneTime) {
		this.oneTimeReminder = (oneTime == ApplicationSettings.INT_TRUE);
	}

	@Override
	public String toString() {
		super.toString();
		String retVal = "";

		retVal += "Reminder ReminderID: " + ReminderID + " "
				+ "Reminder ShortID: " + shortReminderID + " "
				+ "Reminder Name: " + ReminderName + " "
				+ "Reminder Description: " + ReminderDescription + " "
				+ "Reminder Address: " + ReminderAddress + " "
				+ "Reminder Location: (LAT,LONG) (" + latitudeE6 + ","
				+ longitudeE6 + ") " + "Reminder Radii: " + ReminderRadii + " "
				+ "One Time Reminder: " + oneTimeReminder + " "
				+ "Imperial (US) metrics: " + imperialMetrics + " "
				+ " Time Info Presence: " + timeInfoPresent + " "
				+ " All day event: " + allDayEvent + " " + " From Date: "
				+ fromDate.toString() + " " + " To Date: " + toDate.toString();
		return retVal;
	}

	@Override
	public boolean equals(Object o) {
		boolean flag = false;
		if (o instanceof ReminderBO) {
			ReminderBO passedBO = (ReminderBO) o;
			flag = (this.getID() == passedBO.getID())
					& (this.getReminderLatitudeE6() == passedBO
							.getReminderLatitudeE6())
					& (this.getReminderLongitudeE6() == passedBO
							.getReminderLongitudeE6())
					& (this.getReminderRadii() == passedBO.getReminderRadii())
					& (this.getReminderAddress().equals(passedBO
							.getReminderAddress()))
					& (this.getReminderDescription().equals(passedBO
							.getReminderDescription()))
					& (this.getReminderName()
							.equals(passedBO.getReminderName()))
					& (this.getReminderGeoPoint().equals(passedBO
							.getReminderGeoPoint()))
					& (this.isImperialMetrics() == passedBO.isImperialMetrics())
					& (this.isOneTimeReminder() == passedBO.isOneTimeReminder())
					& (this.isTimeInfoPresent() == passedBO.isTimeInfoPresent())
					& (this.isAllDayEvent() == passedBO.isAllDayEvent())
					& (this.fromDate.equals(passedBO.getFromDate()))
					& (this.toDate.equals(passedBO.getToDate()));
		}
		return flag;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Calendar dateFrom) {
		if (null != dateFrom)
			setFromDate(dateFrom.getTime());
		else
			this.fromDate=null;
	}

	public void setFromDate(Date dateFrom) {
			this.fromDate = dateFrom;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date dateTo) {
			this.toDate = dateTo;
	}

	public void setToDate(Calendar dateTo) {
		if (null != dateTo)
			setToDate(dateTo.getTime());
		else
			this.toDate=null;
	}

	public boolean isTimeInfoPresent() {
		return timeInfoPresent;
	}

	/**
	 * 1=> INT_TRUE, 0=> INT_FALSE
	 * 
	 * @param timeInfoPresent
	 */
	public void setTimeInfoPresent(int timeInfoPresent) {
		this.timeInfoPresent = (timeInfoPresent == ApplicationSettings.INT_TRUE);
	}

	public void setTimeInfoPresent(boolean timeInfoPresent) {
		this.timeInfoPresent = timeInfoPresent;
	}

	public boolean isAllDayEvent() {
		return allDayEvent;
	}

	/**
	 * 1=> INT_TRUE, 0=> INT_FALSE
	 * 
	 * @param allDayEvent
	 */
	public void setAllDayEvent(int allDayEvent) {
		this.allDayEvent = (allDayEvent == ApplicationSettings.INT_TRUE);
	}

	public void setAllDayEvent(boolean allDayEvent) {
		this.allDayEvent = allDayEvent;
	}
}
