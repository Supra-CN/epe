package tw.supra.network.request;

import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import tw.supra.epe.ApiDef.EpeErrorCode;

 abstract public class EpeRequest<T extends EpeRequestInfo> extends InfoRequest<EpeErrorCode,T> {
     private static final String LOG_TAG = EpeRequest.class.getSimpleName();

    public EpeRequest(NetWorkHandler<T> eventHandler, T info) {
        super(eventHandler, info);
    }

    @Override
    protected void dispatchVolleyError(VolleyError error) {
		if (!INFO.ERROR_CODE.isReturnByApiError()) {
			if (error instanceof NoConnectionError) {
				INFO.ERROR_CODE
						.setCode(EpeErrorCode.CODE_ERROR_BY_VOLLY_NO_CONNECTION);
			} else if (error instanceof NetworkError) {
				INFO.ERROR_CODE.setCode(EpeErrorCode.CODE_ERROR_BY_VOLLY_NETWORK);
			} else if (error instanceof TimeoutError) {
				INFO.ERROR_CODE.setCode(EpeErrorCode.CODE_ERROR_BY_VOLLY_TIMEOUT);
				INFO.ERROR_CODE.setCode(EpeErrorCode.CODE_ERROR_BY_VOLLY_SERVER);
			} else if (error instanceof ServerError) {
			} else {
				INFO.ERROR_CODE.setCode(EpeErrorCode.CODE_ERROR_BY_VOLLY_UNKNOW);
			}
		}
		INFO.ERROR_CODE.setVollyError(error);

		notifyFinish(true);
		super.deliverError(error);
    }
    

    @Override
    protected void judgeError() {
		String toast = null;

		switch (INFO.ERROR_CODE.getCode()) {
//		case ErrorCode.CODE_INFO_PP_TOKEN_EXPIRED:
//		case ErrorCode.CODE_INFO_TOKEN_EXPIRED:
//			toast = getContext()
//					.getString(R.string.network_toast_token_expired);
//			ClubAccountManager.getInstance().doLogOut();
//			break;
//		case ErrorCode.CODE_ERROR_BY_VOLLY_NETWORK:
//			toast = getContext()
//					.getString(R.string.network_toast_network_error);
//			break;
//		case ErrorCode.CODE_ERROR_BY_VOLLY_NO_CONNECTION:
//			toast = getContext()
//					.getString(R.string.network_toast_no_connection);
//			break;
//		case ErrorCode.CODE_ERROR_BY_VOLLY_TIMEOUT:
//			toast = getContext().getString(R.string.network_toast_timeout);
//			break;
//		case ErrorCode.CODE_ERROR_BY_VOLLY_SERVER:
//			toast = getContext().getString(R.string.network_toast_timeout);
//			toast = getContext().getString(R.string.network_toast_server_error);
//			break;
		default: {
			if (INFO.ERROR_CODE.isFriendlyInfo()) {
				toast = INFO.ERROR_CODE.getDescription();
			}
			break;
		}
		}
		if (!TextUtils.isEmpty(toast)) {
			Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
		}
	}

}
