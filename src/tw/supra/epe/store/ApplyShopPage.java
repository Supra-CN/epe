package tw.supra.epe.store;

import tw.supra.epe.account.AccountUtils;
import tw.supra.epe.account.RequestVerifyCode;
import tw.supra.epe.account.RequestVerifyCode.Type;
import tw.supra.epe.core.BaseHostFrag;
import tw.supra.epe.store.AreaPickerDialog.OnAreaPickedListener;
import tw.supra.epe.store.MallPickerDialog.OnMallPickedListener;
import tw.supra.epe.store.ShopPickerDialog.OnShopPickedListener;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.yijiayi.yijiayi.R;

public class ApplyShopPage extends BaseHostFrag<ApplyStoreActivity> implements
		NetWorkHandler<EpeRequestInfo>, OnAreaPickedListener,
		OnMallPickedListener, OnShopPickedListener, OnClickListener {
	private static final int DELAY_VERIFY_CODE = 60;// 定义发送验证码后的倒数计时

	private ObjArea mArea;
	private ObjMall mMall;
	private ObjShop mMallStore;
	private TextView mTvArea;
	private TextView mTvMall;
	private TextView mTvMallStore;
	private EditText mEtDoorNo;
	private EditText mEtPhone;
	private EditText mEtVerifyCode;
	private Button mBtnVerifyCode;

	private int mCountDown = 0;// 发送验证码后的倒数计时
	private String mVerifyCode;

	private static Handler sHandler = new Handler();

	private final NetWorkHandler<EpeRequestInfo> HANDLE_VERIFY_CODE = new NetWorkHandler<EpeRequestInfo>() {
		@Override
		public boolean HandleEvent(RequestEvent event, EpeRequestInfo info) {
			switch (event) {
			case FINISH:
				if (info.ERROR_CODE.isOK()) {
					mVerifyCode = info.RESULTS
							.getString(RequestVerifyCode.RESULT_CODE);
					// TODO:清理测试代码
//					mEtVerifyCode.setText(mVerifyCode);
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.page_apply_mall_store, container,
				false);
		// mEtPhone = (EditText) v.findViewById(R.id.et_phone);
		// mEtVerifyCode = (EditText) v.findViewById(R.id.et_verifycode);
		// mEtPassword = (EditText) v.findViewById(R.id.et_password);
		// mBtnVerifyCode = (Button) v.findViewById(R.id.btn_verifycode);
		// mBtnReg = (Button) v.findViewById(R.id.btn_reg);

		mTvArea = (TextView) v.findViewById(R.id.area);
		mTvArea.setOnClickListener(this);
		mTvMall = (TextView) v.findViewById(R.id.mall);
		mTvMall.setOnClickListener(this);
		mTvMallStore = (TextView) v.findViewById(R.id.mall_store);
		mTvMallStore.setOnClickListener(this);
		mEtDoorNo = (EditText) v.findViewById(R.id.et_door_no);
		mBtnVerifyCode = (Button) v.findViewById(R.id.btn_verifycode);
		mEtVerifyCode = (EditText) v.findViewById(R.id.et_verifycode);
		mEtPhone = (EditText) v.findViewById(R.id.et_phone);
		mBtnVerifyCode.setOnClickListener(this);
		v.findViewById(R.id.btn_submit).setOnClickListener(this);

		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// mBtnVerifyCode.setOnClickListener(this);
		// mBtnReg.setOnClickListener(this);
	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return c.getText(R.string.apply_mall_store_page_title);
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
		case R.id.area:
			showAreaDialog();
			break;
		case R.id.mall:
			showMallDialog();
			break;
		case R.id.mall_store:
			showFloorDialog();
			break;
		case R.id.btn_submit:
			onClickSubmit();
			break;

		default:
			break;
		}
	}

	private void onClickSubmit() {
		if (mTvMallStore == null) {
			Toast.makeText(getActivity(),
					R.string.store_apply_toast_check_mall_store,
					Toast.LENGTH_SHORT).show();
			return;
		}

		String doorNo = mEtDoorNo.getText().toString().trim();
		if (TextUtils.isEmpty(doorNo)) {
			Toast.makeText(getActivity(),
					R.string.store_apply_toast_check_doorno, Toast.LENGTH_SHORT)
					.show();
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
				new RequestApplyShop(this, verifyCode, phone, 
						mMallStore, doorNo));
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

	private void onClickReg() {
		String phoneNum = mEtPhone.getText().toString().trim();
		if (!AccountUtils.isLegalPhoneNum(phoneNum)) {
			Toast.makeText(getActivity(),
					R.string.account_register_toast_check_phone,
					Toast.LENGTH_SHORT).show();
			return;
		}
		// String passWord = mEtPassword.getText().toString().trim();
		// if (!AccountUtils.isLegalPassword(passWord)) {
		// Toast.makeText(getActivity(),
		// R.string.account_register_toast_check_password,
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		String verifyCode = mEtVerifyCode.getText().toString().trim();
		if (TextUtils.isEmpty(verifyCode)) {
			Toast.makeText(getActivity(),
					R.string.account_register_toast_check_verify_code,
					Toast.LENGTH_SHORT).show();
			return;
		}
		// if (!mChkAgreement.isChecked()) {
		// Toast.makeText(getActivity(),
		// R.string.account_register_toast_check_agreement,
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		// requestPhoneRegister(phoneNum, passWord, verifyCode);
	}

	private void requestVerifyCode(String phoneNum) {
		NetworkCenter.getInstance().putToQueue(
				new RequestVerifyCode(HANDLE_VERIFY_CODE, phoneNum, Type.APPLY_STORE));
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

	private AreaPickerDialog mAreaPicker;

	private boolean checkArea() {
		if (mArea == null) {
			Toast.makeText(getActivity(),
					R.string.store_apply_toast_check_area, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	private boolean checkMall() {
		if (mMall == null) {
			Toast.makeText(getActivity(),
					R.string.store_apply_toast_check_mall, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	private void showAreaDialog() {
		if (mAreaPicker == null) {
			mAreaPicker = new AreaPickerDialog(getActivity(), this);
		}
		mAreaPicker.show();
	}

	private void showMallDialog() {
		if (checkArea()) {
			new MallPickerDialog(getActivity(), this, mArea).show();
		}
	}

	private void showFloorDialog() {
		if (checkMall()) {
			new ShopPickerDialog(getActivity(), this, mMall).show();
		}
	}

	@Override
	public void onAreaPicked(ObjArea area) {
		if (mArea != null && mArea.CITY_ID == area.CITY_ID) {
			return;
		}
		clearArea();
		mArea = area;
		mTvArea.setText(mArea.NAME);
	}

	@Override
	public void onMallPicked(ObjMall mall) {
		if (mMall != null && mMall.mallId.equals(mall.mallId)) {
			return;
		}
		clearMall();
		mMall = mall;
		mTvMall.setText(mMall.name);
	}

	@Override
	public void OnShopPicked(ObjShop mallStore) {
		mMallStore = mallStore;
		mTvMallStore.setText(mMallStore.desc);
	}

	private void clearArea() {
		mArea = null;
		mTvArea.setText("");
		clearMall();
	}

	private void clearMall() {
		mMall = null;
		mTvMall.setText("");
		clearFloor();
	}

	private void clearFloor() {
		mMallStore = null;
		mTvMallStore.setText("");
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

}
