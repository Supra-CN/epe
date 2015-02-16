package tw.supra.epe.mall;

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

public class RequestPushMallFocusStatus extends EpeJsonRequest<EpeRequestInfo> {
	private static final String LOG_TAG = RequestPushMallFocusStatus.class
			.getSimpleName();
	private final String MALL_ID;

	public RequestPushMallFocusStatus(
			NetWorkHandler<EpeRequestInfo> eventHandler, String mallId,
			final boolean status) {
		super(eventHandler, new EpeRequestInfo() {

			@Override
			public int getRequestMethod() {
				return Method.POST;
			}

			@Override
			protected void fillQueryParamters(HashMap<String, String> paramters) {
				paramters.put("d", "api");
				paramters.put("c", "friendship");
				paramters.put("m", status ? "mallShipCreate" : "mallDestory");
			}
		});

		MALL_ID = mallId;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		HashMap<String, String> p = new HashMap<String, String>();
		p.put("authcode", AccountCenter.getCurrentUser().getAuth());
		p.put("mall_id", MALL_ID);
		return p;
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
	}

	@Override
	protected void onPostParseJson() {
	}
}
