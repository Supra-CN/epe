package tw.supra.epe.account;

import java.io.File;
import java.util.Calendar;

import tw.supra.data.DBUtils;
import tw.supra.epe.R;
import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.utils.AppUtiles;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.utils.Log;
import tw.supra.utils.TimeUtil;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

public class UserInfoEditorActivity extends BaseActivity implements
		OnClickListener, NetWorkHandler<EpeRequestInfo>,
		DialogInterface.OnClickListener, OnDateSetListener {

	private static final String LOG_TAG = UserInfoEditorActivity.class
			.getSimpleName();

	RoundedImageView mIvAvator;
	Uri mImg;

	Switch mSwGender;
	Calendar mBirthday = Calendar.getInstance();

	EditText mEtNick;
	TextView mTvBirthday;

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info_editor);
		findViewById(R.id.action_back).setOnClickListener(this);
		findViewById(R.id.action_submit).setOnClickListener(this);

		findViewById(R.id.change_avator).setOnClickListener(this);

		mIvAvator = (RoundedImageView) findViewById(R.id.avator);
		mSwGender = (Switch) findViewById(R.id.gender);
		mEtNick = (EditText) findViewById(R.id.nick);
		mTvBirthday = (TextView) findViewById(R.id.birthday);
		mTvBirthday.setOnClickListener(this);

		User user = AccountCenter.getCurrentUser();
		String avatar = user.getAvatarUrl();
		if (!TextUtils.isEmpty(avatar)) {
			Picasso.with(this).load(user.getAvatarUrl())
					.error(R.drawable.ic_epe_logo).into(mIvAvator);
		}
		mSwGender.setChecked(user.getGender().equals(User.GENDER_BOY));
		mEtNick.setText(user.getName());
		mBirthday.setTimeInMillis(user.getBirthday());
		updateBirthday();
	}

	private void updateBirthday() {
		mTvBirthday.setText(TimeUtil.formatDate(this, mBirthday.getTime()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_back:
			onBackPressed();
			break;
		case R.id.action_submit:
			submit();
			break;
		case R.id.change_avator:
			createPickPhotoDialog();
			break;
		case R.id.birthday:
			createPickDateDialog();
			break;
		default:
			break;
		}
	}

	private void submit() {

		String nick = mEtNick.getText().toString().trim();
		if (TextUtils.isEmpty(nick)) {
			Toast.makeText(this, R.string.account_login_toast_illegal_nick,
					Toast.LENGTH_SHORT).show();
			return;
		}

		showProgressDialog();

		Log.i(LOG_TAG, "mSwGender : " + mSwGender.isChecked());
		RequestPushUserInfo request = new RequestPushUserInfo(this, mImg, nick,
				mBirthday, mSwGender.isChecked() ? User.GENDER_BOY
						: User.GENDER_GIRL);
		NetworkCenter.getInstance().putToQueue(request);

	}

	/**
	 * Creates a dialog offering two options: take a photo or pick a photo from
	 * the gallery.
	 */
	public void createPickDateDialog() {
		DatePickerDialog dialog = new DatePickerDialog(this, this,
				mBirthday.get(Calendar.YEAR), mBirthday.get(Calendar.MONTH),
				mBirthday.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	/**
	 * Creates a dialog offering two options: take a photo or pick a photo from
	 * the gallery
	 */
	public void createPickPhotoDialog() {
		Builder builder = new Builder(this);
		builder.setTitle(getString(R.string.img_picker_title));
		builder.setPositiveButton(getString(R.string.img_picker_pos), this);
		builder.setNegativeButton(getString(R.string.img_picker_neg), this);
		builder.create().show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			AppUtiles.doPickPhotoFromGallery(this);
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			AppUtiles.doTakePhoto(this);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case AppUtiles.REQUEST_CODE_FOR_CAPTURE: {
			mImg = AppUtiles.sTmpImageUri;
			Log.i(LOG_TAG, "mImg : " + mImg);
			Picasso.with(this).load(mImg).into(mIvAvator);
		}
			break;
		case AppUtiles.REQUEST_CODE_FOR_PICK: {
			mImg = data.getData();

			Uri uri = data.getData();
			Log.i(LOG_TAG, "uri : " + uri);
			Cursor cursor = getContentResolver().query(uri, null, null, null,
					null);
			cursor.moveToFirst();
			mImg = Uri.fromFile(new File(DBUtils.getStrByCol(cursor,
					MediaStore.Images.ImageColumns.DATA)));
			Log.i(LOG_TAG, "mImg : " + mImg);

			cursor.close();

			Picasso.with(this).load(mImg).into(mIvAvator);
		}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}

	}

	@Override
	public boolean HandleEvent(RequestEvent event, EpeRequestInfo info) {
		switch (event) {
		case FINISH:
			hideProgressDialog();
			if (info.ERROR_CODE.isOK()) {
				setResult(RESULT_OK);
				finish();
			}
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		mBirthday.set(year, monthOfYear, dayOfMonth);
		updateBirthday();
	}

}
