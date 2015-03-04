package tw.supra.epe.store;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.account.AccountCenter;
import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.core.BaseFrag;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.network.ui.NetworkRoundedImageView;
import tw.supra.utils.JsonUtils;
import android.app.AlertDialog.Builder;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;

public class MyStoreActivity extends BaseActivity implements OnClickListener,
		DialogInterface.OnClickListener {

	private static final String LOG_TAG = MyStoreActivity.class.getSimpleName();

	private final ArrayList<Page> PAGES = new ArrayList<MyStoreActivity.Page>();

	private void setupPages() {
		PAGES.clear();
		PAGES.add(new Page(R.string.my_store_label_product) {

			@Override
			BaseFrag create() {
				StoreProductsPage page = new StoreProductsPage();
				Bundle args = new Bundle();
				args.putString(StoreProductsPage.ARG_STORE_ID, AccountCenter
						.getCurrentUser().getShopId());
				page.setArguments(args);
				return page;
			}
		});

		PAGES.add(new Page(R.string.my_store_label_active) {

			@Override
			BaseFrag create() {
				return new ActivityPage();
			}
		});
		mAdapter.notifyDataSetChanged();
		mPageIndicator.notifyDataSetChanged();
	}

	// MyStoreProductsPage.class,
	// ActivePage.class };

	private PageAdapter mAdapter;
	private PageIndicator mPageIndicator;

	private NetworkRoundedImageView mLogo;
	private TextView mTvName;
	private TextView mTVAddress;

	String mMallId;
	String mBrandId;

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_store);
		findViewById(R.id.action_back).setOnClickListener(this);
		findViewById(R.id.action_create).setOnClickListener(this);

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
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setupPages();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_back:
			onBackPressed();
			break;
		case R.id.action_create:
			showCreateDialog();
			break;
		default:
			break;
		}
	}

	private void showCreateDialog() {
		Builder builder = new Builder(this);
		builder.setTitle(R.string.my_store_create_dialog_title);
		builder.setNegativeButton(R.string.my_store_create_dialog_btn_product,
				this);
		builder.setPositiveButton(R.string.my_store_create_dialog_btn_activity,
				this);
		builder.show();
	}

	public class PageAdapter extends FragmentPagerAdapter implements
			IconPagerAdapter {

		private final HashMap<Page, BaseFrag> PAGE_POOL = new HashMap<Page, BaseFrag>();

		public PageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return PAGES.size();
		}

		@Override
		public BaseFrag getItem(int position) {
			BaseFrag page = PAGE_POOL.get(PAGES.get(position));
			if (null == page) {
				page = PAGES.get(position).create();
				PAGE_POOL.put(PAGES.get(position), page);
			}
			// Log.i(LOG_TAG, "getItem :  pos =" + position + " page" + page);
			return page;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getString(PAGES.get(position).LABEL);
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
					mMallId = JsonUtils.getStrSafely(jo, "mall_id");
					mBrandId = JsonUtils.getStrSafely(jo, "brand_id");
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

	private abstract class Page {
		final int LABEL;

		public Page(int label) {
			LABEL = label;
		}

		abstract BaseFrag create();

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:{
			
			Intent intent = new Intent(this, ActivityEditorActivity.class);
			intent.putExtra(ActivityEditorActivity.EXTRA_MALL_ID, mMallId);
			startActivity(intent);
		}
			break;
		case DialogInterface.BUTTON_NEGATIVE:{
			
			Intent intent = new Intent(this, ProductEditorActivity.class);
			intent.putExtra(ActivityEditorActivity.EXTRA_MALL_ID, mMallId);
			startActivity(intent);
		}

			break;

		default:
			break;
		}
	}

}
