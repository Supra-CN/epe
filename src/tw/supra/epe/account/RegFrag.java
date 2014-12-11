
package tw.supra.epe.account;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseFrag;

public class RegFrag extends BaseFrag implements OnClickListener{
    private static final int DELAY_VERIFY_CODE = 60;// 定义发送验证码后的倒数计时
    
    private Button mBtnVerifyCode;
    private EditText mEtPhone;
    
    private int mCountDown = 0;// 发送验证码后的倒数计时
    
    private static Handler sHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reg, container, false);
        mEtPhone = (EditText)v.findViewById(R.id.et_phone);
        mBtnVerifyCode = (Button)v.findViewById(R.id.btn_verifycode);
        return v;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnVerifyCode.setOnClickListener(this);
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

            default:
                break;
        }
    }
    
    private void onClickGetVerify() {

        String phoneNum = mEtPhone.getText().toString().toLowerCase().trim();
        if (!AccountUtils.isLegalPhoneNum(phoneNum)) {
            Toast.makeText(getActivity(), R.string.account_toast_check_phone, Toast.LENGTH_SHORT).show();
            return;
        }
        mCountDown = DELAY_VERIFY_CODE;
        mBtnVerifyCode.setClickable(false);
        mEtPhone.setEnabled(false);
        requestVerifyCode(phoneNum);
        countDown();
    }
    
    private void requestVerifyCode(String phoneNum) {
//        final NetWorkHandler<BaseRequestInfo> handler = new NetWorkHandler<BaseRequestInfo>() {
//
//            @Override
//            public boolean HandleEvent(RequestEvent event, BaseRequestInfo info) {
//                switch (event) {
//                case FINISH:
//                    if (info.ERROR_CODE.isOK()) {
//                        Toast.makeText(getActivity(), R.string.register_toast_wait_for_verify_code, Toast.LENGTH_SHORT)
//                                .show();
//                    } else {
//                        mCountDown = 0;
//                    }
//                    break;
//
//                default:
//                    break;
//                }
//                return true;
//            }
//        };
//        VerifyCodeRequest request = new VerifyCodeRequest(handler, phoneNum);
//        NetworkCenter.getInstance().putToQueue(request);
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
            mBtnVerifyCode.setText(getString(R.string.account_btn_verifycode_enable_delay, mCountDown));
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
