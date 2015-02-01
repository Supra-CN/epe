package tw.supra.epe.activity.brand;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseActivity;
import tw.supra.location.LocationCallBack;
import tw.supra.location.LocationCenter;
import tw.supra.location.SupraLocation;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class BrandActivity extends BaseActivity implements
		NetWorkHandler<BrandInfo>, LocationCallBack, OnClickListener,
		OnRefreshListener2<ListView> {
	private static final String LOG_TAG = BrandActivity.class.getSimpleName();
	public static final String EXTRA_BRAND_ID = "extra_brand_id";
	public static final String EXTRA_BRAND_NAME = "extra_brand_name";

	private final ArrayList<JSONObject> DATA_SET = new ArrayList<JSONObject>();
	private static final int PAGE_SIZE = 20;
	private SupraLocation mLocation;

	private PullToRefreshListView mPullRefreshList;

	private int mPageLoaded = -1;
	private int mPagePending = -1;

	private String mBrandId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBrandId = getIntent().getStringExtra(EXTRA_BRAND_ID);

		setContentView(R.layout.activity_brand);

		findViewById(R.id.action_back).setOnClickListener(this);

		((TextView) findViewById(R.id.brand_name)).setText(getIntent()
				.getStringExtra(EXTRA_BRAND_NAME));
		;
		mPullRefreshList = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		mPullRefreshList.setOnRefreshListener(this);
		// mPullRefreshList.setEmptyView(new ProgressBar(this));
		mPullRefreshList.getRefreshableView().setAdapter(ADAPTER);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mPullRefreshList.setRefreshing(false);
	}

	private void request(int page) {
		NetworkCenter.getInstance().putToQueue(
				new RequestBrand(this, new BrandInfo(mBrandId, mLocation, page,
						PAGE_SIZE)));
	}

	private class ItemHolder {
		TextView name;
		TextView distance;
		TextView info;
	}

	private final BaseAdapter ADAPTER = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = View.inflate(BrandActivity.this,
						R.layout.brand_activity_item, null);
				ItemHolder holder = new ItemHolder();
				holder.info = (TextView) convertView.findViewById(R.id.info);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.distance = (TextView) convertView
						.findViewById(R.id.distance);
				convertView.setTag(holder);
			}

			String name = "";
			String distance = "";
			String info = "";

			JSONObject jo = getItem(position);
			try {
				info = jo.getString(BrandInfo.ACTIVITY_INFO);
				distance = jo.getString(BrandInfo.DISTANCE) + "m";
				name = jo.getString(BrandInfo.MALL_NAME);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			ItemHolder holder = (ItemHolder) convertView.getTag();
			holder.distance.setText(distance);

			holder.name.setText(name);
			holder.info.setText(info);
			holder.distance.setText(distance);

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
	public boolean HandleEvent(RequestEvent event, BrandInfo info) {

		Log.i(LOG_TAG, "HandleEvent FINISH : " + info);
		if (RequestEvent.FINISH == event) {
			mPullRefreshList.onRefreshComplete();
			if (info.ERROR_CODE.isOK()) {
				if (info.ARG_PAGE <= mPageLoaded) {
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

		}

		return false;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		Log.i(LOG_TAG, "onPullDownToRefresh");
		loadData(1);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		Log.i(LOG_TAG, "onPullDownToRefresh : " + mPageLoaded + 1);
		loadData(mPageLoaded + 1);
	}

	@Override
	public void callBack(SupraLocation location) {
		Log.i(LOG_TAG, "callBack : " + "mPagePending = " + mPagePending
				+ " location : " + location);
		mLocation = location;
		if (mPagePending > 0) {
			request(mPagePending);
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
			request(page);
		}
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
}
