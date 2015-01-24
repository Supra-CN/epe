package tw.supra.epe.pages;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.network.NetworkCenter;
import tw.supra.network.ui.NetworkImageView;
import tw.supra.network.ui.NetworkRoundedImageView;
import tw.supra.utils.JsonUtils;
import tw.supra.utils.TimeUtil;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MsgPage extends BaseMainPage implements
		OnRefreshListener<ListView> {

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
		return v;
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

//			JSONObject jo = getItem(position);
			JSONObject jo = null;
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

			holder.avator.setImageUrl(avator, NetworkCenter.getInstance()
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
		public MsgItem getItem(int position) {
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
		TextView likeCount;
		TextView commentCount;
	}

	private class MsgItem {
		int icon;
		String id;
		String title;
		String content;
		long time;
		int unread;
	}

}
