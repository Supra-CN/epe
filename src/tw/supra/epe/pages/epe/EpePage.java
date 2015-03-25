package tw.supra.epe.pages.epe;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.activity.brand.BrandActivity;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.epe.mall.MallActivity;
import tw.supra.epe.store.StoreActivity;
import tw.supra.epe.utils.AppUtiles;
import tw.supra.location.LocationCallBack;
import tw.supra.location.LocationCenter;
import tw.supra.location.SupraLocation;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.network.ui.NetworkRoundedImageView;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewpagerindicator.PageIndicator;
import com.yijiayi.yijiayi.R;

public class EpePage extends BaseMainPage implements LocationCallBack {
	private static final String LOG_TAG = EpePage.class.getSimpleName();

	private static final float AD_SIZE_RATIO = 320f / 180f;

	// private SupraLocation mLocation;
	private RequestNearStore mRequestNearStore;
	private RequestNearBrand mRequestNearBrand;
	private final ArrayList<JSONObject> AD_DATA_SET = new ArrayList<JSONObject>();

	private ViewGroup mNearStoreContainerTop;
	private ViewGroup mNearStoreContainerBottom;
	private ViewGroup mNearStoreContainerWave;
	private ViewGroup mNearBrandContainer;

	private AdAdapter mAdAdapter;
	ViewPager mAdPager;

