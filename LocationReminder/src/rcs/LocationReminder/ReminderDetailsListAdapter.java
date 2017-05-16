package rcs.LocationReminder;

import java.util.List;

import rcs.LocationReminder.BO.ReminderBO;
import rcs.LocationReminder.Shared.SharedApplicationSettings;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class ReminderDetailsListAdapter extends
		ReminderDetailsListAdapterBase {

	public ReminderDetailsListAdapter(Activity context, ListView listview,
			List<ReminderBO> data) {
		super(context, listview);
		this._data = data;
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
	public ReminderBO getSelectedItem() {
		if (_selectedIndex > -1 && _data != null)
			return (ReminderBO) _data.get(_selectedIndex);
		else
			return null;
	}

	@Override
	public ReminderBO getItem(int position) {
		if (position > -1 && _data != null)
			return (ReminderBO) _data.get(position);
		else
			return null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ActivityRowView rv;
		if (SharedApplicationSettings.MODE_DEVELOPMENT)
			Log.d(TAG, "Retrieve View for position " + position);
		rv = new ActivityRowView(_context, (ReminderBO) getItem(position));
		// Handle selection
		return rv.display(position, position == _selectedIndex);

		// return rv;
	}

}
