package tw.supra.epe.activity;

import java.text.NumberFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.account.AccountCenter;
import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.ui.pullto.PullToRefreshStaggeredGridView;
import tw.supra.epe.ui.staggered.StaggeredGridView;
import tw.supra.location.LocationCallBack;
import tw.supra.location.LocationCenter;
import tw.supra.location.SupraLocation;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.utils.JsonUtils;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public class FavActivity extends BaseActivity implements
		NetWorkHandler<FavInfo>, LocationCallBack,
		OnRefreshListener2<StaggeredGridView> {
	private static final String LOG_TAG = FavActivity.class.getSimpleName();

	private final ArrayList<JSONObject> DATA_SET = new ArrayList<JSONObject>();
	private static final int PAGE_SIZE = 20;
	private SupraLocation mLocation;
	private int mAdjustImageWidth = 0;

	private PullToRefreshStaggeredGridView mPullRefreshGrid;

	private int mPageLoaded = -1;
	private int mPagePending = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fav);

		mPullRefreshGrid = (PullToRefreshStaggeredGridView) findViewById(R.id.pull_refresh_grid);
		mPullRefreshGrid.setOnRefreshListener(this);
		// gridView.setEmptyView(v.findViewById(R.id.progress_bar));
		mPullRefreshGrid.getRefreshableView().setAdapter(ADAPTER);

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (ADAPTER.isEmpty()) {
			Log.i(LOG_TAG, "onStart setRefreshing");

			mPullRefreshGrid.setRefreshing(false);
		} else {
			ADAPTER.notifyDataSetChanged();
		}
	}

	private void requestFav(int page) {
		NetworkCenter.getInstance().putToQueue(
				new RequestFav(this, new FavInfo(AccountCenter
						.getCurrentUserUid(), mLocation, page, PAGE_SIZE)));
	}

	private class ItemHolder {
		NetworkImageView img;
		TextView name;
		TextView distance;
		TextView price;
		TextView discount;
		TextView like;
	}

	private final BaseAdapter ADAPTER = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = View.inflate(FavActivity.this,
						R.layout.fav_activity_item, null);
				ItemHolder holder = new ItemHolder();
				holder.img = (NetworkImageView) convertView
						.findViewById(R.id.img);
				holder.like = (TextView) convertView.findViewById(R.id.like);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.distance = (TextView) convertView
						.findViewById(R.id.distance);
				holder.discount = (TextView) convertView
						.findViewById(R.id.discount);
				holder.price = (TextView) convertView.findViewById(R.id.price);
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

			JSONObject jo = getItem(position);
			try {
				like = jo.getString(FavInfo.PRODUCT_LIKE_NUM);
				distance = jo.getString(FavInfo.DISTANCE) + "m";
				name = jo.getString(FavInfo.PRODUCT_NAME);

				
				double discountNum = JsonUtils.getDoubleSafely(jo,
						FavInfo.DISCOUNT_NUM) * 10;
				NumberFormat nf = NumberFormat.getNumberInstance();
				if (discountNum < 10) {
					nf.setMaximumFractionDigits(1);
					discount = getString(R.string.item_discount,
							nf.format(discountNum));
				}

				nf.setMaximumFractionDigits(2);
				price = nf.format(JsonUtils.getDoubleSafely(jo,
						FavInfo.PRODUCT_PRICE));

				JSONObject joImages = JsonUtils.getJaSafely(jo,
						FavInfo.IMAGELIST).getJSONObject(0);
				JSONObject joMiddle = JsonUtils.getJoSafely(joImages,
						FavInfo.IMG_540MIDDLE);
				img = JsonUtils.getStrSafely(joMiddle, FavInfo.IMG_SRC);
				width = JsonUtils.getIntSafely(joMiddle, FavInfo.IMG_WIDTH);
				height = JsonUtils.getIntSafely(joMiddle, FavInfo.IMG_HEIGTH);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			ItemHolder holder = (ItemHolder) convertView.getTag();
			adjustViewHeight(holder.img, width, height);
			holder.img.setImageUrl(img, NetworkCenter.getInstance()
					.getImageLoader());

			holder.name.setText(name);
			holder.like.setText(like);
			holder.discount.setText(discount);
			holder.discount
					.setVisibility(TextUtils.isEmpty(discount) ? View.GONE
							: View.VISIBLE);
			holder.distance.setText(distance);
			holder.price.setText(price);

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

	@Override
	public boolean HandleEvent(RequestEvent event, FavInfo info) {

		Log.i(LOG_TAG, "HandleEvent FINISH : " + info);
		if (RequestEvent.FINISH == event) {
			if (info.ERROR_CODE.isOK()) {
				if (info.ARG_PAGE < mPageLoaded) {
					DATA_SET.clear();
				}
				mPageLoaded = info.ARG_PAGE;

				JSONArray ja = info.resultJoList;
				for (int i = 0; i < ja.length(); i++) {
					try {
						DATA_SET.add(ja.getJSONObject(i));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				ADAPTER.notifyDataSetChanged();
			}
			mPullRefreshGrid.onRefreshComplete();

		}

		return false;
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

	@Override
	public void callBack(SupraLocation location) {
		Log.i(LOG_TAG, "callBack : " + "mPagePending = " + mPagePending
				+ " location : " + location);
		mLocation = location;
		if (mPagePending > 0) {
			requestFav(mPagePending);
		}
		mPagePending = -1;
	}

	private void loadData(int page) {
		Log.i(LOG_TAG, "loadData : " + "page = " + page + " mLocation : "
				+ mLocation);
		if (null == mLocation) {
			mPagePending = page;
			LocationCenter.getInstance().requestLocation(this);
		} else {
			requestFav(page);
		}
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

}
