package tw.supra.epe.pages.worth;

import java.text.NumberFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.activity.product.ProductActivity;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.epe.ui.pullto.PullToRefreshStaggeredGridView;
import tw.supra.epe.ui.staggered.StaggeredGridView;
import tw.supra.epe.ui.staggered.StaggeredGridView.OnItemClickListener;
import tw.supra.location.LocationCallBack;
import tw.supra.location.LocationCenter;
import tw.supra.location.SupraLocation;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.network.ui.NetworkImageView;
import tw.supra.utils.JsonUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public class WorthPage extends BaseMainPage implements
		NetWorkHandler<WorthInfo>, LocationCallBack,OnItemClickListener,
		OnRefreshListener2<StaggeredGridView> {
	private static final String LOG_TAG = WorthPage.class.getSimpleName();
	private RequestWorth mRequestWorth;
	private Location mLocation;
	private boolean mDiscount = false;
	private String mGender = WorthInfo.GENDER_UNKNOW;
	private final ArrayList<JSONObject> DATA_SET = new ArrayList<JSONObject>();
	private LocationClient mLocationClient = null;
	
	private static Handler sHandler = new Handler();

	private PullToRefreshStaggeredGridView mPullRefreshGrid;
	// private StaggeredGridView mGridView;

	// private int mPageTop = 1;
	// private int mPageBottom = 1;
	private int mPageLoaded = -1;
	private int mPagePending = -1;

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
			int width = 0;
			int height = 0;
			Boolean isLike = false;

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
						width = JsonUtils.getIntSafely(joMiddle,
								WorthInfo.ATTR_IMAGE_WIDTH);
						height = JsonUtils.getIntSafely(joMiddle,
								WorthInfo.ATTR_IMAGE_HEIGHT);
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
					discount = getString(R.string.item_discount,
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
				isLike = JsonUtils.getIntSafely(jo, WorthInfo.ATTR_IS_LIKE, 0)!=0;
				Log.i(LOG_TAG, "# isLike : " + isLike);

				Log.i(LOG_TAG, "#==================");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			ItemHolder holder = (ItemHolder) convertView.getTag();
			adjustViewHeight(holder.iv, width, height);
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
			holder.tvLike.setSelected(isLike);
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

	private int mAdjustImageWidth = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.page_worth, null);
		mPullRefreshGrid = (PullToRefreshStaggeredGridView) v
				.findViewById(R.id.pull_refresh_grid);
		mPullRefreshGrid.setOnRefreshListener(this);
		StaggeredGridView grid = mPullRefreshGrid.getRefreshableView();
		// gridView.setEmptyView(v.findViewById(R.id.progress_bar));
		grid.setAdapter(ADAPTER);
		// mGridView.setAdapter(ADAPTER);
		grid.setOnItemClickListener(this);
		return v;
	}

	@Override
	public void onStart() {
		Log.i(LOG_TAG, "onStart");
		super.onStart();
		if (ADAPTER.isEmpty()) {
			Log.i(LOG_TAG, "onStart setRefreshing");
			sHandler.post(new Runnable() {
				
				@Override
				public void run() {
					mPullRefreshGrid.setRefreshing(false);
				}
			});
		}else {
			ADAPTER.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		Log.i(LOG_TAG, "onAttach");
		super.onAttach(activity);
	}
	
	@Override
	public void onStop() {
		Log.i(LOG_TAG, "onStop");
		super.onStop();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.i(LOG_TAG, "onDetach");
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
	public boolean HandleEvent(RequestEvent event, WorthInfo info) {

		if (RequestEvent.FINISH == event) {
			Log.i(LOG_TAG, "HandleEvent FINISH : " + info);

			if (info.ERROR_CODE.isOK()) {
				if (info.ARG_PAGE < mPageLoaded) {
					DATA_SET.clear();
				}
				mPageLoaded = info.ARG_PAGE;

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
			mPullRefreshGrid.onRefreshComplete();
			mRequestWorth = null;

		}
		return false;
	}

	private void loadData(int page) {
		Log.i(LOG_TAG, "loadData : " + "page = " + page + " mLocation : "
				+ mLocation);
		if (null == mLocation) {
			mPagePending = page;
			LocationCenter.getInstance().requestLocation(this);
		} else {
			requestWorth(page);
		}
	}

	private void requestWorth(int page) {
		Log.i(LOG_TAG, "requestWorth : " + page);
		double latitude = mLocation.getLatitude();
		double longitude = mLocation.getLongitude();
		mRequestWorth = new RequestWorth(this, new WorthInfo(latitude,
				longitude, mDiscount, mGender, 1));
		NetworkCenter.getInstance().putToQueue(mRequestWorth);
	}

	@Override
	public void callBack(SupraLocation location) {
		Log.i(LOG_TAG, "callBack : " + "mPagePending = " + mPagePending
				+ " location : " + location);
		mLocation = location;
		if (mPagePending > 0) {
			requestWorth(mPagePending);
		}
		mPagePending = -1;
	}

	@Override
	public void onPullDownToRefresh(
			PullToRefreshBase<StaggeredGridView> refreshView) {
		Log.i(LOG_TAG, "onPullDownToRefresh");
		loadData(1);
	}

	@Override
	public void onPullUpToRefresh(
			PullToRefreshBase<StaggeredGridView> refreshView) {
		Log.i(LOG_TAG, "onPullDownToRefresh : " + mPageLoaded + 1);
		loadData(mPageLoaded + 1);
	}
	
	private int adjustViewWidth() {
		if (mAdjustImageWidth <= 0) {
			mAdjustImageWidth = mPullRefreshGrid.getWidth() / 2;
		}
		return mAdjustImageWidth;
	}

	private void adjustViewHeight(View view, int width, int height) {
		Log.i(LOG_TAG, "===adjustIconView start===");

		int iw = width;
		int ih = height;
		Log.i(LOG_TAG, "iw : " + iw);
		Log.i(LOG_TAG, "ih : " + ih);
		if (iw < 0 || ih < 0) {
			return;
		}

		float ratio = (Float.valueOf(iw) / Float.valueOf(ih));
		Log.i(LOG_TAG, "ratio : " + ratio);

		int vw = view.getWidth();
		int vh = -1;
		Log.i(LOG_TAG, "vw : " + vw);

		if (vw < 1) {
			vw = adjustViewWidth();
			Log.i(LOG_TAG, "vw = ADJUST_IMAGE_WIDTH : " + vw);
		}

		vh = Float.valueOf(vw / ratio).intValue();
		Log.i(LOG_TAG, "vh : " + vh);
		view.getLayoutParams().height = vh;

		Log.i(LOG_TAG, "===adjustIconView end===");
	}
	
	// private static int getImageWidth(Context context) {
	// DisplayMetrics dm = context.getResources().getDisplayMetrics();
	// int imageWidth = dm.widthPixels;
	// if (dm.density > 2) {
	// imageWidth = 2 * Math.round(dm.widthPixels / dm.density);
	// }
	// imageWidth = imageWidth
	// - (2 * context.getResources().getDimensionPixelSize(
	// R.dimen.post_content_padding));
	// return imageWidth;
	// }



	private class ItemHolder {
		NetworkImageView iv;
		TextView tvName;
		TextView tvPrice;
		TextView tvDistance;
		TextView tvDiscount;
		TextView tvLike;
	}

	@Override
	public void onItemClick(StaggeredGridView parent, View view, int position,
			long id) {
		try {
			String productId = DATA_SET.get(position).getString(WorthInfo.ATTR_PRODUCT_ID);
			Intent intent = new Intent(getActivity(), ProductActivity.class);
			intent.putExtra(ProductActivity.EXTRA_PRODUCT_ID, productId);
			startActivity(intent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


}
