
package tw.supra.data;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * 数据库基类；
 * 
 * @author supra
 */
public abstract class LocalData {

    private static final String LOG_TAG = LocalData.class.getSimpleName();
    public final int VERSION;
    public final Context CONTEXT;
    public final String NAME;
    private final SQLiteOpenHelper DB_HELPER;
    private HashMap<String, String> mPrefStrCache = new HashMap<String, String>();
    private HashMap<String, Integer> mPrefIntCache = new HashMap<String, Integer>();
    private HashMap<String, Long> mPrefLongCache = new HashMap<String, Long>();
    private HashMap<String, Float> mPrefFloatCache = new HashMap<String, Float>();
    private HashMap<String, Boolean> mPrefBoolCache = new HashMap<String, Boolean>();
    private SharedPreferences mPref;

    private SQLiteDatabase mTmpDbOnInitializing;

    private static void checkName(String name) {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("the arg 'name' can not be empty");
        }
    }

    protected LocalData(Context context, String name, int version) {
        checkName(name);
        NAME = name;
        VERSION = version;
        CONTEXT = context;
        DB_HELPER = new SQLiteOpenHelper(CONTEXT, NAME, null, VERSION) {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onConfigure(SQLiteDatabase db) {
                super.onConfigure(db);
                mTmpDbOnInitializing = db;
//                setForeignKeyConstraintsEnabled(db, true);
                onDbConfigure(db);
                mTmpDbOnInitializing = null;
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                mTmpDbOnInitializing = db;
//                setForeignKeyConstraintsEnabled(db, true);
                onDbCreate(db);
                mTmpDbOnInitializing = null;
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                mTmpDbOnInitializing = db;
                onDbUpgrade(db, oldVersion, newVersion);
                mTmpDbOnInitializing = null;
            }

            @Override
            public void onOpen(SQLiteDatabase db) {
                super.onOpen(db);
                mTmpDbOnInitializing = db;
//                if (!db.isReadOnly()) {
//                    // Enable foreign key constraints
//                    db.execSQL(DataDef.ENABLE_DB_FOREIGN_KEY);
//                }
                onDbOpen(db);
                mTmpDbOnInitializing = null;
            }
        };
    }

    public abstract void onDbConfigure(SQLiteDatabase db);

    public abstract void onDbOpen(SQLiteDatabase db);

    public abstract void onDbCreate(SQLiteDatabase db);

    public abstract void onDbUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    
    public void close(){
        DB_HELPER.close();
    }

    /**
     * 在SQLiteOpenHelper初始化期间，返回初始化时的临时数据库，避免初始化时死锁；
     * @return
     */
    public SQLiteDatabase getDb() {
        return mTmpDbOnInitializing == null ? DB_HELPER.getWritableDatabase()
                : mTmpDbOnInitializing;
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db, boolean enable) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledBySql(db, enable);
        } else {
            setForeignKeyConstraintsEnabledByNative(db, enable);
        }
    }

    private void setForeignKeyConstraintsEnabledBySql(SQLiteDatabase db, boolean enable) {
        db.execSQL(DataDef.ENABLE_DB_FOREIGN_KEY);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledByNative(SQLiteDatabase db, boolean enable) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    // =======================================================
    // SharedPreferences
    // =======================================================
    private SharedPreferences getPreferences() {
        if (null == mPref) {
            mPref = CONTEXT.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }
        return mPref;
    }

    public String getPrefStr(String key, String defValue) {

        String value = mPrefStrCache.get(key);
        if (null != value) {
            return value;
        }

        SharedPreferences pref = getPreferences();
        if (pref.contains(key)) {
            value = pref.getString(key, defValue);
            mPrefStrCache.put(key, value);
            return value;
        }
        putPrefStr(key, defValue);
        return defValue;
    }

    public void putPrefStr(String key, String value) {
        mPrefStrCache.put(key, value);
        SharedPreferences pref = getPreferences();
        Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public int getPrefInt(String key, int defValue) {
        Integer value = mPrefIntCache.get(key);
        if (null != value) {
            return value;
        }

        SharedPreferences pref = getPreferences();
        if (pref.contains(key)) {
            value = pref.getInt(key, defValue);
            mPrefIntCache.put(key, value);
            return value;
        }
        putPrefint(key, defValue);
        return defValue;
    }

    public void putPrefint(String key, int value) {
        mPrefIntCache.put(key, value);
        SharedPreferences pref = getPreferences();
        Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public long getPrefLong(String key, long defValue) {

        Long value = mPrefLongCache.get(key);
        if (null != value) {
            return value;
        }

        SharedPreferences pref = getPreferences();
        if (pref.contains(key)) {
            value = pref.getLong(key, defValue);
            mPrefLongCache.put(key, value);
            return value;
        }
        putPrefLong(key, defValue);
        return defValue;
    }

    public void putPrefLong(String key, long value) {
        mPrefLongCache.put(key, value);
        SharedPreferences pref = getPreferences();
        Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public boolean getPrefBool(String key, boolean defValue) {

        Boolean value = mPrefBoolCache.get(key);
        if (null != value) {
            return value;
        }

        SharedPreferences pref = getPreferences();
        if (pref.contains(key)) {
            value = pref.getBoolean(key, defValue);
            mPrefBoolCache.put(key, value);
            return value;
        }
        putPrefBool(key, defValue);
        return defValue;
    }

    public void putPrefBool(String key, boolean value) {
        mPrefBoolCache.put(key, value);
        SharedPreferences pref = getPreferences();
        Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public float getPrefFloat(String key, float defValue) {
        Float value = mPrefFloatCache.get(key);
        if (null != value) {
            return value;
        }

        SharedPreferences pref = getPreferences();
        if (pref.contains(key)) {
            value = pref.getFloat(key, defValue);
            mPrefFloatCache.put(key, value);
            return value;
        }
        putPrefFloat(key, defValue);
        return defValue;
    }

    public void putPrefFloat(String key, float value) {
        mPrefFloatCache.put(key, value);
        SharedPreferences pref = getPreferences();
        Editor editor = pref.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

}
