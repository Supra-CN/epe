package tw.supra.epe;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.data.CommonData;
import tw.supra.epe.ColumnDef.PrefCommon;
import tw.supra.epe.ApiDef.APIDef;
import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.network.request.EpeJsonRequest;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.utils.JsonUtils;

public class RequestCheckUpdate extends EpeJsonRequest<CheckUpdateInfo> {
	private static final String LOG_TAG = RequestCheckUpdate.class
			.getSimpleName();

	public RequestCheckUpdate(NetWorkHandler<CheckUpdateInfo> eventHandler) {
		super(eventHandler, new CheckUpdateInfo());
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
        INFO.resultVersionCode = JsonUtils.getIntSafely(response, "ver_code", 0);
        INFO.resultVersionName = response.getString("ver_name");
        INFO.resultInfo = response.getString("info");
        INFO.resultUrl = response.getString("url");
		INFO.ERROR_CODE.addDyingMsg("response : " + response);
	}

	@Override
	protected void onPostParseJson() {
		if (INFO.ERROR_CODE.isOK() && INFO.isUpToDate()) {
			CommonData.getInstance().putPrefLong(
					PrefCommon.DATE_OF_VERSION_CHECK,
					System.currentTimeMillis());
		}
	}
}
