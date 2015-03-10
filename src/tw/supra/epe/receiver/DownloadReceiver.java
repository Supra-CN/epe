/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tw.supra.epe.receiver;

import java.io.File;

import tw.supra.utils.Log;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

public class DownloadReceiver extends BroadcastReceiver {

	private static final String LOG_TAG = DownloadReceiver.class.getSimpleName();
	private static final String MIME_APK = "application/vnd.android.package-archive";

	public void onReceive(Context context, Intent intent) {
		Log.i(LOG_TAG, "onReceive : " + intent.getAction());

		if (!intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			return;
		}

		Log.i(LOG_TAG, "onReceive : " + intent.getAction());
		DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
		Log.i(LOG_TAG, "id : " + id);
		Uri uri = getUriForDownloadedFile(manager, id);
		String mime = getMimeTypeForDownloadedFile(manager, id);
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		String file = cursor.getString(cursor.getColumnIndex("_data"));
		cursor.close();
		Log.i(LOG_TAG, "mime : " + mime);
		Log.i(LOG_TAG, "file : " + file);

		// 根据文件类型判断是否自动打开
		if (isLegalApk(file,mime)) {
			Intent installIntent = new Intent();
			installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			installIntent.setAction(android.content.Intent.ACTION_VIEW);
			installIntent.setDataAndType(Uri.fromFile(new File(file)), MIME_APK);
			try {
				context.startActivity(installIntent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	private boolean isLegalApk(String file,String mime){
		if(TextUtils.isEmpty(file)){
			return false;
		}
		if(MIME_APK.equals(mime)){
			return true;
		}
		if(file.endsWith(".apk")){
			return true;
		}
		return false;
	}

	/**
	 * Returns {@link Uri} for the given downloaded file id, if the file is
	 * downloaded successfully. otherwise, null is returned.
	 * <p>
	 * If the specified downloaded file is in external storage (for example,
	 * /sdcard dir), then it is assumed to be safe for anyone to read and the
	 * returned {@link Uri} corresponds to the filepath on sdcard.
	 *
	 * @param id
	 *            the id of the downloaded file.
	 * @return the {@link Uri} for the given downloaded file id, if download was
	 *         successful. null otherwise.
	 */
	private Uri getUriForDownloadedFile(DownloadManager dm, long id) {
		// to check if the file is in cache, get its destination from the
		// database
		Query query = new Query().setFilterById(id);
		Cursor cursor = null;
		try {
			cursor = dm.query(query);
			if (cursor == null) {
				return null;
			}
			if (cursor.moveToFirst()) {
				int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
				if (DownloadManager.STATUS_SUCCESSFUL == status) {
					// return public uri
					String uri = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
					return Uri.parse(uri);
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		// downloaded file not found or its status is not 'successfully
		// completed'
		return null;
	}

	/**
	 * Returns {@link Uri} for the given downloaded file id, if the file is
	 * downloaded successfully. otherwise, null is returned.
	 * <p>
	 * If the specified downloaded file is in external storage (for example,
	 * /sdcard dir), then it is assumed to be safe for anyone to read and the
	 * returned {@link Uri} corresponds to the filepath on sdcard.
	 *
	 * @param id
	 *            the id of the downloaded file.
	 * @return the {@link Uri} for the given downloaded file id, if download was
	 *         successful. null otherwise.
	 */
	private String getMimeTypeForDownloadedFile(DownloadManager dm, long id) {
		Query query = new Query().setFilterById(id);
		Cursor cursor = null;
		try {
			cursor = dm.query(query);
			if (cursor == null) {
				return null;
			}
			while (cursor.moveToFirst()) {
				return cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		// downloaded file not found or its status is not 'successfully
		// completed'
		return null;
	}

}
