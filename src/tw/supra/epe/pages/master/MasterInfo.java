package tw.supra.epe.pages.master;

import java.util.HashMap;

import org.json.JSONArray;

import android.text.TextUtils;
import tw.supra.epe.account.AccountCenter;
import tw.supra.network.request.EpeRequestInfo;

public class MasterInfo extends EpeRequestInfo {
	public static final String ATTR_ID ="id";
	public static final String ATTR_UID 	= "uid";
	public static final String ATTR_NAME =	 "name";
	public static final String ATTR_GRADE =	 "grade";
	public static final String ATTR_INTRO =	 "t_intro";
	public static final String ATTR_PHOTO =	 "photo";
	
	public static final String ACTION_POPU="popu";
	public static final String ACTION_MY="my";
	
	// ARGS
	public final String ARG_ACTION;// 参数：动作：
	public final String ARG_AUTHCODE;// 参数：页码；
	public final String ARG_TAG_ID;// 参数：页码；
	public final int ARG_PAGE;// 参数：页码；
//	public final int ARG_COUNT;// 参数：条数

	// RESULTS
	public JSONArray resultJoList  ;// 返回：商品列表；

	
	
	public MasterInfo(String action, String uid, String tagId, int page) {
		ARG_TAG_ID = tagId;
		ARG_ACTION = action;
		ARG_PAGE = page;
		ARG_AUTHCODE = AccountCenter.getUser(uid).getAuth();
//		ARG_COUNT = count;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "division_t");
		paramters.put("m", "get_division_teachers");
		if(!TextUtils.isEmpty(ARG_ACTION)){
			paramters.put("action", ARG_ACTION);
		}
		if(!TextUtils.isEmpty(ARG_AUTHCODE)){
			paramters.put("authcode", ARG_AUTHCODE);
		}
		if(!TextUtils.isEmpty(ARG_TAG_ID)){
			paramters.put("tag_id", ARG_TAG_ID);
		}
		paramters.put("page", String.valueOf(ARG_PAGE));
//		paramters.put("count", String.valueOf(ARG_COUNT));
	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}
}
