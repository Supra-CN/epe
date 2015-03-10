package tw.supra.epe.activity;

import java.util.HashMap;

import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.core.BaseFrag;
import tw.supra.epe.mall.FocusBrandPage;
import tw.supra.epe.mall.FocusMallPage;
import tw.supra.utils.Log;
import android.app.Fragment.InstantiationException;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;
import com.yijiayi.yijiayi.R;

public class FocusActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {
	private static final String LOG_TAG = FocusActivity.class.getSimpleName();

	private PageAdapter mAdapter;
	private PageIndicator mPageIndicator;
	private ToggleButton mTbEditable;

	private final Class<?>[] PAGES = { 
			FocusMallPage.class,FocusBrandPage.class };

	public interface EditableChengeListener {
		void onEditableChenge(boolean editable);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		setContentView(R.layout.activity_focus);
		findViewById(R.id.action_back).setOnClickListener(this);

		ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		mAdapter = new PageAdapter(getFragmentManager());
		viewPager.setAdapter(mAdapter);
		mPageIndicator = (PageIndicator) findViewById(R.id.page_indicator);
		mPageIndicator.setViewPager(viewPager);
		mTbEditable = (ToggleButton) findViewById(R.id.action_focus);
		mTbEditable.setOnCheckedChangeListener(this);
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

	private final HashMap<Class<?>, BaseFrag> PAGE_POOL = new HashMap<Class<?>, BaseFrag>();

	public class PageAdapter extends FragmentPagerAdapter implements
			IconPagerAdapter {

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
					page = (BaseFrag) PAGES[position].newInstance();
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
			Log.i(LOG_TAG, "getItem :  pos =" + position + " page" + page);
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

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		for (Object page : PAGE_POOL.values()) {
			if (page instanceof EditableChengeListener) {
				((EditableChengeListener) page).onEditableChenge(isChecked);
			}
		}
	}

	public boolean isEditable() {
		return mTbEditable.isChecked();
	}

}
