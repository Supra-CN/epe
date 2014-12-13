
package tw.supra.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import tw.supra.data.ColumnDef.PreferencesCommon;
import tw.supra.data.DataDef.DataCommon;
import tw.supra.epe.App;
import tw.supra.epe.account.Account;
import tw.supra.network.misc.Utils;
import tw.supra.utils.MD5;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * 通用数据库，的数据（例如统计信息）
 * 
 * @author supra
 */
public class CommonData extends LocalData {
    private static final String LOG_TAG = CommonData.class.getSimpleName();
    public static final String TMP_DIR = "tmp";
    public static final String DIR_SOHU_CLUB = "SohuClub";
    public static final String DIR_DOWNLOAD = "download";

    private static CommonData sInstance;

    public static synchronized CommonData getInstance() {
        if (sInstance == null) {
            sInstance = new CommonData();
        }
        return sInstance;
    }

    private CommonData() {
        super(App.getInstance(), DataCommon.NAME, DataCommon.VERSION);
    }

    @Override
    public void onDbConfigure(SQLiteDatabase db) {
    }

    @Override
    public void onDbCreate(SQLiteDatabase db) {
//        db.execSQL(DataCommon.TableEmotion.SQL_CREATE);
//        db.execSQL(DataCommon.TableAccounts.SQL_CREATE);
//        db.execSQL(DataCommon.TableForums.SQL_CREATE);
//        db.execSQL(DataCommon.TableFavForums.SQL_CREATE);
//        db.execSQL(DataCommon.TablePost.SQL_CREATE);
//        db.execSQL(DataCommon.TableDrafts.SQL_CREATE);
//        onInitEmotions(db);
//        onInitForums(db);
    }

