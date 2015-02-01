package tw.supra.epe.pages.msg;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.epe.msg.MsgTopicActivity;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MsgPage extends BaseMainPage implements OnItemClickListener,
		NetWorkHandler<EpeRequestInfo>, OnRefreshListener<ListView> {
	private static final String LOG_TAG = MsgPage.class.getSimpleName();

	private PullToRefreshListView mListView;
	private final ArrayList<MsgItem> DATA_SET = new ArrayList<MsgItem>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.page_msg, null);
		mListView = (PullToRefreshListView) v.findViewById(R.id.list_view);
		mListView.setOnRefreshListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(ADAPTER);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mListView.setRefreshing(false);
	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return c.getText(R.string.indictor_tab_msg);
	}

	@Override
	public int getIconResId() {
		return R.drawable.indicator_icon_msg;
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		NetworkCenter.getInstance().putToQueue(new RequestMsgs(this));
	}

	private MsgItem decodeMsg(JSONObject joMsg, String type, int iconResDefault)
			throws JSONException {

		MsgItem item = new MsgItem();
		item.icon = iconResDefault;
		item.type = type;
		item.id = joMsg.getString("latest_msg_id");
		item.title = joMsg.getString("latest_msg_title");
		item.content = joMsg.getString("latest_msg_content");
		// item.time = joMsg.getLong("latest_msg_time");
		item.unread = joMsg.getInt("unread_msg_num");
		return item;
	}

	private final BaseAdapter ADAPTER = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = View.inflate(getActivity(),
						R.layout.msg_page_item, null);
				ItemHolder holder = new ItemHolder();
				holder.avator = (ImageView) convertView.findViewById(R.id.img);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.content = (TextView) convertView
						.findViewById(R.id.content);
				// holder.time = (TextView) convertView.findViewById(R.id.time);
				convertView.setTag(holder);
			}

			ItemHolder holder = (ItemHolder) convertView.getTag();
			MsgItem msg = getItem(position);

			holder.avator.setImageResource(msg.icon);
			holder.title.setText(msg.title);
			holder.content.setText(msg.content);
			// holder.time.setText(TimeUtil.formatTimeWithCountDown(getActivity(),
			// time));
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public MsgItem getItem(int position) {
			return DATA_SET.get(position);
		}

		@Override
		public int getCount() {
			return DATA_SET.size();
		}
	};

	private class ItemHolder {
		ImageView avator;
		TextView title;
		TextView content;
		TextView time;
	}

	private class MsgItem {
		String type;
		int icon;
		String id;
		String title;
		String content;
		long time;
		int unread;
	}

	private static final String YY_MSG = "yy";
	private static final String SHOP_MSG = "shop";
	private static final String DYNAMIC_MSG = "dynamic";

	@Override
	public boolean HandleEvent(RequestEvent event, EpeRequestInfo info) {
		switch (event) {
		case FINISH: {
			mListView.onRefreshComplete();
			if (info.ERROR_CODE.isOK()) {
				JSONObject response = (JSONObject) info.OBJ;
				DATA_SET.clear();
				try {
					DATA_SET.add(decodeMsg(response.getJSONObject("yy_msg"),
							YY_MSG, R.drawable.ic_msg_epe));
					DATA_SET.add(decodeMsg(response.getJSONObject("shop_msg"),
							SHOP_MSG, R.drawable.ic_msg_shop));
					DATA_SET.add(decodeMsg(
							response.getJSONObject("dynamic_msg"), DYNAMIC_MSG,
							R.drawable.ic_msg_default));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				ADAPTER.notifyDataSetChanged();
			}
		}

			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		MsgItem item = DATA_SET.get(position - 1);
		Intent intent = new Intent(getActivity(), MsgTopicActivity.class);
		intent.putExtra(MsgTopicActivity.EXTRA_TOPIC_ID, item.type);
		intent.putExtra(MsgTopicActivity.EXTRA_TOPIC_TITLE, item.title);
		startActivity(intent);
	}
}
