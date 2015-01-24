package tw.supra.epe.store;

import java.util.HashMap;

import tw.supra.epe.App;
import tw.supra.epe.R;
import tw.supra.epe.account.AccountCenter;
import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.core.BaseHostFrag;
import tw.supra.epe.core.BaseMainPage;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;

public class ApplyStoreActivity extends BaseActivity implements OnClickListener {

	private static final String LOG_TAG = ApplyStoreActivity.class
			.getSimpleName();

	private static final Class<?>[] PAGES = {  ApplyMallPage.class,ApplyStorePage.class };

	private PageAdapter mAdapter;
	private PageIndicator mPageIndicator;

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.activity_create_store);
		findViewById(R.id.action_back).setOnClickListener(this);

		ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		mAdapter = new PageAdapter(getFragmentManager());
		viewPager.setAdapter(mAdapter);
		mPageIndicator = (PageIndicator) findViewById(R.id.page_indicator);
		mPageIndicator.setViewPager(viewPager);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!AccountCenter.isLogin()) {
			startActivity(new Intent(App.ACTION_LOGIN));
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

	public class PageAdapter extends FragmentPagerAdapter implements
			IconPagerAdapter {

		private final HashMap<Class<?>, BaseHostFrag<ApplyStoreActivity>> PAGE_POOL = new HashMap<Class<?>, BaseHostFrag<ApplyStoreActivity>>();

		public PageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return PAGES.length;
		}

		@Override
		public BaseHostFrag<ApplyStoreActivity> getItem(int position) {
			BaseHostFrag<ApplyStoreActivity> page = PAGE_POOL.get(PAGES[position]);
			if (null == page) {
				try {
					page = (BaseHostFrag<ApplyStoreActivity>) PAGES[position].newInstance();
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
