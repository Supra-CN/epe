package tw.supra.epe.pages.msg;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MsgPage extends BaseMainPage implements
		NetWorkHandler<EpeRequestInfo>, OnRefreshListener<ListView> {
	private static final String LOG_TAG = MsgPage.class.getSimpleName();

	private PullToRefreshListView mListView;
	private final MsgItem EPE_MSG = new MsgItem();
	private final MsgItem SHOP_MSG = new MsgItem();
	private final MsgItem DYNAMIC_MSG = new MsgItem();
	private final ArrayList<MsgItem> DATA_SET = new ArrayList<MsgItem>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initDataSet();
	}

	private void initDataSet() {
		EPE_MSG.icon = R.drawable.ic_msg_epe;
		SHOP_MSG.icon = R.drawable.ic_msg_shop;

		DATA_SET.add(EPE_MSG);
		DATA_SET.add(SHOP_MSG);
		DATA_SET.add(DYNAMIC_MSG);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.page_msg, null);
		mListView = (PullToRefreshListView) v.findViewById(R.id.list_view);
		mListView.setOnRefreshListener(this);
		mListView.getRefreshableView().setAdapter(ADAPTER);
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
		NetworkCenter.getInstance().putToQueue(new RequestMsg(this));
	}

	private void decodeMsg(MsgItem item, JSONObject joMsg) throws JSONException {
		item.id = joMsg.getString("latest_msg_id");
		item.title = joMsg.getString("latest_msg_title");
		item.content = joMsg.getString("latest_msg_content");
		// item.time = joMsg.getLong("latest_msg_time");
		item.unread = joMsg.getInt("unread_msg_num");
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
		int icon = R.drawable.ic_msg_default;
		String id;
		String title;
		String content;
		long time;
		int unread;
	}

	@Override
	public boolean HandleEvent(RequestEvent event, EpeRequestInfo info) {
		switch (event) {
		case FINISH: {
			mListView.onRefreshComplete();
			if (info.ERROR_CODE.isOK()) {
				JSONObject response = (JSONObject) info.OBJ;
				try {
					decodeMsg(EPE_MSG, response.getJSONObject("yy_msg"));
					decodeMsg(SHOP_MSG, response.getJSONObject("shop_msg"));
					decodeMsg(DYNAMIC_MSG,
							response.getJSONObject("dynamic_msg"));
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

}
