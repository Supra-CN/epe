package tw.supra.epe.store;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import tw.supra.epe.activity.brand.RequestBrandFocusStatus;
import tw.supra.epe.activity.brand.RequestPushBrandFocusStatus;
import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.core.BaseFrag;
import tw.supra.epe.mall.MallInfo;
import tw.supra.epe.mall.RequestMall;
import tw.supra.epe.mall.RequestPushMallFocusStatus;
import tw.supra.epe.mall.RequestShop;
import tw.supra.epe.mall.ShopInfo;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;
import com.yijiayi.yijiayi.R;

public class StoreActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener, NetWorkHandler<EpeRequestInfo> {

	private static final String LOG_TAG = StoreActivity.class.getSimpleName();

	public static final String EXTRA_ID = "extra_id";
	public static final String EXTRA_FOCUS_ID = "extra_focus_id";
	public static final String EXTRA_BROAD_NAME = "extra_broad_name";
	public static final String EXTRA_MALL_NAME = "extra_mall_name";
	public static final String EXTRA_IS_STORE = "extra_is_store";

	public static final String ACTION_BY_DISCOUNT = "by_discount";
	public static final String ACTION_BY_TIME = "by_time";
	public static final String ACTION_BY_HOT = "by_hot";

	private final Page[] PAGES = {
			new Page(R.string.store_product_label_new,
					ProductsOfStoreInfo.SORT_BY_TIME),
			new Page(R.string.store_product_label_discount,
					ProductsOfStoreInfo.SORT_BY_DISCOUNT),
			new Page(R.string.store_product_label_hot,
					ProductsOfStoreInfo.SORT_BY_HOT), };

	private class Page {
		final Class<?> CLAZZ = StoreProductsPage.class;
		final int LABEL;
		final String ACTION;

		public Page(int label, String action) {
			LABEL = label;
			ACTION = action;
		}
	}

	private PageAdapter mAdapter;
	private PageIndicator mPageIndicator;

	private String mId;

	private String mFocusId;
	private boolean mIsStore;

	private ToggleButton mTbFocus;

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		mId = intent.getStringExtra(EXTRA_ID);
		mFocusId = intent.getStringExtra(EXTRA_FOCUS_ID);
		mIsStore = intent.getBooleanExtra(EXTRA_IS_STORE, false);
		String brandName = intent.getStringExtra(EXTRA_BROAD_NAME);
		String mallName = intent.getStringExtra(EXTRA_MALL_NAME);

		setContentView(R.layout.activity_store);

		mTbFocus = (ToggleButton) findViewById(R.id.action_focus);
		mTbFocus.setOnCheckedChangeListener(this);

		String label = TextUtils.isEmpty(brandName) ? mallName : brandName
				+ "." + mallName;
		((TextView) findViewById(R.id.label)).setText(label);
		findViewById(R.id.action_back).setOnClickListener(this);

