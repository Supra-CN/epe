package tw.supra.network;

import tw.supra.epe.App;
import tw.supra.network.cache.DiskLruImageCache;
import tw.supra.network.cache.LruImageCache;
import android.graphics.Bitmap.CompressFormat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;

public class NetworkCenter {

	private static NetworkCenter sInstance;
	private RequestQueue mRequestQueue;
	private RequestQueue mImgQueue;
	private ImageLoader mImgLoader;
	private ImageCache mImgCache;
	
    // Default memory cache size in kilobytes
    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 5; // 5MB

    // Default disk cache size in bytes
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

	private NetworkCenter() {
	}

	public static synchronized NetworkCenter getInstance() {
		if (sInstance == null) {
			sInstance = new NetworkCenter();
		}
		return sInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			// getApplicationContext() is key, it keeps you from leaking the
			// Activity or BroadcastReceiver if someone passes one in.
			mRequestQueue = Volley.newRequestQueue(App.getInstance());
		}
		return mRequestQueue;
	}

	public <T> Request<T> putToQueue(Request<T> request) {
		return getRequestQueue().add(request);
	}

	public ImageLoader getImageLoader() {
		if (null == mImgLoader) {
			mImgLoader = new ImageLoader(getImgQueue(), getImgCache());
		}

		return mImgLoader;
	}

	private RequestQueue getImgQueue() {
		if (mImgQueue == null) {
			mImgQueue = Volley.newRequestQueue(App.getInstance());
		}
		return mImgQueue;
	}

	private ImageCache getImgCache() {
		if (mImgCache == null) {
//			mImgCache = new DiskLruImageCache(App.getInstance(), "imgs", DEFAULT_DISK_CACHE_SIZE, CompressFormat.JPEG, 70);
			mImgCache = new LruImageCache();
		}
		return mImgCache;
	}
}
