package tw.supra.epe.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseActivity;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.network.ui.NetworkRoundedImageView;
import tw.supra.utils.JsonUtils;
import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.viewpagerindicator.PageIndicator;

public class MallActivity extends BaseActivity implements
		NetWorkHandler<MallInfo> {
	private static final String LOG_TAG = MallActivity.class.getSimpleName();
	public static final String EXTRA_MALL_ID = "extra_mall_id";

	private String mMallId;

	private PageIndicator mPageIndicator;
	private PageAdapter mPageAdapter;

	private TextView mTvMallName;
	private TextView mTvAddress;

	private final ArrayList<Page> PAGES = new ArrayList<Page>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mMallId = getIntent().getStringExtra(EXTRA_MALL_ID);

		setContentView(R.layout.activity_mall);

		mTvAddress = (TextView) findViewById(R.id.address);
		mTvMallName = (TextView) findViewById(R.id.mall_name);

		mPageAdapter = new PageAdapter();
		ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		viewPager.setAdapter(mPageAdapter);
		mPageIndicator = (PageIndicator) findViewById(R.id.page_indicator);
		mPageIndicator.setViewPager(viewPager);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		request();
	}

	private void request() {
		NetworkCenter.getInstance().putToQueue(
				new RequestMall(this, new MallInfo(mMallId)));
	}

	@Override
	public boolean HandleEvent(RequestEvent event, MallInfo info) {

		Log.i(LOG_TAG, "HandleEvent FINISH : " + info);
		if (RequestEvent.FINISH == event) {
			if (info.ERROR_CODE.isOK()) {
				try {
					mTvMallName.setText(info.resultJo
							.getString(MallInfo.MALL_NAME));
					mTvAddress.setText(getString(R.string.mall_info_address,
							info.resultJo.getString(MallInfo.ADDRESS)));
					JSONObject joFloors = info.resultJo
							.getJSONObject(MallInfo.BRAND);

					Log.i(LOG_TAG, "joFloors : "+joFloors);
					
					JSONArray names = joFloors.names();
					Log.i(LOG_TAG, "names : "+names);
					if (names != null) {
						for (int i = 0; i < names.length(); i++) {
							String name = names.getString(i);
							PAGES.add(new Page(name, JsonUtils.getJaSafely(
									joFloors, name)));
						}
					}
					Log.i(LOG_TAG, "PAGES : "+PAGES.size());
					for(Page page : PAGES){
						Log.i(LOG_TAG, "page : name = "+page.NAME+" array = "+page.ARRAY);

					}
					mPageAdapter.notifyDataSetChanged();
					mPageIndicator.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	private class Page {
		public final String NAME;
		public final JSONArray ARRAY;

		public Page(String name, JSONArray array) {
			NAME = name;
			ARRAY = array;
		}
	}

	private class PageAdapter extends FragmentStatePagerAdapter {

		public PageAdapter() {
			super(getFragmentManager());
		}

		@Override
		public int getCount() {
			return PAGES.size();
		}

		@Override
		public Fragment getItem(int position) {
			ListFragment fragment = new ListFragment();
			fragment.setListAdapter(new ShopAdapter(PAGES.get(position).ARRAY));
			return fragment;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getString(R.string.mall_floor, PAGES.get(position).NAME);
		}

	}

	private class ItemHolder {
		NetworkRoundedImageView iv;
		TextView name;
		TextView doorNu;
	}

	private class ShopAdapter extends BaseAdapter {

		private final JSONArray JA;

		public ShopAdapter(JSONArray ja) {
			JA = ja;
		}

		@Override
		public int getCount() {
			return JA.length();
		}

		@Override
		public JSONObject getItem(int position) {
			try {
				return JA.getJSONObject(position);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = View.inflate(MallActivity.this,
						R.layout.mall_activity_item, null);
				ItemHolder holder = new ItemHolder();
				holder.iv = (NetworkRoundedImageView) convertView
						.findViewById(R.id.img);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.doorNu = (TextView) convertView
						.findViewById(R.id.door_nu);
				convertView.setTag(holder);
			}

			String name = "";
			String doorNu = "";
			String img = "";

			JSONObject jo = getItem(position);
			try {
				doorNu = jo.getString(MallInfo.DOORNO);
				img = jo.getString(MallInfo.BRAND_LOGO);
				name = jo.getString(MallInfo.BRAND_NAME);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			ItemHolder holder = (ItemHolder) convertView.getTag();
			holder.iv.setImageUrl(img, NetworkCenter.getInstance()
					.getImageLoader());
			holder.doorNu.setText(doorNu);
			holder.name.setText(name);

			return convertView;
		}

	}

}
