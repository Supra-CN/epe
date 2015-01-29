package tw.supra.epe.account;

import tw.supra.epe.R;
import tw.supra.epe.core.BaseActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class UserInfoEditorActivity extends BaseActivity implements
		OnClickListener {

	private static final String LOG_TAG = UserInfoEditorActivity.class
			.getSimpleName();

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info_editor);
		findViewById(R.id.action_back).setOnClickListener(this);
		findViewById(R.id.action_submit).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_back:
			onBackPressed();
			break;
		case R.id.action_submit:
			break;
		default:
			break;
		}
	}
}