    @Override
    public void onDbUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int v = oldVersion; v <= newVersion; v++) {
            switch (v) {
//                case 1:
//                    db.execSQL(DataCommon.SQL_UPGRADE_V1_CREATE_TABLES_EMOTION);
//                    onInitEmotions(db);
//                    db.execSQL(DataCommon.SQL_UPGRADE_V1_CREATE_TABLE_DRAFT);
//                    onUpgradeSaveDrafts();
//                    break;
//                case 2:
//                    db.execSQL(DataCommon.SQL_UPGRADE_V2_CREATE_TABLE_POST);
//                    break;
//                case 3:
//                    db.execSQL(DataCommon.SQL_UPGRADE_V3_CREATE_TABLE_FAV_FORUMS);
//                    db.execSQL(DataCommon.SQL_UPGRADE_V3_CREATE_TABLE_FORUMS);
//                    onInitForums(db);
//                    break;
                // TODO:显示个人信息，数据库v4-v5的升级语句
                default:
                    break;
            }
        }
    }

    @Override
    public void onDbOpen(SQLiteDatabase db) {
    }

    public void putCurrentUserPassport(String passport) {
        putPrefStr(PreferencesCommon.CURRENT_USER, passport);
    }

    public void putTrafficCtrlState(boolean state) {
        putPrefBool(PreferencesCommon.TRAFFIC_CTRL, state);
    }

    public void putLargeTextState(boolean state) {
        putPrefBool(PreferencesCommon.LARGE_TEXT, state);
    }

    public String getGID() {
        return getPrefStringByKey(PreferencesCommon.GID);
    }

    public String getPassportMD5() {
        return getPrefStringByKey(PreferencesCommon.PASSPORT_MD5);
    }

    public String getOsType() {
        return getPrefStringByKey(PreferencesCommon.OS_TYPE);
    }

    public String getModelType() {
        return getPrefStringByKey(PreferencesCommon.MODEL_TYPE);
    }

    public String getAppId() {
        return getPrefStringByKey(PreferencesCommon.APP_ID);
    }

    public String getIMEI() {
        return getPrefStringByKey(PreferencesCommon.IMEI);
    }

    public String getIMSI() {
        return getPrefStringByKey(PreferencesCommon.IMSI);
    }

    public String getMacAddress() {
        return getPrefStringByKey(PreferencesCommon.MAC_ADDRESS);
    }

    public String getUUID() {
        return getPrefStringByKey(PreferencesCommon.UUID);
    }

    public String getCurrentUserPassport() {
        return getPrefStringByKey(PreferencesCommon.CURRENT_USER);
    }

    public boolean getTrafficCtrlStatues() {
        return getPrefBool(PreferencesCommon.TRAFFIC_CTRL, true);
    }

    public boolean getLargeTextStatues() {
        return getPrefBool(PreferencesCommon.LARGE_TEXT, false);
    }

    protected String generateString(String key) {
        if (PreferencesCommon.GID.equals(key)) {
            return generateGID();
//        } else if (PreferencesCommon.PASSPORT_MD5.equals(key)) {
//            return CONTEXT.getString(R.string.passport_md5);
//        } else if (PreferencesCommon.OS_TYPE.equals(key)) {
//            return CONTEXT.getString(R.string.os_type);
//        } else if (PreferencesCommon.APP_ID.equals(key)) {
//            return CONTEXT.getString(R.string.app_id);
//        } else if (PreferencesCommon.MODEL_TYPE.equals(key)) {
//            return CONTEXT.getString(R.string.model_type);
        } else if (PreferencesCommon.IMEI.equals(key)) {
            return generateIMEI();
        } else if (PreferencesCommon.IMSI.equals(key)) {
            return generateIMSI();
        } else if (PreferencesCommon.MAC_ADDRESS.equals(key)) {
            return generateMacAddress();
        } else if (PreferencesCommon.UUID.equals(key)) {
            return UUID.randomUUID().toString();
        } else if (PreferencesCommon.CURRENT_USER.equals(key)) {
            return Account.Type.ANONYMOUS.name();
        } else {
            return "";
        }
    }

    // must have android.permission.READ_PHONE_STATE
    private String generateIMEI() {
        TelephonyManager tm = (TelephonyManager) CONTEXT.getSystemService(
                Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        return TextUtils.isEmpty(imei) ? "" : imei;
    }

    // must have android.permission.READ_PHONE_STATE
    private String generateIMSI() {
        TelephonyManager tm = (TelephonyManager) CONTEXT.getSystemService(
                Context.TELEPHONY_SERVICE);
        String imsi = tm.getSubscriberId();
        return TextUtils.isEmpty(imsi) ? "" : imsi;
    }

    private String generateMacAddress() {
        WifiManager wifi = (WifiManager) CONTEXT.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String result = null;
        if (info != null) {
            String macAddress = info.getMacAddress();
            if (macAddress != null) {
                result = macAddress.replace(":", "");
            }
        }
        return result;// 为了统计那边方便去掉了冒号（:）
    }

    private String generateGID() {
        String osType = getOsType();
        String modelType = getModelType();
        String app_id = getAppId();
        String imei = getIMEI();
        String imsi = getIMSI();
        String mac = getMacAddress();
        String uuid = getUUID();
        String maskImei = (TextUtils.isEmpty(imei) ? "0" : "1");
        String maskImsi = (TextUtils.isEmpty(imsi) ? "0" : "1");
        String maskMac = (TextUtils.isEmpty(mac) ? "0" : "1");
        String maskUuid = (TextUtils.isEmpty(uuid) ? "0" : "1");

        String mask = maskImei + maskImsi + maskMac + maskUuid;

        String md5;
        if (maskImei.equals("0") && maskImsi.equals("0") && maskMac.endsWith("0")) {
            md5 = MD5.Md5(uuid);
        } else {
            md5 = MD5.Md5(imei + imsi + mac);
        }

        String gid = osType + modelType + app_id + mask + md5;
        return gid;
    }

    public String getPrefStringByKey(String key) {
        String v = getPrefStr(key, "");
        if (TextUtils.isEmpty(v)) {
            v = generateString(key);
            putPrefStr(key, v);
        }
        return v;
    }

    public File getPathExternal() {
        if (isExternalMounted()) {
            return Environment.getExternalStorageDirectory();
        }
        return null;
    }

    public File getPathSohuClub() {
        File external = getPathExternal();
        if (null != external) {
            File path = new File(external, DIR_SOHU_CLUB);
            if (!path.isDirectory()) {
                path.delete();
                path.mkdirs();
            }
            return path;
        }
        return null;
    }

    public File getPathDownload() {
        File external = getPathSohuClub();
        if (null != external) {
            File path = new File(external, DIR_DOWNLOAD);
            if (!path.isDirectory()) {
                path.delete();
                path.mkdirs();
            }
            return path;
        }
        return null;
    }

    @SuppressLint("NewApi")
    private static boolean isExternalMounted() {
        if (Utils.hasGingerbread()) {
            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable();
        }
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static final Charset US_ASCII = Charset.forName("US-ASCII");
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    public static String readFully(Reader reader) throws IOException {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[1024];
            int count;
            while ((count = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, count);
            }
            return writer.toString();
        } finally {
            reader.close();
        }
    }

//    private void onInitEmotions(SQLiteDatabase db) {
//        Log.i(LOG_TAG, "onInitEmotions : " + NAME);
//        InputStream in = CONTEXT.getResources().openRawResource(R.raw.emotions);
//        String json = "";
//        CharBuffer buffer = CharBuffer.allocate(20480);
//        StringBuilder sb = new StringBuilder();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//        try {
//            while (reader.read(buffer) != -1) {
//                buffer.flip();
//                sb.append(buffer.toString());
//            }
//            json = sb.toString();
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new IllegalStateException("onInitEmotions abort, cause by raw file exception", e);
//        }
//
//        ArrayList<ClubEmotion> dataArray = null;
//        try {
//            JSONObject jo = new JSONObject(json);
//            JSONArray ja = jo.getJSONArray("face");
//            dataArray = FetchEmotionsRequest.decodeJaFace(ja);
//        } catch (JSONException e) {
//            Log.w(LOG_TAG, "onInitEmotions abort, cause by json decode");
//            e.printStackTrace();
//        }
//
//        if (null != dataArray) {
//            FetchEmotionsRequest.updateDataArrayToDB(dataArray, db);
//        }
//    }

//    private void onInitForums(SQLiteDatabase db) {
//        Log.i(LOG_TAG, "onInitForums : " + NAME);
//        InputStream in = CONTEXT.getResources().openRawResource(R.raw.forums);
//        String json = "";
//        CharBuffer buffer = CharBuffer.allocate(20480);
//        StringBuilder sb = new StringBuilder();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//        try {
//            while (reader.read(buffer) != -1) {
//                buffer.flip();
//                sb.append(buffer.toString());
//            }
//            json = sb.toString();
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new IllegalStateException("onInitForums abort, cause by raw file exception", e);
//        }
//
//        try {
//            JSONObject jo = new JSONObject(json);
//            JSONArray ja = jo.getJSONArray("data");
//            // dataArray = FetchForumsRequest.decodeJaForums(ja,true);
//            FetchForumsRequest.decodeAndFlushJaForums(ja, false);
//        } catch (JSONException e) {
//            Log.w(LOG_TAG, "onInitForums abort, cause by json decode");
//            e.printStackTrace();
//        }
//    }

}
