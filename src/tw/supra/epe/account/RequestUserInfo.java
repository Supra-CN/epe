package tw.supra.epe.account;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.ApiDef.APIDef;
import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.network.request.EpeJsonRequest;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.utils.JsonUtils;

public class RequestUserInfo extends EpeJsonRequest<UserInfo> {
	private static final String LOG_TAG = RequestUserInfo.class.getSimpleName();

	public RequestUserInfo(NetWorkHandler<UserInfo> eventHandler, UserInfo info) {
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

		JSONObject joUser = response.getJSONObject("user_info");
		if (null != joUser) {
			User user = AccountCenter.getUser(joUser.getString("uid"));
			user.setName(joUser.getString("user_name"));
			user.setShopMan(JsonUtils.getStrSafely(joUser, "shopman"));
			user.setShopId(JsonUtils.getStrSafely(joUser, "shop_id"));
			user.setAvatarUrl(joUser.getString("photo"));
			user.setScore(joUser.getInt("score"));
			user.setFansCount(joUser.getInt("fans_num"));
			user.setAttentionCount(joUser.getInt("attentions_num"));
			try {
				user.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		INFO.ERROR_CODE.addDyingMsg("response : " + response);
	}

	@Override
	protected void onPostParseJson() {
	}
}
