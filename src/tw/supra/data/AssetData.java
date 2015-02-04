package tw.supra.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * 数据库基类；
 * 
 * @author supra
 */
public class AssetData {

	private static final String LOG_TAG = AssetData.class.getSimpleName();

	private final HashMap<String, SQLiteDatabase> MAP = new HashMap<String, SQLiteDatabase>();

	private static AssetData sInstance;

	private static void checkName(String name) {
		if (TextUtils.isEmpty(name)) {
			throw new IllegalArgumentException(
					"the arg 'name' can not be empty");
		}
	}

	private AssetData() {
	}

	public static AssetData getInstance() {
		if (sInstance == null) {
			sInstance = new AssetData();
		}
		return sInstance;
	}

	public synchronized SQLiteDatabase getDb(Context c, String assetFile) {
		checkName(assetFile);

		SQLiteDatabase db = MAP.get(assetFile);
		if (db == null) {

			File file = c.getFileStreamPath(assetFile);

			if (!file.isFile()) {
				try {
					copyDataBase(c, assetFile, file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null,
					SQLiteDatabase.OPEN_READONLY);
			MAP.put(assetFile, db);
		}
		return db;
	}

	private static void copyDataBase(Context c, String assetFile, File toFile)
			throws IOException {

		// Open your local db as the input stream
		InputStream myInput = c.getAssets().open(assetFile);

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(toFile);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

}
