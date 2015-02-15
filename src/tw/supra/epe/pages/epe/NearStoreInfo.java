package tw.supra.epe.pages.epe;

import java.util.HashMap;

import org.json.JSONArray;

import tw.supra.network.Request.Method;
import tw.supra.network.request.EpeRequestInfo;

public class NearStoreInfo extends EpeRequestInfo {
	public static final String ATTR_MALL_ID ="mall_id";
	public static final String ATTR_MALL_TYPE ="mall_type";
	public static final String ATTR_DISTANCE="distance";
	public static final String ATTR_LONGITUDE=  "longitude";
	public static final String ATTR_LATITUDE 	= "latitude";
	public static final String ATTR_MALL_NAME =	 "mall_name";
	
	// ARGS
	public final double ARG_LATITUDE;// 参数：维度：
	public final double ARG_LONGITUDE;// 参数：经度；

	public final int ARG_PAGE;// 参数：页码；
//	public final int ARG_COUNT;// 参数：条数

	// RESULTS
	public JSONArray resultJoList  ;// 返回：商品列表；

	public NearStoreInfo(double latitude, double longitude, int page) {
		ARG_LATITUDE = latitude;
		ARG_LONGITUDE = longitude;
		ARG_PAGE = page;
//		ARG_COUNT = count;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "mall");
		paramters.put("m", "gerNearMalls");
		paramters.put("long", String.valueOf(ARG_LONGITUDE));
		paramters.put("lat", String.valueOf(ARG_LATITUDE));
		paramters.put("page", String.valueOf(ARG_PAGE));
//		paramters.put("count", String.valueOf(ARG_COUNT));
	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}
}
