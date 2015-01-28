package tw.supra.epe.msg;

import org.json.JSONException;
import org.json.JSONObject;

import tw.supra.epe.ApiDef.APIDef;
import tw.supra.epe.ApiDef.EpeErrorCode;
import tw.supra.network.request.EpeJsonRequest;
import tw.supra.network.request.NetWorkHandler;
import tw.supra.utils.JsonUtils;

public class RequestMsgTopic extends EpeJsonRequest<MsgTopicInfo> {
	private static final String LOG_TAG = RequestMsgTopic.class.getSimpleName();


	public RequestMsgTopic(NetWorkHandler<MsgTopicInfo> eventHandler, MsgTopicInfo info) {
		super(eventHandler, info);
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

		INFO.resultJoList = JsonUtils.getJaSafely(response, "data");
		INFO.ERROR_CODE.addDyingMsg("response : " + response);
	}

	@Override
	protected void onPostParseJson() {
	}
}
