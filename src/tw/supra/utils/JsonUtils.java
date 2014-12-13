
package tw.supra.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    public static Object getSafely(JSONObject jo, String key) throws JSONException {
        if (jo.has(key) && !jo.isNull(key)) {
            return jo.get(key);
        } else {
            return null;
        }
    }

    public static String getStrSafely(JSONObject jo, String key) throws JSONException {
        return getStrSafely(jo, key, "");
    }

    public static String getStrSafely(JSONObject jo, String key, String def) throws JSONException {
        if (jo.has(key) && !jo.isNull(key)) {
            return jo.getString(key);
        } else {
            return def;
        }
    }

    public static boolean getBoolSafely(JSONObject jo, String key) throws JSONException {
        return getBoolSafely(jo, key, false);
    }

    public static boolean getBoolSafely(JSONObject jo, String key, boolean def)
            throws JSONException {
        if (jo.has(key) && !jo.isNull(key)) {
            return jo.getBoolean(key);
        } else {
            return def;
        }
    }

    public static double getDoubleSafely(JSONObject jo, String key) throws JSONException {
        return getDoubleSafely(jo, key, -1);
    }

    public static double getDoubleSafely(JSONObject jo, String key, double def)
            throws JSONException {
        if (jo.has(key) && !jo.isNull(key)) {
            return jo.getDouble(key);
        } else {
            return def;
        }
    }

    public static int getIntSafely(JSONObject jo, String key) throws JSONException {
        return getIntSafely(jo, key, -1);
    }

    public static int getIntSafely(JSONObject jo, String key, int def) throws JSONException {
        if (jo.has(key) && !jo.isNull(key)) {
            return jo.getInt(key);
        } else {
            return def;
        }
    }

    public static long getLongSafely(JSONObject jo, String key) throws JSONException {
        return getLongSafely(jo, key, -1);
    }

    public static long getLongSafely(JSONObject jo, String key, long def) throws JSONException {
        if (jo.has(key) && !jo.isNull(key)) {
            return jo.getLong(key);
        } else {
            return def;
        }
    }

    public static JSONObject getJoSafely(JSONObject jo, String key) throws JSONException {
        if (jo.has(key) && !jo.isNull(key)) {
            return jo.getJSONObject(key);
        } else {
            return null;
        }
    }

    public static JSONArray getJaSafely(JSONObject jo, String key) throws JSONException {
        if (jo.has(key) && !jo.isNull(key)) {
            return jo.getJSONArray(key);
        } else {
            return null;
        }
    }
}
