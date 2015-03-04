
package tw.supra.epe.account;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.ApiDef.APIDef;
import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.network.request.EpeJsonRequest;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.utils.JsonUtils;

public class RequestVerifyCode extends EpeJsonRequest<EpeRequestInfo> {
    private static final String LOG_TAG = RequestVerifyCode.class.getSimpleName();

    // public static final int TYPE_REG = 1;//用于注册
    // public static final int TYPE_STORE = 2;//用于商店短信验证
    // public static final int TYPE_RESET = 3;//用于找回密码

    // ARGS
    // public static final String ARG_MOBILE = "arg_mobile";// 参数：请求手机号
    // public static final String ARG_TYPE = "arg_type";// 参数：验证码用途，默认{@link
    // RequestVerifyCode#TYPE_REG}}

    // RESULTS
    public static final String RESULT_CODE = "result_code";// 结果，正确验证码；

    /**
     * 验证码用途
     * 
     * @author supra
     */
    public enum Type {
        REG(1), // 用于注册
        STORE(2), // 用于商店短信验证
        RESET(3),// 用于找回密码
        APPLY_STORE(5);
        public final int ID;

        private Type(int id) {
            ID = id;
        }
    }

    // public final String PHONE;
    // public final Type TYPE;

    public RequestVerifyCode(NetWorkHandler<EpeRequestInfo> eventHandler, String mobile, Type type) {
        super(eventHandler, createInfo(mobile, type));
    }

    @Override
    protected void parseJsonResponse(JSONObject response) throws JSONException {
        INFO.ERROR_CODE.setCode(JsonUtils.getIntSafely(response, APIDef.KEY_ERROR_CODE,
                EpeErrorCode.CODE_UNKNOW));
        INFO.ERROR_CODE.setDescription(JsonUtils.getStrSafely(response, APIDef.KEY_ERROR_DESC));
        if (!INFO.ERROR_CODE.isOK()) {
            return;
        }
        INFO.RESULTS.putString(RESULT_CODE, JsonUtils.getStrSafely(response, "code"));
        INFO.ERROR_CODE.addDyingMsg("response : " + response);
    }

    @Override
    protected void onPostParseJson() {
    }

    private static EpeRequestInfo createInfo(final String mobile, final Type type) {
        return new EpeRequestInfo() {
            @Override
            protected void fillQueryParamters(HashMap<String, String> paramters) {
                paramters.put("d", "api");
                paramters.put("c", "user_api");
                paramters.put("m", "get_code");
                paramters.put("mobile", mobile);
                paramters.put("type", String.valueOf(type.ID));
            }

            @Override
            public int getRequestMethod() {
                return Method.GET;
            }
        };
    }

}
