package tw.supra.epe.store;

import java.util.HashMap;

import org.json.JSONArray;

import tw.supra.network.Request.Method;
import tw.supra.network.request.EpeRequestInfo;
import android.text.TextUtils;

public class ProductsOfStoreInfo extends EpeRequestInfo {
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

	// 排序
	public static final String SORT_BY_DISCOUNT = "by_discount";//
	public static final String SORT_BY_TIME = "by_time";//
	public static final String SORT_BY_HOT = "by_hot";//

	// ARGS
	public final String ARG_SROT;// 参数：折扣排序 1 是 0 否 默认为0；
	public final String ARG_STORE_ID;// 参数：折扣排序 1 是 0 否 默认为0；

	/**
	 * 参数：性别，默认 {@link #GENDER_ALL}
	 */

	public final int ARG_PAGE;// 参数：页码；
	// public final int ARG_COUNT;// 参数：条数

	// RESULTS
	public JSONArray resultJoList;// 返回：商品列表；

	public ProductsOfStoreInfo(String sort, String storeId,int page) {
		ARG_SROT = sort;
		ARG_STORE_ID = storeId;
		ARG_PAGE = page;
		// ARG_COUNT = count;
	}

	@Override
	protected void fillQueryParamters(HashMap<String, String> paramters) {
		paramters.put("d", "api");
		paramters.put("c", "products");
		paramters.put("m", "getProductList");
		if (!TextUtils.isEmpty(ARG_SROT)) {
			paramters.put("action", ARG_SROT);
		}
		paramters.put("mb_id", ARG_STORE_ID);
		paramters.put("page", String.valueOf(ARG_PAGE));
		// paramters.put("count", String.valueOf(ARG_COUNT));
	}

	@Override
	public int getRequestMethod() {
		return Method.GET;
	}
}
