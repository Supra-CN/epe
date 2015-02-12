package tw.supra.epe.activity.t;

import com.sohu.club.imagepicker.IntentAction;

import tw.supra.epe.App;
import tw.supra.epe.R;
import tw.supra.epe.core.BaseActivity;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

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
//			doPickPhotoFromGallery();
			break;
		case DialogInterface.BUTTON_NEGATIVE:
//			doTakePhoto();
			break;
		default:
			break;
		}
	}
	
    public static int doTakePhoto(Activity activity) {
        sTmpImageUri = newTmpImageUri();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, sTmpImageUri);
        try {
            activity.startActivityForResult(intent, REQUEST_CODE_FOR_CAPTURE);
            return REQUEST_CODE_FOR_CAPTURE;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, R.string.user_info_toast_can_not_find_camera,
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return -1;
        }
    }

    public static int doPickPhotoFromGallery(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(IntentAction.ACTION_MULTIPLE_PICK);
        activity.startActivityForResult(intent, REQUEST_CODE_FOR_PICK);
        return REQUEST_CODE_FOR_PICK;

        // Intent intent = new Intent(Intent.ACTION_PICK);
        // intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        // try {
        // startActivityForResult(intent, REQUEST_CODE_FOR_PICK);
        // } catch (ActivityNotFoundException e) {
        // Toast.makeText(this, R.string.user_info_toast_can_not_find_gallery,
        // Toast.LENGTH_SHORT).show();
        // e.printStackTrace();
        // }
    }

}
