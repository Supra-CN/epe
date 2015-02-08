package tw.supra.epe.store;

import tw.supra.epe.R;
import tw.supra.epe.account.AccountUtils;
import tw.supra.epe.account.RequestVerifyCode;
import tw.supra.epe.account.RequestVerifyCode.Type;
import tw.supra.epe.core.BaseHostFrag;
import tw.supra.epe.store.AreaPickerDialog.OnAreaPickedListener;
import tw.supra.location.MapPickerActivity;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;

public class ApplyStorePage extends BaseHostFrag<ApplyStoreActivity> implements
		OnAreaPickedListener, OnClickListener, NetWorkHandler<EpeRequestInfo> {

	private static final String LOG_TAG = ApplyStorePage.class.getSimpleName();

	private static final int REQUESTCODE_PICK_MAP = 100;
	private static final int DELAY_VERIFY_CODE = 60;// 定义发送验证码后的倒数计时

	private TextView mTvMap;
	private TextView mTvArea;
	private EditText mEtName;
	private EditText mEtAdd;
	private EditText mEtPhone;
	private EditText mEtVerifyCode;
	private Button mBtnVerifyCode;

	private ObjArea mArea;

	private LatLng mLatLng;

	private int mCountDown = 0;// 发送验证码后的倒数计时

	private String mVerifyCode;
	private static Handler sHandler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.page_apply_boutiques_store,
				container, false);
		mTvArea = (TextView) v.findViewById(R.id.area);
		mTvArea.setOnClickListener(this);
		mTvMap = (TextView) v.findViewById(R.id.map);
		mTvMap.setOnClickListener(this);

		mBtnVerifyCode = (Button) v.findViewById(R.id.btn_verifycode);
		mEtVerifyCode = (EditText) v.findViewById(R.id.et_verifycode);
		mEtPhone = (EditText) v.findViewById(R.id.et_phone);
		mEtName = (EditText) v.findViewById(R.id.et_name);
		mEtAdd = (EditText) v.findViewById(R.id.et_add);

		mBtnVerifyCode.setOnClickListener(this);
		v.findViewById(R.id.btn_submit).setOnClickListener(this);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return c.getText(R.string.store_apply_boutiques_page_title);
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
		case R.id.btn_submit:
			onClickSubmit();
			break;
		case R.id.area:
			showAreaDialog();
			break;
		case R.id.map:
			showMapPicker();
			break;

		default:
			break;
		}
	}

	private AreaPickerDialog mAreaPicker;

	private void showAreaDialog() {
		if (mAreaPicker == null) {
			mAreaPicker = new AreaPickerDialog(getActivity(), this);
		}
		mAreaPicker.show();
	}

	private void showMapPicker() {
		Intent intent = new Intent(getActivity(), MapPickerActivity.class);
		startActivityForResult(intent, REQUESTCODE_PICK_MAP);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK
				&& requestCode == REQUESTCODE_PICK_MAP) {
			mTvMap.setText(data
					.getStringExtra(MapPickerActivity.RESULT_STR_DESC));

			mLatLng = new LatLng(data.getDoubleExtra(
					MapPickerActivity.RESULT_DOUBLE_LAT, 0),
					data.getDoubleExtra(MapPickerActivity.RESULT_DOUBLE_LON, 0));

		}
	}

	private void onClickSubmit() {
		if (mArea == null) {
			Toast.makeText(getActivity(),
					R.string.store_apply_toast_check_area, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		String name = mEtName.getText().toString().trim();
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(getActivity(),
					R.string.store_apply_toast_check_name, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		if (mLatLng == null) {
			Toast.makeText(getActivity(),
					R.string.store_apply_toast_check_latlng, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		String address = mEtAdd.getText().toString().trim();
		if (TextUtils.isEmpty(address)) {
			Toast.makeText(getActivity(),
					R.string.store_apply_toast_check_address,
					Toast.LENGTH_SHORT).show();
			return;
		}

		String phone = mEtPhone.getText().toString().trim();
		if (!AccountUtils.isLegalPassport(phone)) {
			Toast.makeText(getActivity(),
					R.string.account_login_toast_illegal_passport,
					Toast.LENGTH_SHORT).show();
			return;
		}

		String verifyCode = mEtVerifyCode.getText().toString().trim();
		if (TextUtils.isEmpty(verifyCode)) {
			Toast.makeText(getActivity(),
					R.string.account_register_toast_check_verify_code,
					Toast.LENGTH_SHORT).show();
			return;
		}

		getHostActivity().showProgressDialog();
		NetworkCenter.getInstance().putToQueue(
				new RequestApplyBoutiques(this, verifyCode, phone, mArea, name,
						mLatLng, address));
	}

	@Override
	public boolean HandleEvent(RequestEvent event, EpeRequestInfo info) {
		switch (event) {
		case FINISH:
			getHostActivity().hideProgressDialog();
			if (info.ERROR_CODE.isOK()) {
				getHostActivity().notifyOkToFinish();
			} else {
				Toast.makeText(
						getActivity(),
						getString(R.string.store_apply_toast_apply_error,
								info.ERROR_CODE.getDescription()),
						Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	public void onAreaPicked(ObjArea area) {
		mArea = area;
		mTvArea.setText(mArea.NAME);
	}

	private void onClickGetVerify() {

		String phoneNum = mEtPhone.getText().toString().toLowerCase().trim();
		if (!AccountUtils.isLegalPhoneNum(phoneNum)) {
			Toast.makeText(getActivity(), R.string.account_toast_check_phone,
					Toast.LENGTH_SHORT).show();
			return;
		}
		mCountDown = DELAY_VERIFY_CODE;
		mBtnVerifyCode.setClickable(false);
		mEtPhone.setEnabled(false);
		requestVerifyCode(phoneNum);
		countDown();
	}

	private void requestVerifyCode(String phoneNum) {
		NetworkCenter.getInstance().putToQueue(
				new RequestVerifyCode(HANDLE_VERIFY_CODE, phoneNum, Type.REG));
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
			mBtnVerifyCode.setText(getString(
					R.string.account_btn_verifycode_enable_delay, mCountDown));
			mCountDown--;
			sHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					countDown();
				}
			}, 1000);
		}
	}

	private final NetWorkHandler<EpeRequestInfo> HANDLE_VERIFY_CODE = new NetWorkHandler<EpeRequestInfo>() {
		@Override
		public boolean HandleEvent(RequestEvent event, EpeRequestInfo info) {
			switch (event) {
			case FINISH:
				if (info.ERROR_CODE.isOK()) {
					mVerifyCode = info.RESULTS
							.getString(RequestVerifyCode.RESULT_CODE);
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

}
