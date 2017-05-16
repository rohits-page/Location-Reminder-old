package rcs.LocationReminder.Data;

import java.util.List;

import rcs.LocationReminder.BO.ReminderBO;
import rcs.LocationReminder.Shared.SharedApplicationSettings;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


public class DBHelper extends SQLiteOpenHelper {

	private final static String TAG = "rcs.LocationReminder.Data.DBHelper";
	Context _ctx=null;
	
	@SuppressWarnings("null")
	public DBHelper(Context ctx) {
		super(ctx, LocationReminderDataModel.DATABASE_NAME, null,
				LocationReminderDataModel.DATABASE_VERSION);
		_ctx=ctx;
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "DBHelper initialized");

		if (null == ctx)
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "Null Context");
			else {
				String[] dbList = ctx.databaseList();
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					Log.d(TAG, "Associated DB count " + dbList.length);
				for (int i = 0; i < dbList.length; i++) {
					if (SharedApplicationSettings.MODE_DEVELOPMENT)
						Log.d(TAG, dbList[i]);
				}
			}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Creating Database");
		createAllTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion != oldVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion);
			
			if(oldVersion==1 && newVersion==2){
				
				for(int i=0;i<queryStatement.QRY_TABLE_Reminder_Update_V1to2.length;i++){
					String updateStatement=queryStatement.QRY_TABLE_Reminder_Update_V1to2[i];
					if(SharedApplicationSettings.MODE_DEVELOPMENT){
						Log.d(TAG, "Executing update statement "+updateStatement);
					}
					db.execSQL(queryStatement.QRY_TABLE_Reminder_Update_V1to2[i]);
				}
				
				Log.i(TAG, "Database upgraded to new version");
			}else{
				Log.e(TAG, "Upgrade sequence not found");
				Toast.makeText(_ctx, "Unable to upgrade database", Toast.LENGTH_LONG).show();
			}
			
			
		}
	}

	private void createAllTables(SQLiteDatabase db) {
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Creating all tables");
		try {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "Query : \n" + queryStatement.QRY_TABLE_Reminder_CREATE);
			db.execSQL(queryStatement.QRY_TABLE_Reminder_CREATE);
		} catch (Exception ex) {
			Log.e(TAG, "Error creating tables for Database "
					+ LocationReminderDataModel.DATABASE_NAME + " Ver. "
					+ LocationReminderDataModel.DATABASE_VERSION, ex);
		}
	}

	private void deleteAllTables(SQLiteDatabase db) {
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Dropping all tables");
		try {
			db.execSQL("DROP TABLE IF EXISTS "
					+ LocationReminderDataModel.DATABASE_TABLE_REMINDERS);
		} catch (Exception ex) {
			Log.e(TAG,
					"Error occured while deleteing all tables from database "
							+ LocationReminderDataModel.DATABASE_NAME, ex);
		}
	}
}
