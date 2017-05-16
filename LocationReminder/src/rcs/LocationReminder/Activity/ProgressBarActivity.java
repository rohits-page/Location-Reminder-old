package rcs.LocationReminder.Activity;

import rcs.LocationReminder.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

public class ProgressBarActivity extends Activity {
	private LinearLayout linProgressBar;
	private final Handler uiHandler = new Handler();
	private boolean isUpdateRequired = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress_bar);

		basicInitializations();
	}

	public void basicInitializations() {

		linProgressBar = (LinearLayout) findViewById(R.id.lin_progress_bar);
		linProgressBar.setVisibility(View.VISIBLE);

		try {
			new Thread() {
				public void run() {
					initializeApp();
					uiHandler.post(new Runnable() {
						public void run() {
							if (isUpdateRequired) {
								// TODO:
							} else {
								linProgressBar.setVisibility(View.GONE);
								finish();
							}
						}
					});
				}

				public void initializeApp() {
					// Initialize application data here
				}
			}.start();
		} catch (Exception e) {
		}
	}
}
