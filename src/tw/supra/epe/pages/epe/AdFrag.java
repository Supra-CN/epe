package tw.supra.epe.pages.epe;

import tw.supra.epe.core.BaseFrag;
import tw.supra.network.NetworkCenter;
import tw.supra.network.ui.NetworkImageView;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

public class AdFrag extends BaseFrag {
	public static final String ARG_IMG = "arg_img";

	private String mImg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImg = getArguments().getString(ARG_IMG);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		NetworkImageView iv = new NetworkImageView(getActivity());
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setImageUrl(mImg, NetworkCenter.getInstance().getImageLoader());
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

}
