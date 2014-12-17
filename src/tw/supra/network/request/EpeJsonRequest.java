
package tw.supra.network.request;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.ApiDef.EpeErrorCode;
import android.text.TextUtils;

public abstract class EpeJsonRequest<T extends EpeRequestInfo> extends EpeStringRequest<T> {
    private static final String LOG_TAG = EpeJsonRequest.class.getSimpleName();

    public EpeJsonRequest(NetWorkHandler<T> eventHandler, T info) {
        super(eventHandler, info);
    }

    protected abstract void parseJsonResponse(JSONObject response) throws JSONException;

    protected abstract void onPostParseJson();

    @Override
    protected void parseStringResponse(String response) {

        if (TextUtils.isEmpty(response)) {
            INFO.ERROR_CODE.setCode(EpeErrorCode.CODE_UNKNOW);
            INFO.ERROR_CODE.setDescription("cause by response string empty");
            return;
        }

        JSONObject joResult = null;

        try {
            joResult = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            INFO.ERROR_CODE.setCode(EpeErrorCode.CODE_BAD_RESPONSE);
            INFO.ERROR_CODE.setDescription("cause by response Json illegal");
            INFO.ERROR_CODE.addDyingMsg(response);
            return;
        }

        try {
            parseJsonResponse(joResult);
        } catch (JSONException e) {
            e.printStackTrace();
            if (!INFO.ERROR_CODE.isReturnByApiError()) {
                INFO.ERROR_CODE.setCode(EpeErrorCode.CODE_ERROR_BY_JSON_DECODE);
            }
            INFO.ERROR_CODE.addDyingMsg("\n cause by response Json decode" + response);
            return;
        }
        onPostParseJson();
    }
}
