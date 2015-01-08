package tw.supra.epe.pages;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseMainPage;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MsgPage extends BaseMainPage {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v =  inflater.inflate(R.layout.page_msg, null);
		return v;
	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return c.getText(R.string.indictor_tab_msg);
	}

	@Override
	public int getIconResId() {
		return R.drawable.indicator_icon_msg;
	}

}
