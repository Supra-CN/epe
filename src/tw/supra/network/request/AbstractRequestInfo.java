package tw.supra.network.request;

import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Request;

public abstract class AbstractRequestInfo<E extends ErrorCode> {
	private String mRequestUrl;
	public Bundle ARGS = new Bundle();
	public Bundle RESULTS = new Bundle();

	public interface Method extends Request.Method {
	};

	public final E ERROR_CODE;
	public final float PROGRESS_INIT;
	public final float PROGRESS_HALF;
	public final float PROGRESS_MAX;
	private float mProgress;

	abstract protected String buildRequestUrl();
	abstract protected E initErrorCode();

	public AbstractRequestInfo() {
		this(0, 1);
	}

	public AbstractRequestInfo(float initProgress, float maxProgress) {
	    ERROR_CODE = initErrorCode();
		PROGRESS_INIT = initProgress;
		PROGRESS_MAX = maxProgress;
		PROGRESS_HALF = (PROGRESS_INIT + PROGRESS_MAX) / 2;
		mProgress = PROGRESS_INIT;
	}

	public String getRequestUrl() {
		if (TextUtils.isEmpty(mRequestUrl)) {
			mRequestUrl = buildRequestUrl();
		}
		return mRequestUrl;
	}

	/**
	 * @see Request.Method
	 * @return
	 */
	public abstract int getRequestMethod();

	public void setProgress(float progress) {
		if (progress < PROGRESS_INIT) {
			mProgress = PROGRESS_INIT;
		} else if (progress > PROGRESS_MAX) {
			mProgress = PROGRESS_MAX;
		} else {
			mProgress = progress;
		}
	}

	public float getProgress() {
		return mProgress;
	}

	@Override
	public String toString() {
		return String.format("%s:  Method = %d \n URL = %s \n ERROR_CODE : %s", getClass().getSimpleName(),
				getRequestMethod(), getRequestUrl(), ERROR_CODE);
	}
}
