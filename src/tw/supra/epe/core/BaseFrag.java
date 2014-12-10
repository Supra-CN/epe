
package tw.supra.epe.core;

import android.app.Fragment;
import android.content.Context;
import android.text.TextUtils;

import tw.supra.epe.App;

import java.util.UUID;

public abstract class BaseFrag extends Fragment {
    public final UUID FRAG_ID = UUID.randomUUID();
    public final String FRAG_TAG = FRAG_ID.toString();
    private CharSequence mTitle = "";

    private boolean mIsInited = false;

    @Override
    public void onStart() {
        super.onStart();
        mIsInited = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public boolean IsInited() {
        return mIsInited;
    }
    
    abstract protected CharSequence getDefaultTitle(Context c);
    public abstract int getIconResId() ;

    protected void setTitle(int title) {
        setTitle(getText(title));
    }

    protected void setTitle(CharSequence title) {
        mTitle = title;
    }

    public CharSequence getTitle() {
        if(TextUtils.isEmpty(mTitle)){
            mTitle = getDefaultTitle(App.getInstance());
        }
        return mTitle;
    }

    
}
