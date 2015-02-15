package tw.supra.epe.pages.epe;

import tw.supra.epe.core.BaseFrag;
import tw.supra.network.NetworkCenter;
import tw.supra.network.ui.NetworkImageView;
import tw.supra.web.TintWebViewActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

public class AdFrag extends BaseFrag implements OnClickListener {
	public static final String ARG_IMG = "arg_img";
	public static final String ARG_URL = "arg_url";

	private String mImg;
	private String mUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImg = getArguments().getString(ARG_IMG);
		mUrl = getArguments().getString(ARG_URL);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		NetworkImageView iv = new NetworkImageView(getActivity());
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setImageUrl(mImg, NetworkCenter.getInstance().getImageLoader());
		iv.setOnClickListener(this);
		return iv;
	}

	@Override
	protected CharSequence getDefaultTitle(Context c) {
		return "";
	}

	@Override
	public int getIconResId() {
		return 0;
	}

	@Override
	public void onClick(View v) {
		if (!TextUtils.isEmpty(mUrl)) {
			Intent intent = new Intent(getActivity(), TintWebViewActivity.class);
			intent.putExtra(TintWebViewActivity.EXTRA_URL, mUrl);
			startActivity(intent);
		}
	}

}
