package tw.supra.epe.activity.brand;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.navisdk.ui.widget.NewerGuideDialog;

import tw.supra.epe.ApiDef.APIDef;
import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.epe.account.AccountCenter;
import tw.supra.network.request.EpeJsonRequest;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.utils.JsonUtils;

public class RequestBrandFocusStatus extends EpeJsonRequest<EpeRequestInfo> {
	private static final String LOG_TAG = RequestBrandFocusStatus.class.getSimpleName();

	public RequestBrandFocusStatus(NetWorkHandler<EpeRequestInfo> eventHandler, final String brandId) {
		super(eventHandler, new EpeRequestInfo() {
			
			@Override
			public int getRequestMethod() {
				return Method.GET;
			}
			
			@Override
			protected void fillQueryParamters(HashMap<String, String> paramters) {
				paramters.put("d", "api");
				paramters.put("c", "brand");
				paramters.put("m", "isBrandFriend");
				paramters.put("authcode", AccountCenter.getCurrentUser().getAuth());
				paramters.put("brand_id", brandId);
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
		
		if(1!=response.getInt("relation")){
			INFO.ERROR_CODE.setCode(EpeErrorCode.CODE_ERROR_CAN_NOT_FRIEND);
		}

		INFO.ERROR_CODE.addDyingMsg("response : " + response);
	}

	@Override
	protected void onPostParseJson() {
	}
}
