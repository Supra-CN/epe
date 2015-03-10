package tw.supra.epe.activity;

import tw.supra.epe.core.BaseActivity;
import tw.supra.epe.utils.AppUtiles;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.yijiayi.yijiayi.R;

public class AboutActivity extends BaseActivity implements OnClickListener {

	private static final String LOG_TAG = AboutActivity.class.getSimpleName();

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_epe_about);
		findViewById(R.id.action_back).setOnClickListener(this);
		((TextView) findViewById(R.id.ver)).setText(AppUtiles.getVersionName());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_back:
			onBackPressed();
			break;
		default:
			break;
		}
	}

}
