package tw.supra.epe.core;

import tw.supra.epe.account.AccountCenter;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.umeng.analytics.MobclickAgent;
import com.yijiayi.yijiayi.R;

public abstract class BaseActivity extends ActionBarActivity {
	private ProgressDialog mProgressDialog;
	private String mUser = AccountCenter.getCurrentUserUid();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public void showProgressDialog() {
		if (null != mProgressDialog && mProgressDialog.isShowing()) {
			return;
		}
		mProgressDialog = ProgressDialog.show(this,
				getString(R.string.progressing_title),
				getString(R.string.progressing), false, false);
	}

	public void hideProgressDialog() {
		if (null != mProgressDialog) {
			mProgressDialog.cancel();
		}
	}

	protected String getUser() {
		return mUser;
	}

	protected void changeToCurrentUser() {
		mUser = AccountCenter.getCurrentUserUid();
	}

	protected void changeUser(String uid) {
		mUser = uid;
	}

	protected boolean isCurrentUser() {
		return AccountCenter.isCurrentUser(mUser);
	}

}
