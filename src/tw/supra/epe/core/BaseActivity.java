
package tw.supra.epe.core;

import tw.supra.epe.R;
import android.app.Activity;
import android.app.ProgressDialog;

import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends Activity {
    private ProgressDialog mProgressDialog;
    
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        }
        protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        }
    
    public void showProgressDialog() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog = ProgressDialog.show(this, getString(R.string.progressing_title),
                getString(R.string.progressing), false, false);
    }

    public void hideProgressDialog() {
        if (null != mProgressDialog) {
            mProgressDialog.cancel();
        }
    }

}
