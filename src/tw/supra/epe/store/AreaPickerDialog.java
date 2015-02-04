package tw.supra.epe.store;

import java.util.ArrayList;

import tw.supra.epe.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

public class AreaPickerDialog extends Dialog implements OnChildClickListener,
		OnGroupClickListener {
	private static final String LOG_TAG = AreaPickerDialog.class
			.getSimpleName();

	private final ExpandableListView LIST_VIEW;
	private final AreaAdapter ADAPTER = new AreaAdapter();

	private final ArrayList<AreaItem> DATA_SET;

	public final OnAreaPickedListener LISTENER;

	public interface OnAreaPickedListener {
		public void onAreaPicked(AreaItem area);
	}

	public AreaPickerDialog(Context context, OnAreaPickedListener listener) {
		super(context, R.style.CleanDialog);
		// super(context);
		LISTENER = listener;
		DATA_SET = AreaItem.queryAreas();
		LIST_VIEW = new ExpandableListView(context);
		// LIST_VIEW.setBackgroundColor(context.getResources().getColor(android.R.color.black));
		LIST_VIEW.setAdapter(ADAPTER);
		LIST_VIEW.setOnGroupClickListener(this);
		LIST_VIEW.setOnChildClickListener(this);
		LIST_VIEW.setGroupIndicator(null);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LIST_VIEW);

	}

	private class AreaAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return DATA_SET.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return getGroup(groupPosition).getChilds().size();
		}

		@Override
		public AreaItem getGroup(int groupPosition) {
			return DATA_SET.get(groupPosition);
		}

		@Override
		public AreaItem getChild(int groupPosition, int childPosition) {
			return getGroup(groupPosition).getChilds().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return getGroup(groupPosition).ID;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return getChild(groupPosition, childPosition).ID;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView tv = (TextView) View.inflate(getContext(),
					R.layout.area_item_group, null);

			tv.setText(getGroup(groupPosition).NAME);
			return tv;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView tv = (TextView) View.inflate(getContext(),
					R.layout.area_item_child, null);
			tv.setText(getChild(groupPosition, childPosition).NAME);
			return tv;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		Log.i(LOG_TAG, "onChildClick");
		dismiss();
		if (null != LISTENER) {
			LISTENER.onAreaPicked(ADAPTER
					.getChild(groupPosition, childPosition));
		}
		return true;
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			final int groupPosition, long id) {
//		sHandler.post(new Collapser(groupPosition));
		return false;
	}
	
	private  class Collapser implements Runnable {
		final int GROUP_POSITION;
		
		public Collapser(int groupPosition) {
			GROUP_POSITION = groupPosition;
		}
		
		@Override
		public void run() {
			for (int i = 0; i < ADAPTER.getGroupCount(); i++) {
				if (i != GROUP_POSITION) {
					LIST_VIEW.collapseGroup(i);
				}
			}		
			LIST_VIEW.setSelectedGroup(GROUP_POSITION);
		}
	};
	
	private static Handler sHandler = new Handler(); 
}
