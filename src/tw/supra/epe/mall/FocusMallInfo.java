package tw.supra.epe.mall;

import java.util.HashMap;

import org.json.JSONArray;

import tw.supra.epe.account.AccountCenter;
import tw.supra.network.Request.Method;
import tw.supra.network.request.EpeRequestInfo;

public class FocusMallInfo extends EpeRequestInfo {
	public static final String MB_ID = "mb_id";
	public static final String MALL_ID = "mall_id";
	public static final String MALL_NAME = "mall_name";
	public static final String DISTANCE = "distance";
	public static final String ACTIVITY_INFO = "activity_info";

	// ARGS
	public final int ARG_PAGE;// 参数：页码；
	// public final int ARG_SIZE;// 参数：条数；

	// RESULTS
	public JSONArray resultJoList;// 返回：话题列表；

	public FocusMallInfo(int page) {
		ARG_PAGE = page;
		// ARG_SIZE = size;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "friendship");
		paramters.put("m", "listMall");
		paramters.put("authcode", AccountCenter.getCurrentUser().getAuth());
		paramters.put("page", String.valueOf(ARG_PAGE));
		// paramters.put("per_page", String.valueOf(ARG_SIZE));
	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}
}
