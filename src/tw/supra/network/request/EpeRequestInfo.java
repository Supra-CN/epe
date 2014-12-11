package tw.supra.network.request;

import android.net.Uri;

import tw.supra.epe.ApiDef.ClubAPIDefine;
import tw.supra.epe.ApiDef.EpeErrorCode;

import java.util.HashMap;
import java.util.Map.Entry;

public abstract class EpeRequestInfo extends AbstractRequestInfo<EpeErrorCode> {
	

	protected abstract void fillQueryParamters(HashMap<String, String> paramters);

	private void fillQueryParamtersInternal(HashMap<String, String> paramters) {
		paramters.put("client", ClubAPIDefine.CLIENT_ANDROID);
		paramters.put("api_ver", ClubAPIDefine.API_VER);
		fillQueryParamters(paramters);
	}

	protected String buildRequestUrl() {
		Uri uri;
		Uri.Builder builder = new Uri.Builder();
		builder.scheme(ClubAPIDefine.SCHEMA);
		builder.encodedAuthority(ClubAPIDefine.HOST_API);
		builder.path("/");
		HashMap<String, String> paramters = new HashMap<String, String>();
		fillQueryParamtersInternal(paramters);
		for (Entry<String, String> entry : paramters.entrySet()) {
			builder.appendQueryParameter(entry.getKey(), entry.getValue());
		}
		uri = builder.build();
		return uri.toString();
	}
}
