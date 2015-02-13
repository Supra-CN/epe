package tw.supra.network.misc;


/**
 * An {@link com.android.volley.toolbox.HttpStack HttpStack} implementation
 * which uses OkHttp as its transport.
 */
//public class OkHttpStack extends HurlStack {
//	private final OkHttpClient CLIENT;
//	private final OkHttpStack STACK;
//
//	public OkHttpStack() {
//		this(new OkHttpClient());
//	}
//
//	public OkHttpStack(OkHttpClient client) {
//		if (client == null) {
//			throw new NullPointerException("Client must not be null.");
//		}
//		this.CLIENT = client;
//		this.STACK = new OkHttpStack(client);
//	}
//
//	@Override
//	protected HttpURLConnection createConnection(URL url) throws IOException {
//		return STACK.createConnection(url);
//	}
//}