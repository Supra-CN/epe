
package tw.supra.network.request;

public abstract class AbstractErrorCodeWithState extends AbstractErrorCode {

    public AbstractErrorCodeWithState(String errorCodeStr, String errorDesc) {
        super(errorCodeStr, errorDesc);
    }

    public AbstractErrorCodeWithState(String errorCodeStr) {
        super(errorCodeStr);
    }

    public AbstractErrorCodeWithState(int errorCode, String errorDesc) {
        super(errorCode, errorDesc);
    }

    public AbstractErrorCodeWithState(int errorCode) {
        super(errorCode);
    }

    public static enum Status {
        OK, FRIENDLY_INFO, FRIENDLY_INFO_BY_LOCAL, ERROE_BY_CALL, ERROE_BY_API, ERROE_BY_LOCAL, ERROR_BY_NETWORK, ERROE_UNKNOW
    }

    private Status mStatus;

    public abstract Status computeStatusByCode();

    public Status getStatus() {
        return mStatus;
    }

    @Override
    public String toString() {
        return String
                .format("errorCode = %d, errorDesc = %s, status = %s \n VollyError = %s \n Dying Message = %s",
                        getCode(), getDescription(), getStatus(), getVolleyError(), getDyingMsg());
    }

    @Override
    public void setCode(int errorCode) {
        super.setCode(errorCode);
        mStatus = computeStatusByCode();
    }

    @Override
    public boolean isOK() {
        switch (mStatus) {
            case OK:
                return true;
            default:
                return false;
        }
    }

    @Override
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

    @Override
    public boolean isFriendlyInfo() {
        switch (getStatus()) {
            case FRIENDLY_INFO:
            case FRIENDLY_INFO_BY_LOCAL:
                return true;
            default:
                return false;
        }
    }

}
