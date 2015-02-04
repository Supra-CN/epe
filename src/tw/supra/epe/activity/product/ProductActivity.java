package tw.supra.epe.activity.product;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.account.AccountCenter;
import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.pages.PhotoClient;
import tw.supra.epe.utils.AppUtiles;
import tw.supra.location.MapActivity;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.ui.PhotoPager;
import tw.supra.ui.PhotoPager.OnDispatchTouchListener;
import tw.supra.utils.JsonUtils;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.CheckBox;
import android.widget.TextView;

public class ProductActivity extends BaseActivity implements OnClickListener,
		OnDispatchTouchListener, NetWorkHandler<ProductInfo> {
	private static final String LOG_TAG = ProductActivity.class.getSimpleName();
	public static final String EXTRA_PRODUCT_ID = "product_id";
	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	private final ArrayList<String> PHOTOS = new ArrayList<String>();

	private float mPressedX;
	private float mPressedY;
	private int mTouchSlop;
	private boolean mIsMoved = false;

	private TextView mTvProductName;
	private TextView mTvProductInfo;
	private TextView mTvBrandName;
	private TextView mTvPrice;
	private TextView mTvDiscount;
	private CheckBox mCbLike;
	private CheckBox mCbFav;

	private String mProductId;
	private String mBrandName;
	private String mMallName;
	private double mLat;
	private double mLon;
	private JSONObject mJoData;

	private PhotoPager mViewPager;
	private View mFloatLayer;

	private static Handler sHandler = new Handler();

	private ImagePagerAdapter mImageAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mProductId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
		mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
		mImageAdapter = new ImagePagerAdapter(getFragmentManager());

		setContentView(R.layout.activity_product);
		findViewById(R.id.fetch_failed).setOnClickListener(this);
		findViewById(R.id.action_back).setOnClickListener(this);
		findViewById(R.id.map).setOnClickListener(this);

		mFloatLayer = findViewById(R.id.float_layer);
		mViewPager = (PhotoPager) findViewById(R.id.view_pager);
		mCbLike = (CheckBox) findViewById(R.id.like);
		mCbFav = (CheckBox) findViewById(R.id.fav);
		mTvBrandName = (TextView) findViewById(R.id.brand_name);
		mTvDiscount = (TextView) findViewById(R.id.discount);
		mTvPrice = (TextView) findViewById(R.id.price);
		mTvProductName = (TextView) findViewById(R.id.product_name);
		mTvProductInfo = (TextView) findViewById(R.id.product_info);

		mViewPager.setOnDispatchTouchListener(this);
		// mViewPager.setOnPageChangeListener(this);
		mViewPager.setPageMargin(getResources().getDimensionPixelSize(
				R.dimen.image_browser_page_gap));

		mViewPager.setAdapter(mImageAdapter);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		delayedHide(AUTO_HIDE_DELAY_MILLIS);
		request();
	}

	@Override
	public boolean HandleEvent(RequestEvent event, ProductInfo info) {

		Log.i(LOG_TAG, "HandleEvent FINISH : " + info);
		if (RequestEvent.FINISH == event) {
			hideProgressDialog();
			if (info.ERROR_CODE.isOK() && info.resultPInfo != null) {
				mJoData = info.resultPInfo;
				updateUi();
			} else {
				findViewById(R.id.fetch_failed).setVisibility(View.VISIBLE);
			}
		}

		return false;
	}

	private void request() {
		showProgressDialog();
		NetworkCenter.getInstance().putToQueue(
				new RequestProduct(this, new ProductInfo(AccountCenter
						.getCurrentUserUid(), mProductId)));
	}

	private void updateUi() {

		if (mJoData == null) {
			return;
		}

		String img = "";
		String productName = "";
		String discount = "";
		String price = "";
		String productInfo = "";
		int width = 0;
		int height = 0;
		Boolean isLike = false;
		Boolean isFav = false;

		try {

			productName = JsonUtils.getStrSafely(mJoData,
					ProductInfo.PRODUCT_NAME);

			discount = AppUtiles.formatdiscount(JsonUtils.getDoubleSafely(
					mJoData, ProductInfo.DISCOUNT_NUM));

			price = AppUtiles.formatPrice(JsonUtils.getDoubleSafely(mJoData,
					ProductInfo.PRODUCT_PRICE));

			isLike = JsonUtils.getIntSafely(mJoData, ProductInfo.IS_LIKE, 0) != 0;
			isFav = JsonUtils.getIntSafely(mJoData, ProductInfo.IS_FAVOR, 0) != 0;

			JSONObject joMall = JsonUtils.getJoSafely(mJoData,
					ProductInfo.MALL_INFO);
			mMallName = JsonUtils.getStrSafely(joMall, ProductInfo.MALL_NAME);
			mLat = JsonUtils.getLongSafely(joMall, ProductInfo.MALL_LATITUDE);
			mLon = JsonUtils.getLongSafely(joMall, ProductInfo.MALL_LONGITUDE);

			productInfo = getString(R.string.product_info_model,
					JsonUtils.getStrSafely(mJoData, ProductInfo.PRODUCT_SKU))
					+ "\n";
			productInfo += getString(R.string.product_info_tag,
					JsonUtils.getStrSafely(mJoData, ProductInfo.PRODUCT_TAG))
					+ "\n";
			productInfo += getString(R.string.product_info_mall,mMallName)
					+ "\n";
			productInfo += getString(R.string.product_info_address,
					JsonUtils.getStrSafely(joMall, ProductInfo.MALL_ADDRESS));

			JSONObject joBrand = JsonUtils.getJoSafely(mJoData,
					ProductInfo.BRAND_INFO);
			mBrandName = JsonUtils.getStrSafely(joBrand, ProductInfo.BRAND_NAME);

			JSONArray jaImages = JsonUtils.getJaSafely(mJoData,
					ProductInfo.IMAGES);

			PHOTOS.clear();
			for (int i = 0; i < jaImages.length(); i++) {
				PHOTOS.add(jaImages.getJSONObject(i)
						.getJSONObject(ProductInfo.IMG_ORIGINAL)
						.getString(ProductInfo.IMG_SRC));
			}

			// if (null != joImages) {
			// JSONObject joOriginal = JsonUtils.getJoSafely(joImages,
			// ProductInfo.IMG_ORIGINAL);
			// if (null != joOriginal) {
			// img = JsonUtils.getStrSafely(joOriginal,
			// ProductInfo.IMG_SRC);
			// width = JsonUtils.getIntSafely(joOriginal,
			// ProductInfo.IMG_WIDTH);
			// height = JsonUtils.getIntSafely(joOriginal,
			// ProductInfo.IMG_HEIGHT);
			// }
			// }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mTvProductName.setText(productName);
		mTvDiscount.setText(getString(R.string.item_discount, discount));
		mTvDiscount.setVisibility(TextUtils.isEmpty(discount) ? View.GONE
				: View.VISIBLE);
		mTvPrice.setText(price);
		mTvBrandName.setText(mBrandName);
		mCbLike.setChecked(isLike);
		mCbFav.setChecked(isFav);
		mTvProductInfo.setText(productInfo);
		mImageAdapter.notifyDataSetChanged();
		// adjustViewHeight(mIvImg, width, height);
	}

	private int adjustViewWidth() {
		return getResources().getDisplayMetrics().widthPixels;
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

	private class ImagePagerAdapter extends FragmentStatePagerAdapter {

		public ImagePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return PHOTOS.size();
		}

		@Override
		public Fragment getItem(int position) {
			PhotoClient client = new PhotoClient();
			Bundle arg = new Bundle();
			arg.putString(PhotoClient.ARG_STRING_IMG_URL, PHOTOS.get(position));
			client.setArguments(arg);
			return client;
		}
	}

	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			setOverLayVisible(false);
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		sHandler.removeCallbacks(mHideRunnable);
		sHandler.postDelayed(mHideRunnable, delayMillis);
	}

	private void setOverLayVisible(boolean isVisible) {
		mFloatLayer.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}

	public boolean onTouchViewPager(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mIsMoved = false;
			mPressedX = event.getX();
			mPressedY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			mIsMoved = (Math.abs(mPressedX - event.getX()) >= mTouchSlop)
					&& (Math.abs(mPressedY - event.getY()) >= mTouchSlop);
			break;
		case MotionEvent.ACTION_UP:
			if (!mIsMoved) {
				setOverLayVisible(View.VISIBLE != mFloatLayer.getVisibility());
			}
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fetch_failed:
			v.setVisibility(View.GONE);
			request();
			break;
		case R.id.action_back:
			onBackPressed();
			break;
		case R.id.map:
			MapActivity.show(this, mMallName	, mLat, mLon);
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onDispatchTouchEvent(MotionEvent ev) {
		onTouchViewPager(ev);
		return false;
	}

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case R.id.action_share:
	// //
	// shareWithImg(mImageAdapter.images.get(mViewPager.getCurrentItem()).IMAGE);
	// shareWithImg(mPhotos.get(mViewPager.getCurrentItem()).IMG);
	// return true;
	// case R.id.action_download:
	// // String url =
	// // mImageAdapter.images.get(mViewPager.getCurrentItem()).IMAGE;
	// String url = mPhotos.get(mViewPager.getCurrentItem()).IMG;
	// File path = CommonData.getInstance().getPathDownload();
	// if (null == path) {
	// Toast.makeText(this, R.string.external_storage_unavailable,
	// Toast.LENGTH_SHORT).show();
	// return true;
	// }
	// File image = new File(path, Uri.parse(url).getLastPathSegment());
	// DownloadRequest request = new DownloadRequest(url, image.getPath(),
	// this, this);
	// NetworkCenter.getInstance().putToQueue(request);
	// Toast.makeText(this, R.string.image_browser_download_start,
	// Toast.LENGTH_SHORT).show();
	// return true;
	//
	// default:
	// return super.onOptionsItemSelected(item);
	// }
	// }
}
