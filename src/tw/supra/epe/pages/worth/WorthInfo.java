package tw.supra.epe.pages.worth;

import java.util.HashMap;

import org.json.JSONArray;

import tw.supra.network.Request.Method;
import tw.supra.network.request.EpeRequestInfo;

public class WorthInfo extends EpeRequestInfo {
	public static final String ATTR_PRODUCT_ID = "product_id";
	public static final String ATTR_DISTANCE = "distance";
	public static final String ATTR_MALL_ID = "mall_id";
	public static final String ATTR_LONGITUDE = "longitude";
	public static final String ATTR_LATITUDE = "latitude";
	public static final String ATTR_MALL_NAME = "mall_name";
	public static final String ATTR_PRODUCT_NAME = "product_name";
	public static final String ATTR_PRODUCT_PRICE = "product_price";
	public static final String ATTR_DISCOUNT_NUM = "discount_num";
	public static final String ATTR_PRODUCT_GENDER = "product_gender";
	public static final String ATTR_PRODUCT_LIKE_NUM = "product_like_num";
	public static final String ATTR_PRODUCT_FAV_NUM = "product_fav_num";
	public static final String ATTR_PRODUCT_BRAND_ID = "product_brand_id";
	public static final String ATTR_PRODUCT_SHARE_NUM = "product_share_num";
	public static final String ATTR_PRODUCT_SKU = "product_sku";
	public static final String ATTR_PRODUCT_HOSTSALE = "product_hostsale";
	public static final String ATTR_PRODUCT_ADD_TIME = "product_add_time";
	public static final String ATTR_IS_LIKE = "is_like";
	public static final String ATTR_IMAGELIST = "imagelist";
	public static final String ATTR_IMAGE_ORIGINAL = "original";
	public static final String ATTR_IMAGE_540MIDDLE = "540Middle";
	public static final String ATTR_IMAGE_SRC = "src";
	public static final String ATTR_IMAGE_WIDTH = "width";
	public static final String ATTR_IMAGE_HEIGHT = "height";

	// 性别
	public static final int GENDER_ALL = 0;//
	public static final int GENDER_MALE = 1;//
	public static final int GENDER_FEMALE = 2;//
	public static final int SORT_DISCOUNT = 1;//
	public static final int SORT_DISTANCE = 0;//

	// ARGS
	public final double ARG_LATITUDE;// 参数：维度：
	public final double ARG_LONGITUDE;// 参数：经度；
	public final int ARG_SROT;// 参数：折扣排序 1 是 0 否 默认为0；

	/**
	 * 参数：性别，默认 {@link #GENDER_ALL}
	 */
	public final int ARG_GENDER;

	public final int ARG_PAGE;// 参数：页码；
	// public final int ARG_COUNT;// 参数：条数

	// RESULTS
	public JSONArray resultJoList;// 返回：商品列表；

	public WorthInfo(double latitude, double longitude, int sort, int gender,
			int page) {
		ARG_LATITUDE = latitude;
		ARG_LONGITUDE = longitude;
		ARG_SROT = sort;
		ARG_GENDER = gender;
		ARG_PAGE = page;
		// ARG_COUNT = count;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "products");
		paramters.put("m", "listWorthBuy");
		paramters.put("long", String.valueOf(ARG_LONGITUDE));
		paramters.put("lat", String.valueOf(ARG_LATITUDE));
		paramters.put("sex", String.valueOf(ARG_GENDER));
		paramters.put("discount", String.valueOf(ARG_SROT));
		paramters.put("page", String.valueOf(ARG_PAGE));
		// paramters.put("count", String.valueOf(ARG_COUNT));
	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}
}
