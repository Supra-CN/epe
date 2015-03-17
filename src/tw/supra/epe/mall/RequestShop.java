package tw.supra.epe.mall;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.network.request.EpeJsonRequest;
import tw.supra.network.request.NetWorkHandler;

public class RequestShop extends EpeJsonRequest<ShopInfo> {
	private static final String LOG_TAG = RequestShop.class.getSimpleName();

	public RequestShop(NetWorkHandler<ShopInfo> eventHandler, ShopInfo info) {
		super(eventHandler, info);
	}

	@Override
	protected void parseJsonResponse(JSONObject response) throws JSONException {
//		INFO.ERROR_CODE.setCode(JsonUtils.getIntSafely(response,
//				APIDef.KEY_ERROR_CODE, EpeErrorCode.CODE_UNKNOW));
//		INFO.ERROR_CODE.setDescription(JsonUtils.getStrSafely(response,
//				APIDef.KEY_ERROR_DESC));
//		if (!INFO.ERROR_CODE.isOK()) {
//			INFO.ERROR_CODE.addDyingMsg("response : " + response);
//			return;
//		}

//		INFO.resultJo = JsonUtils.getJaSafely(response, "mall_list");
		
		INFO.ERROR_CODE.setCode(EpeErrorCode.CODE_OK);
		INFO.resultJo = response;
		INFO.ERROR_CODE.addDyingMsg("response : " + response);
	}

	@Override
	protected void onPostParseJson() {
	}
}
