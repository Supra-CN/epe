package tw.supra.network;

import tw.supra.epe.App;
import u.aly.cl;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class NetworkCenter {

	private static NetworkCenter sInstance;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

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
		if (null == mImageLoader) {

			mImageLoader = new ImageLoader(getRequestQueue(),
					new ImageLoader.ImageCache() {

						private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(
								20);

						@Override
						public Bitmap getBitmap(String url) {
							return cache.get(url);
						}

						@Override
						public void putBitmap(String url, Bitmap bitmap) {
							cache.put(url, bitmap);
						}
					});
		}

		return mImageLoader;
	}

}
