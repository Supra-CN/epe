package tw.supra.epe.pages;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.activity.t.TActivity;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.epe.ui.pullto.PullToRefreshStaggeredGridView;
import tw.supra.epe.ui.staggered.StaggeredGridView;
import tw.supra.epe.ui.staggered.StaggeredGridView.OnItemClickListener;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.network.ui.NetworkImageView;
import tw.supra.network.ui.NetworkRoundedImageView;
import tw.supra.utils.JsonUtils;
import tw.supra.utils.TimeUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public class TPage extends BaseMainPage implements NetWorkHandler<TArrayInfo>,
		OnRefreshListener2<StaggeredGridView>, OnItemClickListener {
	private static final String LOG_TAG = TPage.class.getSimpleName();

	private static final int PAGE_SIZE = 20;
	private final ArrayList<JSONObject> DATA_SET = new ArrayList<JSONObject>();
	private PullToRefreshStaggeredGridView mPullRefreshGrid;

	private int mPageLoaded = -1;
	private int mAdjustImageWidth = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.page_t, null);
		// v.setText(this.getClass().getSimpleName());
		mPullRefreshGrid = (PullToRefreshStaggeredGridView) v
				.findViewById(R.id.grid);
		mPullRefreshGrid.setOnRefreshListener(this);
		mPullRefreshGrid.getRefreshableView().setAdapter(ADAPTER);
		mPullRefreshGrid.getRefreshableView().setOnItemClickListener(this);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (ADAPTER.isEmpty()) {
			Log.i(LOG_TAG, "onStart setRefreshing");
			mPullRefreshGrid.setRefreshing(false);
		} else {
			ADAPTER.notifyDataSetChanged();
		}
	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return c.getText(R.string.indictor_tab_t);
	}

	@Override
	public int getIconResId() {
		return R.drawable.indicator_icon_t;
	}

	@Override
	public boolean HandleEvent(RequestEvent event, TArrayInfo info) {
		Log.i(LOG_TAG, "HandleEvent FINISH : " + info);
		if (RequestEvent.FINISH == event) {
			mPullRefreshGrid.onRefreshComplete();
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
		}

		return false;
	}

	private class ItemHolder {
		NetworkRoundedImageView avator;
		NetworkImageView img;
		TextView name;
		TextView time;
		TextView likeCount;
		TextView commentCount;
	}

	private final BaseAdapter ADAPTER = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = View.inflate(getActivity(), R.layout.t_page_item,
						null);
				ItemHolder holder = new ItemHolder();
				holder.avator = (NetworkRoundedImageView) convertView
						.findViewById(R.id.avator);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.img = (NetworkImageView) convertView
						.findViewById(R.id.img);
				holder.likeCount = (TextView) convertView
						.findViewById(R.id.like_count);
				holder.commentCount = (TextView) convertView
						.findViewById(R.id.comment_count);
				convertView.setTag(holder);
			}

			ItemHolder holder = (ItemHolder) convertView.getTag();
			String name = "";
			String avator = "";
			String img = "";
			long time = 0;
			String likeCount = "";
			String commentCount = "";
			boolean isLike = false;
			int width = 0;
			int height = 0;

			JSONObject jo = getItem(position);
			try {
				JSONObject joImg = jo.getJSONObject(TArrayInfo.ATTR_IMG);
				img = joImg.getString(TArrayInfo.ATTR_IMG_URL);
				width = JsonUtils
						.getIntSafely(joImg, TArrayInfo.ATTR_IMG_WIDTH);
				height = JsonUtils.getIntSafely(joImg,
						TArrayInfo.ATTR_IMG_HEIGTH);
				likeCount = jo.getString(TArrayInfo.ATTR_TT_LIKE_NUM);
				isLike = jo.getInt(TArrayInfo.ATTR_IS_LIKE) != 0;
				commentCount = jo.getString(TArrayInfo.ATTR_TT_COMMENT_NUM);
				time = jo.getLong(TArrayInfo.ATTR_ADD_TIME);
				JSONObject joUinfo = jo.getJSONObject(TArrayInfo.ATTR_UINFO);
				avator = joUinfo.getString(TArrayInfo.ATTR_UINFO_PHOTO);
				name = joUinfo.getString(TArrayInfo.ATTR_UINFO_USER_NAME);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			adjustViewHeight(holder.img, width, height);
			holder.avator.setImageUrl(avator, NetworkCenter.getInstance()
					.getImageLoader());
			holder.img.setImageUrl(img, NetworkCenter.getInstance()
					.getImageLoader());
			holder.name.setText(name);
			holder.time.setText(TimeUtil.formatTimeWithCountDown(getActivity(),
					time));
			holder.likeCount.setText(likeCount);
			holder.likeCount.setSelected(isLike);
			holder.commentCount.setText(commentCount);

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
	public void onPullDownToRefresh(
			PullToRefreshBase<StaggeredGridView> refreshView) {
		loadData(1);
	}

	@Override
	public void onPullUpToRefresh(
			PullToRefreshBase<StaggeredGridView> refreshView) {
		loadData(mPageLoaded + 1);
	}

	private void loadData(int page) {
		Log.i(LOG_TAG, "loadData : " + "page = " + page);
		request(page);
	}

	private void request(int page) {
		NetworkCenter.getInstance().putToQueue(
				new RequestTArray(this, new TArrayInfo(page, PAGE_SIZE)));
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

	@Override
	public void onItemClick(StaggeredGridView parent, View view, int position,
			long id) {
		String tId=null;
		try {
			JSONObject jo = DATA_SET.get(position);
			tId = JsonUtils.getStrSafely(jo, TArrayInfo.ATTR_TT_ID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (!TextUtils.isEmpty(tId)) {
			Intent intent = new Intent(getActivity(), TActivity.class);
			intent.putExtra(TActivity.EXTRA_T_ID, tId);
			startActivity(intent);
		}

	}
}
