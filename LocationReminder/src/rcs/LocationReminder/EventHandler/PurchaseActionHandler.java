package rcs.LocationReminder.EventHandler;

import rcs.LocationReminder.Shared.SharedApplicationSettings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;

public class PurchaseActionHandler implements OnClickListener {

	Activity mActivity;
	boolean flagActivityClose = false;

	/**
	 * 
	 * @param activity
	 * @param flagButtonPurchase
	 *            TRUE if this the handler for PURCHASE button, False otherwise
	 * @param flagActivityClose
	 *            TRUE if the calling activity needs to be closed after the
	 *            necessary action is taken
	 */
	public PurchaseActionHandler(Activity activity, boolean flagActivityClose) {
		mActivity = activity;
		this.flagActivityClose = flagActivityClose;
	}

	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case (DialogInterface.BUTTON_POSITIVE): {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id="
					+ SharedApplicationSettings.paid_app_package_name));
			mActivity.startActivity(intent);
			if (flagActivityClose)
				mActivity.finish();
			dialog.dismiss();
			break;
		}
		case (DialogInterface.BUTTON_NEGATIVE): {
			dialog.dismiss();
			if (flagActivityClose)
				mActivity.finish();

		}
		}
	}

}
