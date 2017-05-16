package rcs.LocationReminder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

import rcs.LocationReminder.Shared.SharedApplicationSettings;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;


public class ProgressTask_CreateLocalHelpFile implements ProgressTask {

	private final String TAG = getClass().getName();

	private float completion = 0;
	private File outHelpContentFile;
	private String inHelpContentFile;
	private Context mContext;
	private int TASK_STATE = PROGRESS_TASK_NOT_STARTED;

	/**
	 * 
	 * @param context
	 * @param sourceFileName
	 *            : Name of file in the Assets Folder to be Read
	 * @param outFileLocation
	 *            : Location where the file will be created
	 */
	public ProgressTask_CreateLocalHelpFile(Context context,
			String sourceFileName, File outFileLocation) {
		outHelpContentFile = outFileLocation;
		inHelpContentFile = sourceFileName;
		mContext = context;
		TASK_STATE = PROGRESS_TASK_NOT_STARTED;
	}

	public float getCompletionPercentage() {
		return completion;
	}

	public void performTask() {
		TASK_STATE = PROGRESS_TASK_RUNNING;
		try {
			File root = Environment.getExternalStorageDirectory();
			
			InputStreamReader isr = new InputStreamReader(mContext
					.getResources().getAssets()
					.open(inHelpContentFile, AssetManager.ACCESS_BUFFER));
			BufferedReader reader = new BufferedReader(isr);

			String helpContent = "";
			String data = reader.readLine();
			while (null != data) {
				helpContent += data + "\n";
				data = reader.readLine();
			}

			if (root.canWrite()) {
				FileWriter fileWriter = new FileWriter(outHelpContentFile);
				BufferedWriter out = null;
				out = new BufferedWriter(fileWriter);
				out.write(helpContent);
				out.close();
				TASK_STATE = PROGRESS_TASK_DONE;
			} else {
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					Log.d(TAG, "No permission to Write to SD card");
				TASK_STATE = PROGRESS_TASK_FAILED;
			}
		} catch (Exception exc) {
			Log.e(TAG, "Error occured while creating local copy of Help File\n"
					+ exc.getMessage());
			TASK_STATE = PROGRESS_TASK_FAILED;
		}
		completion = 100;

	}

	public int getTaskState() {
		return TASK_STATE;
	}

	public Object getTaskResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
