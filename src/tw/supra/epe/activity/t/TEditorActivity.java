package tw.supra.epe.activity.t;

import tw.supra.epe.App;
import tw.supra.epe.R;
import tw.supra.epe.core.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TEditorActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {

	private static final String LOG_TAG = TEditorActivity.class
			.getSimpleName();

	private final Item[] LIST = {
			new Item(R.string.settings_item_about),
			new Item(R.string.settings_item_clear),
			new Item(R.string.settings_item_check_update),
			new Item(R.string.settings_item_vote),
			new Item(R.string.settings_item_feedback),
			};

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		findViewById(R.id.action_back).setOnClickListener(this);
		findViewById(R.id.logout).setOnClickListener(this);
		ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setOnItemClickListener(this);
		listView.setAdapter(ADAPTER);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_back:
			onBackPressed();
			break;
		case R.id.logout:
			startActivity(new Intent(App.ACTION_LOGIN));
			finish();
			break;
		default:
			break;
		}
	}

	private final BaseAdapter ADAPTER = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Item item = getItem(position);

			if (convertView == null) {
				convertView = View.inflate(TEditorActivity.this,R.layout.settings_activity_item, null);
			}

			TextView tv = ((TextView) convertView);
			tv.setText(item.TITLE_RES);

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

	private class Item {
		final int TITLE_RES;

		public Item(int titleRes) {
			TITLE_RES = titleRes;
		}

		public void onItemClick() {
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		LIST[position].onItemClick();
	}
}
