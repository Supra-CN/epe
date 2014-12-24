package tw.supra.epe.pages.epe;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.App;
import tw.supra.epe.R;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.location.LocationCenter;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import u.aly.ar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.viewpagerindicator.PageIndicator;

public class EpePage extends BaseMainPage implements LocationListener {
	private static final String LOG_TAG = EpePage.class.getSimpleName();

	private Location mLocation;
	private RequestNearStore mRequestNearStore;
	private RequestNearBrand mRequestNearBrand;
	private final ArrayList<JSONObject> AD_DATA_SET = new ArrayList<JSONObject>();

	private ViewGroup mNearStoreContainer;
	private ViewGroup mNearBrandContainer;

	private AdAdapter mAdAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.page_epe, null);
		mNearStoreContainer = (ViewGroup) v
				.findViewById(R.id.near_store_container);
		mNearBrandContainer = (ViewGroup) v
				.findViewById(R.id.near_brand_container);
		mAdAdapter = new AdAdapter(getChildFragmentManager());
		ViewPager pager = (ViewPager) v.findViewById(R.id.view_pager);
		pager.setAdapter(mAdAdapter);
		PageIndicator indicator = (PageIndicator) v
				.findViewById(R.id.page_indicator);
		indicator.setViewPager(pager);
		return v;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		requestAds();
	}

	@Override
	public void onStart() {
		super.onStart();
		if (mNearStoreContainer.getChildCount() < 1) {
			((LocationManager) App.getInstance().getSystemService(
					Context.LOCATION_SERVICE)).requestSingleUpdate(
					LocationCenter.getInstance().getBestProvider(), this,
					getActivity().getMainLooper());
		}
	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return c.getText(R.string.indictor_tab_epe);
	}

	@Override
	public int getIconResId() {
		return 0;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.i(LOG_TAG, "onLocationChanged : " + location);
		mLocation = location;
		requestNearStore();
		requestNearBrand();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	private void requestNearStore() {
		if (null != mLocation && null == mRequestNearStore) {
			double latitude = mLocation.getLatitude();
			double longitude = mLocation.getLongitude();

			mRequestNearStore = new RequestNearStore(HANDLER_NEAR_STORE,
					new NearStoreInfo(latitude, longitude, 1));
			NetworkCenter.getInstance().putToQueue(mRequestNearStore);
		}
	}

	private final NetWorkHandler<NearStoreInfo> HANDLER_NEAR_STORE = new NetWorkHandler<NearStoreInfo>() {

		@Override
		public boolean HandleEvent(RequestEvent event, NearStoreInfo info) {
			if (RequestEvent.FINISH == event) {

				if (info.ERROR_CODE.isOK()) {
					mNearStoreContainer.removeAllViews();
					JSONArray ja = info.resultJoList;
					for (int i = 0; i < ja.length(); i++) {
						try {
							View v = createNearStoreView(ja.getJSONObject(i), i);
							mNearStoreContainer.addView(v, new LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.MATCH_PARENT));
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

	private void requestNearBrand() {
		if (null != mLocation && null == mRequestNearBrand) {
			double latitude = mLocation.getLatitude();
			double longitude = mLocation.getLongitude();

			mRequestNearBrand = new RequestNearBrand(HANDLER_NEAR_BRAND,
					new NearBrandInfo(latitude, longitude, 1));
			NetworkCenter.getInstance().putToQueue(mRequestNearBrand);
		}
	}

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
					JSONArray ja = (JSONArray)info.OBJ;
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
		String distance = joItem.getString(NearStoreInfo.ATTR_DISTANCE) + "m";
		String text = String.format("%s\n%s", isEnen ? distance : name,
				isEnen ? name : distance);
		TextView tv = new TextView(getActivity());
		tv.setGravity((isEnen ? Gravity.BOTTOM : Gravity.TOP)
				| Gravity.CENTER_HORIZONTAL);
		tv.setText(text);
		return tv;
	}

	private View createNearBrandView(JSONObject joItem, int postion)
			throws JSONException {
		View v = View
				.inflate(getActivity(), R.layout.epe_page_brand_item, null);
		TextView tv = (TextView) v.findViewById(R.id.text);
		NetworkImageView iv = (NetworkImageView) v.findViewById(R.id.img);
		String name = joItem.getString(NearBrandInfo.ATTR_NAME);
		String logo = joItem.getString(NearBrandInfo.ATTR_LOGO);
		tv.setText(name);
		iv.setImageUrl(logo, NetworkCenter.getInstance().getImageLoader());
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
				args.putString(AdFrag.ARG_IMG, AD_DATA_SET.get(pos).getString(RequestAds.ATTR_IMG_URL));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			adFrag.setArguments(args);
			
			return adFrag;
		}
	};

}
