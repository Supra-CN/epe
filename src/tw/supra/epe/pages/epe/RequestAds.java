package tw.supra.epe.pages.epe;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.ApiDef.APIDef;
import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.network.request.EpeJsonRequest;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.utils.JsonUtils;

public class RequestAds extends EpeJsonRequest<EpeRequestInfo> {
	private static final String LOG_TAG = RequestAds.class.getSimpleName();

	public static final String ATTR_ID = "id";
	public static final String ATTR_IMG_URL = "img_url";
	public static final String ATTR_ORDER = "order";
	public static final String ATTR_ADDTIME = "addtime";
	public static final String ATTR_OPERATOR = "operator";
	public static final String ATTR_STARTTIME = "starttime";
	public static final String ATTR_ENDTIME = "endtime";
	public static final String ATTR_STATUS = "status";
	
	// RESULTS
	public JSONArray resultJoList  ;// 返回：商品列表；

	public RequestAds(NetWorkHandler<EpeRequestInfo> eventHandler) {
		super(eventHandler, new EpeRequestInfo() {

			@Override
			public int getRequestMethod() {
				return Method.GET;
			}

			@Override
			protected void fillQueryParamters(HashMap<String, String> paramters) {
				paramters.put("d", "api");
				paramters.put("c", "advertisement");
				paramters.put("m", "get_advertisement");
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

		INFO.OBJ = JsonUtils.getJaSafely(response, "advertisements_data");
		INFO.ERROR_CODE.addDyingMsg("response : " + response);
	}

	@Override
	protected void onPostParseJson() {
	}
}
