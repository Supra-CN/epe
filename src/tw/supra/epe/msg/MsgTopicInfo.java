package tw.supra.epe.msg;

import java.util.HashMap;

import org.json.JSONArray;

import tw.supra.epe.account.AccountCenter;
import tw.supra.network.Request.Method;
import tw.supra.network.request.EpeRequestInfo;

public class MsgTopicInfo extends EpeRequestInfo {
	public static final String MSG_ID = "msg_id";
	public static final String FROM_UID = "from_uid";
	public static final String TO_UID = "to_uid";
	public static final String TITLE = "title";
	public static final String PIC = "pic";
	public static final String CONTENT = "content";
	public static final String TYPE = "type";
	public static final String SEND_TIME = "send_time";
	public static final String IS_READ = "is_read";//1未读 等于2已读
	public static final String PHOTO = "photo";
	public static final String FROM_USERNAME = "from_username";
	public static final String THEME_ID = "theme_id";
 
	// ARGS
	public final String ARG_ACTION;// 参数：页码；

	// RESULTS
	public JSONArray resultJoList;// 返回：话题列表；

	public MsgTopicInfo(String topicId) {
		ARG_ACTION = topicId;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "msg");
		paramters.put("m", "get_special_msg");
		paramters.put("authcode", AccountCenter.getCurrentUser().getAuth());
		paramters.put("action",ARG_ACTION);
		
		
	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}
}
