package tw.supra.epe.account;

import java.util.HashMap;

import tw.supra.network.Request.Method;
import tw.supra.network.request.EpeRequestInfo;

public class UserInfo extends EpeRequestInfo {

	// ARGS
	public final String UID;// 参数：用户


	public UserInfo(String uid) {
		UID = uid;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "user_api");
		paramters.put("m", "get_user_info");
		paramters.put("authcode", AccountCenter.getUser(UID).getAuth());
	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}
}
