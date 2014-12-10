
package tw.supra.epe.account;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseFrag;

public class RegFrag extends BaseFrag {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reg, container, false);
        return v;
    }

    @Override
    protected CharSequence getDefaultTitle(Context c) {
        return c.getText(R.string.account_reg_frag_title);
    }

    @Override
    public int getIconResId() {
        return 0;
    }

}
