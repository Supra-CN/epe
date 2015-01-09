package tw.supra.epe.activity.brand;

import java.util.HashMap;

import org.json.JSONArray;

import tw.supra.epe.account.AccountCenter;
import tw.supra.location.SupraLocation;
import tw.supra.network.Request.Method;
import tw.supra.network.request.EpeRequestInfo;

public class BrandInfo extends EpeRequestInfo {
	public static final String MB_ID = "mb_id";
	public static final String MALL_ID = "mall_id";
	public static final String MALL_NAME = "mall_name";
	public static final String DISTANCE = "distance";
	public static final String ACTIVITY_INFO = "activity_info";

	// ARGS
	public final int ARG_PAGE;// 参数：页码；
	public final int ARG_SIZE;// 参数：条数；
	public final String ARG_BRAND_ID;// 参数：品牌ID；
	public final SupraLocation ARG_LOCATION;// 参数：位置；

	// RESULTS
	public JSONArray resultJoList;// 返回：话题列表；

	public BrandInfo(String brandId,SupraLocation location, int page, int size) {
		ARG_BRAND_ID = brandId;
		ARG_PAGE = page;
		ARG_SIZE = size;
		ARG_LOCATION = location;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "mall");
		paramters.put("m", "get_mall_by_brand");
		paramters.put("page", String.valueOf(ARG_PAGE));
		paramters.put("per_page", String.valueOf(ARG_SIZE));
		paramters.put("brand_id", ARG_BRAND_ID);
		paramters.put("long", String.valueOf(ARG_LOCATION.getLongitude()));
		paramters.put("lat", String.valueOf(ARG_LOCATION.getLatitude()));

	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}
}
