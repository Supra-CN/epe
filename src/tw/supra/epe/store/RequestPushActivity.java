package tw.supra.epe.store;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;
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
import tw.supra.utils.TimeUtil;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.util.Log;

import com.squareup.picasso.Picasso;

public class RequestPushActivity extends EpeJsonMultiRequest<EpeRequestInfo> {
	private static final String LOG_TAG = RequestPushActivity.class
			.getSimpleName();
	private static final Charset CHARSET = Charset.defaultCharset();
	private static final String MIME_TEXT = "text/plain";
	private static final int BYTE_COUNT_LIMIT = 1024 * 1204;
	private static final int IMG_SIZE_LIMIT = 1024;

	public final Uri IMG;
	public final String TITLE;
	public final String MALL_ID;
	public final String CONTENT;
	public final String START_TIME;
	public final String END_TIME;

	public RequestPushActivity(NetWorkHandler<EpeRequestInfo> eventHandler,
			Uri img,String mallId,String title,  String content,Calendar startTime,Calendar endTime) {
		super(eventHandler, new EpeRequestInfo() {

			@Override
			public int getRequestMethod() {
				return Method.POST;
			}

			@Override
			protected void fillQueryParamters(HashMap<String, String> paramters) {
				paramters.put("d", "api");
				paramters.put("c", "mall");
				paramters.put("m", "publish_activity");
			}
		});

		IMG = img;
		TITLE = title;
		CONTENT = content;
		MALL_ID = mallId;
		START_TIME = TimeUtil.formatDateWithMM(getContext(), startTime.getTime());
		END_TIME = TimeUtil.formatDateWithMM(getContext(), endTime.getTime());
		Log.i(LOG_TAG, START_TIME);
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
//		entity.addPart("type", StringBody.create("0".equals(MALL_ID)?"2":"1", MIME_TEXT, CHARSET));
		entity.addPart("type", StringBody.create("2", MIME_TEXT, CHARSET));
		
		entity.addPart("mall_id", StringBody.create(MALL_ID, MIME_TEXT, CHARSET));
		
		entity.addPart("shop_id", StringBody.create(AccountCenter
				.getCurrentUser().getShopId(), MIME_TEXT, CHARSET));
		
		entity.addPart("title",
				StringBody.create(TITLE, MIME_TEXT, CHARSET));
		entity.addPart("activity_info",
				StringBody.create(CONTENT, MIME_TEXT, CHARSET));
		entity.addPart("start_time",
				StringBody.create(START_TIME, MIME_TEXT, CHARSET));
		entity.addPart("end_time",
				StringBody.create(END_TIME, MIME_TEXT, CHARSET));
		// String img = decodeImg(Uri.parse(INFO.SRC));

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
			entity.addPart("pic", body);
			bm.recycle();
		}
		return entity;
	}
}
