package tw.supra.epe.pages.master;

import java.util.HashMap;

import org.json.JSONArray;

import android.text.TextUtils;
import tw.supra.epe.account.AccountCenter;
import tw.supra.network.request.EpeRequestInfo;

public class CustomInfo extends EpeRequestInfo {
	public static final String ATTR_TT_ID ="tt_id";
	public static final String ATTR_T_IMG ="t_img";
	public static final String ATTR_UID ="uid";
	public static final String ATTR_NAME ="name";
	public static final String ATTR_U_PHOTO ="u_photo";
	public static final String ATTR_TT_IMG_WIDTH ="tt_img_width";
	public static final String ATTR_TT_IMG_HEIGHT ="tt_img_height";
	
	// ARGS
	public final String ARG_T_UID;// 参数：指定搭配师UID：
	public final int ARG_PAGE;// 参数：页码；
//	public final int ARG_COUNT;// 参数：条数

	// RESULTS
	public JSONArray resultJoList  ;// 返回：话题列表；
	
	public CustomInfo(String tUid,  int page) {
		ARG_T_UID = tUid;
		ARG_PAGE = page;
//		ARG_COUNT = count;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "division_t");
		paramters.put("m", "get_division_tts");
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
