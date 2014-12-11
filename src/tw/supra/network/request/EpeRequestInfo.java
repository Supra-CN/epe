package tw.supra.network.request;

import android.net.Uri;

import tw.supra.epe.ApiDef.APIDef;
import tw.supra.epe.ApiDef.EpeErrorCode;

import java.util.HashMap;
import java.util.Map.Entry;

public abstract class EpeRequestInfo extends AbstractRequestInfo<EpeErrorCode> {
	

	protected abstract void fillQueryParamters(HashMap<String, String> paramters);

	private void fillQueryParamtersInternal(HashMap<String, String> paramters) {
//		paramters.put("client", APIDef.CLIENT_ANDROID);
//		paramters.put("api_ver",APIDef.API_VER);
		fillQueryParamters(paramters);
	}

	protected String buildRequestUrl() {
		Uri uri;
		Uri.Builder builder = new Uri.Builder();
		builder.scheme(APIDef.SCHEMA);
		builder.encodedAuthority(APIDef.HOST_API);
		builder.path("/");
		HashMap<String, String> paramters = new HashMap<String, String>();
		fillQueryParamtersInternal(paramters);
		for (Entry<String, String> entry : paramters.entrySet()) {
			builder.appendQueryParameter(entry.getKey(), entry.getValue());
		}
		uri = builder.build();
		return uri.toString();
	}
	
	@Override
	protected EpeErrorCode initErrorCode() {
	    return new EpeErrorCode();
	}
}
