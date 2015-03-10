package tw.supra.epe.store;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yijiayi.yijiayi.R;

public class MallPickerDialog extends Dialog implements
		NetWorkHandler<EpeRequestInfo>, OnItemClickListener {
	private static final String LOG_TAG = MallPickerDialog.class
			.getSimpleName();

	private final ArrayList<ObjMall> DATA_SET = new ArrayList<ObjMall>();

	public final OnMallPickedListener LISTENER;

	public final ObjArea AREA;

	public interface OnMallPickedListener {
		public void onMallPicked(ObjMall area);
	}

	public MallPickerDialog(Context context, OnMallPickedListener listener,
			ObjArea area) {
		super(context, R.style.CleanDialog);
		LISTENER = listener;
		AREA = area;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new ProgressBar(getContext()));
		request();
	}

	private void request() {
		NetworkCenter.getInstance().putToQueue(new RequestMalls(this, AREA));
	}

	private class MallAdapter extends BaseAdapter {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = (TextView) View.inflate(getContext(),
					R.layout.area_item_group, null);

			tv.setText(getItem(position).name);
			return tv;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public ObjMall getItem(int position) {
			return DATA_SET.get(position);
		}

		@Override
		public int getCount() {
			return DATA_SET.size();
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		LISTENER.onMallPicked(DATA_SET.get(position));
		dismiss();
	}

	@Override
	public boolean HandleEvent(RequestEvent event, EpeRequestInfo info) {

		switch (event) {
		case FINISH:
			if (info.ERROR_CODE.isOK()) {
				JSONArray ja = (JSONArray) info.OBJ;
				DATA_SET.clear();
				for (int i = 0; i < ja.length(); i++) {
					try {
						ObjMall mall = new ObjMall();
						JSONObject jo = ja.getJSONObject(i);
						mall.name = jo.getString("mall_name");
						mall.mallId = jo.getString("mall_id");
						mall.address = jo.getString("address");
						DATA_SET.add(mall);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				ListView lv = new ListView(getContext());
				TextView tv  = new TextView(getContext());
				setContentView(lv);
				addContentView(tv, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
				lv.setAdapter(new MallAdapter());
				lv.setOnItemClickListener(this);
				lv.setEmptyView(tv);
				tv.setText(R.string.store_apply_pick_mall_empty);
			}
			break;

		default:
			break;
		}
		return false;
	}
}
