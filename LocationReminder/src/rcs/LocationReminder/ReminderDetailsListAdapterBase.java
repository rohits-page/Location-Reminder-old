package rcs.LocationReminder;

import java.util.List;

import rcs.LocationReminder.BO.ReminderBO;
import rcs.LocationReminder.Shared.SharedApplicationSettings;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


public abstract class ReminderDetailsListAdapterBase extends
		ActivityListAdapter {

	protected List<?> _data;

	public ReminderDetailsListAdapterBase(Activity context, ListView listview) {
		super();
		// save the activity/context reference
		_context = context;
		_listView = listview;

		// bind this model (and cell renderer) to the listview
		listview.setAdapter(this);
	}

	public void updateData(List<?> data){
		_data=data;
	}
	
	private final String TAG = this.getClass().getName();

	/**
	 * called when item in listview is selected... fires a model changed
	 * event...
	 * 
	 * @param index
	 *            index of item selected in listview. if -1 then it's
	 *            unselected.
	 */
	@Override
	public void setSelected(int index) {
		if (index == -1) {
			// unselected
			// _listView.setSelected(false);
		} else {
			// selected index...
			_selectedIndex = index;
			_listView.setSelected(true);
			_listView.setSelection(index);
			Log.d(TAG,
					"Selected position set to "
							+ _listView.getSelectedItemPosition() + " for "
							+ _listView.getId() + " for object "
							+ _listView.toString());
		}
		// notify the model that the data has changed, need to update the view
		notifyDataSetChanged();
		Log.i(getClass().getSimpleName(),
				"updating _selectionIndex with index and firing model-change-event: index="
						+ index);
	}

	@Override
	public int getCount() {
		if (null != _data)
			return _data.size();
		else
			return 0;
	}

	protected class ActivityRowView {
		private TextView lblActivityName;
		private TextView lblActivityDetail1;
		private TextView lblActivityDetail2;
		private final String TAG = this.getClass().getName();
		Context _context;
		View _view = null;

		public ActivityRowView(Context context, Object activityDetails) {
			_context = context;
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (null != inflater) {
				_view = inflater.inflate(R.layout.detaillistadapterview, null);
			}
			createUI(_context, activityDetails);
		}

		private void createUI(Context context, Object activityDetails) {
			lblActivityName = (TextView) _view
					.findViewById(R.id.lblActivityName);
			lblActivityDetail1 = (TextView) _view
					.findViewById(R.id.lblActivityDetail1);
			lblActivityDetail2 = (TextView) _view
					.findViewById(R.id.lblActivityDetail2);

			if (null == activityDetails) {
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					Log.d(TAG, "Null Object");
			} else {

				ReminderBO activity_details = (ReminderBO) activityDetails;
				if (SharedApplicationSettings.MODE_DEVELOPMENT)
					Log.d(TAG, "Displaying details for ReminderBO \n"
							+ activity_details.toString());
				lblActivityName.setText((activity_details).getReminderName());
				lblActivityDetail1.setText(activity_details.getReminderAddress());
				if (activity_details.isOneTimeReminder())
					lblActivityDetail2.setText("One Time");
				else
					lblActivityDetail2.setText("Recurring");

				lblActivityName.setTextColor(Color.WHITE);
				lblActivityDetail1.setTextColor(Color.LTGRAY);
				lblActivityDetail2.setTextColor(Color.GRAY);
			}
		}

		public View display(int index, boolean selected) {
			if (SharedApplicationSettings.MODE_DEVELOPMENT)
				Log.d(TAG, "Item Selected " + selected);
			if (selected) {
				lblActivityName.setTextSize((float) 20.0);
				lblActivityDetail1.setTextSize((float) 20.0);
				lblActivityDetail2.setTextSize((float) 20.0);
			} else {
				lblActivityName.setTextSize((float) 14.0);
				lblActivityDetail1.setTextSize((float) 14.0);
				lblActivityDetail2.setTextSize((float) 14.0);
			}

			return _view;
		}

	}
}
