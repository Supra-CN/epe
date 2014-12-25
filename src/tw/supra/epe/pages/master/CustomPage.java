package tw.supra.epe.pages.master;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseMainPage;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

public class CustomPage extends BaseMainPage implements
		NetWorkHandler<CustomInfo> {
	private static final String LOG_TAG = CustomPage.class.getSimpleName();

	private JSONArray mJa;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
		return c.getText(R.string.master_page_indictor_tab_custom);
	}

	@Override
	public int getIconResId() {
		return 0;
	}

	private void request() {
		NetworkCenter.getInstance().putToQueue(
				new RequestCustom(this, new CustomInfo(null, 1)));
	}

	private class ItemHolder {
		NetworkImageView avator;
		NetworkImageView img;
		TextView name;
	}

	private final BaseAdapter ADAPTER = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = View.inflate(getActivity(),
						R.layout.master_page_custom_item, null);
				ItemHolder holder = new ItemHolder();
				holder.avator = (NetworkImageView) convertView
						.findViewById(R.id.avator);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.img = (NetworkImageView) convertView
						.findViewById(R.id.img);
				convertView.setTag(holder);
			}

			ItemHolder holder = (ItemHolder) convertView.getTag();
			String name = "";
			String avator = "";
			String img = "";

			JSONObject jo = getItem(position);
			try {
				name = jo.getString(CustomInfo.ATTR_NAME);
				avator = jo.getString(CustomInfo.ATTR_U_PHOTO);
				img = jo.getString(CustomInfo.ATTR_T_IMG);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			holder.avator.setImageUrl(avator, NetworkCenter.getInstance()
					.getImageLoader());
			holder.img.setImageUrl(img, NetworkCenter.getInstance()
					.getImageLoader());
			holder.name.setText(name);

			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public JSONObject getItem(int position) {
			try {
				return mJa.getJSONObject(position);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public int getCount() {
			return (null == mJa) ? 0 : mJa.length();
		}
	};

	@Override
	public boolean HandleEvent(RequestEvent event, CustomInfo info) {
		if (RequestEvent.FINISH == event && info.ERROR_CODE.isOK()) {
			mJa = info.resultJoList;
			ADAPTER.notifyDataSetChanged();
		}
		return false;
	}

}
