package tw.supra.epe.account;

import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.core.BaseFrag;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.PageIndicator;
import com.yijiayi.yijiayi.R;

public class LoginActivity extends BaseActivity {
	private LoginFrag mLoginFrag;
	private RegFrag mRegFrag;

	private FragmentStatePagerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
		pager.setAdapter(getAdapter());
		((PageIndicator) findViewById(R.id.indicator)).setViewPager(pager);
	}

	private FragmentStatePagerAdapter getAdapter() {
		if (null == mAdapter) {
			mAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

				@Override
				public BaseFrag getItem(int pos) {
					switch (pos) {
					case 0:
						return getLoginFrag();
					case 1:
						return getRegFrag();
					default:
						throw new IllegalStateException(
								"Illegal page postion check out getCount()");
					}
				}

				@Override
				public int getCount() {
					return 2;
				}

				@Override
				public CharSequence getPageTitle(int position) {
					return getItem(position).getTitle();
				}
			};

		}
		return mAdapter;
	}

	private LoginFrag getLoginFrag() {
		if (null == mLoginFrag) {
			mLoginFrag = new LoginFrag();
		}
		return mLoginFrag;
	}

	private RegFrag getRegFrag() {
		if (null == mRegFrag) {
			mRegFrag = new RegFrag();
		}
		return mRegFrag;
	}

}
