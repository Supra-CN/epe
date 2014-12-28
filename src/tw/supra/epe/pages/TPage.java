package tw.supra.epe.pages;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.utils.TimeUtil;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

public class TPage extends BaseMainPage implements NetWorkHandler<TInfo> {
	private static final int PAGE_SIZE = 20;
	private final ArrayList<JSONObject> DATA_SET = new ArrayList<JSONObject>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TextView v = (TextView) inflater.inflate(R.layout.page_t, null);
		// v.setText(this.getClass().getSimpleName());
		GridView v = new GridView(getActivity());
		v.setNumColumns(2);
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
		return c.getText(R.string.indictor_tab_t);
	}

	@Override
	public int getIconResId() {
		return R.drawable.indicator_icon_t;
	}

	@Override
	public boolean HandleEvent(RequestEvent event, TInfo info) {
		if (RequestEvent.FINISH == event && info.ERROR_CODE.isOK()
				&& null != info.resultJoList) {

			for (int i = 0; i < info.resultJoList.length(); i++) {
				try {
					DATA_SET.add(info.resultJoList.getJSONObject(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			ADAPTER.notifyDataSetChanged();
		}
		return false;
	}

	private void request() {
		NetworkCenter.getInstance().putToQueue(
				new RequestT(this, new TInfo(1, PAGE_SIZE)));
	}

	private class ItemHolder {
		NetworkImageView avator;
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
				holder.avator = (NetworkImageView) convertView
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

			JSONObject jo = getItem(position);
			try {
				img = jo.getString(TInfo.ATTR_IMG_URL);
				likeCount = jo.getString(TInfo.ATTR_TT_LIKE_NUM);
				commentCount = jo.getString(TInfo.ATTR_TT_COMMENT_NUM);
				time = jo.getLong(TInfo.ATTR_ADD_TIME);
				JSONObject joUinfo = jo.getJSONObject(TInfo.ATTR_UINFO);
				avator = joUinfo.getString(TInfo.ATTR_UINFO_PHOTO);
				name = joUinfo.getString(TInfo.ATTR_UINFO_USER_NAME);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			holder.avator.setImageUrl(avator, NetworkCenter.getInstance()
					.getImageLoader());
			holder.img.setImageUrl(img, NetworkCenter.getInstance()
					.getImageLoader());
			holder.name.setText(name);
			holder.time.setText(TimeUtil.formatTimeWithCountDown(getActivity(),
					time));
			holder.likeCount.setText(likeCount);
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

}
