package tw.supra.epe.pages;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseMainPage;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyPage extends BaseMainPage {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		TextView v = (TextView) inflater.inflate(R.layout.page_my, null);
		v.setText(this.getClass().getSimpleName());
		return v;
	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return c.getText(R.string.indictor_tab_my);
	}

	@Override
	public int getIconResId() {
		return R.drawable.indicator_icon_my;
	}
}
