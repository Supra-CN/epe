package tw.supra.network.request;

import tw.supra.epe.App;
import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

abstract public class InfoRequest<E extends ErrorCode, T extends AbstractRequestInfo<E>>
		extends Request<T> {

	private static final String LOG_TAG = InfoRequest.class.getSimpleName();

	public final T INFO;
	private NetWorkHandler<T> mEventHandler;

	public InfoRequest(NetWorkHandler<T> eventHandler, T info) {
		super(info.getRequestMethod(), info.getRequestUrl(), null);
		INFO = info;
		mEventHandler = eventHandler;
	}

	protected abstract void parseResponse(NetworkResponse response);

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		parseResponse(response);
		return createNormalResponse(response);
	}

	protected void notifyStart() {
		INFO.setProgress(INFO.PROGRESS_INIT);
		if (null != mEventHandler) {
			mEventHandler.HandleEvent(RequestEvent.START, INFO);
		}
	}

	protected void notifyProgressing(float progress) {
		INFO.setProgress(progress);
		if (null != mEventHandler) {
			mEventHandler.HandleEvent(RequestEvent.FINISH, INFO);
		}
	}

	protected void notifyFinish() {
		notifyFinish(false);
	}

	protected void notifyFinish(boolean forceCancel) {
		if (forceCancel) {
			cancel();
		}
		INFO.setProgress(INFO.PROGRESS_MAX);
		judgeError();
		if (null != mEventHandler) {
			mEventHandler.HandleEvent(RequestEvent.FINISH, INFO);
		}
		Log.i(LOG_TAG, "\n\nrequest finish : " + INFO);
	}

	protected Response<T> createNormalResponse(NetworkResponse response) {
		return Response.success(INFO,
				HttpHeaderParser.parseCacheHeaders(response));
	}

	@Override
	protected void deliverResponse(T response) {
		notifyFinish();
	}

	@Override
	public void deliverError(VolleyError error) {
		dispatchVolleyError(error);
		INFO.ERROR_CODE.setVollyError(error);
		notifyFinish(true);
		super.deliverError(error);
	}

	protected Context getContext() {
		return App.getInstance();
	}

	abstract protected void dispatchVolleyError(VolleyError error);

	abstract protected void judgeError();
}
