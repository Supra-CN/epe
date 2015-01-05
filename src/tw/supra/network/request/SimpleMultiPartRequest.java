package tw.supra.network.request;

import java.io.UnsupportedEncodingException;

import tw.supra.network.NetworkResponse;
import tw.supra.network.Response;
import tw.supra.network.Response.ErrorListener;
import tw.supra.network.Response.Listener;
import tw.supra.network.toolbox.HttpHeaderParser;

/**
 * A Simple request for making a Multi Part request whose response is retrieve
 * as String
 */
public class SimpleMultiPartRequest extends MultiPartRequest<String> {

	private Listener<String> mListener;

	/**
	 * Creates a new request with the given method.
	 *
	 * @param method
	 *            the request {@link Method} to use
	 * @param url
	 *            URL to fetch the string at
	 * @param listener
	 *            Listener to receive the String response
	 * @param errorListener
	 *            Error listener, or null to ignore errors
	 */
	public SimpleMultiPartRequest(int method, String url,
			Listener<String> listener, ErrorListener errorListener) {
		super(method, url, listener, errorListener);
		mListener = listener;
	}

	/**
	 * Creates a new GET request.
	 *
	 * @param url
	 *            URL to fetch the string at
	 * @param listener
	 *            Listener to receive the String response
	 * @param errorListener
	 *            Error listener, or null to ignore errors
	 */
	public SimpleMultiPartRequest(String url, Listener<String> listener,
			ErrorListener errorListener) {
		super(Method.POST, url, listener, errorListener);
		mListener = listener;
	}

	@Override
	protected void deliverResponse(String response) {
		if (null != mListener) {
			mListener.onResponse(response);
		}
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed,
				HttpHeaderParser.parseCacheHeaders(response));
	}
}