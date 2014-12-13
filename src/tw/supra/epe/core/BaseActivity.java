
package tw.supra.epe.core;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

import tw.supra.epe.R;

public abstract class BaseActivity extends Activity {
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        }
        public void onPause() {
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
