package tw.supra.epe.store;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.ApiDef.APIDef;
import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.epe.account.AccountCenter;
import tw.supra.network.request.EpeJsonMultiRequest;
import tw.supra.network.request.EpeRequestInfo;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.utils.JsonUtils;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.picasso.Picasso;

public class RequestPushProduct extends EpeJsonMultiRequest<EpeRequestInfo> {
	private static final String LOG_TAG = RequestPushProduct.class
			.getSimpleName();
	private static final Charset CHARSET = Charset.defaultCharset();
	private static final String MIME_TEXT = "text/plain";
	private static final int BYTE_COUNT_LIMIT = 1024 * 1204;
	private static final int IMG_SIZE_LIMIT = 1024;

	public final Uri IMG;
	public final String NAME;
	public final String TAG;
	public final String GENDER;
	public final String PRICE;
	public final String BRAND_ID;
	public final String BRAND_NAME;
	public final String MALL_ID;
	public final String SKU;
	public final String TYPE;
	public final String DISCOUNT;

	public RequestPushProduct(NetWorkHandler<EpeRequestInfo> eventHandler,
			Uri img, String mallId, String brandId, String brandName,
			String name, String sku, String tag, String gender, String price,
			String type, String discount) {
		super(eventHandler, new EpeRequestInfo() {

			@Override
			public int getRequestMethod() {
				return Method.POST;
			}

			@Override
			protected void fillQueryParamters(HashMap<String, String> paramters) {
				paramters.put("d", "api");
				paramters.put("c", "products");
				paramters.put("m", "addProducts");
			}
		});

		IMG = img;
		NAME = name;
		MALL_ID = mallId;

		TAG = tag;
		GENDER = gender;
		PRICE = price;
		BRAND_ID = brandId;
		BRAND_NAME = brandName;
		SKU = sku;
		TYPE = type;
		DISCOUNT = discount;

	}

	@Override
	protected void parseJsonResponse(JSONObject response) throws JSONException {
		INFO.ERROR_CODE.setCode(JsonUtils.getIntSafely(response,
				APIDef.KEY_ERROR_CODE, EpeErrorCode.CODE_UNKNOW));
		INFO.ERROR_CODE.setDescription(JsonUtils.getStrSafely(response,
				APIDef.KEY_ERROR_DESC));
		if (!INFO.ERROR_CODE.isOK()) {
			INFO.ERROR_CODE.addDyingMsg("response : " + response);
			return;
		}
		INFO.ERROR_CODE.addDyingMsg("response : " + response);
	}

	@Override
	protected void onPostParseJson() {
	}

	@Override
	public MultipartEntity getMultipartEntity() {

		MultipartEntity entity = new MultipartEntity();
		entity.addPart("authcode", StringBody.create(AccountCenter
				.getCurrentUser().getAuth(), MIME_TEXT, CHARSET));
		entity.addPart("product_name",
				StringBody.create(NAME, MIME_TEXT, CHARSET));
		entity.addPart("product_gender",
				StringBody.create(GENDER, MIME_TEXT, CHARSET));

		entity.addPart("product_price",
				StringBody.create(PRICE, MIME_TEXT, CHARSET));
		entity.addPart("product_brand_id",
				StringBody.create(BRAND_ID, MIME_TEXT, CHARSET));
		entity.addPart("product_sku",
				StringBody.create(SKU, MIME_TEXT, CHARSET));
		entity.addPart("discount_num",
				StringBody.create(DISCOUNT, MIME_TEXT, CHARSET));
		entity.addPart("product_tag",
				StringBody.create(TAG, MIME_TEXT, CHARSET));

		entity.addPart("product_shop_id", StringBody.create(AccountCenter
				.getCurrentUser().getShopId(), MIME_TEXT, CHARSET));
		if (!TextUtils.isEmpty(TYPE)) {
			entity.addPart(TYPE, StringBody.create("1", MIME_TEXT, CHARSET));
		}

		if (null != IMG) {
			Bitmap bm = null;
			try {
				// bm = Picasso.with(getContext()).load(IMG).get();
				bm = Picasso.with(getContext()).load(IMG)
						.resize(IMG_SIZE_LIMIT, 0).onlyScaleDown().get();
				// RequestCreator creator =
				// Picasso.with(getContext()).load(IMG);

			} catch (IOException e) {
				e.printStackTrace();
			}
			// Bitmap bm = BitmapFactory.decodeFile(img);
			// int width = bm.getWidth();
			// int height = bm.getHeight();
			//
			// if (width > IMG_SIZE_LIMIT) {
			// Bitmap temp = ThumbnailUtils.extractThumbnail(bm, IMG_SIZE_LIMIT,
			// (int) (IMG_SIZE_LIMIT * (float) height / (float) width));
			// bm.recycle();
			// bm = temp;
			// }

			Log.i(LOG_TAG, "bm = " + bm);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bm.compress(CompressFormat.JPEG, 75, out);
			Log.i(LOG_TAG, "out = " + out.size() / 1024);
			ByteArrayBody body = new ByteArrayBody(out.toByteArray(),
					"image/jpeg", IMG.getLastPathSegment());
			entity.addPart("images", body);
			bm.recycle();
		}
		return entity;
	}
}
