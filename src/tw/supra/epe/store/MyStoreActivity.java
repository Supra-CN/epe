package tw.supra.epe.store;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.account.AccountCenter;
import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.core.BaseHostFrag;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.network.ui.NetworkRoundedImageView;
import tw.supra.utils.JsonUtils;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;

public class MyStoreActivity extends BaseActivity implements OnClickListener {

	private static final String LOG_TAG = MyStoreActivity.class.getSimpleName();

	private static final Class<?>[] PAGES = { MyStoreProductsPage.class, ActivePage.class };

	private PageAdapter mAdapter;
	private PageIndicator mPageIndicator;

	private NetworkRoundedImageView mLogo;
	private TextView mTvName;
	private TextView mTVAddress;

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
		mLogo = (NetworkRoundedImageView) findViewById(R.id.logo);
		mTvName = (TextView) findViewById(R.id.name);
		mTVAddress = (TextView) findViewById(R.id.address);
		request();

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

	private void request() {
		NetworkCenter.getInstance().putToQueue(
				new RequestMyStore(HANDLER_MY_STORE, AccountCenter
						.getCurrentUser().getShopId()));
	}

	private final NetWorkHandler<EpeRequestInfo> HANDLER_MY_STORE = new NetWorkHandler<EpeRequestInfo>() {

		@Override
		public boolean HandleEvent(RequestEvent event, EpeRequestInfo info) {
			if (event == RequestEvent.FINISH && info.ERROR_CODE.isOK()
					&& info.OBJ != null) {
				JSONObject jo = (JSONObject) info.OBJ;
				try {
					mTvName.setText(JsonUtils.getStrSafely(jo, "shop_name"));
					mTVAddress.setText(JsonUtils.getStrSafely(jo, "address"));
					mLogo.setImageUrl(JsonUtils.getStrSafely(jo, "logo"),
							NetworkCenter.getInstance().getImageLoader());
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			return false;
		}
	};

}
