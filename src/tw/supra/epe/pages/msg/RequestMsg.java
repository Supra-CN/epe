package tw.supra.epe.pages.msg;

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

	public RequestMsg(NetWorkHandler<EpeRequestInfo> eventHandler
			) {
		super(eventHandler, new EpeRequestInfo() {
			
			@Override
			public int getRequestMethod() {
				return Method.GET;
			}
			
			@Override
			protected void fillQueryParamters(HashMap<String, String> paramters) {
				paramters.put("d", "api");
				paramters.put("c", "msg");
				paramters.put("m", "get_my_msg");
				paramters.put("authcode", AccountCenter.getCurrentUser()
						.getAuth());
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
		INFO.ERROR_CODE.addDyingMsg("response : " + response);
		INFO.OBJ = response;
	}

	@Override
	protected void onPostParseJson() {
		
	}

}
