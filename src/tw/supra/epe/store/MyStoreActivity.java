package tw.supra.epe.store;

import java.util.HashMap;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.core.BaseHostFrag;
import tw.supra.epe.pages.worth.WorthPage;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;

import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;

public class MyStoreActivity extends BaseActivity implements OnClickListener {

	private static final String LOG_TAG = MyStoreActivity.class.getSimpleName();

	private static final Class<?>[] PAGES = { WorthPage.class,
			ActivePage.class };

	private PageAdapter mAdapter;
	private PageIndicator mPageIndicator;

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.activity_my_store);
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

		private final HashMap<Class<?>, BaseHostFrag<MyStoreActivity>> PAGE_POOL = new HashMap<Class<?>, BaseHostFrag<MyStoreActivity>>();

		public PageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return PAGES.length;
		}

		@Override
		public BaseHostFrag<MyStoreActivity> getItem(int position) {
			BaseHostFrag<MyStoreActivity> page = PAGE_POOL.get(PAGES[position]);
			if (null == page) {
				try {
					page = (BaseHostFrag<MyStoreActivity>) PAGES[position]
							.newInstance();
					PAGE_POOL.put(PAGES[position], page);
				} catch (InstantiationException e) {
					e.printStackTrace();
					throw new IllegalStateException(String.format(
							"the page %s is not a legal page",
							PAGES[position].getSimpleName()), e);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new IllegalStateException(String.format(
							"the page %s is not a legal page",
							PAGES[position].getSimpleName()), e);
				}
			}
			// Log.i(LOG_TAG, "getItem :  pos =" + position + " page" + page);
			return page;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getItem(position).getTitle();
		}

		@Override
		public int getIconResId(int index) {
			return getItem(index).getIconResId();
		}
	}
}
