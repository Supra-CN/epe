package tw.supra.epe.account;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseHostFrag;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.NetWorkHandler;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginFrag extends BaseHostFrag<LoginActivity> implements
		OnClickListener, NetWorkHandler<LoginInfo> {

	private EditText mEtPhone;
	private EditText mEtPassword;
	private Button mBtnLogin;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_login, container, false);
		mEtPhone = (EditText) v.findViewById(R.id.et_phone);
		mEtPassword = (EditText) v.findViewById(R.id.et_password);
		mBtnLogin = (Button) v.findViewById(R.id.btn_login);
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

		default:
			break;
		}
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
                AccountHelper.switchUser(info.RESULTS.getString(RegInfo.RESULT_STR_UID, User.ANONYMOUS));
                getActivity().finish();
            }
            break;

        default:
            break;
    }
    return true;
	}

}
