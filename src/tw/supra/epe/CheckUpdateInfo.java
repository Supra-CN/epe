package tw.supra.epe;

import java.util.HashMap;

import tw.supra.epe.utils.AppUtiles;
import tw.supra.network.Request.Method;
import tw.supra.network.request.EpeRequestInfo;

public class CheckUpdateInfo extends EpeRequestInfo {
	public int resultVersionCode = 1;
	public String resultVersionName;
	public String resultInfo;
	public String resultUrl;

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "version");
		// paramters.put("m", "get_msg_info");
		// paramters.put("authcode",
		// AccountCenter.getCurrentUser().getAuth());
		paramters.put("type", "android");
	}

	public boolean isUpToDate() {
		return AppUtiles.getVersionCode() >= resultVersionCode;
	}
}
