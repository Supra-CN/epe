
package tw.supra.epe.core;

import java.util.UUID;

import tw.supra.epe.App;
import tw.supra.network.RequestQueue;
import android.app.Fragment;
import android.content.Context;
import android.text.TextUtils;

public abstract class BaseFrag extends Fragment {
    public final UUID FRAG_ID = UUID.randomUUID();
    public final String FRAG_TAG = FRAG_ID.toString();
    private CharSequence mTitle = "";
    private RequestQueue mRequestQueue;

    private boolean mIsInited = false;

    @Override
    public void onStart() {
        super.onStart();
        mIsInited = true;
    }

    @Override
    public void onDestroy() {
        if (null != mRequestQueue) {
            mRequestQueue.stop();
            mRequestQueue = null;
        }
        super.onDestroy();
    }

    public boolean IsInited() {
        return mIsInited;
    }

    abstract protected CharSequence getDefaultTitle(Context c);

    public abstract int getIconResId();

    protected void setTitle(int title) {
        setTitle(getText(title));
    }

    protected void setTitle(CharSequence title) {
        mTitle = title;
    }

    public CharSequence getTitle() {
        if (TextUtils.isEmpty(mTitle)) {
            mTitle = getDefaultTitle(App.getInstance());
        }
        return mTitle;
    }

}
