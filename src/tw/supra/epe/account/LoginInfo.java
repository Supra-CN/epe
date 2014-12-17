package tw.supra.epe.account;

import java.util.HashMap;

import tw.supra.network.request.EpeRequestInfo;

public class LoginInfo extends EpeRequestInfo {

	//登录类型
	public static final String TYPE_LOGIN_BY_NORMAL = "normal";//普通登录
	public static final String TYPE_LOGIN_BY_WEIBO = "s_weibo";//第三方登录，新浪微博
	public static final String TYPE_LOGIN_BY_QQ = "qq";//第三方登录，腾讯qq
	public static final String TYPE_LOGIN_BY_WECHAT = "weixin";//第三方登录，腾讯微信

	//
	public static final int GENDER_MALE = 1;//
	public static final int GENDER_FEMALE = 2;//

	// ARGS
	public static final String ARG_STR_MOBILE = "mobile";// 参数：请求手机号
	public static final String ARG_STR_PASSWORD = "password";// 参数：请求手机号
	/**
	 * 参数：登录类型，默认 {@link #TYPE_LOGIN_BY_NORMAL}
	 */
	public static final String ARG_STR_TYPE = "type";
	/**
	 * 参数：性别，默认 女{@link #GENDER_FEMALE}
	 */
	public static final String ARG_INT_GENDER = "gender";

	// RESULTS
	public static final String RESULT_STR_UID = "result_str_uid";// 返回：用户id；

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "user_api");
		paramters.put("m", "login");
		paramters.put(ARG_STR_MOBILE, ARGS.getString(ARG_STR_MOBILE));
		paramters.put(ARG_STR_PASSWORD, ARGS.getString(ARG_STR_PASSWORD));
		paramters.put(ARG_STR_TYPE,
				ARGS.getString(ARG_STR_TYPE, TYPE_LOGIN_BY_NORMAL));
		paramters.put(ARG_INT_GENDER,
				String.valueOf(ARGS.getInt(ARG_STR_TYPE, GENDER_FEMALE)));
		paramters.put("devicetoken", "devicetoken");
	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}

}
