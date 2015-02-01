package tw.supra.epe.msg;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.ApiDef.APIDef;
import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.epe.account.AccountCenter;
import tw.supra.network.request.EpeJsonRequest;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.utils.JsonUtils;

public class RequestMsg extends EpeJsonRequest<EpeRequestInfo> {
	private static final String LOG_TAG = RequestMsg.class.getSimpleName();

	public RequestMsg(NetWorkHandler<EpeRequestInfo> eventHandler, final String msgId) {
		super(eventHandler, new EpeRequestInfo() {
			
			@Override
			public int getRequestMethod() {
				return Method.GET;
			}
			
			@Override
			protected void fillQueryParamters(HashMap<String, String> paramters) {
				paramters.put("d", "api");
				paramters.put("c", "msg");
				paramters.put("m", "get_msg_info");
				paramters.put("authcode", AccountCenter.getCurrentUser().getAuth());
				paramters.put("msg_id",msgId);
			}
		});
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

		INFO.OBJ = response;
		INFO.ERROR_CODE.addDyingMsg("response : " + response);
	}

	@Override
	protected void onPostParseJson() {
	}
}