		if (mIsStore) {
			requestMallStatus(mFocusId);
		} else {
			initUI();
			requestShopInfo(mId);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_back:
			onBackPressed();
			break;
		default:
			break;
		}
	}

	private final NetWorkHandler<EpeRequestInfo> HANDLER_BRAND_STATUS = new NetWorkHandler<EpeRequestInfo>() {

		@Override
		public boolean HandleEvent(RequestEvent event, EpeRequestInfo info) {
			if (event == RequestEvent.FINISH && info.ERROR_CODE.isOK()) {
				mTbFocus.setOnCheckedChangeListener(null);
				mTbFocus.setChecked(true);
				mTbFocus.setOnCheckedChangeListener(StoreActivity.this);
			}
			return false;
		}
	};

	private void requestBrandStatus(String brandId) {
		NetworkCenter.getInstance().putToQueue(
				new RequestBrandFocusStatus(HANDLER_BRAND_STATUS, brandId));
	}

	private final NetWorkHandler<MallInfo> HANDLER_MALL_STATUS = new NetWorkHandler<MallInfo>() {

		@Override
		public boolean HandleEvent(RequestEvent event, MallInfo info) {
			Log.i(LOG_TAG, "HandleEvent FINISH : " + info);
			if (RequestEvent.FINISH == event) {
				if (info.ERROR_CODE.isOK()) {
					try {
						String following = info.resultJo
								.getString(MallInfo.FOLLOWING);
						mTbFocus.setOnCheckedChangeListener(null);
						mTbFocus.setChecked("1".equals(following));
						mTbFocus.setOnCheckedChangeListener(StoreActivity.this);
						mId = info.resultJo.getJSONArray(MallInfo.BRAND)
								.getJSONArray(0).getJSONObject(0)
								.getString("shop_id");

						initUI();
						requestShopInfo(mId);

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			return false;

		}
	};
	private final NetWorkHandler<ShopInfo> HANDLER_SHOP_STATUS = new NetWorkHandler<ShopInfo>() {

		@Override
		public boolean HandleEvent(RequestEvent event, ShopInfo info) {
			Log.i(LOG_TAG, "HandleEvent FINISH : " + info);
			if (RequestEvent.FINISH == event) {
				if (info.ERROR_CODE.isOK()) {
					try {
						String address = info.resultJo.getString("address");
						if (!TextUtils.isEmpty(address)) {
							TextView tv = ((TextView) findViewById(R.id.address));
							tv.setText(getString(R.string.shop_info_address,
									address));
							tv.setVisibility(View.VISIBLE);
						}
						JSONArray ja = info.resultJo.getJSONArray("activity");
						if (ja != null && ja.length() > 0 && !ja.isNull(0)) {
							String activity = ja.getJSONObject(0).getString(
									"activity_title");
							if (!TextUtils.isEmpty(activity)) {
							TextView tv = ((TextView) findViewById(R.id.activity));
							tv.setText(getString(R.string.shop_info_activity,
									activity));
							tv.setVisibility(View.VISIBLE);}
						}
						if(!mIsStore){
							mFocusId = info.resultJo.getString("brand_id");
							requestBrandStatus(mFocusId);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			return false;

		}
	};

	private void requestMallStatus(String mallId) {
		NetworkCenter.getInstance().putToQueue(
				new RequestMall(HANDLER_MALL_STATUS, new MallInfo(mallId)));
	}

	private void requestShopInfo(String shopId) {
		NetworkCenter.getInstance().putToQueue(
				new RequestShop(HANDLER_SHOP_STATUS, new ShopInfo(shopId)));
	}

	private void initUI() {
		ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		mAdapter = new PageAdapter(getFragmentManager());
		viewPager.setAdapter(mAdapter);
		mPageIndicator = (PageIndicator) findViewById(R.id.page_indicator);
		mPageIndicator.setViewPager(viewPager);
	}

	public class PageAdapter extends FragmentPagerAdapter implements
			IconPagerAdapter {

		private final HashMap<Page, BaseFrag> PAGE_POOL = new HashMap<Page, BaseFrag>();

		public PageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return PAGES.length;
		}

		@Override
		public BaseFrag getItem(int position) {
			BaseFrag page = PAGE_POOL.get(PAGES[position]);
			if (null == page) {
				try {
					page = (BaseFrag) PAGES[position].CLAZZ.newInstance();
					Bundle args = new Bundle();
					args.putString(StoreProductsPage.ARG_SORT,
							PAGES[position].ACTION);
					args.putString(StoreProductsPage.ARG_STORE_ID, mId);
					page.setArguments(args);
					PAGE_POOL.put(PAGES[position], page);
				} catch (InstantiationException e) {
					e.printStackTrace();
					throw new IllegalStateException(String.format(
							"the page %s is not a legal page",
							PAGES[position].LABEL), e);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new IllegalStateException(String.format(
							"the page %s is not a legal page",
							PAGES[position].LABEL), e);
				}
			}
			// Log.i(LOG_TAG, "getItem :  pos =" + position + " page" + page);
			return page;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getString(PAGES[position].LABEL);
		}

		@Override
		public int getIconResId(int index) {
			return getItem(index).getIconResId();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (mIsStore) {
			NetworkCenter.getInstance().putToQueue(
					new RequestPushMallFocusStatus(this, mFocusId, isChecked));
		} else {
			NetworkCenter.getInstance().putToQueue(
					new RequestPushBrandFocusStatus(this, mFocusId, isChecked));
		}

	}

	@Override
	public boolean HandleEvent(RequestEvent event, EpeRequestInfo info) {
		if (event == RequestEvent.FINISH) {
			if (!info.ERROR_CODE.isOK()) {
				mTbFocus.setOnCheckedChangeListener(null);
				mTbFocus.toggle();
				mTbFocus.setOnCheckedChangeListener(this);
			}
		}
		return false;
	}
}
