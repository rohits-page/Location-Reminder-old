package rcs.LocationReminder.Data;

public class queryStatement {

	public static final String QRY_TABLE_Reminder_CREATE = "create table "
			+ LocationReminderDataModel.DATABASE_TABLE_REMINDERS
			+ " (_id integer primary key autoincrement, "
			+ LocationReminderDataModel.Reminders.Reminder_NAME
			+ " text not null,"
			+ LocationReminderDataModel.Reminders.Reminder_DECSRIPTION
			+ " text,"
			+ LocationReminderDataModel.Reminders.Reminder_RADII
			+ " float not null,"
			+ LocationReminderDataModel.Reminders.Reminder_LATITUDEE6
			+ " integer default 0 not null,"
			+ LocationReminderDataModel.Reminders.Reminder_LONGITUDEE6
			+ " integer default 0 not null,"
			+ LocationReminderDataModel.Reminders.Reminder_ADDRESS
			+ " text not null,"
			+ LocationReminderDataModel.Reminders.Reminder_IMPERIAL_METRICS
			+ " integer default 0 not null, "
			+ LocationReminderDataModel.Reminders.ONE_TIME_Reminder
			+ " integer default 0 not null, "
			+ LocationReminderDataModel.Reminders.Reminder_Timing_Info_Present
			+ " integer default 0, "
			+ LocationReminderDataModel.Reminders.All_Day_event
			+ " integer default 0, "
			+ LocationReminderDataModel.Reminders.Reminder_Window_Start_DateTime
			+ " text, "
			+ LocationReminderDataModel.Reminders.Reminder_Window_End_DateTime
			+ " text " + ");";

	public static final String[] QRY_TABLE_Reminder_Update_V1to2 = {
			"alter table " + LocationReminderDataModel.DATABASE_TABLE_REMINDERS
					+ " add column "
					+ LocationReminderDataModel.Reminders.All_Day_event
					+ " integer default 0",

			"alter table "
					+ LocationReminderDataModel.DATABASE_TABLE_REMINDERS
					+ " add column "
					+ LocationReminderDataModel.Reminders.Reminder_Timing_Info_Present
					+ " integer default 0",

			"alter table "
					+ LocationReminderDataModel.DATABASE_TABLE_REMINDERS
					+ " add column "
					+ LocationReminderDataModel.Reminders.Reminder_Window_Start_DateTime
					+ " text",

			"alter table "
					+ LocationReminderDataModel.DATABASE_TABLE_REMINDERS
					+ " add column "
					+ LocationReminderDataModel.Reminders.Reminder_Window_End_DateTime
					+ " text" };
}
