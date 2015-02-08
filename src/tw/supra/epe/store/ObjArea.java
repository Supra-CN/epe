package tw.supra.epe.store;

import java.util.ArrayList;

import tw.supra.data.AssetData;
import tw.supra.data.DBUtils;
import tw.supra.epe.App;
import tw.supra.mod.ModelObj;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ObjArea extends ModelObj {
	private static final String LOG_TAG = ObjArea.class.getSimpleName();

	private static final String DB_AERA = "garea.db";
	private static final String TABLE_AERA = "area";
	private static final String AERA_NAME = "name";
	private static final String AERA_ID = "id";

	final int PROVINCE_ID;
	final int CITY_ID;
	final String NAME;
	private String mDesc;

	private ArrayList<ObjArea> mChilds;

	public ObjArea(int id, String name) {
		CITY_ID = id;
		PROVINCE_ID = id - (id % 100);
		NAME = name;
		mDesc = name;
	}

	public void setDesc(String desc) {
		mDesc = desc;
	}

	public String getDesc() {
		return mDesc;
	}

	public ArrayList<ObjArea> getChilds() {
		if (null == mChilds) {
			mChilds = queryChilds();
			Log.i(LOG_TAG, "getChilds : " + mChilds.size());
		}
		return mChilds;
	}

	public static ArrayList<ObjArea> queryAreas() {
		ArrayList<ObjArea> areas = new ArrayList<ObjArea>();
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

	private ArrayList<ObjArea> queryChilds() {
		ArrayList<ObjArea> areas = new ArrayList<ObjArea>();
		SQLiteDatabase db = AssetData.getInstance().getDb(App.getInstance(),
				DB_AERA);
		Cursor c = db.query(TABLE_AERA, null,
				String.format(" id-%d between 1 and 99  ", CITY_ID), null,
				null, null, null);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			ObjArea item = createAreaFromCursor(c);
			item.setDesc(NAME + " " + item.NAME);
			areas.add(item);
		}
		c.close();
		return areas;
	}

	private static ObjArea createAreaFromCursor(Cursor c) {
		String name = DBUtils.getStrByCol(c, AERA_NAME);
		int id = DBUtils.getIntByCol(c, AERA_ID);
		return new ObjArea(id, name);
	}

}
