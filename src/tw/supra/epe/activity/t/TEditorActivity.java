package tw.supra.epe.activity.t;

import tw.supra.epe.App;
import tw.supra.epe.R;
import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.utils.AppUtiles;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class TEditorActivity extends BaseActivity implements OnClickListener,
		DialogInterface.OnClickListener {

	private static final String LOG_TAG = TEditorActivity.class.getSimpleName();

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_t_editor);
		findViewById(R.id.action_back).setOnClickListener(this);
		findViewById(R.id.img).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_back:
			onBackPressed();
			break;
		case R.id.logout:
			startActivity(new Intent(App.ACTION_LOGIN));
			finish();
			break;
		case R.id.img:
			createPickPhotoDialog();
			break;
		default:
			break;
		}
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
			AppUtiles.doTakePhoto(this);

			break;
		case DialogInterface.BUTTON_NEGATIVE:
			AppUtiles.doPickPhotoFromGallery(this);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

}
