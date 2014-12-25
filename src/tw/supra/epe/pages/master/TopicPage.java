package tw.supra.epe.pages.master;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.utils.TimeUtil;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

public class TopicPage extends BaseMainPage implements
		NetWorkHandler<TopicInfo> {
	private static final String LOG_TAG = TopicPage.class.getSimpleName();
	private JSONArray mJaTopics;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ListView v = new ListView(getActivity());
		v.setAdapter(ADAPTER);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		request();
	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return c.getText(R.string.master_page_indictor_tab_topic);
	}

	@Override
	public int getIconResId() {
		return 0;
	}

	private void request() {
		NetworkCenter.getInstance().putToQueue(
				new RequestTopics(this, new TopicInfo(null, 1)));
	}

	private class ItemHolder {
		NetworkImageView avator;
		TextView name;
		TextView time;
		TextView count;
		TextView content;
	}

	private final BaseAdapter ADAPTER = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = View.inflate(getActivity(),
						R.layout.master_page_topic_item, null);
				ItemHolder holder = new ItemHolder();
				holder.avator = (NetworkImageView) convertView
						.findViewById(R.id.avator);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.count = (TextView) convertView.findViewById(R.id.count);
				holder.content = (TextView) convertView
						.findViewById(R.id.content);
				convertView.setTag(holder);
			}

			ItemHolder holder = (ItemHolder) convertView.getTag();
			String name = "";
			String avator = "";
			long time = 0;
			String count = "";
			String content = "";

			JSONObject jo = getItem(position);
			try {
				name = jo.getString(TopicInfo.ATTR_T_USERNAME);
				avator = jo.getString(TopicInfo.ATTR_T_USER_PHOTO);
				time = jo.getLong(TopicInfo.ATTR_TOPIC_CREATE_TIME);
				count = jo.getString(TopicInfo.ATTR_TOPIC_REPOST_NUM);
				content = jo.getString(TopicInfo.ATTR_TOPIC_CONTENT);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			holder.avator.setImageUrl(avator, NetworkCenter.getInstance()
					.getImageLoader());
			holder.name.setText(name);
			holder.time.setText(TimeUtil.formatTimeWithCountDown(getActivity(),
					time));
			holder.count.setText(count);
			holder.content.setText(content);

			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public JSONObject getItem(int position) {
			try {
				return mJaTopics.getJSONObject(position);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public int getCount() {
			return (null == mJaTopics) ? 0 : mJaTopics.length();
		}
	};

	@Override
	public boolean HandleEvent(RequestEvent event, TopicInfo info) {
		if (RequestEvent.FINISH == event && info.ERROR_CODE.isOK()) {
			mJaTopics = info.resultJoList;
			ADAPTER.notifyDataSetChanged();
		}
		return false;
	}

}
