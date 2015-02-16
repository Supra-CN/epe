package tw.supra.epe.activity.product;

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

public class RequestPushProductLikeStatus extends EpeJsonRequest<EpeRequestInfo> {
	private static final String LOG_TAG = RequestPushProductLikeStatus.class
			.getSimpleName();

	private final String PRODUCT_ID;
	
	public RequestPushProductLikeStatus(
			NetWorkHandler<EpeRequestInfo> eventHandler,  String productId,
			final boolean status) {
		super(eventHandler, new EpeRequestInfo() {

			@Override
			public int getRequestMethod() {
				return Method.POST;
			}

			@Override
			protected void fillQueryParamters(HashMap<String, String> paramters) {
				paramters.put("d", "api");
				paramters.put("c", "products");
				paramters.put("m", status ? "like"
						: "cancelLike");
			}
		});
		PRODUCT_ID = productId;
	}
	
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		HashMap< String, String > p = new HashMap<String, String>();
		p.put("authcode", AccountCenter.getCurrentUser()
				.getAuth());
		p.put("product_id", PRODUCT_ID);
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
