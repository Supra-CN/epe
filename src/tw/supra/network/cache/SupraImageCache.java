package tw.supra.network.cache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * Simple cache adapter interface. If provided to the ImageLoader, it will be
 * used as an L1 cache before dispatch to Volley. Implementations must not
 * block. Implementation with an LruCache is recommended.
 */
public interface SupraImageCache extends ImageCache {
	public void invalidateBitmap(String url);

	public void clear();

}
