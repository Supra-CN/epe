package tw.supra.network.request;

import org.apache.http.entity.mime.MultipartEntity;

import tw.supra.network.NetworkResponse;

abstract public class InfoMultiRequest<E extends ErrorCode, T extends AbstractRequestInfo<E>>
		extends InfoRequest<E,T> {

	public InfoMultiRequest(NetWorkHandler<T> eventHandler, T info) {
		super(eventHandler, info);
	}

	private static final String LOG_TAG = InfoMultiRequest.class.getSimpleName();

	protected abstract void parseResponse(NetworkResponse response);

    abstract public MultipartEntity getMultipartEntity();
}

