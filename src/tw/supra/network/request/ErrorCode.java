
package tw.supra.network.request;

import com.android.volley.VolleyError;

public interface ErrorCode {

    public void setCode(int errorCode);

    public void setCode(String errorCodeStr);

    public int getCode();

    /**
     * Initializes the cause of this {@code Throwable}. The cause can only be initialized once.
     *
     * @param throwable the cause of this {@code Throwable}.
     * @return this {@code Throwable} instance.
     * @throws IllegalArgumentException if {@code Throwable} is this object.
     */
    void setCauseBy(ErrorCode causeBy);

    /**
     * Returns the cause of this {@code Throwable}, or {@code null} if there is no cause.
     */
    ErrorCode getCauseBy();

    void setVollyError(VolleyError volleyError);

    VolleyError getVolleyError();

    void setThrowableError(Throwable throwableError);

    Throwable getThrowableError();

    boolean isOK();

    boolean isReturnByApiError();

    boolean isFriendlyInfo();

    void setDescription(String desc);

    String getDescription();

    void addDyingMsg(String msg);

    String getDyingMsg();
}
