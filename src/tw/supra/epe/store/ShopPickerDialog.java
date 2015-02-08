package tw.supra.epe.store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ShopPickerDialog extends Dialog implements
		NetWorkHandler<EpeRequestInfo>, OnChildClickListener {
	private static final String LOG_TAG = ShopPickerDialog.class
			.getSimpleName();

	public final OnShopPickedListener LISTENER;

	public final ObjMall MALL;

	public interface OnShopPickedListener {
		public void OnShopPicked(ObjShop store);
	}

	public ShopPickerDialog(Context context,
			OnShopPickedListener listener, ObjMall mall) {
		super(context, R.style.CleanDialog);
		LISTENER = listener;
		MALL = mall;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new ProgressBar(getContext()));
		request();
	}

	private void request() {
		NetworkCenter.getInstance().putToQueue(
				new RequestMallStore(this, MALL.mallId));
	}

	private class ShopAdapter extends BaseExpandableListAdapter {

		private final JSONObject JO;
		private final JSONArray NAMES;

		public ShopAdapter(JSONObject jo) {
			JO = jo;
			NAMES = JO.names();
		}

		@Override
		public int getGroupCount() {
			return NAMES.length();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			try {
				return JO.getJSONArray(getGroup(groupPosition)).length();
			} catch (JSONException e) {
				e.printStackTrace();
				return 0;
			}
		}

		@Override
		public String getGroup(int groupPosition) {
			try {
				return NAMES.getString(groupPosition);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public JSONObject getChild(int groupPosition, int childPosition) {
			try {
				return JO.getJSONArray(getGroup(groupPosition)).getJSONObject(
						childPosition);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
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

			tv.setText(getGroup(groupPosition));
			return tv;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView tv = (TextView) View.inflate(getContext(),
					R.layout.area_item_child, null);
			try {
				tv.setText(getChild(groupPosition, childPosition).getString(
						"brand_name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return tv;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	};

	@Override
	public boolean HandleEvent(RequestEvent event, EpeRequestInfo info) {

		switch (event) {
		case FINISH:
			if (info.ERROR_CODE.isOK()) {
				ExpandableListView lv = new ExpandableListView(getContext());
				TextView tv = new TextView(getContext());
				setContentView(lv);
				addContentView(tv, new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
				lv.setAdapter(new ShopAdapter((JSONObject) info.OBJ));
				lv.setOnChildClickListener(this);
				lv.setEmptyView(tv);
				lv.setGroupIndicator(null);
			}else{
				TextView tv = new TextView(getContext());
				tv.setText(R.string.store_apply_pick_mall_store_empty);
				setContentView(tv);
			}
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		ShopAdapter adapter = (ShopAdapter) parent.getExpandableListAdapter();
		ObjShop shop = new ObjShop();
		shop.floor = adapter.getGroup(groupPosition);
		JSONObject jo = adapter.getChild(groupPosition, childPosition);
		try {
			shop.brandId = jo.getString("brand_id");
			shop.mallId = MALL.mallId;
			shop.desc = shop.floor + " " + jo.getString("brand_name");
			LISTENER.OnShopPicked(shop);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		dismiss();
		return false;
	}
}
