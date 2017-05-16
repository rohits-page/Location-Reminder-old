package rcs.LocationReminder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract class ActivityListAdapter extends BaseAdapter {

	private final String TAG = this.getClass().getName();

	/** holds the currently selected position */
	protected int _selectedIndex = -1;
	protected Activity _context;
	protected ListView _listView = null;

	/**
	 * create the model-view object that will control the listview
	 * 
	 * @param context
	 *            activity that creates this thing
	 * @param listview
	 *            bind to this listview
	 */
	public ActivityListAdapter() {
		super();
	}

	public int getSelectedPosition() {
		return _selectedIndex;
	}

	public long getItemId(int position) {
		return position;
	}

	public abstract void setSelected(int index);

	public abstract Object getSelectedItem();

	public abstract int getCount();

	public abstract Object getItem(int position);

	public abstract View getView(int position, View convertView,
			ViewGroup parent);

}
