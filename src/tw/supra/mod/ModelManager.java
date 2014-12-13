
package tw.supra.mod;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.net.Uri;

import tw.supra.epe.App;
import tw.supra.epe.BuildConfig;
import tw.supra.epe.UriDef;
import tw.supra.utils.ListUtil;
import tw.supra.utils.Log;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 负责管理不同类型的本地数据
 * 
 * @author supra
 */
public class ModelManager implements ComponentCallbacks2 {
    private static final String LOG_TAG = ModelManager.class.getSimpleName();
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private static final long TRIM_FREE_MEM_THRESHOLD = 256 * 1024;
    private static final long TRIM_TOTAL_MEM_THRESHOLD = RUNTIME.maxMemory() / 4;
    private static ModelManager sInstance;
    private final ConcurrentHashMap<String, SoftReference<? extends ModelObj>> POOL = new ConcurrentHashMap<String, SoftReference<? extends ModelObj>>();
    private final ConcurrentLinkedQueue<ModelObj> KEEPER = new ConcurrentLinkedQueue<ModelObj>();
    private long mTotalMem;

    public static ModelManager getInstance() {
        if (null == sInstance) {
            sInstance = new ModelManager();
        }
        return sInstance;
    }

    private ModelManager() {
        App.getInstance().registerComponentCallbacks(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    }

    @Override
    public void onLowMemory() {
        Log.i(LOG_TAG, "onLowMemory");
        trim(50);
    }

    @Override
    public void onTrimMemory(int level) {
        Log.i(LOG_TAG, "onTrimMemory");
        trim(level);
    }

    public void trimIfNecessary() {
        long free = RUNTIME.freeMemory();
        long total = RUNTIME.totalMemory();
        Log.i(LOG_TAG,
                String.format("MEM : size(free/mTotal/total) = %d/%d/%d", free, mTotalMem, total));
        if ((total > TRIM_TOTAL_MEM_THRESHOLD)
                && (free < TRIM_FREE_MEM_THRESHOLD || mTotalMem < total)) {
            trim(20);
        }
    }

    public synchronized void trim(int level) {
        Log.i(LOG_TAG, "=== start trim ===");
        dumpPoolStatus();
        KEEPER.clear();
        Log.i(LOG_TAG, "clear");
        dumpPoolStatus();
        Runtime.getRuntime().gc();
        Log.i(LOG_TAG, "gc");
        dumpPoolStatus();
        for (Entry<String, SoftReference<? extends ModelObj>> entry : POOL.entrySet()) {
            SoftReference<? extends ModelObj> reference = entry.getValue();
            if (null == reference || null == reference.get()) {
                POOL.remove(entry.getKey());
            }
        }
        Log.i(LOG_TAG, "sort");
        dumpPoolStatus();
        mTotalMem = RUNTIME.totalMemory();
        Log.i(LOG_TAG, "=== end trim ===");
    }

    public synchronized <T extends ModelObj> T getObj(ClubObjIdentifier<T> identifier) {

        if (null == identifier || !identifier.isValid()) {
            return null;
        }
        Log.i(LOG_TAG, "[getObj] = " + identifier.getAuthenticatorStr());
        SoftReference<? extends ModelObj> obj = POOL.get(identifier.getAuthenticatorStr());
        // Object obj = POOL.get(identifier);
        T t;
        if (null != obj) {
            t = (T) obj.get();
            if (null != t) {
                Log.i(LOG_TAG, "hit ! ");
                KEEPER.remove(t);
                KEEPER.add(t);
                return t;
            }
        }
        Log.i(LOG_TAG, "");
        Log.i(LOG_TAG, "=================================");
        Log.i(LOG_TAG, "========== before build ===========");
        trimIfNecessary();
        t = buildObj(identifier);
        // POOL.put(identifier, t);
        KEEPER.remove(t);
        KEEPER.add(t);
        POOL.put(identifier.getAuthenticatorStr(), new SoftReference<T>(t));
        Log.i(LOG_TAG, "========== after build =============");
        Log.i(LOG_TAG, "==================================\n");
        Log.i(LOG_TAG, "");
        return t;
    }

    private <T extends ModelObj> T buildObj(ClubObjIdentifier<T> identifier) {
        dumpPoolStatus();
        Log.i(LOG_TAG, "[buildObj] = " + identifier.getAuthenticatorStr());
        Uri uri = identifier.getAuthenticator();
        if (!UriDef.SCHEME_EPE.equalsIgnoreCase(uri.getScheme())
                || !UriDef.HOST_SOHU_CLUB.equalsIgnoreCase(uri.getAuthority())) {
            return null;
        }

        List<String> path = uri.getPathSegments();

        if (ListUtil.isEmpty(path)) {
            return null;
        }

        // String segmentL0 = ListUtil.getSafely(path, 0);
        // if (UriDef.SEGMENT_POST.equals(segmentL0)) {
        //
        // }
        return identifier.build();
    }

    public void dumpPoolStatus() {
        if (!BuildConfig.DEBUG) {
            return;
        }
        int valid = 0;
        for (SoftReference<?> reference : POOL.values()) {
            if (null != reference && null != reference.get()) {
                valid++;
            }
        }
        Runtime r = Runtime.getRuntime();
        Log.i(LOG_TAG, toString());
        Log.i(LOG_TAG,
                String.format("MEM : size(free/max/total) = %.2fm/%.2fm/%.2fm",
                        (Float.valueOf(r.freeMemory()) / 1024 / 1024),
                        (Float.valueOf(r.maxMemory()) / 1024 / 1024),
                        (Float.valueOf(r.totalMemory()) / 1024 / 1024)));
        Log.i(LOG_TAG, "POOL : size(valid/total) = " + valid + "/" + POOL.size());
    }
}
