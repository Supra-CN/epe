package tw.supra.epe.activity;

import java.util.HashMap;

import org.json.JSONObject;

import tw.supra.epe.account.AccountCenter;
import tw.supra.network.Request.Method;
import tw.supra.network.request.EpeRequestInfo;

public class ProductInfo extends EpeRequestInfo {
	public static final String FAVOR_ID = "favor_id";
	public static final String PRODUCT_ID = "product_id";
	public static final String PRODUCT_NAME = "product_name";
	public static final String PRODUCT_PRICE = "product_price";
	public static final String DISCOUNT_NUM = "discount_num";
	public static final String PRODUCT_GENDER = "product_gender";
	public static final String PRODUCT_LIKE_NUM = "product_like_num";
	public static final String PRODUCT_FAV_NUM = "product_fav_num";
	public static final String PRODUCT_BRAND_ID = "product_brand_id";
	public static final String PRODUCT_MALL_ID = "product_mall_id";
	public static final String PRODUCT_SHOP_ID = "product_shop_id";
	public static final String PRODUCT_TAG = "product_tag";
	public static final String PRODUCT_SHARE_NUM = "product_share_num";
	public static final String PRODUCT_SKU = "product_sku";
	public static final String PRODUCT_HOTSALE = "product_hotsale";
	public static final String IS_FAVOR = "is_favor";
	public static final String PRODUCT_ADD_TIME = "product_add_time";
	public static final String PRODUCT_STATUS = "product_status";
	public static final String IS_LIKE = "is_like";
	
	public static final String IMAGES = "images";
	public static final String IMG_ORIGINAL = "original";
	public static final String IMG_540MIDDLE = "540Middle";
	public static final String IMG_SRC = "src";
	public static final String IMG_WIDTH = "width";
	public static final String IMG_HEIGHT = "height";
	
	public static final String MALL_INFO = "mall_info";
	public static final String MALL_ID = "mall_id";
	public static final String MALL_NAME = "mall_name";
	public static final String MALL_ADDRESS = "address";

	public static final String BRAND_INFO = "brand_info";
	public static final String BRAND_NAME = "brand_name";
	public static final String BRAND_ID = "id";

	// ARGS
	public final String ARG_UID;// 参数：用户；
	public final String ARG_PRODUCT_ID;// 参数：产品ID；

	// RESULTS
	public JSONObject resultPInfo;

	public ProductInfo(String uid,  String productId ) {
		ARG_UID = uid;
		ARG_PRODUCT_ID = productId;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "products");
		paramters.put("m", "getProductInfo");
		paramters.put("authcode", AccountCenter.getUser(ARG_UID).getAuth());
		paramters.put("product_id", ARG_PRODUCT_ID);

	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}
}
