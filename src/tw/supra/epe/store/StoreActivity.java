package tw.supra.epe.store;

import java.util.HashMap;

import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.core.BaseFrag;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;
import com.yijiayi.yijiayi.R;

public class StoreActivity extends BaseActivity implements OnClickListener {

	private static final String LOG_TAG = StoreActivity.class.getSimpleName();

	public static final String EXTRA_MB_ID = "extra_mb_id";
	public static final String EXTRA_BROAD_NAME = "extra_broad_name";
	public static final String EXTRA_MALL_NAME = "extra_mall_name";

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

	private String mMbId;

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		mMbId = intent.getStringExtra(EXTRA_MB_ID);
		String brandName = intent.getStringExtra(EXTRA_BROAD_NAME);
		String mallName = intent.getStringExtra(EXTRA_MALL_NAME);

		setContentView(R.layout.activity_store);

		String label = TextUtils.isEmpty(brandName) ? mallName : brandName
				+ "." + mallName;
		((TextView) findViewById(R.id.label)).setText(label);
		findViewById(R.id.action_back).setOnClickListener(this);

		ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		mAdapter = new PageAdapter(getFragmentManager());
		viewPager.setAdapter(mAdapter);
		mPageIndicator = (PageIndicator) findViewById(R.id.page_indicator);
		mPageIndicator.setViewPager(viewPager);
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
					args.putString(StoreProductsPage.ARG_STORE_ID, mMbId);
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
}
