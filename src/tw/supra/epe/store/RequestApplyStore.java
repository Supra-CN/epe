package tw.supra.epe.store;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.ApiDef.APIDef;
import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.epe.account.AccountCenter;
import tw.supra.network.error.AuthFailureError;
import tw.supra.network.request.EpeJsonRequest;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.utils.JsonUtils;

public abstract class RequestApplyStore extends EpeJsonRequest<EpeRequestInfo> {
	private static final String LOG_TAG = RequestApplyStore.class
			.getSimpleName();
	protected static final String MALL_TYPE_MALL = "1";
	protected static final String MALL_TYPE_BOUTIQUES = "2";

	private final String CODE;
	private final String MALL_TYPE;
	private final String MOBILE;
	private final ObjArea AREA;
	private final String DISCTRICT_ID;

	private static EpeRequestInfo createInfo() {
		return new EpeRequestInfo() {

			@Override
			public int getRequestMethod() {
				return Method.POST;
			}

			@Override
			protected void fillQueryParamters(HashMap<String, String> paramters) {
				paramters.put("d", "api");
				paramters.put("c", "mall");
				paramters.put("m", "addMall");
			}
		};
	}

	protected RequestApplyStore(NetWorkHandler<EpeRequestInfo> eventHandler,
			String type, String code, String mobile, ObjArea area) {
		super(eventHandler, createInfo());
		CODE = code;
		MALL_TYPE = type;
		MOBILE = mobile;
		AREA = area;
		DISCTRICT_ID = null;
	}

	abstract protected void fillAppleyParamters(
			HashMap<String, String> paramters);

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		HashMap<String, String> p = new HashMap<String, String>();
		p.put("authcode", AccountCenter.getCurrentUser().getAuth());
		p.put("code", CODE);
		p.put("mall_type", MALL_TYPE);
		p.put("mobile", MOBILE);
		p.put("province_id", String.valueOf(AREA.PROVINCE_ID));
		p.put("city_id", String.valueOf(AREA.CITY_ID));
		p.put("disctrict_id", DISCTRICT_ID);
		fillAppleyParamters(p);
		return p;
	}

	@Override
	protected void parseJsonResponse(JSONObject response) throws JSONException {
		INFO.ERROR_CODE.setCode(JsonUtils.getIntSafely(response,
				APIDef.KEY_ERROR_CODE, EpeErrorCode.CODE_UNKNOW));
		INFO.ERROR_CODE.setDescription(JsonUtils.getStrSafely(response,
				APIDef.KEY_ERROR_DESC));
		INFO.ERROR_CODE.addDyingMsg("response : " + response);
	}

	@Override
	protected void onPostParseJson() {
	}
}
