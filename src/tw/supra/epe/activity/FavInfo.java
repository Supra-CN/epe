package tw.supra.epe.activity;

import java.util.HashMap;

import org.json.JSONArray;

import tw.supra.epe.account.AccountCenter;
import tw.supra.location.SupraLocation;
import tw.supra.network.Request.Method;
import tw.supra.network.request.EpeRequestInfo;

public class FavInfo extends EpeRequestInfo {
	public static final String FAVOR_ID = "favor_id";
	public static final String PRODUCT_ID = "product_id";
	public static final String PRODUCT_NAME = "product_name";
	public static final String PRODUCT_PRICE = "product_price";
	public static final String PRODUCT_GENDER = "product_gender";
	public static final String PRODUCT_LIKE_NUM = "product_like_num";
	public static final String DISCOUNT_NUM = "discount_num";
	public static final String PRODUCT_FAV_NUM = "product_fav_num";
	public static final String PRODUCT_BRAND_ID = "product_brand_id";
	public static final String PRODUCT_MALL_ID = "product_mall_id";
	public static final String PRODUCT_SHOP_ID = "product_shop_id";
	public static final String PRODUCT_TAG = "product_tag";
	public static final String PRODUCT_SHARE_NUM = "product_share_num";
	public static final String PRODUCT_SKU = "product_sku";
	public static final String PRODUCT_HOTSALE = "product_hotsale";
	public static final String PRODUCT_ADD_TIME = "product_add_time";
	public static final String PRODUCT_STATUS = "product_status";
	public static final String IS_LIKE = "is_like";
	public static final String FAV_TIME = "fav_time";
	public static final String IMAGELIST = "imagelist";
	public static final String IMG_ORIGINAL = "original";
	public static final String IMG_540MIDDLE = "540Middle";
	public static final String IMG_SRC = "src";
	public static final String IMG_WIDTH = "width";
	public static final String IMG_HEIGTH = "height";
	public static final String UID = "uid";
	public static final String DISTANCE = "distance";

	// ARGS
	public final int ARG_PAGE;// 参数：页码；
	public final int ARG_SIZE;// 参数：条数；
	public final String ARG_UID;// 参数：用户；
	public final SupraLocation ARG_LOCATION;// 参数：位置；

	// RESULTS
	public JSONArray resultJoList;// 返回：话题列表；

	public FavInfo(String uid, SupraLocation location, int page, int size) {
		ARG_UID = uid;
		ARG_PAGE = page;
		ARG_SIZE = size;
		ARG_LOCATION = location;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "products");
		paramters.put("m", "listFavors");
		paramters.put("page", String.valueOf(ARG_PAGE));
		paramters.put("count", String.valueOf(ARG_SIZE));
		paramters.put("authcode", AccountCenter.getUser(ARG_UID).getAuth());
		paramters.put("long", String.valueOf(ARG_LOCATION.getLongitude()));
		paramters.put("lat", String.valueOf(ARG_LOCATION.getLatitude()));

	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}
}
