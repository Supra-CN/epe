
package tw.supra.epe.account;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import tw.supra.epe.R;
import tw.supra.epe.account.RequestVerifyCode.Type;
import tw.supra.epe.core.BaseHostFrag;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;

public class RegFrag extends BaseHostFrag<LoginActivity> implements OnClickListener {
    private static final int DELAY_VERIFY_CODE = 60;// 定义发送验证码后的倒数计时

    private EditText mEtPhone;
    private EditText mEtVerifyCode;
    private EditText mEtPassword;
    private Button mBtnVerifyCode;
    private Button mBtnReg;

    private int mCountDown = 0;// 发送验证码后的倒数计时
    private String mVerifyCode;

    private static Handler sHandler = new Handler();

    private final NetWorkHandler<EpeRequestInfo> HANDLE_VERIFY_CODE = new NetWorkHandler<EpeRequestInfo>() {
        @Override
        public boolean HandleEvent(RequestEvent event,
                EpeRequestInfo info) {
            switch (event) {
                case FINISH:
                    if (info.ERROR_CODE.isOK()) {
                        mVerifyCode = info.RESULTS.getString(RequestVerifyCode.RESULT_CODE);
                        // TODO:清理测试代码
                        mEtVerifyCode.setText(mVerifyCode);
                        Toast.makeText(getActivity(),
                                R.string.account_toast_wait_for_verify_code,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        mCountDown = 0;
                    }
                    break;

                default:
                    break;
            }
            return true;
        }
    };

    private final NetWorkHandler<RegInfo> HANDLE_REG = new NetWorkHandler<RegInfo>() {

        @Override
        public boolean HandleEvent(RequestEvent event, RegInfo info) {
            switch (event) {
                case FINISH:
                    getHostActivity().hideProgressDialog();
                    if (info.ERROR_CODE.isOK()) {
//                        ClubAccountManager.getInstance().doLogIn(info.PHONE_NUM, info.PASSWORD);
                        getActivity().finish();
                    }
                    break;

                default:
                    break;
            }
            return true;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reg, container, false);
        mEtPhone = (EditText) v.findViewById(R.id.et_phone);
        mEtVerifyCode = (EditText) v.findViewById(R.id.et_verifycode);
        mEtPassword = (EditText) v.findViewById(R.id.et_password);
        mBtnVerifyCode = (Button) v.findViewById(R.id.btn_verifycode);
        mBtnReg = (Button) v.findViewById(R.id.btn_reg);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnVerifyCode.setOnClickListener(this);
        mBtnReg.setOnClickListener(this);
    }

    @Override
    protected CharSequence getDefaultTitle(Context c) {
        return c.getText(R.string.account_reg_frag_title);
    }

    @Override
    public int getIconResId() {
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_verifycode:
                onClickGetVerify();
                break;
            case R.id.btn_reg:
                onClickReg();
                break;

            default:
                break;
        }
    }

    private void onClickGetVerify() {

        String phoneNum = mEtPhone.getText().toString().toLowerCase().trim();
        if (!AccountUtils.isLegalPhoneNum(phoneNum)) {
            Toast.makeText(getActivity(), R.string.account_toast_check_phone, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mCountDown = DELAY_VERIFY_CODE;
        mBtnVerifyCode.setClickable(false);
        mEtPhone.setEnabled(false);
        requestVerifyCode(phoneNum);
        countDown();
    }

    private void onClickReg() {
        String phoneNum = mEtPhone.getText().toString().trim();
        if (!AccountUtils.isLegalPhoneNum(phoneNum)) {
            Toast.makeText(getActivity(), R.string.account_register_toast_check_phone,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String passWord = mEtPassword.getText().toString().trim();
        if (!AccountUtils.isLegalPassword(passWord)) {
            Toast.makeText(getActivity(), R.string.account_register_toast_check_password,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String verifyCode = mEtVerifyCode.getText().toString().trim();
        if (TextUtils.isEmpty(verifyCode)) {
            Toast.makeText(getActivity(), R.string.account_register_toast_check_verify_code,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // if (!mChkAgreement.isChecked()) {
        // Toast.makeText(getActivity(), R.string.account_register_toast_check_agreement,
        // Toast.LENGTH_SHORT).show();
        // return;
        // }
        requestPhoneRegister(phoneNum, passWord, verifyCode);
    }

    private void requestVerifyCode(String phoneNum) {
        NetworkCenter.getInstance().putToQueue(
                new RequestVerifyCode(HANDLE_VERIFY_CODE, phoneNum, Type.REG));
    }

    private void requestPhoneRegister(String phoneNum, String passWord, String verifyCode) {
        getHostActivity().showProgressDialog();
        RegInfo info = new RegInfo();
        info.ARGS.putString(RegInfo.ARG_STR_MOBILE, phoneNum);
        info.ARGS.putString(RegInfo.ARG_STR_PASSWORD, passWord);
        info.ARGS.putString(RegInfo.ARG_STR_VERIFYCODE, verifyCode);
        info.ARGS.putInt(RegInfo.ARG_INT_TYPE, RegInfo.TYPE_REG_BY_PHONE);
        NetworkCenter.getInstance().putToQueue(new RequestReg(HANDLE_REG, info));
    }

    private void countDown() {
        if (!isAdded()) {
            mCountDown = 0;
            return;
        }
        if (mCountDown < 1) {
            mBtnVerifyCode.setClickable(true);
            mEtPhone.setEnabled(true);
            mBtnVerifyCode.setText(R.string.account_reg_frag_btn_verifycode);
        } else {
            mBtnVerifyCode.setText(getString(R.string.account_btn_verifycode_enable_delay,
                    mCountDown));
            mCountDown--;
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    countDown();
                }
            }, 1000);
        }
    }
}
