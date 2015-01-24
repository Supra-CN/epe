package tw.supra.epe.activity.t;

import java.util.HashMap;

import org.json.JSONArray;

import tw.supra.network.Request.Method;
import tw.supra.network.request.EpeRequestInfo;

public class TCommentInfo extends EpeRequestInfo {
	public static final String post_id = "post_id";
	public static final String repost_content = "repost_content";
	public static final String father_id = "father_id";
	public static final String grandfather_id = "grandfather_id";
	public static final String post_time = "post_time";
	public static final String child = "child";
	public static final String user_name = "user_name";
	public static final String photo = "photo";
	public static final String user_banner = "user_banner";
	public static final String uid = "uid";

	// ARGS
	public final String ARG_TID;// 参数：页码；
	public final int ARG_PAGE;// 参数：页码；
	public final int ARG_SIZE;// 参数：条数

	// RESULTS
	public JSONArray resultJoList;// 返回：话题列表；

	public TCommentInfo(String tId, int page, int size) {
		ARG_TID = tId;
		ARG_PAGE = page;
		ARG_SIZE = size;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "tplat");
		paramters.put("m", "listReply");
		paramters.put("tt_id", ARG_TID);
		paramters.put("page", String.valueOf(ARG_PAGE));
		paramters.put("count", String.valueOf(ARG_SIZE));
		
		
	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}
}
