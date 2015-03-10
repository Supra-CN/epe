package tw.supra.epe.account;

import tw.supra.epe.activity.MainActivity;
import tw.supra.epe.core.BaseHostFrag;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.utils.Log;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yijiayi.yijiayi.R;

public class LoginFrag extends BaseHostFrag<LoginActivity> implements
		OnClickListener, NetWorkHandler<LoginInfo>, IUiListener {

	private static final String LOG_TAG = LoginFrag.class.getName();

	private EditText mEtPhone;
	private EditText mEtPassword;
	private Button mBtnLogin;

	private Tencent mTencent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_login, container, false);
		mEtPhone = (EditText) v.findViewById(R.id.et_phone);
		mEtPassword = (EditText) v.findViewById(R.id.et_password);
		mBtnLogin = (Button) v.findViewById(R.id.btn_login);
		v.findViewById(R.id.weibo).setOnClickListener(this);
		v.findViewById(R.id.qq).setOnClickListener(this);

		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mBtnLogin.setOnClickListener(this);
	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return c.getText(R.string.account_login_frag_title);
	}

	@Override
	public int getIconResId() {
		return 0;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			onClickLogin();
			break;
		case R.id.qq:
			onClickLoginWithQQ();
			break;
		case R.id.weibo:
			onClickLogin();
			break;

		default:
			break;
		}
	}

	private void onClickLoginWithQQ() {
		if (null == mTencent) {
			mTencent = Tencent.createInstance("1104065435", getActivity()
					.getApplicationContext());
		}
		mTencent.login(getActivity(), "all", this);
	}

	private void onClickLogin() {
		String phone = mEtPhone.getText().toString().trim();
		if (!AccountUtils.isLegalPassport(phone)) {
			Toast.makeText(getActivity(),
					R.string.account_login_toast_illegal_passport,
					Toast.LENGTH_SHORT).show();
			return;
		}
		String password = mEtPassword.getText().toString().trim();
		if (!AccountUtils.isLegalPassword(password)) {
			Toast.makeText(getActivity(),
					R.string.account_login_toast_illegal_password,
					Toast.LENGTH_SHORT).show();
			return;
		}
		requestPhoneLogin(phone, password);
	}

	private void requestPhoneLogin(String phone, String password) {
		getHostActivity().showProgressDialog();
		LoginInfo info = new LoginInfo();
		info.ARGS.putString(LoginInfo.ARG_STR_MOBILE, phone);
		info.ARGS.putString(LoginInfo.ARG_STR_PASSWORD, password);
		info.ARGS.putString(LoginInfo.ARG_STR_TYPE,
				LoginInfo.TYPE_LOGIN_BY_NORMAL);
		NetworkCenter.getInstance().putToQueue(new RequestLogin(this, info));
	}

	@Override
	public boolean HandleEvent(RequestEvent event, LoginInfo info) {
		switch (event) {
		case FINISH:
			getHostActivity().hideProgressDialog();
			if (info.ERROR_CODE.isOK()) {
				AccountCenter.switchUser(info.RESULTS.getString(
						LoginInfo.RESULT_STR_UID, User.ANONYMOUS));
				startActivity(new Intent(getActivity(), MainActivity.class));
				getActivity().finish();

			}
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_API) {
			if (resultCode == Constants.RESULT_LOGIN) {
				mTencent.handleLoginData(data, this);
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onComplete(Object arg0) {
		Log.i(LOG_TAG, "onComplete : " + arg0);
	}

	@Override
	public void onError(UiError arg0) {
		// TODO Auto-generated method stub
	}

}
