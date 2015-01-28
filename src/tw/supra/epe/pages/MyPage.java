package tw.supra.epe.pages;

import tw.supra.epe.R;
import tw.supra.epe.account.AccountCenter;
import tw.supra.epe.account.User;
import tw.supra.epe.activity.SettingsActivity;
import tw.supra.epe.activity.UserHomeActivity;
import tw.supra.epe.activity.fav.FavActivity;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.epe.store.ApplyStoreActivity;
import tw.supra.network.NetworkCenter;
import tw.supra.network.ui.NetworkRoundedImageView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MyPage extends BaseMainPage implements OnItemClickListener,
		OnClickListener {
	private final Item ITEM_MY_HOME = new Item(R.string.my_page_item_my_home,
			R.drawable.ic_my_home) {
		public void onItemClick() {
			startActivity(new Intent(getActivity(), UserHomeActivity.class));
		};
	};
	private final Item ITEM_MY_FAV = new Item(R.string.my_page_item_my_fav,
			R.drawable.ic_my_fav) {
		public void onItemClick() {
			startActivity(new Intent(getActivity(), FavActivity.class));
		};
	};
	private final Item ITEM_APPLY_STORE = new Item(
			R.string.my_page_item_apply_store, R.drawable.ic_apply_store) {
		public void onItemClick() {
			startActivity(new Intent(getActivity(), ApplyStoreActivity.class));
		};
	};

	private final Item[] LIST = {
			ITEM_MY_HOME,
			new Item(),
			ITEM_MY_FAV,
			new Item(R.string.my_page_item_my_custom, R.drawable.ic_my_custom),
			new Item(),
			new Item(R.string.my_page_item_my_wardrobe,
					R.drawable.ic_my_wardrobe),
			new Item(R.string.my_page_item_my_type, R.drawable.ic_my_type),
			new Item(R.string.my_page_item_my_diary, R.drawable.ic_my_diary),
			new Item(),
			new Item(R.string.my_page_item_my_focus, R.drawable.ic_my_focus),
			new Item(), ITEM_APPLY_STORE, new Item(),
			new Item(R.string.my_page_item_invite, R.drawable.ic_my_invite),
			new Item() };

	private final BaseAdapter ADAPTER = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Item item = getItem(position);
			if (item.FLAG_DIVIDER) {
				return View.inflate(getActivity(), R.layout.item_divider, null);
			}

			if (convertView == null || !(convertView instanceof TextView)) {
				convertView = View.inflate(getActivity(),
						R.layout.my_page_item, null);
			}

			TextView tv = ((TextView) convertView);
			tv.setText(item.TITLE_RES);
			tv.setCompoundDrawablesWithIntrinsicBounds(item.ICON_RES, 0,
					R.drawable.icon_arrow_right, 0);

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

	private TextView mTvName;
	private TextView mTvScore;
	private NetworkRoundedImageView mIvAvator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.page_my, null);
		ListView listView = (ListView) v.findViewById(R.id.list_view);
		listView.setOnItemClickListener(this);
		listView.setAdapter(ADAPTER);
		v.findViewById(R.id.setting).setOnClickListener(this);
		mTvName = (TextView) v.findViewById(R.id.name);
		mTvScore = (TextView) v.findViewById(R.id.score);
		mIvAvator = (NetworkRoundedImageView) v.findViewById(R.id.avator);
		mIvAvator.setErrorImageResId(R.drawable.ic_launcher);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		updateUI();
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
		LIST[position].onItemClick();
	}

	private class Item {
		final int TITLE_RES;
		final int ICON_RES;
		final boolean FLAG_DIVIDER;

		public Item() {
			TITLE_RES = 0;
			ICON_RES = 0;
			FLAG_DIVIDER = true;
		}

		public Item(int titleRes, int iconRes) {
			TITLE_RES = titleRes;
			ICON_RES = iconRes;
			FLAG_DIVIDER = false;
		}

		public void onItemClick() {
		}
	}

	private void updateUI() {
		User user = AccountCenter.getCurrentUser();
		mIvAvator.setImageUrl(user.getAvatarUrl(), NetworkCenter.getInstance()
				.getImageLoader());
		mTvName.setText(user.getName());
		mTvScore.setText(getString(R.string.my_page_score, user.getScore()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting:
			startActivity(new Intent(getActivity(), SettingsActivity.class));
			break;

		default:
			break;
		}
	}
}
