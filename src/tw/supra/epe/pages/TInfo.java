package tw.supra.epe.pages;

import java.util.HashMap;

import org.json.JSONArray;

import tw.supra.network.request.EpeRequestInfo;

public class TInfo extends EpeRequestInfo {
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
	public final int ARG_PAGE;// 参数：页码；
	public final int ARG_SIZE;// 参数：条数

	// RESULTS
	public JSONArray resultJoList;// 返回：话题列表；

	public TInfo(int page, int size) {
		ARG_PAGE = page;
		ARG_SIZE = size;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "tplat");
		paramters.put("m", "listT");
		paramters.put("page", String.valueOf(ARG_PAGE));
		paramters.put("count", String.valueOf(ARG_SIZE));
	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}
}
