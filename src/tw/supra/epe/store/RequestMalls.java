package tw.supra.epe.store;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.ApiDef.APIDef;
import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.network.request.EpeJsonRequest;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.utils.JsonUtils;

public class RequestMalls extends EpeJsonRequest<EpeRequestInfo> {
	private static final String LOG_TAG = RequestMalls.class.getSimpleName();

	public RequestMalls(NetWorkHandler<EpeRequestInfo> eventHandler,
			final ObjArea area) {
		super(eventHandler, new EpeRequestInfo() {

			@Override
			public int getRequestMethod() {
				return Method.GET;
			}

			@Override
			protected void fillQueryParamters(HashMap<String, String> paramters) {
				paramters.put("d", "api");
				paramters.put("c", "mall");
				paramters.put("m", "listMall");
				paramters.put("province_id", String.valueOf(area.PROVINCE_ID));
				paramters.put("city_id", String.valueOf(area.CITY_ID));
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

		INFO.OBJ = JsonUtils.getJaSafely(response, "list");
		INFO.ERROR_CODE.addDyingMsg("response : " + response);
	}

	@Override
	protected void onPostParseJson() {
	}
}
