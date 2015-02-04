package tw.supra.epe.store;

import java.util.ArrayList;

import tw.supra.data.AssetData;
import tw.supra.data.DBUtils;
import tw.supra.epe.App;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AreaItem {
	private static final String LOG_TAG = AreaItem.class.getSimpleName();

	private static final String DB_AERA = "garea.db";
	private static final String TABLE_AERA = "area";
	private static final String AERA_NAME = "name";
	private static final String AERA_ID = "id";

	final int ID;
	final String NAME;
	private String mDesc;

	private ArrayList<AreaItem> mChilds;

	public AreaItem(int id, String name) {
		ID = id;
		NAME = name;
		mDesc = name;
	}

	public void setDesc(String desc) {
		mDesc = desc;
	}

	public String getDesc() {
		return mDesc;
	}

	public ArrayList<AreaItem> getChilds() {
		if (null == mChilds) {
			mChilds = queryChilds();
			Log.i(LOG_TAG, "getChilds : " + mChilds.size());
		}
		return mChilds;
	}

	public static ArrayList<AreaItem> queryAreas() {
		ArrayList<AreaItem> areas = new ArrayList<AreaItem>();
		SQLiteDatabase db = AssetData.getInstance().getDb(App.getInstance(),
				DB_AERA);
		Cursor c = db.query(TABLE_AERA, null, " id%100=0 ", null, null, null,
				null);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			areas.add(createAreaFromCursor(c));
		}
		c.close();
		return areas;
	}

	private ArrayList<AreaItem> queryChilds() {
		ArrayList<AreaItem> areas = new ArrayList<AreaItem>();
		SQLiteDatabase db = AssetData.getInstance().getDb(App.getInstance(),
				DB_AERA);
		Cursor c = db.query(TABLE_AERA, null,
				String.format(" id-%d between 1 and 99  ", ID), null, null,
				null, null);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			AreaItem item = createAreaFromCursor(c);
			item.setDesc(NAME + " " + item.NAME);
			areas.add(item);
		}
		c.close();
		return areas;
	}

	private static AreaItem createAreaFromCursor(Cursor c) {
		String name = DBUtils.getStrByCol(c, AERA_NAME);
		int id = DBUtils.getIntByCol(c, AERA_ID);
		return new AreaItem(id, name);
	}

}
