package tw.supra.epe.account;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.ApiDef.APIDef;
import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.network.request.EpeJsonRequest;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.utils.JsonUtils;
import android.text.TextUtils;

public class RequestLogin extends EpeJsonRequest<LoginInfo> {
	private static final String LOG_TAG = RequestLogin.class.getSimpleName();

	public RequestLogin(NetWorkHandler<LoginInfo> eventHandler, LoginInfo info) {
		super(eventHandler, info);
	}

	@Override
	protected void parseJsonResponse(JSONObject response) throws JSONException {
		INFO.ERROR_CODE.setCode(JsonUtils.getIntSafely(response,
				APIDef.KEY_ERROR_CODE, EpeErrorCode.CODE_UNKNOW));
		INFO.ERROR_CODE.setDescription(JsonUtils.getStrSafely(response,
				APIDef.KEY_ERROR_DESC));
		if (!INFO.ERROR_CODE.isOK()) {
			INFO.ERROR_CODE.addDyingMsg("response : " + response);
			return;
		}

		String uid = JsonUtils.getStrSafely(response, "uid");
		User user = AccountCenter.getUser(uid);
		// UserData data = AccountHelper.getUserData(user.UID);
		user.setAuth(JsonUtils.getStrSafely(response, "authcode"));
		user.setName(JsonUtils.getStrSafely(response, "user_name"));
		user.setAvatarUrl(JsonUtils.getStrSafely(response, "photo"));
		user.setShopMan(JsonUtils.getStrSafely(response, "shopman"));
		user.setShopId(JsonUtils.getStrSafely(response, "shop_id"));
		String scoreStr = JsonUtils.getStrSafely(response, "score");
		if (TextUtils.isDigitsOnly(scoreStr)) {
			user.setScore(Integer.parseInt(scoreStr));
		}

		try {
			user.flush();
		} catch (IOException e) {
			INFO.ERROR_CODE.setCode(EpeErrorCode.CODE_ERROR_BY_LOCAL_IO);
			INFO.ERROR_CODE.addDyingMsg("response : " + response);
			e.printStackTrace();
		}
		INFO.RESULTS.putString(LoginInfo.RESULT_STR_UID, user.UID);
		INFO.ERROR_CODE.addDyingMsg("response : " + response);
	}

	@Override
	protected void onPostParseJson() {
	}
}
