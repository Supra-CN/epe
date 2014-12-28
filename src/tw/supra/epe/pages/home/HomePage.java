package tw.supra.epe.pages.home;

import java.util.HashMap;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.epe.pages.epe.EpePage;
import tw.supra.epe.pages.master.MasterPage;
import tw.supra.epe.pages.worth.WorthPage;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;

public class HomePage extends BaseMainPage implements OnClickListener,
		OnPageChangeListener {
	private static final String LOG_TAG = HomePage.class.getSimpleName();
	private static Handler sHandle = new Handler();
	private static final Class<?>[] PAGES = {WorthPage.class,
			EpePage.class, MasterPage.class };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.page_home, null);

		ViewPager viewPager = (ViewPager) v.findViewById(R.id.view_pager);
		mAdapter = new PageAdapter(getChildFragmentManager());
		viewPager.setAdapter(mAdapter);
		mPageIndicator = (PageIndicator) v.findViewById(R.id.page_indicator);
		mPageIndicator.setViewPager(viewPager);
		mPageIndicator.setOnPageChangeListener(this);
		return v;
	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return c.getText(R.string.indictor_tab_home);
	}

	@Override
	public int getIconResId() {
		return R.drawable.indicator_icon_home;
	}

	private PageAdapter mAdapter;
	private PageIndicator mPageIndicator;

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
				} catch (java.lang.InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
