package tw.supra.epe.account;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.network.request.EpeJsonRequest;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;

import java.util.HashMap;

public class RequestVerifyCode extends EpeJsonRequest<EpeRequestInfo> {
    private static final String LOG_TAG = RequestVerifyCode.class.getSimpleName();

    public final String MOBILE;
    public final Type TYPE;

    public enum Type {
        REG(1),
        STORE(2),
        RESET(3);
        final int CODE;

        private Type(int code) {
            CODE = code;
        }
    }

    public RequestVerifyCode(NetWorkHandler<EpeRequestInfo> eventHandler,  String mobile,  Type type) {
        super(eventHandler, createInfo(mobile, type));
        MOBILE = mobile;
        TYPE = type;
    }

    @Override
    protected void parseJsonResponse(JSONObject response) throws JSONException {
        Log.i(LOG_TAG ,"parseJsonResponse : "+ response);
    }

    @Override
    protected void onPostParseJson() {
        // TODO Auto-generated method stub

    }

    private static EpeRequestInfo createInfo(final String mobile,  final Type type){
        return new EpeRequestInfo() {
            @Override
            protected void fillQueryParamters(HashMap<String, String> paramters) {
                paramters.put("d", "api");
                paramters.put("c", "user_api");
                paramters.put("m", "get_code");
                paramters.put("mobile", mobile);
                paramters.put("type", String.valueOf(type.CODE));
            }

            @Override
            public int getRequestMethod() {
                return Method.GET;
            }
        };
    }

}
