package tw.supra.epe.msg;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.activity.t.TContentInfo;
import tw.supra.epe.core.BaseActivity;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.network.ui.NetworkImageView;
import tw.supra.utils.JsonUtils;
import tw.supra.utils.TimeUtil;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MsgTopicActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener, OnRefreshListener<ListView>,
		NetWorkHandler<MsgTopicInfo> {
	private static final String LOG_TAG = MsgTopicActivity.class
			.getSimpleName();
	public static final String EXTRA_TOPIC_ID = "extra_topic_id";
	public static final String EXTRA_TOPIC_TITLE = "extra_topic_title";
	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */

	private final ArrayList<JSONObject> DATA_SET = new ArrayList<JSONObject>();

	private String mTopicId;
	private JSONObject mJoData;

	private TextView mLabel;
	private PullToRefreshListView mPullableList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTopicId = getIntent().getStringExtra(EXTRA_TOPIC_ID);

		setContentView(R.layout.activity_msg_topic);

		mLabel = (TextView) findViewById(R.id.label);
		mLabel.setText(getIntent().getStringExtra(EXTRA_TOPIC_TITLE));

		findViewById(R.id.action_back).setOnClickListener(this);
		
		mPullableList = (PullToRefreshListView) findViewById(R.id.list);
		mPullableList.getRefreshableView().setAdapter(ADAPTER);
		mPullableList.setOnItemClickListener(this);
		mPullableList.setOnRefreshListener(this);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mPullableList.setRefreshing(false);
	}

	private void request() {
		NetworkCenter.getInstance().putToQueue(
				new RequestMsgTopic(this, new MsgTopicInfo(mTopicId)));
	}

	@Override
	public boolean HandleEvent(RequestEvent event, MsgTopicInfo info) {
		if (RequestEvent.FINISH == event) {
			mPullableList.onRefreshComplete();
			if (info.ERROR_CODE.isOK()) {
				DATA_SET.clear();
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
		String like = "";
		String comment = "";
		String share = "";
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
			time = TimeUtil.formatTimeWithCountDown(this,
					JsonUtils.getLongSafely(mJoData, TContentInfo.ADD_TIME));
			content = JsonUtils.getStrSafely(mJoData, TContentInfo.TT_CONTENT);
			info = JsonUtils.getJaSafely(mJoData, TContentInfo.TT_DETAIL)
					.getJSONObject(0).toString();

			like = JsonUtils.getStrSafely(mJoData, TContentInfo.TT_LIKE_NUM);
			comment = JsonUtils.getStrSafely(mJoData,
					TContentInfo.TT_COMMENT_NUM);
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
		// mTvContent.setText(content);
		// mTvName.setText(name);
		// mTvTime.setText(time);
		// mTvInfo.setText(info);
		//
		// mTvShare.setText(share);
		// mTvLike.setText(like);
		// mTvComment.setText(comment);
		// mTvLike.setSelected(isLike);
		//
		// mIvAvator.setImageUrl(avator, NetworkCenter.getInstance()
		// .getImageLoader());
		// mIvImg.setImageUrl(img,
		// NetworkCenter.getInstance().getImageLoader());
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fetch_failed:
			v.setVisibility(View.GONE);
			break;
		case R.id.action_back:
			onBackPressed();
			break;

		default:
			break;
		}

	}

	private final BaseAdapter ADAPTER = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = View.inflate(MsgTopicActivity.this,
						R.layout.msg_topic_item, null);
				ItemHolder holder = new ItemHolder();
				holder.img = (NetworkImageView) convertView
						.findViewById(R.id.img);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.content = (TextView) convertView
						.findViewById(R.id.content);
				convertView.setTag(holder);
			}

			ItemHolder holder = (ItemHolder) convertView.getTag();
			String img = "";
			String title = "";
			long time = 0;
			String content = "";

			JSONObject jo = getItem(position);

			try {
				img = jo.getString(MsgTopicInfo.PHOTO);
				content = jo.getString(MsgTopicInfo.CONTENT);
				title = jo.getString(MsgTopicInfo.TITLE);
				time = jo.getLong(MsgTopicInfo.SEND_TIME);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			holder.img.setImageUrl(img, NetworkCenter.getInstance()
					.getImageLoader());
			holder.title.setText(title);
			holder.time.setText(TimeUtil.formatTimeWithCountDown(
					MsgTopicActivity.this, time));
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
		NetworkImageView img;
		TextView title;
		TextView time;
		TextView content;
	}

	private void loadData() {
		request();
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		loadData();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		try {
			Intent intent = new Intent(this, MsgActivity.class);
			intent.putExtra(MsgActivity.EXTRA_MSG_ID, JsonUtils.getStrSafely(DATA_SET.get(position-1), MsgTopicInfo.MSG_ID));
			startActivity(intent);
		} catch (JSONException e) {
//			 TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
