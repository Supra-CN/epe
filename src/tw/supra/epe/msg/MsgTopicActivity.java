package tw.supra.epe.msg;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.core.BaseActivity;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.network.ui.NetworkImageView;
import tw.supra.network.ui.NetworkRoundedImageView;
import tw.supra.utils.JsonUtils;
import tw.supra.utils.TimeUtil;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.yijiayi.yijiayi.R;

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
				holder.avatar = (NetworkRoundedImageView) convertView
						.findViewById(R.id.avator);
				holder.img = (NetworkImageView) convertView
						.findViewById(R.id.img);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.content = (TextView) convertView
						.findViewById(R.id.content);
				convertView.setTag(holder);
			}

			ItemHolder holder = (ItemHolder) convertView.getTag();
			String name = "";
			String avatar = "";
			String img = "";
			String title = "";
			long time = 0;
			String content = "";

			JSONObject jo = getItem(position);

			try {
				 avatar = jo.getString(MsgTopicInfo.PHOTO);
				img = jo.getString(MsgTopicInfo.PIC);
				content = jo.getString(MsgTopicInfo.CONTENT);
				name = jo.getString(MsgTopicInfo.FROM_USERNAME);
				title = jo.getString(MsgTopicInfo.TITLE);
				time = jo.getLong(MsgTopicInfo.SEND_TIME);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (TextUtils.isEmpty(img)) {
				holder.img.setVisibility(View.GONE);
			} else {
				holder.img.setVisibility(View.VISIBLE);
				holder.img.setImageUrl(img, NetworkCenter.getInstance()
						.getImageLoader());
			}
			
			holder.avatar.setImageUrl(avatar, NetworkCenter.getInstance()
					.getImageLoader());
			holder.name.setText(name);
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
		NetworkRoundedImageView avatar;
		NetworkImageView img;
		TextView name;
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
			intent.putExtra(MsgActivity.EXTRA_MSG_ID, JsonUtils.getStrSafely(
					DATA_SET.get(position - 1), MsgTopicInfo.MSG_ID));
			startActivity(intent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
