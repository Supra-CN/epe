package tw.supra.network;

import tw.supra.epe.App;
import tw.supra.network.cache.BitmapImageCache;
import tw.supra.network.misc.NetUtils;
import tw.supra.network.toolbox.HttpClientStack;
import tw.supra.network.toolbox.ImageLoader;
import tw.supra.network.toolbox.Volley;
import android.net.http.AndroidHttpClient;

public class NetworkCenter {

	private static NetworkCenter sInstance;
	private RequestQueue mRequestQueue;
	private tw.supra.network.RequestQueue mImgQueue;
	private ImageLoader mImgLoader;

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
			mRequestQueue = Volley.newRequestQueue(
					App.getInstance(),
					new HttpClientStack(AndroidHttpClient.newInstance(NetUtils
							.getUserAgent(App.getInstance()))));
		}
		return mRequestQueue;
	}

	public <T> Request<T> putToQueue(Request<T> request) {
		return getRequestQueue().add(request);
	}

	public ImageLoader getImageLoader() {
		if (null == mImgLoader) {
			mImgLoader = new ImageLoader(getImgQueue(),
					BitmapImageCache.getInstance(null));
		}

		return mImgLoader;
	}

	private RequestQueue getImgQueue() {
		if (mImgQueue == null) {
			mImgQueue = tw.supra.network.toolbox.Volley.newRequestQueue(App
					.getInstance());
		}
		return mImgQueue;
	}
}
