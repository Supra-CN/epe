package tw.supra.epe.activity;

import java.util.ArrayList;

import tw.supra.epe.NeedLoginPage;
import tw.supra.epe.account.AccountCenter;
import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.epe.pages.MyPage;
import tw.supra.epe.pages.TPage;
import tw.supra.epe.pages.home.HomePage;
import tw.supra.epe.pages.msg.MsgPage;
import tw.supra.utils.Log;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;
import com.yijiayi.yijiayi.R;

public class MainActivity extends BaseActivity implements OnClickListener,
		OnPageChangeListener {

	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	private static final int MSG_PENDING_CHECK_FOR_EXIT = 100000;

	private static Handler sHandle = new Handler();

	private ViewPager mViewPager;
	private PageAdapter mAdapter;
	private PageIndicator mPageIndicator;

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initPage();
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mPageIndicator = (PageIndicator) findViewById(R.id.page_indicator);
		mPageIndicator.setOnPageChangeListener(this);
		mAdapter = new PageAdapter(getFragmentManager());
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOffscreenPageLimit(mAdapter.getCount());
		mPageIndicator.setViewPager(mViewPager);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isCurrentUser()) {
			finish();
			startActivity(getIntent());
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

	public void initPage() {
		Log.i(LOG_TAG, "reset");
		PAGES.clear();
		PAGES.add(new HomePage());
		PAGES.add(new TPage());
		if (AccountCenter.isLogin()) {
			PAGES.add(new MsgPage());
			PAGES.add(new MyPage());
		} else {
			PAGES.add(new NeedLoginPage() {
				@Override
				protected CharSequence getDefaultTitle(Context c) {
					return c.getText(R.string.indictor_tab_msg);
				}

				@Override
				public int getIconResId() {
					return R.drawable.indicator_icon_msg;
				}
			});
			PAGES.add(new NeedLoginPage() {
				@Override
				protected CharSequence getDefaultTitle(Context c) {
					return c.getText(R.string.indictor_tab_my);
				}

				@Override
				public int getIconResId() {
					return R.drawable.indicator_icon_my;
				}
			});
		}
	}

	private final ArrayList<BaseMainPage> PAGES = new ArrayList<BaseMainPage>();

	public class PageAdapter extends FragmentPagerAdapter implements
			IconPagerAdapter {

		public PageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return PAGES.size();
		}

		@Override
		public BaseMainPage getItem(int position) {
			Log.i(LOG_TAG, "getItem");
			return PAGES.get(position);
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

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {
		if (NeedLoginPage.class.isInstance(mAdapter.getItem(position))) {
			AccountCenter.doLogin(this);
		}
	}

}
