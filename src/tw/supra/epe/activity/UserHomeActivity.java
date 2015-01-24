package tw.supra.epe.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.account.AccountCenter;
import tw.supra.epe.account.RequestUserInfo;
import tw.supra.epe.account.User;
import tw.supra.epe.account.UserInfo;
import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.pages.RequestTArray;
import tw.supra.epe.pages.TArrayInfo;
import tw.supra.epe.ui.pullto.PullToRefreshStaggeredGridView;
import tw.supra.epe.ui.staggered.StaggeredGridView;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.network.ui.NetworkImageView;
import tw.supra.network.ui.NetworkRoundedImageView;
import tw.supra.utils.JsonUtils;
import tw.supra.utils.TimeUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public class UserHomeActivity extends BaseActivity implements OnClickListener,
		OnRefreshListener2<StaggeredGridView> {
	private static final String LOG_TAG = UserHomeActivity.class
			.getSimpleName();

	private final ArrayList<JSONObject> DATA_SET = new ArrayList<JSONObject>();
	private static final int PAGE_SIZE = 20;

	private NetworkRoundedImageView mAvator;
	private TextView mName;
	private TextView mAttentionCount;
	private TextView mFansCount;
	private PullToRefreshStaggeredGridView mPullRefreshGrid;

	private int mPageLoaded = -1;
	private int mAdjustImageWidth = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_home);
		findViewById(R.id.action_back).setOnClickListener(this);
		mAvator = (NetworkRoundedImageView) findViewById(R.id.avator);
		mName = (TextView) findViewById(R.id.name);
		mAttentionCount = (TextView) findViewById(R.id.attention_count);
		mFansCount = (TextView) findViewById(R.id.fans_count);

		mPullRefreshGrid = (PullToRefreshStaggeredGridView) findViewById(R.id.grid);
		mPullRefreshGrid.setOnRefreshListener(this);

		StaggeredGridView gridView = mPullRefreshGrid.getRefreshableView();
		gridView.setAdapter(ADAPTER);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		updateUiUserInfo();
		requestUserInfo();
		mPullRefreshGrid.setRefreshing(false);
	}

	private void requestUserInfo() {
		NetworkCenter.getInstance().putToQueue(
				new RequestUserInfo(HANDLE_USER_INFO, new UserInfo(
						AccountCenter.getCurrentUserUid())));
	}

	private void requestT(int page) {
		NetworkCenter.getInstance().putToQueue(
				new RequestTArray(HANDLE_T_INFO, new TArrayInfo(page, PAGE_SIZE)));
	}

	private final NetWorkHandler<TArrayInfo> HANDLE_T_INFO = new NetWorkHandler<TArrayInfo>() {
		@Override
		public boolean HandleEvent(RequestEvent event, TArrayInfo info) {
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
	};
	private final NetWorkHandler<UserInfo> HANDLE_USER_INFO = new NetWorkHandler<UserInfo>() {

		@Override
		public boolean HandleEvent(RequestEvent event, UserInfo info) {
			if (RequestEvent.FINISH == event && info.ERROR_CODE.isOK()) {
				updateUiUserInfo();
			}
			return false;
		}
	};

	private void updateUiUserInfo() {
		User user = AccountCenter.getCurrentUser();
		mAvator.setImageUrl(user.getAvatarUrl(), NetworkCenter.getInstance()
				.getImageLoader());
		mName.setText(user.getName());
		mFansCount.setText(getString(R.string.user_home_fans_count,
				user.getFansCount()));
		mAttentionCount.setText(getString(R.string.user_home_attention_count,
				user.getAttentionCount()));
	}

	private class ItemHolder {
		NetworkImageView img;
		TextView time;
		TextView likeCount;
		TextView commentCount;
	}

	private final BaseAdapter ADAPTER = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = View.inflate(UserHomeActivity.this,
						R.layout.user_info_activity_t_item, null);
				ItemHolder holder = new ItemHolder();
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
				width = JsonUtils.getIntSafely(joImg, TArrayInfo.ATTR_IMG_WIDTH);
				height = JsonUtils.getIntSafely(joImg, TArrayInfo.ATTR_IMG_HEIGTH);
				likeCount = jo.getString(TArrayInfo.ATTR_TT_LIKE_NUM);
				isLike = jo.getInt(TArrayInfo.ATTR_IS_LIKE) != 0;
				commentCount = jo.getString(TArrayInfo.ATTR_TT_COMMENT_NUM);
				time = jo.getLong(TArrayInfo.ATTR_ADD_TIME);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			holder.img.setImageUrl(img, NetworkCenter.getInstance()
					.getImageLoader());
			holder.time.setText(TimeUtil.formatTimeWithCountDown(
					UserHomeActivity.this, time));
			holder.likeCount.setText(likeCount);
			holder.likeCount.setSelected(isLike);
			holder.commentCount.setText(commentCount);
			adjustViewHeight(holder.img, width, height);

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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_back:
			onBackPressed();
			break;

		default:
			break;
		}
	}

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
		requestT(page);
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
