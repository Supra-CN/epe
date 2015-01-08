package tw.supra.epe.pages.master;

import java.util.HashMap;

import org.json.JSONArray;

import tw.supra.network.Request.Method;
import tw.supra.network.request.EpeRequestInfo;
import android.text.TextUtils;

public class TopicInfo extends EpeRequestInfo {
	public static final String ATTR_TOPIC_ID ="topic_id";
	public static final String ATTR_TOPIC_TITLE ="topic_title";
	public static final String ATTR_TOPIC_CONTENT ="topic_content";
	public static final String ATTR_TOPIC_CREATE_TIME ="topic_create_time";
	public static final String ATTR_TOPIC_LIKE_NUM 	= "topic_like_num";
	public static final String ATTR_TOPIC_REPOST_NUM 	= "topic_repost_num";
	public static final String ATTR_TOPIC_SHARE_NUM 	= "topic_share_num";
	public static final String ATTR_TOPIC_UID 	= "topic_uid";
	public static final String ATTR_TOPIC_IMAGES 	= "topic_images";
	public static final String ATTR_TOPIC_LAST_POST 	= "topic_last_post";
	public static final String ATTR_T_USERNAME 	= "t_username";
	public static final String ATTR_T_USER_PHOTO 	= "t_user_photo";
	
	// ARGS
	public final String ARG_T_UID;// 参数：指定搭配师UID：
	public final int ARG_PAGE;// 参数：页码；
//	public final int ARG_COUNT;// 参数：条数

	// RESULTS
	public JSONArray resultJoList  ;// 返回：话题列表；
	
	public TopicInfo(String tUid,  int page) {
		ARG_T_UID = tUid;
		ARG_PAGE = page;
//		ARG_COUNT = count;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "topic");
		paramters.put("m", "get_topics");
		if(!TextUtils.isEmpty(ARG_T_UID)){
			paramters.put("t_uid", ARG_T_UID);
		}
		paramters.put("page", String.valueOf(ARG_PAGE));
//		paramters.put("count", String.valueOf(ARG_COUNT));
	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}
}
