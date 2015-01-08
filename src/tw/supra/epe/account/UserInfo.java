package tw.supra.epe.account;

import java.util.HashMap;

import org.json.JSONArray;

import tw.supra.network.Request.Method;
import tw.supra.network.request.EpeRequestInfo;

public class UserInfo extends EpeRequestInfo {
	public static final String ATTR_TT_ID = "tt_id";
	public static final String ATTR_IMG_URL = "img_url";
	public static final String ATTR_TT_LIKE_NUM = "tt_like_num";
	public static final String ATTR_TT_SHARE_NUM = "tt_share_num";
	public static final String ATTR_TT_COMMENT_NUM = "tt_comment_num";
	public static final String ATTR_ADD_TIME = "add_time";
	public static final String ATTR_UINFO = "uinfo";
	public static final String ATTR_UINFO_USER_NAME = "user_name";
	public static final String ATTR_UINFO_PHOTO = "photo";

	// ARGS
	public final String UID;// 参数：用户

	// RESULTS
	public JSONArray resultJoList;// 返回：话题列表；

	public UserInfo(String uid) {
		UID = uid;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "user_api");
		paramters.put("m", "get_user_info");
		paramters.put("authcode", AccountCenter.getUser(UID).getAuth());
	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}
}
