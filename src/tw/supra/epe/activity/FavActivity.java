package tw.supra.epe.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.account.AccountCenter;
import tw.supra.epe.account.RequestUserInfo;
import tw.supra.epe.account.User;
import tw.supra.epe.account.UserInfo;
import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.pages.RequestT;
import tw.supra.epe.pages.TInfo;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.utils.TimeUtil;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

public class FavActivity extends BaseActivity {
	private final ArrayList<JSONObject> DATA_SET = new ArrayList<JSONObject>();
	private static final int PAGE_SIZE = 20;

	private NetworkImageView mAvator;
	private TextView mName;
	private TextView mAttentionCount;
	private TextView mFansCount;
	private GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_home);
		mAvator = (NetworkImageView) findViewById(R.id.avator);
		mName = (TextView) findViewById(R.id.name);
		mAttentionCount = (TextView) findViewById(R.id.attention_count);
		mFansCount = (TextView) findViewById(R.id.fans_count);
		mGridView = (GridView) findViewById(R.id.grid_view);
		mGridView.setAdapter(ADAPTER);
		updateUiUserInfo();
		requestUserInfo();
		requestT();
	}

	private void requestUserInfo() {
		NetworkCenter.getInstance().putToQueue(
				new RequestUserInfo(HANDLE_USER_INFO, new UserInfo(
						AccountCenter.getCurrentUserUid())));
	}

	private void requestT() {
		NetworkCenter.getInstance().putToQueue(
				new RequestT(HANDLE_T_INFO, new TInfo(1, PAGE_SIZE)));
	}

	private final NetWorkHandler<TInfo> HANDLE_T_INFO = new NetWorkHandler<TInfo>() {
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
				convertView = View.inflate(FavActivity.this,
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

			JSONObject jo = getItem(position);
			try {
				img = jo.getString(TInfo.ATTR_IMG_URL);
				likeCount = jo.getString(TInfo.ATTR_TT_LIKE_NUM);
				commentCount = jo.getString(TInfo.ATTR_TT_COMMENT_NUM);
				time = jo.getLong(TInfo.ATTR_ADD_TIME);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			holder.img.setImageUrl(img, NetworkCenter.getInstance()
					.getImageLoader());
			holder.time.setText(TimeUtil.formatTimeWithCountDown(
					FavActivity.this, time));
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
