package tw.supra.epe.pages.master;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import tw.supra.epe.ApiDef.APIDef;
import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.network.request.EpeJsonRequest;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.utils.JsonUtils;

public class RequestTags extends EpeJsonRequest<EpeRequestInfo> {
	private static final String LOG_TAG = RequestTags.class.getSimpleName();

	public static final String ATTR_ID = "tag_id";
	public static final String ATTR_NAME = "tag_name";
	public static final String ATTR_TYPE = "type";
	
	// RESULTS
	public JSONArray resultJoList  ;// 返回：商品列表；

	public RequestTags(NetWorkHandler<EpeRequestInfo> eventHandler, final int page) {
		super(eventHandler, new EpeRequestInfo() {

			@Override
			public int getRequestMethod() {
				return Method.GET;
			}

			@Override
			protected void fillQueryParamters(HashMap<String, String> paramters) {
				paramters.put("d", "api");
				paramters.put("c", "division_t");
				paramters.put("m", "get_division_t_tags");
				paramters.put("page", String.valueOf(page));
//				paramters.put("count", String.valueOf(ARG_COUNT));
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

		INFO.OBJ = JsonUtils.getJaSafely(response, "tag_list");
		INFO.ERROR_CODE.addDyingMsg("response : " + response);
	}

	@Override
	protected void onPostParseJson() {
	}
}
