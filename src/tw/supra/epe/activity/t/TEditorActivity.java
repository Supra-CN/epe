package tw.supra.epe.activity.t;

import tw.supra.epe.App;
import tw.supra.epe.R;
import tw.supra.epe.core.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class TEditorActivity extends BaseActivity implements OnClickListener {

	private static final String LOG_TAG = TEditorActivity.class.getSimpleName();

	/**
	 * 设置布局
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_t_editor);
		findViewById(R.id.action_back).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_back:
			onBackPressed();
			break;
		case R.id.logout:
			startActivity(new Intent(App.ACTION_LOGIN));
			finish();
			break;
		default:
			break;
		}
	}

}
