package tw.supra.epe.activity.t;

import java.util.HashMap;

import org.json.JSONObject;

import tw.supra.epe.account.AccountCenter;
import tw.supra.network.Request.Method;
import tw.supra.network.request.EpeRequestInfo;

public class TContentInfo extends EpeRequestInfo {
	public static final String TT_ID = "tt_id";
	public static final String TT_CONTENT = "tt_content";
	public static final String ADD_TIME = "add_time";
	public static final String TT_LIKE_NUM = "tt_like_num";
	public static final String TT_SHARE_NUM = "tt_share_num";
	public static final String TT_COMMENT_NUM = "tt_comment_num";
	public static final String IS_LIKE = "is_like";
	
	public static final String IMAGE = "image";
	public static final String IMG_SRC = "url";
	public static final String IMG_WIDTH = "width";
	public static final String IMG_HEIGHT = "height";
	
	public static final String UINFO = "uinfo";
	public static final String USER_NAME = "user_name";
	public static final String PHOTO = "photo";
	public static final String USER_BANNER = "user_banner";
	public static final String UID = "uid";

	public static final String TT_DETAIL = "tt_detail";

	// ARGS
	public final String ARG_UID;// 参数：用户；
	public final String ARG_T_ID;// 参数：产品ID；

	// RESULTS
	public JSONObject resultTInfo;

	public TContentInfo(String uid,  String tId ) {
		ARG_UID = uid;
		ARG_T_ID = tId;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "tplat");
		paramters.put("m", "ttinfo");
		paramters.put("authcode", AccountCenter.getUser(ARG_UID).getAuth());
		paramters.put("tt_id", ARG_T_ID);

	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}
}
