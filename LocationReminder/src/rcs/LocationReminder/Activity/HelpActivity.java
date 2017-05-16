package rcs.LocationReminder.Activity;

import java.io.File;

import rcs.LocationReminder.ProgressTask;
import rcs.LocationReminder.ProgressTask_CreateLocalHelpFile;
import rcs.LocationReminder.ProgressThread;
import rcs.LocationReminder.R;
import rcs.LocationReminder.Shared.SharedApplicationSettings;
import rcs.LocationReminder.general.ApplicationSettings;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml.Encoding;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;


public class HelpActivity extends Activity implements OnClickListener {

	private final String TAG = getClass().getName();
	private ProgressDialog progDialog;
	private ProgressThread progThread;
	private int typeProgressBar;
	private ProgressTask progressActivity;
	private File helpContentFile;
	private WebView helpView;

	// Handler on the main (UI) thread that will receive messages from the
	// second thread and update the progress.
	final Handler handler = new Handler() {
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

			if (total == ApplicationSettings.PROGRESS_BAR_MAX_VALUE) {
				removeDialog(typeProgressBar);
				progThread
						.setProgressThreadState(ApplicationSettings.PROGRESS_THREAD_DONE);
				try {
					if (progressActivity.getTaskState() == ProgressTask.PROGRESS_TASK_DONE) {
						displayHelpContent(helpContentFile);
					} else
						noHelpContentToDisplay(getResources().getString(
								R.string.MSG_Help_UnknownError));
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
					noHelpContentToDisplay(getResources().getString(
							R.string.MSG_Help_KnownError)
							+ e.getMessage());
				}
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.help_web);

		helpView = (WebView) findViewById(R.id.webview);
		Button button = (Button) findViewById(R.id.btnExitHelp);
		button.setOnClickListener(this);

		File root = Environment.getExternalStorageDirectory();
		helpContentFile = new File(root, ApplicationSettings.help_file_name);

		helpView.loadData("Loading help file...", "text/html",
				Encoding.US_ASCII.toString());
		try {

			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "Looking for existing help.html");
			if (root.canRead()) {
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					Log.d(TAG,
							"Allowed to Read on SDCARD, now looking for file");
				if (helpContentFile.exists()) {
					displayHelpContent(helpContentFile);
				} else {
					typeProgressBar = ApplicationSettings.PROGRESS_DIALOG_SPINNER;// Spinner
					showDialog(typeProgressBar);
					if (SharedApplicationSettings.MODE_DEVELOPMENT)
						Log.d(TAG, "Creating help file");
				}
			} else
				noHelpContentToDisplay("Unable to access the external storage.");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			noHelpContentToDisplay("Error occured while accessing the help file."
					+ e.getMessage());
		}
	}

	private void noHelpContentToDisplay(String... reasonMessage) {
		if (null != reasonMessage && reasonMessage.length>0)
			helpView.loadData(
					getResources().getString(R.string.MSG_Help_StdError)
							.replaceAll("\n", "<br>")
							+ "<br><font color='red'>Error Message: "
							+ reasonMessage[0].replaceFirst("\n", "<br>")
							+ "</font>", "text/html", Encoding.US_ASCII
							.toString());
		else
			helpView.loadData(
					getResources().getString(R.string.MSG_Help_StdError)
							.replaceAll("\n", "<br>"), "text/html",
					Encoding.US_ASCII.toString());
	}

	public void onClick(View arg0) {
		setResult(RESULT_OK);
		finish();
	}

	private void displayHelpContent(File helpFile) throws Exception {
		if (helpFile.exists()) {
			if (helpFile.length() == 0) {
				if (helpFile.delete()) {
					noHelpContentToDisplay(getResources().getString(
							R.string.MSG_Help_DamagedContent));
				} else {
					noHelpContentToDisplay(getResources().getString(
							R.string.MSG_HelpDamagedBeyondRepair)
							+ "<br>Name of file to be deleted "
							+ helpFile.getName());
				}
			} else {
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					Log.d(TAG, "File exists " + helpFile.toURL().toString());
				helpView.loadUrl(helpFile.toURL().toString());
			}
		} else {
			noHelpContentToDisplay(getResources().getString(
					R.string.MSG_HelpContentNotFound));
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		progressActivity = new ProgressTask_CreateLocalHelpFile(this,
				ApplicationSettings.help_file_name, helpContentFile);
		switch (id) {
		case ApplicationSettings.PROGRESS_DIALOG_SPINNER: // Spinner
			progDialog = new ProgressDialog(this);
			progDialog.setCancelable(false);
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setMessage("Loading...");
			progThread = new ProgressThread(handler, progressActivity, 1000);
			progThread.start();
			return progDialog;
		case ApplicationSettings.PROGRESS_DIALOG_HORIZONTAL: // Horizontal
			progDialog = new ProgressDialog(this);
			progDialog.setCancelable(false);
			progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progDialog.setMax(ApplicationSettings.PROGRESS_BAR_MAX_VALUE);
			progDialog.setMessage("Loading...");
			progThread = new ProgressThread(handler, progressActivity, 1000);
			progThread.start();
			return progDialog;
		default:
			return null;
		}
	}

}
