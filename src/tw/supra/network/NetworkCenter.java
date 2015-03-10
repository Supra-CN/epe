package tw.supra.network;

import tw.supra.data.CommonData;
import tw.supra.epe.App;
import tw.supra.epe.ColumnDef.PrefCommon;
import tw.supra.network.cache.BitmapImageCache;
import tw.supra.network.misc.NetUtils;
import tw.supra.network.toolbox.HttpClientStack;
import tw.supra.network.toolbox.ImageLoader;
import tw.supra.network.toolbox.Volley;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.widget.Toast;

import com.yijiayi.yijiayi.R;

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

	public void download(Uri url, String title, String desc) {
		DownloadManager manager = (DownloadManager) App.getInstance()
				.getSystemService(Context.DOWNLOAD_SERVICE);
		DownloadManager.Request request = new DownloadManager.Request(url);
		request.setTitle(title);
		request.setDescription(desc);
		long id = manager.enqueue(request);
		CommonData.getInstance().putPrefLong(PrefCommon.UPDATE_APK_DOWNLOAD_ID,
				id);
		Toast.makeText(App.getInstance(), R.string.downloading,
				Toast.LENGTH_SHORT).show();
	}

}
