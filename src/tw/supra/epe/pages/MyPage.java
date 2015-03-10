package tw.supra.epe.pages;

import java.util.ArrayList;

import tw.supra.epe.account.AccountCenter;
import tw.supra.epe.account.RequestUserInfo;
import tw.supra.epe.account.User;
import tw.supra.epe.account.UserInfo;
import tw.supra.epe.account.UserInfoEditorActivity;
import tw.supra.epe.activity.FocusActivity;
import tw.supra.epe.activity.SettingsActivity;
import tw.supra.epe.activity.UserHomeActivity;
import tw.supra.epe.activity.fav.FavActivity;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.epe.store.ApplyStoreActivity;
import tw.supra.epe.store.MyStoreActivity;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.network.ui.NetworkRoundedImageView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yijiayi.yijiayi.R;

public class MyPage extends BaseMainPage implements OnItemClickListener,
		NetWorkHandler<UserInfo>, OnRefreshListener<ListView>, OnClickListener {

	private PullToRefreshListView mListView;

	private static final int REQUEST_CODE_PUSH_USER_INFO = 269;
	private static final int REQUEST_CODE_APPLY = 268;

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
	private final Item ITEM_MY_FOCUS = 
			new Item(R.string.my_page_item_my_focus,
					R.drawable.ic_my_focus){
		public void onItemClick() {
			startActivity(new Intent(getActivity(), FocusActivity.class));
		};
	};

	private final Item ITEM_APPLY_STORE = new Item(
			R.string.my_page_item_apply_store, R.drawable.ic_apply_store) {
		public void onItemClick() {
			startActivityForResult(new Intent(getActivity(), ApplyStoreActivity.class),REQUEST_CODE_APPLY);
		};
	};

	private final Item ITEM_MY_STORE = new Item(R.string.my_page_item_my_store,
			R.drawable.ic_apply_store) {
		public void onItemClick() {
			startActivity(new Intent(getActivity(), MyStoreActivity.class));
//			startActivityForResult(new Intent(getActivity(), MyStoreActivity.class),REQUEST_CODE_APPLY);
		};
	};

	private final ArrayList<Item> LIST = new ArrayList<Item>();

	private void setupList() {
		User user = AccountCenter.getCurrentUser();

		LIST.clear();
		LIST.add(ITEM_MY_HOME);
		if (User.SHOP_MAN_OK.equals(user.getShopMan())) {
			LIST.add(ITEM_MY_STORE);
		}
		LIST.add(new Item());
		LIST.add(ITEM_MY_FAV);
		// new Item(R.string.my_page_item_my_custom,
		// R.drawable.ic_my_custom),
		// new Item(),
		// new Item(R.string.my_page_item_my_wardrobe,
		// R.drawable.ic_my_wardrobe),
		// new Item(R.string.my_page_item_my_type, R.drawable.ic_my_type),
		// new Item(R.string.my_page_item_my_diary, R.drawable.ic_my_diary),
		// new Item(),
		LIST.add(ITEM_MY_FOCUS);
		if (User.SHOP_MAN_NO.equals(user.getShopMan())) {
			LIST.add(new Item());
			LIST.add(ITEM_APPLY_STORE);
		}
		if (User.SHOP_MAN_HOLD.equals(user.getShopMan())) {
			LIST.add(new Item());
			LIST.add(new Item(R.string.my_page_item_apply_store_on_hold,
					R.drawable.ic_apply_store));
		}
		LIST.add(new Item());
		LIST.add(new Item(R.string.my_page_item_invite, R.drawable.ic_my_invite));
		// new Item()
	}

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
			return LIST.get(position);
		}

		@Override
		public int getCount() {
			return LIST.size();
		}

		public boolean isEnabled(int position) {
			return !getItem(position).FLAG_DIVIDER;
		};

	};

	private TextView mTvName;
	private TextView mTvScore;
	private NetworkRoundedImageView mIvAvator;

	private static Handler sHandler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.page_my, null);
		mListView = (PullToRefreshListView) v.findViewById(R.id.list_view);
		mListView.setOnRefreshListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(ADAPTER);
		mListView.setDividerDrawable(null);
		v.findViewById(R.id.setting).setOnClickListener(this);
		v.findViewById(R.id.user_info_container).setOnClickListener(this);
		mTvName = (TextView) v.findViewById(R.id.name);
		mTvScore = (TextView) v.findViewById(R.id.score);
		mIvAvator = (NetworkRoundedImageView) v.findViewById(R.id.avator);
		mIvAvator.setErrorImageResId(R.drawable.ic_launcher);
		return v;
	}

	private void refresh() {
		sHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mListView.setRefreshing();
			}
		}, 500);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		refresh();
	}

	@Override
	public void onResume() {
		super.onResume();
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
		LIST.get(position - 1).onItemClick();
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
		setupList();
		ADAPTER.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting:
			startActivity(new Intent(getActivity(), SettingsActivity.class));
			break;
		case R.id.user_info_container:
			startActivityForResult(new Intent(getActivity(),
					UserInfoEditorActivity.class), REQUEST_CODE_PUSH_USER_INFO);
			break;

		default:
			break;
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// mListView.onRefreshComplete();
		updateUI();
		request();
	}

	private void request() {
		NetworkCenter.getInstance().putToQueue(
				new RequestUserInfo(this, new UserInfo(AccountCenter
						.getCurrentUserUid())));
	}

	@Override
	public boolean HandleEvent(RequestEvent event, UserInfo info) {
		switch (event) {
		case FINISH:
			mListView.onRefreshComplete();
			updateUI();
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Activity.RESULT_OK == resultCode) {
			mListView.setRefreshing();
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

}
