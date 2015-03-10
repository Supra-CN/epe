package tw.supra.epe.mall;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.activity.FocusActivity;
import tw.supra.epe.activity.FocusActivity.EditableChengeListener;
import tw.supra.epe.activity.brand.RequestPushBrandFocusStatus;
import tw.supra.epe.core.BaseHostFrag;
import tw.supra.epe.store.StoreActivity;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yijiayi.yijiayi.R;

public class FocusMallPage extends BaseHostFrag<FocusActivity> implements
		OnItemClickListener, OnRefreshListener2<ListView>,
		NetWorkHandler<FocusMallInfo>, EditableChengeListener, OnClickListener {
	private static final String LOG_TAG = FocusMallPage.class.getSimpleName();
	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */

	private final ArrayList<JSONObject> DATA_SET = new ArrayList<JSONObject>();

	private JSONObject mJoData;

	private PullToRefreshListView mPullableList;

	private static Handler sHandler = new Handler();

	private int mPageLoaded = -1;
	private int mPagePending = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mPullableList = new PullToRefreshListView(getActivity());
		mPullableList.getRefreshableView().setAdapter(ADAPTER);
		mPullableList.setOnItemClickListener(this);
		mPullableList.setOnRefreshListener(this);
		return mPullableList;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		sHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mPullableList.setRefreshing();
			}
		}, 500);
	}

	private void request(int page) {
		NetworkCenter.getInstance().putToQueue(
				new RequestFocusMall(this, new FocusMallInfo(page)));
	}

	@Override
	public boolean HandleEvent(RequestEvent event, FocusMallInfo info) {
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

	private final BaseAdapter ADAPTER = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = View.inflate(getActivity(),
						R.layout.focus_mall_item, null);
				ItemHolder holder = new ItemHolder();
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.info = (TextView) convertView.findViewById(R.id.info);
				holder.btn = (Button) convertView.findViewById(R.id.btn_del);
				holder.btn.setOnClickListener(FocusMallPage.this);
				convertView.setTag(holder);
			}

			ItemHolder holder = (ItemHolder) convertView.getTag();
			String name = "";
			String info = "";

			JSONObject jo = getItem(position);

			try {
				name = jo.getString("mall_name");
				info = jo.getString("address");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			holder.name.setText(name);
			holder.info.setText(info);
			holder.btn.setTag(position);
			holder.btn
					.setVisibility(getHostActivity().isEditable() ? View.VISIBLE
							: View.GONE);

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
		TextView name;
		TextView info;
		Button btn;
	}

	private void loadData(int page) {
		request(page);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		JSONObject joItem = DATA_SET.get(position - 1);
		try {
			if (joItem.getString("mall_type").equals("2")) {
				Intent intent = new Intent(getActivity(), StoreActivity.class);
				intent.putExtra(StoreActivity.EXTRA_MB_ID,
						joItem.getString("mall_id"));
				intent.putExtra(StoreActivity.EXTRA_MALL_NAME,
						joItem.getString("mall_name"));
				startActivity(intent);
			} else {
				Intent intent = new Intent(getActivity(), MallActivity.class);
				intent.putExtra(MallActivity.EXTRA_MALL_ID,
						joItem.getString("mall_id"));
				startActivity(intent);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return c.getString(R.string.focus_mall_label);
	}

	@Override
	public int getIconResId() {
		return 0;
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
	public void onClick(View v) {
		int pos = (Integer) v.getTag();
		JSONObject jo = DATA_SET.get(pos);
		try {
			NetworkCenter.getInstance().putToQueue(
					new RequestPushBrandFocusStatus(HANDLER_PUSH_FOCUS_STATUS,
							jo.getString("mall_id"), false));
			DATA_SET.remove(pos);
			ADAPTER.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		

	}

	private final NetWorkHandler<EpeRequestInfo> HANDLER_PUSH_FOCUS_STATUS = new NetWorkHandler<EpeRequestInfo>() {

		@Override
		public boolean HandleEvent(RequestEvent event, EpeRequestInfo info) {
			if (event == RequestEvent.FINISH) {
				if (!info.ERROR_CODE.isOK()) {
				}
			}
			return false;
		}
	};

	@Override
	public void onEditableChenge(boolean editable) {
		ADAPTER.notifyDataSetChanged();
	}

}
