
package tw.supra.epe.account;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.ApiDef.APIDef;
import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.network.request.EpeJsonRequest;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.utils.JsonUtils;

public class RequestReg extends EpeJsonRequest<RegInfo> {
    private static final String LOG_TAG = RequestReg.class.getSimpleName();

    public RequestReg(NetWorkHandler<RegInfo> eventHandler, RegInfo info) {
        super(eventHandler, info);
    }

    @Override
    protected void parseJsonResponse(JSONObject response) throws JSONException {
        INFO.ERROR_CODE.setCode(JsonUtils.getIntSafely(response, APIDef.KEY_ERROR_CODE,
                EpeErrorCode.CODE_UNKNOW));
        INFO.ERROR_CODE.setDescription(JsonUtils.getStrSafely(response, APIDef.KEY_ERROR_DESC));
        if (!INFO.ERROR_CODE.isOK()) {
            return;
        }
        
        INFO.RESULTS.putString(RegInfo.RESULT_STR_AUTH, JsonUtils.getStrSafely(response, "authcode"));
        INFO.RESULTS.putString(RegInfo.RESULT_STR_NAME, JsonUtils.getStrSafely(response, "name"));
        INFO.ERROR_CODE.addDyingMsg("response : " + response);
    }

    @Override
    protected void onPostParseJson() {
    }
}