	private View mWave;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.page_epe, null);
		mNearStoreContainerTop = (ViewGroup) v
				.findViewById(R.id.near_store_container_top);
		mNearStoreContainerBottom = (ViewGroup) v
				.findViewById(R.id.near_store_container_bottom);
		mNearStoreContainerWave = (ViewGroup) v
				.findViewById(R.id.near_store_container_wave);
		mNearBrandContainer = (ViewGroup) v
				.findViewById(R.id.near_brand_container);
		mAdAdapter = new AdAdapter(getChildFragmentManager());
		mAdPager = (ViewPager) v.findViewById(R.id.view_pager);
		Point outSize = new Point();
		getActivity().getWindowManager().getDefaultDisplay().getSize(outSize);
		mAdPager.getLayoutParams().height = Float.valueOf(
				getResources().getDisplayMetrics().widthPixels / AD_SIZE_RATIO)
				.intValue();
		mAdPager.setAdapter(mAdAdapter);
		PageIndicator indicator = (PageIndicator) v
				.findViewById(R.id.page_indicator);
		indicator.setViewPager(mAdPager);

		mWave = v.findViewById(R.id.wave);
		setUpNearStoreView();
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		requestAds();
		// getHostActivity().showProgressDialog();
		LocationCenter.getInstance().requestLocation(this);

		// ((LocationManager) App.getInstance().getSystemService(
		// Context.LOCATION_SERVICE)).requestSingleUpdate(LocationCenter
		// .getInstance().getBestProvider(), this, getActivity()
		// .getMainLooper());

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return c.getText(R.string.indictor_tab_epe);
	}

	@Override
	public int getIconResId() {
		return 0;
	}

	private void requestNearStore(SupraLocation location) {
		if (null != location && null == mRequestNearStore) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();

			mRequestNearStore = new RequestNearStore(HANDLER_NEAR_STORE,
					new NearStoreInfo(latitude, longitude, 1));
			NetworkCenter.getInstance().putToQueue(mRequestNearStore);
		}
	}

	private final NetWorkHandler<NearStoreInfo> HANDLER_NEAR_STORE = new NetWorkHandler<NearStoreInfo>() {

		@Override
		public boolean HandleEvent(RequestEvent event, NearStoreInfo info) {
			if (RequestEvent.FINISH == event && isAdded()) {

				if (info.ERROR_CODE.isOK()) {
					mNearStoreContainerTop.removeAllViews();
					mNearStoreContainerWave.removeAllViews();
					mNearStoreContainerBottom.removeAllViews();
					JSONArray ja = info.resultJoList;

					int length = ja.length();

					int waveCount = length / 2 + 1;
					waveCount = waveCount < 3 ? 3 : waveCount;

					for (int i = 0; i < waveCount; i++) {
						View waveView = new View(getActivity());
						waveView.setBackgroundResource(R.drawable.near_store_item_wave_bg);
						mNearStoreContainerWave.addView(
								waveView,
								new LayoutParams(getResources()
										.getDimensionPixelSize(
												R.dimen.epe_wave_item_width),
										LayoutParams.MATCH_PARENT));
					}

					for (int i = 0; i < length; i++) {
						try {
							View v = createNearStoreView(ja.getJSONObject(i), i);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				}
				mRequestNearStore = null;
			}
			return false;
		}
	};

	private void requestNearBrand(SupraLocation location) {
		if (null != location && null == mRequestNearBrand) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();

			mRequestNearBrand = new RequestNearBrand(HANDLER_NEAR_BRAND,
					new NearBrandInfo(latitude, longitude, 1));
			NetworkCenter.getInstance().putToQueue(mRequestNearBrand);
		}
	}

	private final OnClickListener MALL_CLICK_LISTENER = new OnClickListener() {

		@Override
		public void onClick(View v) {
			JSONObject joItem = (JSONObject) v.getTag();
			try {
				if (joItem.getString(NearStoreInfo.ATTR_MALL_TYPE).equals("2")) {
					Intent intent = new Intent(getActivity(),
							StoreActivity.class);
					intent.putExtra(StoreActivity.EXTRA_IS_STORE, true);
					intent.putExtra(StoreActivity.EXTRA_FOCUS_ID,
							joItem.getString(NearStoreInfo.ATTR_MALL_ID));
					intent.putExtra(StoreActivity.EXTRA_MALL_NAME,
							joItem.getString(NearStoreInfo.ATTR_MALL_NAME));
					startActivity(intent);
				} else {
					Intent intent = new Intent(getActivity(),
							MallActivity.class);
					intent.putExtra(MallActivity.EXTRA_MALL_ID,
							joItem.getString(NearStoreInfo.ATTR_MALL_ID));
					startActivity(intent);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
		}
	};
	private final OnClickListener BRAND_CLICK_LISTENER = new OnClickListener() {

		@Override
		public void onClick(View v) {
			JSONObject joItem = (JSONObject) v.getTag();
			try {
				Intent intent = new Intent(getActivity(), BrandActivity.class);
				intent.putExtra(BrandActivity.EXTRA_BRAND_ID,
						joItem.getString(NearBrandInfo.ATTR_ID));
				intent.putExtra(BrandActivity.EXTRA_BRAND_NAME,
						joItem.getString(NearBrandInfo.ATTR_NAME));
				startActivity(intent);
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	private final NetWorkHandler<NearBrandInfo> HANDLER_NEAR_BRAND = new NetWorkHandler<NearBrandInfo>() {

		@Override
		public boolean HandleEvent(RequestEvent event, NearBrandInfo info) {
			if (RequestEvent.FINISH == event) {

				if (info.ERROR_CODE.isOK()) {
					mNearBrandContainer.removeAllViews();
					JSONArray ja = info.resultJoList;
					for (int i = 0; i < ja.length(); i++) {
						try {
							View v = createNearBrandView(ja.getJSONObject(i), i);
							mNearBrandContainer.addView(v, new LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.MATCH_PARENT));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				}
				mRequestNearBrand = null;
			}
			return false;
		}
	};

	private void requestAds() {
		NetworkCenter.getInstance().putToQueue(new RequestAds(HANDLER_ADS));
	}

	private final NetWorkHandler<EpeRequestInfo> HANDLER_ADS = new NetWorkHandler<EpeRequestInfo>() {

		@Override
		public boolean HandleEvent(RequestEvent event, EpeRequestInfo info) {
			if (RequestEvent.FINISH == event) {

				if (info.ERROR_CODE.isOK() && info.OBJ instanceof JSONArray) {
					JSONArray ja = (JSONArray) info.OBJ;
					AD_DATA_SET.clear();
					for (int i = 0; i < ja.length(); i++) {
						try {
							AD_DATA_SET.add(ja.getJSONObject(i));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					mAdAdapter.notifyDataSetChanged();
				}
			}
			return false;
		}
	};

	private View createNearStoreView(JSONObject joItem, int postion)
			throws JSONException {
		boolean isEnen = (postion % 2) == 0;
		String name = joItem.getString(NearStoreInfo.ATTR_MALL_NAME);
		String distance = AppUtiles.formatDistance(joItem
				.getInt(NearStoreInfo.ATTR_DISTANCE));

		View v = View.inflate(getActivity(),
				isEnen ? R.layout.near_store_item_bottom
						: R.layout.near_store_item_top, null);

		TextView tvName = (TextView) v.findViewById(R.id.name);
		TextView tvDistance = (TextView) v.findViewById(R.id.distance);
		ImageView iv = (ImageView) v.findViewById(R.id.dot);
		// tv.setGravity((isEnen ? Gravity.BOTTOM : Gravity.TOP)
		// | Gravity.CENTER_HORIZONTAL);
		tvName.setText(name);
		tvDistance.setText(distance);

		tvName.setTag(joItem);
		tvName.setOnClickListener(MALL_CLICK_LISTENER);

		iv.setImageResource(joItem.getString(NearStoreInfo.ATTR_MALL_TYPE)
				.equals("2") ? R.drawable.dot_store : R.drawable.dot_shop);

		ViewGroup viewGroup = isEnen ? mNearStoreContainerBottom
				: mNearStoreContainerTop;
		viewGroup.addView(v, new LayoutParams(getResources()
				.getDimensionPixelSize(R.dimen.epe_wave_item_width),
				LayoutParams.MATCH_PARENT));

		return tvName;
	}

	private View createNearBrandView(JSONObject joItem, int postion)
			throws JSONException {
		View v = View
				.inflate(getActivity(), R.layout.epe_page_brand_item, null);
		TextView tv = (TextView) v.findViewById(R.id.text);
		TextView tvDistance = (TextView) v.findViewById(R.id.distance);
		NetworkRoundedImageView iv = (NetworkRoundedImageView) v
				.findViewById(R.id.img);
		String name = joItem.getString(NearBrandInfo.ATTR_NAME);
		String logo = joItem.getString(NearBrandInfo.ATTR_LOGO);
		// String id = joItem.getString(NearBrandInfo.ATTR_ID);
		tvDistance.setText(AppUtiles.formatDistance(joItem
				.getInt(NearBrandInfo.ATTR_DISTANCE)));
		tv.setText(name);
		iv.setImageUrl(logo, NetworkCenter.getInstance().getImageLoader());
		v.setTag(joItem);
		v.setOnClickListener(BRAND_CLICK_LISTENER);
		return v;
	}

	private class AdAdapter extends FragmentStatePagerAdapter {

		public AdAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return AD_DATA_SET.size();
		}

		@Override
		public Fragment getItem(int pos) {

			AdFrag adFrag = new AdFrag();
			Bundle args = new Bundle();
			try {
				args.putString(AdFrag.ARG_IMG,
						AD_DATA_SET.get(pos).getString(RequestAds.ATTR_IMG_URL));
				args.putString(
						AdFrag.ARG_URL,
						AD_DATA_SET.get(pos).getString(
								RequestAds.ATTR_REDIRECT_URL));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			adFrag.setArguments(args);

			return adFrag;
		}
	};

	private void setUpNearStoreView() {

		BitmapDrawable drawableBg = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.near_store_bg));
		drawableBg.setTileModeX(TileMode.REPEAT);
		mWave.setBackgroundDrawable(drawableBg);
	}

	@Override
	public void callBack(SupraLocation location) {
		Log.i(LOG_TAG, "onLocationChanged : " + location);
		// getHostActivity().hideProgressDialog();
		requestNearStore(location);
		requestNearBrand(location);

	}

}
