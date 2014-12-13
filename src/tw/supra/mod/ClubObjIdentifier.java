package tw.supra.mod;

import android.net.Uri;
import android.net.Uri.Builder;

import tw.supra.epe.UriDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public abstract class ClubObjIdentifier<T extends ModelObj> extends Identifier<T> {

	abstract public T build();
	
	@Override
	protected final Uri buildUri() {
		Uri.Builder builder = new Builder();
		builder.scheme(UriDef.SCHEME_EPE);
		builder.authority(UriDef.HOST_SOHU_CLUB);
		ArrayList<String> path = defineUriPath();
		for (String segment : path) {
			builder.appendPath(segment);
		}
		HashMap<String, String> parameters = defineUriParametersInternal();
		for (Entry<String, String> entry : parameters.entrySet()) {
			builder.appendQueryParameter(entry.getKey(), entry.getValue());
		}
		return builder.build();
	}

	private HashMap<String, String> defineUriParametersInternal() {
		HashMap<String, String> parameters = new HashMap<String, String>();
		HashMap<String, String> customParameters = new HashMap<String, String>();
		if (null != customParameters) {
			parameters.putAll(defineUriParameters());
		}
		return parameters;
	}

	abstract protected HashMap<String, String> defineUriParameters();

	abstract protected ArrayList<String> defineUriPath();

	@Override
	public boolean isValid() {
		return true;
	}

}
