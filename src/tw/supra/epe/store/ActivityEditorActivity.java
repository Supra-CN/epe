package tw.supra.epe.store;

import java.io.File;
import java.util.Calendar;

import tw.supra.data.DBUtils;
import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.utils.AppUtiles;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import tw.supra.utils.TimeUtil;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yijiayi.yijiayi.R;

public class ActivityEditorActivity extends BaseActivity implements
		OnClickListener, NetWorkHandler<EpeRequestInfo>,
		DialogInterface.OnClickListener {

	private static final String LOG_TAG = ActivityEditorActivity.class
			.getSimpleName();
	public static final String EXTRA_MALL_ID ="extra_mall_id";
	private Uri mImg;

	Calendar mStart = Calendar.getInstance();
	Calendar mEnd = Calendar.getInstance();

	TextView mTvStart;
	TextView mTvEnd;
	
	String mMallId;

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMallId = getIntent().getStringExtra(EXTRA_MALL_ID);
		setContentView(R.layout.activity_activity_editor);
		findViewById(R.id.action_back).setOnClickListener(this);
		findViewById(R.id.action_submit).setOnClickListener(this);
		findViewById(R.id.img).setOnClickListener(this);
		mTvStart = (TextView) findViewById(R.id.time_start);
		mTvEnd = (TextView) findViewById(R.id.time_end);
		mTvStart.setOnClickListener(this);
		mTvEnd.setOnClickListener(this);
		updateUiTime();
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
		case R.id.img:
			createPickPhotoDialog();
			break;
		case R.id.time_start:
			createPickStartDateDialog();
			break;
		case R.id.time_end:
			createPickEndDateDialog();
			break;
		default:
			break;
		}
	}

	private void updateUiTime() {
		mTvStart.setText(TimeUtil.formatDateWithMM(this, mStart.getTime()));
		mTvEnd.setText(TimeUtil.formatDateWithMM(this, mEnd.getTime()));
	}

	public void createPickStartDateDialog() {

		DatePickerDialog dialog = new DatePickerDialog(this,
				new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						mStart.set(year, monthOfYear, dayOfMonth);
						updateUiTime();
						createPickStartTimeDialog();
					}
				}, mStart.get(Calendar.YEAR), mStart.get(Calendar.MONTH),
				mStart.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	public void createPickStartTimeDialog() {
		TimePickerDialog dialog = new TimePickerDialog(this,
				new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						mStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
						mStart.set(Calendar.MINUTE, minute);
						updateUiTime();
					}
				}, mStart.get(Calendar.HOUR_OF_DAY),
				mStart.get(Calendar.MINUTE), true);
		dialog.show();
	}

	public void createPickEndDateDialog() {
		DatePickerDialog dialog = new DatePickerDialog(this,
				new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						mEnd.set(year, monthOfYear, dayOfMonth);
						updateUiTime();
						createPickEndTimeDialog();
					}
				}, mEnd.get(Calendar.YEAR), mEnd.get(Calendar.MONTH),
				mEnd.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	public void createPickEndTimeDialog() {
		TimePickerDialog dialog = new TimePickerDialog(this,
				new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						mEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
						mEnd.set(Calendar.MINUTE, minute);
						updateUiTime();
					}
				}, mEnd.get(Calendar.HOUR_OF_DAY), mEnd.get(Calendar.MINUTE),
				true);
		dialog.show();
	}

	private void submit() {

		if (mImg == null) {
			Toast.makeText(this, R.string.t_editor_toast_check_img,
					Toast.LENGTH_SHORT).show();
			return;
		}

		String title = ((EditText) findViewById(R.id.title)).getText()
				.toString().trim();
		if (TextUtils.isEmpty(title)) {
			Toast.makeText(this, R.string.activity_editor_toast_check_title,
					Toast.LENGTH_SHORT).show();
			return;
		}
		String content = ((EditText) findViewById(R.id.content)).getText()
				.toString().trim();
		if (TextUtils.isEmpty(content)) {
			Toast.makeText(this, R.string.t_editor_toast_check_content,
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (mStart.after(mEnd)) {
			Toast.makeText(this, R.string.activity_editor_toast_check_time,
					Toast.LENGTH_SHORT).show();
			return;
		}

		showProgressDialog();
		RequestPushActivity request = new RequestPushActivity(this, mImg,
				mMallId, title, content, mStart, mEnd);
		NetworkCenter.getInstance().putToQueue(request);
	}

	/**
	 * Creates a dialog offering two options: take a photo or pick a photo from
	 * the gallery.
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
		ImageView iv = (ImageView) findViewById(R.id.img);
		switch (requestCode) {
		case AppUtiles.REQUEST_CODE_FOR_CAPTURE: {
			mImg = AppUtiles.sTmpImageUri;
			Log.i(LOG_TAG, "mImg : " + mImg);
			Picasso.with(this).load(mImg).into(iv);
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

			Picasso.with(this).load(mImg).into(iv);
		}
			break;
		// case AppUtiles.REQUEST_CODE_FOR_PICK_VIDEO: {
		// Uri uri = data.getData();
		// ContentResolver cr = mActivity.getContentResolver();
		// Cursor cursor = cr.query(uri, null, null, null, null);
		// cursor.moveToFirst();
		//
		// Log.i(LOG_TAG, DBUtils.getStrByCol(cursor,
		// MediaStore.Video.VideoColumns.MIME_TYPE));
		//
		// mDraft.setVideo(DBUtils.getStrByCol(cursor,
		// MediaStore.Video.VideoColumns.DATA));
		// cursor.close();
		// updateUiVideo();
		// }
		// break;
		// case AppUtiles.REQUEST_VIDEO_FOR_CAPTURE: {
		// Log.v(ThreadEditorActivity.class.getSimpleN ame(),
		// "AppUtiles.REQUEST_VIDEO_FOR_CAPTURE");
		// // Uri uri=data.getData();
		// // ContentResolver cr=mActivity.getContentResolver();
		// // Cursor cursor=cr.query(uri, null, null, null, null);
		// // int
		// // index=cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
		// // cursor.moveToFirst();
		// // String path=cursor.getString(index);
		// mDraft.setVideo(data.getExtras().getString(
		// VideoRecorderActivity.VIDEO_PATH_NAME));
		// updateUiVideo();
		// }
		// break;
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
}
