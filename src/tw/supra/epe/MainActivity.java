package tw.supra.epe;

import java.util.HashMap;

import tw.supra.epe.account.AccountCenter;
import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.epe.pages.MsgPage;
import tw.supra.epe.pages.MyPage;
import tw.supra.epe.pages.TPage;
import tw.supra.epe.pages.home.HomePage;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;

public class MainActivity extends BaseActivity implements OnClickListener,
		OnPageChangeListener {
	
	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	private static final int MSG_PENDING_CHECK_FOR_EXIT = 100000;

	private static Handler sHandle = new Handler();

	private static final Class<?>[] PAGES = { HomePage.class,
			TPage.class, MsgPage.class, MyPage.class };

	private PageAdapter mAdapter;
	private PageIndicator mPageIndicator;

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.activity_main);

		ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		mAdapter = new PageAdapter(getFragmentManager());
		viewPager.setAdapter(mAdapter);
		mPageIndicator = (PageIndicator) findViewById(R.id.page_indicator);
		mPageIndicator.setViewPager(viewPager);
		mPageIndicator.setOnPageChangeListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!AccountCenter.isLogin()) {
			startActivity(new Intent(App.ACTION_LOGIN));
		}
	}

	@Override
	public void onBackPressed() {
		if (sHandle.hasMessages(MSG_PENDING_CHECK_FOR_EXIT)) {
			super.onBackPressed();
		} else {
			sHandle.sendEmptyMessageDelayed(MSG_PENDING_CHECK_FOR_EXIT, 3000);
			Toast.makeText(this, R.string.activity_toast_press_again_to_exit,
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		// setActionTitle(mAdapter.getItem(position).getTitle());
	}

	public class PageAdapter extends FragmentPagerAdapter implements
			IconPagerAdapter {

		private final HashMap<Class<?>, BaseMainPage> PAGE_POOL = new HashMap<Class<?>, BaseMainPage>();

		public PageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return PAGES.length;
		}

		@Override
		public BaseMainPage getItem(int position) {
			BaseMainPage page = PAGE_POOL.get(PAGES[position]);
			if (null == page) {
				try {
					page = (BaseMainPage) PAGES[position].newInstance();
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
//			Log.i(LOG_TAG, "getItem :  pos =" + position + " page" + page);
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
