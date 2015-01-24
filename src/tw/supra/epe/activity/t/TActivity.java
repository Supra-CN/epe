package tw.supra.epe.activity.t;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.account.AccountCenter;
import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.pages.TArrayInfo;
import tw.supra.epe.ui.staggered.StaggeredGridView;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.network.ui.NetworkImageView;
import tw.supra.network.ui.NetworkRoundedImageView;
import tw.supra.utils.JsonUtils;
import tw.supra.utils.TimeUtil;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public class TActivity extends BaseActivity implements OnClickListener,OnRefreshListener2<ListView>,
		NetWorkHandler<TContentInfo> {
	private static final String LOG_TAG = TActivity.class.getSimpleName();
	public static final String EXTRA_T_ID = "t_id";
	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */

	private final ArrayList<JSONObject> DATA_SET = new ArrayList<JSONObject>();
	private static final int PAGE_SIZE = 20;

	private String mTId;
	private JSONObject mJoData;

	private TextView mTvLike;
	private TextView mTvComment;
	private TextView mTvShare;

	private NetworkRoundedImageView mIvAvator;
	private NetworkImageView mIvImg;
	private TextView mTvName;
	private TextView mTvTime;
	private TextView mTvContent;
	private TextView mTvInfo;
	
	private PullToRefreshListView mPullableList;

	private static Handler sHandler = new Handler();
	
	private int mPageLoaded = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTId = getIntent().getStringExtra(EXTRA_T_ID);

		setContentView(R.layout.activity_t);
		mTvLike = (TextView) findViewById(R.id.like);
		mTvComment = (TextView) findViewById(R.id.comment);
		mTvShare = (TextView) findViewById(R.id.share);

		View header = View.inflate(this, R.layout.t_content_header, null);
		mIvAvator = (NetworkRoundedImageView) header.findViewById(R.id.avator);
		mIvImg = (NetworkImageView) header.findViewById(R.id.img);
		mTvName = (TextView) header.findViewById(R.id.name);
		mTvTime = (TextView) header.findViewById(R.id.time);
		mTvContent = (TextView) header.findViewById(R.id.content);
		mTvInfo = (TextView) header.findViewById(R.id.info);

		mPullableList = (PullToRefreshListView) findViewById(R.id.list);
		mPullableList.getRefreshableView().addHeaderView(header);
		mPullableList.getRefreshableView().setAdapter(ADAPTER);
		mPullableList.setOnRefreshListener(this);

		mTvLike = (TextView) findViewById(R.id.like);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		requestTContent();
		mPullableList.setRefreshing(false);
	}

	@Override
	public boolean HandleEvent(RequestEvent event, TContentInfo info) {

		Log.i(LOG_TAG, "HandleEvent FINISH : " + info);
		if (RequestEvent.FINISH == event) {
			hideProgressDialog();
			if (info.ERROR_CODE.isOK() && info.resultTInfo != null) {
				mJoData = info.resultTInfo;
				updateUi();
			} else {
				findViewById(R.id.fetch_failed).setVisibility(View.VISIBLE);
			}
		}

		return false;
	}

	private void requestTContent() {
		showProgressDialog();
		NetworkCenter.getInstance().putToQueue(
				new RequestTContent(this, new TContentInfo(AccountCenter
						.getCurrentUserUid(), mTId)));
	}
	private void requestTComment(int page) {
		NetworkCenter.getInstance().putToQueue(
				new RequestTComment(HANDLE_COMMENT, new TCommentInfo(mTId, page, PAGE_SIZE)));
	}
	
	final private NetWorkHandler<TCommentInfo> HANDLE_COMMENT = new NetWorkHandler<TCommentInfo>() {
		
		@Override
		public boolean HandleEvent(RequestEvent event, TCommentInfo info) {
			if (RequestEvent.FINISH == event) {
				mPullableList.onRefreshComplete();
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

	private void updateUi() {

		if (mJoData == null) {
			return;
		}

		String img = "";
		String avator = "";
		String name = "";
		String time = "";
		String content = "";
		String info = "";
		int width = 0;
		int height = 0;
		String like ="";
		String comment="";
		String share="";
		Boolean isLike = false;

		try {
			
			JSONObject joUser = JsonUtils.getJoSafely(mJoData,
					TContentInfo.UINFO);
			avator = JsonUtils.getStrSafely(joUser, TContentInfo.PHOTO);
			name = JsonUtils.getStrSafely(joUser, TContentInfo.USER_NAME);
			
			JSONObject joImg = JsonUtils.getJoSafely(mJoData,
					TContentInfo.IMAGE);
			img = JsonUtils.getStrSafely(joImg, TContentInfo.IMG_SRC);
			width = JsonUtils.getIntSafely(joImg, TContentInfo.IMG_WIDTH);
			height = JsonUtils.getIntSafely(joImg, TContentInfo.IMG_HEIGHT);

			isLike = JsonUtils.getIntSafely(mJoData, TContentInfo.IS_LIKE, 0) != 0;
			time = TimeUtil.formatTimeWithCountDown(this, JsonUtils.getLongSafely(mJoData, TContentInfo.ADD_TIME));
			content = JsonUtils.getStrSafely(mJoData, TContentInfo.TT_CONTENT);
			info = JsonUtils.getJaSafely(mJoData, TContentInfo.TT_DETAIL).getJSONObject(0).toString();	
			
			like = JsonUtils.getStrSafely(mJoData, TContentInfo.TT_LIKE_NUM);
			comment = JsonUtils.getStrSafely(mJoData, TContentInfo.TT_COMMENT_NUM);
			share = JsonUtils.getStrSafely(mJoData, TContentInfo.TT_SHARE_NUM);
			
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
		mTvContent.setText(content);
		mTvName.setText(name);
		mTvTime.setText(time);
		mTvInfo.setText(info);
		
		mTvShare.setText(share);
		mTvLike.setText(like);
		mTvComment.setText(comment);
		mTvLike.setSelected(isLike);
		
		mIvAvator.setImageUrl(avator, NetworkCenter.getInstance().getImageLoader());
		mIvImg.setImageUrl(img, NetworkCenter.getInstance().getImageLoader());
		 adjustViewHeight(mIvImg, width, height);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fetch_failed:
			v.setVisibility(View.GONE);
			requestTContent();
			break;
		case R.id.action_back:
			onBackPressed();
			break;

		default:
			break;
		}

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

	private final BaseAdapter ADAPTER = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = View.inflate(TActivity.this,
						R.layout.t_activity_comment_item, null);
				ItemHolder holder = new ItemHolder();
				holder.avator = (NetworkRoundedImageView) convertView
						.findViewById(R.id.avator);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.content = (TextView) convertView
						.findViewById(R.id.content);
				convertView.setTag(holder);
			}

			ItemHolder holder = (ItemHolder) convertView.getTag();
			String avator = "";
			String name = "";
			long time = 0;
			String content = "";

			JSONObject jo = getItem(position);

			try {
				avator = jo.getString(TCommentInfo.photo);
				content = jo.getString(TCommentInfo.repost_content);
				name = jo.getString(TCommentInfo.user_name);
				time = jo.getLong(TCommentInfo.post_time);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			holder.avator.setImageUrl(avator, NetworkCenter.getInstance()
					.getImageLoader());
			holder.name.setText(name);
			holder.time.setText(TimeUtil.formatTimeWithCountDown(
					TActivity.this, time));
			holder.content.setText(content);

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
		NetworkRoundedImageView avator;
		TextView name;
		TextView time;
		TextView content;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadData(1);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadData(mPageLoaded + 1);
	}
	
	private void loadData(int page) {
		Log.i(LOG_TAG, "loadData : " + "page = " + page);
		requestTComment(page);
	}

}
