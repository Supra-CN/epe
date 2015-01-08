package tw.supra.epe.account;

import java.util.HashMap;

import tw.supra.network.Request.Method;
import tw.supra.network.request.EpeRequestInfo;

public class RegInfo extends EpeRequestInfo {
    
    //验证码用途
     public static final int TYPE_REG_BY_PHONE = 1;// 手机注册
     public static final int TYPE_REG_BY_MAIL = 2;// 邮箱注册

    // ARGS
     public static final String ARG_STR_MOBILE = "arg_str_mobile";// 参数：请求手机号
     public static final String ARG_STR_PASSWORD = "arg_str_password";// 参数：请求手机号
     public static final String ARG_STR_VERIFYCODE = "arg_str_verifycode";// 参数：验证码；
     /**
      * 参数：验证码用途，默认 {@link #TYPE_REG_BY_PHONE}
      */
     public static final String ARG_INT_TYPE = "arg_int_type";

    // RESULTS
    public static final String RESULT_STR_UID = "result_str_uid";// 返回：用户id；

    @Override
    protected void fillQueryParamters(HashMap<String, String> paramters) {
        paramters.put("d", "api");
        paramters.put("c", "user_api");
        paramters.put("m", "register");
        paramters.put("mobile", ARGS.getString(ARG_STR_MOBILE));
        paramters.put("password", ARGS.getString(ARG_STR_PASSWORD));
        paramters.put("code", ARGS.getString(ARG_STR_VERIFYCODE));
        paramters.put("type", String.valueOf(ARGS.getInt(ARG_INT_TYPE,TYPE_REG_BY_PHONE)));
    }

    @Override
    public int getRequestMethod() {
        return Method.GET;
    }
}
