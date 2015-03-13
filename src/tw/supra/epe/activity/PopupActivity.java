package tw.supra.epe.activity;

import tw.supra.epe.activity.fav.FavActivity;
import tw.supra.epe.activity.t.TEditorActivity;
import tw.supra.epe.core.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.yijiayi.yijiayi.R;

public class PopupActivity extends BaseActivity implements OnClickListener {
	private static final String LOG_TAG = PopupActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_create);
		findViewById(R.id.popup_capture).setOnClickListener(this);
		findViewById(R.id.popup_pick).setOnClickListener(this);
		findViewById(R.id.popup_fav).setOnClickListener(this);
		findViewById(R.id.popup_focus).setOnClickListener(this);
		findViewById(R.id.container).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.popup_capture: {
			Intent intent = new Intent(this, TEditorActivity.class);
			intent.putExtra(TEditorActivity.EXTRA_ACTION,
					TEditorActivity.ACTION_CAPTURE);
			startActivity(intent);
		}

			break;
		case R.id.popup_pick: {
			Intent intent = new Intent(this, TEditorActivity.class);
			intent.putExtra(TEditorActivity.EXTRA_ACTION,
					TEditorActivity.ACTION_PICK);
			startActivity(intent);
		}

			break;
		case R.id.popup_fav: {
			Intent intent = new Intent(this, FavActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.popup_focus: {
			Intent intent = new Intent(this, FocusActivity.class);
			startActivity(intent);
		}
			break;

		default:
			break;
		}

		finish();
	}
	
}
