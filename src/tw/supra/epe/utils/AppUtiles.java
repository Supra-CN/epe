package tw.supra.epe.utils;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import tw.supra.epe.App;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import com.yijiayi.yijiayi.R;

public class AppUtiles {

	public static final int REQUEST_CODE_FOR_PICK = 500;
	public static final int REQUEST_CODE_FOR_CAPTURE = 600;
	public static final int REQUEST_CODE_FOR_PICK_VIDEO = 700;
	public static final int REQUEST_VIDEO_FOR_CAPTURE = 800;
	private static final String DIR_SAVE_TMP_IMAGE = Environment
			.getExternalStorageDirectory().getPath() + "/epe/tmp";
	public static Uri sTmpImageUri;

	private static final float TEXT_LARGE_SIZE_SCALE = 1.2f;

	public static void scaleToLargeText(TextView tv) {
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv.getTextSize() * 1.2f);
	}

	public static int getVersionCode() {
		Context context = App.getInstance();
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			// should not happen;
			e.printStackTrace();
			return 0;
		}
	}

	public static String getVersionName() {
		Context context = App.getInstance();
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// should not happen;
			e.printStackTrace();
			return "";
		}
	}

	private static Uri newTmpImageUri() {
		File dir = new File(DIR_SAVE_TMP_IMAGE);
		if (dir.isFile()) {
			dir.delete();
		}
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		mediaFile = new File(dir.getPath() + File.separator + "IMG_"
				+ timeStamp + ".png");
		Uri tmpImageUri = Uri.fromFile(mediaFile);
		return tmpImageUri;
	}

	public static int doTakePhoto(Activity activity) {
		sTmpImageUri = newTmpImageUri();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, sTmpImageUri);
		try {
			activity.startActivityForResult(intent, REQUEST_CODE_FOR_CAPTURE);
			return REQUEST_CODE_FOR_CAPTURE;
		} catch (ActivityNotFoundException e) {
			Toast.makeText(activity,
					R.string.img_picker_toast_can_not_find_camera,
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return -1;
		}
	}

	public static int doPickPhotoFromGallery(Activity activity) {
		// Intent intent = new Intent();
		// intent.setAction(IntentAction.ACTION_MULTIPLE_PICK);
		// activity.startActivityForResult(intent, REQUEST_CODE_FOR_PICK);
		// return REQUEST_CODE_FOR_PICK;

		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
		try {
			activity.startActivityForResult(intent, REQUEST_CODE_FOR_PICK);
			return REQUEST_CODE_FOR_PICK;
		} catch (ActivityNotFoundException e) {
			Toast.makeText(activity,
					R.string.img_picker_toast_can_not_find_gallery,
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 从相册选取视频
	 */
	public static int doPickVideoFromGallery(Activity activity) {
		Intent pickVideoIntent = new Intent(Intent.ACTION_PICK);
		pickVideoIntent.setType(MediaStore.Video.Media.CONTENT_TYPE);
		activity.startActivityForResult(pickVideoIntent,
				REQUEST_CODE_FOR_PICK_VIDEO);
		return REQUEST_CODE_FOR_PICK_VIDEO;
	}

	/**
	 * 拍摄视频
	 */
	public static void doShootVideo(Activity activity) {
		// Intent intent=new Intent(activity,VideoRecorderActivity.class);
		// activity.startActivityForResult(intent,REQUEST_VIDEO_FOR_CAPTURE);
	}

	public static String formatPrice(Double price) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		return nf.format(price);
	}

	public static String formatDiscount(Double discount) {
		double discountNum = discount * 10;
		String discountStr = "";
		if (discountNum < 10) {
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(1);
			discountStr = nf.format(discountNum);
		}
		return discountStr;

	}

	public static String formatDistance(int distance) {
		if (distance < 1000) {
			return distance + "m";
		}else{
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(2);
			return  nf.format(distance/1000f )+"km";
		}
	}
}
