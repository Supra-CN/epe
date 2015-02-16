package tw.supra.epe.mall;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import tw.supra.epe.account.AccountCenter;
import tw.supra.location.SupraLocation;
import tw.supra.network.Request.Method;
import tw.supra.network.request.EpeRequestInfo;

public class MallInfo extends EpeRequestInfo {
	public static final String UID = "uid";
	public static final String MALL_ID = "mall_id";
	public static final String MALL_NAME = "mall_name";
	public static final String MOBILE = "mobile";
	public static final String PROVINCE_ID = "province_id";
	public static final String CITY_ID = "city_id";
	public static final String DISCTRICT_ID = "disctrict_id";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String FLOOR_NUM = "floor_num";
	public static final String ADD_TIME = "add_time";
	public static final String MALL_TYPE = "mall_type";
	public static final String STREET = "street";
	public static final String ADDRESS = "address";
	public static final String FOLLOWING = "following";
	
	public static final String BRAND = "brand";
	public static final String SHOP_ID = "shop_id";
	public static final String DOOR_NO = "doorno";
	public static final String BRAND_ID = "brand_id";
	public static final String BRAND_NAME = "brand_name";
	public static final String BRAND_LOGO = "brand_logo";
	public static final String FLOOR = "floor";

	// ARGS
	public final String ARG_MALL_ID;// 参数：品牌ID；

	// RESULTS
	public JSONObject resultJo;// 返回：话题列表；

	public MallInfo(String mallId) {
		ARG_MALL_ID = mallId;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "mall");
		paramters.put("m", "getMallDetail");
		paramters.put("mall_id", ARG_MALL_ID);
	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}
}
