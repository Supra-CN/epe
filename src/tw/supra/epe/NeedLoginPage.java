package tw.supra.epe;

import tw.supra.epe.account.AccountCenter;
import tw.supra.epe.core.BaseMainPage;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yijiayi.yijiayi.R;

public abstract class NeedLoginPage extends BaseMainPage implements
		OnClickListener {
	private static final String LOG_TAG = NeedLoginPage.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView v = new TextView(getActivity());
		v.setGravity(Gravity.CENTER);
		v.setText(R.string.need_login);
		v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		v.setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {
		AccountCenter.doLogin(getActivity());
	}

}
