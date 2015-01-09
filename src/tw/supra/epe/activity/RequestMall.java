package tw.supra.epe.activity;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.ApiDef.APIDef;
import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.epe.activity.brand.BrandInfo;
import tw.supra.network.request.EpeJsonRequest;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.utils.JsonUtils;

public class RequestMall extends EpeJsonRequest<MallInfo> {
	private static final String LOG_TAG = RequestMall.class.getSimpleName();

	public RequestMall(NetWorkHandler<MallInfo> eventHandler, MallInfo info) {
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
