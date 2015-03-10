package tw.supra.epe;

import tw.supra.data.CommonData;
import tw.supra.epe.ColumnDef.PrefCommon;
import tw.supra.location.LocationCenter;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.utils.TimeUtil;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.view.WindowManager;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yijiayi.yijiayi.BuildConfig;
import com.yijiayi.yijiayi.R;

public class App extends Application implements NetWorkHandler<CheckUpdateInfo> {
	public static final String ACTION_LOGIN = "com.sohu.club.intent.action.ACTION_LOGIN";
	private static App sInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
		MobclickAgent.setDebugMode(BuildConfig.DEBUG);
		LocationCenter.getInstance().startUp(this);

	}

	public static App getInstance() {
		return sInstance;
	}

	public boolean checkUpdateIfNecessary() {
		long dateOfLastCheckOut = CommonData.getInstance().getPrefLong(
				PrefCommon.DATE_OF_VERSION_CHECK, 0);
		if (TimeUtil.compareDiffAsDay(dateOfLastCheckOut,
				System.currentTimeMillis()) > 7) {
			checkUpdate();
			return true;
		} else {
			return false;
		}
	}

	public void checkUpdate() {
		NetworkCenter.getInstance().putToQueue(new RequestCheckUpdate(this));
	}

	@Override
	public boolean HandleEvent(RequestEvent event, CheckUpdateInfo info) {
		switch (event) {
		case FINISH:
			// hideProgressDialog();
			if (info.ERROR_CODE.isOK()) {
				if (info.isUpToDate()) {
					Toast.makeText(this, R.string.update_toast_up_to_date,
							Toast.LENGTH_SHORT).show();
				} else {
					showUpdateInfoDialog(info);
				}
			}
			break;

		default:
			break;
		}
		return false;
	}

	private void showUpdateInfoDialog(CheckUpdateInfo info) {
		UpdateInfoDialogActionListener listener = new UpdateInfoDialogActionListener(
				info);
		Builder builder = new Builder(this);
		builder.setTitle(
				getString(R.string.update_title, info.resultVersionName))
				.setMessage(info.resultInfo)
				.setPositiveButton(R.string.update_btn_pos, listener)
				.setNegativeButton(R.string.update_btn_neg, listener);
		AlertDialog dialog = builder.create();
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();

	}

	private class UpdateInfoDialogActionListener implements OnClickListener {
		final CheckUpdateInfo INFO;

		public UpdateInfoDialogActionListener(CheckUpdateInfo info) {
			INFO = info;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case Dialog.BUTTON_POSITIVE:
				Uri url = Uri.parse(INFO.resultUrl);
				CommonData.getInstance().putPrefLong(
						PrefCommon.DATE_OF_VERSION_CHECK,
						System.currentTimeMillis());
				NetworkCenter.getInstance().download(url,
						getString(R.string.app_name), INFO.resultInfo);
				break;

			default:
				break;
			}
		}
	}

}
