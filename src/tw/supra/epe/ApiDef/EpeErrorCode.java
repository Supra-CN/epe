
package tw.supra.epe.ApiDef;

import tw.supra.network.request.AbstractErrorCodeWithState;

public class EpeErrorCode extends AbstractErrorCodeWithState {

    // ===============================
    // start CODE for API defined
    // FRIENDLY_INFO
    // public static final int CODE_INFO_THE_VERSION_IS_UP_TO_DATE = 7500;
    // public static final int CODE_INFO_THREAD_IS_DELETED = 7503;
    // public static final int CODE_INFO_TOKEN_EXPIRED = 7602;
    // public static final int CODE_INFO_PP_TOKEN_EXPIRED = 7603;
    // end CODE for API defined
    // ===============================

    // ===============================
    // start CODE for epe Api defined
    // FRIENDLY_INFO
    public static final int CODE_ERROR_EPE = 1;
    public static final int CODE_OK = 0;
    // end CODE for API defined
    // ===============================

    // ===============================
    // start CODE for NETWORK defined
    // ERROR cause by NETWORK ;
     public static final int CODE_ERROR_BY_VOLLY_UNKNOW = 10000;
     public static final int CODE_ERROR_BY_VOLLY_NO_CONNECTION = 10002;
     public static final int CODE_ERROR_BY_VOLLY_NETWORK = 10003;
     public static final int CODE_ERROR_BY_VOLLY_TIMEOUT = 10004;
     public static final int CODE_ERROR_BY_VOLLY_SERVER = -10005;
    // end CODE for NETWORK defined
    // ===============================

    // ===============================
    // start CODE for LOCAL defined
    // ERROR cause by UNKNOW； code == -1
    public static final int CODE_UNKNOW = -1;

    // ERROR (code >= -4999 && code < -2)
     public static final int CODE_ERROR_BY_JSON_DECODE = -2;
     public static final int CODE_ERROR_BY_URL_DECODE = -3;
     public static final int CODE_ERROR_BY_DB_QUERY = -4;
     public static final int CODE_BAD_RESPONSE = -5;
     public static final int CODE_ERROR_BY_LOCAL_IO = -6;
     public static final int CODE_ERROR_CAN_NOT_FIND = -7;

    // FRIENDLY_INFO_BY_LOCAL (code >= -5999 && code <= -5000)
    // public static final int CODE_LOCAL_INFO_EMPTY_DATA_SET = -5000;
    // public static final int CODE_LOCAL_INFO_THREAD_UNDER_REVIEW = -5001;
    // public static final int CODE_LOCAL_INFO_DUPLICATE = -5002;
    // end CODE for LOCAL defined
    // ===============================

    public EpeErrorCode() {
        this(CODE_UNKNOW);
    }

    public EpeErrorCode(int errorCode) {
        this(errorCode, "");
    }

    public EpeErrorCode(String errorCodeStr) {
        this(errorCodeStr, "");
    }

    public EpeErrorCode(int errorCode, String errorDesc) {
        super(errorCode, errorDesc);
    }

    public EpeErrorCode(String errorCodeStr, String errorDesc) {
        super(errorCodeStr, errorDesc);
    }

    public static Status computeStatusByCode(int code) {
        if (code >= -5999 && code <= -5000) {
            return Status.FRIENDLY_INFO_BY_LOCAL;
        }
        if (code >= -4999 && code < -2) {
            return Status.ERROE_BY_LOCAL;
        }
        if (code == -1) {
            return Status.ERROE_UNKNOW;
        }
        if (code == 0) {
            return Status.OK;
        }
        if (code == 1) {
            return Status.ERROE_BY_CALL;
        }
        // if (code >= 6000 && code <= 6999) {
        // return Status.ERROE_BY_CALL;
        // }
        // if (code >= 7000 && code <= 7499) {
        // return Status.ERROE_BY_API;
        // }
         if (code >= 1000 && code <= 1999) {
         return Status.FRIENDLY_INFO;
         }
        if (code >= 10000) {
            return Status.ERROR_BY_NETWORK;
        }
        return Status.ERROE_UNKNOW;
    }

    public boolean isReturnByApiError() {
        switch (getStatus()) {
            case ERROE_BY_API:
            case ERROE_BY_CALL:
            case FRIENDLY_INFO:
                return true;
            default:
                return false;
        }
    }

    public boolean isFriendlyInfo() {
        switch (getStatus()) {
            case FRIENDLY_INFO:
            case FRIENDLY_INFO_BY_LOCAL:
                return true;
            default:
                return false;
        }
    }

    @Override
    public Status computeStatusByCode() {
        return computeStatusByCode(getCode());
    }

}
