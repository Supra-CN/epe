package tw.supra.epe.activity.t;

import java.io.File;

import tw.supra.data.DBUtils;
import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.utils.AppUtiles;
import tw.supra.network.NetworkCenter;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.network.request.RequestEvent;
import android.app.Activity;
import android.app.AlertDialog.Builder;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yijiayi.yijiayi.R;

public class TEditorActivity extends BaseActivity implements OnClickListener,
		NetWorkHandler<EpeRequestInfo>, DialogInterface.OnClickListener {

	private static final String LOG_TAG = TEditorActivity.class.getSimpleName();

	public static final String EXTRA_ACTION = "extra_action";

	public static final String ACTION_PICK = "action_pick";
	public static final String ACTION_CAPTURE = "action_capture";

	private Uri mImg;

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_t_editor);
		findViewById(R.id.action_back).setOnClickListener(this);
		findViewById(R.id.action_submit).setOnClickListener(this);
		findViewById(R.id.img).setOnClickListener(this);
		
		String action = getIntent().getStringExtra(EXTRA_ACTION);
		if(ACTION_CAPTURE.equals(action)){
			doTakePhoto();
		}else if (ACTION_PICK.equals(action)) {
			doPickPhotoFromGallery();
		}
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
		default:
			break;
		}
	}

	private void submit() {

		if (mImg == null) {
			Toast.makeText(this, R.string.t_editor_toast_check_img,
					Toast.LENGTH_SHORT).show();
			return;
		}

		String content = ((EditText) findViewById(R.id.content)).getText()
				.toString().trim();
//		if (TextUtils.isEmpty(content)) {
//			Toast.makeText(this, R.string.t_editor_toast_check_content,
//					Toast.LENGTH_SHORT).show();
//			return;
//		}

		String brand = ((EditText) findViewById(R.id.brand)).getText()
				.toString().trim();
//		if (TextUtils.isEmpty(brand)) {
//			Toast.makeText(this, R.string.t_editor_toast_check_brand,
//					Toast.LENGTH_SHORT).show();
//			return;
//		}

		String model = ((EditText) findViewById(R.id.model)).getText()
				.toString().trim();
//		if (TextUtils.isEmpty(brand)) {
//			Toast.makeText(this, R.string.t_editor_toast_check_model,
//					Toast.LENGTH_SHORT).show();
//			return;
//		}

		String price = ((EditText) findViewById(R.id.price)).getText()
				.toString().trim();
//		if (TextUtils.isEmpty(brand)) {
//			Toast.makeText(this, R.string.t_editor_toast_check_price,
//					Toast.LENGTH_SHORT).show();
//			return;
//		}

		showProgressDialog();
		RequestPushT request = new RequestPushT(this, mImg, content, brand,
				model, price);
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
			doPickPhotoFromGallery();
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			doTakePhoto();
			break;
		default:
			break;
		}
	}

	private void doPickPhotoFromGallery() {
		AppUtiles.doPickPhotoFromGallery(this);
	}

	private void doTakePhoto() {
		AppUtiles.doTakePhoto(this);
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
