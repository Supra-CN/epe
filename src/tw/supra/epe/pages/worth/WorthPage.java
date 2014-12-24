package tw.supra.epe.pages.worth;

import java.text.NumberFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.App;
import tw.supra.epe.R;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.location.LocationCenter;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.utils.JsonUtils;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

public class WorthPage extends BaseMainPage implements LocationListener,
		NetWorkHandler<WorthInfo> {
	private static final String LOG_TAG = WorthPage.class.getSimpleName();
	private RequestWorth mRequestWorth;
	private Location mLocation;
	private boolean mDiscount = false;
	private String mGender = WorthInfo.GENDER_UNKNOW;
	private final ArrayList<JSONObject> DATA_SET = new ArrayList<JSONObject>();

	private BaseAdapter ADAPTER = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getActivity(),
						R.layout.worth_page_item, null);
				ItemHolder holder = new ItemHolder();
				holder.iv = (NetworkImageView) convertView
						.findViewById(R.id.img);
				holder.tvName = (TextView) convertView.findViewById(R.id.name);
				holder.tvDistance = (TextView) convertView
						.findViewById(R.id.distance);
				holder.tvDiscount = (TextView) convertView
						.findViewById(R.id.discount);
				holder.tvPrice = (TextView) convertView
						.findViewById(R.id.price);
				holder.tvLike = (TextView) convertView.findViewById(R.id.like);
				convertView.setTag(holder);
			}

			String img = "";
			String name = "";
			String distance = "";
			String discount = "";
			String price = "";
			String like = "";

			try {
				JSONObject jo = getItem(position);
				Log.i(LOG_TAG, "#===== getView =====");

				JSONObject joImages = JsonUtils.getJaSafely(jo,
						WorthInfo.ATTR_IMAGELIST).getJSONObject(0);
				if (null != joImages) {
					JSONObject joMiddle = JsonUtils.getJoSafely(joImages,
							WorthInfo.ATTR_IMAGE_540MIDDLE);
					if (null != joMiddle) {

						img = JsonUtils.getStrSafely(joMiddle,
								WorthInfo.ATTR_IMAGE_SRC);
					}
				}

				Log.i(LOG_TAG, "# img : " + img);

				name = JsonUtils.getStrSafely(jo, WorthInfo.ATTR_MALL_NAME);
				Log.i(LOG_TAG, "# name : " + name);
				distance = JsonUtils.getStrSafely(jo, WorthInfo.ATTR_DISTANCE)
						+ "m";
				Log.i(LOG_TAG, "# distance : " + distance);

				double discountNum = JsonUtils.getDoubleSafely(jo,
						WorthInfo.ATTR_DISCOUNT_NUM) * 10;
				NumberFormat nf = NumberFormat.getNumberInstance();
				if (discountNum < 10) {
					nf.setMaximumFractionDigits(1);
					discount = getString(R.string.worth_page_item_discount,
							nf.format(discountNum));
					Log.i(LOG_TAG, "# discount : " + discount);
				}

				nf.setMaximumFractionDigits(2);
				price = nf.format(JsonUtils.getDoubleSafely(jo,
						WorthInfo.ATTR_PRODUCT_PRICE));
				Log.i(LOG_TAG, "# price : " + price);
				like = JsonUtils.getStrSafely(jo,
						WorthInfo.ATTR_PRODUCT_LIKE_NUM);
				Log.i(LOG_TAG, "# like : " + like);

				Log.i(LOG_TAG, "#==================");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			ItemHolder holder = (ItemHolder) convertView.getTag();
			holder.iv.setImageUrl(img, NetworkCenter.getInstance()
					.getImageLoader());
			holder.tvName.setText(name);
			holder.tvDistance.setText(distance);
			holder.tvDiscount.setText(discount);
			holder.tvDiscount
					.setVisibility(TextUtils.isEmpty(discount) ? View.GONE
							: View.VISIBLE);
			holder.tvPrice.setText(price);
			holder.tvLike.setText(like);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public JSONObject getItem(int position) {
			return DATA_SET.get(position);
		}

		@Override
		public int getCount() {
			return DATA_SET.size();
		}
	};

	private class ItemHolder {
		NetworkImageView iv;
		TextView tvName;
		TextView tvPrice;
		TextView tvDistance;
		TextView tvDiscount;
		TextView tvLike;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.page_worth, null);
		GridView gridView = (GridView)v.findViewById(R.id.grid_view);
		gridView.setEmptyView(v.findViewById(R.id.progress_bar));
		gridView.setAdapter(ADAPTER);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();

		if (ADAPTER.isEmpty()) {
			((LocationManager) App.getInstance().getSystemService(
					Context.LOCATION_SERVICE)).requestSingleUpdate(
					LocationCenter.getInstance().getBestProvider(), this,
					getActivity().getMainLooper());
		}
	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return c.getText(R.string.indictor_tab_worth);
	}

	@Override
	public int getIconResId() {
		return 0;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.i(LOG_TAG, "onLocationChanged : " + location);
		mLocation = location;
		requestWorth();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i(LOG_TAG, "onLocationChanged : provider = " + provider
				+ " status = " + status);
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i(LOG_TAG, "onProviderEnabled : " + provider);
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i(LOG_TAG, "onProviderDisabled : " + provider);
		// TODO Auto-generated method stub
	}

	@Override
	public boolean HandleEvent(
			tw.supra.network.request.NetWorkHandler.RequestEvent event,
			WorthInfo info) {
		
		if (RequestEvent.FINISH == event) {

			if (info.ERROR_CODE.isOK()) {
				JSONArray ja = info.resultJoList;
				for (int i = 0; i < ja.length(); i++) {
					try {
						if (!ja.isNull(i)) {
							JSONObject joItem = ja.getJSONObject(i);
							DATA_SET.add(joItem);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				ADAPTER.notifyDataSetChanged();
			}
			mRequestWorth = null;
		}
		return false;
	}

	private void requestWorth() {
		if (null != mLocation && null == mRequestWorth) {
			double latitude = mLocation.getLatitude();
			double longitude = mLocation.getLongitude();
			mRequestWorth = new RequestWorth(this, new WorthInfo(latitude,
					longitude, mDiscount, mGender, 1));
			NetworkCenter.getInstance().putToQueue(mRequestWorth);
		}
	}

}
