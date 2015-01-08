package tw.supra.epe.utils;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import tw.supra.epe.App;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.util.TypedValue;
import android.widget.TextView;

public class AppUtiles {

	public static final int REQUEST_CODE_FOR_PICK = 500;
	public static final int REQUEST_CODE_FOR_CAPTURE = 600;
	private static final String DIR_SAVE_TMP_IMAGE = Environment
			.getExternalStorageDirectory().getPath() + "/SohuClub/tmp";

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


	public static String formatPrice(Double price) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		return nf.format(price);
	}

	public static String formatdiscount(Double discount) {
		double discountNum = discount * 10;
		String discountStr = "";
		if (discountNum < 10) {
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(1);
			discountStr = nf.format(discountNum);
		}
		return discountStr;

	}
}
