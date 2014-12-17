package tw.supra.epe.pages;

import tw.supra.epe.App;
import tw.supra.epe.R;
import tw.supra.epe.core.BaseMainPage;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MyPage extends BaseMainPage implements OnItemClickListener {
	private final Item ITEM_LOG_OUT = new Item(R.string.my_page_item_log_out) {
		public void onItemClick() {
			startActivity(new Intent(App.ACTION_LOGIN));
		};
	};

	private final Item[] LIST = { new Item(R.string.my_page_item_my_home),
			new Item(R.string.my_page_item_my_fav),
			new Item(R.string.my_page_item_my_custom),
			new Item(R.string.my_page_item_my_wardrobe),
			new Item(R.string.my_page_item_my_type),
			new Item(R.string.my_page_item_my_diary),
			new Item(R.string.my_page_item_my_focus),
			new Item(R.string.my_page_item_apply_store),
			new Item(R.string.my_page_item_invite), ITEM_LOG_OUT };

	private final BaseAdapter ADAPTER = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getActivity(),
						R.layout.my_page_item, null);
			}
			((TextView) convertView).setText(getItem(position).TITLE_RES);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public Item getItem(int position) {
			return LIST[position];
		}

		@Override
		public int getCount() {
			return LIST.length;
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.page_my, null);
		ListView listView = (ListView) v.findViewById(R.id.list_view);
		listView.setOnItemClickListener(this);
		listView.setAdapter(ADAPTER);
		return v;
	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return c.getText(R.string.indictor_tab_my);
	}

	@Override
	public int getIconResId() {
		return R.drawable.indicator_icon_my;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		((Item) ADAPTER.getItem(position)).onItemClick();
	}

	private class Item {
		final int TITLE_RES;

		public Item(int titleRes) {
			TITLE_RES = titleRes;
		}

		public void onItemClick() {
		}
	}
}
