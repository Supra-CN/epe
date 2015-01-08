
package tw.supra.network.request;

import tw.supra.network.error.VolleyError;

public abstract class AbstractErrorCode implements ErrorCode {

    private int mCode;
    private String mDescription;
    private ErrorCode mCauseBy;
    private VolleyError mVolleyError;
    private Throwable mThrowableError;
    private String mDyingMsg = "";

    public AbstractErrorCode(int errorCode) {
        this(errorCode, "");
    }

    public AbstractErrorCode(String errorCodeStr) {
        this(errorCodeStr, "");
    }

    public AbstractErrorCode(int errorCode, String errorDesc) {
        setCode(errorCode);
        this.mDescription = errorDesc;
    }

    public AbstractErrorCode(String errorCodeStr, String errorDesc) {
        setCode(errorCodeStr);
        this.mDescription = errorDesc;
    }

    private int parseErrorCodeStr(String errorCodeStr) {
        int errorCode = -1;
        try {
            errorCode = Integer.parseInt(errorCodeStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return errorCode;
    }

    @Override
    public String toString() {
        return String.format(
                "errorCode = %d, errorDesc = %s, \n VollyError = %s \n Dying Message = %s",
                getCode(), mDescription, getVolleyError(), mDyingMsg);
    }

    @Override
    public void setCode(int errorCode) {
        mCode = errorCode;
    }

    @Override
    public void setCode(String errorCodeStr) {
        setCode(parseErrorCodeStr(errorCodeStr));
    }

    @Override
    public int getCode() {
        return mCode;
    }

    @Override
    public void setCauseBy(ErrorCode causeBy) {
        if (causeBy == this) {
            throw new IllegalArgumentException("throwable == this");
        }
        mCauseBy = causeBy;
    }

    @Override
    public ErrorCode getCauseBy() {
        if (mCauseBy == this) {
            return null;
        }
        return mCauseBy;
    }

    @Override
    public void setVollyError(VolleyError volleyError) {
        mVolleyError = volleyError;
    }

    @Override
    public VolleyError getVolleyError() {
        return mVolleyError;
    }

    @Override
    public void setThrowableError(Throwable throwableError) {
        mThrowableError = throwableError;
    }

    @Override
    public Throwable getThrowableError() {
        return mThrowableError;
    }

    @Override
    public void setDescription(String desc) {
        mDescription = desc;
    }

    @Override
    public String getDescription() {
        return mDescription;
    }

    @Override
    public void addDyingMsg(String msg) {
        mDyingMsg += msg;
        mDyingMsg+="\n";
    }

    @Override
    public String getDyingMsg() {
        return mDyingMsg;
    }

}
