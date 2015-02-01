package tw.supra.epe.msg;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseActivity;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.network.ui.NetworkImageView;
import tw.supra.utils.JsonUtils;
import tw.supra.utils.TimeUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MsgActivity extends BaseActivity implements OnClickListener,
		NetWorkHandler<EpeRequestInfo> {
	private static final String LOG_TAG = MsgActivity.class.getSimpleName();
	public static final String EXTRA_MSG_ID = "extra_msg_id";
	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */

	private final ArrayList<JSONObject> DATA_SET = new ArrayList<JSONObject>();

	private String mMsgId;
	private JSONObject mJoData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMsgId = getIntent().getStringExtra(EXTRA_MSG_ID);
		setContentView(R.layout.activity_msg);
		findViewById(R.id.action_back).setOnClickListener(this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		request();
	}

	private void request() {
		showProgressDialog();
		NetworkCenter.getInstance().putToQueue(new RequestMsg(this, mMsgId));
	}

	@Override
	public boolean HandleEvent(RequestEvent event, EpeRequestInfo info) {
		if (RequestEvent.FINISH == event) {
			hideProgressDialog();
			if (info.ERROR_CODE.isOK()) {
				DATA_SET.clear();
				mJoData = (JSONObject) info.OBJ;
				updateUi();
			}
		}
		return false;
	}

	private void updateUi() {

		if (mJoData == null) {
			return;
		}

		String img = "";
		String title = "";
		String name = "";
		String time = "";
		String content = "";
		int width = 0;
		int height = 0;

		try {

			title = JsonUtils.getStrSafely(mJoData, "title");
			name = JsonUtils.getStrSafely(mJoData, "from_username");

			img = JsonUtils.getStrSafely(mJoData, "photo");

			time = TimeUtil.formatTimeWithCountDown(this,
					JsonUtils.getLongSafely(mJoData, "send_time"));
			content = JsonUtils.getStrSafely(mJoData, "content");

			// if (null != joImages) {
			// JSONObject joOriginal = JsonUtils.getJoSafely(joImages,
			// ProductInfo.IMG_ORIGINAL);
			// if (null != joOriginal) {
			// img = JsonUtils.getStrSafely(joOriginal,
			// ProductInfo.IMG_SRC);
			// width = JsonUtils.getIntSafely(joOriginal,
			// ProductInfo.IMG_WIDTH);
			// height = JsonUtils.getIntSafely(joOriginal,
			// ProductInfo.IMG_HEIGHT);
			// }
			// }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		((TextView) findViewById(R.id.title)).setText(title);
		((TextView) findViewById(R.id.time)).setText(time);
		((TextView) findViewById(R.id.name)).setText(name);
		((TextView) findViewById(R.id.content)).setText(content);
		((NetworkImageView) findViewById(R.id.img)).setImageUrl(img,
				NetworkCenter.getInstance().getImageLoader());

		// mTvContent.setText(content);
		// mTvName.setText(name);
		// mTvTime.setText(time);
		// mTvInfo.setText(info);
		//
		// mTvShare.setText(share);
		// mTvLike.setText(like);
		// mTvComment.setText(comment);
		// mTvLike.setSelected(isLike);
		//
		// mIvAvator.setImageUrl(avator, NetworkCenter.getInstance()
		// .getImageLoader());
		// mIvImg.setImageUrl(img,
		// NetworkCenter.getInstance().getImageLoader());
		// adjustViewHeight(mIvImg, width, height);
	}

	private int adjustViewWidth() {
		return getResources().getDisplayMetrics().widthPixels;
	}

	private void adjustViewHeight(View view, int width, int height) {
		Log.i(LOG_TAG, "===adjustIconView start===");

		int iw = width;
		int ih = height;
		Log.i(LOG_TAG, "iw : " + iw);
		Log.i(LOG_TAG, "ih : " + ih);
		if (iw < 0 || ih < 0) {
			return;
		}

		float ratio = (Float.valueOf(iw) / Float.valueOf(ih));
		Log.i(LOG_TAG, "ratio : " + ratio);

		int vw = view.getWidth();
		int vh = -1;
		Log.i(LOG_TAG, "vw : " + vw);

		if (vw < 1) {
			vw = adjustViewWidth();
			Log.i(LOG_TAG, "vw = ADJUST_IMAGE_WIDTH : " + vw);
		}

		vh = Float.valueOf(vw / ratio).intValue();
		Log.i(LOG_TAG, "vh : " + vh);
		view.getLayoutParams().height = vh;

		Log.i(LOG_TAG, "===adjustIconView end===");
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

}
